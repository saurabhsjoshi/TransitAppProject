package com.sau.transitappproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView lbl_duration;
    private TextView lbl_distance;
    private LatLng from;
    private LatLng to;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Bundle bundle = getIntent().getBundleExtra("latlng");
        from = bundle.getParcelable("from_latlng");
        to = bundle.getParcelable("to_latlng");
        lbl_duration = findViewById(R.id.lbl_duration);
        lbl_distance = findViewById(R.id.lbl_distance);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }







    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLng(from));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

        mMap.addMarker(new MarkerOptions().position(from).title("From"));
        mMap.addMarker(new MarkerOptions().position(to).title("To"));

        GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyDdUnHQmANytORVX4bQAzdz8j_xqj3Mu28").build();
        DirectionsApiRequest request = DirectionsApi.newRequest(context)
                .origin(Utilities.convertLatLng(from))
                .destination(Utilities.convertLatLng(to))
                .mode(TravelMode.TRANSIT);
        try {
            DirectionsResult result = request.await();
            List<LatLng> decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());
            lbl_duration.setText(result.routes[0].legs[0].duration.humanReadable);
            lbl_distance.setText(result.routes[0].legs[0].distance.humanReadable);

            mMap.addPolyline(new PolylineOptions().addAll(decodedPath));

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onGoClick(View v) {
        Intent intent = new Intent(this, StepsActivity.class);
        intent.putExtra("latlng", Utilities.getLatLngBundle(from, to));
        startActivity(intent);
    }
}
