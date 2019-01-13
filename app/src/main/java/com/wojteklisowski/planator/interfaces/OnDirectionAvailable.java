package com.wojteklisowski.planator.interfaces;

import com.wojteklisowski.planator.entities.RoadSegment;

import java.util.ArrayList;

public interface OnDirectionAvailable {
    void onDirectionAvailable(ArrayList<RoadSegment> roadSegments);
}
