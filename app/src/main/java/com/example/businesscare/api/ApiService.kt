package com.example.businesscare.api

import com.example.businesscare.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("login")  // ou "api/login" selon votre configuration Laravel
    suspend fun login(@Body credentials: LoginRequest): Response<LoginResponse>

    @GET("events")
    suspend fun getEvents(@Header("Authorization") token: String): Response<List<Event>>

    @GET("employee/events")
    suspend fun getEmployeeEvents(@Header("Authorization") token: String): Response<List<Event>>

    @POST("events/{id}/register")
    suspend fun registerToEvent(
        @Path("id") eventId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>
}