package com.example.konsulsehat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.konsulsehat.loginRegister.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class ProfileFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userLoggedIn: String
    private var userSaldo: Long? = null
    private lateinit var tvFullName: EditText
    private lateinit var tvEmail: EditText
    private lateinit var tvPhoneNum: EditText
    private lateinit var tvBirthdate: EditText
    private lateinit var tvfotoprofile: ImageView
    private lateinit var tvSaldoProfile: TextView
    private lateinit var btnSave: Button
    private lateinit var btnLogout: Button
    private lateinit var btnTopUp: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        tvFullName = rootView.findViewById(R.id.tvSearchDoctor)
        tvEmail = rootView.findViewById(R.id.editTextTextEmailAddress)
        tvPhoneNum = rootView.findViewById(R.id.editTextPhone)
        tvBirthdate = rootView.findViewById(R.id.editTextDate)
        tvfotoprofile = rootView.findViewById(R.id.imgProfileAccount)
        btnSave = rootView.findViewById(R.id.btnSaveChangesProfile)
        btnLogout = rootView.findViewById(R.id.btnLogoutProfile)
        btnTopUp = rootView.findViewById(R.id.btnTopUpSaldoProfile)
        tvSaldoProfile = rootView.findViewById(R.id.tvSaldoProfile)
        auth = FirebaseAuth.getInstance()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.loggedInUser.observe(viewLifecycleOwner, Observer { loggedInUser ->
            userLoggedIn = loggedInUser.toString()

            db.collection("users")
                .whereEqualTo("email", userLoggedIn)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val userData = document.data

                        userSaldo = userData["saldo"] as Long
                        sharedViewModel.setSaldo(userSaldo!!)

                        tvFullName.setText(userData["name"] as? String)
                        tvEmail.setText(userData["email"] as? String)
                        tvPhoneNum.setText(userData["phoneNum"] as? String ?: "")
                        tvBirthdate.setText(userData["birthdate"] as? String ?: "")
                        tvSaldoProfile.setText(formatToRupiah(userSaldo!!))

                        val profilePictUrl = document.getString("profile_pict")
                        profilePictUrl?.let {
                            Glide.with(requireContext())
                                .load(it)
                                .into(tvfotoprofile)
                        }
                        val name = userData["name"] as? String
                        Toast.makeText(context, name, Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreData", "Error getting user data: ", exception)
                }
        })

        sharedViewModel.saldoUser.observe(viewLifecycleOwner, Observer { userSaldo->
            tvSaldoProfile.setText(formatToRupiah(userSaldo!!))
        })

        btnLogout.setOnClickListener {
            signOutAndStartSignInActivity()
        }

        btnTopUp.setOnClickListener {
            val showTopUp = TopUpFragment()
            showTopUp.show((activity as AppCompatActivity).supportFragmentManager, "showTopUp")
        }

        btnSave.setOnClickListener {
            val updatedName = tvFullName.text.toString().trim()
            val updatedEmail = tvEmail.text.toString().trim()
            val updatedPhoneNum = tvPhoneNum.text.toString().trim()
            val updatedBirthdate = tvBirthdate.text.toString().trim()

            if (updatedName.isNotEmpty() && updatedEmail.isNotEmpty() && updatedPhoneNum.isNotEmpty() && updatedBirthdate.isNotEmpty()) {
                val db = FirebaseFirestore.getInstance()
                db.collection("users")
                    .whereEqualTo("email", userLoggedIn)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            // Update fields
                            val userRef = db.collection("users").document(document.id)
                            userRef.update(mapOf(
                                "name" to updatedName,
                                "email" to updatedEmail,
                                "phoneNum" to updatedPhoneNum,
                                "birthdate" to updatedBirthdate
                            ))
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("FirestoreData", "Error updating document", e)
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("FirestoreData", "Error getting user data: ", exception)
                    }
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()
    }

    private fun formatToRupiah(number: Long): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        val formattedNumber = numberFormat.format(number)
        return formattedNumber.replace("Rp", "Rp ").replace(",00", "")
    }
    private fun signOutAndStartSignInActivity() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
            // Optional: Update UI or show a message to the user
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
