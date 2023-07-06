package com.example.hydrateme.database;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MyDataDao {
    @Insert
    void insert(DataModel data);

    @Query("SELECT * FROM my_data WHERE SUBSTR(dateTime, 1, 10) = :date")
    List<DataModel> getByDate(String date);

    @Query("DELETE FROM my_data WHERE SUBSTR(dateTime, 1, 10) = :date")
    void deleteByDate(String date);

    @Deprecated
    @Query("DELETE FROM my_data WHERE ROWID IN (SELECT ROWID FROM my_data WHERE value = :value LIMIT 1)")
    void deleteByValue(int value);

    @Query("DELETE FROM my_data WHERE ROWID IN (SELECT ROWID FROM my_data WHERE value = :value AND SUBSTR(dateTime, 1, 10) = :date LIMIT 1)")
    void deleteByValueAndDate(int value, String date);

}

