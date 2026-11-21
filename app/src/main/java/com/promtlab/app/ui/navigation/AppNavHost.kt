package com.promtlab.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.promtlab.app.data.LearningLibrary
import com.promtlab.app.data.settings.ThemeMode
import com.promtlab.app.ui.AppViewModel
import com.promtlab.app.ui.GeneratorMode
import com.promtlab.app.ui.screens.about.AboutScreen
import com.promtlab.app.ui.screens.detail.PromptDetailScreen
import com.promtlab.app.ui.screens.favorites.FavoritesScreen
import com.promtlab.app.ui.screens.generate.GenerateScreen
import com.promtlab.app.ui.screens.learn.LearnScreen
import com.promtlab.app.ui.screens.settings.SettingsScreen
import com.promtlab.app.ui.screens.templates.TemplatesScreen
import kotlinx.coroutines.launch

@Composable
fun PromtLabApp(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val colorScheme = MaterialTheme.colorScheme
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val counts by viewModel.counts.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isDetail = currentRoute == Dest.PROMPT_DETAIL_ROUTE

    // «Создать промт» и «Изображения» делят один экран — запоминаем, какой пункт меню активен.
    var selectedRoute by rememberSaveable { mutableStateOf(Dest.Generate.route) }
    val selectedDest = Dest.items.firstOrNull { it.route == selectedRoute } ?: Dest.Generate

    val systemDark = isSystemInDarkTheme()
    // При старте сразу применяем режим, соответствующий выбранному пункту меню.
    LaunchedEffect(selectedRoute) {
        viewModel.setGeneratorMode(
            if (selectedDest == Dest.Image) GeneratorMode.IMAGE else GeneratorMode.TEXT
        )
    }

    val isDark = when (settings.themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            colorScheme.background,
            lerp(colorScheme.background, colorScheme.primary, 0.07f),
            colorScheme.background
        )
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerSheet(
                selected = selectedDest,
                themeMode = settings.themeMode,
                isDark = isDark,
                completedLessons = settings.completedLessons.size,
                totalLessons = LearningLibrary.lessons.size,
                savedCount = counts.first,
                favoriteCount = counts.second,
                onSelect = { dest ->
                    scope.launch { drawerState.close() }
                    selectedRoute = dest.route
                    viewModel.setGeneratorMode(
                        if (dest == Dest.Image) GeneratorMode.IMAGE else GeneratorMode.TEXT
                    )
                    navigateToTab(navController, dest.route)
                },
                onThemeToggle = {
                    viewModel.setThemeMode(if (isDark) ThemeMode.LIGHT else ThemeMode.DARK)
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets.navigationBars,
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .imePadding()
                        .statusBarsPadding()
                ) {
                    TopBar(
                        title = if (isDetail) "Промт" else selectedDest.label,
                        isDark = isDark,
                        isDetail = isDetail,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onBack = { navController.popBackStack() },
                        onThemeToggle = {
                            viewModel.setThemeMode(if (isDark) ThemeMode.LIGHT else ThemeMode.DARK)
                        }
                    )

                    NavHost(
                        navController = navController,
                        startDestination = Dest.Generate.route,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        enterTransition = {
                            fadeIn(tween(220)) + slideInHorizontally(tween(220)) { it / 14 }
                        },
                        exitTransition = { fadeOut(tween(150)) },
                        popEnterTransition = { fadeIn(tween(220)) },
                        popExitTransition = {
                            fadeOut(tween(150)) + slideOutHorizontally(tween(220)) { it / 14 }
                        }
                    ) {
                        composable(Dest.Generate.route) {
                            GenerateScreen(viewModel = viewModel)
                        }
                        composable(Dest.Templates.route) {
                            TemplatesScreen(
                                viewModel = viewModel,
                                onTemplateApplied = {
                                    selectedRoute = Dest.Generate.route
                                    navigateToTab(navController, Dest.Generate.route)
                                }
                            )
                        }
                        composable(Dest.Favorites.route) {
                            FavoritesScreen(
                                viewModel = viewModel,
                                onOpenPrompt = { id -> navController.navigate(Dest.promptDetail(id)) },
                                onCreatePrompt = {
                                    selectedRoute = Dest.Generate.route
                                    navigateToTab(navController, Dest.Generate.route)
                                }
                            )
                        }
                        composable(Dest.Learn.route) {
                            LearnScreen(viewModel = viewModel)
                        }
                        composable(Dest.Settings.route) {
                            SettingsScreen(viewModel = viewModel)
                        }
                        composable(Dest.About.route) {
                            AboutScreen(viewModel = viewModel)
                        }
                        composable(
                            route = Dest.PROMPT_DETAIL_ROUTE,
                            arguments = listOf(navArgument("promptId") { type = NavType.LongType })
                        ) { entry ->
                            PromptDetailScreen(
                                viewModel = viewModel,
                                promptId = entry.arguments?.getLong("promptId") ?: 0L,
                                onBack = { navController.popBackStack() },
                                onGoCreate = {
                                    scope.launch { drawerState.close() }
                                    selectedRoute = Dest.Generate.route
                                    navigateToTab(navController, Dest.Generate.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    title: String,
    isDark: Boolean,
    isDetail: Boolean,
    onMenuClick: () -> Unit,
    onBack: () -> Unit,
    onThemeToggle: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(cs.surfaceContainerHigh)
                .clickable(onClick = if (isDetail) onBack else onMenuClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDetail) Icons.Rounded.ArrowBack else Icons.Rounded.Menu,
                contentDescription = if (isDetail) "Назад" else "Открыть меню",
                tint = cs.onSurface,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = cs.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp
        )

        Spacer(Modifier.weight(1f))

        if (isDetail) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(cs.surfaceContainerHigh)
                    .clickable(onClick = onMenuClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Открыть меню",
                    tint = cs.onSurfaceVariant,
                    modifier = Modifier.size(21.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(cs.surfaceContainerHigh)
                    .clickable(onClick = onThemeToggle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                    contentDescription = "Переключить тему",
                    tint = cs.primary,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}

private fun navigateToTab(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
