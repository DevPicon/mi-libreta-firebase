package com.devpicon.android.milibreta.ubicacion;

import android.location.Location;
import android.util.Log;

/**
 * Created by armando on 8/1/16.
 */
public class TismartcabClientApplication {

    private static final String TAG = TismartcabClientApplication.class.getSimpleName();

    public static void setCurrentLocation(Location location) {
        Log.d(TAG, "Esto es:{" + location.getLatitude() + "|" + location.getLongitude() + "}");
    }
}
