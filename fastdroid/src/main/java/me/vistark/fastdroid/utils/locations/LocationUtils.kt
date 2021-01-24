package me.vistark.fastdroid.utils.locations

import android.location.Location

object LocationUtils {
    fun LatLngToDMS(latitude: Double, longitude: Double): String {
        val builder = StringBuilder()
        if (latitude < 0) {
            builder.append("S ")
        } else {
            builder.append("N ")
        }
        val latitudeDegrees: String = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS)
        val latitudeSplit = latitudeDegrees.split(":").toTypedArray()
        builder.append(latitudeSplit[0])
        builder.append("°")
        builder.append(latitudeSplit[1])
        builder.append("'")
        builder.append(latitudeSplit[2])
        builder.append("\"")
        builder.append(" ")
        if (longitude < 0) {
            builder.append("W ")
        } else {
            builder.append("E ")
        }
        val longitudeDegrees: String =
            Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS)
        val longitudeSplit = longitudeDegrees.split(":").toTypedArray()
        builder.append(longitudeSplit[0])
        builder.append("°")
        builder.append(longitudeSplit[1])
        builder.append("'")
        builder.append(longitudeSplit[2])
        builder.append("\"")
        return builder.toString()
    }
}