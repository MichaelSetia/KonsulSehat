const functions = require('firebase-functions');
const admin = require('firebase-admin');
const midtransClient = require('midtrans-client');

admin.initializeApp();

exports.createTransaction = functions.https.onRequest(async (req, res) => {
  // Dapatkan data dari request body
  const { order_id, gross_amount } = req.body;

  // Setup Midtrans client
  let snap = new midtransClient.Snap({
    isProduction: false,
    serverKey: 'SB-Mid-server-6t_qJu2voRGRgWdjqRAh7UGG'
  });

  // Buat parameter transaksi
  let parameter = {
    "transaction_details": {
      "order_id": order_id,
      "gross_amount": gross_amount
    },
    "credit_card": {
      "secure": true
    }
  };

  try {
    const transaction = await snap.createTransaction(parameter);
    res.status(200).send(transaction);
  } catch (e) {
    res.status(500).send(e.message);
  }
});
