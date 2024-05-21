package com.example.konsulsehat.loginRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.konsulsehat.R
import com.example.konsulsehat.databinding.ActivityLandingBinding

class LandingActivity : AppCompatActivity() {
    private var listTitle = listOf<String>("Never miss an appointment!", "Instantly schedule appointments", "Keep patient records with you !")
    private var listDesc = listOf<String>("App will let you know about all upcoming appointments on time.", "Quickly schedule appointments with easy user interface.", "App has unique feature to create patient profile and keep all related documents.")
    private var listImg = listOf<Int>(R.drawable.landing_page_1, R.drawable.landing_page_2, R.drawable.landing_page_3)

//    lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var binding: ActivityLandingBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager2.adapter = ViewPagerAdapter(listTitle, listDesc, listImg)
        binding.viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.indicator.setViewPager(binding.viewPager2)

        binding.btnLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnRegister.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}