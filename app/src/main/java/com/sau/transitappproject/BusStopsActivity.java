package com.sau.transitappproject;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.opencsv.CSVReader;
import com.sau.transitappproject.model.BusStop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BusStopsActivity extends FragmentActivity implements OnMapReadyCallback {

    private ArrayList<BusStop> busStops;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busStops = new ArrayList<>();

        setContentView(R.layout.activity_bus_stops);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
                                currentLocation = new Location(LocationManager.GPS_PROVIDER);
                                currentLocation.setLongitude(location.getLongitude());
                                currentLocation.setLatitude(location.getLatitude());
                                loadBusStops(currentLocation);
                                mapFragment.getMapAsync(BusStopsActivity.this);
                            }
                        }
                    });
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }


    }

    public void loadBusStops(Location currentLocation) {
        try{
            CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.bus_stops_windsor))));
            String[] line;
            //Skip title
            reader.readNext();
            while ((line = reader.readNext()) != null) {
                Location temp = new Location(LocationManager.GPS_PROVIDER);
                temp.setLatitude(Double.parseDouble(line[7]));
                temp.setLongitude(Double.parseDouble(line[6]));
                if(currentLocation.distanceTo(temp) < 300.0)
                    busStops.add(new BusStop(line[1], line[4], line[5], Double.parseDouble(line[7]), Double.parseDouble(line[6])));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public com.google.maps.model.LatLng getLatLng(LatLng latLng) {
        return new com.google.maps.model.LatLng(latLng.latitude, latLng.longitude);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15.0f));

        for(BusStop busStop: busStops)
            mMap.addMarker(new MarkerOptions().position(busStop.getLatLng()).title(busStop.getRoute_name()));
        try {
            googleMap.setMyLocationEnabled(true);
        }catch (SecurityException ex) {
            ex.printStackTrace();
        }

    }
}
