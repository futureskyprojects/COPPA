package me.vistark.coppa.ui.profile_update

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import de.hdodenhof.circleimageview.CircleImageView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_profile_update.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.vistark.coppa.R
import me.vistark.coppa._core.api.APIService
import me.vistark.coppa._core.utils.CorrectURL.coppaCorrectResourcePath
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.application.api.captain_profile.request.UpdateCaptainProfileRequestDTO
import me.vistark.coppa.domain.entity.languages.CoppaTrans.Companion.syncLanguage
import me.vistark.coppa.ui.auth.AuthActivity
import me.vistark.fastdroid.core.api.JwtAuth.clearAuthentication
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.ui.components.languages.more_languages.MoreLangueExtensionForRecyclerView.bindMoreLanguage
import me.vistark.fastdroid.ui.overlay.LoadingBase.showLoadingBase
import me.vistark.fastdroid.utils.AnimationUtils.fadeIn
import me.vistark.fastdroid.utils.AnimationUtils.fadeOut
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownCenter
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpCenter
import me.vistark.fastdroid.utils.DateTimeUtils.Companion.format
import me.vistark.fastdroid.utils.EdittextUtils.required
import me.vistark.fastdroid.utils.EdittextUtils.validate
import me.vistark.fastdroid.utils.GlideUtils.load
import me.vistark.fastdroid.utils.InternetUtils.isInternetAvailable
import me.vistark.fastdroid.utils.MultipleLanguage.L
import me.vistark.fastdroid.utils.UriUtils.saveImage
import me.vistark.fastdroid.utils.ViewExtension.bindDatePicker
import me.vistark.fastdroid.utils.ViewExtension.bindPopupMenu
import me.vistark.fastdroid.utils.ViewExtension.onTap
import me.vistark.fastdroid.utils.ViewExtension.onTextChanged
import me.vistark.fastdroid.utils.storage.FastdroidFileUtils.CONTENT_ROOT
import me.vistark.fastdroid.utils.storage.FastdroidFileUtils.TEMP_DIR
import me.vistark.fastdroid.utils.storage.FastdroidFileUtils.correctPath
import me.vistark.fastdroid.utils.storage.FastdroidFileUtils.deleteOnExists
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import me.vistark.fastdroid.utils.Retrofit2Extension.Companion.await
import java.io.File
import java.util.*


