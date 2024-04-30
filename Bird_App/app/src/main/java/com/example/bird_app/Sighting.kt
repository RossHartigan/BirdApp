package com.example.bird_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.util.Log


class Sighting : AppCompatActivity() {

    private lateinit var edBirdName : EditText
    private lateinit var edCustomLat : EditText
    private lateinit var edCustomLong : EditText
    private lateinit var btnAddBird : Button

    private var lat : Double = 0.0
    private var long : Double = 0.0

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var sqlLiteHelperHelper: SQLLiteHelperHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sighting)

        sqlLiteHelperHelper = SQLLiteHelperHelper(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initView()

        btnAddBird.setOnClickListener{addNewSighting()}
    }

    private fun initView() {
        edBirdName = findViewById(R.id.ed_BirdName)
        edCustomLat =findViewById(R.id.ed_lat)
        edCustomLong =findViewById(R.id.ed_long)
        btnAddBird = findViewById(R.id.btn_Sighting)
    }

    private fun addNewSighting() {

        val birdName = edBirdName.text.toString()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener(this) { location: Location? ->
                location?.let {

                    val userLocation = LatLng(location.latitude, location.longitude)

                    Log.d("Marker Location","${userLocation.latitude} , ${userLocation.longitude}")

                    val latitude = edCustomLat.text.toString()
                    val longitude = edCustomLong.text.toString()

                    if (latitude.isEmpty() && longitude.isEmpty()) {
                        long = userLocation.longitude
                        lat = userLocation.latitude
                    }else if ((latitude.isEmpty() && longitude.isNotEmpty()) || (latitude.isNotEmpty() && longitude.isEmpty())){
                        Toast.makeText(this, "Enter both the latitude and longitude for  custom co-ordinates.", Toast.LENGTH_SHORT).show()
                    }else{
                        long = longitude.toDouble()
                        lat = latitude.toDouble()

                        Log.d("Marker Location","$long , $lat")
                    }

                    Log.d("co-ords","$lat , $long")

                    if (birdName.isEmpty()){
                        Toast.makeText(this,"Enter the required fields.", Toast.LENGTH_SHORT).show()
                    }else{
                        val std = UserModel(birdName = birdName, lat = lat, long = long)
                        val status = sqlLiteHelperHelper.insertSighting(std)

                        if (status > -1){
                            Toast.makeText(this,"Sighting added ...", Toast.LENGTH_SHORT).show()
                            clearEditText()
                            val intent = Intent(this, HomePage::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this,"Sighting not added ...", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }
    }

    private fun clearEditText() {
        edBirdName.setText("")
    }
}