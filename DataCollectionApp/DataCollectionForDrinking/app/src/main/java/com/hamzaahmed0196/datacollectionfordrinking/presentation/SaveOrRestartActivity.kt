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

class SaveOrRestartActivity : AppCompatActivity() {
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_save_or_restart)

        sharedPrefs = applicationContext.getSharedPreferences("accelerometerData", Context.MODE_PRIVATE)


        val saveButton: Button = findViewById(R.id.button_Save)
        val restartButton: Button = findViewById(R.id.button_Restart)
        val intent = Intent(this, MainActivity::class.java)
        // Handle save Button:
        saveButton.setOnClickListener {
            Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        // Clear data is not saving
        restartButton.setOnClickListener {
            clearSavedData()
            startActivity(intent)
        }

    }

    private fun clearSavedData() {
        sharedPrefs.edit().remove("AccelerometerData").apply()
        Toast.makeText(this, "Data cleared for new collection", Toast.LENGTH_SHORT).show()
    }

}