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
import com.google.gson.annotations.SerializedName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class HeartRateTestDataLoader {

    @Test
    fun testTestData() {
        val testData = testData();
        assertNotNull(testData)
        assertEquals(59.9870768478, testData.samplingRate(), 0.000001)
        assertEquals(60, testData.roundedSamplingRate())
        assertEquals(80, testData.maxLag())
        assertEquals(17, testData.minLag())
    }

    @Test
    fun testTestEarlierPeaksExample() {
        val testData = testEarlierPeaksExample();
        assertNotNull(testData)
        assertEquals(60.0, testData.generatedSamplingRate(), 0.000001)
        assertEquals(47.3684210526, testData.initialGuess(), 0.000001)
    }

    @Test
    fun testTestLaterPeaksExample() {
        val testData = testLaterPeaksExample();
        assertNotNull(testData)
        assertEquals(60.0, testData.generatedSamplingRate(), 0.000001)
        assertEquals(189.4736842105, testData.initialGuess(), 0.000001)
    }

    @Test
    fun testTestHRData() {
        val testData = testHRData();
        assertNotNull(testData)
        assertEquals(0.010989078321, testData.hr_data.first().blue, 0.000001)
    }

    @Test
    fun testTestHRData12hz() {
        val testData = testHRData12hz();
        assertNotNull(testData)
        assertEquals(0.005565308415, testData.hr_data.first().blue, 0.000001)
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

    data class ProcessorTestData(
        val input : DoubleArray,
        val lowpass : DoubleArray,
        val highpass : DoubleArray,
        val mcfilter : DoubleArray,
        val acf : DoubleArray,
        val b_lowpass : DoubleArray,
        val a_lowpass : DoubleArray,
        val b_highpass : DoubleArray,
        val a_highpass : DoubleArray,
        val mean_filter_order : Array<Int>,
        val sampling_rate_round : Array<Int>,
        val sampling_rate: Array<Double>,
        val max_hr: Array<Int>,
        val min_hr: Array<Int>,
        val max_lag: Array<Int>,
        val min_lag: Array<Int>
    ) {

        fun samplingRate(): Double {
            return sampling_rate.first()
        }

        fun maxLag(): Int {
            return max_lag.first()
        }

        fun minLag(): Int {
            return min_lag.first()
        }

        fun roundedSamplingRate(): Int {
            return sampling_rate_round.first()
        }
    }

    data class ColorData(var red: Double, var green: Double, var blue: Double)
    data class ColorDataChunked(var red: Array<Array<Double>>, var green: Array<Array<Double>>, var blue: Array<Array<Double>>)
    data class HrEstimates(var red: Array<HRTuple>, var green: Array<HRTuple>, var blue: Array<HRTuple>)

    data class HRProcessorTestData(
        var hr_data : Array<TestPixelSample>,
        var hr_data_filtered : Array<ColorData>,
        var hr_data_filtered_chunked : ColorDataChunked,
        var hr_data_filtered_chunked_acf : ColorDataChunked,
        var hr_estimates : HrEstimates
    ) {

        fun flip(vector: Array<Array<Double>>) : Array<DoubleArray> {
            val first = vector.first()
            val ret = Array<DoubleArray>(first.size) { nn  ->
                DoubleArray(vector.size) { vector[it][nn] }
            }
            return ret
        }
    }


    data class TestHRPeaks(
        var x: DoubleArray,
        var y: Array<Array<Array<Double>>>,
        var xacf: DoubleArray,
        var hr_initial_guess: Array<Double>,
        var est_hr: Array<Double>,
        var est_conf: Array<Double>,
        var aliased_peak: AliasedPeak,
        var sampling_rate: Array<Double>,
        var min_lag: Array<Int>,
        var max_lag: Array<Int>,
        var y_min: Array<Double>,
        var y_max: Array<Double>,
        var x_min: Array<Double>,
        var x_max: Array<Double>
    ) {

        data class AliasedPeak(
            var Npeaks : Array<Int>,
            var earlier_peak : Array<Int>,
            var later_peak : Array<Int>
        )

        fun generatedSamplingRate() : Double {
            return sampling_rate.first()
        }

        fun initialGuess() : Double {
            return hr_initial_guess.first()
        }

        fun estimatedHR() : Double {
            return est_hr.first()
        }

        fun estimatedConfidence() : Double {
            return est_conf.first()
        }

        fun y_output(): DoubleArray {
            return y.map { it.first().first() }.toDoubleArray()
        }

        fun yMin() : Double {
            return y_min.first()
        }

        fun yMax() : Double {
            return y_max.first()
        }

        fun xMin() : Double {
            return x_min.first()
        }

        fun xMax() : Double {
            return x_max.first()
        }

        fun minLag() : Int {
            return min_lag.first()
        }

        fun maxLag() : Int {
            return max_lag.first()
        }
    }

    data class TestPixelSample (
            @SerializedName("t") override val timestamp: Double,
            override val red: Double,
            override val green: Double,
            override val blue: Double
            ) : PixelSample {

        override
        val uptime = timestamp

        override
        fun isCoveringLens(): Boolean {
            return red > 0 && green > 0 && blue > 0
        }
    }

    data class HRTuple(var hr: Double, var confidence: Double)