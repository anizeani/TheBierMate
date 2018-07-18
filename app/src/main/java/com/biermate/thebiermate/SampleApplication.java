package com.biermate.thebiermate;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

//import android.location.GnssStatus;
//import android.location.OnNmeaMessageListener;
//import android.util.Log;
//import android.content.pm.PackageManager;
//import android.location.GnssStatus;
//import android.location.Location;
//import android.location.LocationListener;
import android.location.LocationManager;
//import android.location.OnNmeaMessageListener;
//import android.os.Bundle;

import com.google.android.things.contrib.driver.gps.NmeaGpsDriver;

import com.sumup.merchant.api.SumUpState;
import java.io.IOException;

public class SampleApplication extends Application {

    private static final String TAG = "GpsActivity";
    public static final int UART_BAUD = 9600;
    public static final float ACCURACY = 2.5f; // From GPS datasheet
    private LocationManager mLocationManager;
    private NmeaGpsDriver mGpsDriver;
    private static final String UART_NAME = "UART0";

    @Override
    public void onCreate() {
        super.onCreate();
        // mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // We need permission to get location updates
//        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // A problem occurred auto-granting the permission
//            Log.d(TAG, "No permission");
//            return;
//        }
        GpsThread t = new GpsThread(this);
        t.start();
        SumUpState.init(this);
//    Report location updates
        /*
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.v(TAG, "Location update: " + location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    };

    // Report satellite status
    private GnssStatus.Callback mStatusCallback = new GnssStatus.Callback() {
        @Override
        public void onStarted() { Log.i(TAG, "teststart");}

        @Override
        public void onStopped() { }

        @Override
        public void onFirstFix(int ttffMillis) { }

        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            Log.v(TAG, "GNSS Status: " + status.getSatelliteCount() + " satellites.");
        }
    };

    // Report raw NMEA messages
    private OnNmeaMessageListener mMessageListener = new OnNmeaMessageListener() {
        @Override
        public void onNmeaMessage(String message, long timestamp) {
            Log.v(TAG, "NMEA: " + message);
        }
    };
*/
//    protected void finalize() throws Throwable {
//        super.finalize();
//        // Verify permission was granted
//        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "No permission");
//            return;
//        }
//
//        if (mGpsDriver != null) {
//            // Unregister components
//            mGpsDriver.unregister();
//            try {
//                mGpsDriver.close();
//            } catch (IOException e) {
//                Log.w(TAG, "Unable to close GPS driver", e);
//            }
//        }
//
//    }
    }

}
