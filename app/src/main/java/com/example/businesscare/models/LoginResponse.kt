package com.example.businesscare.models

data class LoginResponse(
    val success: Boolean,
    val user: UserData?,
    val message: String? = null
)

data class UserData(
    val id: Int,
    val email: String,
    val name: String,
    val type: String,
    val company_id: Int? = null
)