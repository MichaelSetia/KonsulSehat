package com.example.konsulsehat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.konsulsehat.R
import com.google.firebase.database.Transaction
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

class MidtransPaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_midtrans_payment)

        SdkUIFlowBuilder.init()
            .setContext(this)
            .setClientKey("SB-Mid-client-g2JImFSrBtMatNET") // Set your client key
            .enableLog(true)
            .setTransactionFinishedCallback( TransactionFinishedCallback {result ->
                when (result?.status) {
                    TransactionResult.STATUS_SUCCESS -> {
                        // Payment success
                    }
                    TransactionResult.STATUS_PENDING -> {
                        // Payment pending
                    }
                    TransactionResult.STATUS_FAILED -> {
                        // Payment failed
                    }
                    TransactionResult.STATUS_INVALID -> {
                        // Payment invalid
                    }
                }
            })
            .setColorTheme(CustomColorTheme("#5AA7A7", "#244242", "#F6F7F7"))
            .buildSDK()

//        val transactionRequest = TransactionRequest()

    }


}