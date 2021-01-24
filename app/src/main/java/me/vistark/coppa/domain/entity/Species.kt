package me.vistark.coppa.domain.entity


import com.google.gson.annotations.SerializedName

data class Species(
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("deleted_at")
    var deletedAt: Any = Any(),
    @SerializedName("family_id")
    var familyId: Int = 0,
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("image")
    var image: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("science_name")
    var scienceName: String = "",
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("symbol")
    var symbol: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = ""
)