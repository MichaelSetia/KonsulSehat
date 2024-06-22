package com.example.konsulsehat.loginRegister

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.konsulsehat.Admin.AdminFragmentActivity
import com.example.konsulsehat.FragmentActivity
import com.example.konsulsehat.R
import com.example.konsulsehat.SharedViewModel
import com.example.konsulsehat.databinding.ActivityLandingBinding
import com.example.konsulsehat.databinding.ActivityRegisterBinding
import com.example.konsulsehat.dokter.FragmentDokterActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var googleSignInClient:GoogleSignInClient
    private var cloudDB = Firebase.firestore
    lateinit var binding: ActivityRegisterBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var sharedViewModel: SharedViewModel

    companion object {
        private const val RC_SIGN_IN = 9001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

        binding.btnToLoginRegister.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSignUpRegister.setOnClickListener{
            val email = binding.txtEmailRegister.text.toString()
            val name = binding.txtNameRegister.text.toString()
            val password = binding.txtPasswordRegister.text.toString()
            val confirmPassword = binding.txtConfirmPasswordRegister.text.toString()

            if (email.isNotEmpty() && name.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                // Query the collection to find the document with the specified email
                db.collection("users").whereEqualTo("email", email).get()
                    .addOnSuccessListener { documents ->
                        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
                        if (!documents.isEmpty) { // email is registered
                            Log.d("RegisterActivity", "Email is already registered: $email")
                            Toast.makeText(this, "Email is already registered", Toast.LENGTH_LONG).show()
                        } else {
                            if (password == confirmPassword){
                                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                                    if (it.isSuccessful){
                                        val user = hashMapOf(
                                            "email" to email,
                                            "name" to name,
                                            "password" to password,
                                            "profile_pict" to "https://static-00.iconduck.com/assets.00/profile-circle-icon-512x512-zxne30hp.png",
                                            "saldo" to 0,
                                            "status" to "active"
                                        )

                                        val userId = auth.currentUser!!.uid
                                        cloudDB.collection("users").document(userId).set(user)
                                            .addOnSuccessListener {
                                                binding.txtEmailRegister.text.clear()
                                                binding.txtNameRegister.text.clear()
                                                binding.txtPasswordRegister.text.clear()
                                                binding.txtConfirmPasswordRegister.text.clear()

                                                val intent = Intent(this, ChooseRoleActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(this, it.message , Toast.LENGTH_LONG).show()
                                            }
                                    }
                                    else{
                                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            else{
                                Toast.makeText(this, "Password and Confirm Password is not matching!", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Debug log
                        Log.e("SignInActivity", "Failed to fetch role", exception)
                        Toast.makeText(this, "Failed to fetch role: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
            }
            else{
                Toast.makeText(this, "Empty Fields are not allowed!", Toast.LENGTH_LONG).show()
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnSignUpWithGoogle.setOnClickListener{
            signInGoogle()
        }
    }

    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
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
                        "profile_pict" to auth.currentUser!!.photoUrl,
                        "saldo" to 0,
                        "status" to "active"
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



//    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//        result ->
//            if(result.resultCode == Activity.RESULT_OK){
//                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//                handleResults(task)
//            }
//    }

//    private fun handleResults(task:Task<GoogleSignInAccount>){
//        if (task.isSuccessful){
//            val account : GoogleSignInAccount? = task.result
//            if (account != null){
//                updateUI(account)
//            }
//        }
//        else{
//            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_LONG).show()
//        }
//    }
//
//    private fun updateUI(account: GoogleSignInAccount) {
//        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//        auth.signInWithCredential(credential).addOnCompleteListener {
//            if (it.isSuccessful) {
//                val intent:Intent = Intent(this, HomeActivity::class.java)
//                intent.putExtra("email", account.email)
//                intent.putExtra("name", account.displayName)
//                intent.putExtra("password", account.idToken)
//                startActivity(intent)
//            }
//            else{
//                Toast.makeText(this, it.exception.toString() , Toast.LENGTH_LONG).show()
//            }
//        }
//    }
}