package com.example.konsulsehat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.konsulsehat.dokter.AppointmentAdapter

class AppointmentUserAdapter(
    var appointmentList: List<Map<String, Any>>,
    var userLoggedIn: String,
    clickListener: ClickListener
    ): RecyclerView.Adapter<AppointmentUserAdapter.ViewHolder>() {

    private var clickListener: AppointmentUserAdapter.ClickListener = clickListener

    class ViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val tvNama : TextView = row.findViewById(R.id.txtNamaDoktorAppointment)
        val tvJam : TextView = row.findViewById(R.id.txtJamAppointmentUser)
        val tvImg: ImageView = row.findViewById(R.id.imgProfileDoktorAppointment)
        val btnChat : ImageButton = row.findViewById(R.id.btnChatAppointment)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentUserAdapter.ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_appointment_user, parent, false
        )

        return AppointmentUserAdapter.ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: AppointmentUserAdapter.ViewHolder, position: Int) {
        val th = appointmentList[position]

        holder.tvNama.text = th["psychiatrist_name"].toString()
        holder.tvJam.text = th["appointment_time"] as String
        val profilePictUrl = th["psychiatrist_profile_pict"] as? String
        profilePictUrl?.let {
            Glide.with(holder.tvImg.context)
                .load(it)
                .into(holder.tvImg)
        }

        if (th["appointment_status"] as Long == 4.toLong()){
            holder.btnChat.isEnabled = false
        }
        else{
            holder.btnChat.isEnabled = true
        }

        holder.btnChat.setOnClickListener{
            val roomId = th["roomId"] as? String // Replace "roomId" with the actual field name in your data
            if (roomId != null) {
                clickListener.clickedItem(roomId)
            }
        }
    }

    override fun getItemCount(): Int {
        return appointmentList.size
    }

    interface ClickListener{
        fun clickedItem(roomId : String)
    }

}