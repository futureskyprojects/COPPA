package me.vistark.coppa.application

import me.vistark.coppa.application.api.captain_profile.response.CaptainProfile
import me.vistark.fastdroid.utils.storage.AppStorageManager

object RuntimeStorage {
    var CurrentUsername: String = ""
        get() = AppStorageManager.get("COPPA_CURRENT_USERNAME") ?: field
        set(value) {
            AppStorageManager.update("COPPA_CURRENT_USERNAME", value)
            field = value
        }

    var CurrentCaptainProfile: CaptainProfile = CaptainProfile()
        get() = AppStorageManager.get("COPPA_CURRENT_CAPTAIN_PROFILE") ?: field
        set(value) {
            AppStorageManager.update("COPPA_CURRENT_CAPTAIN_PROFILE", value)
            field = value
        }
}