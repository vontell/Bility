package dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import objects.City;

/**
 * Created by Santosh on 05/09/18.
 */

@Dao
public interface CityDao {

    @Insert
    void insert(City... city);

    @Update
    void update(City... city);

    @Delete
    void delete(City... city);

    @Query("Select * FROM city")
    City[] loadAll();

    @Query("DELETE FROM city")
    void deleteAll();
}