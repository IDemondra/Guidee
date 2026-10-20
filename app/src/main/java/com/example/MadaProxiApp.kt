package com.example

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.model.*
import com.example.ui.components.AppIcon
import com.example.ui.screens.*
import com.example.ui.theme.*

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    AUTH,
    HOME,
    MAP,
    TRANSPORT,
    ESTABLISHMENT_DETAIL,
    HISTORY,
    PROFILE,
    NOTIFICATIONS,
    FAVORITES,
    DRIVER_MODE,
    MERCHANT_MODE
}

@Composable
fun GuideMeApp(
    repository: AppRepository = remember { AppRepository() }
) {
    val establishments by repository.establishments.collectAsState()
    val selectedCategory by repository.selectedCategory.collectAsState()
    val searchRadiusKm by repository.searchRadiusKm.collectAsState()
    val searchQuery by repository.searchQuery.collectAsState()
    val userRole by repository.userRole.collectAsState()
    val appLanguage by repository.appLanguage.collectAsState()
    val isDarkMode by repository.isDarkMode.collectAsState()

    val rideStatus by repository.rideStatus.collectAsState()
    val activeRide by repository.activeRide.collectAsState()
    val receivedOffers by repository.receivedOffers.collectAsState()
    val acceptedDriver by repository.acceptedDriver.collectAsState()
    val rideHistory by repository.rideHistory.collectAsState()
    val visitedServices by repository.visitedServices.collectAsState()
    val notifications by repository.notifications.collectAsState()

    val isDriverOnline by repository.isDriverOnline.collectAsState()
    val driverEarnings by repository.driverTodayEarningsAr.collectAsState()
    val driverCompletedTrips by repository.driverCompletedTrips.collectAsState()

    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
    var previousScreen by remember { mutableStateOf(AppScreen.HOME) }
    var selectedEstablishment by remember { mutableStateOf<Establishment?>(null) }
    var transportPresetDestination by remember { mutableStateOf<String?>(null) }

    val isMalagasy = appLanguage == AppLanguage.MG

    GuideMeTheme(darkTheme = isDarkMode) {
        Scaffold(
            bottomBar = {
                // Show bottom bar only on primary app tabs
                val showBottomNav = currentScreen in listOf(
                    AppScreen.HOME,
                    AppScreen.MAP,
                    AppScreen.TRANSPORT,
                    AppScreen.HISTORY,
                    AppScreen.PROFILE,
                    AppScreen.DRIVER_MODE,
                    AppScreen.MERCHANT_MODE
                )

                if (showBottomNav) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("main_bottom_nav")
                    ) {
                        // 1. Accueil
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.HOME,
                            onClick = { currentScreen = AppScreen.HOME },
                            icon = {
                                AppIcon(
                                    imageVector = if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Accueil"
                                )
                            },
                            label = {
                                Text(
                                    text = if (isMalagasy) "Fandraisana" else "Accueil",
                                    fontSize = 11.sp,
                                    fontWeight = if (currentScreen == AppScreen.HOME) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("nav_home")
                        )

                        // 2. Carte
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.MAP,
                            onClick = { currentScreen = AppScreen.MAP },
                            icon = {
                                AppIcon(
                                    imageVector = if (currentScreen == AppScreen.MAP) Icons.Filled.Map else Icons.Outlined.Map,
                                    contentDescription = "Carte"
                                )
                            },
                            label = {
                                Text(
                                    text = if (isMalagasy) "Sarintany" else "Carte",
                                    fontSize = 11.sp,
                                    fontWeight = if (currentScreen == AppScreen.MAP) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("nav_map")
                        )

                        // 3. Transport (Central highlighted action)
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.TRANSPORT,
                            onClick = {
                                transportPresetDestination = null
                                currentScreen = AppScreen.TRANSPORT
                            },
                            icon = {
                                Surface(
                                    shape = CircleShape,
                                    color = if (currentScreen == AppScreen.TRANSPORT) AmberSecondaryLight else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        AppIcon(
                                            imageVector = Icons.Filled.DirectionsCar,
                                            contentDescription = "Transport",
                                            tint = Color.White,
                                            size = 20.dp
                                        )
                                    }
                                }
                            },
                            label = {
                                Text(
                                    text = if (isMalagasy) "Fitaterana" else "Transport",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentScreen == AppScreen.TRANSPORT) AmberSecondaryLight else MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier.testTag("nav_transport")
                        )

                        // 4. Historique (or Role specific dashboard)
                        if (userRole == UserRole.DRIVER) {
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.DRIVER_MODE,
                                onClick = { currentScreen = AppScreen.DRIVER_MODE },
                                icon = {
                                    AppIcon(
                                        imageVector = Icons.Filled.Speed,
                                        contentDescription = "Chauffeur"
                                    )
                                },
                                label = { Text("Chauffeur", fontSize = 11.sp) }
                            )
                        } else if (userRole == UserRole.MERCHANT) {
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.MERCHANT_MODE,
                                onClick = { currentScreen = AppScreen.MERCHANT_MODE },
                                icon = {
                                    AppIcon(
                                        imageVector = Icons.Filled.Store,
                                        contentDescription = "Commerce"
                                    )
                                },
                                label = { Text("Commerce", fontSize = 11.sp) }
                            )
                        } else {
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.HISTORY,
                                onClick = { currentScreen = AppScreen.HISTORY },
                                icon = {
                                    AppIcon(
                                        imageVector = if (currentScreen == AppScreen.HISTORY) Icons.Filled.History else Icons.Outlined.History,
                                        contentDescription = "Historique"
                                    )
                                },
                                label = {
                                    Text(
                                        text = if (isMalagasy) "Tantara" else "Historique",
                                        fontSize = 11.sp,
                                        fontWeight = if (currentScreen == AppScreen.HISTORY) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.testTag("nav_history")
                            )
                        }

                        // 5. Profil
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.PROFILE,
                            onClick = { currentScreen = AppScreen.PROFILE },
                            icon = {
                                AppIcon(
                                    imageVector = if (currentScreen == AppScreen.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Profil"
                                )
                            },
                            label = {
                                Text(
                                    text = if (isMalagasy) "Mombamomba" else "Profil",
                                    fontSize = 11.sp,
                                    fontWeight = if (currentScreen == AppScreen.PROFILE) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("nav_profile")
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    AppScreen.SPLASH -> {
                        SplashScreen(
                            onSplashFinished = { currentScreen = AppScreen.ONBOARDING }
                        )
                    }

                    AppScreen.ONBOARDING -> {
                        OnboardingScreen(
                            onFinished = { currentScreen = AppScreen.HOME }
                        )
                    }

                    AppScreen.AUTH -> {
                        BackHandler { currentScreen = AppScreen.HOME }
                        AuthScreen(
                            onAuthSuccess = { role ->
                                repository.setUserRole(role)
                                currentScreen = when (role) {
                                    UserRole.CLIENT -> AppScreen.HOME
                                    UserRole.DRIVER -> AppScreen.DRIVER_MODE
                                    UserRole.MERCHANT -> AppScreen.MERCHANT_MODE
                                }
                            },
                            onContinueAsGuest = { currentScreen = AppScreen.HOME }
                        )
                    }

                    AppScreen.HOME -> {
                        HomeScreen(
                            establishments = establishments,
                            selectedCategory = selectedCategory,
                            onCategorySelect = { repository.setCategory(it) },
                            searchQuery = searchQuery,
                            onSearchQueryChange = { repository.setSearchQuery(it) },
                            onEstablishmentClick = { est ->
                                selectedEstablishment = est
                                previousScreen = AppScreen.HOME
                                currentScreen = AppScreen.ESTABLISHMENT_DETAIL
                            },
                            onTransportPromoClick = {
                                transportPresetDestination = null
                                currentScreen = AppScreen.TRANSPORT
                            },
                            onNotificationsClick = {
                                previousScreen = AppScreen.HOME
                                currentScreen = AppScreen.NOTIFICATIONS
                            },
                            onProfileClick = {
                                currentScreen = AppScreen.PROFILE
                            },
                            onViewAllClick = {
                                currentScreen = AppScreen.MAP
                            },
                            unreadNotificationCount = notifications.count { !it.isRead },
                            isMalagasy = isMalagasy
                        )
                    }

                    AppScreen.MAP -> {
                        BackHandler { currentScreen = AppScreen.HOME }
                        MapScreen(
                            establishments = establishments,
                            selectedCategory = selectedCategory,
                            onCategorySelect = { repository.setCategory(it) },
                            searchRadiusKm = searchRadiusKm,
                            onRadiusChange = { repository.setRadius(it) },
                            onEstablishmentClick = { est ->
                                selectedEstablishment = est
                                previousScreen = AppScreen.MAP
                                currentScreen = AppScreen.ESTABLISHMENT_DETAIL
                            },
                            onOrderRideToLocation = { est ->
                                transportPresetDestination = "${est.name} (${est.shortAddress})"
                                currentScreen = AppScreen.TRANSPORT
                            },
                            onToggleFavorite = { repository.toggleFavorite(it) }
                        )
                    }

                    AppScreen.TRANSPORT -> {
                        BackHandler {
                            if (rideStatus == RideStatus.IDLE || rideStatus == RideStatus.COMPLETED) {
                                currentScreen = AppScreen.HOME
                            } else {
                                repository.resetRide()
                            }
                        }
                        TransportScreen(
                            rideStatus = rideStatus,
                            activeRide = activeRide,
                            receivedOffers = receivedOffers,
                            acceptedDriver = acceptedDriver,
                            onRequestRide = { repository.createRideRequest(it) },
                            onPopulateOffers = { repository.populateDriverOffers() },
                            onAcceptDriver = { repository.acceptDriver(it) },
                            onCounterOffer = { id, price -> repository.counterOfferPrice(id, price) },
                            onUpdateStatus = { repository.updateRideStatus(it) },
                            onCompleteRide = { payment, rating -> repository.completeRide(payment, rating) },
                            onResetRide = { repository.resetRide() },
                            presetDestination = transportPresetDestination,
                            isMalagasy = isMalagasy
                        )
                    }

                    AppScreen.ESTABLISHMENT_DETAIL -> {
                        BackHandler { currentScreen = previousScreen }
                        selectedEstablishment?.let { est ->
                            EstablishmentDetailScreen(
                                establishment = est,
                                onBack = { currentScreen = previousScreen },
                                onToggleFavorite = { repository.toggleFavorite(it) },
                                onOrderRide = {
                                    transportPresetDestination = "${est.name} (${est.shortAddress})"
                                    currentScreen = AppScreen.TRANSPORT
                                },
                                onActionTaken = { id, act -> repository.addVisitedAction(id, act) },
                                isMalagasy = isMalagasy
                            )
                        } ?: run {
                            currentScreen = AppScreen.HOME
                        }
                    }

                    AppScreen.HISTORY -> {
                        BackHandler { currentScreen = AppScreen.HOME }
                        HistoryScreen(
                            rideHistory = rideHistory,
                            visitedServices = visitedServices,
                            isMalagasy = isMalagasy
                        )
                    }

                    AppScreen.PROFILE -> {
                        BackHandler { currentScreen = AppScreen.HOME }
                        ProfileScreen(
                            currentRole = userRole,
                            onRoleChange = { role ->
                                repository.setUserRole(role)
                                if (role == UserRole.DRIVER) currentScreen = AppScreen.DRIVER_MODE
                                else if (role == UserRole.MERCHANT) currentScreen = AppScreen.MERCHANT_MODE
                            },
                            currentLanguage = appLanguage,
                            onLanguageChange = { repository.setLanguage(it) },
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = { repository.toggleDarkMode() },
                            searchRadiusKm = searchRadiusKm,
                            onRadiusChange = { repository.setRadius(it) },
                            onLogout = { currentScreen = AppScreen.AUTH }
                        )
                    }

                    AppScreen.NOTIFICATIONS -> {
                        BackHandler { currentScreen = previousScreen }
                        NotificationsScreen(
                            notifications = notifications,
                            onMarkAllRead = { repository.markAllNotificationsRead() },
                            onBack = { currentScreen = previousScreen }
                        )
                    }

                    AppScreen.FAVORITES -> {
                        BackHandler { currentScreen = AppScreen.HOME }
                        FavoritesScreen(
                            favoriteEstablishments = establishments.filter { it.isFavorite },
                            onEstablishmentClick = { est ->
                                selectedEstablishment = est
                                previousScreen = AppScreen.FAVORITES
                                currentScreen = AppScreen.ESTABLISHMENT_DETAIL
                            },
                            onToggleFavorite = { repository.toggleFavorite(it) },
                            onBack = { currentScreen = AppScreen.HOME }
                        )
                    }

                    AppScreen.DRIVER_MODE -> {
                        BackHandler { currentScreen = AppScreen.HOME }
                        DriverDashboardScreen(
                            isOnline = isDriverOnline,
                            onToggleOnline = { repository.toggleDriverOnline() },
                            todayEarningsAr = driverEarnings,
                            completedTrips = driverCompletedTrips
                        )
                    }

                    AppScreen.MERCHANT_MODE -> {
                        BackHandler { currentScreen = AppScreen.HOME }
                        MerchantDashboardScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MadaProxiApp(repository: AppRepository = remember { AppRepository() }) = GuideMeApp(repository = repository)

