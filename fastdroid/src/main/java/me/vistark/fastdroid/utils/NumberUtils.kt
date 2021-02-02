package me.vistark.fastdroid.utils

object NumberUtils {
    fun Double.toNumberString(): String {
        if (this == this.toInt().toDouble())
            return this.toInt().toString()
        else
            return this.toString()
    }

    fun Float.toNumberString(): String {
        return this.toDouble().toNumberString()
    }
}