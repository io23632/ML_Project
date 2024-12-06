package com.hamzaahmed0196.datacollectionfordrinking.presentation.watchInputMethod

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hamzaahmed0196.datacollectionfordrinking.R
import com.hamzaahmed0196.datacollectionfordrinking.presentation.activityModel.ActivitySelectionTwo
import com.hamzaahmed0196.datacollectionfordrinking.presentation.collectAccelerometerData.CollectAccelerometerData
//import com.hamzaahmed0196.datacollectionfordrinking.presentation.parseJSONInput.ParseJSON


class GetUserID : AppCompatActivity() {

    private lateinit var tV_EnterUserID : TextView
    private lateinit var tVEdit_getUserID : EditText
    private lateinit var button_submit : Button
    private lateinit var userPrefs : SharedPreferences
    private lateinit var userID : String
    private var Tag: String = "GetUserID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_get_user_id)

        tV_EnterUserID = findViewById(R.id.tV_EnterUserID)
        tVEdit_getUserID = findViewById(R.id.tVEdit_getUserID)!!
        button_submit = findViewById(R.id.button_submit)

        userPrefs = getSharedPreferences("userPreferences", Context.MODE_PRIVATE)

        button_submit.setOnClickListener {
            userID = tVEdit_getUserID.text.toString()
            userPrefs.edit().putString("userID", userID).apply()
            Log.d(Tag, " GetUserID User ID is : $userID")
            val intent = Intent(this, ActivitySelectionTwo::class.java)
            // intent.putExtra("userID", userID)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}



