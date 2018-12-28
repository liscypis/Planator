package com.wojteklisowski.planator.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wojteklisowski.planator.AsyncResponse;
import com.wojteklisowski.planator.GetNearbyPlaces;
import com.wojteklisowski.planator.R;
import com.wojteklisowski.planator.parsers.DirectionJsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMyLocationButtonClickListener, OnMyLocationClickListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener, AsyncResponse {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final LatLng KIELCE = new LatLng(50.903238, 20.665137);

    private static final String TAG = "Main";


    int PROXIMITY_RADIUS = 30000;


    private Marker mKielce;
    private Marker mBrisbane;

    String type;
    String origin;
    String destination;


    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        type = getIntent().getStringExtra("TYPE");
        origin = getIntent().getStringExtra("ORIGIN");
        destination = getIntent().getStringExtra("DESTINATION");

        destination = destination.replaceAll("\\s", "+");
        origin = origin.replaceAll("\\s", "+");

        Log.d(TAG, "onCreate: destination" + destination);
        Log.d(TAG, "onCreate: origin" + origin);
    }


    // TODO: https://developers.google.com/maps/documentation/android-sdk/views#changing_camera_position  coś nie balanga
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KIELCE, 15));

        getLocationPermission();

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);


        // Add some markers to the map, and add a data object to each marker.
