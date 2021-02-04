package me.vistark.coppa.application

import android.content.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.vistark.coppa._core.api.APIService
import me.vistark.coppa._core.utils.CorrectURL.coppaCorrectResourcePath
import me.vistark.coppa.domain.entity.*
import me.vistark.coppa.domain.entity.languages.CoppaTrans
import me.vistark.fastdroid.utils.GlideUtils.cacheImage
import me.vistark.fastdroid.utils.ObjectUtils.clone
import me.vistark.fastdroid.utils.Retrofit2Extension.Companion.await
import me.vistark.fastdroid.utils.storage.AppStorageManager
import java.lang.Exception

object RuntimeStorage {
    fun clear() {
        val speciesCategories = SpeciesCategories.clone()
        val specieses = Specieses.clone()
        val seaPorts = SeaPorts.clone()
        val tripLogs = TripLogs.clone()

        val countries = Countries.clone()
        val translates = Translates.clone()
        val currentUsername = CurrentUsername
        val savedCulture = SavedCulture

        AppStorageManager.storageSP?.edit()?.clear()?.apply()
        SpeciesCategories = speciesCategories
        Specieses = specieses
        SeaPorts = seaPorts
        TripLogs = tripLogs

        Countries = countries
        Translates = translates
        CurrentUsername = currentUsername
        SavedCulture = savedCulture
    }

    fun Context.cacheImages(onCompleted: (() -> Unit)?) {
        Thread {
            Countries.forEach {
                cacheImage(it.flagIcon.coppaCorrectResourcePath())
            }
            SpeciesCategories.forEach {
                cacheImage(it.image.coppaCorrectResourcePath())
            }
            Specieses.forEach {
                cacheImage(it.image.coppaCorrectResourcePath())
            }
            onCompleted?.invoke()
        }.start()
    }


    fun init(onCompleted: (() -> Unit)? = null) {
        GlobalScope.launch {
            try {
                val apis = APIService().APIs
                // Lấy danh sách các nhóm loài
                val speciesCategory = apis.getSpeciesCategories().await()
                speciesCategory!!.result?.apply {
                    SpeciesCategories = this.toTypedArray()
                }

                // Lấy danh sách các loài
                val specieses = apis.getSpecieses().await()
                specieses!!.result?.apply {
                    Specieses = this.toTypedArray()
                }

                // Lấy danh sách các cảng biển
                val seaPorts = apis.getSeaPorts().await()
                seaPorts!!.result?.apply {
                    SeaPorts = this.toTypedArray()
                }

                // Lấy danh sách lịch sử chuyến đi biển
                val tripLogs = apis.getTripHistories().await()
                tripLogs!!.result?.apply {
                    TripLogs = this.toTypedArray()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                onCompleted?.invoke()
            }
        }
    }

    var Countries: Array<Country>
        get() = AppStorageManager.get("COPPA_COUNTRIES") ?: emptyArray()
        set(value) {
            AppStorageManager.update("COPPA_COUNTRIES", value)
        }

    var Translates: CoppaTrans
        get() = AppStorageManager.get("COPPA_TRANSLATE") ?: CoppaTrans()
        set(value) {
            AppStorageManager.update("COPPA_TRANSLATE", value)
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

    var SavedCulture: String
        get() = AppStorageManager.get("COPPA_SAVED_CULTURE") ?: ""
        set(value) {
            AppStorageManager.update("COPPA_SAVED_CULTURE", value)
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