package com.hamzaahmed0196.datacollectionfordrinking.presentation.parseJSONActivity



import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ParseJSON {
    @Serializable
    data class UserInput(
        @SerialName("Activities")
        val activities : List<String>)
    private val jsonFile = """
        {
          "Activities": [
            "Walking",
            "Running",
            "Eating",
            "Drinking",
            "Smoking"
          ]
        }
    """.trimIndent()



   fun parseActivitiesFromJSON(): List<String> {
       val userInput : UserInput = Json.decodeFromString(jsonFile)
       return userInput.activities

   }

}