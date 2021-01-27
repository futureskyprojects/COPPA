package me.vistark.coppa.domain.entity.languages


import com.google.gson.annotations.SerializedName

data class DatetimeFormat(
    @SerializedName("calendarAlgorithmType")
    var calendarAlgorithmType: String = "",
    @SerializedName("dateSeparator")
    var dateSeparator: String = "",
    @SerializedName("dateTimeFormatLong")
    var dateTimeFormatLong: String = "",
    @SerializedName("fullDateTimePattern")
    var fullDateTimePattern: String = "",
    @SerializedName("longTimePattern")
    var longTimePattern: String = "",
    @SerializedName("shortDatePattern")
    var shortDatePattern: String = "",
    @SerializedName("shortTimePattern")
    var shortTimePattern: String = ""
)