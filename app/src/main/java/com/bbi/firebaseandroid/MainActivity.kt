package com.bbi.firebaseandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    val FCM_PROJECT_SENDER_ID = "431269160141"
    val FCM_SERVER_CONNECTION = "@gcm.googleapis.com"
    val BACKEND_ACTION_MESSAGE = "MESSAGE"
    val BACKEND_ACTION_ECHO = "ECHO"

    lateinit var mFunctions: FirebaseFunctions
    private lateinit var dataTitle: String
    private lateinit var id: String
    private lateinit var dataMessage: String

    // ...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (intent.extras != null) {
            for (key in intent.extras!!.keySet()) {
                if (key == "title") {
                    dataTitle = intent.extras!!.get(key) as String
                    print("dataTitle="+dataTitle)
                }
                if (key == "message") {
                    dataMessage = intent.extras!!.get(key) as String
                    print("dataMessage="+dataMessage)
                }
                if (key == "id") {
                    id = intent.extras!!.get(key) as String
                    print("id="+id)
                }
            }
            // showAlertDialog()
        }

        FirebaseMessaging.getInstance().setAutoInitEnabled(true)
        FirebaseMessaging.getInstance().subscribeToTopic("notifications");
        mFunctions = FirebaseFunctions.getInstance();


        btnCloudFunction.setOnClickListener {

            addMessage("Ankit noob.")

        }

        btnNotification.setOnClickListener {


            FirebaseMessaging.getInstance().send(RemoteMessage.Builder(FCM_PROJECT_SENDER_ID + FCM_SERVER_CONNECTION)
                    .setMessageId(Integer.toString(1234))
                    .addData("message", "Hi nooob")
                    .addData("action", BACKEND_ACTION_ECHO)
                    .build())
        }
    }


    private fun addMessage(text: String): Task<String> {
        // Create the arguments to the callable function, which is just one string
        val data = HashMap<String, String>()
        data.put("text", text)

        return mFunctions
                .getHttpsCallable("addMessage")
                .call(data)
                .continueWith(object : Continuation<HttpsCallableResult, String> {
                    @Throws(Exception::class)
                    override fun then(task: Task<HttpsCallableResult>): String {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        return task.result.data as String
                    }
                })
    }
}
