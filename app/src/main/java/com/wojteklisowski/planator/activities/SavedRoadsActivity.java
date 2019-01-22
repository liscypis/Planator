package com.wojteklisowski.planator.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.wojteklisowski.planator.R;
import com.wojteklisowski.planator.adapters.SavedRoadArrayAdapter;
import com.wojteklisowski.planator.database.AppDatabase;
import com.wojteklisowski.planator.entities.SavedRoad;

import java.util.List;

import static com.wojteklisowski.planator.database.AppDatabase.getDatabase;

public class SavedRoadsActivity extends AppCompatActivity {

    private AppDatabase database;
    private List<SavedRoad> savedRoadList;
    private ListView listView;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_roads);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.menu_black_36dp);

        database = getDatabase(getApplicationContext());
        listView = findViewById(R.id.lvRoads);
        new DownloadRoads().execute();

        // do menu bocznego
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        if (menuItem.getTitle().equals("Szukaj trasy")) {
                            Intent intent = new Intent(SavedRoadsActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    // otwiera menu jak sie kliknie na ikonke w toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class DownloadRoads extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            savedRoadList = database.savedRoadDao().loadAllSavedRoads();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!savedRoadList.isEmpty())
                savedRoadList.remove(0);
            SavedRoadArrayAdapter savedRoadArrayAdapter = new SavedRoadArrayAdapter(getApplicationContext(), R.layout.saved_road_record, savedRoadList);
            listView.setAdapter(savedRoadArrayAdapter);
        }
    }


}
