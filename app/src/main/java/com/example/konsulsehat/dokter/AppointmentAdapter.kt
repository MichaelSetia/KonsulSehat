package com.example.konsulsehat.dokter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.konsulsehat.ChatRoomAdapter
import com.example.konsulsehat.DoctorAdapter
import com.example.konsulsehat.R

class AppointmentAdapter(
    var AppointmentList: List<Map<String, Any>>,
//    var userLoggedIn: String,

    ): RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    class ViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val tvNama: TextView = row.findViewById(R.id.txtNamaPasien)
        val tvUmur: TextView = row.findViewById(R.id.txtUmur)
        val tvJam: TextView = row.findViewById(R.id.txtJam)
        val tvImgPasien: ImageView = row.findViewById(R.id.imageViewProfilePasien)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.appointment_layout, parent, false
        )

        return AppointmentAdapter.ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return AppointmentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val th= AppointmentList[position]
        holder.tvNama.text=th["Patient_name"].toString()
        holder.tvUmur.text=th["Patient_age"].toString()
        holder.tvJam.text="[6-18-2024 12:10] - [6-19-2024 12:10]"
        val profilePictUrl = th["Patient_profile_pict"] as? String
        profilePictUrl?.let {
            Glide.with(holder.tvImgPasien.context)
                .load(it)
                .into(holder.tvImgPasien)
        }

    }
}


