package me.vistark.coppa.domain.entity.languages

import androidx.appcompat.app.AlertDialog
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.vistark.coppa._core.api.APIService
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.application.api.trans.request.CoppaTranslateRequest
import me.vistark.fastdroid.core.api.JwtAuth.isAuthenticated
import me.vistark.fastdroid.core.models.FastdroiBaseMap
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.ui.overlay.LoadingBase.showLoadingBase
import me.vistark.fastdroid.utils.InternetUtils.isInternetAvailable
import me.vistark.fastdroid.utils.MultipleLanguage
import me.vistark.fastdroid.utils.Retrofit2Extension.Companion.await

class CoppaTrans(
    @SerializedName("localization")
    var localization: CoppaLocalization = CoppaLocalization(),
    @SerializedName("translates")
    var translates: ArrayList<FastdroiBaseMap> = ArrayList()
) {
    companion object {
        fun FastdroidActivity.syncLanguage(
            isReload: Boolean = false,
            onCompleted: (() -> Unit)? = null
        ) {
            if (!isInternetAvailable()) {
                // Bỏ qua nếu không có mạng
                return
            }
            var loading: AlertDialog? = null
            runOnUiThread {
                loading = showLoadingBase()
            }
            GlobalScope.launch {
                try {
                    val apis = APIService().APIs
                    var cultureName = ""
                    if (isAuthenticated()) {
                        cultureName =
                            RuntimeStorage.Translates.localization.currentCulture.cultureName
                    }

                    if (RuntimeStorage.SavedCulture.isNotEmpty()) {
                        cultureName = RuntimeStorage.SavedCulture
                    }

                    val coppaTrans = if (cultureName.isEmpty()) {
                        apis.getDefaultLanguages().await()
                    } else {
                        println(">>>> [$cultureName]")
                        apis.getLanguages(CoppaTranslateRequest(cultureName))
                            .await()
                    }
                    coppaTrans!!.result?.apply {
                        RuntimeStorage.Translates = this
                        RuntimeStorage.Countries =
                            RuntimeStorage.Translates.localization.languages.toTypedArray()
                        MultipleLanguage.Translates =
                            RuntimeStorage.Translates.translates.toTypedArray()
                        RuntimeStorage.SavedCulture =
                            RuntimeStorage.Translates.localization.currentCulture.cultureName
                        // Khởi động lại màn hình hiện tại
                        if (isReload) {
                            runOnUiThread {
                                reload()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    runOnUiThread {
                        loading?.dismiss()
                        onCompleted?.invoke()
                    }
                }
            }
        }
    }
}