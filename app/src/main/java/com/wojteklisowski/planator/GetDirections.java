package com.wojteklisowski.planator;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wojteklisowski.planator.entities.RoadSegment;
import com.wojteklisowski.planator.parsers.DirectionJsonParser;

import java.util.ArrayList;

public class GetDirections extends AsyncTask<Object, String, String> {
    private static final String TAG = "GetDirections";
    private GoogleMap mMap;
    private ArrayList<Marker> mMarkerArray;

    @Override
    protected String doInBackground(Object... objects) {
        String response;
        GetRawData getRawData = new GetRawData();
        response = getRawData.readUrl((String) objects[0]);
        mMap = (GoogleMap) objects[1];
        mMarkerArray = (ArrayList<Marker>) objects[2];
        return response;
    }


    @Override
    protected void onPostExecute(String s) {
        ArrayList<RoadSegment> roadSegmentArrayList;
        DirectionJsonParser directionJsonParser = new DirectionJsonParser();
        roadSegmentArrayList = directionJsonParser.parse(s);
        populateMap(roadSegmentArrayList);

    }

    private void populateMap(ArrayList<RoadSegment> roadSegment) {
        PolylineOptions polylineOptions = new PolylineOptions();

        for (int i = 0; i < roadSegment.size(); i++) {
            RoadSegment rSegment = roadSegment.get(i);
            polylineOptions.addAll(rSegment.getPoints());
            if (i < mMarkerArray.size()) {
                mMarkerArray.get(rSegment.getPointNumber()).setTitle("numer " + (i + 1));
                Log.d(TAG, "populateMap: add number " + i + " to marker with tag= " + mMarkerArray.get(rSegment.getPointNumber()).getTag() + " marker position " + mMarkerArray.get(i).getPosition());
            }
        }
        polylineOptions.width(15);
        polylineOptions.color(Color.MAGENTA);
        polylineOptions.geodesic(true);


//         dodawanie punktu startowego i koncowego
        mMap.addMarker(new MarkerOptions()
                .position(roadSegment.get(0).getPoints().get(0))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                .title("Start"));

        ArrayList<LatLng> lastSegment = roadSegment.get(roadSegment.size() - 1).getPoints();
        mMap.addMarker(new MarkerOptions()
                .position(lastSegment.get(lastSegment.size() - 1))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Koniec")
                .rotation(45f));


        if (polylineOptions != null) {
            mMap.addPolyline(polylineOptions);
        } else {
//                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "populateMap: " + "error");
        }

    }
}

