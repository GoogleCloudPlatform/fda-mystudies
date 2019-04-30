package org.sagebase.crf.step.active

import org.sagebase.crf.linearAlgebra.*
import kotlin.math.roundToInt

//  Copyright © 2018 Sage Bionetworks. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
// 1.  Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// 2.  Redistributions in binary form must reproduce the above copyright notice,
// this list of conditions and the following disclaimer in the documentation and/or
// other materials provided with the distribution.
//
// 3.  Neither the name of the copyright holder(s) nor the names of any contributors
// may be used to endorse or promote products derived from this software without
// specific prior written permission. No license is granted to the trademarks of
// the copyright holders even if such marks are included in this software.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//


/// Frame rates supported by this processor, with the preferred framerate listed first.
val SUPPORTED_FRAME_RATES = arrayOf(30)

/// The number of seconds for the window used to calculate the heart rate.
const val HEART_RATE_WINDOW_IN_SECONDS: Double = 10.0
const val HEART_RATE_MIN_FRAME_RATE: Double = 12.0
const val HEART_RATE_FILTER_DROP_IN_SECONDS: Double = 2.0

const val HEART_RATE_MAX: Double = 210.0
const val HEART_RATE_MIN: Double = 45.0

const val HEART_RATE_MIN_RESTING_CONFIDENCE = 0.5
const val HEART_RATE_MIN_VO2MAX_CONFIDENCE = 0.5

data class HeartRateBPM(val timestamp: Double, val bpm: Double, val confidence: Double, val channel: String)

interface PixelSample {
    val timestamp: Double
    val uptime: Double
    val red: Double
    val green: Double
    val blue: Double
    fun isCoveringLens(): Boolean
}

enum class Sex {
    male, female, other
}

class HeartRateSampleProcessor @JvmOverloads constructor(val videoProcessorFrameRate: Int = SUPPORTED_FRAME_RATES.first()) {

    var bpmRecords = mutableListOf<HeartRateBPM>()
    var pixelSamples = mutableListOf<PixelSample>()

    fun addSample(sample: PixelSample) {
        // Add the pixel sample
        this.pixelSamples.add(sample)
    }

    fun isReadyToProcess(): Boolean {
        val estimated = estimatedSamplingRate(pixelSamples) ?: return false
        val roundedRate = estimated.roundToInt()
        if (!isValidSamplingRate(roundedRate)) return false

        // look to see if we have enough to process a bpm
        // Need to keep 2 extra seconds due to filtering lopping off the first 2 seconds of data.
        val meanOrder = meanFilterOrder(roundedRate)
        val windowLength = (HEART_RATE_WINDOW_IN_SECONDS + 2.0 * HEART_RATE_FILTER_DROP_IN_SECONDS).roundToInt() * roundedRate + meanOrder

        return (pixelSamples.size >= windowLength)
    }

    fun processSamples(): HeartRateBPM {
        val samplingRate = calculateSamplingRate(pixelSamples)
        val roundedRate = samplingRate.roundToInt()
        val timestamp = this.pixelSamples.last().timestamp
        val samples = this.pixelSamples
        val redChannel = samples.map { it.red }
        val greenChannel = samples.map { it.green }
        this.pixelSamples = this.pixelSamples.drop(roundedRate).toMutableList()
        val redCalc = calculateHeartRate(redChannel, samplingRate)
        val greenCalc = calculateHeartRate(greenChannel, samplingRate)

        val bpm=
                if (redCalc.confidence > greenCalc.confidence)
                    HeartRateBPM(timestamp, redCalc.heartRate, redCalc.confidence, "red")
                else
                    HeartRateBPM(timestamp, greenCalc.heartRate, greenCalc.confidence, "green")

        bpmRecords.add(bpm)
        return bpm
    }

    internal data class CalculatedHeartRate(val heartRate: Double, val confidence: Double)

    /**
     * For a given window return the calculated heart rate and confidence.
     */
    private fun calculateHeartRate(input: List<Double>, samplingRate: Double): CalculatedHeartRate {
        val filtered = getFilteredSignal(input, samplingRate.roundToInt())
        return calculateHRFromFilteredSamples(filtered, samplingRate)
    }

    fun reset() {
        this.pixelSamples.removeAll { true }
        this.bpmRecords.removeAll { true }
    }

    fun restingHeartRate() : HeartRateBPM? {
        val sample = this.bpmRecords.maxWith(compareBy { it.confidence } ) ?: return null
        return if (sample.confidence < HEART_RATE_MIN_RESTING_CONFIDENCE) null else sample
    }

