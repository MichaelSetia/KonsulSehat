package com.example.konsulsehat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.konsulsehat.databinding.ActivityFragmentBinding
import com.google.android.material.navigation.NavigationBarView


class FragmentActivity : AppCompatActivity() {
    lateinit var binding: ActivityFragmentBinding
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        // Retrieve the logged-in user information passed via Intent
        val loggedInUser = intent.getStringExtra("loggedInUser")
        sharedViewModel.setLoggedInUser(loggedInUser ?: "")

        // Load the initial fragment
        loadFragment(ChatFragment())

        // Load the default fragment on startup
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_menu -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.search_menu -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.chat_menu -> {
                    loadFragment(ChatFragment())
                    true
                }
                R.id.journal_menu -> {
                    loadFragment(JournalFragment())
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
        binding.bottomNavigation.selectedItemId = R.id.home_menu
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView2, fragment)
        transaction.commit()
    }

}

