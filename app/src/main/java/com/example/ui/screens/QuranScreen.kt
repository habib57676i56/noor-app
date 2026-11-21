package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.catalog.SurahCatalog
import com.example.domain.model.AppLanguage
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.NoorScreenHeader
import com.example.ui.i18n.L10n
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.LocalNoorColors
import com.example.viewmodel.NoorViewModel

@Composable
fun QuranScreen(
    viewModel: NoorViewModel,
    onOpenSurah: (Int) -> Unit
) {
    val lang = LocalAppLanguage.current
    val isAr = lang == AppLanguage.ARABIC
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val bookmarks by viewModel.quranBookmarks.collectAsStateWithLifecycle()
    val verseMatches by viewModel.verseSearchResults.collectAsStateWithLifecycle()
    val noorColors = LocalNoorColors.current

    var selectedTab by remember { mutableIntStateOf(0) } // 0=Surahs, 1=Juz/Hizb, 2=Bookmarks
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        viewModel.searchQuranVerses(searchQuery)
    }

    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) SurahCatalog.allSurahs
        else SurahCatalog.allSurahs.filter { s ->
            s.nameAr.contains(searchQuery, true) ||
                s.nameEn.contains(searchQuery, true) ||
                s.meaningEn.contains(searchQuery, true) ||
                s.number.toString() == searchQuery.trim()
        }
    }

    val lastSurah = SurahCatalog.findSurah(prefs.lastReadSurah)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("quran_screen"),
        contentPadding = PaddingValues(bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            NoorScreenHeader(
                title = L10n.pick(lang, "القرآن الكريم", "Holy Quran"),
                subtitle = L10n.pick(lang, "١١٤ سورة · الرسم العثماني والتفسير الميسر", "114 Surahs · Uthmani Script & Al-Muyassar Tafsir")
            )
        }

        // Last Read Card
        item {
            LiquidGlassCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .testTag("quran_last_read_card"),
                strong = true,
                onClick = { onOpenSurah(lastSurah.number) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.MenuBook, null, tint = noorColors.goldAccent)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = L10n.pick(lang, "آخر موضع قراءة", "Last Read Position"),
                                style = MaterialTheme.typography.labelMedium,
                                color = noorColors.goldAccent
                            )
                            Text(
                                text = if (isAr) "سورة ${lastSurah.nameAr}" else "Surah ${lastSurah.nameEn}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = L10n.pick(lang, "الآية ${prefs.lastReadAyah} · الجزء ${lastSurah.startJuz}", "Ayah ${prefs.lastReadAyah} · Juz ${lastSurah.startJuz}"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = L10n.pick(lang, "متابعة", "Resume"),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Search Bar & Index Tabs
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(L10n.pick(lang, "ابحث عن سورة أو آية...", "Search Surah or Ayah...")) },
                    leadingIcon = { Icon(Icons.Rounded.Search, null) },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quran_search_input")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LiquidGlassChip(
                        label = L10n.pick(lang, "السور (١١٤)", "Surahs (114)"),
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    LiquidGlassChip(
                        label = L10n.pick(lang, "الأجزاء", "Juz & Hizb"),
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                    LiquidGlassChip(
                        label = L10n.pick(lang, "العلامات (${bookmarks.size})", "Saved (${bookmarks.size})"),
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Matching Cached Verses when searching
        if (searchQuery.trim().length >= 2 && verseMatches.isNotEmpty()) {
            item {
                Text(
                    text = L10n.pick(lang, "نتائج الآيات", "Matching Verses"),
                    style = MaterialTheme.typography.labelLarge,
                    color = noorColors.goldAccent,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            items(verseMatches.take(8)) { v ->
                val surah = SurahCatalog.findSurah(v.surahNumber)
                LiquidGlassCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { onOpenSurah(v.surahNumber) }
                ) {
                    Text(
                        text = if (isAr) "سورة ${surah.nameAr} · آية ${v.numberInSurah}" else "${surah.nameEn} ${v.surahNumber}:${v.numberInSurah}",
                        style = MaterialTheme.typography.labelMedium,
                        color = noorColors.goldAccent
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = v.textUthmani,
                        fontFamily = AmiriFontFamily,
                        fontSize = 18.sp,
                        lineHeight = 32.sp
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                items(filteredSurahs, key = { it.number }) { surah ->
                    LiquidGlassCard(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .testTag("surah_item_${surah.number}"),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        onClick = { onOpenSurah(surah.number) }
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
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f))
                                        .border(1.dp, noorColors.goldAccent.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = surah.number.toString(),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = noorColors.goldAccent
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isAr) "سورة ${surah.nameAr}" else surah.nameEn,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val typeLabel = if (surah.isMeccan) {
                                        L10n.pick(lang, "مكية", "Meccan")
                                    } else {
                                        L10n.pick(lang, "مدنية", "Medinan")
                                    }
                                    Text(
                                        text = L10n.pick(
                                            lang,
                                            "$typeLabel · ${surah.verseCount} آية · الجزء ${surah.startJuz}",
                                            "$typeLabel · ${surah.verseCount} Ayahs · Juz ${surah.startJuz}"
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = surah.nameAr,
                                fontFamily = AmiriFontFamily,
                                fontSize = 22.sp,
                                color = noorColors.goldAccent
                            )
                        }
                    }
                }
            }
            1 -> {
                items((1..30).toList()) { juz ->
                    val surahsInJuz = SurahCatalog.allSurahs.filter { it.startJuz == juz }
                    val firstSurah = surahsInJuz.firstOrNull()
                        ?: SurahCatalog.allSurahs.lastOrNull { it.startJuz <= juz }
                        ?: SurahCatalog.allSurahs.first()
                    val hizbStart = (juz - 1) * 2 + 1
                    val hizbEnd = hizbStart + 1

                    LiquidGlassCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = { onOpenSurah(firstSurah.number) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = L10n.pick(lang, "الجزء $juz", "Juz $juz"),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = noorColors.goldAccent
                                )
                                Text(
                                    text = L10n.pick(
                                        lang,
                                        "الحزب $hizbStart - $hizbEnd · يبدأ من سورة ${firstSurah.nameAr}",
                                        "Hizb $hizbStart–$hizbEnd · Starts at ${firstSurah.nameEn}"
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = L10n.pick(lang, "قراءة", "Read"),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            else -> {
                if (bookmarks.isEmpty()) {
                    item {
                        LiquidGlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Bookmark, null, tint = noorColors.goldAccent)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = L10n.pick(lang, "لا توجد علامات مرجعية بعد. اضغط على أيقونة العلامة بجانب أي آية أثناء القراءة لحفظها.", "No bookmarks saved yet. Tap the bookmark icon next to any Ayah while reading."),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(bookmarks) { bm ->
                        LiquidGlassCard(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onClick = { onOpenSurah(bm.surahNumber) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isAr) "سورة ${bm.surahNameAr} · الآية ${bm.ayahNumber}"
                                        else "${bm.surahNameEn} · Ayah ${bm.ayahNumber}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = noorColors.goldAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = bm.ayahPreview,
                                        fontFamily = AmiriFontFamily,
                                        fontSize = 18.sp,
                                        maxLines = 2
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.toggleBookmark(
                                            bm.surahNumber,
                                            bm.ayahNumber,
                                            bm.ayahPreview
                                        )
                                    }
                                ) {
                                    Icon(Icons.Rounded.DeleteOutline, contentDescription = "Remove bookmark")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
