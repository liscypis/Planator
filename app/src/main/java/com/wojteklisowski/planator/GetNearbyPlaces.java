package com.wojteklisowski.planator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wojteklisowski.planator.entities.NearbyPlace;
import com.wojteklisowski.planator.interfaces.OnPlacesAvailable;
import com.wojteklisowski.planator.parsers.NearbyJsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object, List, List> {
    private static final String TAG = "GetNearbyPlaces";
    public OnPlacesAvailable delegate = null;

    private String mRawPlacesData;
    private GoogleMap mMap;
    private ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();
    private ArrayList<NearbyPlace> nearbyPlaceArrayList;
    private String[] mUrl;
    private String mWayPoints = "";
    private boolean mManualMode;

    @Override
    protected List<String> doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        mUrl = (String[]) objects[1];
        mManualMode = (boolean) objects[2];
        List<String> jsonList = new ArrayList<>();

        for(int i = 0; i < mUrl.length; i++){
            GetRawData getRawData = new GetRawData();
            mRawPlacesData = getRawData.readUrl(mUrl[i]);
            jsonList.add(mRawPlacesData);

            for (; ; ) {
                try {
                    JSONObject jsonObject = new JSONObject(mRawPlacesData);
                    if (jsonObject.has("next_page_token")) {
                        GetRawData getData = new GetRawData();
                        String token = jsonObject.getString("next_page_token");
                        Log.d(TAG, " contain next_page_token " + token);

                        Thread.sleep(2000);
                        mRawPlacesData = getData.readUrl(buildURL(token));
                        jsonList.add(mRawPlacesData);
                        Log.d(TAG, " next places " + mRawPlacesData);
                    } else break;
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException " + e.getMessage());
                } catch (InterruptedException e) {
                    Log.e(TAG, "InterruptedException " + e.getMessage());
                }
            }
        }

        return jsonList;
    }

    @Override
    protected void onPostExecute(List s) {
        ArrayList<NearbyPlace> nearbyPlaceList;
        NearbyJsonParser parser = new NearbyJsonParser();
        nearbyPlaceList = parser.parse(s);
        showNearbyPlaces(nearbyPlaceList);
        delegate.onPlacesAvailable(mWayPoints, mMarkerArray, nearbyPlaceArrayList);
    }

    private void showNearbyPlaces(ArrayList<NearbyPlace> nearbyPlaceList) {
        Log.d(TAG, "showNearbyPlaces: found " + nearbyPlaceList.size() + " places");
        nearbyPlaceArrayList = new ArrayList<>();
        int counter = 0; // licznik znacznikow
        for (int i = 0; i < nearbyPlaceList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            NearbyPlace nearbyPlace = nearbyPlaceList.get(i);
            if (nearbyPlace.getRating() >= 4.5) {
                markerOptions.position(nearbyPlace.getLocation())
                        .title(nearbyPlace.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
                        .alpha(0.8f)
                        .snippet("Średnia ocena " + nearbyPlace.getRating());
//                        .snippet("Okolica: " + nearbyPlace.getVicinity() + " Ocena " + nearbyPlace.getRating());


                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(counter);
                mMarkerArray.add(marker);
                Log.d(TAG, "showNearbyPlaces: rating " + nearbyPlace.getRating());

                nearbyPlaceArrayList.add(nearbyPlace);
                mWayPoints += nearbyPlace.getLocation().latitude + "," + nearbyPlace.getLocation().longitude + "|";
                counter ++;
                if(!mManualMode){
                    if(nearbyPlaceArrayList.size()>= 19)
                        break;
                }
            }
        }
        Log.d(TAG, "showNearbyPlaces: waypoints before substring " + mWayPoints);
        mWayPoints = mWayPoints.substring(0, mWayPoints.length() - 1);
        Log.d(TAG, "showNearbyPlaces: waypoints after substring " + mWayPoints);
    }

    private String buildURL(String nextPageToken) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=");
        url.append(nextPageToken);
        url.append("&key=AIzaSyCGO8Y-5XFNrPEApOGPbJluQfa68kh4IWo");

        return url.toString();
    }
}
