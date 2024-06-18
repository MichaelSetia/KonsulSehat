package com.example.konsulsehat.dokter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.konsulsehat.R
import com.example.konsulsehat.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AppointmentUpcoming : Fragment() {

    private lateinit var rvAppointment: RecyclerView
    private val patientList = mutableListOf<Map<String, Any>>()
    private lateinit var appointmentAdapter: AppointmentAdapter
    private var loggedInUser: String? = null
    private lateinit var auth: FirebaseAuth
    lateinit var a:TextView
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userLoggedIn : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_appointment_upcoming, container, false)
        auth = FirebaseAuth.getInstance()

        loggedInUser = arguments?.getString("loggedInUser")

        rvAppointment = view.findViewById(R.id.rvApointment)
        a = view.findViewById(R.id.textView15)


        userLoggedIn = ""

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.loggedInUser.observe(viewLifecycleOwner, Observer { loggedInUser ->
            // Use the logged-in user information
            userLoggedIn = loggedInUser.toString()

            fetchAppointments()
        })


        rvAppointment.layoutManager = LinearLayoutManager(context)





        return view
    }

    private fun fetchAppointments() {
        val db = FirebaseFirestore.getInstance()
        db.collection("appointment")
            .whereEqualTo("Psychiatrist_email", userLoggedIn)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val appointment = document.data
                    patientList.add(appointment)
                    a.text="List Appointment"+appointment["Psychiatrist_name"]as String
                }
                appointmentAdapter = AppointmentAdapter(patientList)
                rvAppointment.adapter = appointmentAdapter
//                appointmentAdapter.notifyDataSetChanged()

            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting documents: ", exception)
            }

    }
}
