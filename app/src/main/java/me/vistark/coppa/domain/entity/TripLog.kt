package me.vistark.coppa.domain.entity


import com.google.gson.annotations.SerializedName

data class TripLog(
    @SerializedName("captain_id")
    var captainId: Int = 0,
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("culture_name")
    var cultureName: String = "",
    @SerializedName("departure_port")
    var departurePort: Int = 0,
    @SerializedName("departure_time")
    var departureTime: String = "",
    @SerializedName("destination_port")
    var destinationPort: Int = 0,
    @SerializedName("destination_time")
    var destinationTime: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("stastus")
    var stastus: Int = 0,
    @SerializedName("updated_at")
    var updatedAt: String = ""
)