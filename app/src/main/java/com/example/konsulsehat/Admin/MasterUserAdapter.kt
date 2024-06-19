package com.example.konsulsehat.Admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.konsulsehat.BookingFragment
import com.example.konsulsehat.ChatRoomAdapter
import com.example.konsulsehat.R

class MasterUserAdapter(
    var userList: List<Map<String, Any>>,

    ): RecyclerView.Adapter<MasterUserAdapter.ViewHolder>()  {

    class ViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val tvEmailUser: TextView = row.findViewById(R.id.tvMasterUserEmail)
        val tvImg: ImageView = row.findViewById(R.id.imgProfileUserAdmin)
        val btnDetail: Button = row.findViewById(R.id.btnMasterUserDetail)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MasterUserAdapter.ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_master_user, parent, false
        )

        return MasterUserAdapter.ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MasterUserAdapter.ViewHolder, position: Int) {
        val data = userList[position]
        val email = data["email"] as? String
        val profilePictUrl = data["profile_pict"] as? String
        profilePictUrl?.let {
            Glide.with(holder.tvImg.context)
                .load(it)
                .into(holder.tvImg)
        }
        holder.tvEmailUser.setText(email)

        holder.btnDetail.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}