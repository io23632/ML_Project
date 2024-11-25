package com.hamzaahmed0196.datacollectionfordrinking.presentation.postRequest

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.SharedPreferences
import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors


class HTTPRequestManagement(context: Context) {

    // Extract the data from SharedPreferences
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("accelerometerData", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val endpointURL = "https://prod-30.westeurope.logic.azure.com:443/workflows/b101ac1b98504a27bb028bbced6a9369/triggers/manual/paths/invoke?api-version=2016-06-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=YGVg81YYkXgrWvFuWfG4oUMfJ8JUIBoMpEmpQe1x3ZQ"
    // Set up requestQueue variable to hold the list of PostData
    private val postDataQueue = ConcurrentLinkedQueue<PostData>()
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private var Tag : String = "HTTPRequest"


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

    // Function to add new data to queue
    fun addDataToQueue(newPostDataList: List<PostData>) {
        Log.d(Tag, "Adding, ${newPostDataList.size} items to queue")
        newPostDataList.forEach { postDataQueue.add(it)}
        sendDataFromQueue()
    }

    private fun sendDataFromQueue() {
        executor.execute {
            while (postDataQueue.isNotEmpty()) {
                val currentPostData = postDataQueue.poll() // Retrieve and remove the head of the queue
                currentPostData?.let { sendDataToDataBase(it) }
            }
        }
    }

// Function to send a single POSTData Object
private fun sendDataToDataBase(postData: PostData) {
    val postDataJson = gson.toJson(listOf(postData))

    Log.d(Tag, "Sending JSON Body: $postDataJson")

    val requestBody = postDataJson.toRequestBody("application/json".toMediaType())
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(endpointURL)
        .header("Content-Type", "application/json")
        .header("SecurityToken", "23632hbc9")
        .post(requestBody)
        .build()
    try {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.d(Tag, "Failed to send data : $response")
                throw IOException("Unexpected response")
            } else {
                Log.d(Tag, "Data sent successfully")
            }
        }
    } catch (e:IOException) {
        Log.d(Tag, "Error while sending data : ${e.message}")
        e.printStackTrace()
    }

}

    fun retrieverAccelDataAsListPostData() : List<PostData> {

        // get the data from shared preferences in JSON format
        val accelerometerDataJson = sharedPrefs.getString("accelerometerData", null)
        // Convert the Data to a List<Map<String, String>> object
        val accelerometerDataList : List<Map<String, String>> = gson.fromJson(
            accelerometerDataJson,
            object  : TypeToken<List<Map<String, String>>>() {}.type
        ) ?: emptyList() // else return empty list

        // map the accelerometer data to PostData format

        return accelerometerDataList.map { x ->
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

}