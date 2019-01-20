package com.wojteklisowski.planator;

import android.os.AsyncTask;
import android.util.Log;

import com.wojteklisowski.planator.database.AppDatabase;
import com.wojteklisowski.planator.entities.NearbyPlace;
import com.wojteklisowski.planator.entities.RoadSegment;
import com.wojteklisowski.planator.entities.SavedRoad;

import java.util.ArrayList;

public class SaveRoadAsync  extends AsyncTask<Object, Void, Void> {
    private static final String TAG = "SaveRoadAsync";
    private int index;
    @Override
    protected Void doInBackground(Object... objects) {
        ArrayList<NearbyPlace> placeArrayList = (ArrayList<NearbyPlace>) objects[0];
        ArrayList<RoadSegment> roadSegmentArrayList = (ArrayList<RoadSegment>) objects[1];
        String nameOfTrack = (String) objects[2];
        AppDatabase database = (AppDatabase) objects[3];

        if(database.savedRoadDao().getLastItem()== null){
            index=1;
        }else {
            SavedRoad savedRoad =database.savedRoadDao().getLastItem();
            index = savedRoad.getId() + 1;
        }
        Log.d(TAG, "doInBackground: index" + index);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        // wyslac info ze kuniec
    }
}
