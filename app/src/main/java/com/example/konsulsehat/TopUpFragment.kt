package com.example.konsulsehat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class TopUpFragment : DialogFragment() {
    private lateinit var txtTopUpAmount:EditText
    private lateinit var btnConfirmTopUp:Button
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userLoggedIn: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_top_up, container, false)

        btnConfirmTopUp = rootView.findViewById(R.id.btnConfirmTopUp)
        txtTopUpAmount = rootView.findViewById(R.id.txtTopUpAmount)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnConfirmTopUp.setOnClickListener {
            sharedViewModel.loggedInUser.observe(viewLifecycleOwner, Observer { loggedInUser ->
                userLoggedIn = loggedInUser.toString()

                val db = FirebaseFirestore.getInstance()
                db.collection("users")
                    .whereEqualTo("email", userLoggedIn)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val userData = document.data
                            val saldo = userData["saldo"] as Number
                            var updatedSaldo = saldo.toInt() + txtTopUpAmount.text.toString().toInt()

                            val userRef = db.collection("users").document(document.id)

                            userRef.update(mapOf(
                                "saldo" to updatedSaldo,
                            ))
                            .addOnSuccessListener {
                                sharedViewModel.setSaldo(updatedSaldo.toLong())
                                Toast.makeText(requireContext(), "Top Up Saldo Success!", Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                            .addOnFailureListener { e ->
                                Log.e("FirestoreData", "Top Up Saldo Error", e)
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("FirestoreData", "Error getting user data: ", exception)
                    }
            })
        }
    }
}