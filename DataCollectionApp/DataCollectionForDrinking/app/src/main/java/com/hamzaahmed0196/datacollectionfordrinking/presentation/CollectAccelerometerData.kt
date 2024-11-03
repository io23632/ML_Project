package com.hamzaahmed0196.datacollectionfordrinking.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.DateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hamzaahmed0196.datacollectionfordrinking.R
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.io.File
import java.io.FileOutputStream

class CollectAccelerometerData : AppCompatActivity(), SensorEventListener {
    
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var accelerometerData = mutableListOf<HashMap<String, String>>()
    private lateinit var timerTextView: TextView
    private lateinit var circularProgressBar: CircularProgressBar
    private lateinit var sharedPrefs : SharedPreferences
    private lateinit var selectedActivity : String
    private var Tag : String = "CollectAccelData"
    private val file : String = "Data.csv"
    private lateinit var accelData : List<Map<String, String>>
    private val samplingPeriod = 10000000 // Samples one data point every second. Should be 50,000 (for 20 samples per second )
    private lateinit var deviseId : String
    private lateinit var dateString : String
    private lateinit var timestamp : String
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_accelerometer_data)

        // clear the previous accelerometer data :
        accelerometerData.clear()

        // initialise views
        timerTextView = findViewById(R.id.timer_TextView)
        circularProgressBar = findViewById(R.id.circular_ProgressBar)


        //
        deviseId = Build.ID
        val currentDate = Calendar.getInstance()
        dateString = DateFormat.getDateInstance(DateFormat.LONG).format(currentDate.time)
        timestamp = System.currentTimeMillis().toString()



        // initialise selected Activity from ActivitySelectionScreen
        selectedActivity = intent.getStringExtra("selectedActivity") ?: "UnknownActivity"

        // initialise sensor Manger:
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // initialise SharedPreferences
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
                Log.d(Tag, accelData.toString()) // shows data is in sharedPreferences before it is cleared in navigateToSaveOrRestart() method
                navigateToSaveOrRestart()
            }

        }

            countDownTimer.start()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val xAxis = event.values[0].toString()
            val yAxis = event.values[1].toString()
            val zAxis = event.values[2].toString()

            // HasMap to hold accelerometer Data:
            val readingMap = hashMapOf(
                "deviceID" to deviseId,
                "date" to dateString,
                "timeStamp" to timestamp,
                "x-axis" to xAxis,
                "y-axis" to yAxis,
                "z-axis" to zAxis,
                "activity" to selectedActivity
            )
            // Add the reading map to accelerometerData
            accelerometerData.add(readingMap)


            // convert the data to a JSON format
            val dataAsJSON = gson.toJson(accelerometerData)

            // Add the data to sharedPrefs
            val editor = sharedPrefs.edit()
            editor.putString("accelerometerData", dataAsJSON)
            editor.apply()
        }
    }

    /* Goes to Start or Restart Screen */
    private fun navigateToSaveOrRestart() {
        // Save data to file before clearing SharedPreferences
        writeDataToFile(accelerometerData, file)

        // Navigate to the next screen
        val intent = Intent(this, SaveOrRestartActivity::class.java)
        startActivity(intent)
    }


    private fun writeDataToFile(data: List<Map<String, String>>, fileName: String) {
        try {
            // App-specific external storage, providing a null directory
            val externalDir : File? = getExternalFilesDir(null)
            if (externalDir != null) {
                // File object to represent the external storage
                val externalFile = File(externalDir, fileName)
                val addHeaders = externalFile.length() == 0L

                FileOutputStream(externalFile, true).bufferedWriter().use { writer ->
                    if (addHeaders) {
                        writer.write("DeviseID,Date,Year,TimeStamp,X,Y,Z,Activity\n")
                    }
                    data.forEach { entry ->
                        val line = "${entry["deviceID"] ?: ""}, " +
                                "${entry["date"] ?: ""}, " +
                                "${entry["timeStamp"] ?: ""}, " +
                                "${entry["x-axis"] ?: ""}, " +
                                "${entry["y-axis"] ?: ""}, " +
                                "${entry["z-axis"] ?: ""}, " +
                                "${entry["activity"] ?: ""}, "
                        writer.write(line +"\n")
                    }
                    // Log successful writing:
                    Log.d(Tag, "Data Saved to file: $fileName at ${externalFile.absolutePath}")

                }

            } else {
                Log.d(Tag, "External storage is null")
            }
        } catch (e:Exception) {
            e.printStackTrace()
            Log.e(Tag, "Error writing data to file ${e.message}")
        }
    }

    // helper function to retrieve accelerometer data ;
    private fun retrieveAccelDate() : List<Map<String, String>> {
        val accelerometerDataJson = sharedPrefs.getString("accelerometerData", null)
        val accelerometerDataList : List<Map<String, String>> = gson.fromJson(
            accelerometerDataJson,
            object : TypeToken<List<Map<String, String>>>() {}.type
            )
        return accelerometerDataList
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this@CollectAccelerometerData)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //
    }


}