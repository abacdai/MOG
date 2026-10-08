package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.GlassCardSmall
import com.example.ui.theme.*
import com.example.viewmodel.SharedStatsViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: SharedStatsViewModel,
    onNavigateToTimer: () -> Unit
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val hasUsagePermission by viewModel.hasUsagePermission.collectAsStateWithLifecycle()
    val sessions by viewModel.recentSessions.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkAndSyncHealthData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F9FD))
            .verticalScroll(rememberScrollState())
    ) {
        // ==========================================
        // HERO SECTION (Landscape with Sun, Cottage & Mountains)
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(268.dp)
        ) {
            // Scenic landscape image illustration
            Image(
                painter = painterResource(id = R.drawable.img_hero_landscape),
                contentDescription = "Phong cảnh thung lũng",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(268.dp),
                contentScale = ContentScale.Crop
            )

            // Bottom subtle fade gradient into the card section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xFFF6F9FD).copy(alpha = 0.75f),
                                Color(0xFFF6F9FD)
                            )
                        )
                    )
            )

            // Streak Pill (Top Left: 🔥 0 Ngày)
            Surface(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 12.dp, start = 20.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.88f),
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔥", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${stats.streak} Ngày",
                        color = Color(0xFF1E293B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }

            // Decorative soft cloud capsule below streak badge (as seen in mockup)
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 52.dp, start = 30.dp)
                    .size(width = 62.dp, height = 18.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color.White.copy(alpha = 0.40f))
            )

            // Greeting: Xin chào, Minh ✦
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 22.dp, bottom = 24.dp)
            ) {
                Text(
                    text = "Xin chào,",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A),
                    lineHeight = 36.sp,
                    letterSpacing = (-0.5).sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Minh",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        lineHeight = 36.sp,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✦",
                        fontSize = 26.sp,
                        color = Color(0xFF0F172A)
                    )
                }
            }
        }

        // ==========================================
        // MAIN CARDS SECTION
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-16).dp)
                .padding(horizontal = 18.dp)
        ) {
            // MOONCOINS CARD
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(124.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(26.dp),
                        spotColor = Color(0xFF2DD4BF).copy(alpha = 0.28f),
                        ambientColor = Color(0xFF2DD4BF).copy(alpha = 0.12f)
                    )
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFFE2F9F0),
                                Color(0xFFF7FFFD),
                                Color(0xFFE8F6FF)
                            )
                        )
                    )
                    .border(BorderStroke(1.5.dp, Color(0xFFB5F2E1)), RoundedCornerShape(26.dp))
            ) {
                // Subtle inner fluid wave
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    val wavePath = Path().apply {
                        moveTo(0f, h * 0.70f)
                        cubicTo(w * 0.35f, h * 0.90f, w * 0.55f, h * 0.32f, w, h * 0.42f)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(wavePath, color = Color.White.copy(alpha = 0.55f))
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 22.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFBBF24)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "★",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = "MOONCOINS",
                                fontSize = 11.sp,
                                letterSpacing = 1.3.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        val formattedCoins = NumberFormat.getNumberInstance(Locale.US).format(stats.moonCoins)
                        Text(
                            text = formattedCoins,
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A),
                            lineHeight = 52.sp
                        )
                    }

                    // 3D Golden Coin with Embossed Star
                    Image(
                        painter = painterResource(id = R.drawable.img_coin_3d),
                        contentDescription = "MoonCoins Coin",
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 3 STAT CARDS (Ngủ, Tập trung, Màn hình)
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Thẻ Ngủ
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(118.dp)
                        .clickable {
                            if (!hasUsagePermission) viewModel.openUsageSettings()
                            else viewModel.checkAndSyncHealthData()
                        },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 14.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_icon_moon),
                            contentDescription = "Ngủ",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Ngủ",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${stats.sleepHours}h",
                                color = Color(0xFF0284C7),
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp
                            )
                        }
                    }
                }

                // Thẻ Tập trung
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(118.dp)
                        .clickable { onNavigateToTimer() },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 14.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_icon_hourglass),
                            contentDescription = "Tập trung",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Tập trung",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${stats.totalFocusMinutesToday}m",
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp
                            )
                        }
                    }
                }

                // Thẻ Màn hình
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(118.dp)
                        .clickable {
                            if (!hasUsagePermission) viewModel.openUsageSettings()
                            else viewModel.checkAndSyncHealthData()
                        },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 14.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_icon_phone),
                            contentDescription = "Màn hình",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Màn hình",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${stats.screenTimeHours}h",
                                color = Color(0xFFF97316),
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // INSIGHT / TIP CARD
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToTimer() },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_icon_bulb),
                        contentDescription = "Gợi ý",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Bắt đầu phiên tập trung đầu tiên để nhận MoonCoins và khởi động chuỗi ngày (Streak) nhé!",
                        color = Color(0xFF334155),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 17.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF818CF8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // CTA BUTTON: BẮT ĐẦU TẬP TRUNG
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = Color(0xFF7C3AED).copy(alpha = 0.45f),
                        ambientColor = Color(0xFF7C3AED).copy(alpha = 0.20f)
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF8B5CF6),
                                Color(0xFF7C3AED),
                                Color(0xFF6D28D9)
                            )
                        )
                    )
                    .clickable { onNavigateToTimer() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "BẮT ĐẦU TẬP TRUNG",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.28f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Bắt đầu",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // ==========================================
            // RECENT SESSIONS (if any)
            // ==========================================
            if (sessions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Lịch sử tập trung gần đây",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(8.dp))

                sessions.take(3).forEach { session ->
                    val timeFormatted = SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault()).format(Date(session.timestamp))
                    GlassCardSmall(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryPurple.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("⏱", fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${session.durationMinutes} phút tập trung",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = OnSurfaceVariant
                                    )
                                    Text(
                                        text = timeFormatted,
                                        fontSize = 11.sp,
                                        color = OnSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                            }
                            Text(
                                text = "+${session.coinsEarned} ★",
                                color = AmberAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Bottom space to prevent overlap with floating navigation bar
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}
