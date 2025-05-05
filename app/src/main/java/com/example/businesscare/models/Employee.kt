package com.example.businesscare.models

data class Employee(
    val id: Int,
    val company_id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val telephone: String?,
    val position: String?,
    val departement: String?,
    val date_creation_compte: String?,
    val derniere_connexion: String?,
    val preferences_langue: String?,
    val id_carte_nfc: String?
)