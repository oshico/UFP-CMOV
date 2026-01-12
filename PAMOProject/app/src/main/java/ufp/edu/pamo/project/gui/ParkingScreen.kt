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
    val isLoading by viewModel.isLoading.observeAsState(false)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Latest Parking Status: $latestStatus",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.sendResetCommand() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Reset")
            }

            Button(
                onClick = { viewModel.syncFromServer() },
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sync")
                }
            }
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