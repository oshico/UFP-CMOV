package ufp.edu.pamo.project.gui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ufp.edu.pamo.project.database.ParkingDatabase
import ufp.edu.pamo.project.database.ParkingEvent
import java.net.HttpURLConnection
import java.net.URL

class ParkingViewModel(application: Application) : AndroidViewModel(application) {
    private val db = ParkingDatabase.getInstance(application)

    val parkingEvents: LiveData<List<ParkingEvent>> = db.parkingEventDao().getAllLive()

    val latestStatus: LiveData<String> = parkingEvents.map { events ->
        if (events.isNotEmpty()) events.first().status ?: "Unknown"
        else "Unknown"
    }

    fun sendResetCommand(serverUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("$serverUrl/actuate/reset")
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    connect()
                    val response = inputStream.bufferedReader().readText()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}