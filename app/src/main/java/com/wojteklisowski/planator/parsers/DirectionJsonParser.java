package com.wojteklisowski.planator.parsers;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.wojteklisowski.planator.entities.RoadSegment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DirectionJsonParser {
    private static final String TAG = "DirectionJsonParser";
    private int mDuration;
    private int mDistance;
    private int sumDuration = 0;
    private int sumDistance = 0;

    public int getSumDuration() {
        return sumDuration;
    }

    public int getSumDistance() {
        return sumDistance;
    }

    public int getmDuration() {
        return mDuration;
    }

    public int getmDistance() {
        return mDistance;
    }

    public ArrayList<RoadSegment> parse(String json) {
        ArrayList<RoadSegment> roadSegmentArrayList = new ArrayList<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            String status = jsonObject.getString("status");
            if (status.equals("OK")) {
                JSONArray jsonGeocodedWaypoints = jsonObject.getJSONArray("geocoded_waypoints");
                JSONArray jsonRoutes = jsonObject.getJSONArray("routes");
                JSONArray jsonLegs = jsonRoutes.getJSONObject(0).getJSONArray("legs");
                JSONArray jsonWayPointsOrder = jsonRoutes.getJSONObject(0).getJSONArray("waypoint_order");

                // petla po wszsytkich zdekodowanych waypointach razem z pkt pocz i koncowym
                String[] places = new String[jsonGeocodedWaypoints.length()];
                for (int j = 0; j < jsonGeocodedWaypoints.length(); j++) {
                    places[j] = (String) jsonGeocodedWaypoints.getJSONObject(j).get("place_id");
                }
                // loop po legach odczytuje czas i dlugosc kazdego odcinka
                for (int i = 0; i < jsonLegs.length(); i++) {
                    JSONArray jsonSteps = jsonLegs.getJSONObject(i).getJSONArray("steps");
                    mDistance = (int) (jsonLegs.getJSONObject(i).getJSONObject("distance")).get("value");
                    mDuration = (int) (jsonLegs.getJSONObject(i).getJSONObject("duration")).get("value");

                    // petla po wszytkich punktach z polyline i zapisanie ich w liscie
                    ArrayList<LatLng> latLngArrayList = new ArrayList<>();
                    for (int k = 0; k < jsonSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) jsonSteps.getJSONObject(k).getJSONObject("polyline").get("points");
                        latLngArrayList.addAll(decodePolyline(polyline));
                    }
                    if(i == jsonLegs.length() -1){
                        roadSegmentArrayList.add(new RoadSegment(mDistance, mDuration, places[i + 1], latLngArrayList));
                        Log.d(TAG, "parse: KUUUUUUUUURRR");
                    } else {
                        roadSegmentArrayList.add(new RoadSegment(jsonWayPointsOrder.getInt(i),mDistance, mDuration, places[i + 1], latLngArrayList));
                        Log.d(TAG, "orderNumber: " +  jsonWayPointsOrder.getInt(i));
                    }
                    
                    Log.d(TAG, "RoadSegment "+ mDistance + " " + mDuration + " " + places[i+1]);
                    sumDistance += mDistance;
                    sumDuration += mDuration;
                    Log.d(TAG, "distance :" + sumDistance + " sum duration: " + sumDuration);
                }
            } else if (status.equals("NOT_FOUND") || status.equals("ZERO_RESULTS")) {
                // TODO wyswietlanie erroru
                Log.e(TAG, "parse error: ");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return roadSegmentArrayList;
    }

    /**
     * Method to decode polyline
     * Source : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private ArrayList<LatLng> decodePolyline(String encoded) {

        ArrayList<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
