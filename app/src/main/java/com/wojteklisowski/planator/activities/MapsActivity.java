package com.wojteklisowski.planator.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wojteklisowski.planator.AsyncResponse;
import com.wojteklisowski.planator.GetDirections;
import com.wojteklisowski.planator.GetNearbyPlaces;
import com.wojteklisowski.planator.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMyLocationButtonClickListener, OnMyLocationClickListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener, AsyncResponse {
    private static final String TAG = "Main";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final LatLng KIELCE = new LatLng(50.903238, 20.665137);

    int PROXIMITY_RADIUS = 4000;

    private ImageView mExample;

    private String mType1;
    private String mType2;
    private String mType3;
    private String mType4;
    private String mOrigin;
    private String mDestination;
    private String mTravelMode;
    private LatLng mlatLngOrigin;
    private LatLng mlatLangDestination;
    private boolean mManualMode;
    private int mDuration;
    private int mDistance;
    private ArrayList<String> mArrayPlaceType;

    private GeoDataClient mGeoDataClient;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGeoDataClient = Places.getGeoDataClient(this);
        mExample = (ImageView) findViewById(R.id.ivExample);
        mType1 = getIntent().getStringExtra("TYPE1");
        mType2 = getIntent().getStringExtra("TYPE2");
        mType3 = getIntent().getStringExtra("TYPE3");
        mType4 = getIntent().getStringExtra("TYPE4");
        mOrigin = getIntent().getStringExtra("ORIGIN");
        mDestination = getIntent().getStringExtra("DESTINATION");
        mTravelMode = getIntent().getStringExtra("TRAVEL_MODE");
        mManualMode = getIntent().getBooleanExtra("MANUAL_MODE", false);
        mDistance = getIntent().getIntExtra("DISTANCE", -1);
        mDuration = getIntent().getIntExtra("DURATION", -1);

        addPlaceTypeToArray();

//        mDestination = mDestination.replaceAll("\\s", "+");
//        mDestination = mDestination.replaceAll(",", "");
//        mOrigin = mOrigin.replaceAll("\\s", "+");
//        mOrigin = mOrigin.replaceAll(",", "");
        //TODO: chyba trzeba bedzie to robic w osobnym watku
        mlatLngOrigin = getLocationFromAddress(mOrigin);
        mlatLangDestination = getLocationFromAddress(mDestination);

//        Log.d(TAG, "onCreate: getLocationFromOriginAddress " + mlatLngOrigin.toString());
//        Log.d(TAG, "onCreate: getLocationFromDestinationAddress " + mlatLangDestination.toString());
        Log.d(TAG, "onCreate: destination: " + mDestination);
        Log.d(TAG, "onCreate: origin: " + mOrigin);
        Log.d(TAG, "onCreate: TRAVEL_MODE " + mTravelMode);
        Log.d(TAG, "onCreate: MANUAL_MODE " + mManualMode);
        Log.d(TAG, "onCreate: DISTANCE " + mDistance);
        Log.d(TAG, "onCreate: DURATION " + mDuration);
        Log.d(TAG, "onCreate: type1: " + mType1);
        Log.d(TAG, "onCreate: type1: " + mType2);
    }

    private void addPlaceTypeToArray() {
        mArrayPlaceType = new ArrayList<>();
        if (mType1 != null) mArrayPlaceType.add(mType1);
        if (mType2 != null) mArrayPlaceType.add(mType2);
        if (mType3 != null) mArrayPlaceType.add(mType3);
        if (mType4 != null) mArrayPlaceType.add(mType4);
    }

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

//        getPhotos();
        // PLACES
        Object dataTransfer[] = new Object[2];
        GetNearbyPlaces getNearbyPlacesData = new GetNearbyPlaces();

        //mMap.clear();
        String[] url = getUrl(mlatLngOrigin.latitude, mlatLngOrigin.longitude, mArrayPlaceType);
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        getNearbyPlacesData.delegate = this;
        getNearbyPlacesData.execute(dataTransfer);
    }

    // odbiera dane z async GetNearbyPlaces
    @Override
    public void processFinish(String output, ArrayList<Marker> markers) {
//        String waypoints = output;
//        String url = getRequestUrl(waypoints);
//        Log.d(TAG, "processFinish: " + url);
//        GetDirections getDirections = new GetDirections();
//        getDirections.execute(url, mMap, markers);
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

    // zamienia adress na lokalizacje
    public LatLng getLocationFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList;
        LatLng location = null;
        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if (address == null) {
                return null;
            }
            location = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
        } catch (IOException e) {
            Log.e(TAG, "getLocationFromAddress: " + e.getMessage());
        }
        return location;
    }

    /**
     * tworzenie zapytania http
     */
    private String getRequestUrl(String wPoints) {
        String origin = "origin=" + mlatLngOrigin.latitude + "," + mlatLngOrigin.longitude;
        String destination = "destination=" + mlatLangDestination.latitude + "," + mlatLangDestination.longitude;
        String waypoints = "&waypoints=optimize:true|" + wPoints;
        String mode = "mode=" + mTravelMode;
        String param = origin + "&" + destination + waypoints + "&" + mode;
        String output = "json";
        String key = "AIzaSyCGO8Y-5XFNrPEApOGPbJluQfa68kh4IWo";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key=" + key;
        Log.d(TAG, "getRequestUrl: " + url);
        return url;
    }


    //TODO: popracować nad parametrami. trzeba będzie chyba podawać typ i słowo kluczowe bo inaczej to jakieś ścierwo znajduje
    // places
    private String[] getUrl(double latitude, double longitude, ArrayList type) {
        String[] typeArray = new String[type.size()];
        for (int i = 0; i < type.size(); i++) {
            StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            googlePlaceUrl.append("location=" + latitude + "," + longitude);
            googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);

            if (type.get(i).equals("park")) {
                googlePlaceUrl.append("&keyword=" + "rezerwat");
                googlePlaceUrl.append("&type=" + "park");
            }
            if (type.get(i).equals("museum")) {
                googlePlaceUrl.append("&keyword=" + "muzeum");
                googlePlaceUrl.append("&type=" + "museum");
            }
            googlePlaceUrl.append("&key=" + "AIzaSyCGO8Y-5XFNrPEApOGPbJluQfa68kh4IWo");
            typeArray[i] = googlePlaceUrl.toString();
            Log.d(TAG, "url = " + typeArray[i]);
        }
        return typeArray;
    }

    //todo: pobieranie zdjęc
    private void getPhotos() {
        final String placeId = "ChIJqf3Ku9-HF0cRVx0BK4vxBEs";
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(1);

                Log.d(TAG, "onComplete: getPhotos" + " " + photoMetadataBuffer.getCount());
                // Get the attribution text.
                CharSequence attribution = photoMetadata.getAttributions();
                // Get a full-size bitmap for the photo.
                Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                        PlacePhotoResponse photo = task.getResult();
                        Bitmap bitmap = photo.getBitmap();
                        mExample.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }
}
