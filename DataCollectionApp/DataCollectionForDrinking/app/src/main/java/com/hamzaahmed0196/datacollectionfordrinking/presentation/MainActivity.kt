
package com.hamzaahmed0196.datacollectionfordrinking.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hamzaahmed0196.datacollectionfordrinking.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_activity)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_selection_view)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

       //  Start button goes to the CollectAccelerometerData
        val startButton: Button = findViewById(R.id.button_initiateDrinking)
        startButton.setOnClickListener {
            val intent = Intent(this, ActivitySelectionScreen::class.java)
            startActivity(intent)
        }


    }

}


