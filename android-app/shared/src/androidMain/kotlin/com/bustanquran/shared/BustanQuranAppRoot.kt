package com.bustanquran.shared

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bustanquran.core.data.preferences.AppLocaleStore
import com.bustanquran.core.data.preferences.FamilyPreferences
import com.bustanquran.core.data.preferences.UserPreferencesRepository
import com.bustanquran.core.ui.components.BottomNav
import com.bustanquran.core.ui.theme.QpkSand
import com.bustanquran.core.ui.theme.QpkTheme
import com.bustanquran.shared.navigation.BustanQuranDestinations
import com.bustanquran.shared.navigation.BustanQuranNavHost
import org.koin.compose.koinInject

/**
 * Root composable of the application: theme, adaptive bottom bar and the router.
 *
 * It lives in `:shared` (not in `:app`) so both the Android Activity and, later,
 * the iOS view controller can render exactly the same tree.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun BustanQuranAppRoot() {
    ApplyStoredLanguage()
    QpkTheme {
        BustanQuranScaffold()
    }
}

/**
 * Recreates the activity when the language preference no longer matches the
 * resources, so switching to Arabic actually flips the strings and the RTL
 * layout direction instead of only being persisted.
 */
@Composable
private fun ApplyStoredLanguage() {
    val preferences: UserPreferencesRepository = koinInject()
    val familyPreferences by preferences.familyPreferences
        .collectAsState(initial = FamilyPreferences())
    val context = LocalContext.current

    LaunchedEffect(familyPreferences.appLang) {
        val desired = familyPreferences.appLang
        if (AppLocaleStore.appliedLang(context) != desired) {
            AppLocaleStore.persist(context, desired)
            (context as? Activity)?.recreate()
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun BustanQuranScaffold() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: BustanQuranDestinations.MAIN
    val activity = LocalContext.current as? Activity
    val isExpanded = activity?.let {
        calculateWindowSizeClass(it).widthSizeClass == WindowWidthSizeClass.Expanded
    } ?: false

    val showBottomNav = currentRoute in listOf(
        BustanQuranDestinations.HOME,
        BustanQuranDestinations.QURAN_LIBRARY,
        BustanQuranDestinations.KOLEKSI_KARTU
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = QpkSand,
        bottomBar = {
            if (showBottomNav && !isExpanded) {
                BottomNav(
                    currentRoute = currentRoute,
                    onNavigateToRoute = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo(BustanQuranDestinations.HOME) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        BustanQuranNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            startDestination = BustanQuranDestinations.MAIN
        )
    }
}
