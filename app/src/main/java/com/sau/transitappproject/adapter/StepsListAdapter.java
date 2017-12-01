package com.sau.transitappproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.model.DirectionsStep;
import com.sau.transitappproject.R;
import com.sau.transitappproject.StreetViewActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by saurabh on 2017-11-26.
 */

public class StepsListAdapter extends RecyclerView.Adapter<StepsListAdapter.StepsViewHolder> {

    private ArrayList<DirectionsStep> steps;
    private Context context;


    public StepsListAdapter(Context context, ArrayList<DirectionsStep> steps) {
        this.steps = steps;
        this.context =  context;
    }

    public static class StepsViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textView;
        TextView txt_distance;
        ImageView imageView;

        StepsViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            textView = itemView.findViewById(R.id.description);
            txt_distance = itemView.findViewById(R.id.distance);
            imageView = itemView.findViewById(R.id.img_sv);
        }
    }

    @Override
    public StepsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.steps_list_row, parent, false);
        return new StepsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StepsViewHolder holder, final int position) {
        final DirectionsStep step = steps.get(position);
        holder.textView.setText(step.htmlInstructions);
        holder.txt_distance.setText(step.distance.humanReadable  + " (" + step.duration.humanReadable + ")");
        String url = "http://maps.googleapis.com/maps/api/streetview?size=700x850&location=" + step.endLocation.lat + "," + step.endLocation.lng;
        Picasso.with(context).load(url)
                .fit()
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, StreetViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("latlng", new LatLng(step.endLocation.lat, step.endLocation.lng));
                i.putExtra("latlng", bundle);
                context.startActivity(i);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }
}
