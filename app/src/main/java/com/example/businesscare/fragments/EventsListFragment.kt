package com.example.businesscare.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

class EventsListFragment : Fragment() {

    private lateinit var eventsContainer: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var noEventsText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_events_list, container, false)

        eventsContainer = view.findViewById(R.id.eventsContainer)
        progressBar = view.findViewById(R.id.progressBar)
        noEventsText = view.findViewById(R.id.noEventsText)

        loadEvents()

        return view
    }

    private fun loadEvents() {
        progressBar.visibility = View.VISIBLE
        noEventsText.visibility = View.GONE
        eventsContainer.removeAllViews()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val apiService = ApiConfig.retrofit.create(ApiService::class.java)
                val response = apiService.getEvents("")

                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val events = response.body()!!

                    if (events.isEmpty()) {
                        noEventsText.visibility = View.VISIBLE
                    } else {
                        displayEvents(events)
                    }
                } else {
                    Log.e("EVENTS", "Error: ${response.code()}")
                    noEventsText.text = "Impossible de charger les événements"
                    noEventsText.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Log.e("EVENTS", "Exception: ${e.message}")
                progressBar.visibility = View.GONE
                noEventsText.text = "Erreur de connexion"
                noEventsText.visibility = View.VISIBLE
            }
        }
    }

    private fun displayEvents(events: List<Event>) {
        for (event in events) {
            val eventView = layoutInflater.inflate(R.layout.item_event, eventsContainer, false)

            val titleTextView = eventView.findViewById<TextView>(R.id.eventTitle)
            val dateTextView = eventView.findViewById<TextView>(R.id.eventDate)
            val descriptionTextView = eventView.findViewById<TextView>(R.id.eventDescription)
            val locationTextView = eventView.findViewById<TextView>(R.id.eventLocation)
            val registerButton = eventView.findViewById<Button>(R.id.registerButton)

            titleTextView.text = event.name
            dateTextView.text = "Date: ${event.date}"
            descriptionTextView.text = event.description ?: "Pas de description"
            locationTextView.text = "Lieu: ${event.location ?: "N/A"}"

            registerButton.setOnClickListener {
                registerToEvent(event.id)
            }

            eventsContainer.addView(eventView)
        }
    }

    private fun registerToEvent(eventId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val apiService = ApiConfig.retrofit.create(ApiService::class.java)
                val response = apiService.registerToEvent(eventId, "")

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Inscription réussie", Toast.LENGTH_SHORT).show()
                    // Recharger la liste des événements
                    loadEvents()
                } else {
                    Toast.makeText(requireContext(), "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}