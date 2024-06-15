package com.example.konsulsehat.dokter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.konsulsehat.ChatAdapter
import com.example.konsulsehat.ChatRoomActivity
import com.example.konsulsehat.R
import com.example.konsulsehat.SharedViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ChatDokterFragment : Fragment(), ChatDokterAdapter.ClickListener {
    var chatList = mutableListOf<Map<String, Any>>()
    lateinit var recyclerView: RecyclerView
    lateinit var chatAdapter: ChatDokterAdapter
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userLoggedIn : String
    private val uniqueChatIds = HashSet<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat_dokter, container, false)

        userLoggedIn = ""

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.loggedInUser.observe(viewLifecycleOwner, Observer { loggedInUser ->
            // Use the logged-in user information
            userLoggedIn = loggedInUser.toString()
        })

        recyclerView = view.findViewById(R.id.rvChatDokter)
        recyclerView.layoutManager = LinearLayoutManager(context)
        fetchChatDataFromFirestore()


//        Toast.makeText(context, userLoggedIn, Toast.LENGTH_SHORT).show()
        return view
    }

    private fun fetchChatDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("room_chat")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val chatData = document.data
                    val documentId = document.id
                    chatData["documentId"] = documentId
                    val user_1 = chatData["user_1"] as? String
                    val user_2 = chatData["user_2"] as? String

                    if (user_1 == userLoggedIn) {
                        chatList.add(chatData)
                    }else if (user_2 == userLoggedIn) {
                        chatList.add(chatData)
                    }
                }
                chatAdapter = ChatDokterAdapter(chatList, userLoggedIn, this)
                recyclerView.adapter = chatAdapter
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting documents: ", exception)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun clickedItem(roomId: String) {
        startActivity(Intent(context, ChatRoomDokterActivity::class.java).putExtra("roomId", roomId).putExtra("userLoggedIn", userLoggedIn))
    }


}