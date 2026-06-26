package com.example.myapp_428281_jj

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class PointType {
    USER_REPORTED,
    CAMERA_ACTIVE,
    INFO_POINT,
    CAMERA_TO_CONFIRM,
    PAYMENT_STATION
}

@Entity(tableName = "map_points")
data class MapPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val type: PointType = PointType.USER_REPORTED,
    val photoUri: String? = null
)

class MapTypeConverters {
    @TypeConverter
    fun fromPointType(value: PointType): String = value.name

    @TypeConverter
    fun toPointType(value: String): PointType = PointType.valueOf(value)
}
