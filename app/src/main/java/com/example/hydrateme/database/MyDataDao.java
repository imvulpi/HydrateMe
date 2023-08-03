package com.example.hydrateme.database;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.ExperimentalRoomApi;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import kotlin.UnsafeVariance;

@Dao
public interface MyDataDao {
    @Insert
    void insert(DataModel data);

    @Query("SELECT * FROM my_data WHERE SUBSTR(dateTime, 1, 10) = SUBSTR(:date,1,10)")
    List<DataModel> getByDate(String date);

    @Query("DELETE FROM my_data WHERE SUBSTR(dateTime, 1, 10) = SUBSTR(:date,1,10)")
    void deleteByDate(String date);

    @Deprecated
    @Query("DELETE FROM my_data WHERE ROWID IN (SELECT ROWID FROM my_data WHERE value = :value LIMIT 1)")
    void deleteByValue(int value);

    @Query("DELETE FROM my_data WHERE ROWID IN (SELECT ROWID FROM my_data WHERE value = :value AND SUBSTR(dateTime, 1, 10) = SUBSTR(:date,1,10) LIMIT 1)")
    void deleteByValueAndDate(int value, String date);

}

