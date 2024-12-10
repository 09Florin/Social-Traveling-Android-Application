package com.example.testsample2_api34

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.graphics.Color
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var imgExploreButton: ImageView
    private lateinit var imgSearchLocationButton: ImageView
    private lateinit var imgMainPageButton: ImageView
    private lateinit var imgScanQRButton: ImageView
    private lateinit var imgProfileButton: ImageView
    private lateinit var imgCurrentPageBar: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var souvenirAdapter: SouvenirAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        // Add this in your ExploreActivity.kt or Application class
        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        checkSurveyTaken()
        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRecommendedSouvenirs)
        souvenirAdapter = SouvenirAdapter(emptyList(), false) // Initialize the souvenirAdapter
        recyclerView.adapter = souvenirAdapter // Set the adapter to the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        imgExploreButton  = findViewById(R.id.imgExplore)
        imgSearchLocationButton = findViewById(R.id.imgSearchLocation)
        imgMainPageButton = findViewById(R.id.imgMainPage)
        imgScanQRButton = findViewById(R.id.imgScanQR)
        imgProfileButton = findViewById(R.id.imgProfile)
        imgCurrentPageBar = findViewById(R.id.imgCurrentPageLine)

        //Set the CurrentPage icon to dark green
        imgMainPageButton.setColorFilter(Color.parseColor("#138B60"))

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
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
    private fun checkSurveyTaken() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            firestore.collection("Users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val surveyTaken = document.getBoolean("surveyTaken") ?: false
                        if (surveyTaken) {
                            // User has taken the survey, recommend souvenirs
                            findViewById<TextView>(R.id.textRecommendedLocations).visibility = View.VISIBLE
                            findViewById<RecyclerView>(R.id.recyclerViewRecommendedSouvenirs).visibility = View.VISIBLE
                            reccomendSouvenirs(uid)
                        } else {
                            // User has not taken the survey, show survey button
                            showSurveyButton()
                            findViewById<Button>(R.id.btnTakeSurvey).visibility = View.VISIBLE
                            findViewById<TextView>(R.id.textSurveyPrompt).visibility = View.VISIBLE
                        }
                    } else {
                        Log.d("MainActivity", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("MainActivity", "get failed with ", exception)
                }
        }
    }

    private fun showSurveyButton() {
        val takeSurveyButton: Button = findViewById(R.id.btnTakeSurvey)
        takeSurveyButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SurveyTestActivity::class.java))
        }
    }

    private fun reccomendSouvenirs(uid: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("Users").document(uid)

        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val categories = mapOf(
                        "categoryCityAttraction" to (documentSnapshot.getLong("travelerTypeCityAttraction") ?: 0),
                        "categoryCultural" to (documentSnapshot.getLong("travelerTypeCultural") ?: 0),
                        "categoryHistorical" to (documentSnapshot.getLong("travelerTypeHistorical") ?: 0),
                        "categoryNatural" to (documentSnapshot.getLong("travelerTypeNatural") ?: 0),
                        "categoryOther" to (documentSnapshot.getLong("travelerTypeOther") ?: 0),
                        "categoryPopCulture" to (documentSnapshot.getLong("travelerTypePopCulture") ?: 0)
                    )

                    val highestCategory: String? = categories.maxByOrNull { it.value }?.key

                    // Get the collectedSouvenirs array
                    val collectedSouvenirs = documentSnapshot.get("collectedSouvenirs") as? List<Long> ?: listOf()

                    // Fetch all souvenirs
                    db.collection("Souvenirs").get()
                        .addOnSuccessListener { result ->
                            val uncollectedSouvenirs = result.documents.map { it.id.toLong() }.filter { it !in collectedSouvenirs }

                            // Fetch each uncollected souvenir and check if the highestCategory is set to true
                            val matchingSouvenirs = mutableListOf<Souvenir>()
                            for (souvenirId in uncollectedSouvenirs) {
                                val souvenirRef = db.collection("Souvenirs").document(souvenirId.toString())
                                souvenirRef.get()
                                    .addOnSuccessListener { souvenirDocumentSnapshot ->
                                        if (souvenirDocumentSnapshot.exists()) {
                                            highestCategory?.let { category ->
                                                val isCategory = souvenirDocumentSnapshot.getBoolean(category) ?: false
                                                if (isCategory) {
                                                    val souvenirName = souvenirDocumentSnapshot.getString("souvenirName")
                                                    val souvenirLogo = souvenirDocumentSnapshot.getString("souvenirLogo")
                                                    matchingSouvenirs.add(Souvenir(souvenirName ?: "", souvenirLogo ?: ""))
                                                    if (matchingSouvenirs.size <= 3) {
                                                        // Update the RecyclerView with uncollected souvenirs
                                                        souvenirAdapter.updateData(matchingSouvenirs)
                                                        recyclerView.visibility = View.VISIBLE
                                                    }
                                                }
                                            }
                                        } else {
                                            println("Souvenir document does not exist")
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        println("Error getting souvenir document: $e")
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            println("Error getting souvenirs: $e")
                        }
                } else {
                    println("User document does not exist")
                }
            }
            .addOnFailureListener { e ->
                println("Error getting user document: $e")
            }
    }
}