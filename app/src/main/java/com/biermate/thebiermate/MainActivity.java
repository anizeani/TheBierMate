package com.biermate.thebiermate;

import com.biermate.thebiermate.services.DispenserStatus;
import com.biermate.thebiermate.services.DispenserTask;

import android.app.Activity;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.biermate.thebiermate.services.GpsService;
import com.biermate.thebiermate.services.OnDataSendToActivity;
import com.sumup.merchant.Models.TransactionInfo;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpLogin;
import com.sumup.merchant.api.SumUpPayment;

import java.math.BigDecimal;
import java.util.UUID;

public class MainActivity extends Activity implements OnDataSendToActivity{

    private final String TEST_MAFFILIATE_KEY = "14789aa2-d3fa-4424-aa90-d730974777cf";
    private final String TOKEN_URL = "https://api.sumup.com/token";
    private final String TEST_CLIENT_ID = "vsL81Z37q1iE0T1vscbYmY4tNyAV";
    private final String TEST_CLIENT_SECRET = "a7ab2fc133efd95065d4df7217d95a48384c17b79a17bb64c4806e075e0d03a9";

    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_PAYMENT = 2;
    private static final int REQUEST_CODE_PAYMENT_SETTINGS = 3;

    private TextView mResultCode;
    private TextView mResultMessage;
    private TextView mTxCode;
    private TextView mReceiptSent;
    private TextView mTxInfo;
    private Button mDispenseButton;

    private AsyncTask dispenserTask;
    private static final String STATE_RUNNING = "task.running";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(MainActivity.this, GpsService.class);
        intent.setAction(GpsService.ACTION_START_GPS_SERVICE);
        startService(intent);

        if ((savedInstanceState != null) && (savedInstanceState.containsKey(STATE_RUNNING))) {
            dispenserTask = new DispenserTask(this).execute();
        }

        setContentView(R.layout.activity_main);
        findViews();
        Button login = (Button) findViewById(R.id.button_login);
        login.setOnClickListener(v -> {
            // Please go to https://me.sumup.com/developers to get your Affiliate Key by entering the application ID of your app. (e.g. com.sumup.sdksampleapp)
            SumUpLogin sumupLogin = SumUpLogin.builder(TEST_MAFFILIATE_KEY).build();
            SumUpAPI.openLoginActivity(MainActivity.this, sumupLogin, REQUEST_CODE_LOGIN);
        });

        Button btnCharge = (Button) findViewById(R.id.button_charge);
        btnCharge.setOnClickListener(v -> {
                SumUpPayment payment = SumUpPayment.builder()
                        // mandatory parameters
                        .total(new BigDecimal("1.00")) // minimum 1.00
                        .currency(SumUpPayment.Currency.CHF)
                        // optional: add details
                        .title("Taxi Ride")
                        .receiptEmail("customer@mail.com")
                        .receiptSMS("+3531234567890")
                        // optional: Add metadata
                        .addAdditionalInfo("AccountId", "taxi0334")
                        .addAdditionalInfo("From", "Paris")
                        .addAdditionalInfo("To", "Berlin")
                        // optional: foreign transaction ID, must be unique!
                        .foreignTransactionId(UUID.randomUUID().toString()) // can not exceed 128 chars
                        .build();

                SumUpAPI.checkout(MainActivity.this, payment, REQUEST_CODE_PAYMENT);
        });

        Button paymentSettings = (Button) findViewById(R.id.button_payment_settings);
        paymentSettings.setOnClickListener(v ->  SumUpAPI.openPaymentSettingsActivity(MainActivity.this, REQUEST_CODE_PAYMENT_SETTINGS));

        Button prepareCardTerminal = (Button) findViewById(R.id.button_prepare_card_terminal);
        prepareCardTerminal.setOnClickListener(v -> SumUpAPI.prepareForCheckout() );

        Button btnLogout = (Button) findViewById(R.id.button_logout);
        btnLogout.setOnClickListener(v -> SumUpAPI.logout() );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        resetViews();

        switch (requestCode) {
            case REQUEST_CODE_LOGIN:
                if (data != null) {
                    Bundle extra = data.getExtras();
                    mResultCode.setText("Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE));
                    mResultMessage.setText("Message: " + extra.getString(SumUpAPI.Response.MESSAGE));
                }
                break;

            case REQUEST_CODE_PAYMENT:
                if (data != null) {
                    Bundle extra = data.getExtras();

                    mResultCode.setText("Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE));
                    mResultMessage.setText("Message: " + extra.getString(SumUpAPI.Response.MESSAGE));

                    String txCode = extra.getString(SumUpAPI.Response.TX_CODE);
                    mTxCode.setText(txCode == null ? "" : "Transaction Code: " + txCode);

                    boolean receiptSent = extra.getBoolean(SumUpAPI.Response.RECEIPT_SENT);
                    mReceiptSent.setText("Receipt sent: " + receiptSent);

                    TransactionInfo transactionInfo = extra.getParcelable(SumUpAPI.Response.TX_INFO);
                    mTxInfo.setText(transactionInfo == null ? "" : "Transaction Info : " + transactionInfo);

                    //TODO if transaction successfully > enable button
                    mDispenseButton.setEnabled(true);
                }
                break;

            case REQUEST_CODE_PAYMENT_SETTINGS:
                if (data != null) {
                    Bundle extra = data.getExtras();
                    mResultCode.setText("Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE));
                    mResultMessage.setText("Message: " + extra.getString(SumUpAPI.Response.MESSAGE));
                }
                break;

            default:
                break;
        }
    }

    private void resetViews() {
        mResultCode.setText("");
        mResultMessage.setText("");
        mTxCode.setText("");
        mReceiptSent.setText("");
        mTxInfo.setText("");
        mDispenseButton.setEnabled(false);
    }

    private void findViews() {
        mResultCode = (TextView) findViewById(R.id.result);
        mResultMessage = (TextView) findViewById(R.id.result_msg);
        mTxCode = (TextView) findViewById(R.id.tx_code);
        mReceiptSent = (TextView) findViewById(R.id.receipt_sent);
        mTxInfo = (TextView) findViewById(R.id.tx_info);
        mDispenseButton = (Button) findViewById(R.id.button_dispense);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dispenserTask != null) {
            outState.putBoolean(STATE_RUNNING, true);
            dispenserTask.cancel(true);
        }
    }

    private void toggleBeerSwitch() {
        if(dispenserTask == null) {
            dispenserTask = new DispenserTask(this).execute();
        }else {
            dispenserTask.cancel(true);
            dispenserTask = null;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void sendData(DispenserStatus status) {
        if(status.equals(DispenserStatus.OFF)) mDispenseButton.setEnabled(false);
        else mDispenseButton.setEnabled(true);
        mResultMessage.setText("Message: " + status);
    }
}
