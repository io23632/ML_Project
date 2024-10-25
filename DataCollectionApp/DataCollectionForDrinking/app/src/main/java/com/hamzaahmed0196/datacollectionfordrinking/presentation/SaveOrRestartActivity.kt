package com.hamzaahmed0196.datacollectionfordrinking.presentation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hamzaahmed0196.datacollectionfordrinking.R
import java.io.File

class SaveOrRestartActivity : AppCompatActivity() {
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_save_or_restart)

        sharedPrefs = applicationContext.getSharedPreferences("accelerometerData", Context.MODE_PRIVATE)

        val saveButton: Button = findViewById(R.id.button_Save)
        val restartButton: Button = findViewById(R.id.button_Restart)
        val clearAllDataButton: Button = findViewById(R.id.button_clearAll)

        // Handle save Button:
        saveButton.setOnClickListener {
            goToStart()
        }

        // Handle ReStart Button
        restartButton.setOnClickListener {

        }

        // Handle ClearAll Button
        clearAllDataButton.setOnClickListener {

        }

    }


    private fun goToStart() {
        // Go to the start of the main activity screen. Data has been saved.
        val intent = Intent(this, MainActivity::class.java)
        Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }

    private fun restartSession() {
        // Go to the Activity Selection Screen. Previous Session has been deleted
    }


    private fun clearAllData() {
        // Go to the Main Activity Screen. All data has been cleared.
        // Should ask the user to confirm that all data will be cleared.

        // Clear Shared Pref:
        sharedPrefs.edit().remove("accelerometerData").apply()

        // Clear File Contents:

        val file =


    }





}