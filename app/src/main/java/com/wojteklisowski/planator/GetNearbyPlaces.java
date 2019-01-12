package com.wojteklisowski.planator;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wojteklisowski.planator.entities.NearbyPlace;
import com.wojteklisowski.planator.parsers.NearbyJsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object, List, List> {
    private static final String TAG = "GetNearbyPlaces";
    public AsyncResponse delegate = null;

    private String mRawPlacesData;
    private GoogleMap mMap;
    private ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();
    private String mUrl;
    private String mWayPoints = "";

    @Override
    protected List<String> doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        mUrl = (String) objects[1];
        List<String> jsonList = new ArrayList<>();

        GetRawData getRawData = new GetRawData();
        mRawPlacesData = getRawData.readUrl(mUrl);
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

        return jsonList;
    }

    @Override
    protected void onPostExecute(List s) {
        ArrayList<NearbyPlace> nearbyPlaceList;
        NearbyJsonParser parser = new NearbyJsonParser();
        nearbyPlaceList = parser.parse(s);
        showNearbyPlaces(nearbyPlaceList);
        delegate.processFinish(mWayPoints, mMarkerArray);
    }

    private void showNearbyPlaces(ArrayList<NearbyPlace> nearbyPlaceList) {
        Log.d(TAG, "showNearbyPlaces: found " + nearbyPlaceList.size() + " places");
        int counter = 0; // licznik znacznikow
        for (int i = 0; i < nearbyPlaceList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            NearbyPlace nearbyPlace = nearbyPlaceList.get(i);
            if (nearbyPlace.getRating() >= 4.5) {
                markerOptions.position(nearbyPlace.getLocation())
                        .title(nearbyPlace.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                        .alpha(0.7f)
                        .snippet("Okolica: " + nearbyPlace.getVicinity() + " Ocena " + nearbyPlace.getRating());

                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(counter);
                mMarkerArray.add(marker);
                Log.d(TAG, "showNearbyPlaces: rating " + nearbyPlace.getRating());

                mWayPoints += nearbyPlace.getLocation().latitude + "," + nearbyPlace.getLocation().longitude + "|";
                counter ++;
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
