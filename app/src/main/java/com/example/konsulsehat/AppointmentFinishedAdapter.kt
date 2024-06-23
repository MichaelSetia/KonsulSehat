package com.example.konsulsehat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AppointmentFinishedAdapter (
    var appointmentList: List<Map<String, Any>>,
    var userLoggedIn: String,
    ): RecyclerView.Adapter<AppointmentFinishedAdapter.ViewHolder>() {

    class ViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val tvNama : TextView = row.findViewById(R.id.txtNamaDoktorAppointmentFinished)
        val tvJam : TextView = row.findViewById(R.id.txtJamAppointmentUserFinished)
        val tvImg: ImageView = row.findViewById(R.id.imgProfileDoktorAppointmentFinished)
        val btnDetail : ImageButton = row.findViewById(R.id.btnDetailAppointmentFinished)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentFinishedAdapter.ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_finished_appointment_user, parent, false
        )

        return AppointmentFinishedAdapter.ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: AppointmentFinishedAdapter.ViewHolder, position: Int) {
        val th = appointmentList[position]

        holder.tvNama.text = th["psychiatrist_name"].toString()
        holder.tvJam.text = th["appointment_time"] as String
        val profilePictUrl = th["psychiatrist_profile_pict"] as? String
        profilePictUrl?.let {
            Glide.with(holder.tvImg.context)
                .load(it)
                .into(holder.tvImg)
        }


        holder.btnDetail.setOnClickListener{

        }
    }

    override fun getItemCount(): Int {
        return appointmentList.size
    }

}