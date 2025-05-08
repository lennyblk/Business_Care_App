package com.example.businesscare.api

import com.example.businesscare.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): Response<LoginResponse>

    @GET("events")
    suspend fun getAllEvents(): Response<List<Event>>

    // Endpoint pour les événements d'un employé spécifique
    @GET("employee/events")
    suspend fun getEmployeeEvents(
        @Header("Employee-Id") employeeId: Int
    ): Response<List<Event>>


    @POST("events/{id}/register")
    suspend fun registerToEvent(
        @Path("id") eventId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    // Récupère les inscriptions d'un employé
    @GET("employee/{employeeId}/registrations")
    suspend fun getEmployeeRegistrations(
        @Path("employeeId") employeeId: Int,
        @Header("Authorization") token: String
    ): Response<List<EventRegistration>>

    // Nouvel endpoint pour vérifier une inscription spécifique
    @GET("events/{eventId}/is-registered/{employeeId}")
    suspend fun isEmployeeRegistered(
        @Path("eventId") eventId: Int,
        @Path("employeeId") employeeId: Int,
        @Header("Authorization") token: String
    ): Response<Boolean>

    @DELETE("events/{id}/unregister")
    suspend fun unregisterFromEvent(
        @Path("id") eventId: Int,
        @Header("Authorization") token: String
    ): Response<Unit>
}