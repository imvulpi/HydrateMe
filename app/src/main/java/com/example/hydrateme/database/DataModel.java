package com.example.hydrateme.database;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "my_data")
public class DataModel {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String dateTime; // Date and time in string format (yyyy-mm-dd-/time) (ISO 8601) example: 2007-03-09/7:50
    private int value;
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateTime(){
        return dateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
