package com.example.konsulsehat.Admin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.konsulsehat.R

class MasterTransaksiAdapter(
    var transactionList: List<Map<String, Any>>,
    var context: Context?
): RecyclerView.Adapter<MasterTransaksiAdapter.ViewHolder>() {
    class ViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val tvDate: TextView = row.findViewById(R.id.tvDateMasterTransaksi)
        val tvTransID: TextView = row.findViewById(R.id.tvIdMasterTransaksi)
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
        return transactionList.size
    }

    override fun onBindViewHolder(holder: MasterTransaksiAdapter.ViewHolder, position: Int) {
        val data = transactionList[position]

        holder.tvTransID.setText(data["appointment_id"].toString())
        holder.tvDate.setText(data["appointment_time"].toString())

        holder.btnDetail.setOnClickListener {
            val intent = Intent(context, DetailMasterTransaksiActivity::class.java)
            intent.putExtra("appointment_id", data["appointment_id"].toString())
            context?.startActivity(intent)
        }
    }

    fun updateList(newList: List<Map<String, Any>>) {
        transactionList = newList
        notifyDataSetChanged()
    }

}
