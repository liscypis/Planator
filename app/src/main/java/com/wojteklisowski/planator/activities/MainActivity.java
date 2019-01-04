package com.wojteklisowski.planator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.wojteklisowski.planator.R;
import com.wojteklisowski.planator.adapters.PlaceAutocompleteArrayAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "MainActivity";
    private PlaceAutocompleteArrayAdapter madapter;
    private GeoDataClient mGeoDataClient;

    private static final LatLngBounds BOUNDS = new LatLngBounds(new LatLng(49.053526, 13.858457), new LatLng( 54.791003, 23.855164));

    AutoCompleteTextView mDestinationTv;
    AutoCompleteTextView mOriginTv;
    RadioGroup mTravelModeRg;
    CheckBox mParkCb;
    CheckBox mZooCb;
    CheckBox mMonumentsCb;
    CheckBox mMuseumCb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bntOK = (Button) findViewById(R.id.bntOk);
        mDestinationTv = (AutoCompleteTextView) findViewById(R.id.etDestination);
        mOriginTv = (AutoCompleteTextView) findViewById(R.id.etOrigin);
        mTravelModeRg = (RadioGroup) findViewById(R.id.rgTravelMode);
        mParkCb = (CheckBox) findViewById(R.id.cbPark);
        mZooCb = (CheckBox) findViewById(R.id.cbZoo);
        mMonumentsCb = (CheckBox) findViewById(R.id.cbMonuments);
        mMuseumCb = (CheckBox) findViewById(R.id.cbMuseum);

        bntOK.setOnClickListener(this);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("PL")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();

        mGeoDataClient = Places.getGeoDataClient(this);
        madapter = new PlaceAutocompleteArrayAdapter(this, mGeoDataClient, null, typeFilter);
        mOriginTv.setAdapter(madapter);
        mDestinationTv.setAdapter(madapter);

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.bntOk:
                if (checkEditTexts() && checkPlaceType()) {
                    intent = new Intent(this, MapsActivity.class);
                    intent.putExtra("DESTINATION", mDestinationTv.getText().toString());
                    intent.putExtra("ORIGIN", mOriginTv.getText().toString());
                    if (mMuseumCb.isChecked())
                        intent.putExtra("TYPE", "museum");
                    else
                        intent.putExtra("TYPE", "park");
                    break;
                } else {
                    Toast.makeText(this, "Wprowadz lokalizacje", Toast.LENGTH_SHORT).show();
                }

        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    private boolean checkEditTexts() {
        if (mDestinationTv.getText().toString().equals("") || mOriginTv.getText().toString().equals(""))
            return false;
        return true;
    }

    private boolean checkPlaceType() {
        if (mParkCb.isChecked() || mZooCb.isChecked() || mMonumentsCb.isChecked() || mMuseumCb.isChecked())
            return true;
        return false;
    }

}
