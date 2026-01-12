package ufp.edu.pamo.project.gui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import ufp.edu.pamo.project.database.ParkingDatabase
import ufp.edu.pamo.project.database.ParkingEvent
import java.net.HttpURLConnection
import java.net.URL

class ParkingViewModel(application: Application) : AndroidViewModel(application) {
    private val db = ParkingDatabase.getInstance(application)

    private val SERVER_URL = "http://192.168.1.5:8080"

    val parkingEvents: LiveData<List<ParkingEvent>> = db.parkingEventDao().getAllLive()

    val latestStatus: LiveData<String> = parkingEvents.map { events ->
        if (events.isNotEmpty()) events.first().status ?: "Unknown"
        else "Unknown"
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun syncFromServer() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)

            try {
                val url = URL("$SERVER_URL/sensor/history?results=50")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val jsonArray = JSONArray(response)

                    // Parse and save to local database
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)

                        val event = ParkingEvent(
                            id = jsonObject.optInt("id", 0),
                            status = jsonObject.optString("status", "UNKNOWN"),
                            timestamp = jsonObject.optLong("timestamp", 0)
                        )

                        db.parkingEventDao().insert(event)
                    }

                    println("Synced ${jsonArray.length()} events from server")
                }

                connection.disconnect()

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun sendResetCommand() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("$SERVER_URL/actuate/reset")
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