package me.vistark.coppa.domain.entity


import com.google.gson.annotations.SerializedName

data class SeaPort(
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("culture_name")
    var cultureName: String = "",
    @SerializedName("deleted_at")
    var deletedAt: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("symbol")
    var symbol: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = ""
)