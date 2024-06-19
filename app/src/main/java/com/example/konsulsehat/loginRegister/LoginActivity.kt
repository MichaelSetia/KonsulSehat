package com.example.konsulsehat.loginRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.konsulsehat.Admin.AdminFragmentActivity
import com.example.konsulsehat.FragmentActivity
//import com.example.konsulsehat.PatientHomeActivity
import com.example.konsulsehat.R
import com.example.konsulsehat.SharedViewModel
import com.example.konsulsehat.databinding.ActivityLoginBinding
import com.example.konsulsehat.dokter.FragmentDokterActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class LoginActivity : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var googleSignInClient:GoogleSignInClient
    private var cloudDB = Firebase.firestore
    private lateinit var loggedInUser : String
    private lateinit var sharedViewModel: SharedViewModel
    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnToRegisterLogin.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSignInLogin.setOnClickListener{
            val email = binding.txtEmailLogin.text.toString()
            val password = binding.txtPasswordLogin.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful){
                        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
                        // Debug log
                        Log.d("SignInActivity", "Sign-in successful for email: $email")

                        // Retrieve the user's role from the database using email
                        val db = FirebaseFirestore.getInstance()

                        // Query the collection to find the document with the specified email
                        db.collection("users").whereEqualTo("email", email).get()
                            .addOnSuccessListener { documents ->
                                if (!documents.isEmpty) {
                                    val document = documents.documents[0]
                                    val role = document.getString("role")
                                    sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

                                    // Set the logged-in user
                                    loggedInUser = email
                                    sharedViewModel.setLoggedInUser(loggedInUser)

                                    // Check the role and navigate accordingly
                                    when (role) {
                                        "Patient" -> {
                                            val intent = Intent(this, FragmentActivity::class.java).apply {
                                                putExtra("loggedInUser", loggedInUser)
                                            }
                                            startActivity(intent)
                                        }
                                        "Psychiatrist" -> {
                                            val intent = Intent(this, FragmentDokterActivity::class.java).apply {
                                                putExtra("loggedInUser", loggedInUser)
                                            }
                                            startActivity(intent)
                                        }
                                        "Admin" -> {
                                            val intent = Intent(this, AdminFragmentActivity::class.java).apply {
                                                putExtra("loggedInUser", loggedInUser)
                                            }
                                            startActivity(intent)
                                        }
                                        else -> {
                                            Log.d("SignInActivity", "Role not recognized for email: $email")
                                            Toast.makeText(this, "Role not recognized!", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } else {
                                    // Debug log
                                    Log.d("SignInActivity", "No document found for email: $email")
                                    Toast.makeText(this, "No such document!", Toast.LENGTH_LONG).show()
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Debug log
                                Log.e("SignInActivity", "Failed to fetch role", exception)
                                Toast.makeText(this, "Failed to fetch role: ${exception.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        // Debug log
                        Log.e("SignInActivity", "Sign-in failed", it.exception)
                        Toast.makeText(this, "gabisa", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields are not allowed!", Toast.LENGTH_LONG).show()
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnSignInWithGoogle.setOnClickListener{
            signInGoogle()
        }
    }

    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
//        launcher.launch(signInIntent)
        startActivityForResult(signInIntent, LoginActivity.RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LoginActivity.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = hashMapOf(
                        "email" to auth.currentUser!!.email,
                        "name" to auth.currentUser!!.displayName,
                        "password" to "-",
                        "profile_pict" to auth.currentUser!!.photoUrl
                    )
                    val userId = auth.currentUser!!.uid
                    cloudDB.collection("users").document(userId).set(user)
                        .addOnSuccessListener {
                            val intent = Intent(this, ChooseRoleActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_LONG).show()
                }
            }
    }
}