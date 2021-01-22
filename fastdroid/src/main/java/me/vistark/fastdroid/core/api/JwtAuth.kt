package me.vistark.fastdroid.core.api

import me.vistark.fastdroid.utils.storage.AppStorageManager

object JwtAuth {
    var BaseUrl = "https://vistark.me"

    var CurrentToken: String = ""
        get() = AppStorageManager.get("CURRENT_TOKEN") ?: field
        set(value) {
            AppStorageManager.update("CURRENT_TOKEN", value)
            field = value
        }

    var CurrentTokenType: String = "Bearer"
        get() = AppStorageManager.get("CURRENT_TOKEN_TYPE") ?: field
        set(value) {
            AppStorageManager.update("CURRENT_TOKEN_TYPE", value)
            field = value
        }

    var AuthorizationKey: String = "Authorization"
        get() = AppStorageManager.get("AUTHORIZTION_KEY") ?: field
        set(value) {
            AppStorageManager.update("AUTHORIZTION_KEY", value)
            field = value
        }
    var ConnectTimeout = 60L
    var ReadTimeout = 60L
}