package com.example.bird_app

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ShowSighting : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_sighting)

        val dbHelper = SQLLiteHelperHelper(this)
        val sightingsList = dbHelper.getAllSightings()

        val linearLayout = findViewById<LinearLayout>(R.id.linearLayoutSightings)

        if (sightingsList.isEmpty()) {

            Toast.makeText(this, "No sightings added yet", Toast.LENGTH_SHORT).show()
        } else {
            for (sighting in sightingsList) {
                val textView = TextView(this)
                textView.text = "Bird Name: ${sighting.birdName}\n" +
                        "Coordinates: (${sighting.lat}, ${sighting.long})\n" +
                        "Bird Number: ${sighting.birdNumber}\n"

                textView.setTextSize(16f)

                linearLayout.addView(textView)

                val separator = TextView(this)
                separator.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
                )
                separator.setBackgroundColor(resources.getColor(android.R.color.black))
                linearLayout.addView(separator)
            }
        }
    }
}



