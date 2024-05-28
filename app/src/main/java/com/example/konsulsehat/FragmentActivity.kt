package com.example.konsulsehat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.konsulsehat.databinding.ActivityFragmentBinding


class FragmentActivity : AppCompatActivity() {
    lateinit var binding: ActivityFragmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_menu -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.search_menu-> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.chat_menu-> {
                    loadFragment(ChatFragment())
                    true
                }
                R.id.profile_menu-> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false

            }
        }
    }
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}
