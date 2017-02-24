package com.sau.transitappproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.MapView;

public class MainActivity extends AppCompatActivity {

    MapView m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m = (MapView) findViewById(R.id.mapView);
    }
}
