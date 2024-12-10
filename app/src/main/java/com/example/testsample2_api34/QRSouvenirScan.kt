package com.example.testsample2_api34

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity

class QRSouvenirScan : AppCompatActivity(), View.OnClickListener {

    private lateinit var imgExploreButton: ImageView
    private lateinit var imgSearchLocationButton: ImageView
    private lateinit var imgMainPageButton: ImageView
    private lateinit var imgScanQRButton: ImageView
    private lateinit var imgProfileButton: ImageView
    private lateinit var imgCurrentPageBar: ImageView
    private lateinit var btnScanQR: Button
    private lateinit var textScanResult: TextView
    private lateinit var textWelcomeScanning: TextView
    private lateinit var imgScanVerified: ImageView
    private lateinit var imgScanNow: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrsouvenir_scan)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)

        imgExploreButton  = findViewById(R.id.imgExplore)
        imgSearchLocationButton = findViewById(R.id.imgSearchLocation)
        imgMainPageButton = findViewById(R.id.imgMainPage)
        imgScanQRButton = findViewById(R.id.imgScanQR)
        imgProfileButton = findViewById(R.id.imgProfile)
        imgCurrentPageBar = findViewById(R.id.imgCurrentPageLine)
        textScanResult = findViewById(R.id.textScanResult)
        textWelcomeScanning = findViewById(R.id.textWelcomeScanning)
        btnScanQR = findViewById(R.id.btnScanQR)
        imgScanNow = findViewById(R.id.imgScanNow)
        imgScanVerified = findViewById(R.id.imgScanVerified)


        //Set the CurrentPage icon to dark green
        imgScanQRButton.setColorFilter(Color.parseColor("#138B60"))

        btnScanQR.setOnClickListener { startQRCodeScanning() }

        imgCurrentPageBar.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                imgCurrentPageBar.x = imgScanQRButton.x - imgScanQRButton.width / 10
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
    }

    private fun startQRCodeScanning() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR code")
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)
        integrator.setOrientationLocked(true)
        integrator.captureActivity = CaptureActivity::class.java
        integrator.initiateScan()
    }

    @SuppressLint("SetTextI18n")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                textScanResult.text = "Cancelled"
            } else {
                // Get the scanned souvenirId
                val scannedSouvenirId = result.contents.toInt()

                // Get the current logged user's UID
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                // Get the Firestore instance
                val db = FirebaseFirestore.getInstance()

                // Get the user's document
                val docRef = db.collection("Users").document(uid!!)

                docRef.get().addOnSuccessListener { document ->
                    if (document != null) {
                        // Get the collectedSouvenirs array
                        val collectedSouvenirs = document.get("collectedSouvenirs") as List<Int>

                        // Check if the scanned souvenir is already unlocked
                        var alreadyUnlocked = false
                        for (souvenirId in collectedSouvenirs) {
                            if (souvenirId == scannedSouvenirId) {
                                alreadyUnlocked = true
                                break
                            }
                        }

                        if (alreadyUnlocked) {
                            textScanResult.text = "You unlocked this souvenir before"
                        } else {
                            // Unlock the souvenir
                            val newCollectedSouvenirs = collectedSouvenirs.toMutableList()
                            newCollectedSouvenirs.add(scannedSouvenirId)
                            docRef.update("collectedSouvenirs", newCollectedSouvenirs)

                            // Get the souvenirName
                            db.collection("Souvenirs").document(scannedSouvenirId.toString())
                                .get()
                                .addOnSuccessListener { souvenirDocument ->
                                    val souvenirName = souvenirDocument.getString("souvenirName")
                                    textScanResult.text = "Congratulations! You unlocked $souvenirName. Have a nice trek!"
                                    imgScanNow.visibility = View.GONE
                                    imgScanVerified.visibility = View.VISIBLE
                                }
                        }
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }.addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
                textScanResult.visibility = View.VISIBLE
                textWelcomeScanning.visibility = View.GONE
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

}