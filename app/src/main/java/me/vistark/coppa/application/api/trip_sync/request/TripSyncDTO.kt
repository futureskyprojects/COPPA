package me.vistark.coppa.application.api.trip_sync.request

import com.google.gson.annotations.SerializedName
import me.vistark.coppa.domain.entity.TripSync

class TripSyncDTO(
    @SerializedName("trip")
    val trip: TripSync
) {
}