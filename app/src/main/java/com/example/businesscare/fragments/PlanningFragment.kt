package com.example.businesscare.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.businesscare.R
import com.example.businesscare.api.ApiConfig
import com.example.businesscare.api.ApiService
import com.example.businesscare.models.Event
import kotlinx.coroutines.launch

class PlanningFragment : Fragment() {

    private lateinit var planningContainer: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var noPlanningText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_planning, container, false)

        planningContainer = view.findViewById(R.id.planningContainer)
        progressBar = view.findViewById(R.id.progressBar)
        noPlanningText = view.findViewById(R.id.noPlanningText)

        loadPlanning()

        return view
    }

    private fun loadPlanning() {
        progressBar.visibility = View.VISIBLE
        noPlanningText.visibility = View.GONE
        planningContainer.removeAllViews()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val sharedPref = requireActivity().getSharedPreferences("BusinessCarePrefs", Context.MODE_PRIVATE)
                val employeeId = sharedPref.getInt("userId", 0)

                val apiService = ApiConfig.retrofit.create(ApiService::class.java)
                val response = apiService.getEmployeeEvents(employeeId)

                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val events = response.body()!!

                    if (events.isEmpty()) {
                        noPlanningText.visibility = View.VISIBLE
                    } else {
                        val sortedEvents = events.sortedBy { it.date }
                        displayPlanning(sortedEvents)
                    }
                } else {
                    Log.e("PLANNING", "Error: ${response.code()}")
                    noPlanningText.text = "Impossible de charger votre planning"
                    noPlanningText.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Log.e("PLANNING", "Exception: ${e.message}")
                progressBar.visibility = View.GONE
                noPlanningText.text = "Erreur de connexion"
                noPlanningText.visibility = View.VISIBLE
            }
        }
    }

    private fun displayPlanning(events: List<Event>) {
        val eventsByDate = events.groupBy { it.date }

        for ((date, eventsForDate) in eventsByDate) {
            // en-tête pour la date
            val dateHeader = layoutInflater.inflate(R.layout.item_date_header, planningContainer, false)
            val dateTextView = dateHeader.findViewById<TextView>(R.id.dateHeaderText)
            dateTextView.text = formatDate(date)
            planningContainer.addView(dateHeader)

            for (event in eventsForDate) {
                val eventView = layoutInflater.inflate(R.layout.item_planning_event, planningContainer, false)

                val titleTextView = eventView.findViewById<TextView>(R.id.eventTitle)
                val timeTextView = eventView.findViewById<TextView>(R.id.eventTime)
                val locationTextView = eventView.findViewById<TextView>(R.id.eventLocation)
                val cancelButton = eventView.findViewById<TextView>(R.id.cancelButton)

                titleTextView.text = event.name
                timeTextView.text = extractTimeFromDate(date)
                locationTextView.text = event.location ?: "Lieu non spécifié"

                cancelButton.setOnClickListener {
                    unregisterFromEvent(event.id)
                }

                planningContainer.addView(eventView)
            }
        }
    }

    private fun formatDate(dateString: String): String {
        return dateString
    }

    private fun extractTimeFromDate(dateString: String): String {
        // Extraire l'heure de la chaîne de date
        // Exemple simple (à adapter selon le format de votre API)
        return if (dateString.contains(" ")) {
            val parts = dateString.split(" ")
            if (parts.size > 1) parts[1] else "Heure non spécifiée"
        } else {
            "Heure non spécifiée"
        }
    }

    private fun unregisterFromEvent(eventId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val apiService = ApiConfig.retrofit.create(ApiService::class.java)
                val response = apiService.unregisterFromEvent(eventId, "")

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Désinscription réussie", Toast.LENGTH_SHORT).show()
                    loadPlanning()
                } else {
                    Toast.makeText(requireContext(), "Erreur lors de la désinscription", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}