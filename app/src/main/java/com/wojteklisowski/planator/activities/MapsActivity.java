package com.wojteklisowski.planator.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wojteklisowski.planator.asynctasks.GetDirections;
import com.wojteklisowski.planator.asynctasks.GetNearbyPlaces;
import com.wojteklisowski.planator.asynctasks.GetPhotos;
import com.wojteklisowski.planator.R;
import com.wojteklisowski.planator.asynctasks.SaveRoadAsync;
import com.wojteklisowski.planator.asynctasks.ShowSavedRoad;
import com.wojteklisowski.planator.database.AppDatabase;
import com.wojteklisowski.planator.entities.NearbyPlace;
import com.wojteklisowski.planator.entities.RoadSegment;
import com.wojteklisowski.planator.interfaces.OnDirectionAvailable;
import com.wojteklisowski.planator.interfaces.OnLoadComplete;
import com.wojteklisowski.planator.interfaces.OnPhotosAvailable;
import com.wojteklisowski.planator.interfaces.OnPlacesAvailable;
import com.wojteklisowski.planator.utils.ConvertTime;
import com.wojteklisowski.planator.utils.ResizeAnimation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.wojteklisowski.planator.database.AppDatabase.getDatabase;

public class MapsActivity extends AppCompatActivity implements OnMyLocationButtonClickListener, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, OnPlacesAvailable, OnDirectionAvailable, View.OnClickListener, OnPhotosAvailable, OnLoadComplete {

    private static final String TAG = "Main";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private ImageView mImage;
    private ImageView mInfoImageView;
    private ImageView mCloseImageView;
    private ImageView mDeleteImageView;
    private ImageView mVisitedImageView;
    private ImageView mNextImageView;
    private ImageView mPreviousImageView;
    private ImageView mSaveImageView;
    private ImageView mTravelModeImageView;
    private ImageView mMenuImageView;
    private TextView mDeleteTextView;
    private TextView mVisitedTextView;
    private TextView mAuthorTextView;
    private TextView mAuthorTV;
    private TextView mRealDistanceTextView;
    private TextView mRealDurationTextView;
    private Button mAddButton;
    private Button mEndButton;
    private ProgressBar mLoading;


    private int mHeight;

    private String mType1;
    private String mType2;
    private String mType3;
    private String mType4;
    private String mOrigin;
    private String mDestination;
    private String mTravelMode;
    private String mPlaceId;
    private String mSavedTravelMode;
    private LatLng mLatLngOrigin;
    private LatLng mLatLangDestination;
    private boolean mManualMode;
    private boolean mEditMode;
    private boolean mFromSavedActivity = false;
    private int mDuration;
    private int mDistance;
    private int mRadius;
    private int mCurrentIndex;
    private int mNumberOfPhotos;
    private int mMarkerIndex;
    private int mRealDistance;
    private int mRealDuration;
    private int mSavedRoadID;
    private int mSavedDuration;
    private int mSavedDistance;
    private ArrayList<NearbyPlace> mPlacesArrayList;
    private ArrayList<String> mArrayPlaceType;
    private ArrayList<RoadSegment> mRoadSegments;
    private ArrayList<Marker> mMarkerArrayList;
    private ArrayList<Marker> mManualModeMarkerArrayList;
    private ArrayList<NearbyPlace> mManualModePlacesArrayList;
    private NearbyPlace mManualModeNearbyPlace;
    private Marker mManualModeMarker;
    private GetPhotos mPhoto;
    private Polyline mPolylineFromDirections;

    private Polyline mPolyline;

    private GeoDataClient mGeoDataClient;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private DrawerLayout mDrawerLayout;
    AppDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        database = getDatabase(getApplicationContext());

        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mGeoDataClient = Places.getGeoDataClient(this);


