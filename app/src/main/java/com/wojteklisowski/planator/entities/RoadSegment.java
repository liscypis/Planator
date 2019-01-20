package com.wojteklisowski.planator.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "road_segment",
        foreignKeys = @ForeignKey(entity = SavedRoad.class,
        parentColumns = "id",
        childColumns = "fkID",
        onDelete = CASCADE))

public class RoadSegment {

    @PrimaryKey()
    private int id;
    @ColumnInfo()
    private int pointNumber;
    @ColumnInfo()
    private int duration;
    @ColumnInfo()
    private int distance;
    @ColumnInfo()
    private String placeId;
    @Ignore
    private ArrayList<LatLng> points;
    @ColumnInfo()
    private String jsonPoints;
    @ColumnInfo()
    private int fkID;

    public RoadSegment() {
    }

    @Ignore
    public RoadSegment(int pointNumber, int duration, int distance, String placeId, ArrayList<LatLng> points) {
        this.pointNumber = pointNumber;
        this.duration = duration;
        this.distance = distance;
        this.placeId = placeId;
        this.points = points;
    }
    @Ignore
    public RoadSegment(int duration, int distance, String placeId, ArrayList<LatLng> points) {
        this.duration = duration;
        this.distance = distance;
        this.placeId = placeId;
        this.points = points;
        this.pointNumber = 88; // punkt koncowy bedzie taki mial
    }

    public String getJsonPoints() {
        return jsonPoints;
    }

    public void setJsonPoints(String jsonPoints) {
        this.jsonPoints = jsonPoints;
    }

    public int getPointNumber() {
        return pointNumber;
    }

    public int getDuration() {
        return duration;
    }

    public int getDistance() {
        return distance;
    }

    public String getPlaceId() {
        return placeId;
    }

    public ArrayList<LatLng> getPoints() {
        return points;
    }

    public int getId() {
        return id;
    }

    public int getFkID() {
        return fkID;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setFkID(int fkID) {
        this.fkID = fkID;
    }

    public void setPointNumber(int pointNumber) {
        this.pointNumber = pointNumber;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setPoints(ArrayList<LatLng> points) {
        this.points = points;
    }
}
