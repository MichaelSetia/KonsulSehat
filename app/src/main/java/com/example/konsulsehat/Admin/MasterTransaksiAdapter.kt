package com.example.konsulsehat.Admin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.konsulsehat.R

class MasterTransaksiAdapter(
    var transaksiList: List<Map<String, Any>>,
    var context: Context?
): RecyclerView.Adapter<MasterUserAdapter.ViewHolder>() {
    class ViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val tvDate: TextView = row.findViewById(R.id.tvDateMasterTransaksi)
        val tvNamePatient: TextView = row.findViewById(R.id.tvPatientNameMasterTransaksi)
        val btnDetail: Button = row.findViewById(R.id.btnDetailMasterTransaksi)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MasterTransaksiAdapter.ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_master_transaksi, parent, false
        )

        return MasterTransaksiAdapter.ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MasterTransaksiAdapter.ViewHolder, position: Int) {
        val data = transaksiList[position]

        holder.tvNamePatient.setText(data["patient_name"].toString())
        holder.tvDate.setText(data["appointment_time"].toString())

        holder.btnDetail.setOnClickListener {
            val intent = Intent(context, DetailMasterPatientActivity::class.java)
            // Pass the email to the new activity
            intent.putExtra("email", email)
            // Start the new activity
            context?.startActivity(intent)
        }
    }

}
