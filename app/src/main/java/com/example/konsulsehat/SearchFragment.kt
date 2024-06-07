package com.example.konsulsehat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {
    private val userList = mutableListOf<Map<String, Any>>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var doctorAdapter: DoctorAdapter
    private lateinit var tvSeatch: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.rvSearchDoctor)
        tvSeatch = view.findViewById(R.id.tvSearchDoctor)
        recyclerView.layoutManager = LinearLayoutManager(context)
        doctorAdapter = DoctorAdapter(userList)
        recyclerView.adapter = doctorAdapter

        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()

        // Fetch all documents from the "users" collection
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val userData = document.data
                    val role = userData["role"] as? String
                    if (role == "Psychiatrist") {
                        userList.add(userData)
                    }
                }
                doctorAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting documents: ", exception)
            }

        return view
        tvSeatch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Nothing needed here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nothing needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                searchDoctorByName(query)
            }
        }
        )}


    private fun searchDoctorByName(query: String) {
        val filteredList = userList.filter { userData ->
            val name = userData["name"] as? String ?: ""
            name.contains(query, ignoreCase = true)
        }
        doctorAdapter.updateList(filteredList)
    }

}
