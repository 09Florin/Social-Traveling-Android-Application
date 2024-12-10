package com.example.testsample2_api34

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val email: String = "",
    val profilePicture: String = "",
    val userName: String = "",
    val collectedSouvenirs: List<Int> = emptyList()
)

