package com.wojteklisowski.planator.entities;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RoadSegment {
    public RoadSegment(int pointNumber, int duration, int distance, String placeId, ArrayList<LatLng> points) {
        this.pointNumber = pointNumber;
        this.duration = duration;
        this.distance = distance;
        this.placeId = placeId;
        this.points = points;
    }

    public RoadSegment(int duration, int distance, String placeId, ArrayList<LatLng> points) {
        this.duration = duration;
        this.distance = distance;
        this.placeId = placeId;
        this.points = points;
        this.pointNumber = 88; // punkt koncowy bedzie taki mial
    }

    private int pointNumber;
    private int duration;
    private int distance;
    private String placeId;
    private ArrayList<LatLng> points;

    public int getPointNumber() {
        return pointNumber;
    }

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
