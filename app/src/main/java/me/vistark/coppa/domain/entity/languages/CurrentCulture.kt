package me.vistark.coppa.domain.entity.languages


import com.google.gson.annotations.SerializedName

data class CurrentCulture(
    @SerializedName("cultureName")
    var cultureName: String = "",
    @SerializedName("dateTimeFormat")
    var dateTimeFormat: DatetimeFormat = DatetimeFormat(),
    @SerializedName("displayName")
    var displayName: String = "",
    @SerializedName("flag_icon")
    var flagIcon: String = "",
    @SerializedName("isActivated")
    var isActivated: Int = 0,
    @SerializedName("isDefault")
    var isDefault: String = "",
    @SerializedName("isRightToLeft")
    var isRightToLeft: Int = 0,
    @SerializedName("nativeName")
    var nativeName: String = "",
    @SerializedName("twoLetterIsoLanguageName")
    var twoLetterIsoLanguageName: String = ""
)