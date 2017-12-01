package com.sau.transitappproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TravelMode;
import com.sau.transitappproject.adapter.StepsListAdapter;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class StepsActivity extends AppCompatActivity {
    private LatLng from;
    private LatLng to;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    private ArrayList<DirectionsStep> steps;
    private StepsListAdapter stepsListAdapter;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private TextView txt_desc;
    private TextView lbl_complete;
    private ImageView img_sv;
    private LinearLayout linearLayout;

    private DirectionsStep currentStep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        txt_desc = findViewById(R.id.description);
        lbl_complete = findViewById(R.id.lbl_complete);
        img_sv = findViewById(R.id.img_sv);
        linearLayout = findViewById(R.id.layout_card);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationCallback();
        createLocationRequest();
        LinearLayoutManager llm = new LinearLayoutManager(StepsActivity.this);
        steps = new ArrayList<>();
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
            steps = new ArrayList<>(Arrays.asList(result.routes[0].legs[0].steps));
            stepsListAdapter =  new StepsListAdapter(StepsActivity.this, steps);
            recyclerView.setAdapter(stepsListAdapter);


        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private boolean notifythree = true;
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                Location l = new Location(LocationManager.GPS_PROVIDER);

                l.setLatitude(currentStep.endLocation.lat);
                l.setLongitude(currentStep.endLocation.lng);
                if(notifythree && mCurrentLocation.distanceTo(l) > 150.0 && mCurrentLocation.distanceTo(l) < 300.0) {
                    notifythree = false;
                    sendNotification("300 meters from next stop");

                }
                else if(mCurrentLocation.distanceTo(l) < 100.0) {
                    sendNotification("Arriving");
                    notifythree = true;
                    nextUpdate();
                }
            }
        };
    }


    private void nextUpdate() {
        if(steps.size() < 1) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            linearLayout.setVisibility(View.GONE);
            lbl_complete.setVisibility(View.VISIBLE);
            return;
        }

        currentStep = steps.get(0);
        showCurrentCard();

        steps.remove(0);
        stepsListAdapter.notifyItemRemoved(0);
        stepsListAdapter.notifyItemRangeChanged(0, steps.size());
    }

    public void turnOnLive(View view) {

        try {
            nextUpdate();
            showCurrentCard();
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
            view.setVisibility(View.GONE);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }

    }

    public void showCurrentCard() {
        txt_desc.setText(currentStep.htmlInstructions);
        String url = "http://maps.googleapis.com/maps/api/streetview?size=128x128&location=" + currentStep.endLocation.lat + "," + currentStep.endLocation.lng;
        Picasso.with(this).load(url)
                .fit()
                .into(img_sv);
        linearLayout.setVisibility(View.VISIBLE);
    }



    private void sendNotification(String notificationDetails) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             /* Create or update. */
            NotificationChannel channel = new NotificationChannel("tpa_channel_01",
                    "Transit App Alerts",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            // Sets an ID for the notification, so it can be updated.
            int notifyID = 1;
            String CHANNEL_ID = "tpa_channel_01";// The id of the channel.
            CharSequence name = "Alert";// The user-visible name of the channel.

            Notification notification = new Notification.Builder(StepsActivity.this, CHANNEL_ID)
                    .setContentTitle("Alert")
                    .setContentText(notificationDetails)
                    .setSmallIcon(R.drawable.ic_track_changes_white_24dp)
                    .setChannelId(CHANNEL_ID)
                    .build();
            mNotificationManager.notify(notifyID , notification);
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            // Define the notification settings.
            builder.setSmallIcon(R.drawable.ic_track_changes_white_24dp)
                    // In a real app, you may want to use a library like Volley
                    // to decode the Bitmap.
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_track_changes_white_24dp))
                    .setColor(Color.RED)
                    .setContentTitle("Alert")
                    .setContentText(notificationDetails);

            // Dismiss notification once the user touches it.
            builder.setAutoCancel(true);

            // Issue the notification
            mNotificationManager.notify(0, builder.build());

        }

    }


}
