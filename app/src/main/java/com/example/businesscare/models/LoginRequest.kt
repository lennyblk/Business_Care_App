package com.example.businesscare.models

data class LoginRequest(
    val email: String,
    val password: String,
    val user_type: String,
    val company_name: String
)