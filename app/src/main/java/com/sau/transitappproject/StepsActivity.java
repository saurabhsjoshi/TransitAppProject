package com.sau.transitappproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.sau.transitappproject.adapter.StepsListAdapter;

import java.io.IOException;

public class StepsActivity extends AppCompatActivity {
    private LatLng from;
    private LatLng to;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(StepsActivity.this);
        recyclerView.setLayoutManager(llm);
        Bundle bundle = getIntent().getBundleExtra("latlng");
        from = bundle.getParcelable("from_latlng");
        to = bundle.getParcelable("to_latlng");
        GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyDdUnHQmANytORVX4bQAzdz8j_xqj3Mu28").build();

        DirectionsApiRequest request = DirectionsApi.newRequest(context)
                .origin(Utilities.convertLatLng(from))
                .destination(Utilities.convertLatLng(to))
                .mode(TravelMode.TRANSIT);

        try {
            DirectionsResult result = request.await();
            recyclerView.setAdapter(new StepsListAdapter(StepsActivity.this, result.routes[0].legs[0].steps));

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
