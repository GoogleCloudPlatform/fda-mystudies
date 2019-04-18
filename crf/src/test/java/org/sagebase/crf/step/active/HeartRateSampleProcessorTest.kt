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




    // Helper functions

    fun compare(array1: DoubleArray, array2: DoubleArray, accuracy: Double) : Boolean {
        assertEquals(array1.size, array2.size)
        if (array1.size != array2.size) {
            return false
        }
        for (idx in 0 until array1.size) {
            val lhv = array1[idx]
            val rhv = array2[idx]
            assertEquals(lhv, rhv, accuracy)
            if (Math.abs(lhv - rhv) > accuracy) {
                return false
            }
        }
        return true
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