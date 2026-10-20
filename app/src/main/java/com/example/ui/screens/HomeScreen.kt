package com.example.ui.screens

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
import androidx.compose.ui.graphics.Brush
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
fun HomeScreen(
    establishments: List<Establishment>,
    selectedCategory: ServiceCategory,
    onCategorySelect: (ServiceCategory) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onEstablishmentClick: (Establishment) -> Unit,
    onTransportPromoClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onViewAllClick: () -> Unit,
    unreadNotificationCount: Int = 2,
    isMalagasy: Boolean = false,
    modifier: Modifier = Modifier
) {
    var currentLocationName by remember { mutableStateOf("Antananarivo · Analakely") }
    var showLocationDialog by remember { mutableStateOf(false) }

    val filteredList = remember(establishments, selectedCategory, searchQuery) {
        establishments.filter { est ->
            val matchesCategory = selectedCategory == ServiceCategory.ALL ||
                    selectedCategory == ServiceCategory.TRANSPORT ||
                    est.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    est.name.contains(searchQuery, ignoreCase = true) ||
                    est.shortAddress.contains(searchQuery, ignoreCase = true) ||
                    est.services.any { it.contains(searchQuery, ignoreCase = true) }
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            // 3.1 Custom AppBar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Location selector cliquable
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showLocationDialog = true }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("home_location_selector"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                AppIcon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Position GPS",
                                    size = 20.dp,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isMalagasy) "Toerana misy anao" else "Position actuelle",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentLocationName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                AppIcon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Changer",
                                    size = 18.dp,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Actions: Notifications with badge + Profile Avatar
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNotificationsClick,
                            modifier = Modifier.testTag("home_notifications_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadNotificationCount > 0) {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = Color.White
                                        ) {
                                            Text(unreadNotificationCount.toString())
                                        }
                                    }
                                }
                            ) {
                                AppIcon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        IconButton(
                            onClick = onProfileClick,
                            modifier = Modifier.testTag("home_profile_button")
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    AppIcon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profil",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        size = 20.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier.testTag("home_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // 3.2 Search Bar
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = {
                            Text(
                                if (isMalagasy) "Hikaroka trano fisakafoanana, hotely, farmasia..."
                                else "Rechercher un restaurant, hôtel, pharmacie…"
                            )
                        },
                        leadingIcon = {
                            AppIcon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Recherche",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    AppIcon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Effacer",
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_input")
                    )
                }
            }

            // 3.3 Horizontal Category Chips Bar
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ServiceCategory.values()) { category ->
                        AppCategoryChip(
                            category = category,
                            isSelected = selectedCategory == category,
                            isMalagasy = isMalagasy,
                            onClick = {
                                if (category == ServiceCategory.TRANSPORT) {
                                    onTransportPromoClick()
                                } else {
                                    onCategorySelect(category)
                                }
                            }
                        )
                    }
                }
            }

            // 3.5 Transport Hero Banner (Promoted transport module)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onTransportPromoClick() }
                        .testTag("home_transport_hero_banner"),
                    color = Color.Transparent,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        EmeraldPrimaryLight,
                                        Color(0xFF004D36),
                                        Color(0xFF00291D)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AmberSecondaryLight
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AppIcon(
                                            imageVector = Icons.Default.DirectionsCar,
                                            contentDescription = "Guide Ride",
                                            size = 14.dp,
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "GUIDE RIDE · TARIF LIBRE",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.2f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        AppIcon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = "Aller au transport",
                                            tint = Color.White,
                                            size = 18.dp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = if (isMalagasy) "Mila fitaterana? Manolora ny vidinao." else "Besoin d'un trajet ? Proposez votre prix.",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isMalagasy) "Fiara · Môtô / Tuk-Tuk · Fandefasana entana ao Antananarivo"
                                else "Voiture · Moto · Colis · Négociez directement avec les chauffeurs à Antananarivo",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = onTransportPromoClick,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AmberSecondaryLight,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AppIcon(
                                        imageVector = Icons.Default.LocalTaxi,
                                        contentDescription = "",
                                        size = 18.dp,
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isMalagasy) "Hanafatra dia" else "Commander une course",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3.4 "Services à proximité" Carousel
            item {
                Column(modifier = Modifier.padding(top = 24.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isMalagasy) "Akaiky anao" else "À proximité de vous",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isMalagasy) "Toerana sy tolotra ao anatin'ny 2 km" else "Établissements dans un rayon de 2 km",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = onViewAllClick) {
                            Text(
                                text = if (isMalagasy) "Hijery rehetra" else "Voir tout",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(filteredList) { establishment ->
                            EstablishmentCard(
                                establishment = establishment,
                                onClick = { onEstablishmentClick(establishment) },
                                modifier = Modifier.width(220.dp)
                            )
                        }
                    }
                }
            }

            // 3.6 "Recommandés pour vous" Section (2 columns compact)
            item {
                Column(modifier = Modifier.padding(top = 28.dp, start = 16.dp, end = 16.dp)) {
                    Text(
                        text = if (isMalagasy) "Natolotra ho anao" else "Recommandés pour vous",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isMalagasy) "Mifototra amin'ny toerana misy anao sy ny fankafizana"
                        else "Basé sur votre historique et votre localisation",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val recommendedList = establishments.filter { it.rating >= 4.7f }
                    recommendedList.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { est ->
                                CompactEstablishmentCard(
                                    establishment = est,
                                    onClick = { onEstablishmentClick(est) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // 3.7 "Nouveaux partenaires" Section
            item {
                Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
                    Text(
                        text = if (isMalagasy) "Mpiara-miombon'antoka vaovao" else "Nouveaux partenaires",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(establishments.takeLast(4)) { est ->
                            NewPartnerCard(
                                establishment = est,
                                onClick = { onEstablishmentClick(est) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text("Sélectionner votre quartier") },
            text = {
                Column {
                    listOf(
                        "Antananarivo · Analakely",
                        "Antananarivo · Ankorondrano",
                        "Antananarivo · Isoraka",
                        "Antananarivo · Anosy",
                        "Antananarivo · Ivato Aéroport"
                    ).forEach { loc ->
                        Surface(
                            onClick = {
                                currentLocationName = loc
                                showLocationDialog = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            color = if (currentLocationName == loc) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppIcon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "",
                                    size = 18.dp,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = loc,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (currentLocationName == loc) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text("Fermer")
                }
            }
        )
    }
}

@Composable
fun EstablishmentCard(
    establishment: Establishment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("establishment_card_${establishment.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Simulated Visual Banner Header with Category Color & Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                establishment.category.badgeColor.copy(alpha = 0.85f),
                                establishment.category.badgeColor
                            )
                        )
                    )
                    .padding(10.dp)
            ) {
                // Category Icon watermark
                AppIcon(
                    imageVector = when (establishment.category) {
                        ServiceCategory.RESTAURANT -> Icons.Default.Restaurant
                        ServiceCategory.HOTEL -> Icons.Default.Hotel
                        ServiceCategory.PHARMACY -> Icons.Default.LocalPharmacy
                        ServiceCategory.SHOP -> Icons.Default.Storefront
                        ServiceCategory.OTHER -> Icons.Default.Business
                        else -> Icons.Default.Place
                    },
                    contentDescription = "",
                    size = 48.dp,
                    tint = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.align(Alignment.BottomEnd)
                )

                // Category Badge Overlay
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.4f),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = establishment.category.titleFr,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Open status badge overlay
                OpenStatusBadge(
                    isOpen = establishment.isOpen,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            // Card Body
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = establishment.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RatingBar(rating = establishment.rating, reviewCount = establishment.reviewCount)
                    Text(
                        text = "${establishment.distanceKm} km",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = establishment.shortAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = onClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                    ) {
                        AppIcon(
                            imageVector = Icons.Default.Directions,
                            contentDescription = "Y aller",
                            size = 14.dp,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Y aller",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                    ) {
                        AppIcon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Appeler",
                            size = 14.dp,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Appeler",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompactEstablishmentCard(
    establishment: Establishment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("compact_card_${establishment.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = establishment.category.badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = establishment.category.titleFr,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = establishment.category.badgeColor
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "${establishment.distanceKm} km",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = establishment.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            RatingBar(rating = establishment.rating)
        }
    }
}

@Composable
fun NewPartnerCard(
    establishment: Establishment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier
            .width(260.dp)
            .defaultMinSize(minHeight = 64.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = establishment.category.badgeColor,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    AppIcon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "",
                        size = 20.dp,
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = establishment.name,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${establishment.shortAddress} · ${establishment.distanceKm} km",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
