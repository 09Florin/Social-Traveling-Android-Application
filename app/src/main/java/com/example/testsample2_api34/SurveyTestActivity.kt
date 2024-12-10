package com.example.testsample2_api34

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SurveyTestActivity : AppCompatActivity() {
    // Define variables to store user preferences
    private var cityAttraction = 0
    private var cultural = 0
    private var historical = 0
    private var natural = 0
    private var popCulture = 0
    private var other = 0

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_test)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Find the "Done" button
        val doneButton: Button = findViewById(R.id.done_button)
        doneButton.setOnClickListener {// if and only if he completed the test
            // Redirect the user to the main activity
            handleSurveyAnswers()
            updateUserDocument()
            Toast.makeText(this, "Computing the survey results, please wait!", Toast.LENGTH_LONG).show()
            Handler(Looper.getMainLooper()).postDelayed({
            }, 4000) // 4 seconds delay
            startActivity(Intent(this@SurveyTestActivity, MainActivity::class.java))
            finish() // Finish the survey activity
        }
    }

    // Function to handle calculation based on user answers
    private fun handleSurveyAnswers() {
        // Example calculation for question 1
        // Assuming checkboxes for question 1 are checkbox1_option1, checkbox1_option2, etc.
        val checkbox1Option1 = findViewById<CheckBox>(R.id.checkbox1_option1)
        val checkbox1Option2 = findViewById<CheckBox>(R.id.checkbox1_option2)
        val checkbox1Option3 = findViewById<CheckBox>(R.id.checkbox1_option3)
        val checkbox1Option4 = findViewById<CheckBox>(R.id.checkbox1_option4)
        val checkbox1Option5 = findViewById<CheckBox>(R.id.checkbox1_option5)

        val checkbox2Option1 = findViewById<CheckBox>(R.id.checkbox2_option1)
        val checkbox2Option2 = findViewById<CheckBox>(R.id.checkbox2_option2)
        val checkbox2Option3 = findViewById<CheckBox>(R.id.checkbox2_option3)
        val checkbox2Option4 = findViewById<CheckBox>(R.id.checkbox2_option4)
        val checkbox2Option5 = findViewById<CheckBox>(R.id.checkbox2_option5)

        val checkbox3Option1 = findViewById<CheckBox>(R.id.checkbox3_option1)
        val checkbox3Option2 = findViewById<CheckBox>(R.id.checkbox3_option2)
        val checkbox3Option3 = findViewById<CheckBox>(R.id.checkbox3_option3)
        val checkbox3Option4 = findViewById<CheckBox>(R.id.checkbox3_option4)
        val checkbox3Option5 = findViewById<CheckBox>(R.id.checkbox3_option5)

        val radioButton41 = findViewById<RadioButton>(R.id.radioButton4_1)
        val radioButton42 = findViewById<RadioButton>(R.id.radioButton4_2)
        val radioButton43 = findViewById<RadioButton>(R.id.radioButton4_3)
        val radioButton44 = findViewById<RadioButton>(R.id.radioButton4_4)
        val radioButton45 = findViewById<RadioButton>(R.id.radioButton4_5)

        val radioButton51 = findViewById<RadioButton>(R.id.radioButton5_1)
        val radioButton52 = findViewById<RadioButton>(R.id.radioButton5_2)
        val radioButton53 = findViewById<RadioButton>(R.id.radioButton5_3)
        val radioButton54 = findViewById<RadioButton>(R.id.radioButton5_4)
        val radioButton55 = findViewById<RadioButton>(R.id.radioButton5_5)

        val radioButton61 = findViewById<RadioButton>(R.id.radioButton6_1)
        val radioButton62 = findViewById<RadioButton>(R.id.radioButton6_2)
        val radioButton63 = findViewById<RadioButton>(R.id.radioButton6_3)
        val radioButton64 = findViewById<RadioButton>(R.id.radioButton6_4)
        val radioButton65 = findViewById<RadioButton>(R.id.radioButton6_5)

        val radioButton71 = findViewById<RadioButton>(R.id.radioButton7_1)
        val radioButton72 = findViewById<RadioButton>(R.id.radioButton7_2)
        val radioButton73 = findViewById<RadioButton>(R.id.radioButton7_3)
        val radioButton74 = findViewById<RadioButton>(R.id.radioButton7_4)
        val radioButton75 = findViewById<RadioButton>(R.id.radioButton7_5)

        // Check if each checkbox is checked and update variables accordingly
        // Question 1
        if (checkbox1Option1.isChecked) {
            natural++
            other++
        }
        if (checkbox1Option2.isChecked) {
            natural +=2
        }
        if (checkbox1Option3.isChecked) {
            cityAttraction +=2
            cultural++
        }
        if (checkbox1Option4.isChecked) {
            historical++
            natural++
            popCulture++
            cultural++
        }
        if (checkbox1Option5.isChecked) {
            historical +=2
            cultural++
        }

        // Question 2
        if (checkbox2Option1.isChecked) {
            other +=2
        }
        if (checkbox2Option2.isChecked) {
            natural +=2
            cityAttraction +=2
            cultural +=2
            historical++
        }
        if (checkbox2Option3.isChecked) {
            historical +=2
            popCulture +=2
        }
        if (checkbox2Option4.isChecked) {
            cultural +=2
            natural++
        }
        if (checkbox2Option5.isChecked) {
            historical +=2
            popCulture++
            natural++
        }

        // Question 3
        if (checkbox3Option1.isChecked) {
            popCulture +=2
            historical +=2
            other++
        }
        if (checkbox3Option2.isChecked) {
            cultural +=3
        }
        if (checkbox3Option3.isChecked) {
            other++
            popCulture +=2
            cityAttraction++
        }
        if (checkbox3Option4.isChecked) {
            cityAttraction +=2
            historical +=2
            natural +=2
            other++
        }
        if (checkbox3Option5.isChecked) {
            historical +=3
        }

        //Question 4
        if(radioButton41.isChecked){
            historical +=3
        }
        if(radioButton42.isChecked){
            natural +=3
        }
        if(radioButton43.isChecked){
            cityAttraction +=3
        }
        if(radioButton44.isChecked){
            cultural +=3
        }
        if(radioButton45.isChecked){
            other +=2
            cityAttraction +=2
            historical +=2
        }

        //Question 5
        if(radioButton51.isChecked){
            historical++
            popCulture++
            cityAttraction++
        }
        if(radioButton52.isChecked){
            historical++
            popCulture++
            cityAttraction++
            cultural++
        }
        if(radioButton53.isChecked){
            historical++
            popCulture++
            cityAttraction++
            cultural++
            natural++
        }
        if(radioButton54.isChecked){
            historical++
            popCulture++
            cityAttraction++
            cultural++
            natural++
            other +=2
        }
        if(radioButton55.isChecked){
            historical++
            popCulture +=2
            cityAttraction++
            cultural++
            natural++
            other +=3
        }

        //Question 6
        if(radioButton61.isChecked){
            natural +=2
        }
        if(radioButton62.isChecked){
            cultural +=2
        }
        if(radioButton63.isChecked){
            historical +=2
        }
        if(radioButton64.isChecked){
            cityAttraction +=2
        }
        if(radioButton65.isChecked){
            popCulture +=2
        }

        //Question 7
        if(radioButton71.isChecked){
            historical +=4
        }
        if(radioButton72.isChecked){
            cityAttraction +=3
            popCulture +=2
        }
        if(radioButton73.isChecked){
            natural +=4
        }
        if(radioButton74.isChecked){
            popCulture +=4
        }
        if(radioButton75.isChecked){
            popCulture +=2
            cityAttraction +=4
            other +=4
        }
        // Log or display values of each variable
        Log.d("SurveyResults", "City Attraction: $cityAttraction")
        Log.d("SurveyResults", "Cultural: $cultural")
        Log.d("SurveyResults", "Historical: $historical")
        Log.d("SurveyResults", "Natural: $natural")
        Log.d("SurveyResults", "Pop Culture: $popCulture")
        Log.d("SurveyResults", "Other: $other")

        // Determine the highest category
        val categories = mapOf(
            "CityAttraction" to cityAttraction,
            "Cultural" to cultural,
            "Historical" to historical,
            "Natural" to natural,
            "PopCulture" to popCulture,
            "Other" to other
        )

        val highestCategory = categories.maxByOrNull { it.value }?.key
        val highestValue = categories.maxByOrNull { it.value }?.value ?: 0

        // Integrate the highest category into the user's document
        // You can replace this with your actual logic to update the user's document
        Log.d("SurveyResults", "Highest Category: $highestCategory")
        Log.d("SurveyResults", "Highest Value: $highestValue")

        // If the difference between the highest and second-highest values is low, consider adding multiple categories
        val secondHighestValue = categories
            .filter { it.key != highestCategory }
            .maxByOrNull { it.value }?.value ?: 0

        val difference = highestValue - secondHighestValue
        if (difference <= 3) {
            // Add both highest and second-highest categories
            val secondHighestCategory = categories
                .filter { it.key != highestCategory }
                .maxByOrNull { it.value }?.key

            Log.d("SurveyResults", "Second Highest Category: $secondHighestCategory")
            // Integrate both highest and second-highest categories into the user's document
        }
    }

    private fun updateUserDocument() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val uid = user.uid
            val userRef = firestore.collection("Users").document(uid)

            // Update surveyTaken status to true
            userRef.update("surveyTaken", true)
                .addOnSuccessListener {
                    Log.d("SurveyTestActivity", "SurveyTaken status updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("SurveyTestActivity", "Error updating surveyTaken status", e)
                }

            // Update travelerType values according to survey results
            val travelerTypeUpdates = mapOf(
                "travelerTypeCityAttraction" to cityAttraction,
                "travelerTypeCultural" to cultural,
                "travelerTypeHistorical" to historical,
                "travelerTypeNatural" to natural,
                "travelerTypePopCulture" to popCulture,
                "travelerTypeOther" to other
            )

            userRef.update(travelerTypeUpdates)
                .addOnSuccessListener {
                    Log.d("SurveyTestActivity", "TravelerType values updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("SurveyTestActivity", "Error updating TravelerType values", e)
                }
        }
    }
}