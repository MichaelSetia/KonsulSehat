package com.example.konsulsehat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.konsulsehat.databinding.ActivityChooseRoleBinding
//import com.example.konsulsehat.databinding.ActivityPatientHomeBinding
import com.example.konsulsehat.loginRegister.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

//class PatientHomeActivity : AppCompatActivity() {
//    lateinit var binding: ActivityPatientHomeBinding
//    private lateinit var auth: FirebaseAuth
//    private lateinit var googleSignInClient: GoogleSignInClient
//    private var cloudDB = Firebase.firestore
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityPatientHomeBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        auth = FirebaseAuth.getInstance()
//        val currentUser = auth.currentUser
//
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//
//        binding.btnLogout.setOnClickListener{
//            signOutAndStartSignInActivity()
//        }
//    }
//
//    private fun signOutAndStartSignInActivity() {
//        auth.signOut()
//
//        googleSignInClient.signOut().addOnCompleteListener(this) {
//            // Optional: Update UI or show a message to the user
//            val intent = Intent(this@PatientHomeActivity, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }
//}