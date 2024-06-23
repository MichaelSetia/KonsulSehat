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
import com.example.konsulsehat.R
import com.google.firebase.firestore.FirebaseFirestore

class MasterTransaksiFragment : Fragment() {
    var transaksiList = mutableListOf<Map<String, Any>>()
    lateinit var recyclerView: RecyclerView
    lateinit var transaksiAdapter: MasterTransaksiAdapter
    private lateinit var tvSearch: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_master_transaksi, container, false)

        tvSearch = view.findViewById(R.id.tvSearchInvoiceAdmin)

        recyclerView = view.findViewById(R.id.rvMasterTransaksi)
        recyclerView.layoutManager = LinearLayoutManager(context)
        fetchChatDataFromFirestore()
        val context = requireActivity()

        return view
    }

    private fun fetchChatDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("appointment")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val appointment = document.data
                    transaksiList.add(appointment)
                }
                transaksiAdapter = MasterTransaksiAdapter(transaksiList,context)
                recyclerView.adapter = transaksiAdapter
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
                searchInvoice(query)
            }
        })
    }

    private fun searchInvoice(query: String) {
        val filteredList = transaksiList.filter { transaksiData ->
            val name = transaksiData["appointment_id"] as? String ?: ""
            name.contains(query, ignoreCase = true)
        }
        transaksiAdapter.updateList(filteredList)
    }

}