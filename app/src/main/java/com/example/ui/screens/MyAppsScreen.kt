package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun MyAppsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Block list
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Danh sách chặn", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("8 Apps", color = OnSurfaceVariant, fontSize = 14.sp)
                        Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = null, tint = OnSurfaceVariant)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listOf("Y", "I", "T", "F")) { app ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(app, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Lịch trình", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        // Schedule section
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Từ: 20:00", fontWeight = FontWeight.SemiBold)
                    Text("Đến: 22:00", fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("S", "M", "T", "W", "T", "F", "S").forEachIndexed { index, day ->
                        val isActive = index in 1..5
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isActive) PrimaryPurple else GlassSurface)
                                .border(1.dp, if (isActive) PrimaryPurple else GlassBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(day, color = if (isActive) Color.White else OnSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Quy tắc", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GlassCard(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(imageVector = Icons.Outlined.Smartphone, contentDescription = null, tint = PrimaryPurple)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Giới hạn MXH", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Tối đa 1h/ngày", fontSize = 12.sp, color = OnSurfaceVariant)
                }
            }
            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .border(2.dp, PrimaryPurple, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(imageVector = Icons.Outlined.Schedule, contentDescription = null, tint = PrimaryPurple)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Giờ đi ngủ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Sau 23:00", fontSize = 12.sp, color = OnSurfaceVariant)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurpleLight),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Xem Báo Cáo Tuần", fontWeight = FontWeight.Bold)
        }
    }
}
