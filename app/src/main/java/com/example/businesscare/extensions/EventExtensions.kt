package com.example.businesscare.extensions

import com.example.businesscare.api.ApiService
import com.example.businesscare.models.Event
import retrofit2.Response

suspend fun Event.isUserRegistered(
    employeeId: Int,
    apiService: ApiService,
    token: String
): Boolean {
    return try {
        val response = apiService.isEmployeeRegistered(this.id, employeeId, token)
        response.isSuccessful && response.body() == true
    } catch (e: Exception) {
        false
    }
}