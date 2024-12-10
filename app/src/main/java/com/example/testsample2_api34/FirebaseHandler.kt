package com.example.testsample2_api34


import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlin.math.log

class FirebaseHandler {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("NotifyDataSetChanged")
    fun retrieveCountries(
        countryAdapter: CountryAdapter,
        onSuccess: (List<Country>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("Countries")
            .get()
            .addOnSuccessListener { documents ->
                val countries = mutableListOf<Country>()
                for (document in documents) {
                    val name = document.getString("countryName")
                    val description = document.getString("countryDescription")
                    val logo = document.getString("logo")
                    val country = Country(name ?: "", description ?: "", logo ?: "")
                    countries.add(country)
                }
                onSuccess(countries)
                // Call retrieveSouvenirsForCountry for each country
                countries.forEach { country ->
                    retrieveSouvenirsForCountry(country.countryName, onSuccess = { souvenirs ->
                        country.souvenirList.clear()
                        country.souvenirList.addAll(souvenirs)
                        // Notify adapter of data change for this country
                        countryAdapter.notifyDataSetChanged()
                    },
                        onFailure = { exception ->
                            Log.e(
                                "FirebaseHandler",
                                "Failed to retrieve souvenirs for country ${country.countryName}",
                                exception
                            )
                        })
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun retrieveSouvenirsForCountry(
        countryName: String?,
        onSuccess: (List<Souvenir>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (countryName != null) {
            db.collection("Souvenirs")
                .whereEqualTo("souvenirCountry", countryName)
                .get()
                .addOnSuccessListener { documents ->
                    val souvenirs = mutableListOf<Souvenir>()
                    for (document in documents) {
                        val name = document.getString("souvenirName")
                        val description = document.getString("souvenirDescription")
                        val logoSouvenir = document.getString("souvenirLogo")
                        val countryOrigin = document.getString("souvenirCountry")
                        val souvenir = Souvenir(
                            name ?: "",
                            logoSouvenir ?: "",
                            countryOrigin ?: "",
                            description ?: ""
                        )
                        souvenirs.add(souvenir)
                    }
                    onSuccess(souvenirs)
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }
    }
    fun retrieveCollectedSouvenirs(
        onSuccess: (List<Souvenir>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Get the current user's UID
        val currentUserUid = auth.currentUser?.uid

        // Check if the current user is authenticated
        if (currentUserUid != null) {
            // Reference to the user's document in Firestore
            val userDocumentRef = db.collection("Users").document(currentUserUid)

            userDocumentRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val user = documentSnapshot.toObject(User::class.java)

                        // Extract souvenir IDs from the user's collected souvenirs
                        val collectedSouvenirIds = user?.collectedSouvenirs ?: emptyList()

                        if (collectedSouvenirIds.isEmpty()) {
                            // User has no collected souvenirs
                            onFailure(Exception("You don't have any souvenirs available. Go on a trip!"))
                        } else {
                            // Query the "souvenirs" collection for collected souvenirs
                            db.collection("Souvenirs")
                                .whereIn(FieldPath.documentId(), collectedSouvenirIds.map { it.toString() })
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    val souvenirs = querySnapshot.toObjects(Souvenir::class.java)
                                    onSuccess(souvenirs)
                                }
                                .addOnFailureListener { exception ->
                                    onFailure(exception)
                                }
                        }
                    } else {
                        // User document does not exist
                        onFailure(Exception("User document does not exist"))
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        } else {
            // Handle the case when the user is not authenticated
            onFailure(Exception("User is not authenticated"))
        }
    }


    fun printUserData(){
        // Initialize Firebase Firestore
        val db = FirebaseFirestore.getInstance()

// Get the current user's UID
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserUid != null) {
            // Reference to the user's document in Firestore
            val userDocumentRef = db.collection("Users").document(currentUserUid)

            userDocumentRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Print all data from the document
                        val dataMap = documentSnapshot.data
                        if (dataMap != null) {
                            for ((key, value) in dataMap) {
                                println("$key: $value")
                            }
                        }
                    } else {
                        println("User document does not exist.")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error retrieving user data: ${exception.message}")
                }
        } else {
            println("User is not authenticated.")
        }
    }
}