    fun vo2Max(sex: Sex, age: Double, startTime: Double) : Double {
        val highConfidenceSamples = this.bpmRecords.filter {
            it.confidence >= HEART_RATE_MIN_VO2MAX_CONFIDENCE && it.timestamp >= startTime
        }
        if (highConfidenceSamples.size <= 1) {
            return -1.0;
        }
        val meanHR = highConfidenceSamples.map { it.bpm }.average()
        val beats30to60 = meanHR / 2
        val vo2Center: Double = when (sex) {
            Sex.female -> 83.477 - (0.586 * beats30to60) - (0.404 * age) - 7.030
            Sex.male -> 83.477 - (0.586 * beats30to60) - (0.404 * age)
            else -> 84.687 - (0.722 * beats30to60) - (0.383 * age)
        }
        return vo2Center
    }

    // --- Code ported from Swift implementation (CRF - SageResearch), originally validated in R. syoung 04/17/2019

    /// Estimated sampling rate will return nil if there are not enough samples to return a rate.
    fun estimatedSamplingRate(samples: List<PixelSample>) : Double? {
        // Look to see if the sampling rate can be estimated.
        if (samples.size < HEART_RATE_WINDOW_IN_SECONDS * HEART_RATE_MIN_FRAME_RATE) {
            return null
        }
        return calculateSamplingRate(samples)
    }

    //    #' Given a processed time series find its period using autocorrelation
    //    #' and then convert it to heart rate (bpm)
    internal fun calculateHRFromFilteredSamples(filteredInput: DoubleArray, samplingRate: Double) : CalculatedHeartRate {
        val (x, y, minLag, maxLag ) = preprocessSamples(filteredInput, samplingRate)
                ?: return CalculatedHeartRate(0.0, 0.0)
        val (y_max, y_max_pos, y_min, hr_initial_guess) = getBounds(y, samplingRate)
        val aliasedPeak= getAliasingPeakLocation(hr_initial_guess, y_max_pos, minLag, maxLag)
                ?: return CalculatedHeartRate(0.0, 0.0)

        if (aliasedPeak.earlier.isNotEmpty()) {
            // peak_pos_thresholding_result <- (y[aliasedPeak$earlier_peak]-y_min) > 0.7*(y_max-y_min)
            // # Check which of the earlier peak(s) satisfy the threshold
            val peak_pos = aliasedPeak.earlier.filter { (y[it] - y_min) > 0.7 * (y_max - y_min) }
            // # Subset to those earlier peak(s) that satisfy the thresholding criterion above
            if (peak_pos.isNotEmpty()) {
                // # Estimate heartrates for each of the peaks that satisfy the thresholding criterion
                val hr_vec = peak_pos.map { (60 * samplingRate) / (it - 1).toDouble() }
                val hr = hr_vec.average()
                // confidence <- mean(y[peak_pos]-y_min)/(max(x)-min(x))
                // # Estimate the confidence based on the peaks that satisfy the threshold
                val confidence = peak_pos.map({ y[it] - y_min }).average() / (x.max()!! - x.min()!!)
                return CalculatedHeartRate(hr, confidence)
            } else {
                return CalculatedHeartRate(hr_initial_guess, y_max / x.max()!!)
            }
        } else if ((aliasedPeak.later.isNotEmpty()) &&
                    (aliasedPeak.later.fold(true) { ret, value -> ret && (y[value] > 0.7 * y_max) })) {
            // # Get into this loop if no earlier peak was picked up during getAliasingPeakLocation
            return CalculatedHeartRate(hr_initial_guess, y_max / x.max()!!)
        } else {
            // Could not calculate a heart rate
            return CalculatedHeartRate(0.0, 0.0)
        }
    }

    internal data class AliasingPeakLocation(val nPeaks: Int, val earlier: List<Int>, val later: List<Int>)

