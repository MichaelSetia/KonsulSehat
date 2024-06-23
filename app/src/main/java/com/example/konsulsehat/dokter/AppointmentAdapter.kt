package com.example.konsulsehat.dokter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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
//    var ChatList: List<Map<String, Any>>,
//    var userLoggedIn: String,
    clickListener: AppointmentAdapter.ClickListener
    ): RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    private var clickListener: AppointmentAdapter.ClickListener = clickListener

    class ViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val tvNama: TextView = row.findViewById(R.id.txtNamaPasien)
        val tvUmur: TextView = row.findViewById(R.id.txtUmur)
        val tvJam: TextView = row.findViewById(R.id.txtJam)
        val tvImgPasien: ImageView = row.findViewById(R.id.imageViewProfilePasien)
        val btnChatPasien : ImageButton = row.findViewById(R.id.btnChatAppointment)
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
        val th = AppointmentList[position]
//        val chat = ChatList[position]
        holder.tvNama.text = th["patient_name"].toString()
        holder.tvUmur.text = th["patient_age"].toString()
//        holder.tvJam.text = "[6-18-2024 12:10] - [6-19-2024 12:10]"
        holder.tvJam.text = th["appointment_time"] as String
        val profilePictUrl = th["patient_profile_pict"] as? String
        profilePictUrl?.let {
            Glide.with(holder.tvImgPasien.context)
                .load(it)
                .into(holder.tvImgPasien)
        }

        if (th["appointment_status"] as Long == 4.toLong()){
            holder.btnChatPasien.isEnabled = false
        }
        else{
            holder.btnChatPasien.isEnabled = true
        }

        holder.btnChatPasien.setOnClickListener{
            val roomId = th["roomId"] as? String // Replace "roomId" with the actual field name in your data
            if (roomId != null) {
                clickListener.clickedItem(roomId)
            }
        }
    }

    interface ClickListener{
        fun clickedItem(roomId : String)
    }
}


