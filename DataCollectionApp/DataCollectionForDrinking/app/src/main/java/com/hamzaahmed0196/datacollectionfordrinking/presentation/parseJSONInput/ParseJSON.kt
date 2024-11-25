package com.hamzaahmed0196.datacollectionfordrinking.presentation.parseJSONInput

import kotlinx.serialization.json.Json

class ParseJSON {
    var activitiesList : ArrayList<String> = ArrayList()
        private set
    var userID : String = "Unknown User"
        private set

    fun processJSON(json : String) {
        val incomingData : IncomingData = Json.decodeFromString(json)
        activitiesList = ArrayList(incomingData.activities)
        userID = incomingData.userIDs
    }
}