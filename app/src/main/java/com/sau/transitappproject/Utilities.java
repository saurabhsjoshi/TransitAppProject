package com.sau.transitappproject;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by saurabh on 2017-11-26.
 */

public class Utilities {

    public static Bundle getLatLngBundle(LatLng from, LatLng to) {
        Bundle args = new Bundle();
        args.putParcelable("from_latlng", from);
        args.putParcelable("to_latlng", to);
        return args;
    }


    public static com.google.maps.model.LatLng convertLatLng(LatLng latLng) {
        return new com.google.maps.model.LatLng(latLng.latitude, latLng.longitude);
    }
}
