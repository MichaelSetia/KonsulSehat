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
                    Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreData", "Error getting user data: ", exception)
            }

        binding.btnBanDetailDoctor.setOnClickListener{
//            updateUserStatus("non-active")
        }

        binding.btnUnbanDetailDoctor.setOnClickListener {
//            updateUserStatus("active")
        }
    }
//    private fun updateUserStatus(status: String) {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("users").document(userId)
//            .update("status", status)
//            .addOnSuccessListener {
//                Toast.makeText(this, "User status updated to $status", Toast.LENGTH_SHORT).show()
//                if (status == "active") {
//                    binding.btnUnbanDetailDoctor.isEnabled = false
//                    binding.btnBanDetailDoctor.isEnabled = true
//                } else {
//                    binding.btnUnbanDetailDoctor.isEnabled = true
//                    binding.btnBanDetailDoctor.isEnabled = false
//                }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(this, "Error updating status: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
}