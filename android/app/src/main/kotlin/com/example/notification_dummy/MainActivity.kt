package com.example.notification_dummy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.util.ArrayList
import java.util.HashMap

class MainActivity : FlutterActivity() {
    private val CHANNEL = "somethinguniqueforyou.com/channel_test"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->

            if (call.method == "createNotificationChannel") {
                val argData = call.arguments as java.util.HashMap<String, String>
                val completed = createNotificationChannel(argData)
                if (completed == true) {
                    result.success(completed)
                } else {
                    result.error("Error Code", "Error Message", null)
                }
            } else if (call.method == "showNotification") {
                val title = call.argument<String>("title")
                val body = call.argument<String>("body")
                val actions = call.argument<ArrayList<String>>("actions")
                showNotification(title, body, actions)
                result.success(null)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun showNotification(title: String?, body: String?, actions: ArrayList<String>?) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "Your_channel_id"
        val channelName = "Your_channel_name"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
//        val intent = Intent(MainActivity::class.java, MyReceiver::class.java).apply {
//            putExtra("MESSAGE", "Clicked!")
//        }
//        val flag =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                PendingIntent.FLAG_IMMUTABLE
//            else
//                0
//        val pendingIntent = PendingIntent.getBroadcast(
//            MainActivity::class.java,
//            0,
//            intent,
//            flag
//        )
//
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.launch_background)
        actions?.let {
            for ((index, action) in actions.withIndex()) {
                val actionIntent = Intent(this, MyReceiver::class.java).apply {
                    putExtra("MESSAGE", "Clicked!")
                }
                actionIntent.action = "ACTION_$index"
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    actionIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                notification.addAction(android.R.drawable.ic_menu_view, action, pendingIntent)
            }
        }
        notificationManager.notify(0, notification.build())
    }


    private fun createNotificationChannel(mapData: HashMap<String, String>): Boolean {
        val completed: Boolean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val id = mapData["id"]
            val name = mapData["name"]
            val descriptionText = mapData["description"]
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(id, name, importance)
            mChannel.description = descriptionText

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
            completed = true
        } else {
            completed = false
        }
        return completed
    }

}
