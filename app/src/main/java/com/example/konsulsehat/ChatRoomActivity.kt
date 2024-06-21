package com.example.konsulsehat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatRoomActivity : AppCompatActivity() {
    var chatList = mutableListOf<Map<String, Any>>()
    lateinit var rvRoomChat: RecyclerView
    lateinit var chatRoomAdapter: ChatRoomAdapter
    lateinit var tvUsernameChat : TextView
    private lateinit var userLoggedIn : String
    private lateinit var tempReceiver : String
    private lateinit var tempReceiverName : String
    private lateinit var tempSender : String
    private lateinit var tempSenderName : String
    private lateinit var roomChatId : String
    private lateinit var inpIsiChat: EditText
    private lateinit var btnSendChat: ImageButton
    private lateinit var btnBackToChat : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        inpIsiChat = findViewById(R.id.inpIsiChat)
        btnSendChat = findViewById(R.id.btnSendChat)
        tvUsernameChat = findViewById(R.id.tvUsernameChat)
        rvRoomChat = findViewById(R.id.rvRoomChat)
        btnBackToChat= findViewById(R.id.btnBackToChat)
        userLoggedIn = ""


        rvRoomChat.layoutManager = LinearLayoutManager(this)

        getData()

        btnSendChat.setOnClickListener {
            sendMessage()
        }

        btnBackToChat.setOnClickListener {
            finish()
        }
    }

    private fun getData() {
        var intent = intent.extras

        roomChatId = intent!!.getString("roomId").toString()
        userLoggedIn = intent!!.getString("userLoggedIn").toString()
//        Toast.makeText(this, roomChatId, Toast.LENGTH_SHORT).show()
        fetchChatDataFromFirestore()
    }

    private fun fetchChatDataFromFirestore() {
        var doctorName : String = ""
        val db = FirebaseFirestore.getInstance()
        db.collection("chats")
            .whereEqualTo("fk_roomchat", roomChatId)
//            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                chatList.clear()
                for (document in result) {
                    val chatData = document.data
                    val receiver = chatData["receiver"] as? String
                    val sender = chatData["sender"] as? String
                    val receiver_name = chatData["receiver_name"] as? String
                    val sender_name = chatData["sender_name"] as? String

                    chatList.add(chatData)

                    if (receiver == userLoggedIn) {
                        if (doctorName=="") {
                            doctorName = sender_name.toString()
                        }
                    }else if (sender == userLoggedIn) {
                        if (doctorName=="") {
                            doctorName = receiver_name.toString()
                        }
                    }
                }
                setDoctorName(doctorName)
                chatList.sortBy { it["timestamp"] as Long }
                chatRoomAdapter = ChatRoomAdapter(chatList, userLoggedIn)
                rvRoomChat.adapter = chatRoomAdapter
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting documents: ", exception)
            }
    }

    fun setDoctorName(name: String) {
        var doctorName = name
        // Update the doctor's name if the view is already created
        tvUsernameChat.text = doctorName
    }

    private fun sendMessage() {
        val message = inpIsiChat.text.toString()

        if (message.isNotEmpty()) {
            val db = FirebaseFirestore.getInstance()

            //set receiver and sender
            db.collection("room_chat")
                .document(roomChatId)
                .get()
                .addOnSuccessListener {document ->
                    if (document != null && document.exists()) {
                        val roomChatData = document.data
                        if (roomChatData?.get("user_1").toString() == userLoggedIn) {
                            tempSender = roomChatData?.get("user_1").toString()
                            tempSenderName = roomChatData?.get("user_1_name").toString()
                            tempReceiver = roomChatData?.get("user_2").toString()
                            tempReceiverName = roomChatData?.get("user_2_name").toString()
                        } else {
                            tempSender = roomChatData?.get("user_2").toString()
                            tempSenderName = roomChatData?.get("user_2_name").toString()
                            tempReceiver = roomChatData?.get("user_1").toString()
                            tempReceiverName = roomChatData?.get("user_1_name").toString()
                        }
                        val chatData = hashMapOf(
                            "sender" to userLoggedIn,
                            "sender_name" to tempSenderName,
                            "receiver" to tempReceiver, // Replace with actual receiver ID
                            "receiver_name" to tempReceiverName,
                            "isi_chat" to message,
                            "timestamp" to System.currentTimeMillis(),
                            "fk_roomchat" to roomChatId
                        )
                        db.collection("chats")
                            .add(chatData)
                            .addOnSuccessListener { documentReference ->
                                chatData["id"] = documentReference.id // Optional: store the document ID if needed
                                chatList.add(chatData)
                                chatRoomAdapter.notifyItemInserted(chatList.size - 1)
                                rvRoomChat.scrollToPosition(chatList.size - 1)
                                inpIsiChat.text.clear() // Clear the input field

                                db.collection("room_chat")
                                    .document(roomChatId)
                                    .update(
                                        mapOf(
                                            "last_chat" to message,
                                        )
                                    )
                                    .addOnSuccessListener {
                                        Log.d("FirestoreData", "Room chat last message updated successfully")
                                    }

                            }
                            .addOnFailureListener { e ->
                                Log.w("FirestoreData", "Error adding document", e)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("FirestoreData", "Error getting documents: ", exception)
                }

        }
    }

}