class ProfileUpdateActivity : FastdroidActivity(
    R.layout.activity_profile_update,
    isCanAutoTranslate = true
) {

    var pickedAvatarImagePath: String = ""

    val dto = UpdateCaptainProfileRequestDTO(
        RuntimeStorage.CurrentCaptainProfile.captain,
        RuntimeStorage.CurrentCaptainProfile.cultureName,
        RuntimeStorage.CurrentCaptainProfile.duration,
        RuntimeStorage.CurrentCaptainProfile.email,
        RuntimeStorage.CurrentCaptainProfile.fishingLicense,
        RuntimeStorage.CurrentCaptainProfile.image,
        "",
        RuntimeStorage.CurrentCaptainProfile.phone,
        RuntimeStorage.CurrentCaptainProfile.shipowner
    )

    lateinit var fastdroidCircleAvatar: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initEvents()
        setResult(RESULT_CANCELED)

        rlContainer.scaleUpCenter()

        bind()

        initSwitchContainer()

        initForMoreLanguageOptions()
    }

    private fun initForMoreLanguageOptions() {
        moreLanguages.bindMoreLanguage(
            RuntimeStorage.Countries.map { it.flagIcon.coppaCorrectResourcePath() }
                .toTypedArray(),
            RuntimeStorage.Translates.localization.currentCulture.flagIcon.coppaCorrectResourcePath(),
            true
        ) { selected ->
            if (!isInternetAvailable()) {
                Toasty.warning(
                    this,
                    L(getString(R.string.YoutMustConnectToInternetForChangeLanguage)),
                    Toasty.LENGTH_SHORT,
                    true
                ).show()
            } else {
                syncLanguage(
                    cultureName = RuntimeStorage.Countries.firstOrNull { it.flagIcon.coppaCorrectResourcePath() == selected }?.cultureName
                        ?: "",
                    isReload = true
                )
            }
        }
    }

    private fun initSwitchContainer() {
        // Khởi tạo trạng thái chuẩn cho khung nhìn ban đầu
        asIvResetPasswordIcon.visibility = View.VISIBLE
        asIvBackToProfileUpdate.visibility = View.GONE
        srvForUpdatePassword.visibility = View.GONE
        srvForUpdateProfileInfo.visibility = View.VISIBLE

        // Khởi tạo sự kiện switch

        // Khi nhấn nút đổi mật khẩu
        asIvResetPasswordIcon.onTap {
            // Ẩn nút này đi
            asIvResetPasswordIcon.fadeOut()
            // Hiển thị nút quay lại
            asIvBackToProfileUpdate.fadeIn()
            // Ẩn phần cập nhật profile
            srvForUpdateProfileInfo.fadeOut()
            // Hiện chỗ nhập mật khẩu
            srvForUpdatePassword.fadeIn()
        }

        // Khi nhấn nút quay lại cập nhật profile
        asIvBackToProfileUpdate.onTap {
            // Ẩn nút này đi
            asIvResetPasswordIcon.fadeIn()
            // Hiển thị nút quay lại
            asIvBackToProfileUpdate.fadeOut()
            // Ẩn phần cập nhật profile
            srvForUpdateProfileInfo.fadeIn()
            // Hiện chỗ nhập mật khẩu
            srvForUpdatePassword.fadeOut()
        }
    }


    private fun initEvents() {
        rlRoot.setOnClickListener {
            animateFinish()
        }
        asIvBackIcon.onTap {
            animateFinish()
        }

        asBtnSignOut.onTap {
            if (!isInternetAvailable()) {
                Toasty.error(
                    this,
                    L(getString(R.string.YouMustConnectInternetToSignOut)),
                    Toasty.LENGTH_SHORT,
                    false
                )
                    .show()
            } else if (RuntimeStorage.CurrentTripSync != null) {
                Toasty.error(
                    this,
                    L(getString(R.string.YouMustFinishCurrentTripToSignOut)),
                    Toasty.LENGTH_SHORT,
                    false
                )
                    .show()
            } else if (RuntimeStorage.TripSyncs.isNotEmpty()) {
                Toasty.error(
                    this,
                    L(getString(R.string.PleaseWaitForAllTripSyncedToSignOut)),
                    Toasty.LENGTH_SHORT,
                    false
                )
                    .show()
            } else {
                logout()
            }
        }
    }

    private fun animateFinish() {
        rlContainer.scaleDownCenter {
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.ipoCamera)?.title = L(getString(R.string.camera))
        menu?.findItem(R.id.ipoGallery)?.title = L(getString(R.string.gallery))
        return super.onPrepareOptionsMenu(menu)
    }

    private fun loadTempAvatarUri(uri: Uri?) {
        fastdroidCircleAvatar.setImageURI(uri)
        uri?.saveImage(
            this,
            filename = "/snapshot/data/avatar/${UUID.randomUUID()}.jpg"
        )?.apply {
            if (this.isNotEmpty()) {
                pickedAvatarImagePath = this
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bind() {
        fastdroidCircleAvatar = findViewById(R.id.fastdroidCircleAvatar)
        fastdroidCircleAvatar.load(dto.image.coppaCorrectResourcePath(), true)
        fastdroidCircleAvatar.bindPopupMenu(R.menu.image_picker_options) {
            when (it) {
                R.id.ipoCamera -> {
                    takePhotoForUri { uri ->
                        loadTempAvatarUri(uri)
                    }
                    return@bindPopupMenu true
                }
                R.id.ipoGallery -> {
                    pickImageForUri { uri ->
                        loadTempAvatarUri(uri)
                    }
                    return@bindPopupMenu true
                }
                else -> {
                    return@bindPopupMenu false
                }
            }
        }

        aaEmail.setText(dto.email)
        aaEmail.onTextChanged {
            dto.email = it
        }

        aaTvPhone.text = aaTvPhone.text.toString()

        aaPhone.setText(dto.phone)
        aaPhone.onTextChanged {
            dto.phone = it
        }

        aaShipOwner.setText(dto.shipowner)
        aaShipOwner.onTextChanged {
            dto.shipowner = it
        }

        aaCaptain.setText(dto.captain)
        aaCaptain.onTextChanged {
            dto.captain = it
        }

        aaFishLicense.setText(dto.fishingLicense)
        aaFishLicense.onTextChanged {
            dto.fishingLicense = it
        }

        aaDuration.text = dto.duration
        aaDuration.bindDatePicker {
            dto.duration = it.format()
        }

        aaPassword.onTextChanged {
            dto.password = it
        }

        asBtnConfirm.onTap {
            if (!isInternetAvailable()) {
                Toasty.error(
                    this,
                    L(getString(R.string.PleaseConnectToInternetBeforeUpdateYourProfile)),
                    Toasty.LENGTH_SHORT,
                    true
                ).show()
            } else {
                val errors = arrayListOf(
                    aaEmail.required(L(getString(R.string.YouMustInputYourEmail))),
                    aaEmail.validate(
                        "\\S+@\\S+\\.\\S+",
                        L(getString(R.string.EmailIsInvalid))
                    ),
                    aaPhone.required(L(getString(R.string.YouMustInputPhone))),
                    aaPhone.validate(
                        "(\\+\\d{1,3})?[\\s.-]?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s-.]?\\d{4}",
                        L(getString(R.string.YourPhoneIsInvalid))
                    ),
                    aaShipOwner.required(L(getString(R.string.YouMustInputShipOwner))),
                    aaCaptain.required(L(getString(R.string.YoutMustInputCaptain))),
                    aaFishLicense.required(L(getString(R.string.YouMustInputFishLicense))),
                    aaDuration.required(L(getString(R.string.YouMustInputDuration)))
                )
                //region Kiểm tra cho phần nhập mật khẩu
                if (dto.password.isNotEmpty()) {
                    if (aaConfirmPassword.text.isEmpty()) {
                        errors.add(L(getString(R.string.YouMustConfirmPassword)))
                    } else if (aaConfirmPassword.text.toString() != dto.password) {
                        errors.add(getString(R.string.ConfirmPasswordNotCorrect))
                    }
                }
                //endregion
                if (errors.count { it.isNotEmpty() } > 0) {
                    errors.firstOrNull { it.isNotEmpty() }?.apply {
                        Toasty.error(this@ProfileUpdateActivity, this, Toasty.LENGTH_SHORT, true)
                            .show()
                    }
                } else {
                    // Ghi tạm dữ liệu
                    dto.cultureName = "vi"

                    val loading = showLoadingBase()
                    GlobalScope.launch {
                        // Nếu người dùng chọn ảnh để cập nhật
                        if (pickedAvatarImagePath.isNotEmpty()) {
                            // Nếu đã chọn, tiến hành cập nhật lên server
                            // Tạo file body
                            val file = File(pickedAvatarImagePath)
                            val requestFile: RequestBody =
                                RequestBody.create(MediaType.parse("multipart/form-data"), file)
                            val body =
                                MultipartBody.Part.createFormData("image", file.name, requestFile)

                            // Tải lên máy chủ
                            try {
                                val res = APIService().APIs.postUploadImage(body).await()
                                if (res!!.status == 200 && res.result?.path?.isNotEmpty() == true) {
                                    dto.image = res.result!!.path
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                file.name.deleteOnExists()
                            }
                        }
                        try {
                            val successBody =
                                APIService().APIs.postUpdateCaptainProfile(dto).await()
                            if (successBody!!.status == 200) {
                                runOnUiThread {
                                    setResult(RESULT_OK)
                                    Toasty.success(
                                        this@ProfileUpdateActivity,
                                        L(getString(R.string.UpdateYourProfileSuccessful)),
                                        Toasty.LENGTH_SHORT,
                                        true
                                    ).show()
                                }
                            } else {
                                runOnUiThread {
                                    Toasty.error(
                                        this@ProfileUpdateActivity,
                                        successBody.message.first(),
                                        Toasty.LENGTH_SHORT,
                                        true
                                    ).show()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            runOnUiThread {
                                Toasty.error(
                                    this@ProfileUpdateActivity,
                                    L(getString(R.string.UnxptectedError)),
                                    Toasty.LENGTH_SHORT,
                                    true
                                ).show()
                            }
                        } finally {
                            runOnUiThread {
                                loading.dismiss()
                                animateFinish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun logout() {
        val loading = showLoadingBase(getString(R.string.LogingOut))
        Thread {
            RuntimeStorage.clear()
            clearAuthentication()
            // Xóa các foler không cần thiết
            CONTENT_ROOT.correctPath("cache").deleteOnExists()
            CONTENT_ROOT.correctPath("snapshot").deleteOnExists()
            TEMP_DIR.deleteOnExists()

            runOnUiThread {
                loading.dismiss()
                startSingleActivity(AuthActivity::class.java)
            }
        }.start()
    }
}