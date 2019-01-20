package com.wojteklisowski.planator.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.wojteklisowski.planator.entities.NearbyPlace;
import com.wojteklisowski.planator.entities.RoadSegment;

import java.util.List;

@Dao
public interface RoadSegmentDao {

    @Insert
    void insert(RoadSegment roadSegment);

    @Query("SELECT * FROM ROAD_SEGMENT WHERE fkId IN (:savedRoadID)")
    List<RoadSegment> loadAllBySavedRoadId(int savedRoadID);

    @Query("SELECT max(id) FROM ROAD_SEGMENT")
    int getLastID();

}
