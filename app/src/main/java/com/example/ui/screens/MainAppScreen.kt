package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.VendorViewModel
import com.example.util.AppLanguage
import com.example.util.Localization

@Composable
fun MainAppScreen(viewModel: VendorViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedVendorId by viewModel.selectedVendorId.collectAsState()
    val notice by viewModel.userNotice.collectAsState()
    val lang by viewModel.appLanguage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(notice) {
        if (!notice.isNullOrBlank()) {
            snackbarHostState.showSnackbar(notice!!)
            viewModel.clearNotice()
        }
    }

    // Dynamic RTL for Persian, LTR for English
    val layoutDirection = if (lang == AppLanguage.FA) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        // Smart Back Navigation to prevent accidental exit
        BackHandler {
            when {
                currentScreen != AppScreen.MAIN_TABS -> {
                    viewModel.navigateBack()
                }
                currentTab != MainTab.HOME -> {
                    viewModel.currentTab.value = MainTab.HOME
                }
                else -> {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastBackPressTime < 2000L) {
                        (context as? Activity)?.finish()
                    } else {
                        lastBackPressTime = currentTime
                        val exitMsg = if (lang == AppLanguage.FA) {
                            "برای خروج از برنامه، دوباره دکمه بازگشت را بزنید"
                        } else {
                            "Press back again to exit"
                        }
                        Toast.makeText(context, exitMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (currentScreen == AppScreen.MAIN_TABS) {
                    AppBottomNavigationBar(
                        currentTab = currentTab,
                        lang = lang,
                        onTabSelected = { viewModel.currentTab.value = it }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {
                        AppScreen.ONBOARDING -> {
                            OnboardingScreen(
                                onStartEmpty = {
                                    viewModel.currentScreen.value = AppScreen.MAIN_TABS
                                },
                                onLoadSampleData = {
                                    viewModel.loadSampleData {
                                        viewModel.currentScreen.value = AppScreen.MAIN_TABS
                                    }
                                }
                            )
                        }

                        AppScreen.MAIN_TABS -> {
                            when (currentTab) {
                                MainTab.HOME -> {
                                    HomeScreen(
                                        viewModel = viewModel,
                                        onOpenSearch = { query ->
                                            viewModel.searchQuery.value = query
                                            viewModel.currentTab.value = MainTab.SEARCH
                                        },
                                        onOpenAddVendor = { viewModel.openAddVendor() },
                                        onVendorClick = { id -> viewModel.openVendorDetail(id) }
                                    )
                                }

                                MainTab.SEARCH -> {
                                    SearchScreen(
                                        viewModel = viewModel,
                                        onVendorClick = { id -> viewModel.openVendorDetail(id) }
                                    )
                                }

                                MainTab.FAVORITES -> {
                                    FavoritesScreen(
                                        viewModel = viewModel,
                                        onVendorClick = { id -> viewModel.openVendorDetail(id) }
                                    )
                                }

                                MainTab.CATALOG -> {
                                    // Fallback if somehow triggered
                                    SearchScreen(
                                        viewModel = viewModel,
                                        onVendorClick = { id -> viewModel.openVendorDetail(id) }
                                    )
                                }

                                MainTab.SETTINGS -> {
                                    SettingsScreen(viewModel = viewModel)
                                }
                            }
                        }

                        AppScreen.VENDOR_DETAIL -> {
                            selectedVendorId?.let { id ->
                                VendorDetailScreen(
                                    vendorId = id,
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() },
                                    onEditVendor = { v -> viewModel.openEditVendor(v) },
                                    onOpenMap = { vId -> viewModel.openLocationView(vId) }
                                )
                            }
                        }

                        AppScreen.ADD_EDIT_VENDOR -> {
                            AddEditVendorScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateBack() }
                            )
                        }

                        AppScreen.LOCATION_VIEW -> {
                            selectedVendorId?.let { id ->
                                LocationScreen(
                                    vendorId = id,
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(
    currentTab: MainTab,
    lang: AppLanguage,
    onTabSelected: (MainTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = currentTab == MainTab.HOME,
            onClick = { onTabSelected(MainTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = Localization.get("tab_home", lang)
                )
            },
            label = { Text(Localization.get("tab_home", lang), style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyDeep,
                selectedTextColor = TealDark,
                indicatorColor = TealPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("tab_home")
        )

        NavigationBarItem(
            selected = currentTab == MainTab.SEARCH,
            onClick = { onTabSelected(MainTab.SEARCH) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = Localization.get("tab_search", lang)
                )
            },
            label = { Text(Localization.get("tab_search", lang), style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyDeep,
                selectedTextColor = TealDark,
                indicatorColor = TealPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("tab_search")
        )

        NavigationBarItem(
            selected = currentTab == MainTab.FAVORITES,
            onClick = { onTabSelected(MainTab.FAVORITES) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.FAVORITES) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = Localization.get("tab_favorites", lang)
                )
            },
            label = { Text(Localization.get("tab_favorites", lang), style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyDeep,
                selectedTextColor = TealDark,
                indicatorColor = TealPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("tab_favorites")
        )

        NavigationBarItem(
            selected = currentTab == MainTab.SETTINGS,
            onClick = { onTabSelected(MainTab.SETTINGS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = Localization.get("tab_settings", lang)
                )
            },
            label = { Text(Localization.get("tab_settings", lang), style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyDeep,
                selectedTextColor = TealDark,
                indicatorColor = TealPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("tab_settings")
        )
    }
}
