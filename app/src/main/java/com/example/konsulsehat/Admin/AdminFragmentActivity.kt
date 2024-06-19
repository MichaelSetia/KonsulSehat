package com.example.konsulsehat.Admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.konsulsehat.ChatFragment
import com.example.konsulsehat.R
import com.example.konsulsehat.SharedViewModel
import com.example.konsulsehat.databinding.ActivityAdminFragmentBinding
import com.google.android.material.navigation.NavigationBarView

class AdminFragmentActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminFragmentBinding
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        // Retrieve the logged-in user information passed via Intent
        val loggedInUser = intent.getStringExtra("loggedInUser")
        sharedViewModel.setLoggedInUser(loggedInUser ?: "")

        // Load the initial fragment
        loadFragment(ChatFragment())

        // Load the default fragment on startup
        if (savedInstanceState == null) {
            loadFragment(MasterUserFragment())
        }

        binding.bottomNavigationAdmin.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnMUser -> {
                    loadFragment(MasterUserFragment())
                    true
                }
                R.id.btnMAppointment -> {
                    loadFragment(MasterDoctorFragment())
                    true
                }
                R.id.btnMTransaksi -> {
                    loadFragment(MasterTransaksiFragment())
                    true
                }

                else -> false
            }
        })

        // Set default selected item
        binding.bottomNavigationAdmin.selectedItemId = R.id.btnMUser
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView5, fragment)
        transaction.commit()
    }

}