package com.example.konsulsehat.Admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.konsulsehat.R
import com.example.konsulsehat.SharedViewModel
import com.example.konsulsehat.databinding.ActivityDetailMasterPatientBinding
import com.example.konsulsehat.databinding.ActivityDetailMasterTransaksiBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class DetailMasterTransaksiActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailMasterTransaksiBinding
    private lateinit var sharedViewModel: SharedViewModel
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_master_transaksi)

        binding = ActivityDetailMasterTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        val invoice_id = intent.getStringExtra("appointment_id")

        db.collection("appointment")
            .whereEqualTo("appointment_id", invoice_id)
            .get()
            .addOnSuccessListener {documents ->
                for (doc in documents) {
                    val appointment = doc.data
                    val psychiathristProfilePictUrl = doc.getString("psychiatrist_profile_pict")
                    psychiathristProfilePictUrl?.let {
                        Glide.with(this)
                            .load(it)
                            .into(binding.imgPsychiatristDetailTransaksi)
                    }
                    val patientProfilePictUrl = doc.getString("psychiatrist_profile_pict")
                    patientProfilePictUrl?.let {
                        Glide.with(this)
                            .load(it)
                            .into(binding.imgPatientDetailTransaksi)
                    }

                    val age = appointment["patient_age"] as Long
                    binding.tvPsychiatristNameDetailTransaksi.setText(appointment["psychiatrist_name"] as String)
                    binding.tvPatientNameDetailTransaksi.setText(appointment["patient_name"] as String)
                    binding.tvPatientAgeDetailTransaksi.setText(age.toString() + " y.o.")
                    binding.tvPatientDescDetailTransaksi.setText(appointment["patient_info"] as String)
                    var status = ""
                    if (appointment["appointment_status"] as Long == 2.toLong()){
                        status = "Appointment Paid - Upcoming"
                    }
                    else if (appointment["appointment_status"] as Long == 3.toLong()){
                        status = "Appointment In Progress"
                    }
                    else if (appointment["appointment_status"] as Long == 4.toLong()){
                        status = "Appointment Completed"
                    }
                    binding.tvStatusAppointmentDetailTransaksi.setText(status)
                    binding.tvFeeDetailTransaksi.setText(formatToRupiah(appointment["price"] as Long))
                    binding.tvAppointmentDateDetailTransaksi.setText(appointment["appointment_time"] as String)
                }
            }
            .addOnFailureListener {
                Log.e("FirestoreData", "Error getting user data: ", it)
            }

        binding.btnBackDetailMasterTransaksi.setOnClickListener{
            finish()
        }
    }


    private fun formatToRupiah(number: Long): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        val formattedNumber = numberFormat.format(number)
        return formattedNumber.replace("Rp", "Rp ").replace(",00", "")
    }
}