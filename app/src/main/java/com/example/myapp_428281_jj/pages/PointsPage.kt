package com.example.myapp_428281_jj.pages

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp_428281_jj.MapPoint
import com.example.myapp_428281_jj.MapRepository
import com.example.myapp_428281_jj.PointType
import kotlinx.coroutines.launch


fun mapPointsToGeoJson(points: List<MapPoint>): String {
    val features = points.joinToString(",") { point ->
        """
        {
          "type": "Feature",
          "geometry": {
            "type": "Point",
            "coordinates": [${point.longitude}, ${point.latitude}]
          },
          "properties": {
            "id": ${point.id},
            "type": "${point.type.name}"
          }
        }
        """.trimIndent()
    }

    return """
    {
      "type": "FeatureCollection",
      "features": [$features]
    }
    """.trimIndent()
}

@Composable
fun PointsPage(modifier: Modifier = Modifier) {
    val userReports = remember {
        derivedStateOf {
            MapRepository.points.filter { it.type == PointType.USER_REPORTED }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Moje zgłoszenia kamer", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(userReports.value) { point ->
                Card(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Zgłoszenie #${point.id}", fontWeight = FontWeight.Bold)
                            Text("Lat: ${"%.6f".format(point.latitude)} | Lon: ${"%.6f".format(point.longitude)}")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "W realizacji",
                                color = Color(0xFFFFA500),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            IconButton(
                                onClick = { coroutineScope.launch { MapRepository.removePoint(point) } }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Usuń zgłoszenie",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }

        if (userReports.value.isEmpty()) {
            Text(
                "Nie dodałeś jeszcze żadnych zgłoszeń.",
                modifier = Modifier.padding(top = 20.dp),
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExportPointsButton(points = userReports.value)
    }
}

@Composable
fun ExportPointsButton(points: List<MapPoint>) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/geo+json")
    ) { uri ->
        uri?.let {
            try {
                val geoJsonData = mapPointsToGeoJson(points)

                contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(geoJsonData.toByteArray())
                }

                Toast.makeText(context, "Zapisano w Pobranych!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Błąd zapisu: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Button(
        onClick = {
            exportLauncher.launch("moje_kamery.geojson")
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = points.isNotEmpty()
    ) {
        Text("Eksportuj moje zgłoszenia (.geojson)")
    }
}
