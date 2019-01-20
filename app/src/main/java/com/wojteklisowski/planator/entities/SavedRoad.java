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
