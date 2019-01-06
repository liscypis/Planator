package com.wojteklisowski.planator;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RoadSegment {
    public RoadSegment(int duration, int distance, String place_id, ArrayList<LatLng> points) {
        this.duration = duration;
        this.distance = distance;
        this.place_id = place_id;
        this.points = points;
    }

    private int duration;
    private int distance;
    private String place_id;
    private ArrayList<LatLng> points;



    public int getDuration() {
        return duration;
    }

    public int getDistance() {
        return distance;
    }

    public String getPlace_id() {
        return place_id;
    }

    public ArrayList<LatLng> getPoints() {
        return points;
    }
}
