package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SkyGradientStart, SkyGradientEnd)))
            .padding(24.dp)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Top text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Moon Girl",
                color = PrimaryPurple,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            TextButton(onClick = onComplete) {
                Text(
                    text = "Bỏ qua",
                    color = OnSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Center 3D room placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF6B21C8), Color(0xFF8B5CF6)))),
            contentAlignment = Alignment.Center
        ) {
            // Simplified placeholder for the house
            Text("🏠", fontSize = 100.sp)
        }

        // Bottom area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Chào mừng",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Moon Girl - hệ thống tập trung biến thành những viên gạch xây dựng ngôi nhà mơ ước của bạn.",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(PrimaryPurple))
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(OnSurfaceVariant.copy(alpha = 0.3f)))
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(OnSurfaceVariant.copy(alpha = 0.3f)))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text(text = "TIẾP TỤC", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
