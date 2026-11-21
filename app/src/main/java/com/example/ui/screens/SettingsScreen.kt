package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.AppLanguage
import com.example.domain.model.PrayerName
import com.example.domain.model.ThemeMode
import com.example.notifications.NoorNotificationHelper
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.NoorScreenHeader
import com.example.ui.components.NoorSectionHeader
import com.example.ui.i18n.L10n
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.theme.LocalNoorColors
import com.example.viewmodel.NoorViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: NoorViewModel,
    onBack: () -> Unit,
    onOpenPrayers: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lang = LocalAppLanguage.current
    val isAr = lang == AppLanguage.ARABIC
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val noorColors = LocalNoorColors.current

    var showResetConfirm by remember { mutableStateOf(false) }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.sendTestNotification()
    }

    // Zero-permission Storage Access Framework Backup Export
    val exportBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val json = viewModel.buildBackupJson()
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(json.toByteArray(Charsets.UTF_8))
                }
            }
        }
    }

    // Zero-permission Storage Access Framework Backup Restore
    val importBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val json = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                if (!json.isNullOrBlank()) {
                    viewModel.restoreBackupJson(json)
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            NoorScreenHeader(
                title = L10n.pick(lang, "الإعدادات", "Settings"),
                subtitle = L10n.pick(lang, "اللغة والمظهر والتنبيهات والخصوصية", "Language, appearance, alerts & local privacy"),
                onBack = onBack
            )
        }

        // 1. Language & Theme
        item {
            LiquidGlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = L10n.pick(lang, "اللغة (Language)", "Language (اللغة)"),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = noorColors.goldAccent
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    LiquidGlassChip(
                        label = "العربية",
                        selected = prefs.language == AppLanguage.ARABIC,
                        onClick = { viewModel.setLanguage(AppLanguage.ARABIC) },
                        modifier = Modifier.weight(1f).testTag("lang_arabic_btn")
                    )
                    LiquidGlassChip(
                        label = "English",
                        selected = prefs.language == AppLanguage.ENGLISH,
                        onClick = { viewModel.setLanguage(AppLanguage.ENGLISH) },
                        modifier = Modifier.weight(1f).testTag("lang_english_btn")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = L10n.pick(lang, "المظهر", "Appearance"),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = noorColors.goldAccent
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    LiquidGlassChip(
                        label = L10n.pick(lang, "داكن", "Dark"),
                        selected = prefs.themeMode == ThemeMode.DARK,
                        onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
                        modifier = Modifier.weight(1f)
                    )
                    LiquidGlassChip(
                        label = L10n.pick(lang, "فاتح", "Light"),
                        selected = prefs.themeMode == ThemeMode.LIGHT,
                        onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                        modifier = Modifier.weight(1f)
                    )
                    LiquidGlassChip(
                        label = L10n.pick(lang, "النظام", "System"),
                        selected = prefs.themeMode == ThemeMode.SYSTEM,
                        onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 2. Prayer Settings Quick Card
        item {
            LiquidGlassCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = onOpenPrayers
            ) {
                Text(
                    text = L10n.pick(lang, "إعدادات مواقيت الصلاة والموقع", "Prayer Times & Location"),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = noorColors.goldAccent
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isAr) "${prefs.cityNameAr} · ${prefs.calculationMethod.nameAr} · ${prefs.asrMadhhab.nameAr}"
                    else "${prefs.cityNameEn} · ${prefs.calculationMethod.nameEn} · ${prefs.asrMadhhab.nameEn}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 3. Notifications & Reminders
        item {
            NoorSectionHeader(
                title = L10n.pick(lang, "الإشعارات والتنبيهات", "Notifications & Reminders"),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            LiquidGlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = L10n.pick(lang, "التنبيه قبل الصلاة بـ", "Pre-Prayer Alert"),
                    style = MaterialTheme.typography.labelLarge,
                    color = noorColors.goldAccent
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(0, 5, 10, 15).forEach { mins ->
                        LiquidGlassChip(
                            label = if (mins == 0) L10n.pick(lang, "إيقاف", "Off") else "$mins ${L10n.pick(lang, "د", "m")}",
                            selected = prefs.prePrayerAlertMinutes == mins,
                            onClick = { viewModel.setPrePrayerAlertMinutes(mins) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                PrayerName.fardPrayers.forEach { prayer ->
                    SettingsToggleRow(
                        title = L10n.pick(lang, "تنبيه صلاة ${L10n.prayerName(prayer, lang)}", "${L10n.prayerName(prayer, lang)} Prayer Alert"),
                        checked = prefs.isPrayerNotificationEnabled(prayer),
                        onCheckedChange = { viewModel.setPrayerNotification(prayer, it) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                SettingsToggleRow(
                    title = L10n.pick(lang, "تذكير أذكار الصباح (٦:٣٠ ص)", "Morning Adhkar (6:30 AM)"),
                    checked = prefs.morningAdhkarReminderEnabled,
                    onCheckedChange = { viewModel.setReminder("morning", it) }
                )
                SettingsToggleRow(
                    title = L10n.pick(lang, "تذكير أذكار المساء (٥:٣٠ م)", "Evening Adhkar (5:30 PM)"),
                    checked = prefs.eveningAdhkarReminderEnabled,
                    onCheckedChange = { viewModel.setReminder("evening", it) }
                )
                SettingsToggleRow(
                    title = L10n.pick(lang, "تذكير ورد القرآن اليومي (٩:٠٠ م)", "Daily Quran Reminder (9:00 PM)"),
                    checked = prefs.quranReminderEnabled,
                    onCheckedChange = { viewModel.setReminder("quran", it) }
                )
                SettingsToggleRow(
                    title = L10n.pick(lang, "تذكير التسبيح (٣:٠٠ م)", "Tasbih Reminder (3:00 PM)"),
                    checked = prefs.tasbihReminderEnabled,
                    onCheckedChange = { viewModel.setReminder("tasbih", it) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            !NoorNotificationHelper.hasNotificationPermission(context)
                        ) {
                            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.sendTestNotification()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("test_notification_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Rounded.NotificationsActive, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(L10n.pick(lang, "إرسال تنبيه تجريبي", "Send Test Notification"))
                }
            }
        }

        // 4. Sound & Vibration
        item {
            LiquidGlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = L10n.pick(lang, "الصوت والاهتزاز", "Sound & Haptics"),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = noorColors.goldAccent
                )
                Spacer(modifier = Modifier.height(6.dp))
                SettingsToggleRow(
                    title = L10n.pick(lang, "اهتزاز المسبحة والأذكار", "Tasbih & Adhkar Vibration"),
                    checked = prefs.tasbihVibrationEnabled,
                    onCheckedChange = { viewModel.setTasbihVibration(it) }
                )
                SettingsToggleRow(
                    title = L10n.pick(lang, "صوت نقر المسبحة", "Tasbih Click Sound"),
                    checked = prefs.tasbihSoundEnabled,
                    onCheckedChange = { viewModel.setTasbihSound(it) }
                )
            }
        }

        // 5. Privacy, Backup, Restore & Reset
        item {
            LiquidGlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Security, null, tint = noorColors.goldAccent)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = L10n.pick(lang, "الخصوصية والنسخ الاحتياطي", "Privacy & Local Backup"),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = L10n.pick(
                        lang,
                        "تُحفظ جميع بياناتك وإحصائياتك محلياً على جهازك فقط. لا يتم إرسال أي بيانات شخصية أو موقعك إلى أي خادم خارجي.",
                        "All your records and statistics stay 100% local on your device. No personal data or location is sent to external servers."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { exportBackupLauncher.launch("noor_backup.json") },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Rounded.Backup, null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(L10n.pick(lang, "نسخ احتياطي", "Backup"))
                    }
                    OutlinedButton(
                        onClick = { importBackupLauncher.launch(arrayOf("application/json", "*/*")) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Rounded.Restore, null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(L10n.pick(lang, "استعادة", "Restore"))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showResetConfirm = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Rounded.DeleteForever, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(L10n.pick(lang, "إعادة ضبط التطبيق", "Reset Application"))
                }
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text(L10n.pick(lang, "تأكيد إعادة الضبط", "Confirm Full Reset")) },
            text = {
                Text(
                    L10n.pick(
                        lang,
                        "سيتم مسح جميع الجلسات والعلامات المرجعية والإعدادات والعودة إلى الإعدادات الافتراضية.",
                        "This will clear all local sessions, bookmarks, and preferences and restore defaults."
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetApplication()
                    showResetConfirm = false
                }) {
                    Text(L10n.pick(lang, "إعادة ضبط", "Reset"), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text(L10n.pick(lang, "إلغاء", "Cancel"))
                }
            }
        )
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
