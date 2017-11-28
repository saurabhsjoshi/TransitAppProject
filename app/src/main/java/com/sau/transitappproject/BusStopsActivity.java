package com.sau.transitappproject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.opencsv.CSVReader;
import com.sau.transitappproject.model.BusStop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BusStopsActivity extends FragmentActivity implements OnMapReadyCallback {

    private ArrayList<BusStop> busStops;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busStops = new ArrayList<>();
        loadBusStops();
        setContentView(R.layout.activity_bus_stops);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void loadBusStops() {
        try{
            CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.bus_stops_windsor))));
            String[] line;
            //Skip title
            reader.readNext();
            while ((line = reader.readNext()) != null) {
                busStops.add(new BusStop(line[1], line[4], line[5], Double.parseDouble(line[7]), Double.parseDouble(line[6])));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public com.google.maps.model.LatLng getLatLng(LatLng latLng) {
        return new com.google.maps.model.LatLng(latLng.latitude, latLng.longitude);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng windsor = new LatLng(42.3149, -83.0364);


        mMap.moveCamera(CameraUpdateFactory.newLatLng(windsor));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

        for(BusStop busStop: busStops)
            mMap.addMarker(new MarkerOptions().position(busStop.getLatLng()).title(busStop.getRoute_name()));

    }
}
