package com.example.konsulsehat

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class SharedViewModel : ViewModel() {
    private val _loggedInUser = MutableLiveData<String>()
    private val _saldoUser = MutableLiveData<Long>()
    val loggedInUser: LiveData<String> get() = _loggedInUser
    val saldoUser: LiveData<Long> get() = _saldoUser

    fun setLoggedInUser(user: String) {
        _loggedInUser.value = user
    }
    fun setSaldo(saldo: Long) {
        _saldoUser.value = saldo
    }



//    fun setSaldoUser(user:String, updatedSaldo:Long){
//        val db = FirebaseFirestore.getInstance()
//        db.collection("users")
//            .whereEqualTo("email", user)
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val userRef = db.collection("users").document(document.id)
//
//
//                    userRef.update(mapOf(
//                        "saldo" to updatedSaldo,
//                    ))
//                    .addOnSuccessListener {
//                        Log.e( "FirestoreData","Saldo Updated!")
//                    }
//                    .addOnFailureListener { e ->
//                        Log.e("FirestoreData", "Update Saldo Error", e)
//                    }
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e("FirestoreData", "Error getting user data: ", exception)
//            }
//    }
}