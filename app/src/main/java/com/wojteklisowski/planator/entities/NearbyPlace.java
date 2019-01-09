package com.wojteklisowski.planator.entities;

import com.google.android.gms.maps.model.LatLng;

public class NearbyPlace {
    private String placeId;
    private float rating;
    private String vicinity; // w poblizu / okolica
    private String name;
    private LatLng location;

    public NearbyPlace(String placeId, float rating, String vicinity, String name, LatLng location) {
        this.placeId = placeId;
        this.rating = rating;
        this.vicinity = vicinity;
        this.name = name;
        this.location = location;
    }

    public String getPlace_id() {
        return placeId;
    }

    public float getRating() {
        return rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    public String getName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
    }
}
