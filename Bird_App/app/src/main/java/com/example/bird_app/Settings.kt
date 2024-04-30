package com.example.bird_app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView

class Settings : AppCompatActivity() {

    private lateinit var ivHome: ImageView
    private lateinit var sbDistance : SeekBar
    private lateinit var tvDistance : TextView
    private lateinit var swMetric : Switch
    private lateinit var tvDistanceUnit : TextView

    private lateinit var sharedPreferences: SharedPreferences

    private var maxDistance = 0.0
    private var multiply = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("MyAppSettings", Context.MODE_PRIVATE)

        initView()

        ivHome.setOnClickListener{startHome()}

        swMetric.setOnCheckedChangeListener { _, isChecked ->
            val unitLabel = if (isChecked) "km" else "miles"
            multiply = if(isChecked) 1.0 else 1.6
            tvDistanceUnit.text = unitLabel

            saveDistanceUnit(isChecked)
        }

        val savedUnit = loadDistanceUnit()
        swMetric.isChecked = savedUnit

        val savedMaxDistance = loadMaxDistance()
        sbDistance.progress = savedMaxDistance.toInt()
        tvDistance.text = savedMaxDistance.toString()


        sbDistance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // This method is called when the user starts interacting with the SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // This method is called when the user stops interacting with the SeekBar
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // This method is called when the SeekBar's progress changes
                // Update a TextView or any other view to display the distance
                val distanceInKm = progress.toFloat() // Progress is from 0 to 100
                maxDistance = progress.toDouble() * multiply
                tvDistance.text = "$distanceInKm"

                saveMaxDistance(maxDistance)
            }
        })
    }

    private fun startHome() {
        val intent = Intent(this, HomePage::class.java)
        intent.putExtra("MAX_DISTANCE", maxDistance.toFloat())
        startActivity(intent)
    }

    private fun initView() {
        ivHome = findViewById(R.id.iv_Home)
        sbDistance = findViewById(R.id.sb_Distance)
        tvDistance = findViewById(R.id.tv_DistanceView)
        swMetric = findViewById(R.id.sw_Metric)
        tvDistanceUnit = findViewById(R.id.tv_DistanceUnit)
    }

    private fun saveDistanceUnit(isKilometers: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isKilometers", isKilometers)
        editor.apply()
    }

    // Load the saved unit from SharedPreferences
    private fun loadDistanceUnit(): Boolean {
        return sharedPreferences.getBoolean("isKilometers", true) // Use 'true' as the default value
    }

    private fun saveMaxDistance(distance: Double) {
        val editor = sharedPreferences.edit()
        editor.putFloat("maxDistance", distance.toFloat())
        editor.apply()
    }

    // Load the saved max distance from SharedPreferences
    private fun loadMaxDistance(): Double {
        val savedDistance = sharedPreferences.getFloat("maxDistance", 0.0f) // Use '0.0f' as the default value
        return savedDistance.toDouble()
    }
}