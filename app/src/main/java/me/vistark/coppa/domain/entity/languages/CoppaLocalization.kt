package me.vistark.coppa.domain.entity.languages

import com.google.gson.annotations.SerializedName
import me.vistark.coppa.domain.entity.Country

class CoppaLocalization(
    @SerializedName("languages")
    var languages: ArrayList<Country> = ArrayList(),
    @SerializedName("currentCulture")
    var currentCulture: CurrentCulture = CurrentCulture()
)