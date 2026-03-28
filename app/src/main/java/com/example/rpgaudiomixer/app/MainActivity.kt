package com.example.rpgaudiomixer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rpgaudiomixer.app.navigation.AppNavGraph
import com.example.rpgaudiomixer.app.navigation.BottomTab
import com.example.rpgaudiomixer.app.navigation.NavRoutes
import com.example.rpgaudiomixer.app.theme.ArcanumBorder
import com.example.rpgaudiomixer.app.theme.ArcanumCardSurface
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGrayMid
import com.example.rpgaudiomixer.app.theme.RPGAudioMixerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RPGAudioMixerTheme {
                val navController = rememberNavController()
                AppScaffold(navController = navController)
            }
        }
    }
}

@Composable
private fun AppScaffold(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Screens where bottom nav is visible
    val bottomNavRoutes = setOf(NavRoutes.HOME, NavRoutes.CAMPAIGNS, NavRoutes.SCENES, NavRoutes.LIBRARY)
    val showBottomNav = currentRoute in bottomNavRoutes

    Column(modifier = Modifier.fillMaxSize()) {
        AppNavGraph(
            navController = navController,
            modifier = Modifier.weight(1f),
        )
        if (showBottomNav) {
            ArcanumBottomBar(
                currentRoute = currentRoute,
                onTabSelected = { tab ->
                    if (currentRoute != tab.route) {
                        navController.navigate(tab.route) {
                            popUpTo(NavRoutes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
            )
        }
    }
}

private data class TabItem(val tab: BottomTab, val icon: ImageVector)

private val tabItems = listOf(
    TabItem(BottomTab.HOME, Icons.Filled.Home),
    TabItem(BottomTab.CAMPAIGNS, Icons.Filled.MenuBook),
    TabItem(BottomTab.SCENES, Icons.Filled.Movie),
    TabItem(BottomTab.LIBRARY, Icons.Filled.LibraryMusic),
)

@Composable
private fun ArcanumBottomBar(
    currentRoute: String?,
    onTabSelected: (BottomTab) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ArcanumCardSurface)
            .navigationBarsPadding(),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            tabItems.forEach { (tab, icon) ->
                val selected = currentRoute == tab.route
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(tab) }
                        .background(if (selected) ArcanumBorder else ArcanumCardSurface)
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = tab.label,
                        tint = if (selected) ArcanumGold else ArcanumGrayMid,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) ArcanumGold else ArcanumGrayMid,
                    )
                }
            }
        }
    }
}
