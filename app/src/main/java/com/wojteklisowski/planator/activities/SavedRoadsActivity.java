package com.wojteklisowski.planator.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_roads);
        database = getDatabase(getApplicationContext());
        listView = findViewById(R.id.lvRoads);

        new DownloadRoads().execute();

    }
    class DownloadRoads extends AsyncTask <Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            savedRoadList = database.savedRoadDao().loadAllSavedRoads();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SavedRoadArrayAdapter savedRoadArrayAdapter = new SavedRoadArrayAdapter(getApplicationContext(),R.layout.saved_road_record, savedRoadList);
            listView.setAdapter(savedRoadArrayAdapter);
        }
    }


}
