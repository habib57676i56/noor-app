package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.AppLanguage
import com.example.domain.model.AsrMadhhab
import com.example.domain.model.CalculationMethod
import com.example.domain.model.DailyPrayerSchedule
import com.example.domain.model.PrayerName
import com.example.domain.prayer.CityCatalog
import com.example.location.LocationHelper
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.NoorScreenHeader
import com.example.ui.components.prayerIcon
import com.example.ui.i18n.L10n
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.theme.LocalNoorColors
import com.example.viewmodel.NoorViewModel

@Composable
fun PrayerTimesScreen(
    viewModel: NoorViewModel,
    onOpenQibla: () -> Unit
) {
    val context = LocalContext.current
    val lang = LocalAppLanguage.current
    val isAr = lang == AppLanguage.ARABIC
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val dashboard by viewModel.prayerDashboard.collectAsStateWithLifecycle()
    val isDetecting by viewModel.isDetectingLocation.collectAsStateWithLifecycle()
    val noorColors = LocalNoorColors.current

    var selectedTab by remember { mutableIntStateOf(0) } // 0=Daily, 1=Weekly, 2=Monthly
    var showCityPicker by remember { mutableStateOf(false) }
    var showCalcDialog by remember { mutableStateOf(false) }
    var showOffsetsDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.detectLocationAutomatically()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("prayer_times_screen"),
        contentPadding = PaddingValues(bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            NoorScreenHeader(
                title = L10n.pick(lang, "مواقيت الصلاة", "Prayer Times"),
                subtitle = if (isAr) "${prefs.cityNameAr} · ${prefs.calculationMethod.nameAr}"
                else "${prefs.cityNameEn} · ${prefs.calculationMethod.nameEn}",
                trailing = {
                    IconButton(
                        onClick = onOpenQibla,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(noorColors.glassSurface)
                            .border(1.dp, noorColors.glassBorderBottom, CircleShape)
                            .testTag("prayer_qibla_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Explore,
                            contentDescription = L10n.pick(lang, "القبلة", "Qibla"),
                            tint = noorColors.goldAccent
                        )
                    }
                }
            )
        }

        // Location & Calculation Controls Bar
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LiquidGlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("detect_gps_button"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                        onClick = {
                            if (LocationHelper.hasLocationPermission(context)) {
                                viewModel.detectLocationAutomatically()
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            if (isDetecting) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Rounded.MyLocation, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = L10n.pick(lang, "موقعي تلقائياً", "Auto GPS"),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    LiquidGlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("choose_city_button"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                        onClick = { showCityPicker = true }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Rounded.LocationCity, null, tint = noorColors.goldAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = L10n.pick(lang, "اختيار المدينة", "Pick City"),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LiquidGlassChip(
                        label = L10n.pick(lang, "طريقة الحساب والمذهب", "Method & Madhhab"),
                        selected = false,
                        icon = Icons.Rounded.Tune,
                        onClick = { showCalcDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    LiquidGlassChip(
                        label = L10n.pick(lang, "تعديل الدقائق", "Offsets"),
                        selected = false,
                        onClick = { showOffsetsDialog = true }
                    )
                }

                // Period selector: Daily / Weekly / Monthly
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LiquidGlassChip(
                        label = L10n.pick(lang, "يومي", "Daily"),
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f).testTag("prayer_tab_daily")
                    )
                    LiquidGlassChip(
                        label = L10n.pick(lang, "أسبوعي", "Weekly"),
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f).testTag("prayer_tab_weekly")
                    )
                    LiquidGlassChip(
                        label = L10n.pick(lang, "شهري", "Monthly"),
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        modifier = Modifier.weight(1f).testTag("prayer_tab_monthly")
                    )
                }
            }
        }

        // Content by tab
        if (selectedTab == 0) {
            items(dashboard.todaySchedule.allOrdered()) { (prayer, millis) ->
                val isNext = prayer == dashboard.nextPrayerInfo.nextPrayer
                val notifyEnabled = prefs.isPrayerNotificationEnabled(prayer)
                LiquidGlassCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .testTag("prayer_row_${prayer.key}"),
                    strong = isNext,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isNext) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = prayerIcon(prayer),
                                    contentDescription = null,
                                    tint = if (isNext) MaterialTheme.colorScheme.onPrimary else noorColors.goldAccent
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = L10n.prayerName(prayer, lang),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isNext) noorColors.goldAccent else MaterialTheme.colorScheme.onSurface
                                )
                                if (isNext) {
                                    Text(
                                        text = L10n.pick(
                                            lang,
                                            "القادمة بعد ${L10n.formatCountdown(dashboard.nextPrayerInfo.remainingMillis, lang)}",
                                            "Next in ${L10n.formatCountdown(dashboard.nextPrayerInfo.remainingMillis, lang)}"
                                        ),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = L10n.formatTime12h(millis, prefs.timezoneId, lang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (prayer.isFard) {
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { viewModel.setPrayerNotification(prayer, !notifyEnabled) },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = if (notifyEnabled) Icons.Rounded.NotificationsActive else Icons.Rounded.NotificationsOff,
                                        contentDescription = L10n.pick(lang, "تبديل تنبيه الصلاة", "Toggle prayer alert"),
                                        tint = if (notifyEnabled) noorColors.goldAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            val list = if (selectedTab == 1) dashboard.weeklySchedules else dashboard.monthlySchedules
            items(list) { daySchedule ->
                ScheduleCompactCard(
                    schedule = daySchedule,
                    timeZoneId = prefs.timezoneId,
                    lang = lang
                )
            }
        }
    }

    if (showCityPicker) {
        CityPickerDialog(
            isAr = isAr,
            currentCityId = prefs.cityId,
            onSelect = {
                viewModel.selectCity(it)
                showCityPicker = false
            },
            onDismiss = { showCityPicker = false }
        )
    }

    if (showCalcDialog) {
        CalculationMethodDialog(
            isAr = isAr,
            currentMethod = prefs.calculationMethod,
            currentMadhhab = prefs.asrMadhhab,
            onSelectMethod = { viewModel.setCalculationMethod(it) },
            onSelectMadhhab = { viewModel.setAsrMadhhab(it) },
            onDismiss = { showCalcDialog = false }
        )
    }

    if (showOffsetsDialog) {
        PrayerOffsetsDialog(
            lang = lang,
            offsets = prefs.offsets,
            onChangeOffset = { prayer, value -> viewModel.setPrayerOffset(prayer, value) },
            onDismiss = { showOffsetsDialog = false }
        )
    }
}

@Composable
private fun ScheduleCompactCard(
    schedule: DailyPrayerSchedule,
    timeZoneId: String,
    lang: AppLanguage
) {
    LiquidGlassCard(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Text(
            text = schedule.dateKey,
            style = MaterialTheme.typography.labelLarge,
            color = LocalNoorColors.current.goldAccent,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(PrayerName.FAJR, PrayerName.DHUHR, PrayerName.ASR, PrayerName.MAGHRIB, PrayerName.ISHA).forEach { p ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = L10n.prayerName(p, lang),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = L10n.formatTime12h(schedule.timeFor(p), timeZoneId, lang).replace(" AM", "").replace(" PM", "").replace(" ص", "").replace(" م", ""),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun CityPickerDialog(
    isAr: Boolean,
    currentCityId: String,
    onSelect: (com.example.domain.model.CityLocation) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) CityCatalog.cities
        else CityCatalog.cities.filter {
            it.nameAr.contains(query, ignoreCase = true) ||
                it.nameEn.contains(query, ignoreCase = true) ||
                it.countryAr.contains(query, ignoreCase = true) ||
                it.countryEn.contains(query, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isAr) "اختر مدينتك" else "Select City") },
        text = {
            Column(modifier = Modifier.height(380.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text(if (isAr) "ابحث عن مدينة..." else "Search city...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(filtered) { city ->
                        val selected = city.id == currentCityId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (selected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .clickable { onSelect(city) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isAr) city.nameAr else city.nameEn,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isAr) city.countryAr else city.countryEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isAr) "إغلاق" else "Close")
            }
        }
    )
}

@Composable
private fun CalculationMethodDialog(
    isAr: Boolean,
    currentMethod: CalculationMethod,
    currentMadhhab: AsrMadhhab,
    onSelectMethod: (CalculationMethod) -> Unit,
    onSelectMadhhab: (AsrMadhhab) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isAr) "طريقة الحساب والمذهب" else "Calculation Method & Madhhab") },
        text = {
            LazyColumn(
                modifier = Modifier.height(400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = if (isAr) "المذهب الفقهي لصلاة العصر" else "Asr Juristic Madhhab",
                        style = MaterialTheme.typography.labelLarge,
                        color = LocalNoorColors.current.goldAccent
                    )
                }
                items(AsrMadhhab.entries) { m ->
                    val selected = m == currentMadhhab
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .clickable { onSelectMadhhab(m) }
                            .padding(12.dp)
                    ) {
                        Text(text = if (isAr) m.nameAr else m.nameEn, style = MaterialTheme.typography.bodyMedium, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isAr) "طريقة حساب المواقيت" else "Calculation Convention",
                        style = MaterialTheme.typography.labelLarge,
                        color = LocalNoorColors.current.goldAccent
                    )
                }
                items(CalculationMethod.entries) { method ->
                    val selected = method == currentMethod
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .clickable { onSelectMethod(method) }
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (isAr) method.nameAr else method.nameEn,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isAr) "تم" else "Done")
            }
        }
    )
}

@Composable
private fun PrayerOffsetsDialog(
    lang: AppLanguage,
    offsets: com.example.domain.model.PrayerMinuteOffsets,
    onChangeOffset: (PrayerName, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val rows = listOf(
        PrayerName.FAJR to offsets.fajr,
        PrayerName.SUNRISE to offsets.sunrise,
        PrayerName.DHUHR to offsets.dhuhr,
        PrayerName.ASR to offsets.asr,
        PrayerName.MAGHRIB to offsets.maghrib,
        PrayerName.ISHA to offsets.isha
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(L10n.pick(lang, "تعديل دقائق المواقيت", "Manual Minute Offsets")) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                rows.forEach { (prayer, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(L10n.prayerName(prayer, lang), style = MaterialTheme.typography.titleSmall)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onChangeOffset(prayer, value - 1) }) {
                                Icon(Icons.Rounded.Remove, contentDescription = "Decrease")
                            }
                            Text(
                                text = "${if (value > 0) "+" else ""}$value",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { onChangeOffset(prayer, value + 1) }) {
                                Icon(Icons.Rounded.Add, contentDescription = "Increase")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(L10n.pick(lang, "حفظ", "Done"))
            }
        }
    )
}
