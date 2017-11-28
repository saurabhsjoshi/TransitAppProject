package com.sau.transitappproject.model;


import com.google.android.gms.maps.model.LatLng;

/**
 * Created by saurabh on 2017-11-19.
 */

public class BusStop {
    private String route_name;
    private LatLng latLng;
    private String at_street;
    private String on_street;


    public BusStop(String route_name, String on_street, String at_street, Double latitude, Double longitude) {
        this.route_name = route_name;
        this.on_street = on_street;
        this.at_street = at_street;
        this.latLng = new LatLng(latitude, longitude);
    }

    public String getRoute_name() {
        return route_name;
    }

    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getAt_street() {
        return at_street;
    }

    public void setAt_street(String at_street) {
        this.at_street = at_street;
    }

    public String getOn_street() {
        return on_street;
    }

    public void setOn_street(String on_street) {
        this.on_street = on_street;
    }
}
