package com.example.businesscare.models

data class Event(
    val id: Int,
    val name: String,
    val description: String?,
    val date: String,
    val event_type: String,
    val provider_id: Int?,
    val capacity: Int?,
    val location: String?,
    val registrations: Int?,
    val company_id: Int?
)