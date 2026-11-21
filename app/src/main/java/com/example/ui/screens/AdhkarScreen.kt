package com.example.ui.screens

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
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.DoorFront
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.catalog.AdhkarCatalog
import com.example.domain.model.AdhkarCategory
import com.example.domain.model.AppLanguage
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidGlassChip
import com.example.ui.components.NoorScreenHeader
import com.example.ui.i18n.L10n
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.theme.LocalNoorColors
import com.example.viewmodel.NoorViewModel

@Composable
fun AdhkarScreen(
    viewModel: NoorViewModel,
    onOpenCategory: (String) -> Unit
) {
    val lang = LocalAppLanguage.current
    val isAr = lang == AppLanguage.ARABIC
    val progressMap by viewModel.adhkarProgressMap.collectAsStateWithLifecycle()
    val favorites by viewModel.favoriteDhikrIds.collectAsStateWithLifecycle()
    val noorColors = LocalNoorColors.current

    var showFavoritesOnly by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val totalAdhkar = AdhkarCatalog.allAdhkar.size
    val completedToday = progressMap.values.count { it.isCompleted }
    val completionRatio = if (totalAdhkar > 0) completedToday.toFloat() / totalAdhkar else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("adhkar_screen"),
        contentPadding = PaddingValues(bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            NoorScreenHeader(
                title = L10n.pick(lang, "الأذكار والأدعية", "Adhkar & Duas"),
                subtitle = L10n.pick(lang, "حصن المسلم اليومي بتشكيل صحيح ومصادر موثقة", "Authentic daily remembrances & Prophetic supplications"),
                trailing = {
                    IconButton(onClick = { viewModel.resetTodayAdhkar() }) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = L10n.pick(lang, "إعادة تعيين ورد اليوم", "Reset today's progress"),
                            tint = noorColors.goldAccent
                        )
                    }
                }
            )
        }

        // Daily progress summary card
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
                    Text(
                        text = L10n.pick(lang, "إنجاز الأذكار اليومي", "Daily Adhkar Progress"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$completedToday / $totalAdhkar",
                        style = MaterialTheme.typography.titleMedium,
                        color = noorColors.goldAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { completionRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50)),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Filter chips + Search
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LiquidGlassChip(
                        label = L10n.pick(lang, "جميع الأقسام (٩)", "All Categories (9)"),
                        selected = !showFavoritesOnly,
                        onClick = { showFavoritesOnly = false }
                    )
                    LiquidGlassChip(
                        label = L10n.pick(lang, "المفضلة (${favorites.size})", "Favorites (${favorites.size})"),
                        selected = showFavoritesOnly,
                        icon = Icons.Rounded.Favorite,
                        onClick = { showFavoritesOnly = true }
                    )
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(L10n.pick(lang, "ابحث في الأذكار...", "Search Adhkar...")) },
                    leadingIcon = { Icon(Icons.Rounded.Search, null) },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (showFavoritesOnly) {
            val favItems = AdhkarCatalog.allAdhkar.filter {
                favorites.contains(it.id) && (searchQuery.isBlank() || it.textAr.contains(searchQuery, true) || it.meaningAr.contains(searchQuery, true))
            }
            if (favItems.isEmpty()) {
                item {
                    LiquidGlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = L10n.pick(lang, "لم تحفظ أي أذكار في المفضلة بعد. اضغط على أيقونة القلب بجانب أي ذكر لحفظه.", "No favorite Adhkar saved yet. Tap the heart icon on any Dhikr to save it."),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(favItems) { dhikr ->
                    LiquidGlassCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = { onOpenCategory(dhikr.category.id) }
                    ) {
                        Text(
                            text = if (isAr) dhikr.category.nameAr else dhikr.category.nameEn,
                            style = MaterialTheme.typography.labelMedium,
                            color = noorColors.goldAccent
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = dhikr.textAr,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 3
                        )
                    }
                }
            }
        } else if (searchQuery.isNotBlank()) {
            val matched = AdhkarCatalog.allAdhkar.filter {
                it.textAr.contains(searchQuery, true) ||
                    it.meaningAr.contains(searchQuery, true) ||
                    it.meaningEn.contains(searchQuery, true) ||
                    it.category.nameAr.contains(searchQuery, true) ||
                    it.category.nameEn.contains(searchQuery, true)
            }
            items(matched) { dhikr ->
                LiquidGlassCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { onOpenCategory(dhikr.category.id) }
                ) {
                    Text(
                        text = if (isAr) dhikr.category.nameAr else dhikr.category.nameEn,
                        style = MaterialTheme.typography.labelMedium,
                        color = noorColors.goldAccent
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = dhikr.textAr, style = MaterialTheme.typography.bodyLarge, maxLines = 3)
                }
            }
        } else {
            items(AdhkarCategory.entries) { category ->
                val itemsInCat = AdhkarCatalog.byCategory(category)
                val doneInCat = itemsInCat.count { progressMap[it.id]?.isCompleted == true }
                LiquidGlassCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .testTag("adhkar_cat_${category.id}"),
                    onClick = { onOpenCategory(category.id) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = categoryIcon(category),
                                    contentDescription = null,
                                    tint = noorColors.goldAccent
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = if (isAr) category.nameAr else category.nameEn,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = L10n.pick(
                                        lang,
                                        "${itemsInCat.size} أذكار · مكتمل $doneInCat",
                                        "${itemsInCat.size} items · $doneInCat completed"
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = if (doneInCat == itemsInCat.size && itemsInCat.isNotEmpty()) "100%" else "$doneInCat/${itemsInCat.size}",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (doneInCat == itemsInCat.size) MaterialTheme.colorScheme.primary else noorColors.goldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun categoryIcon(cat: AdhkarCategory): ImageVector = when (cat) {
    AdhkarCategory.MORNING -> Icons.Rounded.WbSunny
    AdhkarCategory.EVENING -> Icons.Rounded.NightsStay
    AdhkarCategory.AFTER_PRAYER -> Icons.Rounded.Mosque
    AdhkarCategory.SLEEP -> Icons.Rounded.Bedtime
    AdhkarCategory.WAKING -> Icons.Rounded.WbTwilight
    AdhkarCategory.HOME_MOSQUE -> Icons.Rounded.DoorFront
    AdhkarCategory.TRAVEL -> Icons.Rounded.Flight
    AdhkarCategory.PROPHETIC_DUA -> Icons.Rounded.AutoAwesome
    AdhkarCategory.QURANIC_DUA -> Icons.Rounded.MenuBook
}
