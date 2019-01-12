package com.wojteklisowski.planator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
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

    private static final LatLngBounds BOUNDS = new LatLngBounds(new LatLng(49.053526, 13.858457), new LatLng(54.791003, 23.855164));

    private AutoCompleteTextView mDestinationTv;
    private AutoCompleteTextView mOriginTv;
    private RadioGroup mTravelModeRg;
    private CheckBox mParkCb;
    private CheckBox mZooCb;
    private CheckBox mMonumentsCb;
    private CheckBox mMuseumCb;
    private TextView mDistanceTv;
    private TextView mDurationTv;
    private TextView mTripTimeTv;
    private TextView mTripLengthTV;
    private SeekBar mLengthSb;
    private SeekBar mDurationSb;
    private Button mAutomaticBnt;
    private Button mManualBnt;
    private Button mOKbnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDestinationTv = (AutoCompleteTextView) findViewById(R.id.etDestination);
        mOriginTv = (AutoCompleteTextView) findViewById(R.id.etOrigin);

        mTravelModeRg = (RadioGroup) findViewById(R.id.rgTravelMode);
        mParkCb = (CheckBox) findViewById(R.id.cbPark);
        mZooCb = (CheckBox) findViewById(R.id.cbZoo);
        mMonumentsCb = (CheckBox) findViewById(R.id.cbMonuments);
        mMuseumCb = (CheckBox) findViewById(R.id.cbMuseum);

        mOKbnt = (Button) findViewById(R.id.bntOk);
        mAutomaticBnt = (Button) findViewById(R.id.bntAutomatic);
        mManualBnt = (Button) findViewById(R.id.bntManual);

        mTripTimeTv = (TextView) findViewById(R.id.tvTripTime);
        mTripLengthTV = (TextView) findViewById(R.id.tvTripLength);
        mDistanceTv = (TextView) findViewById(R.id.tvKm);
        mDurationTv = (TextView) findViewById(R.id.tvMinutes);
        mLengthSb = (SeekBar) findViewById(R.id.sbLength);
        mDurationSb = (SeekBar) findViewById(R.id.sbDuration);
        mDurationSb.setMax(600 - 25); // minuty max 600 min 25
        mDurationSb.setMin(0);
        mLengthSb.setMax(500 - 20); //km  max 500 min 20
        mLengthSb.setMin(0);
        mLengthSb.setProgress(1);

        // na poczatku sa ukryte
        setInvisible();
        mOKbnt.setEnabled(false);


        mOKbnt.setOnClickListener(this);
        mManualBnt.setOnClickListener(this);
        mAutomaticBnt.setOnClickListener(this);
        seekBarsChangeListener();

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
                    intent.putExtra("TRAVEL_MODE", checkTravelMode());

                    if (mMuseumCb.isChecked())
                        intent.putExtra("TYPE", "museum");
                    else
                        intent.putExtra("TYPE", "park");
                } else {
                    Toast.makeText(this, "Wprowadz lokalizacje", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bntAutomatic:
                mAutomaticBnt.setEnabled(false);
                mManualBnt.setEnabled(true);
                setVisible();
                break;
            case R.id.bntManual:
                mAutomaticBnt.setEnabled(true);
                mManualBnt.setEnabled(false);
                setInvisible();
                break;
            case R.id.rbCar:
                //srednia predkosc ok 50km/h = 0.8333333333333 km/min
                mDurationSb.setMax(600); // min
                mDurationSb.setMin(25);
                mLengthSb.setMax(500); //km
                mLengthSb.setMin(20);

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

    private String checkTravelMode() {
        String travelMode = "";
        int radioButtonId = mTravelModeRg.getCheckedRadioButtonId();
        switch (radioButtonId) {
            case R.id.rbCar:
                travelMode = "driving";
                break;
            case R.id.rbBike:
                travelMode = "bicycling";
                break;
            case R.id.rbWalk:
                travelMode = "walking";
                break;
        }
        return travelMode;
    }

    private void setVisible() {
        mTripTimeTv.setVisibility(View.VISIBLE);
        mTripLengthTV.setVisibility(View.VISIBLE);
        mDurationTv.setVisibility(View.VISIBLE);
        mDistanceTv.setVisibility(View.VISIBLE);
        mLengthSb.setVisibility(View.VISIBLE);
        mDurationSb.setVisibility(View.VISIBLE);
    }

    private void setInvisible() {
        mTripTimeTv.setVisibility(View.GONE);
        mTripLengthTV.setVisibility(View.GONE);
        mDurationTv.setVisibility(View.GONE);
        mDistanceTv.setVisibility(View.GONE);
        mLengthSb.setVisibility(View.GONE);
        mDurationSb.setVisibility(View.GONE);
    }
    private void seekBarsChangeListener () {
        mLengthSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDistanceTv.setText((progress + 20) + "km");
                if (fromUser) {
                    mDistanceTv.setText((progress + 20) + "km");
                    int pr = (int) (progress / 0.8333);
                    mDurationSb.setProgress(pr);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mDurationSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDurationTv.setText((progress + 25) + "min");
                if (fromUser) {
                    mDurationTv.setText((progress + 25) + "min");
                    int pr = (int) (progress * 0.8333);
                    mLengthSb.setProgress(pr);
                }

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


}
