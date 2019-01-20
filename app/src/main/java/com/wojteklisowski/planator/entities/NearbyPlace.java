package com.wojteklisowski.planator.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import static android.arch.persistence.room.ForeignKey.CASCADE;


@Entity(tableName = "nearby_place",
        foreignKeys = @ForeignKey(entity = SavedRoad.class,
                parentColumns = "id",
                childColumns = "fkId",
                onDelete = CASCADE))
public class NearbyPlace {
    @PrimaryKey
    private int id;
    @ColumnInfo()
    private int fkId;
    @ColumnInfo()
    private String placeId;
    @ColumnInfo()
    private float rating;
    @ColumnInfo()
    private String vicinity; // w poblizu / okolica
    @ColumnInfo()
    private String name;
    @Ignore
    private LatLng location;
    @ColumnInfo()
    private Double latitude;
    @ColumnInfo()
    private Double longitude;
    @ColumnInfo()
    private boolean visited;

    public NearbyPlace() {

    }

    public NearbyPlace(String placeId, float rating, String vicinity, String name, LatLng location) {
        this.placeId = placeId;
        this.rating = rating;
        this.vicinity = vicinity;
        this.name = name;
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPlaceId() {
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

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFkId() {
        return fkId;
    }

    public void setFkId(int fkId) {
        this.fkId = fkId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
