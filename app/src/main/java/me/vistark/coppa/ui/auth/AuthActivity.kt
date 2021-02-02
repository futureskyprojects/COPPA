package me.vistark.coppa.ui.auth

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.auth_signin.*
import kotlinx.android.synthetic.main.auth_signup.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.vistark.coppa.R
import me.vistark.coppa._core.api.APIService
import me.vistark.coppa._core.utils.CorrectURL.coppaCorrectResourcePath
import me.vistark.coppa.application.DefaultValue
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.application.RuntimeStorage.CurrentUsername
import me.vistark.coppa.application.api.signin.request.LoginRequestDTO
import me.vistark.coppa.application.api.signup.request.RegisterRequestDTO
import me.vistark.coppa.domain.entity.languages.CoppaTrans.Companion.syncLanguage
import me.vistark.coppa.ui.home.HomeActivity
import me.vistark.coppa.ui.splash_screen.SplashActivity
import me.vistark.fastdroid.core.api.JwtAuth.updateJwtAuth
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.ui.components.languages.more_languages.MoreLangueExtensionForRecyclerView.bindMoreLanguage
import me.vistark.fastdroid.ui.overlay.LoadingBase.showLoadingBase
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownBottomRight
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownTopLeft
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpBottomLeft
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpTopLeft
import me.vistark.fastdroid.utils.DateTimeUtils.Companion.format
import me.vistark.fastdroid.utils.EdittextUtils.required
import me.vistark.fastdroid.utils.EdittextUtils.validate
import me.vistark.fastdroid.utils.InternetUtils.isInternetAvailable
import me.vistark.fastdroid.utils.MultipleLanguage.L
import me.vistark.fastdroid.utils.Retrofit2Extension.Companion.await
import me.vistark.fastdroid.utils.ViewExtension.bindDatePicker
import me.vistark.fastdroid.utils.ViewExtension.hide
import me.vistark.fastdroid.utils.ViewExtension.moveToTop
import me.vistark.fastdroid.utils.ViewExtension.onTap
import me.vistark.fastdroid.utils.ViewExtension.onTextChanged
import me.vistark.fastdroid.utils.ViewExtension.show
import me.vistark.fastdroid.utils.keyboard.HideKeyboardExtension.Companion.HideKeyboard


