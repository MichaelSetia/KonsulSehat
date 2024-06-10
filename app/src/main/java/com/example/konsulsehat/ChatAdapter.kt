package com.example.konsulsehat

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class ChatAdapter(
    var chatList: List<Map<String, Any>>,
    var userLoggedIn: String,
    clickListener: ClickListener
    ): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var clickListener:ClickListener = clickListener

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
        if (data["user_1"].toString() == userLoggedIn.toString()) {
            holder.tvNama.text = data["user_2_name"].toString()
            val profileUrl = data["profile_pict_user_2"].toString()
//            Glide.with(ChatFragment())
//                .load(profileUrl)
//                .placeholder(R.drawable.icon_profile) // optional placeholder
//                .error(R.drawable.icon_profile) // optional error image
//                .into(holder.tvImg)
            holder.tvLastChat.text = data["last_chat"].toString()
        }else if (data["user_2"].toString() == userLoggedIn.toString()) {
            holder.tvNama.text = data["user_1_name"].toString()
            val profileUrl = data["profile_pict_user_1"].toString()
//            Glide.with(ChatFragment())
//                .load(profileUrl)
//                .placeholder(R.drawable.icon_profile) // optional placeholder
//                .error(R.drawable.icon_profile) // optional error image
//                .into(holder.tvImg)
            holder.tvLastChat.text = data["last_chat"].toString()
        }


        holder.itemView.setOnClickListener {
            val roomId = data["documentId"] as? String // Replace "roomId" with the actual field name in your data
            if (roomId != null) {
                clickListener.clickedItem(roomId)
            }
        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    interface ClickListener{
        fun clickedItem(roomId : String)
    }
}