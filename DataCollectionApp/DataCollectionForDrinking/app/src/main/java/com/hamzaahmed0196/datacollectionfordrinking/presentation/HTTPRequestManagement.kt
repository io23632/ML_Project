package com.hamzaahmed0196.datacollectionfordrinking.presentation

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson


class HTTPRequestManagement(context: Context) {
    // Extract the data from SharedPreferences
    private val sharedPrefs : SharedPreferences = context.getSharedPreferences("accelerometerData", Context.MODE_PRIVATE)
    // Data Class to hold the hold data
    data class PostData(
       val id : String,
       val timeStamp: String,
       val xAxis : String,
       val yAxis : String,
       val zAxis : String,
       val activity : String
    )

    private val gson = Gson()














}