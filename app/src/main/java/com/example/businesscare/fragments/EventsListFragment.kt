package com.example.businesscare.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.businesscare.R
import com.example.businesscare.api.ApiConfig
import com.example.businesscare.api.ApiService
import com.example.businesscare.databinding.FragmentEventsListBinding
import com.example.businesscare.models.Event
import kotlinx.coroutines.launch

class EventsListFragment : Fragment() {
    private var _binding: FragmentEventsListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadEventsWithRegistrations()
    }

    private fun loadEventsWithRegistrations() {
        binding.progressBar.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val (token, employeeId) = getAuthCredentials()
                Log.e("AUTH", "Token: ${token.take(5)}...${token.takeLast(5)}")

                val apiService = ApiConfig.retrofit.create(ApiService::class.java)
                val events = apiService.getAllEvents().body() ?: emptyList()

                Log.e("EVENTS", "Reçu ${events.size} événements")
                events.forEach { Log.d("EVENT_DETAIL", it.toString()) }

                if (events.isEmpty()) {
                    showError("Aucun événement trouvé (même en mode test)")
                } else {
                    displayEvents(events, emptySet())
                }
            } catch (e: Exception) {
                Log.e("LOAD_ERROR", "Erreur", e)
                showError("Erreur technique. Voir logs.")
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun getAuthCredentials(): Pair<String, Int> {
        val sharedPref = requireActivity().getSharedPreferences("BusinessCarePrefs", Context.MODE_PRIVATE)
        return Pair(
            "Bearer ${sharedPref.getString("authToken", "")}",
            sharedPref.getInt("userId", 0)
        )
    }

    private fun displayEvents(events: List<Event>, registeredEventIds: Set<Int>) {
        // Implémentez la logique d'affichage ici
        // Exemple basique :
        binding.noEventsText.visibility = View.GONE
        // Ajoutez votre logique RecyclerView ici
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.noEventsText.visibility = View.VISIBLE
        binding.noEventsText.text = message
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}