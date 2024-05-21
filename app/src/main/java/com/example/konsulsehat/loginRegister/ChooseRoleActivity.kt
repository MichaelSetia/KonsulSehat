package com.example.konsulsehat.loginRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.konsulsehat.PatientHomeActivity
import com.example.konsulsehat.R
import com.example.konsulsehat.databinding.ActivityChooseRoleBinding
import com.example.konsulsehat.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import java.util.Objects

class ChooseRoleActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient:GoogleSignInClient
    private lateinit var userPassword:String
    private var cloudDB = Firebase.firestore
    lateinit var binding: ActivityChooseRoleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val listRole = listOf<String>("Patient", "Psychiatrist")
        var spinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, listRole)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRole.adapter = spinnerAdapter
        binding.spRole.setSelection(0)

        val ref = cloudDB.collection("users").document(currentUser!!.uid)
        ref.get().addOnSuccessListener {
            if (it != null) {
                userPassword = it.data?.get("password")?.toString()!!
                if (userPassword != "-"){
                    binding.ivPass1.isVisible = false
                    binding.ivPass2.isVisible = false
                    binding.tvSetPassword.isVisible = false
                    binding.txtPasswordChooseRole.isVisible = false
                    binding.txtConfirmPasswordChooseRole.isVisible = false

//                    binding.imageView.setPadding(0, 100, 0, 0)
                }
                else{
                    binding.ivPass1.isVisible = true
                    binding.ivPass2.isVisible = true
                    binding.tvSetPassword.isVisible = true
                    binding.txtPasswordChooseRole.isVisible = true
                    binding.txtConfirmPasswordChooseRole.isVisible = true
                }
            }
        }

        binding.btnFinishChooseRole.setOnClickListener{
            val password = binding.txtPasswordChooseRole.text.toString()
            val confirmPassword = binding.txtConfirmPasswordChooseRole.text.toString()
            val role = binding.spRole.selectedItem.toString()

            if (userPassword=="-" && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if (password == confirmPassword){
                    val updateMap = mapOf(
                        "password" to password,
                        "role" to role
                    )
                    cloudDB.collection("users").document(currentUser!!.uid).update(updateMap)
                    toHomePage(currentUser)
                }
                else{
                    Toast.makeText(this, "Password and Confirm Password is not matching!", Toast.LENGTH_LONG).show()
                }
            }
            else if (userPassword!="-"){
                val updateMap = mapOf(
                    "role" to role
                )
                cloudDB.collection("users").document(currentUser!!.uid).update(updateMap)
                toHomePage(currentUser)
            }
            else{
                Toast.makeText(this, "Empty Fields are not allowed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun toHomePage(currentUser :FirebaseUser){
        val ref = cloudDB.collection("users").document(currentUser.uid)
        ref.get().addOnSuccessListener {
            if (it.data?.get("role")?.toString() == "Patient"){
                startActivity(Intent(this, PatientHomeActivity::class.java))
            }
            else if (it.data?.get("role")?.toString() == "Psychiatrist"){
//                startActivity(Intent(this, PsychiatristHomeActivity::class.java))
            }
            finish()
        }
    }

    private fun signOutAndStartSignInActivity() {
        auth.signOut()

        googleSignInClient.signOut().addOnCompleteListener(this) {
            // Optional: Update UI or show a message to the user
            val intent = Intent(this@ChooseRoleActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}