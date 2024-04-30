package com.example.bird_app

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray

data class Hotspot(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

fun parseHotspotsFromJson(jsonString: String?): List<Hotspot> {
    val hotspotList = mutableListOf<Hotspot>()

    try {
        // Parse the JSON response and populate hotspotList
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val name = jsonObject.getString("locName")
            val latitude = jsonObject.getDouble("lat")
            val longitude = jsonObject.getDouble("lng")
            hotspotList.add(Hotspot(name, latitude, longitude))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return hotspotList
}
