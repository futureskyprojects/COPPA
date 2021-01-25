package me.vistark.fastdroid.services

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

abstract class FastdroidService(
    var notiTitle: String,
    var notiIcon: Int,
    var notiText: String = "",
    val notiId: String = "Settings",
    val notiName: String = "Cài đặt",
    val mNotificationId: Int = 140398,
    val startType: Int = START_STICKY,
    var isAutoCancel: Boolean = false
) : Service() {
    lateinit var pendingIntent: PendingIntent
    val notiImportant: Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_DEFAULT
        } else {
            3
        }

    companion object {
        fun Context.isServiceRunning(serviceName: String): Boolean {
            val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceName == service.service.className) {
                    return true
                }
            }
            return false
        }
    }

    var PREVIOUS_STATE = ""

    open fun tasks() {
        throw Exception("You must implement to service know what task will running in background!")
    }

    var notiManager: NotificationManager? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notiManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // B. Tạo pendingIntent cho notify
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        createNotification()

        // Khai báo task
        tasks()
        return startType
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    notiId,
                    notiName,
                    notiImportant
                ).apply {
                    setShowBadge(true)
                }
            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            notiManager?.createNotificationChannel(channel)
        }

        updateDefault()
    }

    fun updateDefault() {
        if (PREVIOUS_STATE == "DEFAULT")
            return
        PREVIOUS_STATE = "DEFAULT"
        // c. Hiển thị noti và chạy services ngầm
        val notification: Notification = NotificationCompat.Builder(this, notiId)
            .setContentTitle(notiTitle)
            .setContentText(notiText)
            .setSmallIcon(notiIcon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(isAutoCancel)
            .build()

        notiManager?.notify(mNotificationId, notification)

        // C. Tiến hành chạy Forefround (chạy dưới nền)
        startForeground(mNotificationId, notification)
    }

    fun updateProgress(message: String = notiText, progress: Int) {
        PREVIOUS_STATE = "PROGRESS"
        // c. Hiển thị noti và chạy services ngầm
        val notification: Notification = NotificationCompat.Builder(this, notiId)
            .setContentTitle(notiTitle)
            .setContentText(message)
            .setSmallIcon(notiIcon)
            .setProgress(100, progress, false)
            .setContentIntent(pendingIntent)
            .setAutoCancel(isAutoCancel)
            .build()
        notiManager?.notify(mNotificationId, notification)
    }

    fun updateLoading(message: String = notiText) {
        if (PREVIOUS_STATE == "LOADING")
            return
        PREVIOUS_STATE = "LOADING"
        // c. Hiển thị noti và chạy services ngầm
        val notification: Notification = NotificationCompat.Builder(this, notiId)
            .setContentTitle(notiTitle)
            .setContentText(message)
            .setSmallIcon(notiIcon)
            .setProgress(100, 30, true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(isAutoCancel)
            .build()
        notiManager?.notify(mNotificationId, notification)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}