package com.example.testsample2_api34

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener


class ExploreActivity : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback {


    private lateinit var mMap: GoogleMap
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private var newLocationMarker: Marker? = null

    private lateinit var imgExploreButton: ImageView
    private lateinit var imgSearchLocationButton: ImageView
    private lateinit var imgMainPageButton: ImageView
    private lateinit var imgScanQRButton: ImageView
    private lateinit var imgProfileButton: ImageView
    private lateinit var imgCurrentPageBar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)

        //Autocomplete fragment (search location)
        Places.initialize(applicationContext, getString(R.string.google_map_api_key))
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Toast.makeText(this@ExploreActivity, "Search Error", Toast.LENGTH_SHORT).show()
            }

            override fun onPlaceSelected(place: Place) {
                //val address = place.address
                //val id = place.id
                val latLng = place.latLng
                if (latLng != null) {
                    zoomMapOnSearchLocation(latLng)
                }
            }
        })


        // mapFragment is the parent UI of the google maps application
        val mapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Buttons
        imgExploreButton = findViewById(R.id.imgExplore)
        imgSearchLocationButton = findViewById(R.id.imgSearchLocation)
        imgMainPageButton = findViewById(R.id.imgMainPage)
        imgScanQRButton = findViewById(R.id.imgScanQR)
        imgProfileButton = findViewById(R.id.imgProfile)
        imgCurrentPageBar = findViewById(R.id.imgCurrentPageLine)

        //Set the CurrentPage icon to dark green
        imgExploreButton.setColorFilter(Color.parseColor("#138B60"))

        imgCurrentPageBar.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                imgCurrentPageBar.x = imgExploreButton.x - imgExploreButton.width / 10
                // Optionally, remove the listener if you only need to move the view once
                imgCurrentPageBar.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        imgExploreButton.setOnClickListener {
            if (!this::class.java.isAssignableFrom(ExploreActivity::class.java)) {
                // Logic to handle click on Explore image
                val intentButton = Intent(this, ExploreActivity::class.java)
                startActivity(intentButton)
            }
        }


        imgSearchLocationButton.setOnClickListener {
            // Logic to handle click on Search image
            val intentButton = Intent(this, SouvenirsActivity::class.java)
            startActivity(intentButton)
        }

        imgMainPageButton.setOnClickListener {
            // Logic to handle click on Main Page image
            val intentButton = Intent(this, MainActivity::class.java)
            startActivity(intentButton)
        }

        imgScanQRButton.setOnClickListener {
            // Logic to handle click on Search People image
            val intentButton = Intent(this, QRSouvenirScan::class.java)
            startActivity(intentButton)
        }

        imgProfileButton.setOnClickListener {
            // Logic to handle click on Profile image
            val intentButton = Intent(this, ProfileActivity::class.java)
            startActivity(intentButton)
        }
    }

    private fun zoomMapOnSearchLocation(latLng: LatLng) {
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 12f) // 12f is the zoom level
        val location = LatLng(latLng.latitude, latLng.longitude)
        newLocationMarker?.remove()
        newLocationMarker = mMap.addMarker(
            MarkerOptions()
                .position(location)
                .title("Marker in the searched location")
        )
        mMap.animateCamera(newLatLngZoom)
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        newLocationMarker?.remove()
        // Add a marker in Craiova and move the camera
        val craiova = LatLng(44.319305, 23.800678)
        newLocationMarker = mMap.addMarker(
            MarkerOptions()
                .position(craiova)
                .title("Marker in Craiova")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(craiova))

        mMap.setOnMapLongClickListener { latLng ->
            // Handle the long click event here
            setPinOnMap(latLng)
        }

    }

    private fun setPinOnMap(latLng: LatLng) {
        // Remove the previous marker
        newLocationMarker?.remove()

        // Add a new marker at the touched location
        newLocationMarker = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Custom Marker")
        )

        // Zoom to the touched location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))

        // Show the place details UI (you can customize this part)
        //showPlaceDetailsUI()
    }
}

//   private fun showPlaceDetailsUI() {
//       // Show the layout containing place details (you can customize this part)
//       placeDetailsLayout.visibility = View.VISIBLE
//
//        // You can update the description, photos, and souvenirs UI elements as needed
//        tvPlaceDescription.text = "Custom Marker Description"
//    }

