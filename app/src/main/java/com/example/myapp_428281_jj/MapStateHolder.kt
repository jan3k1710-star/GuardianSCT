package com.example.myapp_428281_jj

import androidx.compose.runtime.mutableStateOf
import com.mapbox.geojson.Point

data class MapState(
    val zoom: Double = 12.0,
    val center: Point = Point.fromLngLat(19.94, 50.06) // Kraków
)

class MapStateHolder {
    var mapState = mutableStateOf(MapState())
        private set

    fun updateMapState(zoom: Double, center: Point) {
        mapState.value = MapState(zoom = zoom, center = center)
    }
}