package com.sau.transitappproject;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlacesOptions;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {
    private Place from;
    private Place to;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlaceAutocompleteFragment autocompleteFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_from);
        autocompleteFragment.setHint("Locating you...");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                from = place;
            }

            @Override
            public void onError(Status status) {
            }
        });

        PlaceAutocompleteFragment autocompleteFragment_to = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_to);
        autocompleteFragment_to.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                to = place;
            }

            @Override
            public void onError(Status status) {
            }
        });
        dispatchCurrentLocation();
    }

    public void dispatchCurrentLocation() {
        MainActivityPermissionsDispatcher.getCurrentLocationWithPermissionCheck(MainActivity.this);
    }

    public void dispatchPermission(View v) {
        MainActivityPermissionsDispatcher.startMapActivityWithPermissionCheck(MainActivity.this);
    }

    public void dispatchSvPermission(View v) {
        MainActivityPermissionsDispatcher.startStreetViewActivityWithPermissionCheck(MainActivity.this);
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void getCurrentLocation() {
        try{
//            fusedLocationProviderClient.getLastLocation()
//                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            if(location != null){
//
//
//                            }
//                        }
//                    });
            Task<PlaceLikelihoodBufferResponse> placeResult = Places.getPlaceDetectionClient(this, new PlacesOptions.Builder().build()).getCurrentPlace(null);
            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                    from = task.getResult().get(0).getPlace();
                    autocompleteFragment.setText("Current Location");

                }
            });
        }catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }


    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void startStreetViewActivity() {

//        startActivity(new Intent(this, StreetViewActivity.class));
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void startMapActivity() {
        if(from !=  null && to != null) {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("latlng",
                    Utilities.getLatLngBundle(from.getLatLng(), to.getLatLng()));
            startActivity(intent);
        }
    }

    public void dispatchBusStopPermission(View v) {
        startActivity(new Intent(this, BusStopsActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
