package com.example.konsulsehat.loginRegister

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.konsulsehat.R

class ViewPagerAdapter(private var title:List<String>, private var desc:List<String>, private var image:List<Int>) :RecyclerView.Adapter<ViewPagerAdapter.Pager2ViewHolder>(){
    inner class Pager2ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val itemTitle:TextView = itemView.findViewById(R.id.tvLandingTitle)
        val itemDesc:TextView = itemView.findViewById(R.id.tvLandingDesc)
        val itemImg:ImageView = itemView.findViewById(R.id.imgLanding)

        init {
            itemImg.setOnClickListener {v:View ->
                val position = adapterPosition

            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPagerAdapter.Pager2ViewHolder {
        return Pager2ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.landing_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.Pager2ViewHolder, position: Int) {
        holder.itemTitle.text = title[position]
        holder.itemDesc.text = desc[position]
        holder.itemImg.setImageResource(image[position])
    }

    override fun getItemCount(): Int {
        return title.size
    }

}