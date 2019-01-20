package com.wojteklisowski.planator;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.wojteklisowski.planator.database.AppDatabase;
import com.wojteklisowski.planator.entities.NearbyPlace;
import com.wojteklisowski.planator.entities.RoadSegment;
import com.wojteklisowski.planator.entities.SavedRoad;

import java.util.ArrayList;

public class SaveRoadAsync extends AsyncTask<Object, Void, Void> {
    private static final String TAG = "SaveRoadAsync";
    private AppDatabase database;
    private int index;

    @Override
    protected Void doInBackground(Object... objects) {
        ArrayList<NearbyPlace> placeArrayList = (ArrayList<NearbyPlace>) objects[0];
        ArrayList<RoadSegment> roadSegmentArrayList = (ArrayList<RoadSegment>) objects[1];
        String nameOfTrack = (String) objects[2];
        database = (AppDatabase) objects[3];
        int duration = (int) objects[4];
        int distance = (int) objects[5];

        insertSavedRoad(nameOfTrack, duration, distance);
        insertRoadSegment(roadSegmentArrayList);
        insertNearbyPlaces(placeArrayList);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        // wyslac info ze kuniec
    }

    private void insertSavedRoad(String nameOfTrack, int duration, int distance) {
        if (database.savedRoadDao().getLastItem() == null) {
            index = 1;
            database.savedRoadDao().insert(new SavedRoad(index, nameOfTrack, duration, distance));
        } else {
            SavedRoad savedRoad = database.savedRoadDao().getLastItem();
            index = savedRoad.getId() + 1;
            database.savedRoadDao().insert(new SavedRoad(index, nameOfTrack, duration, distance));
        }
        Log.d(TAG, "insertSavedRoad: savedRoad: " + index);
    }

    private void insertRoadSegment(ArrayList<RoadSegment> arrayList) {
        int roadIndex = 0;
        if (database.roadSegmentDao().getLastID() == null) {
            roadIndex = 1;
        } else {
            RoadSegment roadSegment = database.roadSegmentDao().getLastID();
            roadIndex = roadSegment.getId() + 1;
        }
        Log.d(TAG, "insertRoadSegment: roadIndex: " + roadIndex );
        Gson gson = new Gson();
        for (int i = 0; i < arrayList.size(); i++) {
            arrayList.get(i).setId(roadIndex);
            arrayList.get(i).setFkID(index);
            String pointsInJson = gson.toJson(arrayList.get(i).getPoints());
            arrayList.get(i).setJsonPoints(pointsInJson);
            Log.d(TAG, "insertRoadSegment: points in string " + pointsInJson);
            database.roadSegmentDao().insert(arrayList.get(i));
            roadIndex++;
        }
    }

    private void insertNearbyPlaces(ArrayList<NearbyPlace> arrayList) {
        int placeIndex = 0;
        if (database.nearbyPlaceDao().getLastID() == null) {
            placeIndex = 1;
        } else {
            NearbyPlace nearbyPlace = database.nearbyPlaceDao().getLastID();
            placeIndex = nearbyPlace.getId() + 1;
        }
        Log.d(TAG, "insertNearbyPlaces: placeIndex: " + placeIndex);
        for (int j = 0; j < arrayList.size(); j++) {
            NearbyPlace place = arrayList.get(j);
            place.setId(placeIndex);
            place.setFkId(index);
            LatLng l = place.getLocation();
            place.setLatitude(l.latitude);
            place.setLongitude(l.longitude);
            database.nearbyPlaceDao().insert(place);
            placeIndex++;
        }
    }
}
