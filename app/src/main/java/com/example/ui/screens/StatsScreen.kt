package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.AppLanguage
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.NoorScreenHeader
import com.example.ui.components.NoorSectionHeader
import com.example.ui.i18n.L10n
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LocalNoorColors
import com.example.viewmodel.NoorViewModel

@Composable
fun StatsScreen(
    viewModel: NoorViewModel,
    onBack: () -> Unit
) {
    val lang = LocalAppLanguage.current
    val isAr = lang == AppLanguage.ARABIC
    val stats by viewModel.statsSummary.collectAsStateWithLifecycle()
    val noorColors = LocalNoorColors.current

    var chartPeriod by remember { mutableIntStateOf(0) } // 0=Weekly(7d), 1=Monthly(30d)

    val chartData = remember(stats.recentActivities, chartPeriod) {
        val count = if (chartPeriod == 0) 7 else 14
        val slice = stats.recentActivities.take(count).reversed()
        if (slice.isEmpty()) List(count) { 0 } else slice.map {
            it.prayersCompletedCount * 10 + it.adhkarCompleted * 5 + (it.tasbihCount / 5) + it.quranVersesRead
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("stats_screen"),
        contentPadding = PaddingValues(bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            NoorScreenHeader(
                title = L10n.pick(lang, "الإحصائيات والإنجازات", "Statistics & Milestones"),
                subtitle = L10n.pick(lang, "متابعة وردك اليومي بخصوصية تامة على جهازك", "Track your worship consistency privately on-device"),
                onBack = onBack
            )
        }

        // Streak Hero Card
        item {
            LiquidGlassCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                strong = true
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(noorColors.goldAccent.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocalFireDepartment,
                                contentDescription = null,
                                tint = noorColors.goldAccent,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = L10n.pick(lang, "أيام المحافظة على الورد", "Active Worship Days"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = L10n.pick(lang, "أحب الأعمال إلى الله أدومها وإن قلّ", "The most beloved deeds to Allah are consistent"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = "${stats.currentStreakDays}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = noorColors.goldAccent
                    )
                }
            }
        }

        // 2x2 Metrics Grid
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    StatMetricCard(
                        title = L10n.pick(lang, "إجمالي التسبيح", "Total Tasbih"),
                        value = stats.allTimeTasbih.toString(),
                        subtitle = L10n.pick(lang, "${stats.totalTasbihSessions} جلسة", "${stats.totalTasbihSessions} sessions"),
                        icon = Icons.Rounded.TouchApp,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        title = L10n.pick(lang, "الأذكار المكتملة", "Completed Adhkar"),
                        value = stats.totalCompletedAdhkar.toString(),
                        subtitle = L10n.pick(lang, "ذكر مكتمل", "items completed"),
                        icon = Icons.Rounded.AutoAwesome,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    StatMetricCard(
                        title = L10n.pick(lang, "قراءة القرآن", "Quran Reading"),
                        value = stats.totalQuranVersesRead.toString(),
                        subtitle = L10n.pick(lang, "سجل القراءة", "reading records"),
                        icon = Icons.Rounded.MenuBook,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        title = L10n.pick(lang, "الصلوات المسجلة", "Logged Prayers"),
                        value = stats.totalPrayersLogged.toString(),
                        subtitle = L10n.pick(lang, "صلاة مكتملة", "prayers logged"),
                        icon = Icons.Rounded.Mosque,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Weekly / Monthly Activity Canvas Chart
        item {
            LiquidGlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = L10n.pick(lang, "مؤشر النشاط التعبدي", "Worship Activity"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        LiquidGlassChip(
                            label = L10n.pick(lang, "أسبوعي", "Weekly"),
                            selected = chartPeriod == 0,
                            onClick = { chartPeriod = 0 }
                        )
                        LiquidGlassChip(
                            label = L10n.pick(lang, "شهري", "Monthly"),
                            selected = chartPeriod == 1,
                            onClick = { chartPeriod = 1 }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                val maxVal = (chartData.maxOrNull() ?: 1).coerceAtLeast(1)
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    val count = chartData.size.coerceAtLeast(1)
                    val gap = 8.dp.toPx()
                    val barW = ((size.width - gap * (count - 1)) / count).coerceAtLeast(6.dp.toPx())
                    chartData.forEachIndexed { i, value ->
                        val ratio = (value.toFloat() / maxVal.toFloat()).coerceIn(0.08f, 1f)
                        val barH = size.height * ratio
                        val x = i * (barW + gap)
                        val y = size.height - barH
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                listOf(if (value > 0) GoldBright else EmeraldBright.copy(alpha = 0.25f), EmeraldBright)
                            ),
                            topLeft = Offset(x, y),
                            size = Size(barW, barH),
                            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                        )
                    }
                }
            }
        }

        // Respectful, non-exaggerated Achievements
        item {
            NoorSectionHeader(
                title = L10n.pick(lang, "إنجازات الورد اليومي", "Consistency Milestones"),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        items(stats.achievements) { ach ->
            LiquidGlassCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (ach.unlocked) GoldPrimary.copy(alpha = 0.22f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (ach.unlocked) Icons.Rounded.CheckCircle else Icons.Rounded.WorkspacePremium,
                            contentDescription = null,
                            tint = if (ach.unlocked) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAr) ach.titleAr else ach.titleEn,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isAr) ach.subtitleAr else ach.subtitleEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { ach.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(50)),
                            color = if (ach.unlocked) GoldPrimary else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val noorColors = LocalNoorColors.current
    LiquidGlassCard(
        modifier = modifier,
        contentPadding = PaddingValues(14.dp)
    ) {
        Icon(icon, contentDescription = null, tint = noorColors.goldAccent, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
