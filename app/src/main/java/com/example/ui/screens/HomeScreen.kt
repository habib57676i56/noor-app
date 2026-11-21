package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.catalog.SurahCatalog
import com.example.domain.calendar.HijriCalendarEngine
import com.example.domain.model.AppLanguage
import com.example.domain.model.PrayerName
import com.example.domain.model.ThemeMode
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.NoorSectionHeader
import com.example.ui.components.prayerIcon
import com.example.ui.i18n.L10n
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LocalNoorColors
import com.example.viewmodel.NoorViewModel

@Composable
fun HomeScreen(
    viewModel: NoorViewModel,
    onOpenPrayers: () -> Unit,
    onOpenQibla: () -> Unit,
    onOpenAdhkar: () -> Unit,
    onOpenTasbih: () -> Unit,
    onOpenQuran: () -> Unit,
    onOpenSurah: (Int) -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val lang = LocalAppLanguage.current
    val isAr = lang == AppLanguage.ARABIC
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val dashboard by viewModel.prayerDashboard.collectAsStateWithLifecycle()
    val todayAct by viewModel.todayActivity.collectAsStateWithLifecycle()
    val noorColors = LocalNoorColors.current

    val nextInfo = dashboard.nextPrayerInfo
    val animatedProgress by animateFloatAsState(targetValue = nextInfo.progress, label = "prayer_progress")
    val completedPrayersCount = todayAct?.prayersCompletedCount ?: 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Luxury Top Header: Brand + Date + Theme/Settings quick buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = L10n.pick(lang, "نور", "NOOR"),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(noorColors.goldAccent.copy(alpha = 0.16f))
                                .clickable(onClick = onOpenPrayers)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .testTag("home_city_chip")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.LocationOn,
                                    contentDescription = null,
                                    tint = noorColors.goldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAr) prefs.cityNameAr else prefs.cityNameEn,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = noorColors.goldAccent,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = HijriCalendarEngine.formatHijri(dashboard.hijriToday, isAr),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(onClick = onOpenCalendar)
                    )
                    Text(
                        text = HijriCalendarEngine.formatGregorian(dashboard.gregorianCal, isAr),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = {
                            val nextTheme = if (noorColors.isDark) ThemeMode.LIGHT else ThemeMode.DARK
                            viewModel.setThemeMode(nextTheme)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(noorColors.glassSurface)
                            .border(1.dp, noorColors.glassBorderBottom, CircleShape)
                            .testTag("home_theme_toggle")
                    ) {
                        Icon(
                            imageVector = if (noorColors.isDark) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                            contentDescription = L10n.pick(lang, "تبديل المظهر", "Toggle Theme"),
                            tint = noorColors.goldAccent
                        )
                    }
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(noorColors.glassSurface)
                            .border(1.dp, noorColors.glassBorderBottom, CircleShape)
                            .testTag("home_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = L10n.pick(lang, "الإعدادات", "Settings"),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 2. HERO CARD: Next Prayer + Live Countdown + Progress Bar (Visually Prominent)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .border(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(listOf(GoldBright.copy(alpha = 0.65f), Color(0x33FFFFFF))),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .clickable(onClick = onOpenPrayers)
                    .testTag("hero_next_prayer_card")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_noor_hero_bg_1795289370813),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    EmeraldDeep.copy(alpha = 0.62f),
                                    Color(0xD8051510)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0x33FFFFFF))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = prayerIcon(nextInfo.nextPrayer),
                                contentDescription = null,
                                tint = GoldBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = L10n.pick(lang, "الصلاة القادمة", "NEXT PRAYER"),
                                style = MaterialTheme.typography.labelMedium,
                                color = GoldBright
                            )
                        }
                        Text(
                            text = L10n.formatTime12h(nextInfo.nextPrayerMillis, prefs.timezoneId, lang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = L10n.prayerName(nextInfo.nextPrayer, lang),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = L10n.formatCountdown(nextInfo.remainingMillis, lang),
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 38.sp),
                            fontWeight = FontWeight.Bold,
                            color = GoldBright,
                            modifier = Modifier.testTag("hero_countdown_text")
                        )
                        Text(
                            text = L10n.pick(lang, "متبقي", "remaining"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xCCFFFFFF),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { animatedProgress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(50)),
                        color = GoldPrimary,
                        trackColor = Color(0x38FFFFFF)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = L10n.pick(
                                lang,
                                "إنجاز صلوات اليوم: $completedPrayersCount / 5",
                                "Today's Prayers: $completedPrayersCount / 5"
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xDDFFFFFF)
                        )
                        Text(
                            text = L10n.pick(lang, "عرض الجدول", "Full Schedule"),
                            style = MaterialTheme.typography.labelMedium,
                            color = GoldBright,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 3. Today's Prayer Strip + Tap to mark prayer completed
        item {
            LiquidGlassCard(contentPadding = PaddingValues(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = L10n.pick(lang, "مواقيت اليوم (اضغط لتسجيل الإتمام)", "Today's Prayers (Tap to log)"),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$completedPrayersCount/5",
                        style = MaterialTheme.typography.labelLarge,
                        color = noorColors.goldAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(dashboard.todaySchedule.allOrdered()) { (prayer, millis) ->
                        val isNext = prayer == nextInfo.nextPrayer
                        val isDone = when (prayer) {
                            PrayerName.FAJR -> todayAct?.fajrPrayed == true
                            PrayerName.DHUHR -> todayAct?.dhuhrPrayed == true
                            PrayerName.ASR -> todayAct?.asrPrayed == true
                            PrayerName.MAGHRIB -> todayAct?.maghribPrayed == true
                            PrayerName.ISHA -> todayAct?.ishaPrayed == true
                            PrayerName.SUNRISE -> false
                        }
                        val pillBg = when {
                            isNext -> MaterialTheme.colorScheme.primary
                            isDone -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        }
                        val textColor = if (isNext) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

                        Column(
                            modifier = Modifier
                                .width(88.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(pillBg)
                                .border(
                                    1.dp,
                                    if (isNext) GoldPrimary else Color.Transparent,
                                    RoundedCornerShape(18.dp)
                                )
                                .clickable(enabled = prayer.isFard) { viewModel.togglePrayerLogged(prayer) }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (prayer.isFard && isDone) Icons.Rounded.CheckCircle
                                else if (prayer.isFard) prayerIcon(prayer)
                                else prayerIcon(prayer),
                                contentDescription = null,
                                tint = if (isNext) GoldBright else if (isDone) MaterialTheme.colorScheme.primary else noorColors.goldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = L10n.prayerName(prayer, lang),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Text(
                                text = L10n.formatTime12h(millis, prefs.timezoneId, lang),
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor.copy(alpha = 0.85f)
                            )
                            if (prayer.isFard) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(
                                    imageVector = if (isDone) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isNext) GoldBright else if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Quick Shortcuts Grid (Qibla, Adhkar, Tasbih, Quran, Calendar, Stats)
        item {
            NoorSectionHeader(title = L10n.pick(lang, "الوصول السريع", "Quick Access"))
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickShortcutTile(
                        title = L10n.pick(lang, "القبلة", "Qibla"),
                        icon = Icons.Rounded.Explore,
                        modifier = Modifier.weight(1f).testTag("shortcut_qibla"),
                        onClick = onOpenQibla
                    )
                    QuickShortcutTile(
                        title = L10n.pick(lang, "الأذكار", "Adhkar"),
                        icon = Icons.Rounded.AutoAwesome,
                        modifier = Modifier.weight(1f).testTag("shortcut_adhkar"),
                        onClick = onOpenAdhkar
                    )
                    QuickShortcutTile(
                        title = L10n.pick(lang, "المسبحة", "Tasbih"),
                        icon = Icons.Rounded.TouchApp,
                        modifier = Modifier.weight(1f).testTag("shortcut_tasbih"),
                        onClick = onOpenTasbih
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickShortcutTile(
                        title = L10n.pick(lang, "القرآن", "Quran"),
                        icon = Icons.Rounded.MenuBook,
                        modifier = Modifier.weight(1f).testTag("shortcut_quran"),
                        onClick = onOpenQuran
                    )
                    QuickShortcutTile(
                        title = L10n.pick(lang, "التقويم", "Calendar"),
                        icon = Icons.Rounded.CalendarMonth,
                        modifier = Modifier.weight(1f).testTag("shortcut_calendar"),
                        onClick = onOpenCalendar
                    )
                    QuickShortcutTile(
                        title = L10n.pick(lang, "الإحصائيات", "Stats"),
                        icon = Icons.Rounded.BarChart,
                        modifier = Modifier.weight(1f).testTag("shortcut_stats"),
                        onClick = onOpenStats
                    )
                }
            }
        }

        // 5. Resume Last Quran Reading Banner
        item {
            val lastSurah = SurahCatalog.findSurah(prefs.lastReadSurah)
            LiquidGlassCard(
                onClick = { onOpenSurah(lastSurah.number) },
                modifier = Modifier.testTag("home_resume_quran")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = L10n.pick(lang, "متابعة القراءة", "Continue Reading"),
                                style = MaterialTheme.typography.labelMedium,
                                color = noorColors.goldAccent
                            )
                            Text(
                                text = if (isAr) "سورة ${lastSurah.nameAr} · آية ${prefs.lastReadAyah}"
                                else "Surah ${lastSurah.nameEn} · Ayah ${prefs.lastReadAyah}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Text(
                        text = L10n.pick(lang, "فتح", "Open"),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 6. Ayah of the Day
        item {
            val ayah = dashboard.dailyAyah
            LiquidGlassCard(onClick = { onOpenSurah(ayah.surahNumber) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = L10n.pick(lang, "آية اليوم", "Ayah of the Day"),
                        style = MaterialTheme.typography.labelLarge,
                        color = noorColors.goldAccent,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAr) "سورة ${ayah.surahNameAr} : ${ayah.ayahNumber}"
                        else "${ayah.surahNameEn} ${ayah.surahNumber}:${ayah.ayahNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "﴿ ${ayah.textAr} ﴾",
                    fontFamily = AmiriFontFamily,
                    fontSize = 22.sp,
                    lineHeight = 38.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
                if (!isAr) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = ayah.meaningEn,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 7. Dhikr of the Day
        item {
            val dhikr = dashboard.dailyDhikr
            LiquidGlassCard(onClick = onOpenAdhkar) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = L10n.pick(lang, "ذكر اليوم", "Dhikr of the Day"),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAr) dhikr.sourceAr else dhikr.sourceEn,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = dhikr.textAr,
                    fontFamily = AmiriFontFamily,
                    fontSize = 19.sp,
                    lineHeight = 34.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isAr) dhikr.meaningAr else dhikr.meaningEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun QuickShortcutTile(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val noorColors = LocalNoorColors.current
    LiquidGlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 8.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = noorColors.goldAccent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