        mInfoImageView = (ImageView) findViewById(R.id.ivInfo);
        mImage = (ImageView) findViewById(R.id.ivExample);
        mCloseImageView = (ImageView) findViewById(R.id.ivClose);
        mDeleteImageView = (ImageView) findViewById(R.id.ivDelete);
        mVisitedImageView = (ImageView) findViewById(R.id.ivVisited);
        mNextImageView = (ImageView) findViewById(R.id.ivNext);
        mPreviousImageView = (ImageView) findViewById(R.id.ivPrevious);
        mSaveImageView = (ImageView) findViewById(R.id.ivSave);
        mTravelModeImageView = (ImageView) findViewById(R.id.ivTravelMode);
        mMenuImageView = (ImageView) findViewById(R.id.ivMenu);
        mDeleteTextView = (TextView) findViewById(R.id.tvDelete);
        mVisitedTextView = (TextView) findViewById(R.id.tvVisited);
        mAuthorTextView = (TextView) findViewById(R.id.tvAuthor);
        mAuthorTextView.setMovementMethod(LinkMovementMethod.getInstance()); //otwiera strone autora
        mAuthorTV = (TextView) findViewById(R.id.tvAuthorConst);
        mRealDistanceTextView = (TextView) findViewById(R.id.tvDistance);
        mRealDurationTextView = (TextView) findViewById(R.id.tvDuration);
        mAddButton = (Button) findViewById(R.id.bndAdd);
        mEndButton = (Button) findViewById(R.id.bntEnd);
        mLoading = (ProgressBar) findViewById(R.id.pbLoading);

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

        mSavedRoadID = getIntent().getIntExtra("SAVED_ROAD_ID", -1);
        mSavedDuration = getIntent().getIntExtra("SAVED_DURATION", -1);
        mSavedDistance = getIntent().getIntExtra("SAVED_DISTANCE", -1);
        mSavedTravelMode = getIntent().getStringExtra("SAVED_TRAVEL_MODE");

        if (mSavedRoadID != -1)
            mFromSavedActivity = true;

        if (!mFromSavedActivity) {
            if (mManualMode)
                mEditMode = true;

            if (mDistance >= 250 || mManualMode) {
                mRadius = 50000;
            } else {
                mRadius = mDistance * 1000 / 5;
            }
            addPlaceTypeToArray();
            mLatLngOrigin = getLocationFromAddress(mOrigin);
            mLatLangDestination = getLocationFromAddress(mDestination);
        }


        // do menu bocznego
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        if (menuItem.getTitle().equals("Szukaj trasy")) {
                            Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        if (menuItem.getTitle().equals("Twoje trasy")) {
                            Intent intent = new Intent(MapsActivity.this, SavedRoadsActivity.class);
                            startActivity(intent);
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        mMapFragment.getView().setVisibility(View.GONE); // na poczatku mapa niewidoczna


        mInfoImageView.setOnClickListener(this);
        mCloseImageView.setOnClickListener(this);
        mNextImageView.setOnClickListener(this);
        mPreviousImageView.setOnClickListener(this);
        mDeleteImageView.setOnClickListener(this);
        mVisitedImageView.setOnClickListener(this);
        mSaveImageView.setOnClickListener(this);
        mMenuImageView.setOnClickListener(this);
        mAddButton.setOnClickListener(this);
        mEndButton.setOnClickListener(this);

        setInvisible(); // na poczatku uktyre
        if (mManualMode) {
            mManualModePlacesArrayList = new ArrayList<>();
            mManualModeMarkerArrayList = new ArrayList<>();
        }

//        Log.d(TAG, "onCreate: getLocationFromOriginAddress " + mLatLngOrigin.toString());
//        Log.d(TAG, "onCreate: getLocationFromDestinationAddress " + mLatLangDestination.toString());
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
        if (!mFromSavedActivity)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLngOrigin, 10));

        getLocationPermission();
        setCompassMargin();

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerClickListener(this);

