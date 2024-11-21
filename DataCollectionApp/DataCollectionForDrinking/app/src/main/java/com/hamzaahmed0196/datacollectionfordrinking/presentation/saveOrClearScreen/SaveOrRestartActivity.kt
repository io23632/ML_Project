package com.hamzaahmed0196.datacollectionfordrinking.presentation.saveOrClearScreen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.hamzaahmed0196.datacollectionfordrinking.R
import com.hamzaahmed0196.datacollectionfordrinking.presentation.MainActivity
import com.hamzaahmed0196.datacollectionfordrinking.presentation.usefulFunctions.UsefulFunctions
import com.hamzaahmed0196.datacollectionfordrinking.presentation.postRequest.HTTPRequestManagement
import com.hamzaahmed0196.datacollectionfordrinking.presentation.watchInputMethod.ActivitySelectionScreen
import java.io.File
import java.io.FileOutputStream

class SaveOrRestartActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferences
    private var Tag : String = "Save or Clear Data"
    private var dataFileName : String = "Data.csv"
    private val usefulFunctions = UsefulFunctions()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_save_or_restart)

        sharedPrefs = getSharedPreferences("accelerometerData", Context.MODE_PRIVATE)
        val accelData : List<String> = usefulFunctions.retrieveAccelData(this@SaveOrRestartActivity)
        Log.d(Tag, "From SaveOrRestart Screen: $accelData")


        val hTTPButton: Button = findViewById(R.id.button_HTTP)
        val restartButton: Button = findViewById(R.id.button_Restart)
        val clearAllDataButton: Button = findViewById(R.id.button_clearAll)

        // Handle save Button:
        hTTPButton.setOnClickListener {
            sendHTTP()
        }

        // Handle ReStart Button
        restartButton.setOnClickListener {
            restartSession()
            val intent = Intent(this, ActivitySelectionScreen::class.java)
            startActivity(intent)
        }

        // Handle ClearAll Button
        clearAllDataButton.setOnClickListener {
            clearAllData()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }


    private fun sendHTTP() {
        val httpRequestManagement = HTTPRequestManagement(this)
        httpRequestManagement.sendDataToDataBase()
        // Clear SharedPreferences:
        //TODO: Write Test Case to Ensure SharedPrefrences is empty after HTTP button is pressed
        sharedPrefs.edit().remove("accelerometerData").apply()
        // Go to the start of the main activity screen. Data has been saved.
        val intent = Intent(this, MainActivity::class.java)
        Toast.makeText(this, "HTTP Request Sent", Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }

    private fun restartSession() {
        // clear the Shared Preferences:
        sharedPrefs.edit().remove("accelerometerData").apply()

        val externalDir : File? = getExternalFilesDir(null)

        // Get File Contents:
        if (externalDir !=null) {

            val file = File(externalDir, dataFileName)

            if (file.exists()) {
                // read the file contents:
                val fileContents = file.readText()
                val dataSessions = fileContents.split("\n").filter { it.isNotEmpty() }
                if (dataSessions.isNotEmpty()) {
                    //TODO: Calculate Sampling Frequency and number of samples collected per session and export from CollectAcclerometerData, and use that number in the dropLast method
                    val updatedContents = dataSessions.dropLast(1).joinToString("\n")

                    val fileOutputStream = FileOutputStream(file, false)
                    fileOutputStream.write((updatedContents).toByteArray())
                    fileOutputStream.close()

                    Toast.makeText(this, "Last Data Session Cleared", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Data File is empty, nothing to clear", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "File: $file not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d(Tag, "External dir : ${externalDir} is not found")
        }
    }


    // TODO: Should ask the user to confirm that all data will be cleared.
    private fun clearAllData() {
        // Go to the Main Activity Screen. All data has been cleared.
        // Clear Shared Pref:
        sharedPrefs.edit().remove("accelerometerData").apply()

        val externalDir : File? = getExternalFilesDir(null)

        // Clear File Contents:
        if (externalDir != null) {
            val file = File(externalDir, dataFileName)
            if (file.exists()) {
                val fileOutputStream = FileOutputStream(file, false) // false overwrited the file
                // write empty strings to file
                fileOutputStream.write(("").toByteArray())
                fileOutputStream.close()
            } else {
                Toast.makeText(this, "File: $file not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d(Tag, "External dir : ${externalDir} is not found")
        }

    }

}