    internal fun getAliasingPeakLocation(hr: Double, actualLag: Int, minLag: Int, maxLag: Int) : AliasingPeakLocation? {
        //    # The following ranges are only valid if the minimum hr is 45bpm and
        //    # maximum hr is less than 210bpm, since for the acf of the ideal hr signal
        //    # Npeaks = floor(BPM/minHR) - floor(BPM/maxHR)
        //    # in the search ranges 60*fs/maxHR to 60*fs/minHR samples
        val nPeaks: Int =
                when {
                    hr < 90 -> 1
                    hr < 135 -> 2
                    hr < 180 -> 3
                    hr < 225 -> 4
                    hr <= 240 -> 5
                    else -> 0
                }

        if (nPeaks == 0) return null

        // # Added this step because in R, the indexing of an array starts at 1
        // # and so our period of the signal is really actual_lag-1, hence
        // # the correction
        val actualLag = actualLag - 1

        var earlier_peak: List<Int>
        if ((actualLag % 2 == 0)) {
            earlier_peak = listOf(actualLag / 2)
        } else {
            earlier_peak = listOf(Math.floor(actualLag.toDouble() / 2.0).toInt(), Math.ceil(actualLag.toDouble() / 2.0).toInt())
        }
        earlier_peak = earlier_peak.filter { it >= minLag }

        val later_peak: List<Int>
        if (nPeaks > 1) {
            // later_peak <- actual_lag*seq(2,Npeaks)
            // later_peak[later_peak>max_lag] <- NA
            later_peak = IntArray(nPeaks - 1){ (it + 2) * actualLag }.filter { it <= maxLag }
        } else {
            later_peak = listOf()
        }

        // # Correction for R index
        // earlier_peak <- earlier_peak + 1
        // later_peak <- later_peak + 1
        return AliasingPeakLocation(nPeaks, earlier_peak.map { it + 1 }, later_peak.map { it + 1 })
    }

    internal data class PreprocessorBounds(val y_max: Double, val y_max_pos: Int, val y_min: Double, val hr_initial_guess: Double)

    internal fun getBounds(y: DoubleArray, samplingRate: Double) : PreprocessorBounds {
        //    y_max_pos <- which.max(y)
        //    y_max <- max(y)
        //    y_min <- min(y)
        val (y_max, y_max_pos) = y.seekMax()
        val y_min = y.min()!!
        //    hr_initial_guess <- 60 * sampling_rate / (y_max_pos - 1)
        val hr_initial_guess = (60.0 * samplingRate) / (y_max_pos - 1).toDouble()
        return PreprocessorBounds(y_max, y_max_pos, y_min, hr_initial_guess)
    }

    internal data class PreprocessTuple(val x: DoubleArray, val y: DoubleArray, val minLag: Int, val maxLag: Int)

    internal fun preprocessSamples(input: DoubleArray, samplingRate: Double) : PreprocessTuple? {
        //    max_lag = round(60 * sampling_rate / min_hr) # 4/3 fs is 45BPM
        val maxLag = calculateMaxLag(samplingRate)
        //    min_lag = round(60 * sampling_rate / max_hr) # 1/3.5 fs is 210BPM
        val minLag = calculateMinLag(samplingRate)
        //    x <- stats::acf(x, lag.max = max_lag, plot = F)$acf
        val x = autocorrelation(input, maxLag).offsetR()

        // Check that the sampling rate is valid and the max/min are within range.
        val roundedSamplingRate = Math.round(samplingRate).toInt()
        if (!isValidSamplingRate(roundedSamplingRate) ||
                (maxLag >= x.size) ||
                (minLag > maxLag) ||
                (minLag < 0)) return null

        val y = DoubleArray(x.size) {
            if ((it < minLag) || (it > maxLag))  0.0 else x[it]
        }

        return PreprocessTuple(x, y, minLag, maxLag)
    }

    fun chunkSamples(input: List<Double>, samplingRate: Double, window: Double = HEART_RATE_WINDOW_IN_SECONDS) : List<DoubleArray> {
        //    hr.data.filtered.chunks <- hr.data.filtered %>%
        //    dplyr::select(red, green, blue) %>%
        //    na.omit() %>%
        //    lapply(mhealthtools:::window_signal, window_length, window_overlap, 'rectangle')
        val output: MutableList<DoubleArray> = mutableListOf()
        val windowLength = Math.round(window * samplingRate).toInt()
        var start: Int = 0
        var end: Int = windowLength
        while (end < input.size) {
            output.add(input.subList(start, end).toDoubleArray())
            start += Math.round(samplingRate).toInt()
            end = start + windowLength
        }
        return output
    }

