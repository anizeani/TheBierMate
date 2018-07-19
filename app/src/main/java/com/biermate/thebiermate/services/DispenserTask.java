package com.biermate.thebiermate.services;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.biermate.thebiermate.MainActivity;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class DispenserTask extends AsyncTask<Void, DispenserStatus, Void> {

    private static final String GPIO_NAME = "BCM26";
    private static final String TAG = "DispenserTask";

    private PeripheralManager peripheralManager = PeripheralManager.getInstance();
    private Gpio gpio;
    private boolean error;

    OnDataSendToActivity dataSendToActivity;

    public DispenserTask(Activity activity) {
        dataSendToActivity = (OnDataSendToActivity)activity;
    }

    @Override
    protected void onPreExecute(){
        error = false;
        List<String> portList = peripheralManager.getGpioList();
        if (portList.isEmpty()) {
            Log.i(TAG, "No GPIO port available on this device.");
            error = true;
        } else {
            Log.i(TAG, "List of available ports: " + portList);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Running on Worker Thread
        //TODO while only for testing
        while (!error && !isCancelled()) {
            try {
                gpio = peripheralManager.openGpio(GPIO_NAME);
                gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
                Log.i(TAG, GPIO_NAME + "is high");
                publishProgress(DispenserStatus.ON);

                Thread.sleep(1000);

                gpio.setValue(true);
                gpio.close();
                publishProgress(DispenserStatus.OFF);
            } catch (InterruptedException | IOException e) {
                Log.w(TAG, "Unable to access GPIO", e);
                error = true;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (gpio != null) {
            try {
                gpio.close();
                gpio = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close GPIO", e);
            }
        }
    }

    @Override
    protected void onProgressUpdate(DispenserStatus... values) {
        super.onProgressUpdate(values);
        //TODO update UI-Activity
        if (dataSendToActivity != null && values.length > 0) {
            dataSendToActivity.sendData(values[0]);
        }
    }
}

