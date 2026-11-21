package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Mosque
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.IslamicGeometricBackground
import com.example.ui.i18n.L10n
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.screens.AdhkarDetailScreen
import com.example.ui.screens.AdhkarScreen
import com.example.ui.screens.HijriCalendarScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PrayerTimesScreen
import com.example.ui.screens.QiblaScreen
import com.example.ui.screens.QuranReaderScreen
import com.example.ui.screens.QuranScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.TasbihScreen
import com.example.ui.theme.LocalNoorColors
import com.example.viewmodel.NoorViewModel

object NoorRoutes {
    const val HOME = "home"
    const val PRAYERS = "prayers"
    const val QURAN = "quran"
    const val ADHKAR = "adhkar"
    const val TASBIH = "tasbih"
    const val QIBLA = "qibla"
    const val CALENDAR = "calendar"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val QURAN_READER = "quran_reader/{surahNumber}"
    const val ADHKAR_DETAIL = "adhkar_detail/{categoryId}"

    fun quranReader(surahNumber: Int) = "quran_reader/$surahNumber"
    fun adhkarDetail(categoryId: String) = "adhkar_detail/$categoryId"
}

private data class NavTabItem(
    val route: String,
    val labelAr: String,
    val labelEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val bottomTabs = listOf(
    NavTabItem(NoorRoutes.HOME, "الرئيسية", "Home", Icons.Rounded.Home, Icons.Outlined.Home, "nav_home"),
    NavTabItem(NoorRoutes.PRAYERS, "الصلاة", "Prayers", Icons.Rounded.Mosque, Icons.Outlined.Mosque, "nav_prayers"),
    NavTabItem(NoorRoutes.QURAN, "القرآن", "Quran", Icons.Rounded.MenuBook, Icons.Outlined.MenuBook, "nav_quran"),
    NavTabItem(NoorRoutes.ADHKAR, "الأذكار", "Adhkar", Icons.Rounded.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_adhkar"),
    NavTabItem(NoorRoutes.TASBIH, "المسبحة", "Tasbih", Icons.Rounded.TouchApp, Icons.Outlined.TouchApp, "nav_tasbih")
)

@Composable
fun NoorAppShell(viewModel: NoorViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: NoorRoutes.HOME
    val lang = LocalAppLanguage.current
    val noorColors = LocalNoorColors.current
    val snackbarHostState = remember { SnackbarHostState() }
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()

    LaunchedEffect(statusBanner) {
        statusBanner?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusBanner()
        }
    }

    val topLevelRoutes = remember { bottomTabs.map { it.route }.toSet() }
    val showNavBar = currentRoute in topLevelRoutes

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    IslamicGeometricBackground {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useRail = maxWidth >= 700.dp

            Row(modifier = Modifier.fillMaxSize()) {
                if (useRail && showNavBar) {
                    NavigationRail(
                        modifier = Modifier
                            .fillMaxHeight()
                            .statusBarsPadding()
                            .navigationBarsPadding(),
                        containerColor = noorColors.glassSurfaceStrong
                    ) {
                        bottomTabs.forEach { tab ->
                            val selected = currentRoute == tab.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateToTab(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = L10n.pick(lang, tab.labelAr, tab.labelEn)
                                    )
                                },
                                label = { Text(L10n.pick(lang, tab.labelAr, tab.labelEn)) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }

                Scaffold(
                    containerColor = Color.Transparent,
                    contentWindowInsets = WindowInsets.safeDrawing,
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    bottomBar = {
                        if (!useRail && showNavBar) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .navigationBarsPadding()
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                NavigationBar(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(28.dp))
                                        .border(
                                            1.dp,
                                            noorColors.glassBorderBottom,
                                            RoundedCornerShape(28.dp)
                                        ),
                                    containerColor = noorColors.glassSurfaceStrong,
                                    tonalElevation = 0.dp,
                                    windowInsets = WindowInsets(0, 0, 0, 0)
                                ) {
                                    bottomTabs.forEach { tab ->
                                        val selected = currentRoute == tab.route
                                        NavigationBarItem(
                                            selected = selected,
                                            onClick = { navigateToTab(tab.route) },
                                            icon = {
                                                Icon(
                                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                    contentDescription = L10n.pick(lang, tab.labelAr, tab.labelEn)
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = L10n.pick(lang, tab.labelAr, tab.labelEn),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    maxLines = 1
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = noorColors.goldAccent,
                                                selectedTextColor = noorColors.goldAccent,
                                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            modifier = Modifier.testTag(tab.testTag)
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = innerPadding.calculateTopPadding())
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = NoorRoutes.HOME
                        ) {
                            composable(NoorRoutes.HOME) {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onOpenPrayers = { navigateToTab(NoorRoutes.PRAYERS) },
                                    onOpenQibla = { navController.navigate(NoorRoutes.QIBLA) },
                                    onOpenAdhkar = { navigateToTab(NoorRoutes.ADHKAR) },
                                    onOpenTasbih = { navigateToTab(NoorRoutes.TASBIH) },
                                    onOpenQuran = { navigateToTab(NoorRoutes.QURAN) },
                                    onOpenSurah = { surah -> navController.navigate(NoorRoutes.quranReader(surah)) },
                                    onOpenCalendar = { navController.navigate(NoorRoutes.CALENDAR) },
                                    onOpenStats = { navController.navigate(NoorRoutes.STATS) },
                                    onOpenSettings = { navController.navigate(NoorRoutes.SETTINGS) }
                                )
                            }
                            composable(NoorRoutes.PRAYERS) {
                                PrayerTimesScreen(
                                    viewModel = viewModel,
                                    onOpenQibla = { navController.navigate(NoorRoutes.QIBLA) }
                                )
                            }
                            composable(NoorRoutes.QURAN) {
                                QuranScreen(
                                    viewModel = viewModel,
                                    onOpenSurah = { surah -> navController.navigate(NoorRoutes.quranReader(surah)) }
                                )
                            }
                            composable(
                                route = NoorRoutes.QURAN_READER,
                                arguments = listOf(navArgument("surahNumber") { type = NavType.IntType })
                            ) { entry ->
                                val surahNum = entry.arguments?.getInt("surahNumber") ?: 1
                                QuranReaderScreen(
                                    surahNumber = surahNum,
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(NoorRoutes.ADHKAR) {
                                AdhkarScreen(
                                    viewModel = viewModel,
                                    onOpenCategory = { catId -> navController.navigate(NoorRoutes.adhkarDetail(catId)) }
                                )
                            }
                            composable(
                                route = NoorRoutes.ADHKAR_DETAIL,
                                arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
                            ) { entry ->
                                val catId = entry.arguments?.getString("categoryId") ?: "morning"
                                AdhkarDetailScreen(
                                    categoryId = catId,
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(NoorRoutes.TASBIH) {
                                TasbihScreen(viewModel = viewModel)
                            }
                            composable(NoorRoutes.QIBLA) {
                                QiblaScreen(
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(NoorRoutes.CALENDAR) {
                                HijriCalendarScreen(
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(NoorRoutes.STATS) {
                                StatsScreen(
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable(NoorRoutes.SETTINGS) {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() },
                                    onOpenPrayers = {
                                        navController.popBackStack()
                                        navigateToTab(NoorRoutes.PRAYERS)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
