package com.example.ui.components

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface

fun Modifier.frostedBackgroundBlur(
    blurRadius: Float = 16f,
    color: Color = Color(0x33FFFFFF),
    cornerRadius: Float = 60f
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
                this.color = color.toArgb()
            }
        }
        canvas.drawRoundRect(
            0f, 0f, size.width, size.height, cornerRadius, cornerRadius, paint
        )
    }
}

@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .frostedBackgroundBlur(blurRadius = 24f, color = GlassSurface, cornerRadius = 60f)
            .clip(RoundedCornerShape(24.dp))
            .background(GlassSurface)
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp)),
        content = content
    )
}

@Composable
fun GlassCardSmall(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .frostedBackgroundBlur(blurRadius = 16f, color = Color(0x66FFFFFF), cornerRadius = 40f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x66FFFFFF))
            .border(1.dp, Color(0x80FFFFFF), RoundedCornerShape(16.dp)),
        content = content
    )
}

