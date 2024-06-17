package com.example.konsulsehat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class BookingFragment : Fragment() {
    private lateinit var imgDok: ImageView
    private lateinit var namaDok: TextView
    private lateinit var gelarDok: TextView
    private lateinit var deskDok: TextView
    private lateinit var rateDok: TextView
    private lateinit var btnCancel:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_booking, container, false)

        // Initialize views
        imgDok = view.findViewById(R.id.imageView10)
        namaDok = view.findViewById(R.id.tvBookingNama)
        gelarDok = view.findViewById(R.id.tvBookingGelar)
        deskDok = view.findViewById(R.id.tvBookingDeskripsi)
        rateDok = view.findViewById(R.id.tvBookingStar)
        btnCancel = view.findViewById(R.id.btnBookingCancel)

        btnCancel.setOnClickListener{
            requireActivity().onBackPressed()
        }
        val email = arguments?.getString("email")
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Handle document data
                    val nama = document.getString("name")
                    val gelar = document.getString("gelar")
                    val deskripsi = document.getString("deskripsi")
                    val rating = document.getDouble("rating")
                    val profilePictUrl = document.getString("profile_pict")

                    namaDok.text = nama
                    gelarDok.text = "Dr"
                    deskDok.text = deskripsi
                    rateDok.text = "5"

                    // Load image using Glide
                    profilePictUrl?.let {
                        Glide.with(requireContext())
                            .load(it)
                            .into(imgDok)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting documents: ", exception)
            }


        return view
    }
}
