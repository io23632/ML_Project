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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hamzaahmed0196.datacollectionfordrinking.R
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.io.File
import java.io.FileOutputStream

class CollectAccelerometerData : AppCompatActivity(), SensorEventListener {
    
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val accelerometerData = mutableListOf<String>()
    private lateinit var timerTextView: TextView
    private lateinit var circularProgressBar: CircularProgressBar
    private lateinit var sharedPrefs : SharedPreferences
    private lateinit var selectedActivity : String
    private var Tag : String = "CollectAccelData"
    private val file : String = "Data.csv"
    private lateinit var accelData: List<String>
    private val samplingPeriod = 10000000 // Samples one data point every second. Should be 50,000 (for 20 samples per second )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_accelerometer_data)

        // clear the previous accelerometer data :
        accelerometerData.clear()

        // initialise views
        timerTextView = findViewById(R.id.timer_TextView)
        circularProgressBar = findViewById(R.id.circular_ProgressBar)

        // initialise selected Activity from ActivitySelectionScreen
        selectedActivity = intent.getStringExtra("selectedActivity") ?: "UnknownActivity"

        // initialise sensor Manger:
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // initialise SharedPrefrences
        sharedPrefs = getSharedPreferences("accelerometerData", Context.MODE_PRIVATE)
        Log.d(Tag, "Start of Collection")
        Log.d(Tag, selectedActivity)

        // start collecting data when app is launched
        startDataCollection()
    }

    private fun startDataCollection() {
        // register Listener if accelerometer data is not null
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, samplingPeriod) // 20Hz collection frequency is 50,000 microseoncds
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
                //Log.d(Tag, accelerometerData.toString()) // shows data is in accelerometerData mutable list
                accelData = retrieveAccelDate()
                Log.d(Tag, accelData.toString()) // shows data is in sharedPrefrences before it is cleared in navigateToSaveOrRestart() method
                navigateToSaveOrRestart()
            }

        }

            countDownTimer.start()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val timestamp = System.currentTimeMillis()
            val formattedData = "$timestamp, ${event.values[0]}, ${event.values[1]}, ${event.values[2]}, $selectedActivity"
            accelerometerData.add(formattedData)
            val dataAsText = accelerometerData.joinToString("\n")
            val editor = sharedPrefs.edit()
            editor.putString("accelerometerData", dataAsText)
            editor.apply()
        }
    }

    /* Goes to Start or Restart Screen */
    private fun navigateToSaveOrRestart() {
        // Save data to file before clearing SharedPreferences
        writeDataToFile(accelerometerData, file)
        // Clear SharedPreferences for the next session
        val editor = sharedPrefs.edit()
        editor.clear().apply()

        // Navigate to the next screen
        val intent = Intent(this, SaveOrRestartActivity::class.java)
        startActivity(intent)
    }

    //TODO: Data to be formatted in Excel format with headings: User Id, Date, TimeStamp, X, Y, Z, Activity
    private fun writeDataToFile(data: List<String>, fileName: String) {
        try {
            // Convert the data to a single string joining by the end of line
            val dataAsString = data.joinToString(separator = "\n")
            // App-specific external storage, providing a null directory
            val externalDir : File? = getExternalFilesDir(null)
            if (externalDir != null) {
                // File object to represent the external storage
                val externalFile = File(externalDir, fileName)
                val addHeaders = externalFile.length() == 0L

                FileOutputStream(externalFile, true).bufferedWriter().use { writer ->
                    if(addHeaders) {
                        writer.write("TimeStamp,X,Y,Z,Activity\n")
                    }
                    data.forEach { entry -> writer.write("$entry\n") }
                }
                // Log succesful writing:
                Log.d(Tag, "Data Saved to file: $fileName at ${externalFile.absolutePath}")

            } else {
                Log.d(Tag, "External storage is null")
            }
        } catch (e:Exception) {
            e.printStackTrace()
            Log.e(Tag, "Error writing data to file ${e.message}")
        }
    }

    // helper function to retrieve accelerometer data ;
    private fun retrieveAccelDate() : List<String> {
        val dataAsText = sharedPrefs.getString("accelerometerData", null)
        return dataAsText?.split("\n") ?: emptyList()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this@CollectAccelerometerData)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //
    }


}