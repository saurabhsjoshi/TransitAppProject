package com.sau.transitappproject;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TravelMode;
import com.sau.transitappproject.adapter.StepsListAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class StepsActivity extends AppCompatActivity implements

        ResultCallback<Status>
{
    private LatLng from;
    private LatLng to;

    private GeofencingClient geofencingClient;
    protected ArrayList<Geofence> mGeofenceList;


    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Toast.makeText(
                    this,
                    "Geofences Added",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);
        mGeofenceList = new ArrayList<>();
        geofencingClient = LocationServices.getGeofencingClient(this);
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
            for(DirectionsStep step: result.routes[0].legs[0].steps) {
                mGeofenceList.add(new Geofence.Builder()
                        .setRequestId(step.htmlInstructions)
                        .setCircularRegion(
                                step.endLocation.lat,
                                step.endLocation.lng,
                                Constants.GEOFENCE_RADIUS_IN_METERS
                        )
                        .setNotificationResponsiveness(0)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());
            }

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void turnOnLive(View view) {
        try {
            geofencingClient.addGeofences(getGeofencingRequest(), getPIntent()).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(StepsActivity.this, "Added fence", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getPIntent() {
        Intent intent = new Intent(this, LiveService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}
