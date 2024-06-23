package com.example.konsulsehat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.konsulsehat.databinding.ActivityDetailJournalBinding
import com.google.firebase.firestore.FirebaseFirestore

class DetailJournalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailJournalBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var journalId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailJournalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        // Retrieve the logged-in user information passed via Intent
        val loggedInUser = intent.getStringExtra("loggedInUser")
        sharedViewModel.setLoggedInUser(loggedInUser ?: "")

        // Retrieve other journal details from Intent
        val journalDate = intent.getStringExtra("journal_date")
        val journalContent = intent.getStringExtra("journal")
        val journalEmoji = intent.getStringExtra("journal_emoji")
        journalId = intent.getStringExtra("journal_id") ?: ""

        // Set the data to the views
        binding.tvTanggalDetailJournal.text = journalDate
        binding.tvIsiDetailJournal.text = journalContent

        // Set the emoji image
        val emojiResId = getEmojiDrawableResource(journalEmoji ?: "")
        binding.imgMoodJournal.setImageResource(emojiResId)

        binding.btnBackToJournal.setOnClickListener {
            finish() // This will close the current activity and go back to the previous one
        }

        binding.btnDeleteJournal.setOnClickListener {
            deleteJournal()
        }
    }

    private fun deleteJournal() {
        val db = FirebaseFirestore.getInstance()
        db.collection("journal").document(journalId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Journal entry deleted successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the current activity and go back to the previous one
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting journal entry: ${e.message}", Toast.LENGTH_SHORT).show()
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
