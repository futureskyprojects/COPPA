package me.vistark.coppa.ui.auth

import android.os.Bundle
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.auth_signin.*
import kotlinx.android.synthetic.main.auth_signup.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.vistark.coppa.R
import me.vistark.coppa.application.DefaultValue
import me.vistark.coppa.application.api.request_body.sign_up.RegisterRequestDTO
import me.vistark.coppa.application.api.response_body.sign_up.ErrorResponse
import me.vistark.coppa.core.api.APIService
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.ui.overlay.LoadingBase.showLoadingBase
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownBottomRight
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownTopLeft
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpBottomLeft
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpTopLeft
import me.vistark.fastdroid.utils.DateTimeUtils.Companion.format
import me.vistark.fastdroid.utils.EdittextUtils.required
import me.vistark.fastdroid.utils.EdittextUtils.validate
import me.vistark.fastdroid.utils.ViewExtension.bindDatePicker
import me.vistark.fastdroid.utils.ViewExtension.hide
import me.vistark.fastdroid.utils.ViewExtension.moveToTop
import me.vistark.fastdroid.utils.ViewExtension.onTap
import me.vistark.fastdroid.utils.ViewExtension.onTextChanged
import me.vistark.fastdroid.utils.ViewExtension.show
import retrofit2.HttpException
import retrofit2.await
import java.lang.Exception


class AuthActivity : FastdroidActivity(R.layout.activity_auth) {
    // Dto cho phần đăng ký
    val registerRequestDTO = RegisterRequestDTO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        switchManager()

        bindDtoForRegister()

    }

    //region Quản lý hiệu ứng chuyển đổi
    fun switchManager() {
        asuBtnSignIn.onTap {
            aaTvActionLabel.scaleDownTopLeft(150) {
                aaTvActionLabel.scaleUpTopLeft(150) {
                    aaTvActionLabel.text = "Login"
                }
            }
            asiScrvSignIn.scaleUpBottomLeft {
                asiScrvSignIn.moveToTop()
            }
            asuScrvSignUp.scaleDownBottomRight()
        }

        asiBtnSignUp.onTap {
            aaTvActionLabel.scaleDownTopLeft(150) {
                aaTvActionLabel.scaleUpTopLeft(150) {
                    aaTvActionLabel.text = "Register"
                }
            }
            asiScrvSignIn.scaleDownBottomRight()
            asuScrvSignUp.scaleUpBottomLeft {
                asuScrvSignUp.moveToTop()
            }
        }
    }
    //endregion

    private fun bindDtoForRegister() {
        asUsername.onTextChanged {
            aaTvAlertDanger.hide()
            registerRequestDTO.username = it
        }

        aaEmail.onTextChanged {
            aaTvAlertDanger.hide()
            registerRequestDTO.email = it
        }

        aaTvPhone.text = aaTvPhone.text.toString() + " (Ex: +84123456789)"

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
            val errors = arrayListOf(
                asUsername.required(),
                asUsername.validate("^(?=.{4,64}\$)(?:[a-zA-Z\\d]+(?:(?:|_)[a-zA-Z\\d])*)+\$"),
                aaEmail.required(),
                aaEmail.validate("\\S+@\\S+\\.\\S+"),
                aaPhone.required(),
                aaPhone.validate("(\\+\\d{1})?[\\s.-]?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s-.]?\\d{4}"),
                aaShipOwner.required(),
                aaCaptain.required(),
                aaFishLicense.required(),
                aaVesselRegistration.required(),
                aaDuration.required()
            )
            if (errors.count { it.isNotEmpty() } > 0) {
                val err = errors.firstOrNull { it.isNotEmpty() }
                aaTvAlertDanger.text = err
                aaTvAlertDanger.show()
            } else {
                // Ghi tạm dữ liệu
                registerRequestDTO.image = ""
                registerRequestDTO.cultureName = "vi"
                registerRequestDTO.password = DefaultValue.DefaultPassword

                val loading = showLoadingBase()
                GlobalScope.launch {
                    try {
                        val successBody = APIService().APIs.postRegister(registerRequestDTO).await()
                        println(Gson().toJson(successBody))

                    } catch (he: HttpException) {
                        println(Gson().toJson(he.response()?.raw()?.body()?.string()))
                        he.printStackTrace()
                        aaTvAlertDanger.post {
                            aaTvAlertDanger.text = ErrorResponse(he).extractMessage()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        aaTvAlertDanger.post {
                            aaTvAlertDanger.text = "RegisterFaildUnexpectedError"
                        }
                    }
                    runOnUiThread {
                        loading.dismiss()
                    }
                }
            }
        }
    }
}