package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Establishment
import com.example.model.ServiceCategory
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    establishments: List<Establishment>,
    selectedCategory: ServiceCategory,
    onCategorySelect: (ServiceCategory) -> Unit,
    searchRadiusKm: Double,
    onRadiusChange: (Double) -> Unit,
    onEstablishmentClick: (Establishment) -> Unit,
    onOrderRideToLocation: (Establishment) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPin by remember { mutableStateOf<Establishment?>(null) }
    var isListView by remember { mutableStateOf(false) }
    var useOpenStreetMap by remember { mutableStateOf(true) }
    var recenterTrigger by remember { mutableStateOf(0) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var onlyOpenNow by remember { mutableStateOf(false) }
    var minRating by remember { mutableStateOf(0f) }

    val filteredEstablishments = remember(
        establishments,
        selectedCategory,
        searchRadiusKm,
        searchQuery,
        onlyOpenNow,
        minRating
    ) {
        establishments.filter { est ->
            val matchesCat = selectedCategory == ServiceCategory.ALL || est.category == selectedCategory
            val matchesRadius = est.distanceKm <= searchRadiusKm
            val matchesQuery = searchQuery.isBlank() ||
                    est.name.contains(searchQuery, ignoreCase = true) ||
                    est.shortAddress.contains(searchQuery, ignoreCase = true)
            val matchesOpen = !onlyOpenNow || est.isOpen
            val matchesRating = est.rating >= minRating

            matchesCat && matchesRadius && matchesQuery && matchesOpen && matchesRating
        }
    }

    val radiusOptions = listOf(0.5, 1.0, 2.0, 5.0, 10.0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("map_screen")
    ) {
        if (!isListView) {
            if (useOpenStreetMap) {
                // Official OpenStreetMap live tile rendering (OSMDroid)
                OpenStreetMapComponent(
                    establishments = filteredEstablishments,
                    selectedRadiusKm = searchRadiusKm,
                    selectedEstablishment = selectedPin,
                    onEstablishmentClick = { est -> selectedPin = est },
                    recenterTrigger = recenterTrigger,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // High-contrast Offline Vector Map Canvas
                InteractiveMapCanvas(
                    establishments = filteredEstablishments,
                    selectedRadiusKm = searchRadiusKm,
                    selectedEstablishment = selectedPin,
                    onEstablishmentClick = { est -> selectedPin = est },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            // Alternate List View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 160.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredEstablishments) { est ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedPin = est },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = est.category.badgeColor,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    AppIcon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = "",
                                        size = 24.dp,
                                        tint = Color.White
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = est.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${est.category.titleFr} · ${est.shortAddress}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RatingBar(rating = est.rating, reviewCount = est.reviewCount)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${est.distanceKm} km",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Top Floating Overlay (Search Bar & Radius chips)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            // Search Input with Filter and View Toggle
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Recherche",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Rechercher sur la carte…", style = MaterialTheme.typography.bodyMedium) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("map_search_input")
                    )

                    // Filter dialog button
                    IconButton(
                        onClick = { showFilterDialog = true },
                        modifier = Modifier.testTag("map_filter_button")
                    ) {
                        AppIcon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filtres",
                            tint = if (onlyOpenNow || minRating > 0f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }

                    // Toggle OpenStreetMap / Mode Hors-Ligne
                    IconButton(
                        onClick = { useOpenStreetMap = !useOpenStreetMap },
                        modifier = Modifier.testTag("map_layer_toggle")
                    ) {
                        AppIcon(
                            imageVector = if (useOpenStreetMap) Icons.Default.Public else Icons.Default.Layers,
                            contentDescription = if (useOpenStreetMap) "Mode OpenStreetMap" else "Mode Carte Vectorielle",
                            tint = if (useOpenStreetMap) EmeraldPrimaryLight else MaterialTheme.colorScheme.outline
                        )
                    }

                    // Toggle Map / List view
                    IconButton(
                        onClick = { isListView = !isListView },
                        modifier = Modifier.testTag("map_view_toggle")
                    ) {
                        AppIcon(
                            imageVector = if (isListView) Icons.Default.Map else Icons.Default.FormatListBulleted,
                            contentDescription = "Basculer vue",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Radius Selector Chips (500m, 1km, 2km, 5km, 10km)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(radiusOptions) { radius ->
                    val isSelected = searchRadiusKm == radius
                    val label = if (radius < 1.0) "${(radius * 1000).toInt()} m" else "${radius.toInt()} km"
                    Surface(
                        onClick = { onRadiusChange(radius) },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        shadowElevation = 3.dp,
                        modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIcon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = "",
                                size = 14.dp,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(ServiceCategory.values().filter { it != ServiceCategory.TRANSPORT }) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        onClick = { onCategorySelect(cat) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) cat.badgeColor else MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        shadowElevation = 2.dp,
                        modifier = Modifier.defaultMinSize(minHeight = 32.dp)
                    ) {
                        Text(
                            text = cat.titleFr,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // OpenStreetMap Attribution badge
        if (useOpenStreetMap && !isListView) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.Black.copy(alpha = 0.55f),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 12.dp, bottom = if (selectedPin != null) 320.dp else 100.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(
                        imageVector = Icons.Default.Public,
                        contentDescription = "OpenStreetMap",
                        tint = Color.White,
                        size = 12.dp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "© OpenStreetMap",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    )
                }
            }
        }

        // Recenter FAB (Bottom right)
        FloatingActionButton(
            onClick = {
                recenterTrigger++
                selectedPin = null
            },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = if (selectedPin != null) 310.dp else 100.dp)
                .testTag("map_recenter_fab")
        ) {
            AppIcon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Recentrer sur ma position",
                tint = MaterialTheme.colorScheme.primary,
                size = 24.dp
            )
        }

        // 4.3 Bottom Sheet on Pin Click
        AnimatedVisibility(
            visible = selectedPin != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 80.dp)
        ) {
            selectedPin?.let { est ->
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("map_pin_bottom_sheet")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = est.category.badgeColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = est.category.titleFr,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = est.category.badgeColor
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OpenStatusBadge(isOpen = est.isOpen)
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { onToggleFavorite(est.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    AppIcon(
                                        imageVector = if (est.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favori",
                                        tint = if (est.isFavorite) Color.Red else MaterialTheme.colorScheme.outline,
                                        size = 22.dp
                                    )
                                }
                                IconButton(
                                    onClick = { selectedPin = null },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    AppIcon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Fermer",
                                        tint = MaterialTheme.colorScheme.outline,
                                        size = 20.dp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = est.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RatingBar(rating = est.rating, reviewCount = est.reviewCount)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "·  ${est.distanceKm} km",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = est.fullAddress,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons: Itinéraire, Appeler, Détail
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onEstablishmentClick(est) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 44.dp)
                            ) {
                                AppIcon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "",
                                    size = 18.dp,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Détails")
                            }

                            Button(
                                onClick = { onOrderRideToLocation(est) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .defaultMinSize(minHeight = 44.dp)
                                    .testTag("map_order_ride_button")
                            ) {
                                AppIcon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = "",
                                    size = 18.dp,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Transport ici")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filtres de recherche avancés") },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ouvert actuellement uniquement")
                        Switch(
                            checked = onlyOpenNow,
                            onCheckedChange = { onlyOpenNow = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Note minimale : ${if (minRating == 0f) "Toutes" else "$minRating★+"}")
                    Slider(
                        value = minRating,
                        onValueChange = { minRating = it },
                        valueRange = 0f..4.8f,
                        steps = 4
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Appliquer")
                }
            }
        )
    }
}
