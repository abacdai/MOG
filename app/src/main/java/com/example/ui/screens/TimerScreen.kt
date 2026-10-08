package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.border
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.viewmodel.SharedStatsViewModel
import com.example.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TimerScreen(viewModel: SharedStatsViewModel, snackbarHostState: SnackbarHostState, onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val timerRunning by viewModel.timerRunning.collectAsStateWithLifecycle()
    val remainingSeconds by viewModel.remainingSeconds.collectAsStateWithLifecycle()
    val selectedMode by viewModel.selectedMode.collectAsStateWithLifecycle()
    val focusMinutes by viewModel.focusMinutes.collectAsStateWithLifecycle()

    var wasRunning by remember { mutableStateOf(false) }
    LaunchedEffect(timerRunning, remainingSeconds) {
        if (timerRunning) {
            wasRunning = true
        } else if (wasRunning) {
            wasRunning = false
            onNavigateBack()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> Toast.makeText(context, event.msg, Toast.LENGTH_SHORT).show()
                is UiEvent.StrictModeBlocked -> snackbarHostState.showSnackbar("Strict Mode: không thể dừng!")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Brush.verticalGradient(listOf(PrimaryPurpleLight, PrimaryPurple)))
                .statusBarsPadding()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            val progress = (remainingSeconds.toFloat() / (focusMinutes * 60f).coerceAtLeast(1f)).coerceIn(0f, 1f)
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(300.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color(0xFFE2E8FF),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx())
                    )
                    drawArc(
                        color = Color.White,
                        startAngle = -90f,
                        sweepAngle = progress * 360f,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val m = remainingSeconds / 60
                    val s = remainingSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", m, s),
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when(selectedMode) {
                                "infinite" -> "VÔ HẠN"
                                "strict" -> "NGHIÊM NGẶT"
                                else -> "BÌNH THƯỜNG"
                            },
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Bottom area controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(GlassSurface)
                    .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("infinite" to "Vô hạn", "normal" to "Bình thường", "strict" to "Nghiêm ngặt").forEach { (key, label) ->
                    val isSelected = selectedMode == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) PrimaryPurple else Color.Transparent)
                            .clickable(enabled = !timerRunning) { viewModel.setMode(key) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else OnSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (selectedMode == "strict") {
                Text(
                    text = "Không thể dừng sớm",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!timerRunning) {
                // Focus Slider
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Thời gian tập trung", color = Color.Black, fontWeight = FontWeight.Bold)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GlassSurface)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("$focusMinutes phút", color = PrimaryPurple, fontWeight = FontWeight.Bold)
                        }
                    }
                    Slider(
                        value = focusMinutes.toFloat(),
                        onValueChange = { viewModel.setFocusMinutes(it.toInt()) },
                        valueRange = 5f..60f,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryPurple,
                            activeTrackColor = PrimaryPurpleLight
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Play/Pause
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(PrimaryPurple)
                    .clickable {
                        if (!timerRunning) viewModel.startTimer() else viewModel.pauseTimer()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (timerRunning) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
