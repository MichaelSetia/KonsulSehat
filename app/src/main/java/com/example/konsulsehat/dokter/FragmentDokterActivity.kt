package com.example.konsulsehat.dokter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.konsulsehat.ChatFragment
import com.example.konsulsehat.HomeFragment
import com.example.konsulsehat.ProfileFragment
import com.example.konsulsehat.R
import com.example.konsulsehat.SearchFragment
import com.example.konsulsehat.SharedViewModel
import com.example.konsulsehat.databinding.ActivityFragmentBinding
import com.example.konsulsehat.databinding.ActivityFragmentDokter2Binding
import com.google.android.material.navigation.NavigationBarView

class FragmentDokterActivity : AppCompatActivity() {
    lateinit var binding: ActivityFragmentDokter2Binding
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentDokter2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        // Retrieve the logged-in user information passed via Intent
        val loggedInUser = intent.getStringExtra("loggedInUser")
        sharedViewModel.setLoggedInUser(loggedInUser ?: "")

        loadFragment(ChatFragment())

        // Load the default fragment on startup
        if (savedInstanceState == null) {
            loadFragment(Appointment())
        }

        binding.bttomnav.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnAppointment -> {
                    loadFragment(Appointment())
                    true
                }
                R.id.btnPatientList -> {
                    loadFragment(ListPatienFragment())
                    true
                }
                R.id.profile_menu -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        })

        // Set default selected item
        binding.bttomnav.selectedItemId = R.id.btnAppointment
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView4, fragment)
        transaction.commit()
    }

}