package com.hamzaahmed0196.datacollectionfordrinking.presentation.HTTPManagement

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.SharedPreferences
import android.util.Log
import com.hamzaahmed0196.datacollectionfordrinking.presentation.UsefulFunctions
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.util.concurrent.Executors


class HTTPRequestManagement(context: Context) {

    // Extract the data from SharedPreferences
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("accelerometerData", Context.MODE_PRIVATE)
    private val gson = Gson()
    private var postDataList: List<PostData>
    private val endpointURL = "https://prod-30.westeurope.logic.azure.com:443/workflows/b101ac1b98504a27bb028bbced6a9369/triggers/manual/paths/invoke?api-version=2016-06-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=YGVg81YYkXgrWvFuWfG4oUMfJ8JUIBoMpEmpQe1x3ZQ"
    private var Tag : String = "HTTPRequest"
    private val usefulFunctions = UsefulFunctions()
    private val accelData : List<String> = usefulFunctions.retrieveAccelData(context)

    // Retrieve accelerometerData
    private val accelerometerDataJson = sharedPrefs.getString("accelerometerData", null)
    private val accelerometerDataList: List<Map<String, String>> = gson.fromJson(
        accelerometerDataJson,
        object : TypeToken<List<Map<String, String>>>() {}.type
    )


    // Data Class to hold the data
    data class PostData(
        val id: String,
        val userID : String,
        val date: String,
        val timeStamp: String,
        val xAxis: String,
        val yAxis: String,
        val zAxis: String,
        val activity: String
    )

    // Initialize postDataList in the init block
    init {
        postDataList = accelerometerDataList.map { x ->
            PostData(
                id = x["deviceID"] ?: "",
                userID = x["userID"] ?: "",
                date = x["date"] ?: "",
                timeStamp = x["timeStamp"] ?: "",
                xAxis = x["x-axis"] ?: "",
                yAxis = x["y-axis"] ?: "",
                zAxis = x["z-axis"] ?: "",
                activity = x["activity"] ?: ""
            )
        }
    }

    fun sendDataToDataBase() {
        val postDataJson = gson.toJson(postDataList)

        Log.d(Tag, "HTTPS : PostDataJSON: $postDataJson") // Sucessfull ! I can see the data.

        val requestBody = postDataJson.toRequestBody("application/json".toMediaType())
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(endpointURL)
            .header("Content-Type", "application/json")
            .header("SecurityToken", "23632hbc9")
            .post(requestBody)
            .build()
        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.execute {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected response")
                    Log.d(Tag, "Data sent successfully: ${response.body.toString()}") // Also sucessfull
                }
            }catch (e:IOException) {
                Log.d(Tag,e.printStackTrace().toString())
            }

        }
    }

    // Function to verify HTTP Management class has access to the same accelerometer Data
    fun showAccelData() {
        Log.d(Tag, "HTTPS Management Class: $accelData")
    }

}