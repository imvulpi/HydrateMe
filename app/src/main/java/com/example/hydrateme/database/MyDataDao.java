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
}

