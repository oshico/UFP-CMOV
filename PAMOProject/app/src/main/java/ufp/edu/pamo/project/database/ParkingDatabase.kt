package ufp.edu.pamo.project.database

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase

@Database(entities = [ParkingEvent::class], version = 1)
abstract class ParkingDatabase : RoomDatabase() {

    abstract fun parkingEventDao(): ParkingEventDAO

    companion object {
        @Volatile
        private var INSTANCE: ParkingDatabase? = null

        fun getInstance(context: Context): ParkingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParkingDatabase::class.java,
                    "parking_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}