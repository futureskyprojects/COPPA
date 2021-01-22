package me.vistark.coppa.ui.auth

import android.os.Bundle
import android.widget.ScrollView
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.auth_signin.*
import kotlinx.android.synthetic.main.auth_signup.*
import me.vistark.coppa.R
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownBottomRight
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownTopLeft
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpBottomLeft
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpTopLeft
import me.vistark.fastdroid.utils.ViewExtension.moveToTop
import me.vistark.fastdroid.utils.ViewExtension.onTap


class AuthActivity : FastdroidActivity(R.layout.activity_auth) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
}