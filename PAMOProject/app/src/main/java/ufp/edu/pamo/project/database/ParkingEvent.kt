package ufp.edu.pamo.project.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parking_events")
data class ParkingEvent(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var status: String? = null,
    var timestamp: Long = 0
)
