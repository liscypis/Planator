package com.wojteklisowski.planator.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.wojteklisowski.planator.DAO.NearbyPlaceDao;
import com.wojteklisowski.planator.DAO.RoadSegmentDao;
import com.wojteklisowski.planator.DAO.SavedRoadDao;
import com.wojteklisowski.planator.entities.NearbyPlace;
import com.wojteklisowski.planator.entities.RoadSegment;
import com.wojteklisowski.planator.entities.SavedRoad;

@Database(entities = {NearbyPlace.class, RoadSegment.class, SavedRoad.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NearbyPlaceDao nearbyPlaceDao();
    public abstract RoadSegmentDao roadSegmentDao();
    public abstract SavedRoadDao savedRoadDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "planator_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
