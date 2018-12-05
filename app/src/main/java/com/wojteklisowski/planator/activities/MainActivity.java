package com.wojteklisowski.planator.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wojteklisowski.planator.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bntOK = findViewById(R.id.bntOk);

        bntOK.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.bntOk:
                intent = new Intent(this, MapsActivity.class);
                break;
        }
        if(intent != null){
            startActivity(intent);
        }
    }
}
