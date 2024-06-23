package com.example.konsulsehat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.konsulsehat.databinding.ActivityAddJournalBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddJournalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddJournalBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddJournalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        // Retrieve the logged-in user information passed via Intent
        val loggedInUser = intent.getStringExtra("loggedInUser")
        sharedViewModel.setLoggedInUser(loggedInUser ?: "")

        binding.btnDiscardJournal.setOnClickListener {
            // Discard the journal entry, clear the input field
            binding.inpIsiJournal.text?.clear()
        }

        binding.btnBackToJournal.setOnClickListener {
            // Navigate back to JournalFragment
            finish()
        }

        binding.tvTanggalAddJournal.text= SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        binding.btnSaveJournal.setOnClickListener {
            // Save the journal entry to Firebase Firestore
            val journalContent = binding.inpIsiJournal.text.toString()
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val journalEmoji = when {
                binding.btnEmoji1.isPressed -> "@drawable/asset_emoji_1"
                binding.btnEmoji2.isPressed -> "@drawable/asset_emoji_2"
                binding.btnEmoji3.isPressed -> "@drawable/asset_emoji_3"
                binding.btnEmoji4.isPressed -> "@drawable/asset_emoji_4"
                binding.btnEmoji5.isPressed -> "@drawable/asset_emoji_5"
                else -> ""
            }

            val journalEntry = hashMapOf(
                "journal" to journalContent,
                "journal_date" to currentDate,
                "journal_emoji" to journalEmoji,
                "journal_user_email" to loggedInUser
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("journal")
                .add(journalEntry)
                .addOnSuccessListener {
                    // Successfully added the journal entry
                    finish() // Navigate back to the previous activity/fragment
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                }
        }
    }
}
