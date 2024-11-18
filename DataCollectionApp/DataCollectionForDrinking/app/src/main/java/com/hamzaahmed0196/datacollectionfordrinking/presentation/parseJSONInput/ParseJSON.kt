package com.hamzaahmed0196.datacollectionfordrinking.presentation.parseJSONInput



import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ParseJSON {
    @Serializable
    data class UserInput(
        @SerialName("Activities") val activities : List<String>,
        @SerialName("UserID") val userID : String
    )
    private val jsonFile = """
        {
          "Activities": [
            "Walking",
            "Running",
            "Eating",
            "Drinking",
            "Smoking"
          ],
          "UserID" : "User1"
        }
    """.trimIndent()



   @OptIn(ExperimentalSerializationApi::class)
   fun parseActivitiesFromJSON(): List<String> {
       val userInput : UserInput = Json.decodeFromString(jsonFile)
       return userInput.activities

   }

    @OptIn(ExperimentalSerializationApi::class)
    fun parseUserIDFromJSON() : String {
        val userInput : UserInput = Json.decodeFromString(jsonFile)
        return userInput.userID
    }

}