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

    fun testData(): ProcessorTestData {

        val json = this.javaClass.classLoader.getResource("io_examples.json").readText()
        val gson = Gson()
        val testData = gson.fromJson(json, ProcessorTestData::class.java)
        return testData
    }

    data class ProcessorTestData(
        val input : Array<Double>,
        val lowpass : Array<Double>,
        val highpass : Array<Double>,
        val mcfilter : Array<Double>,
        val acf : Array<Double>,
        val b_lowpass : Array<Double>,
        val a_lowpass : Array<Double>,
        val b_highpass : Array<Double>,
        val a_highpass : Array<Double>,
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



}