package com.wojteklisowski.planator.interfaces;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.wojteklisowski.planator.entities.NearbyPlace;
import com.wojteklisowski.planator.entities.RoadSegment;

import java.util.ArrayList;

public interface OnLoadComplete {
    void onLoadComplete(ArrayList<RoadSegment> roadSegments, ArrayList<Marker> markers, ArrayList<NearbyPlace> nearbyPlaces);
}
