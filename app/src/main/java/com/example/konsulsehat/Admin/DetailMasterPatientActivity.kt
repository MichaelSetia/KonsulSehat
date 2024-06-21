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
                    if (userData["status"]=="active"){
                        binding.btnUnbanDetailPatient.isEnabled=false
                        binding.btnBanDetailPatient.isEnabled=true
                    }
                    else{
                        binding.btnUnbanDetailPatient.isEnabled=true
                        binding.btnBanDetailPatient.isEnabled=false
                    }
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

        binding.btnSaveEditDetailPatient.setOnClickListener {
            val updatedName = binding.tvFullNameDetailPatient.text.toString().trim()
            val updatedEmail = binding.tvEmailDetailPatient.text.toString().trim()
            val updatedPhoneNum = binding.tvPhoneDetailPatient.text.toString().trim()
            val updatedBirthdate = binding.tvDateDetailPatient.text.toString().trim()

            if (updatedName.isNotEmpty() && updatedEmail.isNotEmpty() && updatedPhoneNum.isNotEmpty() && updatedBirthdate.isNotEmpty()) {
                val db = FirebaseFirestore.getInstance()
                db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            // Update fields
                            val userRef = db.collection("users").document(document.id)
                            userRef.update(mapOf(
                                "name" to updatedName,
                                "email" to updatedEmail,
                                "phoneNum" to updatedPhoneNum,
                                "birthdate" to updatedBirthdate
                            ))
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("FirestoreData", "Error updating document", e)
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("FirestoreData", "Error getting user data: ", exception)
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBanDetailPatient.setOnClickListener{
            if (email != null) {
                updateUserStatus("non-active",email)
            }
        }

        binding.btnUnbanDetailPatient.setOnClickListener {
            if (email != null) {
                updateUserStatus("active",email)
            }
        }
    }
    private fun updateUserStatus(status: String, email: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (document in documents) {
                    val userId = document.id
                    db.collection("users").document(userId)
                        .update("status", status)
                        .addOnSuccessListener {
                            Toast.makeText(this, "User status updated to $status", Toast.LENGTH_SHORT).show()
                            if (status=="active"){
                                binding.btnUnbanDetailPatient.isEnabled=false
                                binding.btnBanDetailPatient.isEnabled=true
                            }
                            else{
                                binding.btnUnbanDetailPatient.isEnabled=true
                                binding.btnBanDetailPatient.isEnabled=false
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error updating status: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error finding user: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}