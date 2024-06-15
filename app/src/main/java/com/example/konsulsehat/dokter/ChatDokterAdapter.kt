package com.example.konsulsehat.dokter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.konsulsehat.ChatAdapter
import com.example.konsulsehat.R

class ChatDokterAdapter(
    var chatList: List<Map<String, Any>>,
    var userLoggedIn: String,
    clickListener: ChatDokterAdapter.ClickListener
): RecyclerView.Adapter<ChatDokterAdapter.ViewHolder>() {

    private var clickListener: ChatDokterAdapter.ClickListener = clickListener
    class ViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val tvNama : TextView = row.findViewById(R.id.tvChatNamaPasien)
        val tvLastChat : TextView = row.findViewById(R.id.tvLastChatPasien)
        val tvImg: ImageView = row.findViewById(R.id.imgProfilePasien)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatDokterAdapter.ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_chat_dokter, parent, false
        )

        return ChatDokterAdapter.ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = chatList[position]
        if (data["user_1"].toString() == userLoggedIn.toString()) {
            holder.tvNama.text = data["user_2_name"].toString()
            val profileUrl = data["profile_pict_user_2"].toString()
//            Glide.with(ChatFragment())
//                .load(profileUrl)
//                .placeholder(R.drawable.icon_profile) // optional placeholder
//                .error(R.drawable.icon_profile) // optional error image
//                .into(holder.tvImg)
            val profilePictUrl = data["profile_pict_user_2"] as? String
            profilePictUrl?.let {
                Glide.with(holder.tvImg.context)
                    .load(it)
                    .into(holder.tvImg)
            }
            holder.tvLastChat.text = data["last_chat"].toString()
        }else if (data["user_2"].toString() == userLoggedIn.toString()) {
            holder.tvNama.text = data["user_1_name"].toString()
//            val profileUrl = data["profile_pict_user_1"].toString()
//            Glide.with(ChatFragment())
//                .load(profileUrl)
//                .placeholder(R.drawable.icon_profile) // optional placeholder
//                .error(R.drawable.icon_profile) // optional error image
//                .into(holder.tvImg)

            val profilePictUrl = data["profile_pict_user_1"] as? String
            profilePictUrl?.let {
                Glide.with(holder.tvImg.context)
                    .load(it)
                    .into(holder.tvImg)
            }
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