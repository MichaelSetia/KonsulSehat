package com.example.konsulsehat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.auth.User

class DoctorAdapter (
    var userList: List<Map<String, Any>>,

    ): RecyclerView.Adapter<DoctorAdapter.ViewHolder>() {

    class ViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val tvNama: TextView = row.findViewById(R.id.tvNamaDokterSearch)
        val tvRating: TextView = row.findViewById(R.id.tvRatingDokterSearch)
        val tvImg: ImageView = row.findViewById(R.id.imgSearchDokter)
        val btnAppointment: Button=row.findViewById(R.id.btnMakeAppointment)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_search_doctor, parent, false
        )

        return ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val th= userList[position]
////        holder.tvImg.text="Owner by : ${th.owner.username}"
        holder.tvNama.text=th["name"].toString()
        holder.tvRating.text="0"
        val profilePictUrl = th["profile_pict"] as? String
        profilePictUrl?.let {
            Glide.with(holder.tvImg.context)
                .load(it)
                .into(holder.tvImg)
        }
//
        holder.btnAppointment.setOnClickListener {
            val fragment = BookingFragment()
            val bundle = Bundle()
            // Pass any data needed to BookingFragment using Bundle
            bundle.putString("email", th["email"].toString())
            fragment.arguments = bundle

            // Navigate to BookingFragment
            val fragmentManager: FragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerView2, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }
    fun updateList(newList: List<Map<String, Any>>) {
        userList = newList
        notifyDataSetChanged()
    }
}
