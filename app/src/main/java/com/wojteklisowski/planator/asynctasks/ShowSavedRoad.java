package com.wojteklisowski.planator.asynctasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wojteklisowski.planator.R;
import com.wojteklisowski.planator.database.AppDatabase;
import com.wojteklisowski.planator.entities.NearbyPlace;
import com.wojteklisowski.planator.entities.RoadSegment;
import com.wojteklisowski.planator.interfaces.OnDirectionAvailable;
import com.wojteklisowski.planator.interfaces.OnLoadComplete;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ShowSavedRoad extends AsyncTask<Object,Void,Void> {
    private static final String TAG = "ShowSavedRoad";
    private GoogleMap mMap;
    private ArrayList<Marker> mMarkerArray = new ArrayList<>();
    private ArrayList<NearbyPlace> mNearbyPlaces;
    private ArrayList<RoadSegment> mRoadSegments;
    private AppDatabase database;
    private Context context;
    private OnLoadComplete listener;

    @Override
    protected Void doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        int id = (int) objects[1];
        database = (AppDatabase) objects[2];
        context = (Context) objects[3];
        listener = (OnLoadComplete) objects[4];

        readAndModifyPlaces(id);
        readAndModifyRoads(id);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        populateMap();
        listener.onLoadComplete(mRoadSegments,mMarkerArray,mNearbyPlaces);
    }
    private void populateMap() {
        updateMarkers();
        PolylineOptions polylineOptions = new PolylineOptions();

        for (int i = 0; i < mRoadSegments.size(); i++) {
            Log.d(TAG, "populateMap: roadSegment size " + mRoadSegments.size());
            Log.d(TAG, "populateMap: mMarkerArray size " + mMarkerArray.size());
            RoadSegment rSegment = mRoadSegments.get(i);
            polylineOptions.addAll(rSegment.getPoints());
            if (i < mMarkerArray.size()) {
                Log.d(TAG, "populateMap: index " + rSegment.getPointNumber());
                mMarkerArray.get(rSegment.getPointNumber()).setIcon(BitmapDescriptorFactory.fromBitmap(addNumberToBitmap(i+1)));
                Log.d(TAG, "populateMap: add number " + i + " to marker with tag= " + mMarkerArray.get(rSegment.getPointNumber()).getTag() + " marker position " + mMarkerArray.get(rSegment.getPointNumber()).getPosition());
            }
        }
        polylineOptions.width(15);
        polylineOptions.color((Color.rgb(50,203,0)));
        polylineOptions.geodesic(true);
        mMap.addPolyline(polylineOptions);

//         dodawanie punktu startowego i koncowego
        mMap.addMarker(new MarkerOptions()
                .position(mRoadSegments.get(0).getPoints().get(0))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                .title("Start"))
                .setTag(89);

        ArrayList<LatLng> lastSegment = mRoadSegments.get(mRoadSegments.size() - 1).getPoints();
        mMap.addMarker(new MarkerOptions()
                .position(lastSegment.get(lastSegment.size() - 1))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Koniec")
                .rotation(45f))
                .setTag(88);
    }

    private void readAndModifyPlaces(int id) {
        mNearbyPlaces = (ArrayList<NearbyPlace>) database.nearbyPlaceDao().loadAllBySavedRoadId(id);
        for(NearbyPlace nearbyPlace:mNearbyPlaces){
            nearbyPlace.setLocation(new LatLng(nearbyPlace.getLatitude(),nearbyPlace.getLongitude()));
        }
    }

    private void readAndModifyRoads(int id) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LatLng>>() {}.getType();
        mRoadSegments = (ArrayList<RoadSegment>) database.roadSegmentDao().loadAllBySavedRoadId(id);
        for(RoadSegment roadSegment : mRoadSegments) {
            String points = roadSegment.getJsonPoints();
            ArrayList<LatLng> p = gson.fromJson(points,type);
            roadSegment.setPoints(p);
        }
    }

    private void updateMarkers() {
        mMap.clear();
        int counter = 0; // licznik znacznikow
        for (int i = 0; i < mNearbyPlaces.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            NearbyPlace nearbyPlace = mNearbyPlaces.get(i);
            markerOptions.position(nearbyPlace.getLocation())
                    .title(nearbyPlace.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    .alpha(0.7f)
                    .snippet("średnia ocena " + nearbyPlace.getRating());
            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(counter);
            mMarkerArray.add(marker);

            counter++;
        }
    }

    private Bitmap addNumberToBitmap(int nr){
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.map_marker).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setTextSize(35);
        paint.setColor(Color.WHITE);
        String text = String.valueOf(nr);
        if(nr >= 10){
            canvas.drawText(text, 42, 92, paint);
        } else
            canvas.drawText(text, 52, 92, paint);

        return bmp;
    }
}
