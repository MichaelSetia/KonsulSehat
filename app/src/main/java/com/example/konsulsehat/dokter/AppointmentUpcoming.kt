package com.example.konsulsehat.dokter

import android.content.Intent
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

class AppointmentUpcoming : Fragment(), AppointmentAdapter.ClickListener{

    private lateinit var rvAppointment: RecyclerView
    var patientList = mutableListOf<Map<String, Any>>()
    var chatList = mutableListOf<Map<String, Any>>()
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

        // Step 1: Fetch appointments
        db.collection("appointment")
            .whereEqualTo("Psychiatrist_email", userLoggedIn)
            .get()
            .addOnSuccessListener { appointmentResult ->
                // Create a list to hold mutable maps of appointments
                val mutablePatientList = mutableListOf<MutableMap<String, Any>>()

                for (document in appointmentResult) {
                    val appointment = document.data.toMutableMap() // Mutable map to add roomId later
                    mutablePatientList.add(appointment)
                    a.text = "List Appointment " + (appointment["Psychiatrist_name"] as String)
                }

                // Step 2: Fetch room chats after appointments are fetched
                db.collection("room_chat")
                    .get()
                    .addOnSuccessListener { roomChatResult ->
                        // Create a map to store the roomId by patient email
                        val roomChatMap = mutableMapOf<String, String>()

                        for (document in roomChatResult) {
                            val chatData = document.data
                            val documentId = document.id
                            val user_1 = chatData["user_1"] as? String
                            val user_2 = chatData["user_2"] as? String

                            // Map the roomId to the patient if either user_1 or user_2 matches userLoggedIn
                            if (user_1 != null && user_2 != null) {
                                if (user_1 == userLoggedIn) {
                                    roomChatMap[user_2!!] = documentId
                                } else if (user_2 == userLoggedIn) {
                                    roomChatMap[user_1!!] = documentId
                                }
                            }
                        }

                        // Step 3: Update the mutablePatientList with the roomId
                        for (appointment in mutablePatientList) {
                            val patientEmail = appointment["Patient_email"] as? String
                            if (patientEmail != null && roomChatMap.containsKey(patientEmail)) {
                                appointment["roomId"] = roomChatMap[patientEmail]!!
                            }
                        }

                        // Step 4: Set the adapter
                        appointmentAdapter = AppointmentAdapter(mutablePatientList, userLoggedIn, this)
                        rvAppointment.adapter = appointmentAdapter
                    }
                    .addOnFailureListener { exception ->
                        Log.w("FirestoreData", "Error getting room chat documents: ", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting appointment documents: ", exception)
            }
    }

    override fun clickedItem(roomId: String) {
        startActivity(Intent(context, ChatRoomDokterActivity::class.java).putExtra("roomId", roomId).putExtra("userLoggedIn", userLoggedIn))
    }


}
