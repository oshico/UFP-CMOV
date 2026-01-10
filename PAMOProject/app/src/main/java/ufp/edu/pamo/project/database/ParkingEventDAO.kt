package ufp.edu.pamo.project.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface ParkingEventDAO {
    @Insert
    fun insert(event: ParkingEvent)

    @Query("SELECT * FROM parking_events ORDER BY timestamp DESC")
    fun getAll(): MutableList<ParkingEvent>

    @Query("SELECT * FROM parking_events ORDER BY timestamp DESC")
    fun getAllLive(): LiveData<List<ParkingEvent>>
}