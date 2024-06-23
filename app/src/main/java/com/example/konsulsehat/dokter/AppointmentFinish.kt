package com.example.konsulsehat.dokter

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.bumptech.glide.Glide
import com.example.konsulsehat.Admin.MasterTransaksiAdapter
import com.example.konsulsehat.R
import com.example.konsulsehat.SharedViewModel
import com.google.firebase.firestore.FirebaseFirestore


class AppointmentFinish : Fragment(), AppointmentAdapter.ClickListener {
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var appointmentAdapter: AppointmentAdapter
    private lateinit var userLoggedIn: String
    var patientList = mutableListOf<Map<String, Any>>()
    val db = FirebaseFirestore.getInstance()
    lateinit var a: TextView


   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_appointment_finish, container, false)
       a = view.findViewById(R.id.textView14)

       sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
       sharedViewModel.loggedInUser.observe(viewLifecycleOwner, Observer { loggedInUser ->
           userLoggedIn = loggedInUser.toString()

           fetchAppointments()
       })

       recyclerView = view.findViewById(R.id.rvListAppointmentFinish)
       recyclerView.layoutManager = LinearLayoutManager(context)
       val context = requireActivity()

       return view
    }

    private fun fetchAppointments() {
        val db = FirebaseFirestore.getInstance()

        // Step 1: Fetch appointments
        db.collection("appointment")
            .whereEqualTo("psychiatrist_email", userLoggedIn)
            .whereEqualTo("appointment_status", 4)
            .get()
            .addOnSuccessListener { appointmentResult ->
                // Create a list to hold mutable maps of appointments
                val mutablePatientList = mutableListOf<MutableMap<String, Any>>()

                for (document in appointmentResult) {
                    val appointment = document.data.toMutableMap() // Mutable map to add roomId later
                    mutablePatientList.add(appointment)
                    a.text = "List Appointment " + (appointment["psychiatrist_name"] as String)
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
                            val patientEmail = appointment["patient_email"] as? String
                            if (patientEmail != null && roomChatMap.containsKey(patientEmail)) {
                                appointment["roomId"] = roomChatMap[patientEmail]!!
                            }
                        }

                        // Step 4: Set the adapter
                        appointmentAdapter = AppointmentAdapter(mutablePatientList, this)
                        recyclerView.adapter = appointmentAdapter
                    }
                    .addOnFailureListener { exception ->
                        Log.w("FirestoreData", "Error getting room chat documents: ", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting appointment documents: ", exception)
            }
    }
//    private fun fetchChatDataFromFirestore() {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("appointment")
//            .whereEqualTo("psychiatrist_email", userLoggedIn)
//            .whereEqualTo("appointment_status", 4)
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    val appointment = document.data
//                    patientList.add(appointment)
//                }
//                appointmentAdapter = AppointmentAdapter(patientList,this)
//                recyclerView.adapter = appointmentAdapter
//            }
//            .addOnFailureListener { exception ->
//                Log.w("FirestoreData", "Error getting documents: ", exception)
//            }
//
//    }

    override fun clickedItem(roomId: String) {
        startActivity(Intent(context, ChatRoomDokterActivity::class.java).putExtra("roomId", roomId).putExtra("userLoggedIn", userLoggedIn))
    }
}