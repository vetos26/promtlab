package com.promtlab.app.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.promtlab.app.ui.AppViewModel
import com.promtlab.app.ui.screens.detail.PromptDetailScreen
import com.promtlab.app.ui.screens.generate.GenerateScreen
import com.promtlab.app.ui.screens.history.HistoryScreen
import com.promtlab.app.ui.screens.settings.SettingsScreen
import com.promtlab.app.ui.screens.templates.TemplatesScreen

@Composable
fun PromtLabApp(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val colorScheme = MaterialTheme.colorScheme
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.navigationBars,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                AnimatedVisibility(
                    visible = Dest.tabs.any { it.route == currentRoute },
                    enter = slideInVertically(animationSpec = tween(250)) { it } + fadeIn(),
                    exit = slideOutVertically(animationSpec = tween(200)) { it } + fadeOut()
                ) {
                    PromtLabBottomBar(
                        currentRoute = currentRoute,
                        onSelect = { dest -> navigateToTab(navController, dest) }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Dest.Generate.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .statusBarsPadding(),
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
                            navigateToTab(navController, Dest.Generate)
                        }
                    )
                }
                composable(Dest.History.route) {
                    HistoryScreen(
                        viewModel = viewModel,
                        onOpenPrompt = { id -> navController.navigate(Dest.promptDetail(id)) },
                        onCreatePrompt = { navigateToTab(navController, Dest.Generate) }
                    )
                }
                composable(Dest.Settings.route) {
                    SettingsScreen(viewModel = viewModel)
                }
                composable(
                    route = Dest.PROMPT_DETAIL_ROUTE,
                    arguments = listOf(navArgument("promptId") { type = NavType.LongType })
                ) { entry ->
                    PromptDetailScreen(
                        viewModel = viewModel,
                        promptId = entry.arguments?.getLong("promptId") ?: 0L,
                        onBack = { navController.popBackStack() },
                        onGoCreate = { navigateToTab(navController, Dest.Generate) }
                    )
                }
            }
        }
    }
}

private fun navigateToTab(navController: NavHostController, dest: Dest) {
    navController.navigate(dest.route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
