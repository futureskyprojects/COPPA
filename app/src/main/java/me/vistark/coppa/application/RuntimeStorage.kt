package me.vistark.coppa.application

import me.vistark.coppa.domain.entity.CaptainProfile
import me.vistark.coppa.domain.entity.Species
import me.vistark.coppa.domain.entity.SpeciesCategory
import me.vistark.fastdroid.utils.storage.AppStorageManager

object RuntimeStorage {
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

    var SpeciesCategories: ArrayList<SpeciesCategory>
        get() = AppStorageManager.get("COPPA_SPECIES_CATEGORIES") ?: ArrayList()
        set(value) {
            AppStorageManager.update("COPPA_SPECIES_CATEGORIES", value)
        }

    var Specieses: ArrayList<Species>
        get() = AppStorageManager.get("COPPA_SPECIESES") ?: ArrayList()
        set(value) {
            AppStorageManager.update("COPPA_SPECIESES", value)
        }

}