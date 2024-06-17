package com.example.konsulsehat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userLoggedIn: String
    private lateinit var tvFullName: EditText
    private lateinit var tvEmail: EditText
    private lateinit var tvPhoneNum: EditText
    private lateinit var tvBirthdate: EditText
    private lateinit var tvfotoprofile: ImageView
    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        tvFullName = rootView.findViewById(R.id.tvSearchDoctor)
        tvEmail = rootView.findViewById(R.id.editTextTextEmailAddress)
        tvPhoneNum = rootView.findViewById(R.id.editTextPhone)
        tvBirthdate = rootView.findViewById(R.id.editTextDate)
        tvfotoprofile = rootView.findViewById(R.id.imgProfileAccount)
        btnSave = rootView.findViewById(R.id.button4)


        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.loggedInUser.observe(viewLifecycleOwner, Observer { loggedInUser ->
            userLoggedIn = loggedInUser.toString()

            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .whereEqualTo("email", userLoggedIn)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val userData = document.data
                        tvFullName.setText(userData["name"] as? String)
                        tvEmail.setText(userData["email"] as? String)
                        tvPhoneNum.setText(userData["phoneNum"] as? String ?: "")
                        tvBirthdate.setText(userData["birthdate"] as? String ?: "")

                        val profilePictUrl = document.getString("profile_pict")
                        profilePictUrl?.let {
                            Glide.with(requireContext())
                                .load(it)
                                .into(tvfotoprofile)
                        }
                        val name = userData["name"] as? String
                        Toast.makeText(context, name, Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreData", "Error getting user data: ", exception)
                }
        })

        btnSave.setOnClickListener {
            // Update user data in Firestore
            val updatedName = tvFullName.text.toString().trim()
            val updatedEmail = tvEmail.text.toString().trim()
            val updatedPhoneNum = tvPhoneNum.text.toString().trim()
            val updatedBirthdate = tvBirthdate.text.toString().trim()

            // Validate if all fields are filled
            if (updatedName.isNotEmpty() && updatedEmail.isNotEmpty() && updatedPhoneNum.isNotEmpty() && updatedBirthdate.isNotEmpty()) {
                // Update Firestore document
                val db = FirebaseFirestore.getInstance()
                db.collection("users")
                    .whereEqualTo("email", userLoggedIn)
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
                                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }
}
