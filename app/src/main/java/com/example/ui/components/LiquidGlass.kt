package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Brightness5
import androidx.compose.material.icons.rounded.Brightness6
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.domain.model.PrayerName
import com.example.ui.theme.LocalNoorColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Ambient atmospheric background with subtle Islamic 8-point geometric Rub el Hizb lattice
 * and gentle emerald/gold radial luminescence.
 */
@Composable
fun IslamicGeometricBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val noorColors = LocalNoorColors.current
    val bgColor = MaterialTheme.colorScheme.background
    val patternColor = noorColors.patternTint
    val glowColor = if (noorColors.isDark) Color(0x2A0E6B4D) else Color(0x1C19936B)
    val goldGlow = if (noorColors.isDark) Color(0x1CD4AF6A) else Color(0x16D4AF6A)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Subtle top emerald ambient radiance
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(glowColor, Color.Transparent),
                    center = Offset(size.width * 0.25f, size.height * 0.08f),
                    radius = size.width * 0.9f
                )
            )
            // Subtle antique gold radiance opposite corner
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(goldGlow, Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.22f),
                    radius = size.width * 0.7f
                )
            )

            // Geometric 8-point star Islamic tessellation (very subtle architectural motif)
            val cellSize = 112.dp.toPx()
            val radius = cellSize * 0.28f
            val cols = (size.width / cellSize).toInt() + 2
            val rows = (size.height / cellSize).toInt() + 2

            for (r in 0..rows) {
                for (c in 0..cols) {
                    val cx = c * cellSize + if (r % 2 == 1) cellSize * 0.5f else 0f
                    val cy = r * cellSize * 0.88f
                    drawRubElHizb(cx, cy, radius, patternColor)
                }
            }
        }
        content()
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRubElHizb(
    cx: Float,
    cy: Float,
    radius: Float,
    color: Color
) {
    val stroke = Stroke(width = 1.dp.toPx())
    val path1 = Path()
    val path2 = Path()
    for (i in 0 until 4) {
        val a1 = (i * 90.0) * PI / 180.0
        val x1 = cx + (radius * cos(a1)).toFloat()
        val y1 = cy + (radius * sin(a1)).toFloat()
        if (i == 0) path1.moveTo(x1, y1) else path1.lineTo(x1, y1)

        val a2 = (i * 90.0 + 45.0) * PI / 180.0
        val x2 = cx + (radius * cos(a2)).toFloat()
        val y2 = cy + (radius * sin(a2)).toFloat()
        if (i == 0) path2.moveTo(x2, y2) else path2.lineTo(x2, y2)
    }
    path1.close()
    path2.close()
    drawPath(path1, color = color, style = stroke)
    drawPath(path2, color = color, style = stroke)
}

/**
 * iOS-inspired Liquid Glass translucent container with specular gradient border and layered depth.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    strong: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val noorColors = LocalNoorColors.current
    val fillBrush = if (noorColors.isDark) {
        Brush.verticalGradient(
            colors = listOf(
                if (strong) Color(0xE8142B23) else Color(0xC911241E),
                if (strong) Color(0xDA0B1813) else Color(0xB3091410)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                if (strong) Color(0xF5FFFFFF) else Color(0xE6FFFFFF),
                if (strong) Color(0xECF4F9F6) else Color(0xD8F2F7F4)
            )
        )
    }

    val borderBrush = Brush.linearGradient(
        colors = listOf(noorColors.glassBorderTop, noorColors.glassBorderBottom)
    )

    val clickMod = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Box(
        modifier = modifier
            .clip(shape)
            .background(fillBrush)
            .border(width = 1.dp, brush = borderBrush, shape = shape)
            .then(clickMod)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun LiquidGlassChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val noorColors = LocalNoorColors.current
    val bgColor = when {
        selected -> MaterialTheme.colorScheme.primary
        noorColors.isDark -> Color(0x88132720)
        else -> Color(0xCCFFFFFF)
    }
    val contentColor = when {
        selected -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }
    val borderColor = if (selected) {
        noorColors.goldAccent.copy(alpha = 0.65f)
    } else {
        noorColors.glassBorderBottom
    }

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = bgColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun NoorSectionHeader(
    title: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val noorColors = LocalNoorColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 18.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(noorColors.goldAccent)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        if (actionLabel != null && onActionClick != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = noorColors.goldAccent,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun NoorScreenHeader(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(LocalNoorColors.current.glassSurface)
                    .border(1.dp, LocalNoorColors.current.glassBorderBottom, CircleShape)
                    .testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailing()
        }
    }
}

fun prayerIcon(prayer: PrayerName): ImageVector = when (prayer) {
    PrayerName.FAJR -> Icons.Rounded.WbTwilight
    PrayerName.SUNRISE -> Icons.Rounded.Brightness5
    PrayerName.DHUHR -> Icons.Rounded.WbSunny
    PrayerName.ASR -> Icons.Rounded.Brightness6
    PrayerName.MAGHRIB -> Icons.Rounded.NightsStay
    PrayerName.ISHA -> Icons.Rounded.Bedtime
}
