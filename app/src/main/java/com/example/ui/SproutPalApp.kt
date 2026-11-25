package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.ui.components.DemoToolsSheet
import com.example.ui.components.SimulatedNotificationBanner
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.EcoTutorScreen
import com.example.ui.screens.GardenScreen
import com.example.ui.screens.ProgressScreen

const val ROUTE_GARDEN = "garden"
const val ROUTE_TUTOR = "tutor"
const val ROUTE_ALERTS = "alerts"
const val ROUTE_PROGRESS = "progress"

private data class NavDestinationItem(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val NAV_ITEMS = listOf(
    NavDestinationItem(
        route = ROUTE_GARDEN,
        labelRes = R.string.nav_garden,
        selectedIcon = Icons.Filled.Spa,
        unselectedIcon = Icons.Outlined.Spa,
        testTag = "nav_garden"
    ),
    NavDestinationItem(
        route = ROUTE_TUTOR,
        labelRes = R.string.nav_tutor,
        selectedIcon = Icons.Filled.ChatBubble,
        unselectedIcon = Icons.Outlined.ChatBubbleOutline,
        testTag = "nav_tutor"
    ),
    NavDestinationItem(
        route = ROUTE_ALERTS,
        labelRes = R.string.nav_alerts,
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.NotificationsNone,
        testTag = "nav_alerts"
    ),
    NavDestinationItem(
        route = ROUTE_PROGRESS,
        labelRes = R.string.nav_progress,
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart,
        testTag = "nav_progress"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SproutPalApp(
    viewModel: SproutViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    var showDemoSheet by remember { mutableStateOf(false) }

    val navigateToRoute: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (uiState.virtualOffsetHours > 0) {
                                Text(
                                    text = "  (+${uiState.virtualOffsetHours}h)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showDemoSheet = true },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("demo_tools_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Science,
                                contentDescription = stringResource(R.string.action_demo_tools),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            bottomBar = {
                if (!useNavRail) {
                    NavigationBar(
                        windowInsets = WindowInsets.navigationBars,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        NAV_ITEMS.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any {
                                it.route == item.route
                            } == true
                            val label = stringResource(item.labelRes)
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateToRoute(item.route) },
                                icon = {
                                    NavItemIcon(
                                        item = item,
                                        selected = selected,
                                        unreadCount = if (item.route == ROUTE_ALERTS) uiState.unreadNotificationsCount else 0
                                    )
                                },
                                label = { Text(label) },
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            if (!uiState.isReady) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (useNavRail) {
                        NavigationRail(
                            modifier = Modifier
                                .fillMaxHeight()
                                .windowInsetsPadding(WindowInsets.navigationBars),
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            NAV_ITEMS.forEach { item ->
                                val selected = currentDestination?.hierarchy?.any {
                                    it.route == item.route
                                } == true
                                val label = stringResource(item.labelRes)
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { navigateToRoute(item.route) },
                                    icon = {
                                        NavItemIcon(
                                            item = item,
                                            selected = selected,
                                            unreadCount = if (item.route == ROUTE_ALERTS) uiState.unreadNotificationsCount else 0
                                        )
                                    },
                                    label = { Text(label) },
                                    modifier = Modifier.testTag(item.testTag)
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f).fillMaxSize()) {
                        SimulatedNotificationBanner(
                            banner = uiState.activeBanner,
                            onWaterAction = { viewModel.waterPlant() },
                            onDismiss = { viewModel.dismissBanner() }
                        )

                        NavHost(
                            navController = navController,
                            startDestination = ROUTE_GARDEN,
                            modifier = Modifier.weight(1f).fillMaxSize()
                        ) {
                            composable(ROUTE_GARDEN) {
                                GardenScreen(
                                    uiState = uiState,
                                    onWaterPlant = viewModel::waterPlant,
                                    onSelectPlantType = viewModel::selectPlantType,
                                    onRenamePlant = viewModel::renamePlant,
                                    onOpenTutor = { navigateToRoute(ROUTE_TUTOR) }
                                )
                            }
                            composable(ROUTE_TUTOR) {
                                EcoTutorScreen(
                                    uiState = uiState,
                                    onSendMessage = viewModel::sendChatMessage,
                                    onClearChat = viewModel::clearChatHistory,
                                    onDismissError = viewModel::dismissChatError
                                )
                            }
                            composable(ROUTE_ALERTS) {
                                AlertsScreen(
                                    uiState = uiState,
                                    onToggleReminders = viewModel::toggleReminders,
                                    onToggleEcoTips = viewModel::toggleEcoTips,
                                    onTriggerNotification = viewModel::triggerSimulatedNotification,
                                    onMarkAllRead = viewModel::markAllNotificationsRead
                                )
                            }
                            composable(ROUTE_PROGRESS) {
                                ProgressScreen(uiState = uiState)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDemoSheet) {
        DemoToolsSheet(
            uiState = uiState,
            onDismiss = { showDemoSheet = false },
            onFastForwardHours = viewModel::fastForwardHours,
            onJumpToHydration = viewModel::jumpToHydration,
            onTriggerNotification = viewModel::triggerSimulatedNotification,
            onResetDemo = viewModel::resetDemo
        )
    }
}

@Composable
private fun NavItemIcon(
    item: NavDestinationItem,
    selected: Boolean,
    unreadCount: Int
) {
    val iconVector = if (selected) item.selectedIcon else item.unselectedIcon
    val label = stringResource(item.labelRes)
    if (unreadCount > 0) {
        BadgedBox(
            badge = {
                Badge { Text(unreadCount.coerceAtMost(9).toString()) }
            }
        ) {
            Icon(imageVector = iconVector, contentDescription = label)
        }
    } else {
        Icon(imageVector = iconVector, contentDescription = label)
    }
}
