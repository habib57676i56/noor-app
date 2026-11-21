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
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.catalog.SurahCatalog
import com.example.domain.model.AppLanguage
import com.example.domain.model.QuranReadingTheme
import com.example.domain.model.QuranReciter
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.NoorScreenHeader
import com.example.ui.i18n.L10n
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.GoldMuted
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LocalNoorColors
import com.example.ui.theme.MushafNightBg
import com.example.ui.theme.MushafNightInk
import com.example.ui.theme.MushafSepiaBg
import com.example.ui.theme.MushafSepiaInk
import com.example.viewmodel.NoorViewModel
import com.example.viewmodel.SurahReaderState

@Composable
fun QuranReaderScreen(
    surahNumber: Int,
    viewModel: NoorViewModel,
    onBack: () -> Unit
) {
    val lang = LocalAppLanguage.current
    val isAr = lang == AppLanguage.ARABIC
    val surah = SurahCatalog.findSurah(surahNumber)
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val readerState by viewModel.surahReaderState.collectAsStateWithLifecycle()
    val bookmarks by viewModel.quranBookmarks.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()

    var showReaderControls by remember { mutableStateOf(false) }
    var expandedTafsirAyah by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(surahNumber) {
        viewModel.loadSurah(surahNumber)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopQuranAudio() }
    }

    val (surfaceBg, inkColor, accentGold) = when (prefs.quranReadingTheme) {
        QuranReadingTheme.SEPIA -> Triple(MushafSepiaBg, MushafSepiaInk, GoldMuted)
        QuranReadingTheme.NIGHT -> Triple(MushafNightBg, MushafNightInk, GoldPrimary)
        QuranReadingTheme.LIGHT -> Triple(Color(0xFFFAFBF9), Color(0xFF101F19), GoldMuted)
        QuranReadingTheme.AUTO -> Triple(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.onBackground,
            LocalNoorColors.current.goldAccent
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceBg)
            .testTag("quran_reader_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NoorScreenHeader(
                title = if (isAr) "سورة ${surah.nameAr}" else surah.nameEn,
                subtitle = L10n.pick(
                    lang,
                    "${surah.verseCount} آية · الجزء ${surah.startJuz} · ${prefs.quranReciter.nameAr}",
                    "${surah.verseCount} Ayahs · Juz ${surah.startJuz} · ${prefs.quranReciter.nameEn}"
                ),
                onBack = onBack,
                trailing = {
                    IconButton(
                        onClick = { showReaderControls = true },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(LocalNoorColors.current.glassSurface)
                            .border(1.dp, LocalNoorColors.current.glassBorderBottom, CircleShape)
                            .testTag("quran_reader_controls_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FormatSize,
                            contentDescription = L10n.pick(lang, "إعدادات القراءة", "Reading Settings"),
                            tint = accentGold
                        )
                    }
                }
            )

            // Active Recitation Player Bar
            if (audioState.playingSurah != null || audioState.isBuffering || audioState.errorMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (audioState.errorMessage != null) {
                                L10n.pick(lang, "تتطلب التلاوة الصوتية اتصالاً بالإنترنت", "Audio recitation requires internet connection")
                            } else if (audioState.isBuffering) {
                                L10n.pick(lang, "جارٍ تحميل التلاوة...", "Loading recitation...")
                            } else {
                                L10n.pick(
                                    lang,
                                    "تلاوة الآية ${audioState.playingAyah} · ${prefs.quranReciter.nameAr}",
                                    "Reciting Ayah ${audioState.playingAyah} · ${prefs.quranReciter.nameEn}"
                                )
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Row {
                        if (audioState.playingSurah != null) {
                            IconButton(onClick = { viewModel.toggleAudioPauseResume() }) {
                                Icon(
                                    imageVector = if (audioState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.stopQuranAudio() }) {
                            Icon(
                                imageVector = Icons.Rounded.Stop,
                                contentDescription = "Stop",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            when (val st = readerState) {
                is SurahReaderState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = accentGold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = L10n.pick(lang, "جارٍ تحميل السورة بالرسم العثماني...", "Loading Uthmani Surah..."),
                                color = inkColor
                            )
                        }
                    }
                }

                is SurahReaderState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LiquidGlassCard {
                            Text(
                                text = if (isAr) st.messageAr else st.messageEn,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { viewModel.loadSurah(surahNumber) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Rounded.Refresh, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(L10n.pick(lang, "إعادة المحاولة", "Retry"))
                            }
                        }
                    }
                }

                is SurahReaderState.Ready -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 104.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Bismillah Banner (for all Surahs except At-Tawbah 9 and Al-Fatihah 1 where it is Ayah 1)
                        if (surahNumber != 1 && surahNumber != 9) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(20.dp))
                                        .border(1.dp, accentGold.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                        .padding(vertical = 14.dp, horizontal = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                        fontFamily = AmiriFontFamily,
                                        fontSize = (prefs.quranFontSizeSp + 2).sp,
                                        color = accentGold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        if (st.isOfflineCached) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Rounded.CloudDone, null, tint = accentGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = L10n.pick(lang, "محفوظة للقراءة دون إنترنت · التفسير الميسر", "Saved for offline reading · Tafsir Al-Muyassar"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = inkColor.copy(alpha = 0.65f)
                                    )
                                }
                            }
                        }

                        items(st.verses, key = { it.numberInSurah }) { verse ->
                            val isBookmarked = bookmarks.any {
                                it.surahNumber == verse.surahNumber && it.ayahNumber == verse.numberInSurah
                            }
                            val isPlayingThis = audioState.playingSurah == verse.surahNumber &&
                                audioState.playingAyah == verse.numberInSurah
                            val tafsirExpanded = expandedTafsirAyah == verse.numberInSurah

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isPlayingThis) accentGold.copy(alpha = 0.14f)
                                        else inkColor.copy(alpha = 0.04f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isPlayingThis) accentGold else inkColor.copy(alpha = 0.1f),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(accentGold.copy(alpha = 0.16f))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${verse.numberInSurah}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = accentGold
                                        )
                                    }

                                    Row {
                                        IconButton(
                                            onClick = { viewModel.playQuranVerse(verse, st.verses) },
                                            modifier = Modifier.size(44.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.PlayArrow,
                                                contentDescription = L10n.pick(lang, "استماع", "Listen"),
                                                tint = accentGold
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                expandedTafsirAyah = if (tafsirExpanded) null else verse.numberInSurah
                                            },
                                            modifier = Modifier.size(44.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Info,
                                                contentDescription = L10n.pick(lang, "التفسير الميسر", "Tafsir"),
                                                tint = if (tafsirExpanded) MaterialTheme.colorScheme.primary else inkColor.copy(alpha = 0.7f)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                viewModel.toggleBookmark(
                                                    verse.surahNumber,
                                                    verse.numberInSurah,
                                                    verse.textUthmani
                                                )
                                                viewModel.saveLastRead(verse.surahNumber, verse.numberInSurah)
                                            },
                                            modifier = Modifier.size(44.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                                contentDescription = L10n.pick(lang, "حفظ علامة", "Bookmark"),
                                                tint = if (isBookmarked) accentGold else inkColor.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "${verse.textUthmani} ﴿${verse.numberInSurah}﴾",
                                    fontFamily = AmiriFontFamily,
                                    fontSize = prefs.quranFontSizeSp.sp,
                                    lineHeight = (prefs.quranFontSizeSp * 1.85f).sp,
                                    textAlign = TextAlign.End,
                                    color = inkColor,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (tafsirExpanded && verse.tafsirAr.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(accentGold.copy(alpha = 0.1f))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = L10n.pick(lang, "التفسير الميسر", "Al-Muyassar Tafsir"),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = accentGold,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = verse.tafsirAr,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = inkColor.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showReaderControls) {
        AlertDialog(
            onDismissRequest = { showReaderControls = false },
            title = { Text(L10n.pick(lang, "إعدادات القراءة والتلاوة", "Reading & Audio Settings")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = L10n.pick(lang, "حجم الخط: ${prefs.quranFontSizeSp}", "Font Size: ${prefs.quranFontSizeSp}sp"),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        value = prefs.quranFontSizeSp.toFloat(),
                        onValueChange = { viewModel.setQuranFontSize(it.toInt()) },
                        valueRange = 18f..36f
                    )

                    Text(
                        text = L10n.pick(lang, "مظهر المصحف", "Reading Surface"),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            QuranReadingTheme.AUTO to L10n.pick(lang, "تلقائي", "Auto"),
                            QuranReadingTheme.SEPIA to L10n.pick(lang, "ورقي", "Sepia"),
                            QuranReadingTheme.NIGHT to L10n.pick(lang, "ليلي", "Night")
                        ).forEach { (mode, label) ->
                            LiquidGlassChip(
                                label = label,
                                selected = prefs.quranReadingTheme == mode,
                                onClick = { viewModel.setQuranReadingTheme(mode) }
                            )
                        }
                    }

                    Text(
                        text = L10n.pick(lang, "القارئ", "Reciter"),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        QuranReciter.entries.forEach { reciter ->
                            LiquidGlassChip(
                                label = if (isAr) reciter.nameAr else reciter.nameEn,
                                selected = prefs.quranReciter == reciter,
                                icon = Icons.Rounded.RecordVoiceOver,
                                onClick = { viewModel.setQuranReciter(reciter) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReaderControls = false }) {
                    Text(L10n.pick(lang, "تم", "Done"))
                }
            }
        )
    }
}
