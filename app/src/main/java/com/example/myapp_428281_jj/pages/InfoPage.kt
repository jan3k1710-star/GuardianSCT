package com.example.myapp_428281_jj.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp_428281_jj.MapRepository
import kotlinx.coroutines.launch

@Composable
fun InfoPage(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()

    val totalSpent by MapRepository.totalExpenses.collectAsState(initial = 0.0)

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Zarządzanie opłatami SCT", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { coroutineScope.launch { MapRepository.addExpense(2.50) } },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2e7d32))
        ) {
            Text("Opłata 1h (2.50 zł)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { coroutineScope.launch { MapRepository.addExpense(5.00) } },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2e7d32))
        ) {
            Text("Opłata cały dzień (5.00 zł)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { coroutineScope.launch { MapRepository.addExpense(100.00) } },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2e7d32))
        ) {
            Text("Abonament miesięczny (100.00 zł)")
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Suma wydatków w tym miesiącu: ${"%.2f".format(totalSpent)} zł",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF000000)
        )

        TextButton(
            onClick = { coroutineScope.launch { MapRepository.clearExpenses() } },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Resetuj licznik", color = Color.Red)
        }
    }
}