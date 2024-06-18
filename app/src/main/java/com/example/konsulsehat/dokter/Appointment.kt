package com.example.konsulsehat.dokter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.konsulsehat.R
import com.example.konsulsehat.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class Appointment : Fragment() {

    private var loggedInUser: String? = null
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var btmNavView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_appointment, container, false)

        // Retrieve the loggedInUser from arguments
        loggedInUser = arguments?.getString("loggedInUser")

        // Initialize BottomNavigationView
        btmNavView = rootView.findViewById(R.id.BottomNavigationView1)

        btmNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnUpcoming -> {
                    loadFragment(AppointmentUpcoming())
                    true
                }
                R.id.btnFinished -> {
                    loadFragment(AppointmentFinish())
                    true
                }
                else -> false
            }
        }

        // Set default selected item
        btmNavView.selectedItemId = R.id.btnUpcoming

        return rootView
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView3, fragment)
        transaction.commit()
    }
}
