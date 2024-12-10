package com.example.testsample2_api34

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import android.view.ViewTreeObserver
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class SouvenirsActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var imgExploreButton: ImageView
    private lateinit var imgSearchLocationButton: ImageView
    private lateinit var imgMainPageButton: ImageView
    private lateinit var imgScanQRButton: ImageView
    private lateinit var imgProfileButton: ImageView
    private lateinit var imgCurrentPageBar: ImageView
    private lateinit var recyclerViewCountries: RecyclerView
    private lateinit var countryAdapter: CountryAdapter
    private lateinit var originalCountryAdapter: CountryAdapter
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchButton: Switch
    private lateinit var firebaseHandler: FirebaseHandler



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_souvenirs)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)

        imgExploreButton  = findViewById(R.id.imgExplore)
        imgSearchLocationButton = findViewById(R.id.imgSearchLocation)
        imgMainPageButton = findViewById(R.id.imgMainPage)
        imgScanQRButton = findViewById(R.id.imgScanQR)
        imgProfileButton = findViewById(R.id.imgProfile)
        imgCurrentPageBar = findViewById(R.id.imgCurrentPageLine)
        recyclerViewCountries = findViewById(R.id.recyclerViewCountries)
        switchButton = findViewById(R.id.switchButton)

        recyclerViewCountries.setHasFixedSize(true)
        recyclerViewCountries.layoutManager = LinearLayoutManager(this)

        firebaseHandler = FirebaseHandler()
        countryAdapter = CountryAdapter(ArrayList(), firebaseHandler)
        originalCountryAdapter = CountryAdapter(ArrayList(), firebaseHandler)
        recyclerViewCountries.adapter = countryAdapter

        //Set the CurrentPage icon to dark green
        imgSearchLocationButton.setColorFilter(Color.parseColor("#138B60"))

        imgCurrentPageBar.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                imgCurrentPageBar.x = imgSearchLocationButton.x - imgSearchLocationButton.width / 10
                // Optionally, remove the listener if you only need to move the view once
                imgCurrentPageBar.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        imgExploreButton.setOnClickListener {
            // Logic to handle click on Explore image
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent)
        }

        imgSearchLocationButton.setOnClickListener {
            // Logic to handle click on Search image
            val intent = Intent(this, SouvenirsActivity::class.java)
            startActivity(intent)
        }

        imgMainPageButton.setOnClickListener {
            // Logic to handle click on Main Page image
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        imgScanQRButton.setOnClickListener {
            // Logic to handle click on Search People image
            val intent = Intent(this, QRSouvenirScan::class.java)
            startActivity(intent)
        }

        imgProfileButton.setOnClickListener {
            // Logic to handle click on Profile image
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        displayCountries()

        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                firebaseHandler.printUserData() //debugging logcat statement
                originalCountryAdapter = recyclerViewCountries.adapter as CountryAdapter
                // Query the user's collected souvenirs from Firestore
                firebaseHandler.retrieveCollectedSouvenirs(
                    onSuccess = { souvenirs ->
                        // Update the RecyclerView with collected souvenirs
                        val collectedSouvenirsAdapter = SouvenirAdapter(souvenirs, isChecked)
                        recyclerViewCountries.adapter = collectedSouvenirsAdapter
                    },
                    onFailure = { exception ->
                        Log.e("SouvenirsActivity", "Failed to retrieve collected souvenirs", exception)
                    }
                )
            } else {
                recyclerViewCountries.adapter = originalCountryAdapter
                displayCountries()
            }
        }
    }

    override fun onClick(v: View?) {
        // Handle item click from RecyclerView if needed
    }

    private fun displayCountries() {
        firebaseHandler.retrieveCountries(
            countryAdapter,
            onSuccess = { countries ->
                countryAdapter.updateCountries(countries)
            },
            onFailure = { exception ->
                Log.e("SouvenirsActivity", "Failed to retrieve countries", exception)
            }
        )
    }

}