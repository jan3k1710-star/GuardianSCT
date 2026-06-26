package com.example.myapp_428281_jj

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.myapp_428281_jj.pages.InfoPage
import com.example.myapp_428281_jj.pages.MainPage
import com.example.myapp_428281_jj.pages.MapPage
import com.example.myapp_428281_jj.pages.PointsPage
import com.example.myapp_428281_jj.MapStateHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navItemList = listOf(
        NavItem(label = "Główny", icon = Icons.Default.Home),
        NavItem(label = "Mapa", icon = Icons.Default.Place),
        NavItem(label = "Punkty", icon = Icons.Default.ShoppingCart),
        NavItem(label = "Info", icon = Icons.Default.DateRange)
    )

    var selectedIndex by remember { mutableIntStateOf(1) }
    val mapStateHolder = remember { MapStateHolder() }

    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(imageVector = navItem.icon, contentDescription = navItem.label) },
                        label = { Text(text = navItem.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIndex = selectedIndex,
            mapStateHolder = mapStateHolder
        )
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex: Int, mapStateHolder: MapStateHolder) {
    when(selectedIndex) {
        0 -> MainPage(modifier = modifier)
        1 -> MapPage(modifier = modifier, mapStateHolder = mapStateHolder)
        2 -> PointsPage(modifier = modifier)
        3 -> InfoPage(modifier = modifier)
    }
}