package com.example.konsulsehat.Admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.konsulsehat.R
import com.google.firebase.firestore.FirebaseFirestore

class MasterDoctorFragment : Fragment() {
    var userList = mutableListOf<Map<String, Any>>()
    lateinit var recyclerView: RecyclerView
    lateinit var doctorAdapter: MasterDoctorAdapter
    private lateinit var tvSearch: EditText
    private lateinit var tvTotalDoctor: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_master_doctor, container, false)
        tvSearch = view.findViewById(R.id.tvSearchDoctorAdmin)
        tvTotalDoctor = view.findViewById(R.id.tvTotalDoctorAdmin)
        recyclerView = view.findViewById(R.id.rvMasterDoctor)
        recyclerView.layoutManager = LinearLayoutManager(context)
        fetchChatDataFromFirestore()
        val context = requireActivity()

        return view
    }

    private fun fetchChatDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                var doctorCount = 0
                for (document in result) {
                    val userData = document.data
                    val role = userData["role"] as? String
                    if (role == "Psychiatrist" ) {
                        userList.add(userData)
                        doctorCount++
                    }
                }
                setTotalDoctor(doctorCount)
                doctorAdapter = MasterDoctorAdapter(userList,context)
                recyclerView.adapter = doctorAdapter
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting documents: ", exception)
            }
        tvSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Nothing needed here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nothing needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                searchUserByEmail(query)
            }
        })


    }
    private fun searchUserByEmail(query: String) {
        val filteredList = userList.filter { userData ->
            val name = userData["email"] as? String ?: ""
            name.contains(query, ignoreCase = true)
        }
        doctorAdapter.updateList(filteredList)
    }

    fun setTotalDoctor (total:Int) {
        tvTotalDoctor.text = "Total Doctor : " + total.toString()
    }
}