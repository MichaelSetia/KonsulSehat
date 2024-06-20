package com.example.konsulsehat.dokter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.konsulsehat.R
import com.example.konsulsehat.R.id.imgProfileDokter1
import com.example.konsulsehat.SharedViewModel
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileDokterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileDokterFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userLoggedIn: String
    private lateinit var tvNamaDokter: EditText
    private lateinit var tvEmailDokter: EditText
    private lateinit var tvDeskripsi: EditText
    private lateinit var tvTelpDokter: EditText
    private lateinit var tvUltahDokter: EditText
    private lateinit var tvFotoProfileDokter: ImageView
    private lateinit var btnSaveChangesDokter: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_profile_dokter, container, false)
        tvNamaDokter = rootView.findViewById(R.id.txtNamaDokter)
        tvEmailDokter = rootView.findViewById(R.id.txtEmailDokter)
        tvDeskripsi = rootView.findViewById(R.id.txtDeskripsi)
        tvTelpDokter = rootView.findViewById(R.id.txtTelpDokter)
        tvUltahDokter = rootView.findViewById(R.id.txtUltahDokter)
        tvFotoProfileDokter = rootView.findViewById(imgProfileDokter1)
        btnSaveChangesDokter = rootView.findViewById(R.id.btnSaveChangesDokter)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.loggedInUser.observe(viewLifecycleOwner, Observer { loggedInUser ->
            userLoggedIn = loggedInUser.toString()

            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .whereEqualTo("email", userLoggedIn)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val userData = document.data
                        tvNamaDokter.setText(userData["name"] as? String)
                        tvEmailDokter.setText(userData["email"] as? String)
                        tvDeskripsi.setText(userData["deskripsi"] as? String ?: "")
                        tvTelpDokter.setText(userData["phoneNum"] as? String ?: "")
                        tvUltahDokter.setText(userData["birthdate"] as? String ?: "")

                        val profilePictUrl = document.getString("profile_pict")
                        profilePictUrl?.let {
                            Glide.with(requireContext())
                                .load(it)
                                .into(tvFotoProfileDokter)
                        }
                        val name = userData["name"] as? String
                        Toast.makeText(context, name, Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreData", "Error getting user data: ", exception)
                }
        })

        btnSaveChangesDokter.setOnClickListener {
            val updatedName = tvNamaDokter.text.toString().trim()
            val updatedEmail = tvEmailDokter.text.toString().trim()
            val updatedDeskripsi = tvDeskripsi.text.toString().trim()
            val updatedPhoneNum = tvTelpDokter.text.toString().trim()
            val updatedBirthdate = tvUltahDokter.text.toString().trim()

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
                                "deskripsi" to updatedDeskripsi,
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

//        return inflater.inflate(R.layout.fragment_profile_dokter, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileDokterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileDokterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}