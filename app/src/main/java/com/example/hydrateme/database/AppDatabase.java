package com.example.hydrateme.database;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DataModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MyDataDao myDataDao();
}
