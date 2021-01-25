package me.vistark.coppa.domain.entity

import com.google.gson.annotations.SerializedName
import me.vistark.coppa._core.api.APIService
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.fastdroid.utils.DateTimeUtils.Companion.format
import me.vistark.fastdroid.utils.ObjectUtils.clone
import java.util.*
import kotlin.collections.ArrayList

data class TripSync(
    @SerializedName("captain_id")
    var captainId: Int = RuntimeStorage.CurrentCaptainProfile.id,
    @SerializedName("culture_name")
    var cultureName: String = RuntimeStorage.CurrentCaptainProfile.cultureName,
    @SerializedName("departure_port")
    var departurePort: Int = 0,
    @SerializedName("departure_time")
    var departureTime: String = "",
    @SerializedName("destination_port")
    var destinationPort: Int = 0,
    @SerializedName("destination_time")
    var destinationTime: String = "",
    @SerializedName("submit_time")
    var submitTime: String = "",
    @SerializedName("hauls")
    var hauls: ArrayList<SpeciesSync> = ArrayList()
) {
    companion object {
        fun createTripSync(seaPortId: Int) {
            val tripSync = TripSync(
                departurePort = seaPortId,
                departureTime = Date().format("yyyy-MM-dd HH:mm:ss")
            )
            RuntimeStorage.CurrentTripSync = tripSync
        }

        fun endTripSync(seaPortId: Int) {
            // Cập nhật thời điểm và cảng cập bến
            val tripSync = RuntimeStorage.CurrentTripSync?.clone()
                ?: throw Exception("Current trip can not be null")
            tripSync.destinationPort = seaPortId
            tripSync.destinationTime = Date().format("yyyy-MM-dd HH:mm:ss")
            RuntimeStorage.CurrentTripSync = tripSync

            // Thêm vào danh sách chờ đồng bộ
            val tripSyncs = ArrayList<TripSync>()
            tripSyncs.addAll(RuntimeStorage.TripSyncs)
            tripSyncs.add(tripSync)
            RuntimeStorage.TripSyncs = tripSyncs.toTypedArray()

            // Xóa chuyến hiện
            RuntimeStorage.CurrentTripSync = null
        }

        fun removeTripSync(tripSync: TripSync) {
            // Clone danh sách
            val tripSyncs = ArrayList<TripSync>()
            tripSyncs.addAll(RuntimeStorage.TripSyncs)
            tripSyncs.remove(tripSync)

            RuntimeStorage.TripSyncs = tripSyncs.toTypedArray()
        }

        fun addSpeciesSync(speciesSync: SpeciesSync) {
            val tripSync = RuntimeStorage.CurrentTripSync?.clone()
                ?: throw Exception("Current trip can not be null")
            tripSync.hauls.add(speciesSync)
            RuntimeStorage.CurrentTripSync = tripSync
        }
    }

    fun updateSubmitTime() {
        submitTime = Date().format("yyyy-MM-dd HH:mm:ss")
    }

    fun toTripLog(): TripLog {
        return TripLog(
            departureTime = departureTime,
            departurePort = departurePort,
            destinationPort = destinationPort,
            destinationTime = destinationTime
        )
    }
}