package com.example.konsulsehat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JournalAdapter(
    private var journalList: List<Map<String, Any>>,
    context: Context
) : RecyclerView.Adapter<JournalAdapter.ViewHolder>() {

    class ViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val tvIsiJournal: TextView = row.findViewById(R.id.tvIsiJournal)
        val tvTanggalJournal: TextView = row.findViewById(R.id.tvTanggalJournal)
        val imgEmojiJournal: ImageView = row.findViewById(R.id.imgEmojiJournal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_journal, parent, false
        )
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return journalList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val journal = journalList[position]
        holder.tvIsiJournal.text = journal["journal"].toString()
        holder.tvTanggalJournal.text = journal["journal_date"].toString()

        val emojiString = journal["journal_emoji"] as? String
        val emojiResId = getEmojiDrawableResource(emojiString ?: "")
        holder.imgEmojiJournal.setImageResource(emojiResId)

        // Set onClickListener to navigate to DetailJournalActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailJournalActivity::class.java).apply {
                putExtra("loggedInUser", journal["journal_user_email"].toString())
                putExtra("journal_date", journal["journal_date"].toString())
                putExtra("journal", journal["journal"].toString())
                putExtra("journal_emoji", journal["journal_emoji"].toString())
                putExtra("journal_id", journal["journal_id"].toString()) // Assuming journal_id is a field in the journal document
            }
            context.startActivity(intent)
        }
    }

    private fun getEmojiDrawableResource(emojiString: String): Int {
        return when (emojiString) {
            "@drawable/asset_emoji_1" -> R.drawable.asset_emoji_1
            "@drawable/asset_emoji_2" -> R.drawable.asset_emoji_2
            "@drawable/asset_emoji_3" -> R.drawable.asset_emoji_3
            "@drawable/asset_emoji_4" -> R.drawable.asset_emoji_4
            "@drawable/asset_emoji_5" -> R.drawable.asset_emoji_5
            else -> R.drawable.asset_emoji_1 // Default drawable if none match
        }
    }
}
