package com.wojteklisowski.planator.entities;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "saved_roads")
public class SavedRoad {
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "name_of_road")
    private String name;
    @ColumnInfo()
    private int duration;
    @ColumnInfo()
    private int distance;
    @ColumnInfo
    private String travelMode;
    public SavedRoad(int id, String name, int duration, int distance, String travelMode) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.distance = distance;
        this.travelMode = travelMode;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
