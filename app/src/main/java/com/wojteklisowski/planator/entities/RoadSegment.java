package com.wojteklisowski.planator.entities;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RoadSegment {
    public RoadSegment(int duration, int distance, String placeId, ArrayList<LatLng> points) {
        this.duration = duration;
        this.distance = distance;
        this.placeId = placeId;
        this.points = points;
    }

    private int duration;
    private int distance;
    private String placeId;
    private ArrayList<LatLng> points;



    public int getDuration() {
        return duration;
    }

    public int getDistance() {
        return distance;
    }

    public String getPlace_id() {
        return placeId;
    }

    public ArrayList<LatLng> getPoints() {
        return points;
    }
}
