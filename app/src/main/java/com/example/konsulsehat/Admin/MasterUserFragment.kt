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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.konsulsehat.ChatAdapter
import com.example.konsulsehat.R
import com.example.konsulsehat.SharedViewModel
import com.google.firebase.firestore.FirebaseFirestore

class MasterUserFragment : Fragment() {
    var userList = mutableListOf<Map<String, Any>>()
    lateinit var recyclerView: RecyclerView
    lateinit var userAdapter: MasterUserAdapter
    private lateinit var tvSearch: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_master_user, container, false)

        tvSearch = view.findViewById(R.id.tvSearchPatientAdmin)

        recyclerView = view.findViewById(R.id.rvMasterUser)
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
                for (document in result) {
                    val userData = document.data
                    val role = userData["role"] as? String
                    if (role == "Patient" ) {
                        userList.add(userData)
                    }
                }
                userAdapter = MasterUserAdapter(userList,context)
                recyclerView.adapter = userAdapter
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
        userAdapter.updateList(filteredList)
    }





}