    ///#' Bandpass and sorted mean filter the given signal
    ///#'
    ///#' @param x A time series numeric data
    ///#' @param sampling_rate The sampling rate (fs) of the signal
    fun getFilteredSignal(input: List<Double>, samplingRate: Int, dropSeconds: Int = 0) : DoubleArray {
        //        x[is.na(x)] <- 0
        //        x <- x[round(3*sampling_rate):length(x)]
        //        # Ignore the first 3s
        // - note: syoung 04/16/2019 There is a small round-off error in the sample prefiltering code where
        // the first 3 seconds minus 1 is what should actually be dropped. During task run, this is ignored
        // (because the 3 seconds is removed by checking for the isLensCovered flag), but to match his output
        // it is reproduced here for testing purposes.
        val drop = if (dropSeconds > 0) dropSeconds * samplingRate - 1 else 0

        // get minimum value in the input window and offset each signale frame by the minimum value observed in the window
        //        let minValue = x.min()
        //        let minnedOutX = x.map({$0 - minValue!})
        val xInput = input.drop(drop)
        val minValue = xInput.min()!!
        val x = xInput.map { if (it.isFinite()) (it - minValue) else 0.0 }
        //    x <- signal::filter(bf_low, x) # lowpass
        //    x <- x[round(sampling_rate):length(x)] # 1s
        val lowpass = passFilter(x.toDoubleArray(), samplingRate = samplingRate, type = FilterType.low).drop(HEART_RATE_FILTER_DROP_IN_SECONDS.toInt() * samplingRate)
        //    x <- signal::filter(bf_high, x) # highpass
        //    x <- x[round(sampling_rate):length(x)] # 1s @ 60Hz
        val highpass = passFilter(lowpass.toDoubleArray(), samplingRate = samplingRate, type = FilterType.high).drop(HEART_RATE_FILTER_DROP_IN_SECONDS.toInt() * samplingRate)
        // filter using mean centering
        val filtered = meanCenteringFilter(highpass.toDoubleArray(), samplingRate = samplingRate)
        return filtered
    }

    fun autocorrelation(input: DoubleArray, lagMax: Int = 80) : DoubleArray {
        //    x_acf <- rep(0, lag.max+1) # the +1 is because, we will have a 0 lag value also
        //    xl <- length(x) # total no of samples
        //    mx <- mean(x) # average/mean
        //    varx <- sum((x-mx)^2) # Unscaled variance
        //    for(i in seq(0, lag.max)){ # for i=0:lag.max
        //        x_acf[i+1] <- sum( (x[1:(xl-i)]-mx) * (x[(i+1):xl]-mx))/varx // # (Unscaled Co-variance)/(Unscaled variance)
        val xl = input.size
        val mx = input.average()
        val varx: Double = input.fold(0.0) { sum, element -> sum + (element - mx) * (element - mx) }
        val x = input.offsetR()
        val x_acf = DoubleArray(lagMax + 1) {
            val x_left = x.copyOfRange(1, (xl - it) + 1)
            val x_right = x.copyOfRange((it + 1), xl + 1)
            var sum: Double = 0.0
            for (idx in 0 until x_left.size) {
                sum += (x_left[idx] - mx) * (x_right[idx] - mx)
            }
            sum / varx
        }
        return x_acf
    }

    fun meanCenteringFilter(input: DoubleArray, samplingRate: Int) : DoubleArray {
        val mean_filter_order = meanFilterOrder(samplingRate)
        val lowerBounds = (mean_filter_order + 1) / 2
        val upperBounds = input.size - (mean_filter_order - 1) / 2
        val x = input.offsetR()
        val range = IntArray(upperBounds - lowerBounds + 1) { it ->
            (it + lowerBounds)
        }
        val y = range.map { i  ->
            val tempLower: Int = i - (mean_filter_order - 1) / 2
            val tempUpper: Int = i + (mean_filter_order - 1) / 2
            val temp_sequence = x.copyOfRange(tempLower, tempUpper + 1)
            val temp_sum = temp_sequence.sum()
            val temp_max = temp_sequence.max()!!
            val temp_min = temp_sequence.min()!!
            val temp_minus = (temp_sum - temp_max + temp_min) / (mean_filter_order - 2).toDouble()
            // # 0.00001 is a small value, ideally this should be machine epsilon
            ((x[i] - temp_minus) / (temp_max - temp_min + 0.00001))
        }
        return y.toDoubleArray()
    }

    fun calculateMinLag(samplingRate: Double) : Int =
            Math.round(60 * samplingRate / HEART_RATE_MAX).toInt()

    fun calculateMaxLag(samplingRate: Double) : Int =
            Math.round(60 * samplingRate / HEART_RATE_MIN).toInt()

