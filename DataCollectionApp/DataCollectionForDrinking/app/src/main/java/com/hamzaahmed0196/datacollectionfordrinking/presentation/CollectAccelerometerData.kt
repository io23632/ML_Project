package com.hamzaahmed0196.datacollectionfordrinking.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hamzaahmed0196.datacollectionfordrinking.R
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.io.File
import java.io.FileOutputStream
import java.util.*

class CollectAccelerometerData : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val accelerometerData = mutableListOf<String>()
    private lateinit var timerTextView: TextView
    private lateinit var circularProgressBar: CircularProgressBar
    private lateinit var sharedPrefs : SharedPreferences
    // private val Tag : String = "CollectAccelData"
    private var Tag : String = "Initialiase Tag"
    private lateinit var fileOutputStream : FileOutputStream
    val file : String = "drinkingData.txt" // Data is not being saved to the Text File
    private lateinit var accelData: List<String>
    private lateinit var activityNames : ActivitySelectionScreen


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_accelerometer_data)

        // initialise views
        timerTextView = findViewById(R.id.timer_TextView)
        circularProgressBar = findViewById(R.id.circular_ProgressBar)
        // initialise sensor Manger:
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        // initialise SharedPrefrences
        sharedPrefs = getSharedPreferences("AccelerometerData", Context.MODE_PRIVATE)
        Log.d(Tag, "Start of Collection")
        // start collecting data when app is launched
        startDataCollection()
        // show the accelerometer data:
    }

    private fun startDataCollection() {
        // register Listener if accelerometer data is not null
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            startTimer()
        } else {
            Toast.makeText(this, "No Accelerometer data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer(){
        val activityTime = 10000L // 10 seconds
        val countDownTimer = object : CountDownTimer(activityTime, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(milisUntilFinished: Long) {
                val secondsLeft = milisUntilFinished / 1000
                timerTextView.text = "Time remaining: $secondsLeft s"
                // update the circular progress bar
                val progress = ((activityTime - milisUntilFinished).toFloat() / activityTime)*100
                circularProgressBar.setProgressWithAnimation(progress, 1000)
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                // unregister listener:
                sensorManager.unregisterListener(this@CollectAccelerometerData)
                timerTextView.text = "Finished"
                circularProgressBar.setProgressWithAnimation(100f)
                navigateToSaveOrRestart()
            }

        }

            countDownTimer.start()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val timestamp = System.currentTimeMillis()
            val formattedData = "$timestamp" + " X: " + event.values[0] + " Y: " + event.values[1] + " Z: " + event.values[2] + "Label "
            accelerometerData.add(formattedData)
            accelData = retrieveAccelDate()
            Log.d(Tag, accelData.toString())
        }
    }

    /* Goes to Start or Restart Screen */
    private fun navigateToSaveOrRestart() {
        saveDataToPrefs()
        val intent = Intent(this, SaveOrRestartActivity::class.java)
        startActivity(intent)
    }


    private fun saveDataToPrefs() {
        // Convert the accelerometer data list to a single string with each entry on a new line>
        val dataAsText = accelerometerData.joinToString(separator = "\n")
        val editor = sharedPrefs.edit()
        editor.putString("accelerometerData", dataAsText)
        editor.apply()
    }

    // helper function to retrieve accelerometer data ;
    private fun retrieveAccelDate() : List<String> {
        val dataAsText = sharedPrefs.getString("accelerometerData", null)
        return dataAsText?.split("\n") ?: emptyList()
    }

    private fun writeDataToFile(data: List<String>, fileName: String) {
        try {
            // Convert the data to a single string joining by the end of line
            val dataAsString = data.joinToString(separator = "\n")
            // Open the file using fileOutPutStream
            fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
            // Write the file as a ByteArray :
            fileOutputStream.write(dataAsString.toByteArray())
            // close the file
            fileOutputStream.close()
            Log.d(Tag, "Data Saved to file: $fileName")

        } catch (e:Exception) {
            e.printStackTrace()
            Log.e(Tag, "Error writing data to file ${e.message}")
        }

    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this@CollectAccelerometerData)
    }


}