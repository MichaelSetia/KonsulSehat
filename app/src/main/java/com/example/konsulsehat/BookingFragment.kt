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

    private var patient_age: Int? = null
    private lateinit var patient_email:String
    private lateinit var patient_phone:String
    private lateinit var patient_name:String
    private lateinit var patient_profile_pict:String
    private lateinit var psychiatrist_email:String
    private var pricing:Double? = 0.0



//    private var pricing : Int? = null
//    private var dokter_id : String? = null


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
                    val dokter_profilePictUrl = document.getString("profile_pict")
                    psychiatrist_email = document.getString("profile_pict")!!
//                    dokter_id = document.id
                    var tempPricing = document.get("price").toString() ?: null
                    pricing = tempPricing?.toDouble() ?: 0.0

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

            db.collection("appointment")
                .add(appointment)
                .addOnSuccessListener {

//                    SdkUIFlowBuilder.init()
//                        .setContext(requireContext())
//                        .setClientKey("SB-Mid-client-g2JImFSrBtMatNET") // Set your client key
//                        .enableLog(true)
//                        .setTransactionFinishedCallback( TransactionFinishedCallback {result ->
//                            when (result?.status) {
//                                TransactionResult.STATUS_SUCCESS -> {
//                                    // Payment success
//                                }
//                                TransactionResult.STATUS_PENDING -> {
//                                    // Payment pending
//                                }
//                                TransactionResult.STATUS_FAILED -> {
//                                    // Payment failed
//                                }
//                                TransactionResult.STATUS_INVALID -> {
//                                    // Payment invalid
//                                }
//                            }
//                        })
//                        .setColorTheme(CustomColorTheme("#5AA7A7", "#244242", "#F6F7F7"))
//                        .buildSDK()

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


//                    val intent = Intent(requireContext(), MidtransPaymentActivity::class.java)
//                    intent.putExtra(EXTRA_TRANSACTION_REQUEST, transactionRequest)
//                    startActivityForResult(intent, REQUEST_CODE_PAYMENT)

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

