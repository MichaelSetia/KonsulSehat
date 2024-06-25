package com.example.konsulsehat.Admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.konsulsehat.R
import com.example.konsulsehat.SharedViewModel
import com.example.konsulsehat.databinding.ActivityDetailMasterDoctorBinding
import com.example.konsulsehat.databinding.ActivityFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore

class DetailMasterDoctorActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailMasterDoctorBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var emailTxt: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_master_doctor)
        binding = ActivityDetailMasterDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("email")
        emailTxt=findViewById(R.id.tvEmailDetailDoctor)

//        emailTxt.setText(email)

        binding.btnBackToMasterDoctor.setOnClickListener {
            finish()
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userData = document.data
                    binding.tvFullNameDetailDoctor.setText(userData["name"] as? String)
                    binding.tvEmailDetailDoctor.setText(userData["email"] as? String)
                    binding.tvPhoneDetailDoctor.setText(userData["phoneNum"] as? String ?: "")
                    binding.tvDateDetailDoctor.setText(userData["birthdate"] as? String ?: "")
                    binding.tvRateDetailDoctor.setText(userData["rate"] as? String ?: "")
                    binding.tvDeskripsiDetailDoctor.setText(userData["deskripsi"] as? String ?: "")
                    binding.tvExperienceDetailDoctor.setText(userData["experiance"] as? String ?: "")
                    if (userData["status"]=="active"){
                        binding.btnUnbanDetailDoctor.isEnabled=false
                        binding.btnBanDetailDoctor.isEnabled=true
                    }
                    else{
                        binding.btnUnbanDetailDoctor.isEnabled=true
                        binding.btnBanDetailDoctor.isEnabled=false
                    }
                    val profilePictUrl = document.getString("profile_pict")
                    profilePictUrl?.let {
                        Glide.with(this)
                            .load(it)
                            .into(binding.imgProfileDetailDoctor)
                    }
                    val name = userData["name"] as? String
//                    Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreData", "Error getting user data: ", exception)
            }

        binding.btnBanDetailDoctor.setOnClickListener{
            if (email != null) {
                updateUserStatus("non-active",email)
            }
        }

        binding.btnUnbanDetailDoctor.setOnClickListener {
            if (email != null) {
                updateUserStatus("active",email)
            }
        }

        binding.btnSaveEditDetailDoctor.setOnClickListener {
            val updatedName = binding.tvFullNameDetailDoctor.text.toString().trim()
            val updatedEmail = binding.tvEmailDetailDoctor.text.toString().trim()
            val updatedPhoneNum = binding.tvPhoneDetailDoctor.text.toString().trim()
            val updatedBirthdate = binding.tvDateDetailDoctor.text.toString().trim()
            val updatedRate = binding.tvRateDetailDoctor.text.toString().trim()
            val updatedDeskripsi = binding.tvDeskripsiDetailDoctor.text.toString().trim()
            val updatedExperiance = binding.tvExperienceDetailDoctor.text.toString().trim()

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
                                "birthdate" to updatedBirthdate,
                                "rate" to updatedRate,
                                "deskripsi" to updatedDeskripsi,
                                "experiance" to updatedExperiance
                            ))
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Detail Doctor updated successfully", Toast.LENGTH_SHORT).show()
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
                            if (status == "active") {
                                binding.btnUnbanDetailDoctor.isEnabled = false
                                binding.btnBanDetailDoctor.isEnabled = true
                            } else {
                                binding.btnUnbanDetailDoctor.isEnabled = true
                                binding.btnBanDetailDoctor.isEnabled = false
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