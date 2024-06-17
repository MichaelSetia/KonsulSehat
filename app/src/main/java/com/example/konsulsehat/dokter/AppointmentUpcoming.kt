package com.example.konsulsehat.dokter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.konsulsehat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentUpcoming : Fragment() {

    private lateinit var rvAppointment: RecyclerView
    private val patientList = mutableListOf<Map<String, Any>>()
    private lateinit var appointmentAdapter: AppointmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_appointment_upcoming, container, false)

        rvAppointment = view.findViewById(R.id.rvApointment)
        rvAppointment.layoutManager = LinearLayoutManager(context)

        appointmentAdapter = AppointmentAdapter(patientList)
        rvAppointment.adapter = appointmentAdapter

        fetchAppointments()

        return view
    }

    private fun fetchAppointments() {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        currentUser?.email?.let { email ->
            db.collection("appointments")
                .whereEqualTo("Psychiatrist_email", email)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val appointment = document.data
                        patientList.add(appointment)
                    }
                    appointmentAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                }
        }
    }
}
