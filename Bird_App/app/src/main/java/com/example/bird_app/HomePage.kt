package com.example.bird_app

import android.Manifest
import android.app.DownloadManager.Request
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import okhttp3.Call
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.PolyUtil
import com.google.android.gms.maps.model.Polyline



class HomePage : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var ivSettings: ImageView
    private lateinit var btnSighting: Button
    private lateinit var btnViewSightings: Button
    private lateinit var tvGoal: TextView

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var sqlLiteHelperHelper: SQLLiteHelperHelper

    private var selectedMarker: Marker? = null

    private val directionPolylines = mutableListOf<Polyline>()

    //ebird key : efghcvpuddmj
    //current location for emulator Latitude: 37.421998333333335, Longitude: -122.08400000000002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        sqlLiteHelperHelper = SQLLiteHelperHelper(this)
        initView()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.bird_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        ivSettings.setOnClickListener { startSettings() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnSighting.setOnClickListener { startSighting() }

        btnViewSightings.setOnClickListener{ viewSightings() }

        tvGoal = findViewById(R.id.tv_Goal)
        val count = sqlLiteHelperHelper.getSightingsCount()
        val goalString: String = when {
            count >= 10 -> "Packrat"
            count >= 3 -> "Collector"
            count >= 1 -> "Starter"
            else -> "No Sightings Added"
        }

        tvGoal.text = goalString

        val pbGoal = findViewById<ProgressBar>(R.id.pb_Goal)
        val pbGoal1 = findViewById<ProgressBar>(R.id.pb_Goal1)
        val pbGoal2 = findViewById<ProgressBar>(R.id.pb_Goal2)

        val goalThreshold1 = 1
        val goalThreshold2 = 3
        val goalThreshold3 = 10

        val progressGoal = if (count > goalThreshold1) 100 else (count.toFloat() / goalThreshold1 * 100).toInt()
        val progressGoal1 = if (count > goalThreshold2) 100 else (count.toFloat() / goalThreshold2 * 100).toInt()
        val progressGoal2 = if (count > goalThreshold3) 100 else (count.toFloat() / goalThreshold3 * 100).toInt()


        pbGoal.progress = progressGoal
        pbGoal1.progress = progressGoal1
        pbGoal2.progress = progressGoal2
    }

    private fun startSighting() {
        val intent = Intent(this, Sighting::class.java)
        startActivity(intent)
    }

    private fun viewSightings() {
        val intent = Intent(this, ShowSighting::class.java)
        startActivity(intent)
    }

    private fun startSettings() {
        val intent = Intent(this, Settings::class.java)
        startActivity(intent)
    }

    private fun initView() {
        ivSettings = findViewById(R.id.iv_settings)
        btnSighting = findViewById(R.id.btn_AddBird)
        btnViewSightings = findViewById(R.id.btn_ViewSightings)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val customMarkerIcon =
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE) //changes the users icon colour to blue in order to stand out
        val customSightingMarkerIcon =
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)

        val apiKey = getString(R.string.my_map_api_key)
        MapsInitializer.initialize(this)
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true

        // Request location permissions if not already granted
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

        //directions for markers
        mMap.setOnMarkerClickListener { marker ->
            selectedMarker = marker
            marker.showInfoWindow()  // Ensure that the infoWindow is shown
            showDirections()
            true
        }


        //add markers that user created
        val sightingList = sqlLiteHelperHelper.getAllSightings()

        for (sighting in sightingList) {
            val markerLocation = LatLng(sighting.lat, sighting.long)

            Log.d("Marker", "${markerLocation.latitude} , ${markerLocation.longitude}")

            mMap.addMarker(
                MarkerOptions()
                    .position(markerLocation)
                    .icon(customSightingMarkerIcon)
                    .title(sighting.birdName)
            )
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener(this) { location: Location? ->
                location?.let {

                    // Use the last known location to set the initial map position
                    val userLocation = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(userLocation)
                            .icon(customMarkerIcon)
                            .title("You are here")  // Add a title for the user's location marker
                    )

                    val desiredZoom = 1.25
                    val zoomLevel = calculateZoomLevel(desiredZoom)

                    Log.d(
                        "Location",
                        "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                    )

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel))


                    val apiKey = getString(R.string.eBird_api_key)
                    val hotspotEndpoint = "https://api.ebird.org/v2/ref/hotspot/geo"
                    val maxDistance = intent.getFloatExtra("MAX_DISTANCE", 10.0f)

                    Log.d("Max Distance", "Distance: $maxDistance")

                    val request = okhttp3.Request.Builder()
                        .url("$hotspotEndpoint?lat=${userLocation.latitude}&lng=${userLocation.longitude}&dist=${maxDistance}&fmt=json")
                        .addHeader("X-eBirdApiToken", apiKey)
                        .build()

                    val client = OkHttpClient()

                    Log.d("Request", request.toString())

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            e.printStackTrace()
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (response.isSuccessful) {
                                val responseJson = response.body?.string()

                                Log.d("API Response", "Success")

                                val hotspotList = parseHotspotsFromJson(responseJson)

                                runOnUiThread {
                                    for (hotspot in hotspotList) {
                                        val hotspotLocation =
                                            LatLng(hotspot.latitude, hotspot.longitude)
                                        mMap.addMarker(
                                            MarkerOptions()
                                                .position(hotspotLocation)
                                                .title(hotspot.name)
                                        )
                                    }
                                }

                            }
                        }
                    })
                }
            }
    }

    private fun showDirections() {
        selectedMarker?.let { marker ->
            getLastKnownLocation { userLocation ->
                userLocation?.let {
                    val origin = LatLng(userLocation.latitude, userLocation.longitude)
                    val destination = marker.position
                    val apiKey = getString(R.string.my_map_api_key)

                    for (polyline in directionPolylines) {
                        polyline.remove()
                    }
                    directionPolylines.clear()

                    val directionsApiUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
                            "origin=${origin.latitude},${origin.longitude}&" +
                            "destination=${destination.latitude},${destination.longitude}&" +
                            "key=$apiKey"

                    val client = OkHttpClient()
                    val request = okhttp3.Request.Builder()
                        .url(directionsApiUrl)
                        .build()

                    client.newCall(request).enqueue(object : okhttp3.Callback {
                        override fun onFailure(call: okhttp3.Call, e: IOException) {
                            e.printStackTrace()
                        }

                        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                            if (response.isSuccessful) {
                                val responseJson = response.body?.string()

                                runOnUiThread {
                                    val gson = Gson()
                                    val directionsType = object : TypeToken<DirectionsResponse>() {}.type
                                    val directions = gson.fromJson<DirectionsResponse>(responseJson, directionsType)

                                    val overviewPolyline = directions.routes[0].overview_polyline
                                    val points = PolyUtil.decode(overviewPolyline.points)

                                    val polylineOptions = PolylineOptions()
                                        .addAll(points)
                                        .color(Color.BLUE)
                                        .width(5f)

                                    val polyline = mMap.addPolyline(polylineOptions)
                                    directionPolylines.add(polyline)

                                    val distance = calculateDistance(origin, destination)
                                    val distanceString = if (distance < 1000) {
                                        String.format("%.2f meters", distance)
                                    } else {
                                        String.format("%.2f km", distance / 1000)
                                    }

                                    Toast.makeText(this@HomePage, "Distance: $distanceString", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    })
                }
            }
        }
    }



    private fun getLastKnownLocation(callback: (Location) -> Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    callback(location) // Pass the location to the callback
                }
            }
    }


    private fun calculateZoomLevel(desiredRadiusKm: Double): Float {
        val METERS_PER_INCH = 0.0254
        val INITIAL_SCALE = 156543.03392

        val zoomLevel = (Math.log(INITIAL_SCALE * METERS_PER_INCH * desiredRadiusKm) / Math.log(2.0)).toFloat()

        return zoomLevel
    }
    private fun calculateDistance(origin: LatLng, destination: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            origin.latitude, origin.longitude,
            destination.latitude, destination.longitude,
            results
        )
        return results[0]
    }


}
