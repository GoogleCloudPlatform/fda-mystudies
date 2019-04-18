package org.sagebase.crf.step.active

import org.sagebase.crf.linearAlgebra.*

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

data class HeartRateBPM(val uptime: Double, val bpm: Int, val confidence: Double, val channel: String)

class HeartRateSampleProcessor @JvmOverloads constructor(val videoProcessorFrameRate: Int = SUPPORTED_FRAME_RATES.first()) {

    var bpmRecords = mutableListOf<HeartRateBPM>()
    var pixelSamples = mutableListOf<HeartBeatSample>()

    fun addSample(sample: HeartBeatSample) {
        // Add the pixel sample
        this.pixelSamples.add(sample)
    }

    fun isReadyToProcess(): Boolean {
        // look to see if we have enough to process a bpm
        return (pixelSamples.size >= calculateWindowLength())
    }

    private fun calculateWindowLength(): Int = HEART_RATE_WINDOW_IN_SECONDS.toInt() * this.videoProcessorFrameRate

    fun processSamples(): HeartRateBPM {
        val windowLen = calculateWindowLength()
        val halfLength = windowLen / 2
        val uptime = this.pixelSamples[halfLength].uptime
        val redChannel = this.pixelSamples.map { s -> s.red }
        val greenChannel = this.pixelSamples.map { s -> s.green }
        this.pixelSamples = this.pixelSamples.subList(halfLength, this.pixelSamples.size)
        val redCalc = calculateHeartRate(redChannel)
        val greenCalc = calculateHeartRate(greenChannel)
        val bpm =
                if (redCalc.confidence > greenCalc.confidence)
                    HeartRateBPM(uptime, redCalc.heartRate.toInt(), redCalc.confidence, "red")
                else
                    HeartRateBPM(uptime, greenCalc.heartRate.toInt(), greenCalc.confidence, "green")

        bpmRecords.add(bpm)
        return bpm
    }

    // --- Code ported from Matlab
    private val fs = videoProcessorFrameRate.toDouble()         // frames / second
    private val window = HEART_RATE_WINDOW_IN_SECONDS           // seconds
    private val windowLength = Math.round(fs * window).toInt()


    /**
     * number of frames in the window
     * channel, 60fps, 10sec window
     */
    internal fun findHeartRateValues(channel: List<Double>): List<CalculatedHeartRate> {
        val nframes = Math.floor(channel.size.toDouble() / (windowLength.toDouble() / 2.0)).toInt() - 1
        if (nframes < 1) {
            return listOf()
        }
        val output = mutableListOf<CalculatedHeartRate>()
        for (frame_no in 1..nframes) {
            val lower = (1 + ((frame_no - 1) * windowLength / 2)) - 1
            val upper = ((frame_no + 1) * windowLength / 2) - 1
            val currframe = channel.subList(lower, upper + 1)
            output.add(calculateHeartRate(currframe))
        }
        return output
    }

    internal data class CalculatedHeartRate(val heartRate: Long, val confidence: Double)

    /**
     * For a given window return the calculated heart rate and confidence.
     * @note The calculated heart rate is rounded.
     */
    private fun calculateHeartRate(input: List<Double>): CalculatedHeartRate {
        //% Preprocess and find the autocorrelation function
        val filteredValues = bandpassFiltered(input.toTypedArray())
        val xCorrValues = LinearAlgebra.xcorr(filteredValues)
        //% To just remove the repeated part of the autocorr function (since it is even)
        val maxRet = xCorrValues.maxSplice()
        val x = maxRet.v2.toTypedArray()

        //% HR ranges from 40-200 BPM, so consider only that part of the autocorr
        //% function
        val lower = Math.round(60.0 * fs / 200.0).toInt()
        val upper = Math.round(60.0 * fs / 40.0).toInt()
        val retVal = x.zeroReplace(lower-1, upper-1).seekMax()
        val value = retVal.value
        val pos = retVal.index
        val heartRate = Math.round(60.0 * fs / (pos.toDouble() + 1.0))
        return CalculatedHeartRate(heartRate, value / maxRet.maxValue)
    }

