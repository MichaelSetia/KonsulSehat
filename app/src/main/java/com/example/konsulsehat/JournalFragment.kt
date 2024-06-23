package com.example.konsulsehat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.konsulsehat.Admin.AdminFragmentActivity
//import com.example.konsulsehat.dokter.JournalAdapter
import com.google.firebase.firestore.FirebaseFirestore

class JournalFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userLoggedIn: String
    private lateinit var journalAdapter: JournalAdapter
    private lateinit var rvJournal: RecyclerView
    private lateinit var btnAdd: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_journal, container, false)
        rvJournal = view.findViewById(R.id.rvJournal)
        btnAdd = view.findViewById(R.id.btnAddJournal)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.loggedInUser.observe(viewLifecycleOwner, Observer { loggedInUser ->
            userLoggedIn = loggedInUser.toString()

            fetchAppointments()

            btnAdd.setOnClickListener {
                val intent = Intent(requireActivity(), AddJournalActivity::class.java).apply {
                    putExtra("loggedInUser", loggedInUser)
                }
                startActivity(intent)
            }
        })

        rvJournal.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    private fun fetchAppointments() {
        val db = FirebaseFirestore.getInstance()

        db.collection("journal")
            .whereEqualTo("journal_user_email", userLoggedIn)
            .get()
            .addOnSuccessListener { journalResult ->
                val mutablePatientList = mutableListOf<MutableMap<String, Any>>()

                for (document in journalResult) {
                    val journal = document.data.toMutableMap()
                    journal["journal_id"] = document.id // Set document ID as journal_id
                    mutablePatientList.add(journal)
                }
                journalAdapter = JournalAdapter(mutablePatientList,requireContext())
                rvJournal.adapter = journalAdapter
                journalAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting appointment documents: ", exception)
            }
    }
}
