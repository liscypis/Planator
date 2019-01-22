package com.wojteklisowski.planator.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.wojteklisowski.planator.DAO.NearbyPlaceDao;
import com.wojteklisowski.planator.DAO.RoadSegmentDao;
import com.wojteklisowski.planator.DAO.SavedRoadDao;
import com.wojteklisowski.planator.entities.NearbyPlace;
import com.wojteklisowski.planator.entities.RoadSegment;
import com.wojteklisowski.planator.entities.SavedRoad;

import java.util.concurrent.Executors;

@Database(entities = {NearbyPlace.class, RoadSegment.class, SavedRoad.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "AppDatabase";
    public abstract NearbyPlaceDao nearbyPlaceDao();
    public abstract RoadSegmentDao roadSegmentDao();
    public abstract SavedRoadDao savedRoadDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    Log.d(TAG, "getDatabase: ioioio");
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "database").addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    INSTANCE.savedRoadDao().insert(new SavedRoad(1,"x",0,0,"x"));
                                }
                            });
                        }
                    }).build();
                }
            }
        }
        return INSTANCE;
    }
}
