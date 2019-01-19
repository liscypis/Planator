package com.wojteklisowski.planator.interfaces;

import com.google.android.gms.maps.model.Polyline;
import com.wojteklisowski.planator.entities.RoadSegment;

import java.util.ArrayList;

public interface OnDirectionAvailable {
    void onDirectionAvailable(ArrayList<RoadSegment> roadSegments, Polyline polyline);
}
