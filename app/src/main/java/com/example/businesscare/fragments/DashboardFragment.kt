package com.example.businesscare.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.businesscare.R
import com.example.businesscare.api.ApiConfig
import com.example.businesscare.api.ApiService
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private lateinit var welcomeTextView: TextView
    private lateinit var upcomingEventsTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        welcomeTextView = view.findViewById(R.id.welcomeTextView)
        upcomingEventsTextView = view.findViewById(R.id.upcomingEventsTextView)

        // Récupérer le nom d'utilisateur depuis SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("BusinessCarePrefs",
            android.content.Context.MODE_PRIVATE)
        val userName = sharedPref.getString("userName", "")

        // Afficher le message de bienvenue
        welcomeTextView.text = "Bienvenue, $userName !"

        // Charger les événements à venir
        loadUpcomingEvents()

        return view
    }

    private fun loadUpcomingEvents() {
        // Récupérer le token des SharedPreferences (si vous l'avez implémenté)
        // Sinon, cette fonction peut fonctionner sans authentification pour le moment

        val sharedPref = requireActivity().getSharedPreferences("BusinessCarePrefs",
            android.content.Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", 0)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val apiService = ApiConfig.retrofit.create(ApiService::class.java)
                val response = apiService.getEmployeeEvents("") // Remplacer par un vrai token si nécessaire

                if (response.isSuccessful && response.body() != null) {
                    val events = response.body()!!

                    if (events.isEmpty()) {
                        upcomingEventsTextView.text = "Aucun événement à venir"
                    } else {
                        val upcomingEventsText = StringBuilder()
                        upcomingEventsText.append("Prochains événements :\n\n")

                        // Limiter à 3 événements pour la lisibilité
                        val eventsToShow = if (events.size > 3) events.take(3) else events

                        for (event in eventsToShow) {
                            upcomingEventsText.append("• ${event.name}\n")
                            upcomingEventsText.append("  Date: ${event.date}\n")
                            upcomingEventsText.append("  Lieu: ${event.location ?: "N/A"}\n\n")
                        }

                        upcomingEventsTextView.text = upcomingEventsText.toString()
                    }
                } else {
                    Log.e("DASHBOARD", "Error: ${response.code()}")
                    upcomingEventsTextView.text = "Impossible de charger les événements"
                }
            } catch (e: Exception) {
                Log.e("DASHBOARD", "Exception: ${e.message}")
                upcomingEventsTextView.text = "Erreur de connexion"
            }
        }
    }
}