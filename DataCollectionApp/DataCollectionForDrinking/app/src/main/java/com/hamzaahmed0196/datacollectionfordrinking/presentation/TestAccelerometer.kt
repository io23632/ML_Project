package com.hamzaahmed0196.datacollectionfordrinking.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hamzaahmed0196.datacollectionfordrinking.R
import kotlin.math.abs
import kotlin.math.sqrt

class TestAccelerometer : AppCompatActivity(), SensorEventListener {

    private lateinit var currentAccel : TextView
    private lateinit var changeAccel : TextView
    private var previousAccel : Double = 0.0
    private lateinit var progress_bar : ProgressBar

    private lateinit var sensorManager: SensorManager
    private var sensor : Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test_accelerometer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_selection_view)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currentAccel = findViewById(R.id.current_accel)
        changeAccel = findViewById(R.id.change_in_accel)

        // initialise seneor Manager:

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)






    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }


    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val x : Float = event.values[0]
            val y : Float = event.values[1]
            val z : Float = event.values[2]

            // normalise the three axis value into one
            val sumSquares : Float = (x*x) + (y*y) + (z*z)
            var currentAcceleration = sqrt(sumSquares)
            val changeAcceleration = abs(currentAcceleration - previousAccel)
            previousAccel = currentAcceleration.toDouble()

            currentAccel.text = "Current Accel is $currentAcceleration"
            changeAccel.text = "Change in Accel is $changeAcceleration"

        }
    }



    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}

