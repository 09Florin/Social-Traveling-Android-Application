package com.example.testsample2_api34


data class Country(
    val countryName: String?,
    val countryDescription: String?,
    val logo: String?,
    val souvenirList: ArrayList<Souvenir> = ArrayList(),
    var isExpandable: Boolean = false
) {
    // Secondary constructor to handle cases where only the basic parameters are provided
    constructor(countryName: String?, countryDescription: String?, logo: String?) :
            this(countryName, countryDescription, logo, ArrayList(), false)
}