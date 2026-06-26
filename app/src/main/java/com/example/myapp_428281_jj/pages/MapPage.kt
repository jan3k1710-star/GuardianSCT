package com.example.myapp_428281_jj.pages

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.myapp_428281_jj.MapRepository
import com.example.myapp_428281_jj.MapStateHolder
import com.example.myapp_428281_jj.PointType
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.standard.rememberStandardStyleState
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.launch
import com.example.myapp_428281_jj.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    mapStateHolder: MapStateHolder
) {
    val token = BuildConfig.MAPBOX_ACCESS_TOKEN
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val coroutineScope = rememberCoroutineScope()
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(19.94, 50.06))
            zoom(12.0)
            pitch(45.0)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            reportCameraWithPhoto(context, fusedLocationClient, coroutineScope, tempPhotoUri!!)
        } else {
            Toast.makeText(context, "Anulowano robienie zdjęcia", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            moveToUser(context, fusedLocationClient, mapViewportState)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            style = {
                MapboxStandardStyle(
                    standardStyleState = rememberStandardStyleState()
                )
            }
        ) {
            MapEffect(permissionState.status.isGranted) { mapView ->
                if (permissionState.status.isGranted) {
                    mapView.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        enabled = true
                    }
                }
            }

            MapRepository.points.forEach { p ->
                val markerColor = when (p.type) {
                    PointType.USER_REPORTED -> Color.Yellow
                    PointType.CAMERA_ACTIVE -> Color.Red
                    PointType.INFO_POINT -> Color.Blue
                    PointType.PAYMENT_STATION -> Color.Green
                    PointType.CAMERA_TO_CONFIRM -> Color.Magenta
                }

                CircleAnnotation(
                    point = Point.fromLngLat(p.longitude, p.latitude),
                    onClick = {
                        val msg = if (p.photoUri != null) "Punkt typu: ${p.type} (Zdjęcie)" else "Punkt typu: ${p.type}"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        true
                    }
                ) {
                    circleRadius = 8.0
                    circleColor = markerColor
                    circleStrokeWidth = 2.0
                    circleStrokeColor = Color.White
                }
            }
        }


        Column(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Przycisk Zoom +
            FloatingActionButton(
                onClick = {
                    val currentZoom = mapViewportState.cameraState?.zoom ?: 12.0
                    mapViewportState.flyTo(CameraOptions.Builder().zoom(currentZoom + 1.0).build())
                },
                containerColor = Color.White,
                modifier = Modifier.size(42.dp)
            ) { Text("+", style = MaterialTheme.typography.titleLarge) }

            // Przycisk Zoom -
            FloatingActionButton(
                onClick = {
                    val currentZoom = mapViewportState.cameraState?.zoom ?: 12.0
                    mapViewportState.flyTo(CameraOptions.Builder().zoom(currentZoom - 1.0).build())
                },
                containerColor = Color.White,
                modifier = Modifier.size(42.dp)
            ) { Text("-", style = MaterialTheme.typography.titleLarge) }

            Spacer(modifier = Modifier.height(8.dp))

            // Przycisk Centrum
            FloatingActionButton(
                onClick = {
                    if (permissionState.status.isGranted) {
                        moveToUser(context, fusedLocationClient, mapViewportState)
                    } else {
                        permissionState.launchPermissionRequest()
                    }
                },
                containerColor = Color.White
            ) { Icon(Icons.Default.Place, "Lokalizacja") }

            Button(
                onClick = {
                    if (permissionState.status.isGranted) {
                        try {
                            val uri = createTempPictureUri(context)
                            tempPhotoUri = uri
                            cameraLauncher.launch(uri)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Błąd aparatu: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        permissionState.launchPermissionRequest()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Zgłoś kamerę")
            }
        }
    }
}


private fun createTempPictureUri(context: Context): Uri {
    val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
    val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
    val tempFile = java.io.File.createTempFile("SCT_${timeStamp}_", ".jpg", storageDir)

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider", // Automatyczne i bezpieczne dopasowanie ID
        tempFile
    )
}

@SuppressLint("MissingPermission")
private fun reportCameraWithPhoto(
    context: Context,
    client: FusedLocationProviderClient,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    photoUri: Uri
) {
    client.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            coroutineScope.launch {
                MapRepository.addPoint(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    photoUri = photoUri.toString()
                )
                Toast.makeText(context, "Zgłoszono kamerę ze zdjęciem!", Toast.LENGTH_SHORT).show()
            }
        } else {
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { freshLocation ->
                    freshLocation?.let {
                        coroutineScope.launch {
                            MapRepository.addPoint(
                                latitude = it.latitude,
                                longitude = it.longitude,
                                photoUri = photoUri.toString()
                            )
                            Toast.makeText(context, "Zgłoszono kamerę (świeża lokalizacja)!", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run { Toast.makeText(context, "Błąd GPS. Spróbuj ponownie.", Toast.LENGTH_SHORT).show() }
                }
        }
    }
}

@SuppressLint("MissingPermission")
private fun moveToUser(
    context: Context,
    client: FusedLocationProviderClient,
    mapViewportState: MapViewportState
) {
    client.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            mapViewportState.flyTo(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(location.longitude, location.latitude))
                    .zoom(15.0)
                    .build()
            )
        } else {
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { freshLocation ->
                    freshLocation?.let {
                        mapViewportState.flyTo(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(it.longitude, it.latitude))
                                .zoom(15.0)
                                .build()
                        )
                    }
                }
        }
    }
}