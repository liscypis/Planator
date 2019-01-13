package com.wojteklisowski.planator;

import com.google.android.gms.maps.model.Marker;
import com.wojteklisowski.planator.entities.NearbyPlace;

import java.util.ArrayList;

public interface AsyncResponse {
    void processFinish(String output, ArrayList<Marker> markers, ArrayList<NearbyPlace> placesArrayList);
}
