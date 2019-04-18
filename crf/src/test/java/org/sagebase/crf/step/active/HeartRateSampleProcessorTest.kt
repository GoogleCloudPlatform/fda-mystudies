/*
 *    Copyright 2019 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.sagebase.crf.step.active

import com.google.gson.Gson
import org.junit.Test

import org.junit.Assert.*
import org.sagebase.crf.linearAlgebra.*
import kotlin.math.roundToInt

class HeartRateSampleProcessorTest {


    @Test
    fun testConv() {

        val u = Array(10) { ii -> ii.toDouble() + 1.0 }
        val v = Array(15) { ii -> ii.toDouble() + 1.0 }

        val output1 = LinearAlgebra.conv(u, v, LinearAlgebra.ConvolutionType.SAME)
        val expectedAnswer1 = arrayOf(120.0, 165.0, 220.0, 275.0, 330.0, 385.0, 440.0, 495.0, 534.0, 556.0)
        assertArrayEquals(expectedAnswer1, output1)

        val output2 = LinearAlgebra.conv(v, u, LinearAlgebra.ConvolutionType.SAME)
        val expectedAnswer2 = arrayOf(56.0, 84.0, 120.0, 165.0, 220.0, 275.0, 330.0, 385.0, 440.0, 495.0, 534.0, 556.0, 560.0, 545.0, 510.0)
        assertArrayEquals(expectedAnswer2, output2)

        val input1 = u.zeroPadBefore(9)
        val input2 = u.zeroPadAfter(9)

        val expectedAnswer3 = arrayOf(1.0, 4.0, 10.0, 20.0, 35.0, 56.0, 84.0, 120.0, 165.0, 220.0, 264.0, 296.0, 315.0, 320.0, 310.0, 284.0, 241.0, 180.0, 100.0)
        val convNoPad = LinearAlgebra.conv(u, u)
        val convPad = LinearAlgebra.conv(input1, input2, LinearAlgebra.ConvolutionType.SAME)
        assertArrayEquals(expectedAnswer3, convNoPad)
        assertArrayEquals(expectedAnswer3, convPad)
    }

    @Test
    fun testXCorr() {
        val input = Array(10) { ii -> ii.toDouble() + 1.0 }

        val output = LinearAlgebra.xcorr(input)
        val expectedAnswer = arrayOf(10.0, 29.0, 56.0, 90.0, 130.0, 175.0, 224.0, 276.0, 330.0, 385.0, 330.0, 276.0, 224.0, 175.0, 130.0, 90.0, 56.0, 29.0, 10.0)
        assertArrayEquals(expectedAnswer, output)
    }

    @Test
    fun testParams() {
        val processor = HeartRateSampleProcessor()
        assertEquals(56, processor.highPassParameters.size)
        assertEquals(56, processor.lowPassParameters.size)
    }

    @Test
    fun testInvalidSamplingRate() {
        val processor = HeartRateSampleProcessor()
        assertFalse(processor.isValidSamplingRate(2))
    }

    @Test
    fun testLowPassFilterParams() {
        val processor = HeartRateSampleProcessor()
        val testData = testData()
        assertEquals(56, processor.lowPassParameters.size)
        val filter = processor.lowPassParameters[testData.roundedSamplingRate()]
        assertNotNull(filter)
        if (filter == null) return
        compare(testData.a_lowpass, filter.aParams, accuracy = 0.00000001)
        compare(testData.b_lowpass, filter.bParams, accuracy = 0.00000001)
    }

    @Test
    fun testHighPassFilterParams() {
        val processor = HeartRateSampleProcessor()
        val testData = testData()
        assertEquals(56, processor.highPassParameters.size)
        val filter = processor.highPassParameters[testData.roundedSamplingRate()]
        assertNotNull(filter)
        if (filter == null) return
        compare(testData.a_highpass, filter.aParams, accuracy = 0.00000001)
        compare(testData.b_highpass, filter.bParams, accuracy = 0.00000001)
    }

    @Test
    fun testLowPassFilter() {
        val processor = HeartRateSampleProcessor()
        val testData = testData()
        val isValid = processor.isValidSamplingRate(testData.roundedSamplingRate())
        assertTrue(isValid)
        if (!isValid) {
            return
        }
        val result = processor.passFilter(testData.input, samplingRate = testData.roundedSamplingRate(), type = FilterType.low)
        compare(testData.lowpass, result, accuracy = 0.0001)
    }

    @Test
    fun testHighPassFilter() {
        val processor = HeartRateSampleProcessor()
        val testData = testData()
        val isValid = processor.isValidSamplingRate(testData.roundedSamplingRate())
        assertTrue(isValid)
        if (!isValid) {
            return
        }
        val result = processor.passFilter(testData.input, samplingRate = testData.roundedSamplingRate(), type = FilterType.high)
        compare(testData.highpass, result, accuracy = 0.0001)
    }

    @Test
    fun testMeanCenteringFilter() {
        val processor = HeartRateSampleProcessor()
        val testData = testData()
        val mcf = processor.meanCenteringFilter(testData.input, samplingRate = testData.roundedSamplingRate())
        compare(testData.mcfilter, mcf, accuracy = 0.000001)
    }

    @Test
    fun testAutocorrelation() {
        val processor = HeartRateSampleProcessor()
        val testData = testData()
        val acf = processor.autocorrelation(testData.input)
        compare(testData.acf, acf, accuracy = 0.0001)
    }

    @Test
    fun testCalculateSamplingRate() {
        val processor = HeartRateSampleProcessor()
        val testHRData = testHRData()
        val samplingRate = processor.calculateSamplingRate(testHRData.hr_data.toList())
        assertEquals(59.980, samplingRate, 0.001)
    }

    @Test
    fun testCalculateSamplingRate_12hz() {
        val processor = HeartRateSampleProcessor()
        val testHRData = testHRData12hz()
        val samplingRate = processor.calculateSamplingRate(testHRData.hr_data.toList())
        assertEquals(12.728, samplingRate, 0.001)
    }

    @Test
    fun testMeanOrderValue() {
        val processor = HeartRateSampleProcessor()
        assertEquals(15, processor.meanFilterOrder(12))
        assertEquals(15, processor.meanFilterOrder(15))
        assertEquals(19, processor.meanFilterOrder(16))
        assertEquals(19, processor.meanFilterOrder(18))
        assertEquals(33, processor.meanFilterOrder(19))
        assertEquals(33, processor.meanFilterOrder(32))
        assertEquals(65, processor.meanFilterOrder(33))
        assertEquals(65, processor.meanFilterOrder(60))
    }

    @Test
    fun testGetFilteredSignal_Red() {
        val processor = HeartRateSampleProcessor()
        val testHRData = testHRData()

        val input = testHRData.hr_data.map { it.red }
        val samplingRate = 60
        val expectedOutput = testHRData.hr_data_filtered.map { it.red }.toDoubleArray()
        val output = processor.getFilteredSignal(input, samplingRate, 3)
        compare(expectedOutput, output, 0.0001)
    }

    @Test
    fun testGetFilteredSignal_Green() {
        val processor = HeartRateSampleProcessor()
        val testHRData = testHRData()

        val input = testHRData.hr_data.map { it.green }
        val samplingRate = 60
        val expectedOutput = testHRData.hr_data_filtered.map { it.green }.toDoubleArray()
        val output = processor.getFilteredSignal(input, samplingRate, 3)
        compare(expectedOutput, output, 0.0001)
    }


    @Test
    fun testCalculatedLag() {
        val processor = HeartRateSampleProcessor()
        val testData = testData()

        val minLag = processor.calculateMinLag(testData.samplingRate())
        assertEquals(testData.minLag(), minLag)
        val maxLag = processor.calculateMaxLag(testData.samplingRate())
        assertEquals(testData.maxLag(), maxLag)
    }

    @Test
    fun testChunkedSamples() {
        val processor = HeartRateSampleProcessor()
        val testHRData = testHRData()

        val samplingRate = processor.calculateSamplingRate(testHRData.hr_data.toList())
        val input = testHRData.hr_data_filtered.map { it.red }
        val expectedOutput = testHRData.flip(testHRData.hr_data_filtered_chunked.red)
        val output = processor.chunkSamples(input, samplingRate)
        assertEquals(output.size, expectedOutput.size)
        for (ii in 0 until output.size) {
            compare(output[ii], expectedOutput[ii], 0.00000001)
        }
    }

    @Test
    fun testEarlierPeaks() {
        val processor = HeartRateSampleProcessor()
        val data = testEarlierPeaksExample()
        val samplingRate = data.generatedSamplingRate()

        val output = processor.preprocessSamples(data.x, samplingRate)
        assertNotNull(output)
        compare(data.xacf, output!!.x.drop(1).toDoubleArray(), accuracy = 0.00000001)
        assertEquals(data.minLag(), output.minLag)
        assertEquals(data.maxLag(), output.maxLag)
        assertEquals(data.xMin(), output.x.min()!!, 0.00000001)
        assertEquals(data.xMax(), output.x.max()!!,0.00000001)
        assertEquals(data.xMax(), data.xacf.max()!!,0.00000001)
        compare(data.y_output(), output.y.drop(1).toDoubleArray(), accuracy = 0.00000001)

        val bounds = processor.getBounds(output.y, samplingRate)
        assertEquals(data.initialGuess(), bounds.hr_initial_guess,0.00000001)
        assertEquals(data.yMin(), bounds.y_min,0.00000001)
        assertEquals(data.yMax(), bounds.y_max,0.00000001)

        val peaks = processor.getAliasingPeakLocation(bounds.hr_initial_guess, bounds.y_max_pos, output.minLag, output.maxLag)
        assertNotNull(peaks)

        assertEquals(data.aliased_peak.Npeaks.first(), peaks!!.nPeaks)
        assertEquals(data.aliased_peak.earlier_peak.toList(), peaks!!.earlier)
        assertEquals(data.aliased_peak.later_peak.toList(), peaks!!.later)

        val (hr, confidence) = processor.calculateHRFromFilteredSamples(data.x, samplingRate)
        assertEquals(data.estimatedHR(), hr, 0.00000001)
        assertEquals(data.estimatedConfidence(), confidence,0.00000001)
    }

    @Test
    fun testLaterPeaks() {
        val processor = HeartRateSampleProcessor()
        val data = testLaterPeaksExample()
        val samplingRate = data.generatedSamplingRate()

        val output = processor.preprocessSamples(data.x, samplingRate)
        assertNotNull(output)
        compare(data.xacf, output!!.x.drop(1).toDoubleArray(), accuracy = 0.00000001)
        assertEquals(data.minLag(), output.minLag)
        assertEquals(data.maxLag(), output.maxLag)
        assertEquals(data.xMin(), output.x.min()!!, 0.00000001)
        assertEquals(data.xMax(), output.x.max()!!,0.00000001)
        assertEquals(data.xMax(), data.xacf.max()!!,0.00000001)
        compare(data.y_output(), output.y.drop(1).toDoubleArray(), accuracy = 0.00000001)

        val bounds = processor.getBounds(output.y, samplingRate)
        assertEquals(data.initialGuess(), bounds.hr_initial_guess,0.00000001)
        assertEquals(data.yMin(), bounds.y_min,0.00000001)
        assertEquals(data.yMax(), bounds.y_max,0.00000001)

        val peaks = processor.getAliasingPeakLocation(bounds.hr_initial_guess, bounds.y_max_pos, output.minLag, output.maxLag)
        assertNotNull(peaks)

        assertEquals(data.aliased_peak.Npeaks.first(), peaks!!.nPeaks)
        assertEquals(data.aliased_peak.earlier_peak.toList(), peaks!!.earlier)
        assertEquals(data.aliased_peak.later_peak.toList(), peaks!!.later)

        val (hr, confidence) = processor.calculateHRFromFilteredSamples(data.x, samplingRate)
        assertEquals(data.estimatedHR(), hr, 0.00000001)
        assertEquals(data.estimatedConfidence(), confidence,0.00000001)
    }

    @Test
    fun testEstimatedHR_Red() {
        val processor = HeartRateSampleProcessor()
        val testHRData = testHRData()

        val samplingRate = processor.calculateSamplingRate(testHRData.hr_data.toList())

        val inputChunks: Array<DoubleArray> = testHRData.flip(testHRData.hr_data_filtered_chunked.red)
        val expectedOutputs: Array<HRTuple> = testHRData.hr_estimates.red

        // check assumption
        assertEquals(inputChunks.size, expectedOutputs.size)

        // Only check some of the results b/c the calculations are for a resting heart rate and all use
        // initial estimate or else they are invalid (Initial windows).
        for (ii in 0 until Math.min(10, expectedOutputs.size)) {
            val (hr, confidence) = processor.calculateHRFromFilteredSamples(inputChunks[ii], samplingRate)
            val expectedHR = expectedOutputs[ii].hr
            val expectedConfidence = expectedOutputs[ii].confidence
            assertEquals(expectedHR, hr,0.000001)
            assertEquals(expectedConfidence, confidence, 0.000001)
        }
    }

    @Test
    fun testEstimatedHR_Green() {
        val processor = HeartRateSampleProcessor()
        val testHRData = testHRData()

        val samplingRate = processor.calculateSamplingRate(testHRData.hr_data.toList())

        val inputChunks: Array<DoubleArray> = testHRData.flip(testHRData.hr_data_filtered_chunked.green)
        val expectedOutputs: Array<HRTuple> = testHRData.hr_estimates.green

        // check assumption
        assertEquals(inputChunks.size, expectedOutputs.size)

        // Only check some of the results b/c the calculations are for a resting heart rate and all use
        // initial estimate or else they are invalid (Initial windows).
        for (ii in 0 until Math.min(10, expectedOutputs.size)) {
            val (hr, confidence) = processor.calculateHRFromFilteredSamples(inputChunks[ii], samplingRate)
            val expectedHR = expectedOutputs[ii].hr
            val expectedConfidence = expectedOutputs[ii].confidence
            assertEquals(expectedHR, hr,0.000001)
            assertEquals(expectedConfidence, confidence, 0.000001)
        }
    }

    @Test
    fun testEstimatedHR_12hz() {
        val processor = HeartRateSampleProcessor()
        val testHRData = testHRData12hz()

        val samplingRate = processor.calculateSamplingRate(testHRData.hr_data.toList())

        val inputChunks: Array<DoubleArray> = testHRData.flip(testHRData.hr_data_filtered_chunked.red)
        val expectedOutputs: Array<HRTuple> = testHRData.hr_estimates.red

        // check assumption
        assertEquals(inputChunks.size, expectedOutputs.size)

        // Only check some of the results b/c the calculations are for a resting heart rate and all use
        // initial estimate or else they are invalid (Initial windows).
        for (ii in 0 until Math.min(10, expectedOutputs.size)) {
            val (hr, confidence) = processor.calculateHRFromFilteredSamples(inputChunks[ii], samplingRate)
            val expectedHR = expectedOutputs[ii].hr
            val expectedConfidence = expectedOutputs[ii].confidence
            assertEquals(expectedHR, hr,0.000001)
            assertEquals(expectedConfidence, confidence, 0.000001)
        }
    }

    @Test
    fun testIsReadyToProcess() {

        val processor = HeartRateSampleProcessor()
        val testHRData = testHRData()
        val estimatedSamplingRate = 60.0
        val drop = (3.0 * estimatedSamplingRate).roundToInt() - 1
        val samples = testHRData.hr_data.drop(drop)
        val roundedRate = estimatedSamplingRate.roundToInt()

        // Add samples less than 12 seconds + mean order (65)
        val eleven = roundedRate * 11
        processor.pixelSamples.addAll(samples.subList(0, eleven))
        assertFalse(processor.isReadyToProcess())

        // Add samples for 12 seconds + mean order
        val nextChunk = eleven + roundedRate + 65
        processor.pixelSamples.addAll(samples.subList(eleven, nextChunk))
        assertTrue(processor.isReadyToProcess())
    }

    @Test
    fun testIsReadyToProcess_12hz() {

        val processor = HeartRateSampleProcessor()
        val testHRData = testHRData12hz()
        val estimatedSamplingRate = 12.728
        val drop = (3.0 * estimatedSamplingRate).roundToInt() - 1
        val samples = testHRData.hr_data.drop(drop)
        val roundedRate = estimatedSamplingRate.roundToInt()

        // Add samples less than 12 seconds
        val eleven = roundedRate * 11
        processor.pixelSamples.addAll(samples.subList(0, eleven))
        assertFalse(processor.isReadyToProcess())

        // Add samples for 12 seconds + mean order
        val nextChunk = eleven + roundedRate + 15 + 1
        processor.pixelSamples.addAll(samples.subList(eleven, nextChunk))
        assertTrue(processor.isReadyToProcess())
    }

    @Test
    fun testProcessSamples() {

        val processor = HeartRateSampleProcessor()
        val testHRData = testHRData()
        val estimatedSamplingRate = 60.0

        // Because this test takes a while to run, and it's using data from a resting heart rate,
        // only process a subset of the data. syoung 04/16/2019
        val start = 30
        val expectedHRValues = arrayOf<HeartRateBPM>(
                HeartRateBPM(47.3040004163,69.27709332103974,0.8683590800933014,"green"),
                HeartRateBPM(48.3046064583,67.96997857106084,0.8644802170884017,"green"),
                HeartRateBPM(49.3052123743,69.27709376612277,0.8936955072357293,"green"),
                HeartRateBPM(50.3058184583,67.96997813437561,0.9014633198345332,"green"),
                HeartRateBPM(51.3064244993,67.9699768399158,0.9108713480292215,"green"),
                HeartRateBPM(52.3070303743,67.96997748974505,0.9071506266991106,"green"),
                HeartRateBPM(53.3076363743,67.96997792123159,0.9000145584327659,"green"),
                HeartRateBPM(54.3082423743,67.96997792123162,0.8998513053646106,"green"),
                HeartRateBPM(55.3088484993,67.96997727140241,0.9128580892467326,"green"),
                HeartRateBPM(56.3094543333,67.96997792123159,0.9029084106060474,"green"),
                HeartRateBPM(57.3100602913,67.96997792123162,0.89087079839508,"green"),
                HeartRateBPM(58.3106662913,67.96997770288898,0.8793877599812155,"green"),
                HeartRateBPM(59.3112722493,67.96997835791682,0.8893933827220402,"green"),
                HeartRateBPM(60.3118782083,67.9699783527182,0.8879583462485395,"green"),
                HeartRateBPM(61.3124843743,67.96997705825844,0.8743429841892499,"green")
        )

        val drop = (3.0 * estimatedSamplingRate).roundToInt() - 1 + (start * estimatedSamplingRate).roundToInt()
        val samples = testHRData.hr_data.drop(drop)
        var expectedIdx = 0

        // Test expectations to see that they match the Swift implementation
        assertEquals(1979, drop)
        assertEquals(1681, samples.size)
        assertEquals(34.2294156243, samples.first().timestamp, 0.0000001)

        samples.forEach {
            processor.addSample(it)
            if (processor.isReadyToProcess()) {

                val hr= processor.processSamples()

                assertEquals(expectedHRValues[expectedIdx].bpm, hr.bpm, 0.000001)
                assertEquals(expectedHRValues[expectedIdx].confidence, hr.confidence, 0.000001)
                assertEquals(expectedHRValues[expectedIdx].timestamp, hr.timestamp, 0.000001)
                assertEquals(expectedHRValues[expectedIdx].channel, hr.channel)

                expectedIdx++
            }
        }
    }

    // Helper functions

    fun compare(array1: DoubleArray, array2: DoubleArray, accuracy: Double) {
        assertEquals(array1.size, array2.size)
        for (idx in 0 until array1.size) {
            val lhv = array1[idx]
            val rhv = array2[idx]
            if (Math.abs(lhv - rhv) > accuracy) {
                fail("Expected ${lhv} not equal to ${rhv} at index ${idx}")
            }
        }
    }

    fun testData(): ProcessorTestData {

        val json = this.javaClass.classLoader.getResource("io_examples.json").readText()
        val gson = Gson()
        val testData = gson.fromJson(json, ProcessorTestData::class.java)
        return testData
    }

    fun testEarlierPeaksExample(): TestHRPeaks {
        val json = this.javaClass.classLoader.getResource("io_example_earlier_peak.json").readText()
        val gson = Gson()
        val testData = gson.fromJson(json, TestHRPeaks::class.java)
        return testData
    }

    fun testLaterPeaksExample(): TestHRPeaks {
        val json = this.javaClass.classLoader.getResource("io_example_later_peak.json").readText()
        val gson = Gson()
        val testData = gson.fromJson(json, TestHRPeaks::class.java)
        return testData
    }

    fun testHRData(): HRProcessorTestData {
        val json = this.javaClass.classLoader.getResource("io_examples_whole.json").readText()
        val gson = Gson()
        val testData = gson.fromJson(json, HRProcessorTestData::class.java)
        return testData
    }

    fun testHRData12hz(): HRProcessorTestData {
        val json = this.javaClass.classLoader.getResource("io_examples_whole_12hz.json").readText()
        val gson = Gson()
        val testData = gson.fromJson(json, HRProcessorTestData::class.java)
        return testData
    }
}