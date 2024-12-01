package com.hamzaahmed0196.datacollectionfordrinking.presentation.activityModel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.hamzaahmed0196.datacollectionfordrinking.databinding.ActivitySelectionRecyclerviewBinding
import com.hamzaahmed0196.datacollectionfordrinking.R
import com.hamzaahmed0196.datacollectionfordrinking.presentation.collectAccelerometerData.CollectAccelerometerData
import com.hamzaahmed0196.datacollectionfordrinking.presentation.parseJSONInput.startServer
import com.hamzaahmed0196.datacollectionfordrinking.presentation.postRequest.HTTPRequestManagement

class ActivitySelectionTwo : AppCompatActivity() {

    private lateinit var binding: ActivitySelectionRecyclerviewBinding
    private val Tag : String? = null
    private var activitiesList : ArrayList<ActivityModelTwo> = ArrayList()
    private lateinit var activityAdaptorTwo: ActivityAdaptorTwo
    private lateinit var userID : String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = intent.getStringExtra("userID") ?: ": UnknownUser"
        Log.d(Tag, "ActivitySelectionTwo.0: $userID")
        loadData()
        binding = ActivitySelectionRecyclerviewBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        activityAdaptorTwo = ActivityAdaptorTwo(activitiesList) { selectedActivity ->
            val intent = Intent(this, CollectAccelerometerData::class.java)
            intent.putExtra("selectedActivity", selectedActivity)
            Log.d(Tag, "ActivitySelectionTwo.1: $userID")
            intent.putExtra("userID", userID)
            startActivity(intent)
        }

        binding.apply {
            mRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@ActivitySelectionTwo)
                adapter = activityAdaptorTwo
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun loadData() {
        val httpRequestManagement = HTTPRequestManagement(this)
        val activityNames : List<String> = httpRequestManagement.getSavedActivities()
        for (name in activityNames) {
            activitiesList.add(ActivityModelTwo(name))
        }

    }
}