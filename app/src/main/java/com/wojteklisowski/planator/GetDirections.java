package com.wojteklisowski.planator;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wojteklisowski.planator.entities.NearbyPlace;
import com.wojteklisowski.planator.entities.RoadSegment;
import com.wojteklisowski.planator.interfaces.OnDirectionAvailable;
import com.wojteklisowski.planator.parsers.DirectionJsonParser;

import java.util.ArrayList;

public class GetDirections extends AsyncTask<Object, String, String> {
    private static final String TAG = "GetDirections";

    public OnDirectionAvailable delegate = null; // dodać do konstruktora
    private GoogleMap mMap;
    private ArrayList<Marker> mMarkerArray;
    private ArrayList<NearbyPlace> mNearbyPlaces;
    private int duration;
    private int distance;
    private String mURL;
    private boolean manualMode;
    private Context context;

    @Override
    protected String doInBackground(Object... objects) {
        String response = null;
        mURL = (String) objects[0];
        mMap = (GoogleMap) objects[1];
        mMarkerArray = (ArrayList<Marker>) objects[2];
        manualMode = (boolean) objects[3];
        distance = (int) objects[4];
        distance *= 1000;
        duration = (int) objects[5];
        duration *= 60;
        mNearbyPlaces = (ArrayList<NearbyPlace>) objects[6];
        context = (Context) objects[7];

        if (!manualMode) {
            response = checkDurationAndDistance();
        } else {
            GetRawData getRawData = new GetRawData();
            response = getRawData.readUrl(mURL);
            Log.d(TAG, "doInBackground: true" + manualMode);
        }
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        if (!manualMode) updateMarkers();
        ArrayList<RoadSegment> roadSegmentArrayList;
        DirectionJsonParser directionJsonParser = new DirectionJsonParser();
        roadSegmentArrayList = directionJsonParser.parse(s);
        populateMap(roadSegmentArrayList);
        delegate.onDirectionAvailable(roadSegmentArrayList);
    }

    private void populateMap(ArrayList<RoadSegment> roadSegment) {
        PolylineOptions polylineOptions = new PolylineOptions();

        for (int i = 0; i < roadSegment.size(); i++) {
            Log.d(TAG, "populateMap: roadSegment size " + roadSegment.size());
            Log.d(TAG, "populateMap: mMarkerArray size " + mMarkerArray.size());
            RoadSegment rSegment = roadSegment.get(i);
            polylineOptions.addAll(rSegment.getPoints());
            if (i < mMarkerArray.size()) {
                Log.d(TAG, "populateMap: index " + rSegment.getPointNumber());
                mMarkerArray.get(rSegment.getPointNumber()).setIcon(BitmapDescriptorFactory.fromBitmap(addNumberToBitmap(i+1)));
                Log.d(TAG, "populateMap: add number " + i + " to marker with tag= " + mMarkerArray.get(rSegment.getPointNumber()).getTag() + " marker position " + mMarkerArray.get(rSegment.getPointNumber()).getPosition());
            }
        }
        polylineOptions.width(15);
        polylineOptions.color(Color.CYAN);
        polylineOptions.geodesic(true);
        mMap.addPolyline(polylineOptions);

//         dodawanie punktu startowego i koncowego
        mMap.addMarker(new MarkerOptions()
                .position(roadSegment.get(0).getPoints().get(0))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                .title("Start"))
                .setTag(89);

        ArrayList<LatLng> lastSegment = roadSegment.get(roadSegment.size() - 1).getPoints();
        mMap.addMarker(new MarkerOptions()
                .position(lastSegment.get(lastSegment.size() - 1))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Koniec")
                .rotation(45f))
                .setTag(88);
    }

    private String checkDurationAndDistance() {
        String response;
        for (; ; ) {
            GetRawData getRawData = new GetRawData();
            response = getRawData.readUrl(mURL);

            Log.d(TAG, "doInBackground:");
            ArrayList<RoadSegment> roadSegmentArrayList;
            DirectionJsonParser directionJsonParser = new DirectionJsonParser();
            roadSegmentArrayList = directionJsonParser.parse(response);

            if (directionJsonParser.getmDistance() > distance * 1.15 || directionJsonParser.getSumDuration() > duration * 1.15) {
                RoadSegment max = roadSegmentArrayList.get(0);
                for (int i = 0; i < roadSegmentArrayList.size(); i++) {
                    RoadSegment rs = roadSegmentArrayList.get(i);
                    if (i < mNearbyPlaces.size()) {
                        if (rs.getDistance() > max.getDistance())
                            max = rs;
                    }
                }
                NearbyPlace np = mNearbyPlaces.get(max.getPointNumber());
                LatLng l = np.getLocation();
                String locationToRemove = l.latitude + "," + l.longitude;
                mURL = mURL.replace(locationToRemove, "");
                mURL = mURL.replace("||", "|");
                mNearbyPlaces.remove(np);
                Log.d(TAG, "doInBackground: new url " + mURL);


                Log.d(TAG, "checkDurationAndDistance: " + l);
            } else {
                break;
            }
        }
        return response;
    }

    private void updateMarkers() {
        mMap.clear();
        mMarkerArray.clear();
        int counter = 0; // licznik znacznikow
        for (int i = 0; i < mNearbyPlaces.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            NearbyPlace nearbyPlace = mNearbyPlaces.get(i);
            markerOptions.position(nearbyPlace.getLocation())
                    .title(nearbyPlace.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    .alpha(0.7f)
                    .snippet("Okolica: " + nearbyPlace.getVicinity() + " Ocena " + nearbyPlace.getRating());

            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(counter);
            mMarkerArray.add(marker);

            counter++;
        }
    }
    private Bitmap addNumberToBitmap(int nr){
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.map_marker).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setTextSize(35);
        paint.setColor(Color.WHITE);
        String text = String.valueOf(nr);
        canvas.drawText(text, 40, 85, paint);
        return bmp;
    }
}

