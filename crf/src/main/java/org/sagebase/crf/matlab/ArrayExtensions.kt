package org.sagebase.crf.matlab


internal data class ValueAndIndex(val value: Double, val index: Int)
internal data class MaxAndSplice(val maxValue: Double, val v2: List<Double>)

/**
 * Returns the max value and index of that value.
 */
internal fun Array<Double>.seekMax(): ValueAndIndex {
    val value = this.max()!!
    val index = this.indexOf(value)
    return ValueAndIndex(value, index)
}

/**
 * Replace the elements of the array with the given number of zeros on the left and clipped on the
 * right.
 */
internal fun Array<Double>.zeroReplace(lowerBounds: Int, upperBounds: Int): Array<Double> {
    val dropCount = this.size - upperBounds - 1
    val y = if (dropCount > 0) this.dropLast(dropCount).toTypedArray() else this.copyOf()
    if (lowerBounds >= 0) {
        y.fill(0.0, 0, lowerBounds)
    }
    return y
}

/**
 * Pad the array with zeros before the value.
 */
internal fun Array<Double>.zeroPadBefore(count: Int): Array<Double> {
    val output = MutableList(count) { 0.0 }
    output.addAll(this)
    return output.toTypedArray()
}

/**
 * Pad the array with zeros before the value.
 */
internal fun Array<Double>.zeroPadAfter(count: Int): Array<Double> {
    val output = this.toMutableList()
    output.addAll(Array(count) { 0.0 })
    return output.toTypedArray()
}

/**
 * Return the center of the range minus the ends to endCount.
 */
internal fun Array<Double>.centerSplice(endCount: Int): Array<Double> =
        this.slice((endCount - 1) until (this.size - endCount)).toTypedArray()

/**
 * Return the center of the range minus the ends to endCount.
 */
internal fun Array<Double>.endSplice(fromIndex: Int): Array<Double> =
        this.slice(fromIndex until this.size).toTypedArray()

internal fun Array<Double>.maxSplice(): MaxAndSplice {
    //% To just remove the repeated part of the autocorr function (since it is even)
    val ret = this.seekMax()
    return MaxAndSplice(ret.value, this.endSplice(ret.index).toList())
}