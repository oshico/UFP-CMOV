package ufp.edu.pamo.project.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ufp.edu.pamo.project.database.ParkingEvent

@Composable
fun ParkingScreen (viewModel: ParkingViewModel){
    val events by viewModel.parkingEvents.observeAsState(emptyList())
    val latestStatus by viewModel.latestStatus.observeAsState("Unknown")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Latest Parking Status: $latestStatus", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.sendResetCommand("http://127.0.0.1:8080") }) {
            Text("Reset Parking")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "History:", style = MaterialTheme.typography.titleMedium)

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(events) { event: ParkingEvent ->
                ParkingEventItem(event)
            }
        }
    }

}

@Composable
fun ParkingEventItem(event: ParkingEvent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(if (event.status == "PARKING_FULL") Color.Red else Color.Green)
            .padding(8.dp)
    ) {
        Text(
            text = "${event.status} at ${event.timestamp}",
            color = Color.White
        )
    }
}