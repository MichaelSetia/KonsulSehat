package com.example.konsulsehat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class ChatAdapter(
    var chatList: List<Map<String, Any>>,
    var userLoggedIn: String,

    ): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    class ViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val tvNama : TextView = row.findViewById(R.id.tvNamaDokter)
        val tvLastChat : TextView = row.findViewById(R.id.tvLastChat)
        val tvImg: ImageView = row.findViewById(R.id.imgProfileDoctor)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_chat, parent, false
        )

        return ChatAdapter.ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ChatAdapter.ViewHolder, position: Int) {
        val data = chatList[position]
        if (data["sender"].toString() == userLoggedIn.toString()) {
            holder.tvNama.text = data["receiver"].toString()
            holder.tvLastChat.text = data["sender"].toString()
        }else if (data["receiver"].toString() == userLoggedIn.toString()) {
            holder.tvNama.text = data["sender"].toString()
            holder.tvLastChat.text = data["receiver"].toString()
        }


//        holder.tvImg.setImageResource(th["profile_pict"]as Int)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

}