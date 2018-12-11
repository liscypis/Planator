package com.wojteklisowski.planator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.wojteklisowski.planator.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    EditText etDestination;
    EditText etOrigin;
    RadioGroup rgTravelMode;
    CheckBox cbPark;
    CheckBox cbZoo;
    CheckBox cbMonuments;
    CheckBox cbMuseum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bntOK = findViewById(R.id.bntOk);
        etDestination = findViewById(R.id.etDestination);
        etOrigin = findViewById(R.id.etOrigin);
        rgTravelMode = findViewById(R.id.rgTravelMode);
        cbPark = findViewById(R.id.cbPark);
        cbZoo = findViewById(R.id.cbZoo);
        cbMonuments = findViewById(R.id.cbMonuments);
        cbMuseum = findViewById(R.id.cbMuseum);

        bntOK.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.bntOk:
                if (checkEditTexts() && checkPlaceType()) {
                    intent = new Intent(this, MapsActivity.class);
                    intent.putExtra("DESTINATION", etDestination.getText().toString());
                    intent.putExtra("ORIGIN", etOrigin.getText().toString());
                    if(cbMuseum.isChecked())
                        intent.putExtra("TYPE", "museum");
                    else
                        intent.putExtra("TYPE","park");
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
        if (etDestination.getText().toString().equals("") || etOrigin.getText().toString().equals(""))
            return false;
        return true;
    }

    private boolean checkPlaceType() {
        if (cbPark.isChecked() || cbZoo.isChecked() || cbMonuments.isChecked() || cbMuseum.isChecked())
            return true;
        return false;
    }

}
