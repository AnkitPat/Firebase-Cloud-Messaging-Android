package com.bbi.firebaseandroid

import android.annotation.TargetApi
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues.TAG
import android.content.Context;
import android.content.Intent;
import android.graphics.Color
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by BBIM1040 on 27/03/18.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {


    private lateinit var dataTitle: String

    private lateinit var dataMessage: String
    private lateinit var id: String

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage!!.getFrom())

        // Check if message contains a data payload.
        if (remoteMessage.getData().size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


            dataTitle = remoteMessage.getData().get("title")!!;
            dataMessage = remoteMessage.getData().get("message")!!;
            id = remoteMessage.getData().get("id")!!;
        }
        var notificationTitle: String? = null
        var notificationBody: String? = null
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification()!!.getBody());
            notificationTitle = remoteMessage.getNotification()!!.getTitle();
            notificationBody = remoteMessage.getNotification()!!.getBody();

        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(notificationTitle!!, notificationBody!!, dataTitle, dataMessage);
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
        /* val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
         val myJob = dispatcher.newJobBuilder()
                 .setService(MyJobService::class.java)
                 .setTag("my-job-tag")
                 .build()
         dispatcher.schedule(myJob)*/
        // [END dispatch_job]
    }





    private fun sendNotification(notificationTitle: String, notificationBody: String, dataTitle: String, dataMessage: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("title", dataTitle)
        intent.putExtra("message", dataMessage)
        intent.putExtra("id", id)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent) as NotificationCompat.Builder
        val channelId = getString(R.string.default_notification_channel_id)

        val NOTIFICATION_ID = 234
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


            val CHANNEL_ID = "my_channel_01"
            val name = "notifications"
            val Description = "This is my channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, name, importance)
            mChannel.description = Description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mChannel.setShowBadge(false)

            notificationManager.createNotificationChannel(mChannel)
        }
        notificationManager.notify(NOTIFICATION_ID /* ID of notification */, notificationBuilder.build())
    }




    override fun onMessageSent(p0: String?) {
        super.onMessageSent(p0)
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    override fun onSendError(p0: String?, p1: Exception?) {
        super.onSendError(p0, p1)
    }
}