//        mKielce = mMap.addMarker(new MarkerOptions()
//                .position(KIELCE)
//                .title("Kielce")
//                .draggable(true));
//        mKielce.setTag(0);
//
//        mSuchedniów = mMap.addMarker(new MarkerOptions()
//                .position(SUCHEDNIOW)
//                .title("Suchedniów")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                .rotation(90.0f));
//        mSuchedniów.setTag(0);
//
//        mBrisbane = mMap.addMarker(new MarkerOptions()
//                .position(BRISBANE)
//                .title("Brisbane")
//                .alpha(0.7f)
//                .snippet("Population: 4,137,400"));
//        mBrisbane.setTag(0);
//
//        // Instantiates a new Polyline object and adds points to define a rectangle
//        PolylineOptions rectOptions = new PolylineOptions()
//                .add(new LatLng(37.35, -122.0))
//                .add(new LatLng(37.45, -122.0))  // North of the previous point, but at the same longitude
//                .add(new LatLng(37.45, -122.2))  // Same latitude, and 30km to the west
//                .add(new LatLng(37.35, -122.2))  // Same longitude, and 16km to the south
//                .add(new LatLng(37.35, -122.0)); // Closes the polyline.
//
//        // Get back the mutable Polyline
//        Polyline polyline = mMap.addPolyline(rectOptions);

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);

    }

    /**
     * sprawdza czy uzytkownik dal pozwolenie do lokalizacji, jeśli nie to okno z zapytaniem znów się wyświetli.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    turnOnMyLocation();
                    Log.i(TAG, "onRequestPermissionsResult: cos nie działa");
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    Log.d(TAG, "onRequestPermissionsResult: nie wybrał");
                }
            }
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        // TODO: raczej do wyjebania bo nie można dać optimize
        //  do odpalania nawigacji google maps

//        String url = "https://www.google.com/maps/dir/?api=1&origin=50.879376,20.637002&destination=51.047378,20.831268&waypoints=50.8444941,20.5729616|50.8611224,20.6175605|51.0578124,20.7055183|50.8545151,20.6452738|50.9594408,20.7206301|50.9982541,20.8691851|50.75,20.85|50.968056,20.576944|51.146944,20.6625|50.799167,20.450833|50.847222,20.358889|50.847222,20.358889&mode=driving";
//        //"&origin=50.879376,20.637002&destination=51.047378,20.831268&waypoints=50.8444941,20.5729616|50.8611224,20.6175605|51.0578124,20.7055183|50.8545151,20.6452738|50.9594408,20.7206301|50.9982541,20.8691851|50.75,20.85|50.968056,20.576944|51.0675,20.567222|51.146944,20.6625|50.799167,20.450833|50.799167,20.450833&mode=driving"
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//        startActivity(intent);
        // String url = "https://www.google.com/maps/dir/?api=1&destination=50.879376,20.637002&origin=Suchedniow,Poland&waypoints=Zagnansk&travelmode=driving";

//  https://maps.googleapis.com/maps/api/directions/json?origin=50.879376,20.637002&destination=51.047378,20.831268&waypoints=optimize:true|50.8444941,20.5729616|50.8611224,20.6175605|51.0578124,20.7055183|50.8545151,20.6452738|50.9594408,20.7206301|50.9982541,20.8691851|50.75,20.85|50.968056,20.576944|51.146944,20.6625|50.799167,20.450833|50.847222,20.358889|50.847222,20.358889&mode=driving&key=AIzaSyCGO8Y-5XFNrPEApOGPbJluQfa68kh4IWo

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }


    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(this, "On marker drag start position " + marker.getPosition(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Toast.makeText(this, "On marker drag position " + marker.getPosition(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Toast.makeText(this, "On marker drag end position " + marker.getPosition(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {


        // PLACES
        Object dataTransfer[] = new Object[2];
        GetNearbyPlaces getNearbyPlacesData = new GetNearbyPlaces();

        //mMap.clear();
        String url = getUrl(KIELCE.latitude, KIELCE.longitude, type);
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        getNearbyPlacesData.delegate = this;
        getNearbyPlacesData.execute(dataTransfer);
//        Toast.makeText(MapsActivity.this, "Rezerwaty w okolicy", Toast.LENGTH_SHORT).show();
    }

    // odbiera dane z async GetNearbyPlaces
    @Override
    public void processFinish(String output) {
        String waypoints = output;
        Log.d(TAG, "onMapLongClick: " + waypoints);
        //Toast.makeText(MapsActivity.this, "Rezerwaty w okolicy", Toast.LENGTH_SHORT).show();


        String url = getRequestUrl(waypoints);
        RequestDirections taskRequestDirection = new RequestDirections();
        taskRequestDirection.execute(url);
    }

    public class RequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    /**
     * sprawdzamy czy sa dane pozowolenia do lokalizacji, jesli nie to wywolujemy zapytanie o pozwolenie ktore osbluguje  onRequestPermissionsResult
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            turnOnMyLocation();
            Log.d(TAG, "getLocationPermission: TRUE");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            Log.d(TAG, "getLocationPermission: FALSE");
        }
    }

    private void turnOnMyLocation() {
        if (mMap == null) {
            return;
        }
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            Log.d(TAG, "changeLocationUI: TRUE");
        } catch (SecurityException e) {
            Log.e("changeLocationUI exception: %s", e.getMessage());
        }
    }

    private String getRequestUrl(String wPoints) {
        //Value of origin
//        String str_org = "origin=" + origin.latitude +","+origin.longitude;
        String str_org = "origin=" + origin;
        //Value of destination
//        String str_dest = "destination=" + dest.latitude+","+dest.longitude;
        String str_dest = "destination=" + destination;
        //waypoints
        String waypoints = "&waypoints=optimize:true|" + wPoints;
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org + "&" + str_dest + waypoints + "&" + mode;
        //Output format
        String output = "json";
        //API KEY
        String key = "AIzaSyCGO8Y-5XFNrPEApOGPbJluQfa68kh4IWo";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key=" + key;
        Log.d(TAG, "getRequestUrl: " + url);
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        Log.d(TAG, "responseDirection: " + responseString);
        return responseString;
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);

                DirectionJsonParser directionsParser = new DirectionJsonParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();


                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.MAGENTA);
                polylineOptions.geodesic(true);

                // dodawanie punktu startowego i koncowego
                mMap.addMarker(new MarkerOptions()
                        .position((LatLng) points.get(0))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                        .title("Start"));

                mMap.addMarker(new MarkerOptions()
                        .position((LatLng) points.get(points.size() - 1))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title("Koniec"));
            }

            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }


    //TODO: popracować nad parametrami. trzeba będzie chyba podawać typ i słowo kluczowe bo inaczej to jakieś ścierwo znajduje
    // places
    private String getUrl(double latitude, double longitude, String t) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);

        if (t.equals("park")) {
            googlePlaceUrl.append("&keyword=" + "rezerwat");
            googlePlaceUrl.append("&type=" + "park");
        }
        if (t.equals("museum")) {
            googlePlaceUrl.append("&keyword=" + "muzeum");
            googlePlaceUrl.append("&type=" + "museum");
        }

        googlePlaceUrl.append("&key=" + "AIzaSyCGO8Y-5XFNrPEApOGPbJluQfa68kh4IWo");

        Log.d("MapsActivity", "url = " + googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }
}
