package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.model.HotelRoomItem
import com.example.model.MenuItem
import com.example.model.ServiceCategory
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstablishmentDetailScreen(
    establishment: Establishment,
    onBack: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOrderRide: (Establishment) -> Unit,
    onActionTaken: (String, String) -> Unit,
    isMalagasy: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showReviewDialog by remember { mutableStateOf(false) }
    var newReviewComment by remember { mutableStateOf("") }
    var newReviewRating by remember { mutableStateOf(5) }
    var actionMessage by remember { mutableStateOf<String?>(null) }

    // Restaurant Menu State
    var selectedMenuSection by remember { mutableStateOf("Tous") }
    var orderCount by remember { mutableStateOf(0) }

    // Hotel Room Reservation State
    var selectedRoomForBooking by remember { mutableStateOf<HotelRoomItem?>(null) }
    var bookingNights by remember { mutableStateOf(1) }

    val filteredMenu = remember(establishment.menuCatalog, selectedMenuSection) {
        if (selectedMenuSection == "Tous") establishment.menuCatalog
        else establishment.menuCatalog.filter { it.categorySection == selectedMenuSection }
    }

    val menuSections = remember(establishment.menuCatalog) {
        listOf("Tous") + establishment.menuCatalog.map { it.categorySection }.distinct()
    }

    Scaffold(
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            actionMessage = "Itinéraire lancé vers ${establishment.name}"
                            onActionTaken(establishment.id, "Itinéraire")
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                    ) {
                        AppIcon(
                            imageVector = Icons.Default.Directions,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary,
                            size = 18.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isMalagasy) "Lalana" else "Itinéraire",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { onOrderRide(establishment) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .weight(1.3f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("detail_order_ride_button")
                    ) {
                        AppIcon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "",
                            tint = Color.White,
                            size = 18.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isMalagasy) "Handeha (Transport)" else "Commander un Transport",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        modifier = modifier.testTag("establishment_detail_screen")
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Visual Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                establishment.category.badgeColor,
                                establishment.category.badgeColor.copy(alpha = 0.8f)
                            )
                        )
                    )
            ) {
                // Background artistic pattern
                AppIcon(
                    imageVector = when (establishment.category) {
                        ServiceCategory.RESTAURANT -> Icons.Default.Restaurant
                        ServiceCategory.HOTEL -> Icons.Default.Hotel
                        ServiceCategory.PHARMACY -> Icons.Default.LocalPharmacy
                        ServiceCategory.SHOP -> Icons.Default.Storefront
                        else -> Icons.Default.Place
                    },
                    contentDescription = "",
                    size = 120.dp,
                    tint = Color.White.copy(alpha = 0.18f),
                    modifier = Modifier.align(Alignment.Center)
                )

                // Top navigation bar buttons (Back, Favorite, Share)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                            .testTag("detail_back_button")
                    ) {
                        AppIcon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { onToggleFavorite(establishment.id) },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                                .testTag("detail_favorite_button")
                        ) {
                            AppIcon(
                                imageVector = if (establishment.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favori",
                                tint = if (establishment.isFavorite) Color.Red else Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { actionMessage = "Lien de l'établissement partagé !" },
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.35f), CircleShape)
                        ) {
                            AppIcon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Partager",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Overlay Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = establishment.category.titleFr,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "·  ${establishment.priceLevel}",
                            style = MaterialTheme.typography.labelSmall.copy(color = AmberSecondaryDark)
                        )
                    }
                }
            }

            // Body content
            Column(modifier = Modifier.padding(16.dp)) {
                // Name & Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = establishment.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    OpenStatusBadge(isOpen = establishment.isOpen)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rating & distance
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingBar(rating = establishment.rating, reviewCount = establishment.reviewCount)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "·  ${establishment.distanceKm} km depuis votre position",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Action Bar: Appeler, Message, Commander Transport
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = {
                            actionMessage = "Appel direct vers ${establishment.phone}"
                            onActionTaken(establishment.id, "Appelé")
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 54.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AppIcon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Appeler",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                size = 20.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Appeler",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Surface(
                        onClick = { actionMessage = "Discussion en direct ouverte avec ${establishment.name}" },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 54.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AppIcon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Message",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                size = 20.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Message",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Surface(
                        onClick = { onOrderRide(establishment) },
                        shape = RoundedCornerShape(12.dp),
                        color = AmberSecondaryContainerLight,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 54.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AppIcon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = "Transport",
                                tint = AmberSecondaryLight,
                                size = 20.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Transport",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                // Action toast notice
                actionMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIcon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary,
                                size = 18.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Full Address & Hours
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppIcon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary,
                                size = 20.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = establishment.fullAddress,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppIcon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary,
                                size = 20.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = establishment.openingHours,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppIcon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary,
                                size = 20.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = establishment.phone,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Description
                Text(
                    text = "À propos",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = establishment.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )

                // -------------------------------------------------------------------------
                // CATALOGUE DES MENUS (RESTAURANT)
                // -------------------------------------------------------------------------
                if (establishment.category == ServiceCategory.RESTAURANT && establishment.menuCatalog.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Catalogue du Menu & Carte",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${establishment.menuCatalog.size} plats & spécialités disponibles",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (orderCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = EmeraldPrimaryContainerLight
                            ) {
                                Text(
                                    text = "$orderCount plat(s) au panier",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimaryLight),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Menu Section Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(menuSections) { sec ->
                            val isSelected = selectedMenuSection == sec
                            Surface(
                                onClick = { selectedMenuSection = sec },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.defaultMinSize(minHeight = 34.dp)
                            ) {
                                Text(
                                    text = sec,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // List of Menu Items
                    filteredMenu.forEach { item ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = item.name,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            item.tag?.let { tag ->
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = when (tag) {
                                                        "Spécialité" -> CategoryRestaurant.copy(alpha = 0.15f)
                                                        "Chef" -> AmberSecondaryContainerLight
                                                        "Bio" -> EmeraldPrimaryContainerLight
                                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                                    }
                                                ) {
                                                    Text(
                                                        text = tag,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = when (tag) {
                                                                "Spécialité" -> CategoryRestaurant
                                                                "Chef" -> AmberSecondaryLight
                                                                "Bio" -> EmeraldPrimaryLight
                                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                                            }
                                                        ),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = item.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 18.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = "${item.priceAr} Ar",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            orderCount++
                                            actionMessage = "« ${item.name} » ajouté à votre commande !"
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        AppIcon(
                                            imageVector = Icons.Default.AddShoppingCart,
                                            contentDescription = "",
                                            size = 16.dp,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Ajouter à la commande",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------------------------------
                // CATALOGUE DES CHAMBRES & SUITES (HÔTEL)
                // -------------------------------------------------------------------------
                if (establishment.category == ServiceCategory.HOTEL && establishment.roomCatalog.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Column {
                        Text(
                            text = "Catalogue des Chambres & Suites",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Hébergements de prestige disponibles avec réservation instantanée",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    establishment.roomCatalog.forEach { room ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = room.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${room.roomType} · ${room.capacity}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = CategoryHotel.copy(alpha = 0.12f)
                                        ) {
                                            Text(
                                                text = room.viewType,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = CategoryHotel
                                                ),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${room.pricePerNightAr} Ar",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                        Text(
                                            text = "par nuit",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Amenities
                                Text(
                                    text = "Équipements & Prestations :",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    room.amenities.forEach { am ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            AppIcon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "",
                                                size = 14.dp,
                                                tint = EmeraldPrimaryLight
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = am, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                AppButton(
                                    text = "Réserver cette chambre",
                                    onClick = { selectedRoomForBooking = room },
                                    leadingIcon = Icons.Default.Hotel,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Services & Equipments
                Text(
                    text = "Services proposés",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    establishment.services.take(4).forEach { srv ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppIcon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "",
                                    size = 14.dp,
                                    tint = EmeraldPrimaryLight
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = srv,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Reviews Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Avis clients",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = { showReviewDialog = true }) {
                        Text("Donner mon avis")
                    }
                }

                if (establishment.reviews.isEmpty()) {
                    Text(
                        text = "Soyez le premier à donner votre avis !",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    establishment.reviews.forEach { rev ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = rev.authorName,
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = rev.date,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                RatingBar(rating = rev.rating)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = rev.comment,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Hotel Room Booking Dialog
    selectedRoomForBooking?.let { room ->
        AlertDialog(
            onDismissRequest = { selectedRoomForBooking = null },
            icon = {
                AppIcon(
                    imageVector = Icons.Default.Bed,
                    contentDescription = "",
                    size = 36.dp,
                    tint = CategoryHotel
                )
            },
            title = {
                Text(
                    text = "Réservation · ${room.name}",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            },
            text = {
                Column {
                    Text("Tarif par nuit : ${room.pricePerNightAr} Ar")
                    Text("Vue : ${room.viewType}")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Nombre de nuits :")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (bookingNights > 1) bookingNights-- },
                            enabled = bookingNights > 1
                        ) {
                            AppIcon(imageVector = Icons.Default.Remove, contentDescription = "Moins")
                        }
                        Text(
                            text = "$bookingNights nuit(s)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        IconButton(onClick = { bookingNights++ }) {
                            AppIcon(imageVector = Icons.Default.Add, contentDescription = "Plus")
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 10.dp))
                    Text(
                        text = "Total séjour : ${room.pricePerNightAr * bookingNights} Ar",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            },
            confirmButton = {
                AppButton(
                    text = "Confirmer la réservation",
                    onClick = {
                        actionMessage = "Réservation confirmée pour ${room.name} (${bookingNights} nuit(s)) !"
                        selectedRoomForBooking = null
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { selectedRoomForBooking = null }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Review Dialog
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Votre avis sur ${establishment.name}") },
            text = {
                Column {
                    Row {
                        (1..5).forEach { star ->
                            IconButton(onClick = { newReviewRating = star }) {
                                AppIcon(
                                    imageVector = if (star <= newReviewRating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "$star",
                                    tint = AmberSecondaryLight
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    AppInput(
                        value = newReviewComment,
                        onValueChange = { newReviewComment = it },
                        label = "Partagez votre expérience",
                        leadingIcon = Icons.Default.RateReview
                    )
                }
            },
            confirmButton = {
                AppButton(
                    text = "Publier",
                    onClick = {
                        actionMessage = "Merci ! Votre avis a été publié."
                        showReviewDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
