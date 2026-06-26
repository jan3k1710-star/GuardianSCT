package com.example.myapp_428281_jj

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

object MapRepository {
    private lateinit var mapPointDao: MapPointDao
    val points = mutableStateListOf<MapPoint>()

    fun initialize(dao: MapPointDao) {
        mapPointDao = dao
    }

    val totalExpenses: Flow<Double?> by lazy { mapPointDao.getTotalExpenses() }

    suspend fun addExpense(amount: Double) {
        mapPointDao.insertExpense(Expense(amount = amount))
    }

    suspend fun clearExpenses() {
        mapPointDao.deleteAllExpenses()
    }

    suspend fun loadPoints() {
        mapPointDao.getAllPoints().collect { newPoints ->
            points.clear()
            points.addAll(newPoints)
        }
    }

    suspend fun addPoint(latitude: Double, longitude: Double, type: PointType = PointType.USER_REPORTED, photoUri: String? = null) {
        val newPoint = MapPoint(latitude = latitude, longitude = longitude, type = type, photoUri = photoUri)
        mapPointDao.insert(newPoint)
    }

    suspend fun removePoint(point: MapPoint) {
        mapPointDao.deletePoint(point)
    }

    suspend fun initializeDatabaseIfEmpty() {
        val currentPoints = mapPointDao.getAllPoints().first()
        if (currentPoints.isEmpty()) {
            val staticPoints = listOf(
                // Kamery SCT w Krakowie
                MapPoint(1001, 50.08726, 19.89143, PointType.CAMERA_ACTIVE), // Conrada X Armii Krajowej
                MapPoint(1002, 50.08613, 19.95474, PointType.CAMERA_ACTIVE), // Aleja 29 Listopada X Opolska
                MapPoint(1003, 50.06690, 20.00445, PointType.CAMERA_ACTIVE), // Aleja Pokoju x Nowohucka
                MapPoint(1004, 50.06854, 20.05261, PointType.CAMERA_ACTIVE), // Bulwarowa X Ptaszyckiego
                MapPoint(1005, 50.05669, 20.00105, PointType.CAMERA_ACTIVE), // Nowohucka x Cieplownicza
                MapPoint(1006, 50.05964, 19.97574, PointType.CAMERA_ACTIVE), // Ofiar Dąbia x Aleja Pokoju
                MapPoint(1007, 50.05773, 19.95932, PointType.CAMERA_ACTIVE), // Rondo Grzegórzeckie
                MapPoint(1008, 50.04549, 19.92137, PointType.CAMERA_ACTIVE), // Monte Cassino
                MapPoint(1009, 50.04892, 19.93274, PointType.CAMERA_ACTIVE), // Rondo Grunwaldzkie
                MapPoint(1010, 50.04219, 19.96095, PointType.CAMERA_ACTIVE), // Podgórze SKA
                MapPoint(1011, 50.04511, 19.97360, PointType.CAMERA_ACTIVE), // Nowohucka x Kuklińskiego
                MapPoint(1012, 50.04897, 19.98174, PointType.CAMERA_ACTIVE), // Saska x Nowohucka
                MapPoint(1013, 50.03606, 19.94075, PointType.CAMERA_ACTIVE), // Rondo Matecznego
                MapPoint(1014, 50.04078, 19.98346, PointType.CAMERA_ACTIVE), // Lipska x Saska
                MapPoint(1015, 50.03983, 20.00018, PointType.CAMERA_ACTIVE), // Mały Płaszów
                MapPoint(1016, 50.03907, 20.00449, PointType.CAMERA_ACTIVE), // Lipska x Mierzeja Wiślana
                MapPoint(1017, 50.03859, 20.04263, PointType.CAMERA_ACTIVE), // Śliwaka x Półtanki
                MapPoint(1018, 50.03767, 20.05908, PointType.CAMERA_ACTIVE), // Śliwaka x Wrobela
                MapPoint(1019, 50.03161, 19.92056, PointType.CAMERA_ACTIVE), // Grota-Roweckiego x Kobierzyńska
                MapPoint(1020, 50.02910, 19.93666, PointType.CAMERA_ACTIVE), // Tischnera x Wadowicka
                MapPoint(1021, 50.03271, 19.94587, PointType.CAMERA_ACTIVE), // Powstanców Slaskich x Kamieńskiego
                MapPoint(1022, 50.03145, 19.94575, PointType.CAMERA_ACTIVE), // Turowicza x Tischnera
                MapPoint(1023, 50.03161, 19.94864, PointType.CAMERA_ACTIVE), // Puszkarska x Kamienskiego
                MapPoint(1024, 50.02685, 19.97679, PointType.CAMERA_ACTIVE), // Malborska x Wielicka
                MapPoint(1025, 50.02628, 19.97400, PointType.CAMERA_ACTIVE), // Malbroska x Kamienskiego
                MapPoint(1026, 50.02404, 19.97954, PointType.CAMERA_ACTIVE), // Nowosądecka x Wielicka
                MapPoint(1027, 50.02350, 19.98103, PointType.CAMERA_ACTIVE), // Biezanowska x Wielicka
                MapPoint(1028, 50.01751, 19.88946, PointType.CAMERA_ACTIVE), // pętla Czerwone Maki
                MapPoint(1029, 50.01842, 19.93099, PointType.CAMERA_ACTIVE), // Zakopianska x Zbrojarzy
                MapPoint(1030, 50.01486, 19.94541, PointType.CAMERA_ACTIVE), // Trasa Lagiewnicka x Turowicza
                MapPoint(1031, 50.01394, 19.99943, PointType.CAMERA_ACTIVE), // Teligi x Wielicka
                MapPoint(1032, 50.00905, 20.01136, PointType.CAMERA_ACTIVE), // Wielicka x Czerwiakowskiego
                MapPoint(1033, 50.00152, 20.02590, PointType.CAMERA_ACTIVE), // Wielicka x Nad Serafą
                MapPoint(1034, 50.00702, 19.92492, PointType.CAMERA_ACTIVE), // Jugowicka x Zakopiańska
                MapPoint(1035, 50.00561, 19.94445, PointType.CAMERA_ACTIVE), // Herberta x Halszki
                MapPoint(1036, 49.98738, 19.91462, PointType.CAMERA_ACTIVE), // Zakopianska x Proninska
                MapPoint(1037, 50.00660, 19.87372, PointType.CAMERA_ACTIVE), // Babińskiego x Skotnicka
                MapPoint(1038, 49.99933, 19.86194, PointType.CAMERA_ACTIVE), // Skotnicka
                MapPoint(1039, 49.99570, 19.85248, PointType.CAMERA_ACTIVE), // Skotnicka x Wrony

                // Punkty informacyjne
                MapPoint(2001, 50.09308, 19.89591, PointType.INFO_POINT), // Galeria Bronowice
                MapPoint(2002, 50.10472, 19.96293, PointType.INFO_POINT), // P&R Górka Narodowa
                MapPoint(2003, 50.08753, 19.98216, PointType.INFO_POINT), // POM UMK, Centrum Serenada
                MapPoint(2004, 50.07691, 20.03190, PointType.INFO_POINT), // Urząd Miasta Krakowa os. Zgody 2
                MapPoint(2005, 50.06350, 19.91296, PointType.INFO_POINT), // Biuro Strefy Płatnego Parkowania, Stadion Wisły
                MapPoint(2006, 50.06208, 19.96158, PointType.INFO_POINT), // Urząd Miasta Krakowa al. Powstania Warszawskiego 10
                MapPoint(2007, 50.06353, 20.00789, PointType.INFO_POINT), // Zarząd Dróg Miasta Krakowa, ul. Centralna 53
                MapPoint(2008, 50.03879, 19.96637, PointType.INFO_POINT), // Urząd Miasta Krakowa, ul. Wielicka 28a


                // Stacje BP (płatności)
                MapPoint(3001, 50.09602, 19.87343, PointType.PAYMENT_STATION), // BP 576 Rękawka
                MapPoint(3002, 50.12586, 19.87134, PointType.PAYMENT_STATION), // BP 319 Jasnogórska
                MapPoint(3003, 50.13347, 19.96668, PointType.PAYMENT_STATION), // BP 849 Bibice
                MapPoint(3004, 50.10594, 20.07408, PointType.PAYMENT_STATION), // BP 820 Kocmyrzowska
                MapPoint(3005, 50.07024, 20.10172, PointType.PAYMENT_STATION), // BP 873 Meksyk
                MapPoint(3006, 50.03560, 19.82042, PointType.PAYMENT_STATION), // BP 306 Mirowska
                MapPoint(3007, 49.98161, 19.83715, PointType.PAYMENT_STATION), // BP 882 Skawina
                MapPoint(3008, 49.98164, 19.91215, PointType.PAYMENT_STATION), // BP 573 Lajkonik
                MapPoint(3009, 49.99593, 20.03430, PointType.PAYMENT_STATION), // BP 230 Solanka - Wieliczka

                // Kamery do potwierdzenia
                MapPoint(4001, 50.05519, 19.92755, PointType.CAMERA_TO_CONFIRM), // Zwierzyniecka x Most Dębnicki
                MapPoint(4002, 50.04904, 19.90358, PointType.CAMERA_TO_CONFIRM) // Księcia Józefa x Malczewskiego
            )
            mapPointDao.insertAll(staticPoints)
        }
    }
}
