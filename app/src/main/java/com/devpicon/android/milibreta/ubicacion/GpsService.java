package com.devpicon.android.milibreta.ubicacion;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by armando on 8/1/16.
 */
public class GpsService extends IntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final String LOCATION_ACTION = "LOCATION ACTION";
    public static final String LOCATION = "LOCATION";
    private static GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    public GpsService() {
        super(GpsService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void locationServiceUpdate() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    locationRequest, this);
        } catch (SecurityException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (googleApiClient == null) {
            buildGoogleApiClient();
        }
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

        if (locationRequest != null)
            locationServiceUpdate();

        return START_STICKY;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000); // 5 seg
        locationRequest.setFastestInterval(2000); // 5 seg
        locationServiceUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
        if (locationRequest != null)
            locationServiceUpdate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onLocationChanged(final Location location) {
        if (location != null) {
            try {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TismartcabClientApplication.setCurrentLocation(location);
                        Intent intent = new Intent();
                        intent.putExtra(LOCATION, location);
                        intent.setAction(LOCATION_ACTION);
                        sendBroadcast(intent);
                        //stopSelf();
                    }
                }, 3000);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (googleApiClient == null) {
            buildGoogleApiClient();
        }
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
        if (locationRequest != null)
            locationServiceUpdate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopSelf();
    }
}