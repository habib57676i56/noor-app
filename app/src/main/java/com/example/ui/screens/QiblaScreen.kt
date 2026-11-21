package com.example.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CompassCalibration
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.AppLanguage
import com.example.domain.prayer.QiblaCalculator
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.NoorScreenHeader
import com.example.ui.i18n.L10n
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LocalNoorColors
import com.example.ui.theme.SemanticSuccess
import com.example.viewmodel.NoorViewModel
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun QiblaScreen(
    viewModel: NoorViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lang = LocalAppLanguage.current
    val isAr = lang == AppLanguage.ARABIC
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val noorColors = LocalNoorColors.current

    val qiblaBearing = remember(prefs.latitude, prefs.longitude) {
        QiblaCalculator.calculateQiblaBearing(prefs.latitude, prefs.longitude)
    }
    val distanceKm = remember(prefs.latitude, prefs.longitude) {
        QiblaCalculator.calculateDistanceToMakkahKm(prefs.latitude, prefs.longitude).roundToInt()
    }

    var sensorAvailable by remember { mutableStateOf(true) }
    var headingDeg by remember { mutableFloatStateOf(0f) }
    var accuracyLevel by remember { mutableIntStateOf(SensorManager.SENSOR_STATUS_ACCURACY_HIGH) }
    var showCalibrationDialog by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (rotationSensor == null && (accelSensor == null || magnetSensor == null)) {
            sensorAvailable = false
            onDispose { }
        } else {
            val gravity = FloatArray(3)
            val geomagnetic = FloatArray(3)
            var hasGravity = false
            var hasMagnet = false
            val rotationMatrix = FloatArray(9)
            val orientation = FloatArray(3)

            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val azimuthDeg = when (event.sensor.type) {
                        Sensor.TYPE_ROTATION_VECTOR -> {
                            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                            SensorManager.getOrientation(rotationMatrix, orientation)
                            Math.toDegrees(orientation[0].toDouble()).toFloat()
                        }
                        Sensor.TYPE_ACCELEROMETER -> {
                            System.arraycopy(event.values, 0, gravity, 0, 3)
                            hasGravity = true
                            if (hasMagnet && SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                                SensorManager.getOrientation(rotationMatrix, orientation)
                                Math.toDegrees(orientation[0].toDouble()).toFloat()
                            } else return
                        }
                        Sensor.TYPE_MAGNETIC_FIELD -> {
                            System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                            hasMagnet = true
                            if (hasGravity && SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                                SensorManager.getOrientation(rotationMatrix, orientation)
                                Math.toDegrees(orientation[0].toDouble()).toFloat()
                            } else return
                        }
                        else -> return
                    }
                    val normalized = (azimuthDeg + 360f) % 360f
                    // Smooth low-pass filter on circular degrees
                    val delta = ((normalized - headingDeg + 540f) % 360f) - 180f
                    headingDeg = (headingDeg + delta * 0.2f + 360f) % 360f
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    accuracyLevel = accuracy
                }
            }

            if (rotationSensor != null) {
                sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
            } else {
                sensorManager.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_UI)
                sensorManager.registerListener(listener, magnetSensor, SensorManager.SENSOR_DELAY_UI)
            }

            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    val deltaToQibla = QiblaCalculator.shortestAngleDelta(headingDeg.toDouble(), qiblaBearing)
    val isAligned = sensorAvailable && abs(deltaToQibla) <= 6.0
    val animatedDialRotation by animateFloatAsState(
        targetValue = if (sensorAvailable) -headingDeg else 0f,
        label = "compass_dial"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("qibla_screen"),
        contentPadding = PaddingValues(bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            NoorScreenHeader(
                title = L10n.pick(lang, "اتجاه القبلة", "Qibla Compass"),
                subtitle = if (isAr) "${prefs.cityNameAr} · المسافة إلى الكعبة $distanceKm كم"
                else "${prefs.cityNameEn} · $distanceKm km to Kaaba",
                onBack = onBack,
                trailing = {
                    IconButton(
                        onClick = { showCalibrationDialog = true },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(noorColors.glassSurface)
                            .border(1.dp, noorColors.glassBorderBottom, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CompassCalibration,
                            contentDescription = L10n.pick(lang, "معايرة البوصلة", "Calibrate Compass"),
                            tint = noorColors.goldAccent
                        )
                    }
                }
            )
        }

        // Sensor unavailable warning (graceful real fallback, no fake sensor motion)
        if (!sensorAvailable) {
            item {
                LiquidGlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.WarningAmber, null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = L10n.pick(
                                lang,
                                "مستشعر البوصلة غير متوفر على هذا الجهاز. تم عرض زاوية القبلة الدقيقة بالنسبة للشمال الحقيقي (${qiblaBearing.roundToInt()}°).",
                                "Compass sensor is unavailable on this device. Showing exact Qibla bearing relative to True North (${qiblaBearing.roundToInt()}°)."
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Luxury Compass Dial Card
        item {
            LiquidGlassCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                strong = true,
                contentPadding = PaddingValues(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = L10n.pick(lang, "زاوية القبلة", "Qibla Angle"),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${qiblaBearing.roundToInt()}°",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = noorColors.goldAccent
                        )
                    }

                    if (isAligned) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(SemanticSuccess.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Rounded.CheckCircle, null, tint = SemanticSuccess, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = L10n.pick(lang, "أنت باتجاه القبلة", "Aligned to Qibla"),
                                style = MaterialTheme.typography.labelLarge,
                                color = SemanticSuccess,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else if (sensorAvailable) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = L10n.pick(lang, "اتجاه الهاتف", "Heading"),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${headingDeg.roundToInt()}°",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(270.dp)) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val r = size.minDimension / 2f - 8.dp.toPx()

                        // Outer luminous ring
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(EmeraldPrimary.copy(alpha = 0.28f), EmeraldDeep.copy(alpha = 0.9f))
                            ),
                            radius = r
                        )
                        drawCircle(
                            color = if (isAligned) SemanticSuccess else GoldPrimary.copy(alpha = 0.7f),
                            radius = r,
                            style = Stroke(width = if (isAligned) 4.dp.toPx() else 2.dp.toPx())
                        )

                        // Rotating compass rose
                        rotate(degrees = animatedDialRotation, pivot = Offset(cx, cy)) {
                            for (deg in 0 until 360 step 15) {
                                val rad = (deg - 90) * PI / 180.0
                                val isCardinal = deg % 90 == 0
                                val tickLen = if (isCardinal) 16.dp.toPx() else 8.dp.toPx()
                                val start = Offset(
                                    cx + ((r - tickLen) * cos(rad)).toFloat(),
                                    cy + ((r - tickLen) * sin(rad)).toFloat()
                                )
                                val end = Offset(
                                    cx + ((r - 2.dp.toPx()) * cos(rad)).toFloat(),
                                    cy + ((r - 2.dp.toPx()) * sin(rad)).toFloat()
                                )
                                drawLine(
                                    color = if (deg == 0) Color(0xFFE05A5A) else Color(0x88FFFFFF),
                                    start = start,
                                    end = end,
                                    strokeWidth = if (isCardinal) 3.dp.toPx() else 1.2.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }

                            // Golden Qibla Pointer Needle
                            rotate(degrees = qiblaBearing.toFloat(), pivot = Offset(cx, cy)) {
                                val pointer = Path().apply {
                                    moveTo(cx, cy - r + 20.dp.toPx())
                                    lineTo(cx - 14.dp.toPx(), cy + 12.dp.toPx())
                                    lineTo(cx, cy - 4.dp.toPx())
                                    lineTo(cx + 14.dp.toPx(), cy + 12.dp.toPx())
                                    close()
                                }
                                drawPath(
                                    path = pointer,
                                    brush = Brush.verticalGradient(
                                        listOf(if (isAligned) SemanticSuccess else GoldBright, GoldPrimary)
                                    )
                                )
                                // Kaaba golden square emblem near tip
                                drawCircle(
                                    color = if (isAligned) SemanticSuccess else GoldBright,
                                    radius = 8.dp.toPx(),
                                    center = Offset(cx, cy - r + 20.dp.toPx())
                                )
                            }
                        }

                        // Fixed top phone alignment notch
                        drawLine(
                            color = GoldBright,
                            start = Offset(cx, 0f),
                            end = Offset(cx, 14.dp.toPx()),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawCircle(color = GoldBright, radius = 6.dp.toPx(), center = Offset(cx, cy))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = L10n.pick(
                        lang,
                        "أبقِ الهاتف مستوياً وبعيداً عن المعادن والأجسام المغناطيسية.",
                        "Hold your device flat and away from magnetic or metal objects."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Calibration Guidance Card
        item {
            LiquidGlassCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = { showCalibrationDialog = true }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Info, null, tint = noorColors.goldAccent)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = L10n.pick(lang, "إرشادات معايرة البوصلة", "Compass Calibration Guide"),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        val accuracyText = when (accuracyLevel) {
                            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> L10n.pick(lang, "دقة المستشعر: عالية", "Sensor Accuracy: High")
                            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> L10n.pick(lang, "دقة المستشعر: متوسطة", "Sensor Accuracy: Medium")
                            else -> L10n.pick(lang, "دقة المستشعر: تحتاج معايرة", "Sensor Accuracy: Calibration Recommended")
                        }
                        Text(
                            text = accuracyText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showCalibrationDialog) {
        AlertDialog(
            onDismissRequest = { showCalibrationDialog = false },
            title = { Text(L10n.pick(lang, "معايرة البوصلة", "Calibrate Compass")) },
            text = {
                Text(
                    L10n.pick(
                        lang,
                        "لتحسين دقة البوصلة، حرّك هاتفك في الهواء على شكل الرقم (8) باللغة الإنجليزية ثلاث مرات مع تدوير المعصم ببطء، وتأكد من الابتعاد عن الأجهزة الكهربائية والمغناطيس.",
                        "To calibrate the magnetometer, gently wave your phone in a figure-8 motion three times while tilting the device, away from magnets or electronics."
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { showCalibrationDialog = false }) {
                    Text(L10n.pick(lang, "حسناً", "Got it"))
                }
            }
        )
    }
}
