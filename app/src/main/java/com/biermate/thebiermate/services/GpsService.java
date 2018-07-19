package com.biermate.thebiermate.services;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.things.contrib.driver.gps.NmeaGpsDriver;

import java.io.IOException;

public class GpsService extends Service {

    public static final String ACTION_START_GPS_SERVICE = "ACTION_START_GPS_SERVICE";
    public static final String ACTION_STOP_GPS_SERVICE = "ACTION_STOP_GPS_SERVICE";

    private static final String TAG = "GpsService";
    public static final int UART_BAUD = 9600;
    public static final float ACCURACY = 2.5f; // From GPS datasheet
    private static final String UART_NAME = "UART0";

    private LocationManager mLocationManager;
    private NmeaGpsDriver mGpsDriver;

    public GpsService(){}

    /* Used to build and start gps-service. */
    private void startGpsService() {
        Log.i(TAG, "Start gps service.");
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // We need permission to get location updates
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // A problem occurred auto-granting the permission
            Log.d(TAG, "No permission");
//            return TODO;
        }

        try {
            // Register the GPS driver
            mGpsDriver = new NmeaGpsDriver(this, UART_NAME, UART_BAUD, ACCURACY);
            mGpsDriver.register();
            // Register for location updates
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, mLocationListener);
            mLocationManager.registerGnssStatusCallback(mStatusCallback);
            mLocationManager.addNmeaListener(mMessageListener);
        } catch (IOException e) {
            Log.w(TAG, "Unable to open GPS UART", e);
        }

        Log.i(TAG, "gps service started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null)
        {
            String action = intent.getAction();
            if(action!=null) {
                switch (action) {
                    case ACTION_START_GPS_SERVICE:
                        startGpsService();
                        Toast.makeText(getApplicationContext(), "gps service is started.", Toast.LENGTH_LONG).show();
                        break;
                    case ACTION_STOP_GPS_SERVICE:
                        Boolean stopped = stopGpsService();
                        Toast.makeText(getApplicationContext(), stopped ? "gps service is stopped." : "gps service not stopped properly", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean stopGpsService() {
        // Verify permission was granted
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No permission");
            return false;
        }

        if (mGpsDriver != null) {
            // Unregister components
            mGpsDriver.unregister();
            mLocationManager.removeUpdates(mLocationListener);
            mLocationManager.unregisterGnssStatusCallback(mStatusCallback);
            mLocationManager.removeNmeaListener(mMessageListener);
            try {
                mGpsDriver.close();
            } catch (IOException e) {
                Log.w(TAG, "Unable to close GPS driver", e);
                return false;
            }
        }
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        stopGpsService();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** Report location updates */
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

    /** Report satellite status */
    private GnssStatus.Callback mStatusCallback = new GnssStatus.Callback() {
        @Override
        public void onStarted() { }

        @Override
        public void onStopped() { }

        @Override
        public void onFirstFix(int ttffMillis) { }

        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            Log.v(TAG, "GNSS Status: " + status.getSatelliteCount() + " satellites.");
        }
    };

    /** Report raw NMEA messages */
    private OnNmeaMessageListener mMessageListener = new OnNmeaMessageListener() {
        @Override
        public void onNmeaMessage(String message, long timestamp) {
            Log.v(TAG, "NMEA: " + message);
        }
    };
}
