package org.sagebase.crf.matlab

internal class Matlab {
    
    enum class ConvolutionType {
        FULL,
        SAME
    }

    companion object {

        /**
         * autocorrelation
         */
        internal fun xcorr(x: Array<Double>): Array<Double> {
            val xflip = x.reversedArray()
            return conv(x, xflip)
        }

        /**
         * convolution
         * https://www.mathworks.com/help/matlab/ref/conv.html#bucr92l-2
         */
        internal fun conv(u: Array<Double>, v: Array<Double>, convolutionType: ConvolutionType = ConvolutionType.FULL): Array<Double> {
            return when (convolutionType) {
                ConvolutionType.SAME ->
                    outputConv(u, v, outputLength = u.size)
                ConvolutionType.FULL ->
                    outputConv(u, v, outputLength = -1)
            }
        }

        internal fun outputConv(u: Array<Double>, v: Array<Double>, outputLength: Int): Array<Double> {
            val m = u.size
            val n = v.size
            val range = Array(m + n - 1) { ii -> ii + 1 }
            val output: List<Double> = range.map { k ->
                var sum = 0.0
                for (j in Math.max(1, k + 1 - n)..Math.min(k, m)) {
                    sum += u[j - 1] * v[k - j]
                }
                return@map sum
            }

            return when (outputLength > 0) {
                true -> {
                    val center = Math.floor(output.size.toDouble() / 2.0).toInt()
                    val halfU = Math.floor(outputLength.toDouble() / 2.0).toInt()
                    val start = center - halfU
                    val end = start + outputLength
                    output.subList(start, end).toTypedArray()
                }
                false ->
                    output.toTypedArray()
            }
        }

    }
}