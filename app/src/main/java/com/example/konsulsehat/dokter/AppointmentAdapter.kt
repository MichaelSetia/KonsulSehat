package com.example.konsulsehat.dokter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.konsulsehat.ChatRoomAdapter
import com.example.konsulsehat.DoctorAdapter
import com.example.konsulsehat.R

class AppointmentAdapter(
    var AppointmentList: List<Map<String, Any>>,
    var userLoggedIn: String,

    ): RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    class ViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val tvNama: LinearLayout = row.findViewById(R.id.txtNama)
        val tvUmur: LinearLayout = row.findViewById(R.id.txtUmur)
        val tvJam: TextView = row.findViewById(R.id.txtJam)
        val tvImgPasien: TextView = row.findViewById(R.id.imageViewProfilePasien)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_search_doctor, parent, false
        )

        return AppointmentAdapter.ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return AppointmentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val th= AppointmentList[position]
        
    }
}


