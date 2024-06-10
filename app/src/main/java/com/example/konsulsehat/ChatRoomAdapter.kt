package com.example.konsulsehat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatRoomAdapter (
    var chatList: List<Map<String, Any>>,
    var userLoggedIn: String,

    ): RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>() {

    class ViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val leftChatLayout: LinearLayout = row.findViewById(R.id.left_chat_layout)
        val rightChatLayout: LinearLayout = row.findViewById(R.id.right_chat_layout)
        val tvLeftChat: TextView = row.findViewById(R.id.tvLeftChat)
        val tvRightChat: TextView = row.findViewById(R.id.tvRightChat)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomAdapter.ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_bubble_chat, parent, false
        )

        return ChatRoomAdapter.ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ChatRoomAdapter.ViewHolder, position: Int) {
        val data = chatList[position]
        val sender = data["sender"] as? String
        val message = data["isi_chat"] as? String

        if (sender == userLoggedIn) {
            holder.rightChatLayout.visibility = View.VISIBLE
            holder.leftChatLayout.visibility = View.GONE
            holder.tvRightChat.text = message
        } else {
            holder.rightChatLayout.visibility = View.GONE
            holder.leftChatLayout.visibility = View.VISIBLE
            holder.tvLeftChat.text = message
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

}