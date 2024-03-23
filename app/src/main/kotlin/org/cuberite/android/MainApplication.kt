package org.cuberite.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Parcelable
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import java.io.Serializable

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Application theme
        val preferences = getSharedPreferences(this.packageName, MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(preferences.getInt("defaultTheme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM))
        DynamicColors.applyToActivitiesIfAvailable(this)

        // Notification channel
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val channelId = "cuberiteservice"
        val name = getString(R.string.app_name)
        val channel = NotificationChannel(
                channelId,
                name,
                NotificationManager.IMPORTANCE_HIGH
        )
        channel.setSound(null, null)
        channel.setVibrationPattern(longArrayOf(0))
        channel.enableVibration(true)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= TIRAMISU -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("deprecation") getParcelableExtra(key) as? T
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("deprecation") getSerializableExtra(key) as? T
}