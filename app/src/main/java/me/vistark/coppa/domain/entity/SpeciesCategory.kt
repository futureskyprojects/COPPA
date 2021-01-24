package me.vistark.coppa.domain.entity


import com.google.gson.annotations.SerializedName

data class SpeciesCategory(
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("deleted_at")
    var deletedAt: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("image")
    var image: String = "",
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("title")
    var title: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = ""
)