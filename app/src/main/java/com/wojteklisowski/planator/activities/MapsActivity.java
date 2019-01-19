package com.wojteklisowski.planator.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wojteklisowski.planator.GetDirections;
import com.wojteklisowski.planator.GetNearbyPlaces;
import com.wojteklisowski.planator.GetPhotos;
import com.wojteklisowski.planator.R;
import com.wojteklisowski.planator.entities.NearbyPlace;
import com.wojteklisowski.planator.entities.RoadSegment;
import com.wojteklisowski.planator.interfaces.OnDirectionAvailable;
import com.wojteklisowski.planator.interfaces.OnPhotosAvailable;
import com.wojteklisowski.planator.interfaces.OnPlacesAvailable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMyLocationButtonClickListener, OnMyLocationClickListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, OnPlacesAvailable, OnDirectionAvailable, View.OnClickListener, OnPhotosAvailable {

    private static final String TAG = "Main";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private ImageView mExample;
    private ImageView mInfoImageView;
    private ImageView mCloseImageView;
    private ImageView mDeleteImageView;
    private ImageView mVisitedImageView;
    private ImageView mNextImageView;
    private ImageView mPreviousImageView;
    private TextView mDeleteTextView;
    private TextView mVisitedTextView;
    private TextView mAuthorTextView;
    private TextView mAuthorTV;


    private int mHeight;

    private String mType1;
    private String mType2;
    private String mType3;
    private String mType4;
    private String mOrigin;
    private String mDestination;
    private String mTravelMode;
    private String mPlaceId;
    private LatLng mlatLngOrigin;
    private LatLng mlatLangDestination;
    private boolean mManualMode;
    private int mDuration;
    private int mDistance;
    private int mRadius;
    private int mCurrentIndex;
    private int mNumberOfPhotos;
    private int mMarkerIndex;
    private ArrayList<NearbyPlace> mPlacesArrayList;
    private ArrayList<String> mArrayPlaceType;
    private ArrayList<RoadSegment> mRoadSegments;
    private ArrayList<Marker> mMarkerArrayList;
    private GetPhotos mPhoto;

    private Polyline mPolyline;

    private GeoDataClient mGeoDataClient;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mGeoDataClient = Places.getGeoDataClient(this);


        mInfoImageView = (ImageView) findViewById(R.id.ivInfo);
        mExample = (ImageView) findViewById(R.id.ivExample);
        mCloseImageView = (ImageView) findViewById(R.id.ivClose);
        mDeleteImageView = (ImageView) findViewById(R.id.ivDelete);
        mVisitedImageView = (ImageView) findViewById(R.id.ivVisited);
        mNextImageView = (ImageView) findViewById(R.id.ivNext);
        mPreviousImageView = (ImageView) findViewById(R.id.ivPrevious);
        mDeleteTextView = (TextView) findViewById(R.id.tvDelete);
        mVisitedTextView = (TextView) findViewById(R.id.tvVisited);
        mAuthorTextView = (TextView) findViewById(R.id.tvAuthor);
        mAuthorTextView.setMovementMethod(LinkMovementMethod.getInstance()); //otwiera strone autora
        mAuthorTV = (TextView) findViewById(R.id.tvAuthorConst);

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

        //todo ustawione do testów
        mManualMode = true;

        if (mDistance >= 250) {
            mRadius = 50000;
        } else {
            mRadius = mDistance * 1000 / 5;
        }

        addPlaceTypeToArray();

//        mDestination = mDestination.replaceAll("\\s", "+");
//        mDestination = mDestination.replaceAll(",", "");
//        mOrigin = mOrigin.replaceAll("\\s", "+");
//        mOrigin = mOrigin.replaceAll(",", "");
        //TODO: chyba trzeba bedzie to robic w osobnym watku
        mlatLngOrigin = getLocationFromAddress(mOrigin);
        mlatLangDestination = getLocationFromAddress(mDestination);

        mInfoImageView.setOnClickListener(this);
        mCloseImageView.setOnClickListener(this);
        mNextImageView.setOnClickListener(this);
        mPreviousImageView.setOnClickListener(this);
        mDeleteImageView.setOnClickListener(this);
        mVisitedImageView.setOnClickListener(this);

        setInvisible(); // na poczatku uktyre
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

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mlatLngOrigin, 10));

        getLocationPermission();

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);


        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);

        View v = (View) findViewById(R.id.map);
        mHeight = v.getHeight();
        Log.d(TAG, "map height: " + v.getHeight());

        GetNearbyPlaces getNearbyPlacesData = new GetNearbyPlaces();
        String[] url = getUrl(mlatLngOrigin.latitude, mlatLngOrigin.longitude, mArrayPlaceType);
        getNearbyPlacesData.delegate = this;
        getNearbyPlacesData.execute(mMap, url, mManualMode);

    }

    @Override
    public void onClick(View v) {
        ViewGroup.LayoutParams params = mMapFragment.getView().getLayoutParams();
        switch (v.getId()) {
            case R.id.ivInfo:
                setVisible();
                mPhoto = new GetPhotos(mGeoDataClient, mPlaceId, this);
                params.height = mHeight / 2;
                mMapFragment.getView().setLayoutParams(params);
                break;
            case R.id.ivClose:
                params.height = mHeight;
                mMapFragment.getView().setLayoutParams(params);
                setInvisible();
                break;
            case R.id.ivNext:
                mPhoto.nextPhoto();
                break;
            case R.id.ivPrevious:
                mPhoto.previousPhoto();
                break;
            case R.id.ivDelete:
                mPlacesArrayList.remove(mMarkerIndex);
                Marker mr = mMarkerArrayList.get(mMarkerIndex);
                mr.remove();
                mMarkerArrayList.remove(mMarkerIndex);
                GetDirections getDirections = new GetDirections();
                getDirections.delegate = this;
                getDirections.execute(getRequestUrl(getWaypoints()), mMap, mMarkerArrayList, mManualMode, mDistance, mDuration, mPlacesArrayList, getApplicationContext());

                break;


        }
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
        PolylineOptions polylineOptions = new PolylineOptions();
        RoadSegment rs = null;
        if (mPolyline != null) mPolyline.remove();
        if ((int) marker.getTag() != 89) {
            if ((int) marker.getTag() == 88) {
                rs = mRoadSegments.get(mRoadSegments.size() - 1);
            } else {
                int i = 0;
                for (RoadSegment roadSegment : mRoadSegments) {
                    if (roadSegment.getPointNumber() == (int) marker.getTag()) {
                        rs = mRoadSegments.get(i);
                        break;
                    }
                    i++;
                }
            }
            polylineOptions.addAll(rs.getPoints());
            polylineOptions.width(15);
            polylineOptions.color(Color.GRAY);
            polylineOptions.geodesic(true);
            mPolyline = mMap.addPolyline(polylineOptions);
        }
        // pobieranie id do wyświetlania zdjec
        if ((int) marker.getTag() != 89 && (int) marker.getTag() != 88) {
            mPlaceId = mPlacesArrayList.get((int) marker.getTag()).getPlace_id();
            mPhoto = new GetPhotos(mGeoDataClient, mPlaceId, this);
            mMarkerIndex = (int) marker.getTag();
        }


        // TODO: raczej do wyjebania bo nie można dać optimize i max 9 punktow
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
    public void onMapLongClick(LatLng latLng) {
        // PLACES

    }

    // odbiera dane z async GetNearbyPlaces
    @Override
    public void onPlacesAvailable(String output, ArrayList<Marker> markers, ArrayList<NearbyPlace> placesArrayList) {
        mPlacesArrayList = placesArrayList;
        mMarkerArrayList = markers;
        String waypoints = output;
        String url = getRequestUrl(waypoints);
        Log.d(TAG, "processFinish: " + url);
        GetDirections getDirections = new GetDirections();
        getDirections.delegate = this;
        getDirections.execute(url, mMap, markers, mManualMode, mDistance, mDuration, placesArrayList, getApplicationContext());
    }

    @Override
    public void onDirectionAvailable(ArrayList<RoadSegment> roadSegments) {
        mRoadSegments = roadSegments;
        Log.d(TAG, "onDirectionAvailable: size roadSegments" + mRoadSegments.size());
    }

    @Override
    public void onPhotosAvailable(Bitmap photo, int index, int numberOfPhotos, String author) {
        mCurrentIndex = index;
        mNumberOfPhotos = numberOfPhotos;
        Log.d(TAG, "onPhotosAvailable: " + mCurrentIndex + ", numberOfPhotos" + mNumberOfPhotos);
        if (photo == null) {
            mExample.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.photo));
        } else {
            mExample.invalidate();
            mExample.setImageBitmap(photo);
        }

        setAuthor(author);
        setArrowVisibility();
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

    private void addPlaceTypeToArray() {
        mArrayPlaceType = new ArrayList<>();
        if (mType1 != null) mArrayPlaceType.add(mType1);
        if (mType2 != null) mArrayPlaceType.add(mType2);
        if (mType3 != null) mArrayPlaceType.add(mType3);
        if (mType4 != null) mArrayPlaceType.add(mType4);
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
            googlePlaceUrl.append("&radius=" + mRadius);

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

    private void setInvisible() {
        mAuthorTV.setVisibility(View.GONE);
        mAuthorTextView.setVisibility(View.GONE);
        mExample.setVisibility(View.GONE);
        mCloseImageView.setVisibility(View.GONE);
        mDeleteImageView.setVisibility(View.GONE);
        mVisitedImageView.setVisibility(View.GONE);
        mNextImageView.setVisibility(View.GONE);
        mPreviousImageView.setVisibility(View.GONE);
        mDeleteTextView.setVisibility(View.GONE);
        mVisitedTextView.setVisibility(View.GONE);
    }

    private void setVisible() {
        mAuthorTV.setVisibility(View.VISIBLE);
        mAuthorTextView.setVisibility(View.VISIBLE);
        mExample.setVisibility(View.VISIBLE);
        mCloseImageView.setVisibility(View.VISIBLE);
        mDeleteImageView.setVisibility(View.VISIBLE);
        mVisitedImageView.setVisibility(View.VISIBLE);
        mNextImageView.setVisibility(View.VISIBLE);
        mPreviousImageView.setVisibility(View.VISIBLE);
        mDeleteTextView.setVisibility(View.VISIBLE);
        mVisitedTextView.setVisibility(View.VISIBLE);
    }

    //TODO ogarnac Spannable i  URLSpan
    private void setAuthor(String author) {
        if (author == null)
            mAuthorTextView.setText("Anonymous");
        else {
            Spannable spannable = (Spannable) Html.fromHtml(author, Html.FROM_HTML_MODE_LEGACY);
            for (URLSpan urlSpan : spannable.getSpans(0, spannable.length(), URLSpan.class)) {
                spannable.setSpan(new UnderlineSpan() {
                    public void updateDrawState(TextPaint textPaint) {
                        textPaint.setUnderlineText(false);
                    }
                }, spannable.getSpanStart(urlSpan), spannable.getSpanEnd(urlSpan), 0);
            }
            mAuthorTextView.setText(spannable);
        }

    }

    private void setArrowVisibility() {
        if (mCurrentIndex <= 0) {
            mPreviousImageView.setEnabled(false);
            mPreviousImageView.setColorFilter(Color.GRAY);
        } else {
            mPreviousImageView.setEnabled(true);
            mPreviousImageView.setColorFilter(Color.BLACK);
        }

        if (mCurrentIndex < mNumberOfPhotos - 1) {
            mNextImageView.setEnabled(true);
            mNextImageView.setColorFilter(Color.BLACK);
        } else {
            mNextImageView.setEnabled(false);
            mNextImageView.setColorFilter(Color.GRAY);
        }

    }

    private String getWaypoints() {
        String waypoints = "";
        for (int i = 0; i < mPlacesArrayList.size(); i++) {
            NearbyPlace place = mPlacesArrayList.get(i);
            waypoints += place.getLocation().latitude + "," + place.getLocation().longitude + "|";
        }
        waypoints = waypoints.substring(0, waypoints.length() - 1);
        Log.d(TAG, "showNearbyPlaces: waypoints after substring " + waypoints);

        return waypoints;
    }

}
