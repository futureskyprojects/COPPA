package me.vistark.coppa.application

import me.vistark.coppa.domain.entity.*
import me.vistark.fastdroid.utils.storage.AppStorageManager

object RuntimeStorage {
    fun clear() {
        TripSyncs = emptyArray()
        CurrentTripSync = null
        TripLogs = emptyArray()
        CurrentCaptainProfile = CaptainProfile()
    }

    var TripSyncs: Array<TripSync>
        get() = AppStorageManager.get("COPPA_TRIP_SYNCS") ?: emptyArray()
        set(value) {
            AppStorageManager.update("COPPA_TRIP_SYNCS", value)
        }

    var CurrentTripSync: TripSync?
        get() = AppStorageManager.get("COPPA_CURRENT_TRIP")
        set(value) {
            AppStorageManager.update("COPPA_CURRENT_TRIP", value)
        }

    var TripLogs: Array<TripLog>
        get() = AppStorageManager.get("COPPA_TRIP_LOGS") ?: emptyArray()
        set(value) {
            AppStorageManager.update("COPPA_TRIP_LOGS", value)
        }

    var CurrentUsername: String
        get() = AppStorageManager.get("COPPA_CURRENT_USERNAME") ?: ""
        set(value) {
            AppStorageManager.update("COPPA_CURRENT_USERNAME", value)
        }

    var CurrentCaptainProfile: CaptainProfile
        get() = AppStorageManager.get("COPPA_SAVED_CAPTAIN_PROFILE") ?: CaptainProfile()
        set(value) {
            AppStorageManager.update("COPPA_SAVED_CAPTAIN_PROFILE", value)
        }

    var SpeciesCategories: Array<SpeciesCategory>
        get() = AppStorageManager.get("COPPA_SPECIES_CATEGORIES") ?: emptyArray()
        set(value) {
            AppStorageManager.update("COPPA_SPECIES_CATEGORIES", value)
        }

    var Specieses: Array<Species>
        get() = AppStorageManager.get("COPPA_SPECIESES") ?: emptyArray()
        set(value) {
            AppStorageManager.update("COPPA_SPECIESES", value)
        }

    var SeaPorts: Array<SeaPort>
        get() = AppStorageManager.get("COPPA_SEA_PORTS") ?: emptyArray()
        set(value) {
            AppStorageManager.update("COPPA_SEA_PORTS", value)
        }

}