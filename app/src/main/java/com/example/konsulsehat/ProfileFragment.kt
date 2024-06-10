package com.example.konsulsehat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userLoggedIn: String
    private lateinit var tvFullName: EditText
    private lateinit var tvEmail: EditText
    private lateinit var tvPhoneNum: EditText
    private lateinit var tvBirthdate: EditText

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

                        val name = userData["name"] as? String
                        Toast.makeText(context, name, Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreData", "Error getting user data: ", exception)
                }
        })

        return rootView
    }
}
