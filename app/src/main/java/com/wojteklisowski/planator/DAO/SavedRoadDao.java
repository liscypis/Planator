package com.wojteklisowski.planator.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.wojteklisowski.planator.entities.NearbyPlace;
import com.wojteklisowski.planator.entities.SavedRoad;

import java.util.List;

@Dao
public interface SavedRoadDao {
    @Insert
    void insert(NearbyPlace nearbyPlace);

    @Query("SELECT * FROM saved_roads WHERE id IN (:savedRoadID)")
    List<SavedRoad> loadAllBySavedRoadId(int savedRoadID);

    @Query("SELECT * FROM saved_roads")
    List<SavedRoad> loadAllSavedRoads();

    @Query("SELECT max(id) FROM saved_roads")
    int getLastID();
}