    internal fun bandpassFiltered(input: Array<Double>): Array<Double> {
        // % Setting no. of samples as per a max HR of 220 BPM
        val nsamples = Math.round(60 * fs / 220).toInt()
        // % b1 = fir1(128,[1/30, 25/30], 'bandpass');
        val b1: Array<Double> = arrayOf(-0.000506610984132016, 0.000281340196104213, -0.000453477478785663, 0.000175433848479960, 5.78571000126717e-19, -0.000200178238070410, 0.000588479261901569, -0.000412615808534457, 0.000832401037231464, -4.84818239396100e-19, 0.000465554741153073, 0.00102165166976478, -0.000118534274769341, 0.00192609062899124, -2.40024436102973e-18, 0.00182952606970045, 0.00135480554590726, 0.000748599044261129, 0.00319643179850945, -2.30788276369201e-19, 0.00382994518525259, 0.00107470141262219, 0.00233017559097417, 0.00376919225339987, -8.21109764793137e-18, 0.00568709829032464, -0.000418547259970266, 0.00430878547299781, 0.00234096774958672, -1.06597329751523e-17, 0.00589948032626289, -0.00345001874823703, 0.00577085280898743, -0.00228532700432350, -3.81044085438483e-18, 0.00263801974428747, -0.00769131382422690, 0.00531148463293734, -0.0104990208677403, 1.62815935886881e-17, -0.00558417076326117, -0.0119241848598587, 0.00134611898423683, -0.0212997771796790, -2.07091826506435e-17, -0.0192845505914200, -0.0139952617851127, -0.00760318790070690, -0.0320397640632609, -3.05719612807051e-18, -0.0378997870775431, -0.0106518977344771, -0.0232807805994706, -0.0382418951609459, 1.64113172833343e-17, -0.0611787321852445, 0.00471988055056295, -0.0517540592057603, -0.0305770938728010, 3.42293636763843e-17, -0.100426633129967, 0.0729786483544900, -0.170609488045242, 0.125861208906484, 0.800308136102957, 0.125861208906484, -0.170609488045242, 0.0729786483544900, -0.100426633129967, 3.42293636763843e-17, -0.0305770938728010, -0.0517540592057603, 0.00471988055056295, -0.0611787321852445, 1.64113172833343e-17, -0.0382418951609459, -0.0232807805994706, -0.0106518977344771, -0.0378997870775431, -3.05719612807051e-18, -0.0320397640632609, -0.00760318790070690, -0.0139952617851127, -0.0192845505914200, -2.07091826506435e-17, -0.0212997771796790, 0.00134611898423683, -0.0119241848598587, -0.00558417076326117, 1.62815935886881e-17, -0.0104990208677403, 0.00531148463293734, -0.00769131382422690, 0.00263801974428747, -3.81044085438483e-18, -0.00228532700432350, 0.00577085280898743, -0.00345001874823703, 0.00589948032626289, -1.06597329751523e-17, 0.00234096774958672, 0.00430878547299781, -0.000418547259970266, 0.00568709829032464, -8.21109764793137e-18, 0.00376919225339987, 0.00233017559097417, 0.00107470141262219, 0.00382994518525259, -2.30788276369201e-19, 0.00319643179850945, 0.000748599044261129, 0.00135480554590726, 0.00182952606970045, -2.40024436102973e-18, 0.00192609062899124, -0.000118534274769341, 0.00102165166976478, 0.000465554741153073, -4.84818239396100e-19, 0.000832401037231464, -0.000412615808534457, 0.000588479261901569, -0.000200178238070410, 5.78571000126717e-19, 0.000175433848479960, -0.000453477478785663, 0.000281340196104213, -0.000506610984132016)
        // Normalize the input
        val meanValue = input.average()
        val normalizedValues = input.map { (it - meanValue) }
        //% Preprocess and find the autocorrelation function
        return meanfilter(normalizedValues.toTypedArray(), 2 * nsamples + 1, b1)
    }

    /**
     * Mean filter which emphasizes the maxima in a specified window length (n), but de-emphasizes
     * everything else in that window.
     */
    private fun meanfilter(input: Array<Double>, n: Int, b1: Array<Double>): Array<Double> {
        val x = LinearAlgebra.conv(input, b1, LinearAlgebra.ConvolutionType.SAME).centerSplice(65)
        val output = x.copyOf()
        for (nn in ((n + 1) / 2)..(x.size - (n - 1) / 2)) {
            val lower = (nn - (n - 1) / 2) - 1
            val upper = (nn + (n - 1) / 2) - 1
            val currwin = x.copyOfRange(lower, upper + 1).sortedArray()
            output[nn - 1] = x[nn - 1] - ((currwin.sum() - currwin.max()!!) / (n - 1).toDouble())
        }
        return output
    }

    // --- Code ported from Swift implementation (CRF - SageResearch), originally validated in R. syoung 04/17/2019

    fun chunkSamples(input: List<Double>, samplingRate: Double, window: Double = HEART_RATE_WINDOW_IN_SECONDS) : List<List<Double>> {
        //    hr.data.filtered.chunks <- hr.data.filtered %>%
        //    dplyr::select(red, green, blue) %>%
        //    na.omit() %>%
        //    lapply(mhealthtools:::window_signal, window_length, window_overlap, 'rectangle')
        var output: MutableList<List<Double>> = mutableListOf()
        val windowLength = Math.round(window * samplingRate).toInt()
        var start: Int = 0
        var end: Int = windowLength
        while (end < input.size) {
            output.add(input.subList(start, end))
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
        val x = input.drop(drop).map { if (it.isFinite()) it else 0.0 }
        //    x <- signal::filter(bf_low, x) # lowpass
        //    x <- x[round(sampling_rate):length(x)] # 1s
        val lowpass = passFilter(x.toDoubleArray(), samplingRate = samplingRate, type = FilterType.low).drop(samplingRate)
        //    x <- signal::filter(bf_high, x) # highpass
        //    x <- x[round(sampling_rate):length(x)] # 1s @ 60Hz
        val highpass = passFilter(lowpass.toDoubleArray(), samplingRate = samplingRate, type = FilterType.high).drop(samplingRate)
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
        val varx: Double = input.reduce() { sum, element -> sum + (element - mx) * (element - mx) }
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

    // TODO: syoung 04/17/2019 Need to figure out how to call the CsvUtils method. What's a context?
    val lowPassParameters: Map<Int, CsvUtils.PassFilterParams> = HashMap<Int, CsvUtils.PassFilterParams>()
    val highPassParameters: Map<Int, CsvUtils.PassFilterParams> = HashMap<Int, CsvUtils.PassFilterParams>()

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
        if ((samplingRate <= 32)) {
            return 33
        } else if ((samplingRate <= 18)) {
            return 19
        } else if ((samplingRate <= 15)) {
            return 15
        } else {
            return 65
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

