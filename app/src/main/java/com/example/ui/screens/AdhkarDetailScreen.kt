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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.StopCircle
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.catalog.AdhkarCatalog
import com.example.domain.model.AdhkarCategory
import com.example.domain.model.AppLanguage
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.NoorScreenHeader
import com.example.ui.i18n.L10n
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.LocalNoorColors
import com.example.viewmodel.NoorViewModel

@Composable
fun AdhkarDetailScreen(
    categoryId: String,
    viewModel: NoorViewModel,
    onBack: () -> Unit
) {
    val lang = LocalAppLanguage.current
    val isAr = lang == AppLanguage.ARABIC
    val category = AdhkarCategory.fromId(categoryId)
    val items = AdhkarCatalog.byCategory(category)
    val progressMap by viewModel.adhkarProgressMap.collectAsStateWithLifecycle()
    val favorites by viewModel.favoriteDhikrIds.collectAsStateWithLifecycle()
    val speakingId by viewModel.speakingDhikrId.collectAsStateWithLifecycle()
    val noorColors = LocalNoorColors.current

    DisposableEffect(Unit) {
        onDispose { viewModel.stopSpeakingDhikr() }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("adhkar_detail_screen"),
        contentPadding = PaddingValues(bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            NoorScreenHeader(
                title = if (isAr) category.nameAr else category.nameEn,
                subtitle = L10n.pick(lang, "اضغط على الذكر للعدّ وتتبع الإنجاز", "Tap card or counter button to count"),
                onBack = onBack
            )
        }

        items(items) { dhikr ->
            val current = progressMap[dhikr.id]?.currentCount ?: 0
            val isCompleted = current >= dhikr.targetCount
            val isFav = favorites.contains(dhikr.id)
            val isSpeaking = speakingId == dhikr.id
            val ratio = (current.toFloat() / dhikr.targetCount.coerceAtLeast(1)).coerceIn(0f, 1f)

            LiquidGlassCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .testTag("dhikr_card_${dhikr.id}"),
                strong = isCompleted,
                onClick = { viewModel.incrementDhikr(dhikr) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isAr) dhikr.sourceAr else dhikr.sourceEn,
                            style = MaterialTheme.typography.labelSmall,
                            color = noorColors.goldAccent
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { viewModel.toggleSpeakDhikr(dhikr) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Rounded.StopCircle else Icons.Rounded.VolumeUp,
                                contentDescription = L10n.pick(lang, "استماع للذكر", "Listen to Dhikr"),
                                tint = if (isSpeaking) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = { viewModel.toggleFavoriteDhikr(dhikr.id) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = L10n.pick(lang, "حفظ في المفضلة", "Toggle Favorite"),
                                tint = if (isFav) noorColors.goldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = dhikr.textAr,
                    fontFamily = AmiriFontFamily,
                    fontSize = 22.sp,
                    lineHeight = 40.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isAr) dhikr.meaningAr else dhikr.meaningEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { ratio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = if (isCompleted) noorColors.goldAccent else MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.incrementDhikr(dhikr) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted) noorColors.goldAccent else MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isCompleted) {
                        Icon(Icons.Rounded.CheckCircle, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = L10n.pick(lang, "مكتمل (${dhikr.targetCount}/${dhikr.targetCount})", "Completed (${dhikr.targetCount}/${dhikr.targetCount})"),
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "$current / ${dhikr.targetCount}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