    /// Calculate the sampling rate.
    fun calculateSamplingRate(samples: List<PixelSample>) : Double {
        val startIndex = samples.indexOfFirst { it.isCoveringLens() }
        if (startIndex == -1) return 0.0
        val startTime = samples[startIndex].timestamp
        val endTime = samples.last().timestamp
        val diff = endTime - startTime
        if (diff <= 0) return 0.0
        val count = (samples.size - startIndex).toDouble()
        return count / diff
    }

    /// A valid sampling rate has filter coefficients defined for high and low pass.
    fun isValidSamplingRate(samplingRate: Int) : Boolean {
        val low = lowPassParameters[samplingRate]
        val high = highPassParameters[samplingRate]
        return (low != null) && (high != null)
    }

    val lowPassParameters: Map<Int, CsvUtils.PassFilterParams> = CsvUtils.getLowPassFilterParams()
    val highPassParameters: Map<Int, CsvUtils.PassFilterParams> = CsvUtils.getHighPassFilterParams()

    fun passFilter(input: DoubleArray, samplingRate: Int, type: FilterType) : DoubleArray{
        val paramArray = if ((type == FilterType.low)) lowPassParameters else highPassParameters
        val params = paramArray[samplingRate]
        if (params == null) {
            throw Exception("WARNING! Failed to get butterworth filter coeffients for ${samplingRate}")
        }
        // insert 0 at the first element to offset the array to the 1 index
        val b = params.bParams.offsetR()
        val a = params.aParams.offsetR()
        val x = input.offsetR()
        //    y <- rep(0, xl)
        //    # Create a sequence of 0s of the same length as x
        var y = DoubleArray(x.size)

        //Array(repeating = Double(0), count = x.size)
        //    # For index i, less than the length of the filter we have
        //    # a[1] = 1, always, as the filter coeffs are normalized
        y[1] = b[1] * x[1]
        y[2] = b[1] * x[2] + b[2] * x[1] - a[2] * y[1]
        y[3] = b[1] * x[3] + b[2] * x[2] + b[3] * x[1] - a[2] * y[2] - a[3] * y[1]
        y[4] = b[1] * x[4] + b[2] * x[3] + b[3] * x[2] + b[4] * x[1] - a[2] * y[3] - a[3] * y[2] - a[4] * y[1]
        y[5] = b[1] * x[5] + b[2] * x[4] + b[3] * x[3] + b[4] * x[2] + b[5] * x[1] - a[2] * y[4] - a[3] * y[3] - a[4] * y[2] - a[5] * y[1]
        y[6] = b[1] * x[6] + b[2] * x[5] + b[3] * x[4] + b[4] * x[3] + b[5] * x[2] + b[6] * x[1] - a[2] * y[5] - a[3] * y[4] - a[4] * y[3] - a[5] * y[2] - a[6] * y[1]
        y[7] = b[1] * x[7] + b[2] * x[6] + b[3] * x[5] + b[4] * x[4] + b[5] * x[3] + b[6] * x[2] + b[7] * x[1] - a[2] * y[6] - a[3] * y[5] - a[4] * y[4] - a[5] * y[3] - a[6] * y[2] - a[7] * y[1]
        //    # For index i, greater than or equal to the length of the filter, we have
        //    for(i in seq(8,length(x))){
        for (i in 8 until x.size) {
            y[i] = b[1] * x[i] + b[2] * x[i - 1] + b[3] * x[i - 2] + b[4] * x[i - 3] + b[5] * x[i - 4] + b[6] * x[i - 5] + b[7] * x[i - 6] + b[8] * x[i - 7] - a[2] * y[i - 1] - a[3] * y[i - 2] - a[4] * y[i - 3] - a[5] * y[i - 4] - a[6] * y[i - 5] - a[7] * y[i - 6] - a[8] * y[i - 7]
        }
        return y.drop(1).toDoubleArray()
    }

    fun meanFilterOrder(samplingRate: Int) : Int {
        return when {
            samplingRate <= 15 -> 15
            samplingRate <= 18 -> 19
            samplingRate <= 32 -> 33
            else -> 65
        }
    }
}

enum class FilterType {
    low, high
}

/**
 * R is indexed from 1 so to simplify porting the code, everything is offset by one
 * in the filters before calculating.
 */
internal fun DoubleArray.offsetR() : DoubleArray {
    var list = this.toMutableList()
    list.add(0, 0.0)
    return list.toDoubleArray()
}

/**
 * Returns the max value and index of that value.
 */
internal fun DoubleArray.seekMax(): ValueAndIndex {
    val value = this.max()!!
    val index = this.indexOf(value)
    return ValueAndIndex(value, index)
}

