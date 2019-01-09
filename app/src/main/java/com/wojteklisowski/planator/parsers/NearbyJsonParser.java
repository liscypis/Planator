package com.wojteklisowski.planator.parsers;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.wojteklisowski.planator.entities.NearbyPlace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NearbyJsonParser {

    private static final String TAG = "NearbyJsonParser";
    private ArrayList<NearbyPlace> mNearbyPlaces;

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String placeName = "-";
        String vicinity = "-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        Log.d("DataParser", "jsonobject =" + googlePlaceJson.toString());


        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("°");
            }

            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = googlePlaceJson.getString("reference");

            googlePlaceMap.put("place_name", placeName);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;

    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
        int count = jsonArray.length();
        List<HashMap<String, String>> placelist = new ArrayList<>();
        HashMap<String, String> placeMap = null;

        for (int i = 0; i < count; i++) {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placelist.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placelist;
    }

    public List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        Log.d("json data", jsonData);

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }


    public ArrayList<NearbyPlace> parsee(List json) {
        ArrayList<String> jsonArrayList = (ArrayList) json;
        mNearbyPlaces = new ArrayList<>();

        JSONObject jsonObject;
        for (int i = 0; i < jsonArrayList.size(); i++) {
            String rawJson = jsonArrayList.get(i);
            try {
                jsonObject = new JSONObject(rawJson);
                Log.d(TAG, "jsonobject =" + jsonObject.toString());
                if (jsonObject.get("status").equals("OK")) {
                    mNearbyPlaces.addAll(getPlacesInfo(jsonObject));
                } else {
                    //TODO : jakis error
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mNearbyPlaces;
    }

    private ArrayList<NearbyPlace> getPlacesInfo(JSONObject jsonObject) {
        ArrayList<NearbyPlace> placesArrayList = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject placeObject = (JSONObject) jsonArray.get(i);
                String name = placeObject.getString("name");
                String vicinity;

                if(placeObject.isNull("vicinity")){
                    vicinity = "-";
                } else {
                    vicinity = placeObject.getString("vicinity");
                    if (vicinity.contains("°")) vicinity = "-";
                }
                double lat = placeObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                double lon = placeObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                float rating = (float) placeObject.getDouble("rating");
                String placeId = placeObject.getString("place_id");

                placesArrayList.add(new NearbyPlace(placeId, rating, vicinity, name, new LatLng(lat, lon)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return placesArrayList;
    }
}
