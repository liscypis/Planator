package com.wojteklisowski.planator;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public interface AsyncResponse {
    void processFinish(String output, ArrayList<Marker> markers);
}
