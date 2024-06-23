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
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import androidx.lifecycle.Observer
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookingFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var userLoggedIn: String
    private var userSaldo: Long? = null
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
    private  lateinit var date :String
    private  lateinit var userRef :QueryDocumentSnapshot
    var pricing:Long? = 0

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

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.loggedInUser.observe(viewLifecycleOwner, Observer { loggedInUser ->
            userLoggedIn = loggedInUser.toString()
            getPatient()
        })

        btnCancel.setOnClickListener{
            requireActivity().onBackPressed()
        }
        val dokter_email = arguments?.getString("email")
        db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("email", dokter_email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Handle document data
                    val dokter_nama = document.data["name"] as String
                    val dokter_gelar = document.data["gelar"] as String? ?: ""
                    val dokter_deskripsi = document.data["deskripsi"] as String? ?: ""
                    val dokter_harga = formatToRupiah(document.data["price"] as Long? ?: 100000)
                    val dokter_rating = document.data["rate"] as Long? ?: 0
                    dokter_profilePictUrl = document.data["profile_pict"] as String? ?: ""
                    psychiatrist_email = document.data["email"] as String
                    pricing = document.data["price"] as Long? ?: 100000

                    namaDok.text = dokter_nama
                    hargaDok.text = dokter_harga
                    deskDok.text = dokter_deskripsi
                    rateDok.text = dokter_rating.toString()

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
            .whereEqualTo("email", userLoggedIn)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents){
                    userRef = doc
                    val data = doc.data
                    patient_age = calculateAge(data["birthdate"] as String? ?: LocalDate.now().toString())
                    patient_email = data["email"] as String
                    patient_name = data["name"] as String
                    patient_profile_pict = data["profile_pict"] as String? ?: "-"
                    patient_phone = data["phoneNum"] as String? ?: "-"
                }

            }
            .addOnFailureListener { exception ->
                Log.w("FirestoreData", "Error getting documents: ", exception)
            }
    }

    fun calculateAge(birthDateString: String): Int {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")  // Define the format of your birthdate string
        val birthDate = LocalDate.parse(birthDateString, formatter)

        val period = Period.between(birthDate, today)
        return period.years
    }

    private fun formatToRupiah(number: Long): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        val formattedNumber = numberFormat.format(number)
        return formattedNumber.replace("Rp", "Rp ").replace(",00", "")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btnPayment.setOnClickListener {
            db.collection("users")
                .whereEqualTo("email", userLoggedIn)
                .get()
                .addOnSuccessListener { documents ->
                    var data = documents.first()

                    var saldoUser = data!!.get("saldo") as Long
                    if (saldoUser < pricing!!) {
                        Toast.makeText(
                            requireContext(),
                            "Transaction declined due to insufficient balance",
                            Toast.LENGTH_LONG
                        ).show()
                        return@addOnSuccessListener
                    }

                    var updatedSaldo = saldoUser.toInt() - pricing!!

                    val userRef = db.collection("users").document(data.id)
                    userRef.update(
                        mapOf(
                            "saldo" to updatedSaldo,
                        )
                    )
                        .addOnSuccessListener {
                            val patient_info = patientInfo.text.toString()
                            sharedViewModel.setSaldo(updatedSaldo.toLong())

                            val appointment = hashMapOf(
                                "appointment_id" to generateAppointmentId(),
                                "patient_age" to patient_age,
                                "patient_email" to patient_email,
                                "patient_name" to patient_name,
                                "patient_info" to patient_info,
                                "patient_profile_pict" to patient_profile_pict,
                                "psychiatrist_profile_pict" to dokter_profilePictUrl,
                                "psychiatrist_email" to psychiatrist_email,
                                "psychiatrist_name" to namaDok.text,
                                "appointment_status" to 2,
                                "appointment_time" to date,
                                "price" to pricing,
                            )

                            // Appointment Status:
                            // 0 : cancelled
                            // 1 : belum bayar
                            // 2 : sudah bayar, appointment blm berlangsung / blm konsul
                            // 3 : konsul on going
                            // 4 : sudah konsul

                            db.collection("appointment")
                                .add(appointment)
                                .addOnSuccessListener {
                                    // ini dipindah ke codingan kalo sudah bayar
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
                                                            Log.d(
                                                                "FirestoreData",
                                                                "Appointment time updated successfully"
                                                            )
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Log.w(
                                                                "FirestoreData",
                                                                "Error updating appointment time",
                                                                e
                                                            )
                                                        }
                                                }
                                            } else {
                                                // Add a new chat document
                                                val newChat = hashMapOf(
                                                    "user_1" to psychiatrist_email,
                                                    "user_1_name" to namaDok.text,
                                                    "user_2" to patient_email,
                                                    "user_2_name" to patient_name,
                                                    "profile_pict_user_1" to dokter_profilePictUrl,
                                                    "profile_pict_user_2" to patient_profile_pict,
                                                    "time_limit" to date
                                                )

                                                db.collection("room_chat")
                                                    .add(newChat)
                                                    .addOnSuccessListener {
                                                        Log.d(
                                                            "FirestoreData",
                                                            "New chat room created successfully"
                                                        )
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.w(
                                                            "FirestoreData",
                                                            "Error creating new chat room",
                                                            e
                                                        )
                                                    }
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.w(
                                                "FirestoreData",
                                                "Error getting documents: ",
                                                exception
                                            )
                                        }
                                }
                                .addOnFailureListener {
                                    Log.w(
                                        "FirestoreData",
                                        "Error add appointment to FireStore: ",
                                        it
                                    )
                                }

                            Toast.makeText(
                                requireActivity(),
                                "Appointment request successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                            requireActivity().onBackPressed()
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirestoreData", "Appointment request failed", e)
                        }
                }
                .addOnFailureListener { ex ->
                    Toast.makeText(requireActivity(), "Data Fetching Failed", Toast.LENGTH_LONG)
                        .show()
                }
        }

        dateKonseling.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // The user has selected a new date
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            // Do something with the selected date (e.g., display it, store it)
            date = SimpleDateFormat("dd/MM/yyyy").format(selectedDate.time)
        }
    }

    fun generateAppointmentId(): String {
        val initials = patient_name.trim(' ').substring(0, 1) + namaDok.text.trim(' ').substring(0, 1)
        val randomInt = (1000..9999).random()
        return "$initials-$randomInt"
    }
}
//            db.collection("appointment")
//                .add(appointment)
//                .addOnSuccessListener {
//                    SdkUIFlowBuilder.init()
//                        .setContext(requireContext())
//                        .setClientKey("SB-Mid-client-g2JImFSrBtMatNET") // Set your client key
//                        .enableLog(true)
//                        .setTransactionFinishedCallback( TransactionFinishedCallback {result ->
//                            when (result?.status) {
//                                TransactionResult.STATUS_SUCCESS -> {
//                                    Toast.makeText(requireContext(), "Payment Success", Toast.LENGTH_LONG).show()
//                                }
//                                TransactionResult.STATUS_PENDING -> {
//                                    Toast.makeText(requireContext(), "Payment Pending", Toast.LENGTH_LONG).show()
//                                    // Payment pending
//                                }
//                                TransactionResult.STATUS_FAILED -> {
//                                    Toast.makeText(requireContext(), "Payment Failed", Toast.LENGTH_LONG).show()
//                                    // Payment failed
//                                }
//                                TransactionResult.STATUS_INVALID -> {
//                                    Toast.makeText(requireContext(), "Payment Status Invalid", Toast.LENGTH_LONG).show()
//                                    // Payment invalid
//                                }
//                            }
//                        })
//                        .setColorTheme(CustomColorTheme("#5AA7A7", "#244242", "#F6F7F7"))
//                        .buildSDK()
//
//                    val clientKey = "SB-Mid-client-g2JImFSrBtMatNET"
//                    val transactionRequest = TransactionRequest(
//                        System.currentTimeMillis().toString(), // Unique transaction ID
//                        pricing!!, // Set total price
//                    )
//                    val detail = ItemDetails(
//                        "PuxBIeQ9sm5spQJ", //random ID
//                        pricing!!,
//                        1,
//                        "1 on 1 Consultation"
//                        )
//                    val itemDetails = ArrayList<ItemDetails>()
//                    itemDetails.add(detail)
//                    uiKitDetails(transactionRequest)
//                    transactionRequest.itemDetails = itemDetails
//
//                    MidtransSDK.getInstance().transactionRequest = transactionRequest
//                    MidtransSDK.getInstance().startPaymentUiFlow(requireContext())
//
//                }
//                .addOnFailureListener {
//                    Toast.makeText(requireContext(),"Add to DB Failed : ${it.message}", Toast.LENGTH_LONG).show()
//                }
//



//    private fun uiKitDetails(transactionRequest: TransactionRequest) {
//        val customerDetails = CustomerDetails()
//        customerDetails.customerIdentifier = patient_email
//        customerDetails.phone = patient_phone ?: "-"
//    }

