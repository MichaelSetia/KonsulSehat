package com.example.konsulsehat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.konsulsehat.Admin.AdminFragmentActivity
import com.example.konsulsehat.databinding.FragmentHomeBinding
import com.example.konsulsehat.dokter.AppointmentAdapter
import com.example.konsulsehat.dokter.ChatRoomDokterActivity
import com.example.konsulsehat.dokter.FragmentDokterActivity
import com.example.konsulsehat.loginRegister.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class HomeFragment : Fragment(), AppointmentUserAdapter.ClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val cloudDB = Firebase.firestore
    val db = FirebaseFirestore.getInstance()

    var appointmentList = mutableListOf<Map<String, Any>>()
    private lateinit var rvAppointment: RecyclerView
    private lateinit var appointmentAdapter: AppointmentUserAdapter
    private lateinit var rvFinished: RecyclerView
    private lateinit var appointmentFinishedAdapter: AppointmentFinishedAdapter
    private lateinit var userLoggedIn : String
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        userLoggedIn = ""

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.loggedInUser.observe(viewLifecycleOwner, Observer { loggedInUser ->
            // Use the logged-in user information
            userLoggedIn = loggedInUser.toString()

            fetchAppointments()
            fetchAppointmentsFinished()
        })

        rvAppointment = binding.rvAppointmentUser
        rvFinished = binding.rvFinishedAppointmentUser

        rvAppointment.layoutManager = LinearLayoutManager(context)
        rvFinished.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        displayUserName(currentUser)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

//        binding.btnLogout.setOnClickListener {
//            signOutAndStartSignInActivity()
//        }
        updateAppointmentStatusIfOverdue()
    }

    private fun displayUserName(currentUser: FirebaseUser?) {
        currentUser?.let {
            var userName:String = ""
            if (it.displayName?.isEmpty() == false){
                userName = it.displayName!!
            }
            else {
                db.collection("users").whereEqualTo("email", it.email).get()
                    .addOnSuccessListener { documents ->
                        val document = documents.documents[0]
                        if (!documents.isEmpty) {
                            userName = document.getString("name") ?: "User"
                        } else {
                            // Debug log
                            Log.d("SignInActivity", "No document found for email: ${document.getString("email")!!}")
                            Toast.makeText(requireContext(), "No such document!", Toast.LENGTH_LONG).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Debug log
                        Log.e("SignInActivity", "Failed to fetch role", exception)
                        Toast.makeText(requireContext(), "Failed to fetch role: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
            }

            binding.tvUname.text = "$userName!"
        }
    }

    private fun fetchAppointments() {
        val db = FirebaseFirestore.getInstance()

        // Step 1: Fetch appointments
        db.collection("appointment")
            .whereEqualTo("patient_email", userLoggedIn)
            .whereEqualTo("appointment_status", 2)
            .get()
            .addOnSuccessListener { appointmentResult ->
                // Create a list to hold mutable maps of appointments
                val mutableDoctorList = mutableListOf<MutableMap<String, Any>>()

                for (document in appointmentResult) {
                    val appointment = document.data.toMutableMap() // Mutable map to add roomId later
                    mutableDoctorList.add(appointment)
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
                        for (appointment in mutableDoctorList) {
                            val patientEmail = appointment["psychiatrist_email"] as? String
                            if (patientEmail != null && roomChatMap.containsKey(patientEmail)) {
                                appointment["roomId"] = roomChatMap[patientEmail]!!
                            }
                        }

                        // Step 4: Set the adapter
                        appointmentAdapter = AppointmentUserAdapter(mutableDoctorList, userLoggedIn, this)
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

    private fun fetchAppointmentsFinished() {
        val db = FirebaseFirestore.getInstance()

        // Step 1: Fetch appointments
        db.collection("appointment")
            .whereEqualTo("patient_email", userLoggedIn)
            .whereEqualTo("appointment_status", 4)
            .get()
            .addOnSuccessListener { appointmentResult ->
                // Create a list to hold mutable maps of appointments
                val mutableDoctorList = mutableListOf<MutableMap<String, Any>>()

                for (document in appointmentResult) {
                    val appointment = document.data.toMutableMap() // Mutable map to add roomId later
                    mutableDoctorList.add(appointment)
                }

                appointmentFinishedAdapter = AppointmentFinishedAdapter(mutableDoctorList, userLoggedIn)
                rvFinished.adapter = appointmentFinishedAdapter

            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting appointment documents: ", exception)
            }
    }

    private fun updateAppointmentStatusIfOverdue() {
        val db = FirebaseFirestore.getInstance()

        // Get current date
        val currentDate = Calendar.getInstance().time

        db.collection("appointment")
            .whereEqualTo("patient_email", userLoggedIn)
            .whereEqualTo("appointment_status", 2) // Check only ongoing appointments
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val appointmentTime = document.getDate("appointment_time")

                    if (appointmentTime != null && currentDate.after(appointmentTime)) {
                        // Update appointment_status to 4 if the appointment time is overdue
                        db.collection("appointment").document(document.id)
                            .update("appointment_status", 4)
                            .addOnSuccessListener {
                                Log.d("UpdateStatus", "Appointment status updated to 4 for document ID: ${document.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w("UpdateStatus", "Error updating document", e)
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting appointment documents: ", exception)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun clickedItem(roomId: String) {
        startActivity(Intent(context, ChatRoomActivity::class.java).putExtra("roomId", roomId).putExtra("userLoggedIn", userLoggedIn))
    }
}