class AuthActivity : FastdroidActivity(
    R.layout.activity_auth,
    isCanAutoTranslate = true,
    windowBackground = R.drawable.bg_window_45
) {
    // Dto cho phần đăng ký
    val registerRequestDTO = RegisterRequestDTO()
    val loginRequestDTO = LoginRequestDTO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        aaTvAlertDanger.hide()

        initDefaultData()

        switchManager()

        bindDtoForRegister()

        bindDtoForLogin()

        initMoreLanguages()

    }

    private fun initMoreLanguages() {
        moreLanguages.bindMoreLanguage(RuntimeStorage.Countries.map { it.flagIcon.coppaCorrectResourcePath() }
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

    private fun initDefaultData() {
        asiUsername.setText(CurrentUsername)
        loginRequestDTO.username = CurrentUsername
    }

    //region Quản lý hiệu ứng chuyển đổi
    fun switchManager() {
        asuBtnSignIn.onTap {
            aaTvAlertDanger.hide()
            aaTvActionLabel.scaleDownTopLeft(150) {
                aaTvActionLabel.scaleUpTopLeft(150) {
                    aaTvActionLabel.text = L(getString(R.string.Login))
                }
            }
            asiScrvSignIn.scaleUpBottomLeft {
                asiScrvSignIn.moveToTop()
            }
            asuScrvSignUp.scaleDownBottomRight()
        }

        asiBtnSignUp.onTap {
            aaTvAlertDanger.hide()
            aaTvActionLabel.scaleDownTopLeft(150) {
                aaTvActionLabel.scaleUpTopLeft(150) {
                    aaTvActionLabel.text = L(getString(R.string.Register))
                }
            }
            asiScrvSignIn.scaleDownBottomRight()
            asuScrvSignUp.scaleUpBottomLeft {
                asuScrvSignUp.moveToTop()
            }
        }
    }
    //endregion

    //region Bind và xử lý
    private fun bindDtoForLogin() {
        if (!isInternetAvailable()) {
            Toasty.error(
                this,
                L(getString(R.string.YouMustConnectToInternetForSignIn)),
                Toasty.LENGTH_SHORT,
                true
            ).show()
            return
        }

        asiUsername.onTextChanged {
            aaTvAlertDanger.hide()
            loginRequestDTO.username = it
        }

        asiPassword.onTextChanged {
            aaTvAlertDanger.hide()
            loginRequestDTO.password = it
        }

        asiBtnSignIn.onTap {
            val errors = arrayListOf(
                asiUsername.required(L(getString(R.string.YouMustInputYourUserName))),
                asiPassword.required(L(getString(R.string.YouMustInputYourPassword)))
            )
            if (errors.count { it.isNotEmpty() } > 0) {
                val err = errors.firstOrNull { it.isNotEmpty() }
                aaTvAlertDanger.text = err
                aaTvAlertDanger.show()
            } else {
                loginProcessing()
            }
        }
    }

    fun clearAllEdtForcus() {
        asUsername.clearFocus()
        aaEmail.clearFocus()
        aaPhone.clearFocus()
        aaShipOwner.clearFocus()
        aaCaptain.clearFocus()
        aaFishLicense.clearFocus()
        aaVesselRegistration.clearFocus()
        aaDuration.clearFocus()
    }

    private fun bindDtoForRegister() {
        asuScrvSignUp.viewTreeObserver.addOnScrollChangedListener {
            clearAllEdtForcus()
        }
        defaultPasswordShow.text = String.format(
            getString(R.string.yourdefaultpasswordwas____),
            DefaultValue.DefaultPassword
        )
        asUsername.onTextChanged {
            aaTvAlertDanger.hide()
            registerRequestDTO.username = it
            asiUsername.setText(it)
        }

        aaEmail.onTextChanged {
            aaTvAlertDanger.hide()
            registerRequestDTO.email = it
        }

//        aaTvPhone.text = aaTvPhone.text.toString()

        aaPhone.onTextChanged {
            aaTvAlertDanger.hide()
            registerRequestDTO.phone = it
        }

        aaShipOwner.onTextChanged {
            aaTvAlertDanger.hide()
            registerRequestDTO.shipowner = it
        }


        aaCaptain.onTextChanged {
            aaTvAlertDanger.hide()
            registerRequestDTO.captain = it
        }


        aaFishLicense.onTextChanged {
            registerRequestDTO.fishingLicense = it
        }

        aaVesselRegistration.onTextChanged {
            aaTvAlertDanger.hide()
            registerRequestDTO.vesselRegistration = it
        }

        aaDuration.bindDatePicker {
            aaTvAlertDanger.hide()
            registerRequestDTO.duration = it.format()
        }

        asuBtnSignUp.onTap {
            if (!isInternetAvailable()) {
                Toasty.error(
                    this,
                    L(getString(R.string.YouMustConnectToInternetForRegister)),
                    Toasty.LENGTH_SHORT,
                    true
                ).show()
                return@onTap
            }

            val errors = arrayListOf(
                asUsername.required(L(getString(R.string.YouMustInputYourUserName))),
                asUsername.validate(
                    "^(?=.{4,64}\$)(?:[a-zA-Z\\d]+(?:(?:|_)[a-zA-Z\\d])*)+\$",
                    L(getString(R.string.UsernameMustBeazAZ09_AndHaveLengthFrom4To64))
                ),
                aaEmail.required(L(getString(R.string.YouMustInputYourEmail))),
                aaEmail.validate("\\S+@\\S+\\.\\S+", L(getString(R.string.EmailIsInvalid))),
                aaPhone.required(L(getString(R.string.YouMustInputPhone))),
                aaPhone.validate(
                    "(\\+\\d{1,3})?[\\s.-]?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s-.]?\\d{4}",
                    L(getString(R.string.YourPhoneIsInvalid))
                ),
                aaShipOwner.required(L(getString(R.string.YouMustInputShipOwner))),
                aaCaptain.required(L(getString(R.string.YoutMustInputCaptain))),
                aaFishLicense.required(L(getString(R.string.YouMustInputFishLicense))),
                aaVesselRegistration.required(L(getString(R.string.YouMustInputVesselRegistration))),
                aaDuration.required(L(getString(R.string.YouMustInputDuration)))
            )
            if (errors.count { it.isNotEmpty() } > 0) {
                val err = errors.firstOrNull { it.isNotEmpty() }
                aaTvAlertDanger.text = err
                aaTvAlertDanger.show()
            } else {
                // Ghi tạm dữ liệu
                registerRequestDTO.image = ""
                registerRequestDTO.cultureName = RuntimeStorage.SavedCulture
                registerRequestDTO.password = DefaultValue.DefaultPassword

                val loading = showLoadingBase()
                GlobalScope.launch {
                    try {
                        val successBody = APIService().APIs.postRegister(registerRequestDTO).await()
                        if (successBody?.status == 200 && successBody.result != null) {
                            // Cập nhật thông tin xác thực
                            updateJwtAuth(
                                successBody.result!!.token.original.accessToken,
                                successBody.result!!.token.original.tokenType
                            )
                            // Thông báo thành công
                            if (successBody.message.isNotEmpty()) {
                                CurrentUsername = loginRequestDTO.username
                                runOnUiThread {
                                    Toasty.success(
                                        this@AuthActivity.applicationContext,
                                        successBody.message.first(),
                                        Toasty.LENGTH_SHORT,
                                        true
                                    ).show()
                                }
                            }
                            startFoSync()
                        } else {
                            if (successBody!!.message.isNotEmpty()) {
                                aaTvAlertDanger.post {
                                    aaTvAlertDanger.text = successBody.message.first()
                                    aaTvAlertDanger.show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        aaTvAlertDanger.post {
                            aaTvAlertDanger.text =
                                L(getString(R.string.RegisterFaild_UnexpectedError))
                        }
                    } finally {
                        runOnUiThread {
                            loading.dismiss()
                        }
                    }
                }
            }
        }
    }
    //endregion

    private fun loginProcessing() {
        val loading = showLoadingBase()
        GlobalScope.launch {
            try {
                val res = APIService().APIs.postLogin(loginRequestDTO).await()
                if (res?.status == 200 && res.result != null) {
                    // Cập nhật thông tin xác thực
                    updateJwtAuth(
                        res.result!!.original.accessToken,
                        res.result!!.original.tokenType
                    )
                    // Thông báo thành công
                    if (res.message.isNotEmpty()) {
                        CurrentUsername = loginRequestDTO.username
                        runOnUiThread {
                            Toasty.success(
                                this@AuthActivity.applicationContext,
                                res.message.first(),
                                Toasty.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }
                    startFoSync()
                } else {
                    if (res!!.message.isNotEmpty()) {
                        aaTvAlertDanger.post {
                            aaTvAlertDanger.text = res.message.first()
                            aaTvAlertDanger.show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                aaTvAlertDanger.post {
                    aaTvAlertDanger.text = L(getString(R.string.LoginFaild_UnexpectedError))
                    aaTvAlertDanger.show()
                }
            } finally {
                runOnUiThread {
                    loading.dismiss()
                }
            }
        }
    }

    private fun startFoSync() {
        startSingleActivity(SplashActivity::class.java)
    }
}