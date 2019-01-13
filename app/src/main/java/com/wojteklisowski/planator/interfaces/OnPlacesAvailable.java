package com.wojteklisowski.planator.interfaces;

import com.google.android.gms.maps.model.Marker;
import com.wojteklisowski.planator.entities.NearbyPlace;

import java.util.ArrayList;

public interface OnPlacesAvailable {
    void onPlacesAvailable(String output, ArrayList<Marker> markers, ArrayList<NearbyPlace> placesArrayList);
}
