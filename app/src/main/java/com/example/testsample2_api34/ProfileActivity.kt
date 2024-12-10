package com.example.testsample2_api34

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.widget.ImageView
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var imgExploreButton: ImageView
    private lateinit var imgSearchLocationButton: ImageView
    private lateinit var imgMainPageButton: ImageView
    private lateinit var imgScanQRButton: ImageView
    private lateinit var imgProfileButton: ImageView
    private lateinit var imgCurrentPageBar: ImageView
    private lateinit var logout: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var user: FirebaseUser
    private lateinit var imgProfilePicture: ImageView
    private lateinit var txtUserName: TextView
    private lateinit var textUserEmail: TextView
    private lateinit var buttonEditEmail: ImageButton
    private var selectedImageUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var btnChangePicture: ImageButton
    private lateinit var changePasswordText: TextView

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the result
            val data: Intent? = result.data
            data?.data?.let { uri ->
                selectedImageUri = uri
                // Call the method to handle the selected image URI
                handleSelectedImageUri()
            }
        }
    }

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)

        imgExploreButton  = findViewById(R.id.imgExplore)
        imgSearchLocationButton = findViewById(R.id.imgSearchLocation)
        imgMainPageButton = findViewById(R.id.imgMainPage)
        imgScanQRButton = findViewById(R.id.imgScanQR)
        imgProfileButton = findViewById(R.id.imgProfile)
        imgCurrentPageBar = findViewById(R.id.imgCurrentPageLine)
        logout = findViewById(R.id.LogOut)
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        user = firebaseAuth.currentUser!!
        imgProfilePicture = findViewById(R.id.imgProfilePicture)
        btnChangePicture = findViewById(R.id.btnChangePicture)
        txtUserName = findViewById(R.id.txtUserName)
        textUserEmail = findViewById(R.id.textUserEmail)
        changePasswordText = findViewById(R.id.changePassword)
        buttonEditEmail = findViewById(R.id.buttonEditEmail)
        storageRef = FirebaseStorage.getInstance().reference
        //Set the CurrentPage icon to dark green
        imgProfileButton.setColorFilter(Color.parseColor("#138B60"))

        imgCurrentPageBar.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                imgCurrentPageBar.x = imgProfileButton.x - imgProfileButton.width / 10
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

        logout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(
                baseContext,
                "You logged out",
                Toast.LENGTH_SHORT,
            ).show()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        loadUserProfile()

        setupEditButtons()

        btnChangePicture.setOnClickListener {
            openGallery()
        }

        changePasswordText.setOnClickListener {
        showChangePasswordDialog()
        }
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    private fun loadUserProfile() {
        val userUid = user.uid
        val userDocRef = firestore.collection("Users").document(userUid)

        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val userName = documentSnapshot.getString("userName")
                    val email = documentSnapshot.getString("email")
                    val profilePictureUrl = documentSnapshot.getString("profilePicture")

                    txtUserName.text = userName
                    textUserEmail.text = email
                    // Load profile picture using Picasso or Glide
                    profilePictureUrl?.let {
                        Picasso.get().load(it).into(imgProfilePicture)
                    }
                }
            }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        // Launch the image picker using the launcher
        pickImageLauncher.launch(intent)
    }

    private fun handleSelectedImageUri() {
        // Check if selectedImageUri is not null
        if (selectedImageUri != null) {
            // Load the selected image into imgProfilePicture
            imgProfilePicture.setImageURI(selectedImageUri)

            // Upload the selected image to Firebase Storage
            uploadImageToStorageAndSetProfilePicture()
        }
    }


    private fun uploadImageToStorageAndSetProfilePicture() {
        selectedImageUri?.let { uri ->
            // Create a reference to the location where you want to store the image in Firebase Storage
            val userProfilePicsRef = storageRef.child("User Profile Pictures/${user.uid}")

            // Upload the image to Firebase Storage
            userProfilePicsRef.putFile(uri)
                .addOnSuccessListener { _ ->
                    // Get the download URL for the image
                    userProfilePicsRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        // Update profile picture URL in Firestore
                        updateUserProfilePicture(downloadUri.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                    Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateUserProfilePicture(profilePictureUrl: String) {
        val userUid = user.uid
        val userDocRef = firestore.collection("Users").document(userUid)

        // Update profilePicture field in Firestore document
        userDocRef.update("profilePicture", profilePictureUrl)
            .addOnSuccessListener {
                // Load updated user profile
                loadUserProfile()
                Toast.makeText(this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Toast.makeText(this, "Failed to update profile picture: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupEditButtons() {
        val buttonEditEmail = findViewById<ImageButton>(R.id.buttonEditEmail)
        val userUid = user.uid
        val userDocRef = firestore.collection("Users").document(userUid)

        buttonEditEmail.setOnClickListener {
            // Show a dialog to edit email
            showEditDialog("Edit Email") { newEmail ->
                // Update email in Firestore
                userDocRef.update("email", newEmail)
                    .addOnSuccessListener {
                        // Email updated successfully
                        Toast.makeText(this, "Email updated successfully", Toast.LENGTH_SHORT).show()
                        // Update the displayed email
                        textUserEmail.text = newEmail
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                        Toast.makeText(this, "Failed to update email: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }


    private fun showEditDialog(title: String, onConfirm: (String) -> Unit) {
        val editText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newInput = editText.text.toString().trim()
                onConfirm(newInput)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null)

        val editTextCurrentPassword = dialogView.findViewById<EditText>(R.id.editTextCurrentPassword)
        val editTextNewPassword = dialogView.findViewById<EditText>(R.id.editTextNewPassword)
        val editTextConfirmPassword = dialogView.findViewById<EditText>(R.id.editTextConfirmPassword)

        val alertDialogBuilder = AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Change") { dialog, _ ->
                val currentPassword = editTextCurrentPassword.text.toString()
                val newPassword = editTextNewPassword.text.toString()
                val confirmPassword = editTextConfirmPassword.text.toString()

                // Handle password change logic
                handleChangePassword(currentPassword, newPassword, confirmPassword)

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun handleChangePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        // Validate input fields
        if (newPassword != confirmPassword) {
            // Passwords do not match
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Reauthenticate the user with their current password
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential)
                .addOnSuccessListener {
                    // Password reauthentication successful, update the password
                    user.updatePassword(newPassword)
                        .addOnSuccessListener {
                            // Password updated successfully
                            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            // Failed to update password
                            Toast.makeText(this, "Failed to change password: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { exception ->
                    // Failed to reauthenticate user
                    Toast.makeText(this, "Failed to reauthenticate: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}