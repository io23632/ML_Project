package com.hamzaahmed0196.datacollectionfordrinking.presentation.activityModel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hamzaahmed0196.datacollectionfordrinking.R
import com.hamzaahmed0196.datacollectionfordrinking.presentation.postRequest.HTTPRequestManagement
import com.hamzaahmed0196.datacollectionfordrinking.presentation.watchInputMethod.GetUserID

class ConfigureActivitiesList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_configure_activities_list)

        val configure_button : Button = findViewById(R.id.button_configure)

        configure_button.setOnClickListener {
            sendPOSTRequest()
            val intent = Intent(this, GetUserID::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun sendPOSTRequest() {
        val httpRequestManagement = HTTPRequestManagement(this)
        httpRequestManagement.retrieveActivitiesData()
    }
}