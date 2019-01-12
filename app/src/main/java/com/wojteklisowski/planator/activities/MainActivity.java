package com.wojteklisowski.planator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.wojteklisowski.planator.R;
import com.wojteklisowski.planator.adapters.PlaceAutocompleteArrayAdapter;

import java.time.Duration;
import java.time.LocalTime;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private PlaceAutocompleteArrayAdapter madapter;
    private GeoDataClient mGeoDataClient;

    private int mDuration;
    private int mDistance;
    private boolean mManualMode = false;

    //UI references.
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
        initUIreferences();

        mDurationSb.setMax(600 - 25); // minuty max 600 min 25
        mDurationSb.setMin(0);
        mLengthSb.setMax(500 - 20); //km  max 500 min 20
        mLengthSb.setMin(0);
        mDurationSb.setProgress(1);

        // na poczatku sa ukryte
        setInvisible();
        mOKbnt.setEnabled(false);

        mOKbnt.setOnClickListener(this);
        mManualBnt.setOnClickListener(this);
        mAutomaticBnt.setOnClickListener(this);
        mTravelModeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbCar:
                        //srednia predkosc ok 50km/h = 0.8333333333333 km/min
                        mDurationSb.setMax(600 - 25); // minuty max 600 min 25
                        mDurationSb.setMin(0);
                        mLengthSb.setMax(500 - 20); //km  max 500 min 20
                        mDurationSb.setProgress(1);
                        mLengthSb.setProgress(1);
                        mDuration = 25;
                        mDistance = 20;
                        break;
                    case R.id.rbBike:
                        //srednia predkosc ok 17km/h = 0.2833333333333 km/min
                        mDurationSb.setMax(600 - 53); // minuty max 600 min 53
                        mDurationSb.setMin(0);
                        mLengthSb.setMax(170 - 15); //km  max 170 min 15
                        mDurationSb.setProgress(1);
                        mLengthSb.setProgress(1);
                        mDuration = 53;
                        mDistance = 15;
                        break;
                    case R.id.rbWalk:
                        //srednia predkosc ok 4,5km/h = 0.075 km/min
                        mDurationSb.setMax(600 - 93); // minuty max 600 min 93
                        mDurationSb.setMin(0);
                        mLengthSb.setMax(45 - 7); //km  max 45 min 7
                        mDurationSb.setProgress(1);
                        mLengthSb.setProgress(1);
                        mDuration = 93;
                        mDistance = 7;
                        break;
                }
            }
        });

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
                if (!checkEditTexts()) {
                    Toast.makeText(this, "Podaj punkt startowy i końcowy", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkPlaceType()) {
                        intent = new Intent(this, MapsActivity.class);
                        intent.putExtra("DESTINATION", mDestinationTv.getText().toString());
                        intent.putExtra("ORIGIN", mOriginTv.getText().toString());
                        intent.putExtra("TRAVEL_MODE", checkTravelMode());
                        intent.putExtra("MANUAL_MODE", mManualMode);
                        if (mManualMode) {
                            intent.putExtra("DURATION", mDuration);
                            intent.putExtra("DISTANCE", mDistance);
                        }
                        if (mMuseumCb.isChecked())
                            intent.putExtra("TYPE", "museum");
                        else
                            intent.putExtra("TYPE", "park");
                    } else
                        Toast.makeText(this, "Wybierz co chciałbyś zwiedzić", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bntAutomatic:
                mAutomaticBnt.setEnabled(false);
                mManualBnt.setEnabled(true);
                setVisible();
                mManualMode = false;
                mOKbnt.setEnabled(true);
                break;
            case R.id.bntManual:
                mAutomaticBnt.setEnabled(true);
                mManualBnt.setEnabled(false);
                setInvisible();
                mManualMode = true;
                mOKbnt.setEnabled(true);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    private void initUIreferences() {
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

    private void seekBarsChangeListener() {
        mLengthSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (checkTravelMode()) {
                    case "driving":
                        mDistanceTv.setText((progress + 20) + " km");
                        if (fromUser) {
                            mDistanceTv.setText((progress + 20) + " km");
                            int dur = (int) (progress / 0.8333);
                            mDurationSb.setProgress(dur);
                        }
                        mDistance = progress + 20;
                        Log.d(TAG, "onProgressChanged: distance " + mDistance);
                        break;
                    case "bicycling":
                        mDistanceTv.setText((progress + 15) + " km");
                        if (fromUser) {
                            mDistanceTv.setText((progress + 15) + " km");
                            int dur = (int) (progress / 0.2833);
                            mDurationSb.setProgress(dur);
                        }
                        mDistance = progress + 15;
                        Log.d(TAG, "onProgressChanged: distance " + mDistance);
                        break;
                    case "walking":
                        mDistanceTv.setText((progress + 7) + " km");
                        if (fromUser) {
                            mDistanceTv.setText((progress + 7) + " km");
                            int dur = (int) (progress / 0.075);
                            mDurationSb.setProgress(dur);
                        }
                        mDistance = progress + 7;
                        Log.d(TAG, "onProgressChanged: distance " + mDistance);
                        break;
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
                switch (checkTravelMode()) {
                    case "driving":
                        mDurationTv.setText((convertTime(progress + 25)));
                        if (fromUser) {
                            mDurationTv.setText(convertTime(progress + 25));
                            int dis = (int) (progress * 0.8333);
                            mLengthSb.setProgress(dis);
                        }
                        mDuration = progress + 25;
                        Log.d(TAG, "onProgressChanged: duration " + mDuration);
                        break;
                    case "bicycling":
                        mDurationTv.setText(convertTime((progress + 53)));
                        if (fromUser) {
                            mDurationTv.setText(convertTime((progress + 53)));
                            int dis = (int) (progress * 0.2833);
                            mLengthSb.setProgress(dis);
                        }
                        mDuration = progress + 53;
                        Log.d(TAG, "onProgressChanged: duration " + mDuration);
                        break;
                    case "walking":
                        mDurationTv.setText(convertTime((progress + 93)));
                        if (fromUser) {
                            mDurationTv.setText(convertTime((progress + 93)));
                            int dis = (int) (progress * 0.075);
                            mLengthSb.setProgress(dis);
                        }
                        mDuration = progress + 93;
                        Log.d(TAG, "onProgressChanged: duration " + mDuration);
                        break;
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

    private String convertTime(int minutes) {
        return LocalTime.MIN.plus(
                Duration.ofMinutes(minutes)
        ).toString();
    }
}
