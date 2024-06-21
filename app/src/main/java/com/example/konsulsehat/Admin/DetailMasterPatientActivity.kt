package com.example.konsulsehat.Admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.konsulsehat.R
import com.example.konsulsehat.SharedViewModel
import com.example.konsulsehat.databinding.ActivityDetailMasterDoctorBinding
import com.example.konsulsehat.databinding.ActivityDetailMasterPatientBinding
import com.google.firebase.firestore.FirebaseFirestore

class DetailMasterPatientActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailMasterPatientBinding
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_master_patient)
        binding = ActivityDetailMasterPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        // Retrieve the logged-in user information passed via Intent
        val loggedInUser = intent.getStringExtra("loggedInUser")
        sharedViewModel.setLoggedInUser(loggedInUser ?: "")
        val email = intent.getStringExtra("email")

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userData = document.data
                    binding.tvFullNameDetailPatient.setText(userData["name"] as? String)
                    binding.tvEmailDetailPatient.setText(userData["email"] as? String)
                    binding.tvPhoneDetailPatient.setText(userData["phoneNum"] as? String ?: "")
                    binding.tvDateDetailPatient.setText(userData["birthdate"] as? String ?: "")

                    val profilePictUrl = document.getString("profile_pict")
                    profilePictUrl?.let {
                        Glide.with(this)
                            .load(it)
                            .into(binding.imgProfileDetailPatient)
                    }
                    val name = userData["name"] as? String
                    Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreData", "Error getting user data: ", exception)
            }
    }
}