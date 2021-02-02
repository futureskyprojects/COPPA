package me.vistark.coppa.domain.entity

import com.google.gson.annotations.SerializedName
import me.vistark.coppa._core.api.APIService
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.fastdroid.utils.DateTimeUtils.Companion.format
import me.vistark.fastdroid.utils.ObjectUtils.clone
import java.util.*
import kotlin.collections.ArrayList

data class TripSync(
    var uuid: String = UUID.randomUUID().toString(),
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
    fun isSyncedAllImages(): Boolean {
        for (h in hauls) {
            val _imgs = h.images.split(",").filter { it.isNotEmpty() && it.isNotBlank() }
            if (_imgs.any { it.contains(SUB_CURRENT_TRIP_FOLDER) })
                return false
        }
        return true
    }

    fun departureTime(): String {
        return departureTime
            .replace("/", "-")
            .replace(":", "")
            .replace(" ", "_")
            .replace("-", "")
    }

    companion object {
        val SUB_CURRENT_TRIP_FOLDER =
            "/snapshot/data/trip_${RuntimeStorage.CurrentTripSync?.departureTime()}"

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

        fun updateTripSyncHauls(speciesSync: SpeciesSync) {
            // Clone danh sách
            val tripSyncs = ArrayList<TripSync>()
            tripSyncs.addAll(RuntimeStorage.TripSyncs)

            // Cập nhật đường dẫn ảnh
            for (i in 0 until tripSyncs.size) {
                for (j in 0 until tripSyncs[i].hauls.size) {
                    if (tripSyncs[i].hauls[j].uuid == speciesSync.uuid) {
                        tripSyncs[i].hauls[j].images = speciesSync.images
                    }
                }
            }

            // Cập nhật vào bộ nhớ
            RuntimeStorage.TripSyncs = tripSyncs.toTypedArray()
        }

        fun removeTripSync(tripSync: TripSync) {
            // Clone danh sách
            val tripSyncs = ArrayList<TripSync>()
            tripSyncs.addAll(RuntimeStorage.TripSyncs.filter { it.uuid != tripSync.uuid })

            RuntimeStorage.TripSyncs = tripSyncs.toTypedArray()
        }

        fun addSpeciesSync(speciesSync: SpeciesSync): Boolean {
            val tripSync = RuntimeStorage.CurrentTripSync?.clone()
                ?: throw Exception("Current trip can not be null")
            tripSync.hauls.add(speciesSync)
            RuntimeStorage.CurrentTripSync = tripSync
            return RuntimeStorage.CurrentTripSync?.hauls?.any { it.uuid == speciesSync.uuid }
                ?: false
        }

        fun removeSpeciesSync(speciesSync: SpeciesSync): Boolean {
            val tripSync = RuntimeStorage.CurrentTripSync?.clone()
                ?: throw Exception("Current trip can not be null")
            tripSync.hauls.remove(speciesSync)
            RuntimeStorage.CurrentTripSync = tripSync
            return RuntimeStorage.CurrentTripSync?.hauls?.all { it.uuid != speciesSync.uuid }
                ?: true
        }

        fun updateSpeciesSync(speciesSync: SpeciesSync) {
            val tripSync = RuntimeStorage.CurrentTripSync?.clone()
                ?: throw Exception("Current trip can not be null")
            for (i in 0 until tripSync.hauls.size) {
                if (tripSync.hauls[i].uuid == speciesSync.uuid) {
                    tripSync.hauls[i] = speciesSync
                }
            }
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