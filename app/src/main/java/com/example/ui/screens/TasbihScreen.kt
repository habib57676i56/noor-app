package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.catalog.AdhkarCatalog
import com.example.domain.model.AppLanguage
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.NoorScreenHeader
import com.example.ui.components.NoorSectionHeader
import com.example.ui.i18n.L10n
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LocalNoorColors
import com.example.viewmodel.NoorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TasbihScreen(viewModel: NoorViewModel) {
    val lang = LocalAppLanguage.current
    val isAr = lang == AppLanguage.ARABIC
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val todayAct by viewModel.todayActivity.collectAsStateWithLifecycle()
    val sessions by viewModel.tasbihSessions.collectAsStateWithLifecycle()
    val noorColors = LocalNoorColors.current

    var showResetConfirm by remember { mutableStateOf(false) }
    var showTargetDialog by remember { mutableStateOf(false) }

    val activePreset = AdhkarCatalog.tasbihPresets.firstOrNull { it.id == prefs.activeTasbihPresetId }
    val activeLabelAr = activePreset?.textAr ?: prefs.customTasbihText
    val activeLabelEn = activePreset?.textEn ?: prefs.customTasbihText

    val count by viewModel.liveTasbihCount.collectAsStateWithLifecycle()
    val target = prefs.activeTasbihTarget.coerceAtLeast(1)
    val cycleCount = count % target
    val progress = if (count > 0 && cycleCount == 0) 1f else cycleCount.toFloat() / target.toFloat()
    val animatedSweep by animateFloatAsState(
        targetValue = progress * 360f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tasbih_arc"
    )
    val completedRounds = count / target

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tasbih_press"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("tasbih_screen"),
        contentPadding = PaddingValues(bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            NoorScreenHeader(
                title = L10n.pick(lang, "المسبحة الإلكترونية", "Electronic Tasbih"),
                subtitle = L10n.pick(
                    lang,
                    "إجمالي تسبيحات اليوم: ${todayAct?.tasbihCount ?: 0}",
                    "Today's total: ${todayAct?.tasbihCount ?: 0}"
                ),
                trailing = {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = { viewModel.setTasbihVibration(!prefs.tasbihVibrationEnabled) },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(noorColors.glassSurface)
                                .border(1.dp, noorColors.glassBorderBottom, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Vibration,
                                contentDescription = L10n.pick(lang, "الاهتزاز", "Vibration"),
                                tint = if (prefs.tasbihVibrationEnabled) noorColors.goldAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.setTasbihSound(!prefs.tasbihSoundEnabled) },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(noorColors.glassSurface)
                                .border(1.dp, noorColors.glassBorderBottom, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (prefs.tasbihSoundEnabled) Icons.AutoMirrored.Rounded.VolumeUp else Icons.AutoMirrored.Rounded.VolumeOff,
                                contentDescription = L10n.pick(lang, "الصوت", "Sound"),
                                tint = if (prefs.tasbihSoundEnabled) noorColors.goldAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            )
        }

        // Preset Dhikr Horizontal Picker
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AdhkarCatalog.tasbihPresets) { preset ->
                    LiquidGlassChip(
                        label = if (isAr) preset.textAr else preset.textEn.substringBefore(" ("),
                        selected = prefs.activeTasbihPresetId == preset.id,
                        onClick = { viewModel.selectTasbihPreset(preset.id, preset.defaultTarget) }
                    )
                }
                item {
                    LiquidGlassChip(
                        label = L10n.pick(lang, "ذكر مخصص", "Custom"),
                        selected = prefs.activeTasbihPresetId == "custom",
                        icon = Icons.Rounded.Edit,
                        onClick = { showTargetDialog = true }
                    )
                }
            }
        }

        // Active Dhikr Display Card + Huge Interactive Counter Orb
        item {
            LiquidGlassCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                strong = true,
                contentPadding = PaddingValues(20.dp)
            ) {
                Text(
                    text = activeLabelAr,
                    fontFamily = AmiriFontFamily,
                    fontSize = 26.sp,
                    lineHeight = 42.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
                if (!isAr) {
                    Text(
                        text = activeLabelEn,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Interactive Luminous Tasbih Orb
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(236.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { viewModel.incrementTasbih() }
                            )
                            .testTag("tasbih_tap_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeW = 12.dp.toPx()
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        EmeraldPrimary.copy(alpha = 0.35f),
                                        EmeraldDeep.copy(alpha = 0.85f)
                                    )
                                ),
                                radius = size.minDimension / 2f - strokeW
                            )
                            drawCircle(
                                color = Color(0x28FFFFFF),
                                radius = size.minDimension / 2f - strokeW / 2f,
                                style = Stroke(width = strokeW)
                            )
                            drawArc(
                                brush = Brush.sweepGradient(listOf(GoldPrimary, GoldBright, GoldPrimary)),
                                startAngle = -90f,
                                sweepAngle = animatedSweep,
                                useCenter = false,
                                style = Stroke(width = strokeW, cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.testTag("tasbih_count_text")
                            )
                            Text(
                                text = L10n.pick(lang, "الهدف: $target", "Goal: $target"),
                                style = MaterialTheme.typography.labelLarge,
                                color = GoldBright
                            )
                            if (completedRounds > 0) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = L10n.pick(lang, "الدورات المكتملة: $completedRounds", "Rounds: $completedRounds"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xCCFFFFFF)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Quick Target & Reset Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(33, 99, 100).forEach { goal ->
                            LiquidGlassChip(
                                label = goal.toString(),
                                selected = target == goal,
                                onClick = { viewModel.updateTasbihTarget(goal) }
                            )
                        }
                        LiquidGlassChip(
                            label = L10n.pick(lang, "تخصيص", "Edit"),
                            selected = target !in listOf(33, 99, 100),
                            onClick = { showTargetDialog = true }
                        )
                    }

                    IconButton(
                        onClick = { if (count > 0) showResetConfirm = true },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("tasbih_reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.RestartAlt,
                            contentDescription = L10n.pick(lang, "تصفير العداد", "Reset Counter"),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Session History Section
        item {
            NoorSectionHeader(
                title = L10n.pick(lang, "سجل جلسات التسبيح", "Recent Sessions"),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (sessions.isEmpty()) {
            item {
                LiquidGlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.History, null, tint = noorColors.goldAccent)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = L10n.pick(lang, "ستظهر جلساتك المكتملة هنا تلقائياً عند بلوغ الهدف.", "Your completed sessions will automatically appear here."),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            val fmt = SimpleDateFormat("dd MMM · HH:mm", Locale.getDefault())
            items(sessions.take(15)) { s ->
                LiquidGlassCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAr) s.labelAr else s.labelEn,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = fmt.format(Date(s.timestampMillis)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${s.count} / ${s.target}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = noorColors.goldAccent
                        )
                    }
                }
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text(L10n.pick(lang, "تأكيد تصفير العداد", "Reset Counter?")) },
            text = {
                Text(
                    L10n.pick(
                        lang,
                        "سيتم حفظ عددك الحالي ($count) في السجل ثم إعادة العداد إلى الصفر.",
                        "Your current count ($count) will be saved to history and reset to zero."
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetTasbihCounter()
                        showResetConfirm = false
                    },
                    modifier = Modifier.testTag("tasbih_confirm_reset")
                ) {
                    Text(L10n.pick(lang, "تصفير", "Reset"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text(L10n.pick(lang, "إلغاء", "Cancel"))
                }
            }
        )
    }

    if (showTargetDialog) {
        var customText by remember { mutableStateOf(prefs.customTasbihText) }
        var customTargetText by remember { mutableStateOf(target.toString()) }
        AlertDialog(
            onDismissRequest = { showTargetDialog = false },
            title = { Text(L10n.pick(lang, "تخصيص الذكر والهدف", "Custom Dhikr & Target")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = customText,
                        onValueChange = { customText = it },
                        label = { Text(L10n.pick(lang, "نص الذكر", "Dhikr text")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customTargetText,
                        onValueChange = { customTargetText = it.filter { ch -> ch.isDigit() } },
                        label = { Text(L10n.pick(lang, "الهدف المطلوب", "Target count")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val parsed = customTargetText.toIntOrNull()?.coerceIn(1, 10000) ?: 33
                    viewModel.selectTasbihPreset("custom", parsed, customText.ifBlank { "سبحان الله" })
                    showTargetDialog = false
                }) {
                    Text(L10n.pick(lang, "حفظ", "Save"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTargetDialog = false }) {
                    Text(L10n.pick(lang, "إلغاء", "Cancel"))
                }
            }
        )
    }
}
