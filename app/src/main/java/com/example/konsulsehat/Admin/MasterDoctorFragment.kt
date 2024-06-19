package com.example.konsulsehat.Admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.konsulsehat.R
import com.google.firebase.firestore.FirebaseFirestore

class MasterDoctorFragment : Fragment() {
    var userList = mutableListOf<Map<String, Any>>()
    lateinit var recyclerView: RecyclerView
    lateinit var doctorAdapter: MasterDoctorAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_master_doctor, container, false)

        recyclerView = view.findViewById(R.id.rvMasterDoctor)
        recyclerView.layoutManager = LinearLayoutManager(context)
        fetchChatDataFromFirestore()

        return view
    }

    private fun fetchChatDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val userData = document.data
                    val role = userData["role"] as? String
                    if (role == "Psychiatrist" ) {
                        userList.add(userData)
                    }
                }
                doctorAdapter = MasterDoctorAdapter(userList,)
                recyclerView.adapter = doctorAdapter
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting documents: ", exception)
            }
    }

}