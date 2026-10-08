package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
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
fun CommunityScreen() {
    Scaffold(
        containerColor = BackgroundLight,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = PrimaryPurple,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add Room")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            // Search Bar
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search", tint = OnSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tìm phòng học...", color = OnSurfaceVariant)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Room list
            val rooms = listOf(
                RoomItem("Học khuya cùng Moon", "12/20", Color(0xFF4F46E5)),
                RoomItem("Focus with Music", "45/50", EmeraldGreen),
                RoomItem("IELTS Reading 8.0+", "8/10", SkyBlue)
            )
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(rooms) { room ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(room.color)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(room.name, fontWeight = FontWeight.Bold)
                                Text(room.members, fontSize = 12.sp, color = OnSurfaceVariant)
                            }
                            IconButton(onClick = { }) {
                                Icon(imageVector = Icons.Outlined.ArrowForward, contentDescription = "Join", tint = PrimaryPurple)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class RoomItem(val name: String, val members: String, val color: Color)
