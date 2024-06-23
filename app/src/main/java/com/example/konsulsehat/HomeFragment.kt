package com.example.konsulsehat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.konsulsehat.Admin.AdminFragmentActivity
import com.example.konsulsehat.databinding.FragmentHomeBinding
import com.example.konsulsehat.dokter.FragmentDokterActivity
import com.example.konsulsehat.loginRegister.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val cloudDB = Firebase.firestore
    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        displayUserName(currentUser)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

//        binding.btnLogout.setOnClickListener {
//            signOutAndStartSignInActivity()
//        }
    }

    private fun displayUserName(currentUser: FirebaseUser?) {
        currentUser?.let {
            var userName:String = ""
            if (it.displayName?.isEmpty() == false){
                userName = it.displayName!!
            }
            else {
                db.collection("users").whereEqualTo("email", it.email).get()
                    .addOnSuccessListener { documents ->
                        val document = documents.documents[0]
                        if (!documents.isEmpty) {
                            userName = document.getString("name") ?: "User"
                        } else {
                            // Debug log
                            Log.d("SignInActivity", "No document found for email: ${document.getString("email")!!}")
                            Toast.makeText(requireContext(), "No such document!", Toast.LENGTH_LONG).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Debug log
                        Log.e("SignInActivity", "Failed to fetch role", exception)
                        Toast.makeText(requireContext(), "Failed to fetch role: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
            }

            binding.tvUname.text = "$userName!"
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}