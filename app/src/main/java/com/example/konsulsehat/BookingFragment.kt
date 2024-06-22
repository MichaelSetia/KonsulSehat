package com.example.konsulsehat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

class BookingFragment : Fragment() {
    private lateinit var imgDok: ImageView
    private lateinit var namaDok: TextView
    private lateinit var hargaDok: TextView
    private lateinit var deskDok: TextView
    private lateinit var rateDok: TextView
    private lateinit var btnCancel:Button
    private lateinit var btnPayment:Button
    private lateinit var patientInfo: TextInputEditText
    private lateinit var dateKonseling:CalendarView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var dokter_profilePictUrl:String
    private var patient_age: Int? = null
    private lateinit var patient_email:String
    private lateinit var patient_phone:String
    private lateinit var patient_name:String
    private lateinit var patient_profile_pict:String
    private lateinit var psychiatrist_email:String
    private var pricing:Double? = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_booking, container, false)

        // Initialize views
        imgDok = view.findViewById(R.id.imageView10)
        namaDok = view.findViewById(R.id.tvBookingNama)
        hargaDok = view.findViewById(R.id.tvBookingHarga)
        deskDok = view.findViewById(R.id.tvBookingDeskripsi)
        rateDok = view.findViewById(R.id.tvBookingStar)
        btnCancel = view.findViewById(R.id.btnBookingCancel)
        btnPayment = view.findViewById(R.id.btnBookingPayment)
        patientInfo = view.findViewById(R.id.txtPatientInfo)
        dateKonseling = view.findViewById(R.id.inpDateKonseling)
        auth = FirebaseAuth.getInstance()

        btnCancel.setOnClickListener{
            requireActivity().onBackPressed()
        }
        val dokter_email = arguments?.getString("email")
        db = FirebaseFirestore.getInstance()
        getPatient()

        db.collection("users")
            .whereEqualTo("email", dokter_email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Handle document data
                    val dokter_nama = document.getString("name")
                    val dokter_gelar = document.getString("gelar")
                    val dokter_deskripsi = document.getString("deskripsi")
                    val dokter_harga = document.getString("price")
                    val dokter_rating = document.getDouble("rating")
                    dokter_profilePictUrl = document.getString("profile_pict")!!
                    psychiatrist_email = document.getString("email")!!
//                    dokter_id = document.id
                    if (document.getString("rate") != null){
                        pricing = document.getString("rate")!!.toDouble()
                    }
                    else {
                        pricing = 100000.toDouble()
                    }

                    namaDok.text = dokter_nama
                    hargaDok.text = dokter_harga
                    deskDok.text = dokter_deskripsi
                    rateDok.text = "Rp " + dokter_rating.toString()

                    // Load image using Glide
                    dokter_profilePictUrl?.let {
                        Glide.with(requireContext())
                            .load(it)
                            .into(imgDok)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting documents: ", exception)
            }
        return view
    }

    fun getPatient(){
        db.collection("users")
            .whereEqualTo("email", auth.currentUser!!.email)
            .get()
            .addOnSuccessListener { documents ->
                val data = documents.documents[0]

                patient_age = calculateAge(data.getString("birthdate") ?: LocalDate.now().toString())
                patient_email = data.getString("email")!!
                patient_name = data.getString("name")!!
                patient_profile_pict = data.getString("profile_pict") ?: "-"
                patient_phone = data.getString("phoneNum") ?: "-"
            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting documents: ", exception)
            }
    }

    fun calculateAge(birthDateString: String): Int {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")  // Define the format of your birthdate string
        val birthDate = LocalDate.parse(birthDateString, formatter)

        val period = Period.between(birthDate, today)
        return period.years
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btnPayment.setOnClickListener{
            val patient_info = patientInfo.text.toString()
            val date = dateKonseling.date

            val appointment = hashMapOf(
                "patient_age" to patient_age,
                "patient_email" to patient_email,
                "patient_name" to patient_name,
                "patient_info" to patient_info,
                "patient_profile_pict" to patient_profile_pict,
                "psychiatrist_email" to psychiatrist_email,
                "psychiatrist_name" to namaDok.text,
                "appointment_status" to 1,
                "appointment_time" to date,
                "price" to pricing,
            )

            // Appointment Status:
            // 0 : cancelled
            // 1 : belum bayar
            // 2 : sudah bayar, appointment blm berlangsung / blm konsul
            // 3 : sudah konsul


            // ini dipindah ke codingan kalo sudah bayar
            val db = FirebaseFirestore.getInstance()
            db.collection("room_chat")
                .get()
                .addOnSuccessListener { result ->
                    var chatExists = false
                    var chatDocumentId: String? = null

                    for (document in result) {
                        val chatData = document.data
                        val user_1 = chatData["user_1"] as? String
                        val user_2 = chatData["user_2"] as? String

                        if ((user_1 == psychiatrist_email && user_2 == patient_email) || (user_1 == patient_email && user_2 == psychiatrist_email)) {
                            chatExists = true
                            chatDocumentId = document.id
                            break
                        }
                    }

                    if (chatExists) {
                        // Update the appointment time in the existing chat document
                        chatDocumentId?.let {
                            db.collection("room_chat").document(it)
                                .update("time_limit", date)
                                .addOnSuccessListener {
                                    Log.d("FirestoreData", "Appointment time updated successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("FirestoreData", "Error updating appointment time", e)
                                }
                        }
                    } else {
                        // Add a new chat document
                        val newChat = hashMapOf(
                            "user_1" to psychiatrist_email,
                            "user_1_name" to namaDok.text,
                            "user_2" to patient_email,
                            "user_2_name" to patient_name,
                            "profile_pict_user_1" to imgDok,
                            "profile_pict_user_2" to patient_profile_pict,
                            "time_limit" to date
                        )

                        db.collection("room_chat")
                            .add(newChat)
                            .addOnSuccessListener {
                                Log.d("FirestoreData", "New chat room created successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.w("FirestoreData", "Error creating new chat room", e)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("FirestoreData", "Error getting documents: ", exception)
                }

            db.collection("appointment")
                .add(appointment)
                .addOnSuccessListener {
                    SdkUIFlowBuilder.init()
                        .setContext(requireContext())
                        .setClientKey("SB-Mid-client-g2JImFSrBtMatNET") // Set your client key
                        .enableLog(true)
                        .setTransactionFinishedCallback( TransactionFinishedCallback {result ->
                            when (result?.status) {
                                TransactionResult.STATUS_SUCCESS -> {
                                    Toast.makeText(requireContext(), "Payment Success", Toast.LENGTH_LONG).show()
                                }
                                TransactionResult.STATUS_PENDING -> {
                                    Toast.makeText(requireContext(), "Payment Pending", Toast.LENGTH_LONG).show()
                                    // Payment pending
                                }
                                TransactionResult.STATUS_FAILED -> {
                                    Toast.makeText(requireContext(), "Payment Failed", Toast.LENGTH_LONG).show()
                                    // Payment failed
                                }
                                TransactionResult.STATUS_INVALID -> {
                                    Toast.makeText(requireContext(), "Payment Status Invalid", Toast.LENGTH_LONG).show()
                                    // Payment invalid
                                }
                            }
                        })
                        .setColorTheme(CustomColorTheme("#5AA7A7", "#244242", "#F6F7F7"))
                        .buildSDK()

                    val clientKey = "SB-Mid-client-g2JImFSrBtMatNET"
                    val transactionRequest = TransactionRequest(
                        System.currentTimeMillis().toString(), // Unique transaction ID
                        pricing!!, // Set total price
                    )
                    val detail = ItemDetails(
                        "PuxBIeQ9sm5spQJ", //random ID
                        pricing!!,
                        1,
                        "1 on 1 Consultation"
                        )
                    val itemDetails = ArrayList<ItemDetails>()
                    itemDetails.add(detail)
                    uiKitDetails(transactionRequest)
                    transactionRequest.itemDetails = itemDetails

                    MidtransSDK.getInstance().transactionRequest = transactionRequest
                    MidtransSDK.getInstance().startPaymentUiFlow(requireContext())

                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(),"Add to DB Failed : ${it.message}", Toast.LENGTH_LONG).show()
                }


        }
    }

    private fun uiKitDetails(transactionRequest: TransactionRequest) {
        val customerDetails = CustomerDetails()
        customerDetails.customerIdentifier = patient_email
        customerDetails.phone = patient_phone ?: "-"
    }
}

