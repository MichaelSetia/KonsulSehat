package com.example.konsulsehat.Admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.konsulsehat.R

class MasterDoctorAdapter(
    var userList: List<Map<String, Any>>,

    ): RecyclerView.Adapter<MasterDoctorAdapter.ViewHolder>() {

    class ViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val tvEmailUser: TextView = row.findViewById(R.id.tvMasterDoctorEmail)
        val tvImg: ImageView = row.findViewById(R.id.imgProfileDoctorAdmin)
        val btnDetail: Button = row.findViewById(R.id.btnMasterDoctorDetail)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MasterDoctorAdapter.ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_master_doctor, parent, false
        )

        return MasterDoctorAdapter.ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MasterDoctorAdapter.ViewHolder, position: Int) {
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

    fun updateList(newList: List<Map<String, Any>>) {
        userList = newList
        notifyDataSetChanged()
    }
}