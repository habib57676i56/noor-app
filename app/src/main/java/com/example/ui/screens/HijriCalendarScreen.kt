package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.calendar.HijriCalendarEngine
import com.example.domain.model.AppLanguage
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.NoorScreenHeader
import com.example.ui.components.NoorSectionHeader
import com.example.ui.i18n.L10n
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.theme.LocalNoorColors
import com.example.viewmodel.NoorViewModel
import java.util.Calendar

@Composable
fun HijriCalendarScreen(
    viewModel: NoorViewModel,
    onBack: () -> Unit
) {
    val lang = LocalAppLanguage.current
    val isAr = lang == AppLanguage.ARABIC
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val dashboard by viewModel.prayerDashboard.collectAsStateWithLifecycle()
    val noorColors = LocalNoorColors.current

    val initialCal = remember { Calendar.getInstance() }
    var displayYear by remember { mutableIntStateOf(initialCal.get(Calendar.YEAR)) }
    var displayMonth by remember { mutableIntStateOf(initialCal.get(Calendar.MONTH) + 1) } // 1..12

    val monthCells = remember(displayYear, displayMonth, prefs.hijriAdjustmentDays) {
        HijriCalendarEngine.buildMonthCells(displayYear, displayMonth, prefs.hijriAdjustmentDays)
    }
    val upcomingOccasions = remember(prefs.hijriAdjustmentDays) {
        HijriCalendarEngine.getUpcomingOccasions(prefs.hijriAdjustmentDays)
    }

    val firstCellDow = (monthCells.firstOrNull()?.dayOfWeek ?: Calendar.SUNDAY) - 1 // 0..6
    val monthHeaderTitle = if (isAr) {
        "${HijriCalendarEngine.gregorianMonthsAr[displayMonth - 1]} $displayYear"
    } else {
        "${HijriCalendarEngine.gregorianMonthsEn[displayMonth - 1]} $displayYear"
    }
    val hijriSpanTitle = remember(monthCells, isAr) {
        val first = monthCells.firstOrNull()?.hijri
        val last = monthCells.lastOrNull()?.hijri
        if (first == null || last == null) ""
        else if (first.month == last.month) {
            if (isAr) "${first.monthNameAr} ${first.year} هـ" else "${first.monthNameEn} ${first.year} AH"
        } else {
            if (isAr) "${first.monthNameAr} – ${last.monthNameAr} ${last.year} هـ"
            else "${first.monthNameEn} – ${last.monthNameEn} ${last.year} AH"
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("hijri_calendar_screen"),
        contentPadding = PaddingValues(bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            NoorScreenHeader(
                title = L10n.pick(lang, "التقويم الهجري", "Hijri Calendar"),
                subtitle = HijriCalendarEngine.formatHijri(dashboard.hijriToday, isAr),
                onBack = onBack
            )
        }

        // Manual Hijri Date Adjustment (-2 .. +2 days)
        item {
            LiquidGlassCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = L10n.pick(lang, "تعديل التاريخ الهجري يدوياً", "Hijri Date Adjustment"),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = L10n.pick(lang, "مطابقة رؤية الهلال في بلدك", "Match local crescent moon sighting"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.setHijriAdjustment(prefs.hijriAdjustmentDays - 1) }) {
                            Icon(Icons.Rounded.Remove, contentDescription = "Decrease day")
                        }
                        Text(
                            text = "${if (prefs.hijriAdjustmentDays > 0) "+" else ""}${prefs.hijriAdjustmentDays}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = noorColors.goldAccent
                        )
                        IconButton(onClick = { viewModel.setHijriAdjustment(prefs.hijriAdjustmentDays + 1) }) {
                            Icon(Icons.Rounded.Add, contentDescription = "Increase day")
                        }
                    }
                }
            }
        }

        // Monthly Dual Calendar Grid
        item {
            LiquidGlassCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                strong = true
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (displayMonth == 1) {
                            displayMonth = 12
                            displayYear -= 1
                        } else displayMonth -= 1
                    }) {
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, contentDescription = "Previous month")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = hijriSpanTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = noorColors.goldAccent
                        )
                        Text(
                            text = monthHeaderTitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = {
                        if (displayMonth == 12) {
                            displayMonth = 1
                            displayYear += 1
                        } else displayMonth += 1
                    }) {
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Next month")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Weekday headers
                val shortDays = if (isAr) listOf("أح", "إث", "ثل", "أر", "خم", "جم", "سب")
                else listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
                Row(modifier = Modifier.fillMaxWidth()) {
                    shortDays.forEach { d ->
                        Text(
                            text = d,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val totalSlots = firstCellDow + monthCells.size
                val rows = (totalSlots + 6) / 7
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (r in 0 until rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            for (c in 0 until 7) {
                                val idx = r * 7 + c - firstCellDow
                                if (idx !in monthCells.indices) {
                                    Spacer(modifier = Modifier.weight(1f))
                                } else {
                                    val cell = monthCells[idx]
                                    val bg = when {
                                        cell.isToday -> MaterialTheme.colorScheme.primary
                                        cell.isMonthStart -> noorColors.goldAccent.copy(alpha = 0.24f)
                                        cell.isWhiteDay -> MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                    }
                                    val fg = if (cell.isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(bg)
                                            .border(
                                                1.dp,
                                                if (cell.isSunnahFastDay && !cell.isToday) noorColors.goldAccent.copy(alpha = 0.5f)
                                                else androidx.compose.ui.graphics.Color.Transparent,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = cell.hijri.day.toString(),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = fg
                                        )
                                        Text(
                                            text = cell.gregorianDay.toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = fg.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LegendDot(MaterialTheme.colorScheme.primary, L10n.pick(lang, "اليوم", "Today"))
                    LegendDot(noorColors.goldAccent, L10n.pick(lang, "أيام الصيام / غرة الشهر", "Fasting / Month Start"))
                }
            }
        }

        // Recommended Fasting Days Info Card
        item {
            LiquidGlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = L10n.pick(lang, "الأيام المستحب صيامها", "Recommended Fasting Days"),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = noorColors.goldAccent
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = L10n.pick(
                        lang,
                        "• الأيام البيض (١٣، ١٤، ١٥) من كل شهر هجري.\n• يوما الإثنين والخميس من كل أسبوع.\n• يوم عاشوراء (١٠ محرم) ويوم عرفة (٩ ذو الحجة).",
                        "• The White Days (13th, 14th, 15th) of every Hijri month.\n• Mondays and Thursdays each week.\n• Day of Ashura (10 Muharram) and Day of Arafah (9 Dhu al-Hijjah)."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Upcoming Islamic Occasions
        item {
            NoorSectionHeader(
                title = L10n.pick(lang, "المناسبات الإسلامية القادمة", "Upcoming Islamic Occasions"),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        items(upcomingOccasions) { occ ->
            val hMonthName = if (isAr) HijriCalendarEngine.hijriMonthsAr[occ.hijriMonth - 1]
            else HijriCalendarEngine.hijriMonthsEn[occ.hijriMonth - 1]
            LiquidGlassCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Event, null, tint = noorColors.goldAccent)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isAr) occ.nameAr else occ.nameEn,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${occ.hijriDay} $hMonthName",
                                style = MaterialTheme.typography.labelMedium,
                                color = noorColors.goldAccent
                            )
                            Text(
                                text = if (isAr) occ.descriptionAr else occ.descriptionEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (occ.daysUntil == 0) L10n.pick(lang, "اليوم", "Today")
                        else L10n.pick(lang, "بعد ${occ.daysUntil} ي", "${occ.daysUntil}d"),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