        if (mFromSavedActivity) {
            ShowSavedRoad showSavedRoad = new ShowSavedRoad();
            showSavedRoad.execute(mMap, mSavedRoadID, database, getApplicationContext(), this);
        } else {
            GetNearbyPlaces getNearbyPlacesData = new GetNearbyPlaces();
            String[] url = getUrl(mLatLngOrigin.latitude, mLatLngOrigin.longitude, mArrayPlaceType);
            getNearbyPlacesData.delegate = this;
            getNearbyPlacesData.execute(mMap, url, mManualMode, getApplicationContext());
        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivInfo:
                expandMap();
                mInfoImageView.setVisibility(View.GONE);
                break;
            case R.id.ivClose:
                collapseMap();
                break;
            case R.id.ivNext:
                mPhoto.nextPhoto();
                break;
            case R.id.ivPrevious:
                mPhoto.previousPhoto();
                break;
            case R.id.ivMenu:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.ivDelete:
                if (!mEditMode) {
                    mPlacesArrayList.remove(mMarkerIndex);
                    Marker mr = mMarkerArrayList.get(mMarkerIndex);
                    mr.remove();
                    mMarkerArrayList.remove(mMarkerIndex);
                    getDirection(getRequestUrl(getWaypoints(mPlacesArrayList)), mMarkerArrayList, mPlacesArrayList);
                    collapseMap();
                }
                break;
            case R.id.ivVisited:
                final ArrayList<NearbyPlace> list = new ArrayList<>();
                list.addAll(mPlacesArrayList);
                final int index;
                if (!mEditMode)
                    index = mMarkerIndex;
                else
                    index = list.indexOf(mManualModeNearbyPlace);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NearbyPlace nr = database.nearbyPlaceDao().getLastID();
                        int id = 0;
                        if (nr == null) {
                            id = 1;
                        } else {
                            id = nr.getId() + 1;
                        }
                        nr = list.get(index);
                        nr.setId(id);
                        nr.setFkId(1);
                        nr.setVisited(true);
                        database.nearbyPlaceDao().insert(nr);
                        Log.d(TAG, "run: id pace " + id);
                    }
                }).start();


                if (!mEditMode) {
                    mPlacesArrayList.remove(mMarkerIndex);
                    Marker mr = mMarkerArrayList.get(mMarkerIndex);
                    mr.remove();
                    mMarkerArrayList.remove(mMarkerIndex);
                    getDirection(getRequestUrl(getWaypoints(mPlacesArrayList)), mMarkerArrayList, mPlacesArrayList);
                    collapseMap();
                } else {
                    mPlacesArrayList.remove(mManualModeNearbyPlace);
                    mManualModeMarker.remove();
                    mMarkerArrayList.remove(mManualModeMarker);
                    mAddButton.setEnabled(false);
                }
                break;
            case R.id.bndAdd:
                mVisitedImageView.setEnabled(false);
                mVisitedImageView.setColorFilter(Color.GRAY);
                if (!mManualModePlacesArrayList.contains(mManualModeNearbyPlace)) {
                    if (mPolylineFromDirections != null)
                        mPolylineFromDirections.remove();
                    mManualModePlacesArrayList.add(mManualModeNearbyPlace);
                    mManualModeMarker.setTag(mManualModePlacesArrayList.size());
                    mManualModeMarkerArrayList.add(mManualModeMarker);
                    getDirection(getRequestUrl(getWaypoints(mManualModePlacesArrayList)), mManualModeMarkerArrayList, mManualModePlacesArrayList);
                } else {
                    Toast.makeText(this, "juz dodano", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.bntEnd:
                mPlacesArrayList = mManualModePlacesArrayList;
                mMarkerArrayList = mManualModeMarkerArrayList;
                mEditMode = false;
                setInvisibleButton();
                collapseMap();
                getDirection(getRequestUrl(getWaypoints(mPlacesArrayList)), mMarkerArrayList, mPlacesArrayList);
                mDeleteImageView.setEnabled(true);
                mDeleteImageView.setColorFilter(Color.BLACK);
                mVisitedImageView.setEnabled(true);
                mVisitedImageView.setColorFilter(Color.BLACK);
                break;
            case R.id.ivSave:
                saveRoad();
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
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation", Toast.LENGTH_SHORT).show();
        return false;
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.getTag() == null) {
            mInfoImageView.setVisibility(View.VISIBLE);
            mAddButton.setEnabled(true);
            mDeleteImageView.setEnabled(false);
            mDeleteImageView.setColorFilter(Color.GRAY);
            mVisitedImageView.setEnabled(true);
            mVisitedImageView.setColorFilter(Color.BLACK);
            LatLng l = marker.getPosition();
            for (NearbyPlace nearbyPlace : mPlacesArrayList) {
                if (l.equals(nearbyPlace.getLocation())) {
                    mPlaceId = nearbyPlace.getPlaceId();
                    mPhoto = new GetPhotos(mGeoDataClient, mPlaceId, this);
                    mManualModeMarker = marker;
                    mManualModeNearbyPlace = nearbyPlace;
                    break;
                }
            }
        } else {
            if (!mEditMode) {
                PolylineOptions polylineOptions = new PolylineOptions();
                RoadSegment rs = null;
                mSaveImageView.setVisibility(View.VISIBLE);
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
                    polylineOptions.zIndex(2);
                    mPolyline = mMap.addPolyline(polylineOptions);
                }
            } else {
                // jesli jest mod edycji i tag ma id to ivVisited jest wylaczony
                mVisitedImageView.setEnabled(false);
                mVisitedImageView.setColorFilter(Color.GRAY);
            }

            // pobieranie id do wyświetlania zdjec
            if ((int) marker.getTag() != 89 && (int) marker.getTag() != 88) {
                mPlaceId = mPlacesArrayList.get((int) marker.getTag()).getPlaceId();
                mPhoto = new GetPhotos(mGeoDataClient, mPlaceId, this);
                mMarkerIndex = (int) marker.getTag();
                mInfoImageView.setVisibility(View.VISIBLE);
            } else {
                mInfoImageView.setVisibility(View.GONE);
            }
        }
        return false;
    }


    // odbiera dane z async GetNearbyPlaces
    @Override
    public void onPlacesAvailable(String output, ArrayList<Marker> markers, ArrayList<NearbyPlace> placesArrayList) {
        if (output == null) {
            errorDialog("places");
        } else {
            mPlacesArrayList = placesArrayList;
            mMarkerArrayList = markers;
            if (!mManualMode)
                getDirection(getRequestUrl(output), markers, placesArrayList);
            else
                showMap();
        }
    }

    @Override
    public void onDirectionAvailable(ArrayList<RoadSegment> roadSegments, Polyline polyline, int distance, int duration) {
        if(roadSegments == null){
            errorDialog("directions");
        } else {
            mRoadSegments = roadSegments;
            mPolylineFromDirections = polyline;
            mRealDistance = distance;
            mRealDuration = duration;
            Log.d(TAG, "onDirectionAvailable: size roadSegments" + mRoadSegments.size());

            mRealDistanceTextView.setText("Dlugość " + distance / 1000 + "km");
            mRealDurationTextView.setText("Czas " + ConvertTime.convertTime(duration / 60));
            if (!mEditMode)
                mSaveImageView.setVisibility(View.VISIBLE);
            if (!mManualMode)
                showMap();
        }
    }

    @Override
    public void onLoadComplete(ArrayList<RoadSegment> roadSegments, ArrayList<Marker> markers, ArrayList<NearbyPlace> nearbyPlaces) {
        mRoadSegments = roadSegments;
        mMarkerArrayList = markers;
        mPlacesArrayList = nearbyPlaces;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mRoadSegments.get(0).getPoints().get(0), 10));
        mRealDistanceTextView.setText("Dlugość " + mSavedDistance / 1000 + "km");
        mRealDurationTextView.setText("Czas " + ConvertTime.convertTime(mSavedDuration / 60));
        mLatLngOrigin = mRoadSegments.get(0).getPoints().get(0);
        ArrayList<LatLng> points = mRoadSegments.get(mRoadSegments.size() - 1).getPoints();
        mLatLangDestination = points.get(points.size() - 1);
        mTravelMode = mSavedTravelMode;
        setImageOfTravelMode(mTravelMode);
        showMap();
    }

    @Override
    public void onPhotosAvailable(Bitmap photo, int index, int numberOfPhotos, String author) {
        mCurrentIndex = index;
        mNumberOfPhotos = numberOfPhotos;
        Log.d(TAG, "onPhotosAvailable: " + mCurrentIndex + ", numberOfPhotos" + mNumberOfPhotos);
        if (photo == null) {
            mImage.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.photo));
        } else {
            mImage.invalidate();
            mImage.setImageBitmap(photo);
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
     * tworzenie adresu URL
     */
    private String getRequestUrl(String wPoints) {
        String origin = "origin=" + mLatLngOrigin.latitude + "," + mLatLngOrigin.longitude;
        String destination = "destination=" + mLatLangDestination.latitude + "," + mLatLangDestination.longitude;
        String waypoints = "&waypoints=optimize:true|" + wPoints;
        String mode = "mode=" + mTravelMode;
        String param = origin + "&" + destination + waypoints + "&" + mode;
        String output = "json";
        String key = "AIzaSyCGO8Y-5XFNrPEApOGPbJluQfa68kh4IWo";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key=" + key;
        Log.d(TAG, "getRequestUrl: " + url);
        return url;
    }

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
            if (type.get(i).equals("church")) {
                googlePlaceUrl.append("&keyword=" + "kosciol");
                googlePlaceUrl.append("&type=" + "church");
            }
            if (type.get(i).equals("Monuments")) {
                googlePlaceUrl.append("&keyword=" + "zabytek");
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
        mImage.setVisibility(View.GONE);
        mCloseImageView.setVisibility(View.GONE);
        mDeleteImageView.setVisibility(View.GONE);
        mVisitedImageView.setVisibility(View.GONE);
        mNextImageView.setVisibility(View.GONE);
        mPreviousImageView.setVisibility(View.GONE);
        mDeleteTextView.setVisibility(View.GONE);
        mVisitedTextView.setVisibility(View.GONE);
        mEndButton.setVisibility(View.GONE);
        mAddButton.setVisibility(View.GONE);
        mInfoImageView.setVisibility(View.GONE);
        mSaveImageView.setVisibility(View.GONE);
        mTravelModeImageView.setVisibility(View.GONE);
    }

    private void setVisible() {
        mAuthorTV.setVisibility(View.VISIBLE);
        mAuthorTextView.setVisibility(View.VISIBLE);
        mImage.setVisibility(View.VISIBLE);
        mCloseImageView.setVisibility(View.VISIBLE);
        mDeleteImageView.setVisibility(View.VISIBLE);
        mVisitedImageView.setVisibility(View.VISIBLE);
        mNextImageView.setVisibility(View.VISIBLE);
        mPreviousImageView.setVisibility(View.VISIBLE);
        mDeleteTextView.setVisibility(View.VISIBLE);
        mVisitedTextView.setVisibility(View.VISIBLE);
    }

    private void setVisibleButton() {
        mEndButton.setVisibility(View.VISIBLE);
        mAddButton.setVisibility(View.VISIBLE);
    }

    private void setInvisibleButton() {
        mEndButton.setVisibility(View.GONE);
        mAddButton.setVisibility(View.GONE);
    }

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

    private String getWaypoints(ArrayList<NearbyPlace> list) {
        String waypoints = "";
        for (int i = 0; i < list.size(); i++) {
            NearbyPlace place = list.get(i);
            waypoints += place.getLocation().latitude + "," + place.getLocation().longitude + "|";
        }
        if (waypoints != "") {
            waypoints = waypoints.substring(0, waypoints.length() - 1);
            Log.d(TAG, "showNearbyPlaces: waypoints after substring " + waypoints);
        }
        return waypoints;
    }

    private void getDirection(String url, ArrayList<Marker> markers, ArrayList<NearbyPlace> placesArrayList) {
        GetDirections getDirections = new GetDirections();
        getDirections.delegate = this;
        if (mFromSavedActivity)
            getDirections.execute(url, mMap, markers, true, mDistance, mDuration, placesArrayList, getApplicationContext(), mEditMode);
        else
            getDirections.execute(url, mMap, markers, mManualMode, mDistance, mDuration, placesArrayList, getApplicationContext(), mEditMode);
    }

    private void expandMap() {
        View v = (View) findViewById(R.id.map);
        mHeight = v.getHeight();
        Log.d(TAG, "expandMap: height of map " + mHeight);
        Animation animation = new ResizeAnimation(mHeight, mHeight / 2, mMapFragment.getView());
        animation.setDuration(300);
        mMapFragment.getView().startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisible();
                if (mEditMode)
                    setVisibleButton();
                mMap.getUiSettings().setMapToolbarEnabled(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void collapseMap() {
        Animation animation = new ResizeAnimation(mHeight / 2, mHeight, mMapFragment.getView());
        animation.setDuration(300);
        mMapFragment.getView().startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setInvisible();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMap.getUiSettings().setMapToolbarEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void saveRoad() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Zapisywanie trasy")
                .setMessage("Proszę podać nazwę trasy");

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog, null);
        builder.setView(dialogView);

        final EditText input = (EditText) dialogView.findViewById(R.id.edit1);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveRoadAsync saveRoadAsync = new SaveRoadAsync();
                saveRoadAsync.execute(mPlacesArrayList, mRoadSegments, input.getText().toString(), database, mRealDuration, mRealDistance, mTravelMode);
            }
        });
        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void setImageOfTravelMode(String mode) {
        mTravelModeImageView.setVisibility(View.VISIBLE);
        if (mode.equals("driving"))
            mTravelModeImageView.setImageResource(R.drawable.car_black_36dp);
        if (mode.equals("bicycling"))
            mTravelModeImageView.setImageResource(R.drawable.bike_black_36dp);
        if (mode.equals("walking"))
            mTravelModeImageView.setImageResource(R.drawable.walk_black_36dp);
    }

    private void showMap() {
        mMapFragment.getView().setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.GONE);
    }

    private void setCompassMargin() {
        View locationCompass = mMapFragment.getView().findViewById(Integer.parseInt("5"));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                locationCompass.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_LEFT);
        layoutParams.topMargin = 120;
        layoutParams.leftMargin = 16;
    }

    private void errorDialog(String type) {
        String title ="";
        String  message = "Proszę ustawić inne parametry wyszukiwania";
        if(type.equals("places")){
            title = "Błąd podczas wyszukiwania miejsc";
        }
        if(type.equals("directions")){
            title = "Błąd podczas wyznaczania trasy";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

}
