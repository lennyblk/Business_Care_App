package com.example.businesscare.models

data class EventRegistration(
    val id: Int,
    val event_id: Int,
    val employee_id: Int,
    val registration_date: String,
    val status: String // "Confirmed", "Canceled", "Waiting"
)