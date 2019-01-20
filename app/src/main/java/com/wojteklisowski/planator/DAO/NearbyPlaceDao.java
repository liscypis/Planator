package com.wojteklisowski.planator.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.wojteklisowski.planator.entities.NearbyPlace;

import java.util.List;

@Dao
public interface NearbyPlaceDao {
    @Insert
    void insert(NearbyPlace nearbyPlace);

    @Query("SELECT * FROM NEARBY_PLACE WHERE fkId IN (:savedRoadID)")
    List<NearbyPlace> loadAllBySavedRoadId(int savedRoadID);

    @Query("SELECT * FROM NEARBY_PLACE WHERE visited IN (:visited)")
    List<NearbyPlace> loadAllVisitedPlaces(boolean visited);

    @Query("SELECT * FROM nearby_place WHERE id = (SELECT max(id) FROM nearby_place)")
    NearbyPlace getLastID();

}
