package com.example.testsample2_api34

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Souvenir(
    val souvenirName: String = "",
    val souvenirLogo: String = "",
    val souvenirCountry: String = "",
    val souvenirDescription: String = ""
) {
    // Add a no-argument constructor
    constructor() : this("", "", "", "")
}

