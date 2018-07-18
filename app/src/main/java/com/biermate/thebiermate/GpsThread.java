package com.biermate.thebiermate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.things.contrib.driver.gps.NmeaGpsDriver;

import java.io.IOException;

class GpsThread extends Thread {


    private static final String TAG = "GpsActivity";
    public static final int UART_BAUD = 9600;
    public static final float ACCURACY = 2.5f; // From GPS datasheet
    private LocationManager mLocationManager;
    private NmeaGpsDriver mGpsDriver;
    private static final String UART_NAME = "UART0";

    private SampleApplication context;

    public GpsThread(SampleApplication context) {
        this.context = context;
    }

    public void run() {

        try {
            // Register the GPS driver
            mGpsDriver = new NmeaGpsDriver(context, UART_NAME,
                    UART_BAUD, ACCURACY);
            mGpsDriver.register();

            // Register for location updates
//            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                    0, 0, mLocationListener);
//            mLocationManager.registerGnssStatusCallback(mStatusCallback);
//            mLocationManager.addNmeaListener(mMessageListener);
////
        } catch (IOException e) {
            Log.w(TAG, "Unable to open GPS UART", e);
        }
//      register gps driver
    }

    protected void finalize() throws Throwable {
        super.finalize();
        // Verify permission was granted

        if (mGpsDriver != null) {
            // Unregister components
            mGpsDriver.unregister();
            try {
                mGpsDriver.close();
            } catch (IOException e) {
                Log.w(TAG, "Unable to close GPS driver", e);
            }
        }

    }
}
