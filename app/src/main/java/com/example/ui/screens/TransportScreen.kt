package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreen(
    rideStatus: RideStatus,
    activeRide: RideRequest?,
    receivedOffers: List<DriverOffer>,
    acceptedDriver: DriverOffer?,
    onRequestRide: (RideRequest) -> Unit,
    onPopulateOffers: () -> Unit,
    onAcceptDriver: (DriverOffer) -> Unit,
    onCounterOffer: (String, Int) -> Unit,
    onUpdateStatus: (RideStatus) -> Unit,
    onCompleteRide: (PaymentMethod, Int) -> Unit,
    onResetRide: () -> Unit,
    presetDestination: String? = null,
    isMalagasy: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    // Form state
    var pickupLocation by remember { mutableStateOf("Analakely (Avenue de l'Indépendance)") }
    var destinationLocation by remember(presetDestination) {
        mutableStateOf(presetDestination ?: "Hôtel Carlton Madagascar (Anosy)")
    }
    var selectedVehicle by remember { mutableStateOf(VehicleType.CAR) }
    var proposedPrice by remember { mutableStateOf(7000) }
    var passengerCount by remember { mutableStateOf(1) }
    var hasAC by remember { mutableStateOf(false) }
    var hasLuggage by remember { mutableStateOf(false) }
    var noteToDriver by remember { mutableStateOf("") }

    // Negotiation state
    var negotiatingOffer by remember { mutableStateOf<DriverOffer?>(null) }
    var counterPriceInput by remember { mutableStateOf("6500") }

    // Final payment state
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.MVOLA) }
    var givenRating by remember { mutableStateOf(5) }
    var feedbackComment by remember { mutableStateOf("Chauffeur très prudent et ponctuel.") }
    var showReceiptDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showSosDialog by remember { mutableStateOf(false) }

    // Auto trigger driver offers simulation when SEARCHING
    LaunchedEffect(rideStatus) {
        if (rideStatus == RideStatus.SEARCHING) {
            delay(2200)
            onPopulateOffers()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AmberSecondaryLight,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "Guide Ride",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = if (isMalagasy) "Fitaterana & Dia" else "Transport & Courses",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                actions = {
                    if (rideStatus != RideStatus.IDLE && rideStatus != RideStatus.COMPLETED) {
                        IconButton(onClick = { showCancelDialog = true }) {
                            AppIcon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Annuler la course",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier.testTag("transport_screen")
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (rideStatus) {
                RideStatus.IDLE -> {
                    // 6.1 Request Ride Form
                    RequestRideForm(
                        pickup = pickupLocation,
                        onPickupChange = { pickupLocation = it },
                        destination = destinationLocation,
                        onDestinationChange = { destinationLocation = it },
                        selectedVehicle = selectedVehicle,
                        onVehicleSelect = {
                            selectedVehicle = it
                            proposedPrice = it.basePriceEstimate
                        },
                        proposedPrice = proposedPrice,
                        onPriceChange = { proposedPrice = it },
                        passengerCount = passengerCount,
                        onPassengerChange = { passengerCount = it },
                        hasAC = hasAC,
                        onAcToggle = { hasAC = it },
                        hasLuggage = hasLuggage,
                        onLuggageToggle = { hasLuggage = it },
                        note = noteToDriver,
                        onNoteChange = { noteToDriver = it },
                        onSubmit = {
                            onRequestRide(
                                RideRequest(
                                    pickupName = pickupLocation,
                                    destinationName = destinationLocation,
                                    vehicleType = selectedVehicle,
                                    proposedPriceAr = proposedPrice,
                                    passengerCount = passengerCount,
                                    hasAC = hasAC,
                                    hasLuggage = hasLuggage,
                                    note = noteToDriver
                                )
                            )
                        },
                        isMalagasy = isMalagasy
                    )
                }

                RideStatus.SEARCHING -> {
                    // 6.2 Radar Searching Screen
                    RadarSearchingView(
                        request = activeRide,
                        onCancel = { onResetRide() }
                    )
                }

                RideStatus.OFFERS_RECEIVED -> {
                    // 6.2 Driver Offers List (Negotiation & bids)
                    DriverOffersView(
                        offers = receivedOffers,
                        activeRide = activeRide,
                        onAccept = { onAcceptDriver(it) },
                        onNegotiate = { offer ->
                            negotiatingOffer = offer
                            counterPriceInput = offer.offeredPriceAr.toString()
                        },
                        onDecline = { offer ->
                            // Remove from local view
                        }
                    )
                }

                RideStatus.ACCEPTED,
                RideStatus.DRIVER_ON_WAY,
                RideStatus.DRIVER_ARRIVED,
                RideStatus.IN_PROGRESS -> {
                    // 6.3 Live Ride Tracking
                    LiveRideTrackingView(
                        status = rideStatus,
                        driver = acceptedDriver,
                        ride = activeRide,
                        onUpdateStatus = onUpdateStatus,
                        onSos = { showSosDialog = true }
                    )
                }

                RideStatus.COMPLETED -> {
                    // 6.4 Ride Finished & Payment / Rating
                    CompletedRideView(
                        driver = acceptedDriver,
                        ride = activeRide,
                        selectedPayment = selectedPaymentMethod,
                        onSelectPayment = { selectedPaymentMethod = it },
                        rating = givenRating,
                        onRatingChange = { givenRating = it },
                        comment = feedbackComment,
                        onCommentChange = { feedbackComment = it },
                        onShowReceipt = { showReceiptDialog = true },
                        onFinish = { onResetRide() }
                    )
                }

                RideStatus.CANCELLED -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AppIcon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Annulé",
                            size = 64.dp,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Course annulée",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Vous pouvez relancer une nouvelle proposition de prix à tout moment.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        AppButton(
                            text = "Nouvelle course",
                            onClick = onResetRide
                        )
                    }
                }
            }
        }
    }

    // Negotiation Dialog
    negotiatingOffer?.let { offer ->
        AlertDialog(
            onDismissRequest = { negotiatingOffer = null },
            icon = {
                AppIcon(
                    imageVector = Icons.Default.PriceChange,
                    contentDescription = "Négociation",
                    size = 32.dp,
                    tint = AmberSecondaryLight
                )
            },
            title = {
                Text("Contre-proposition à ${offer.driverName}")
            },
            text = {
                Column {
                    Text(
                        text = "Prix demandé par le chauffeur : ${offer.offeredPriceAr} Ar",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AppInput(
                        value = counterPriceInput,
                        onValueChange = { counterPriceInput = it },
                        label = "Votre contre-offre (Ar)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = Icons.Default.AttachMoney
                    )
                }
            },
            confirmButton = {
                AppButton(
                    text = "Envoyer l'offre",
                    onClick = {
                        val parsed = counterPriceInput.toIntOrNull() ?: offer.offeredPriceAr
                        onCounterOffer(offer.id, parsed)
                        negotiatingOffer = null
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { negotiatingOffer = null }) {
                    Text("Annuler")
                }
            }
        )
    }

    // SOS Dialog
    if (showSosDialog) {
        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            icon = {
                AppIcon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "SOS",
                    tint = MaterialTheme.colorScheme.error,
                    size = 36.dp
                )
            },
            title = { Text("Assistance & Urgence Madagascar") },
            text = {
                Text(
                    "En cas d'urgence, Guide Me transmet votre position GPS en temps réel.\n\n" +
                            "• Police Secours : 117\n" +
                            "• Gendarmerie : 119\n" +
                            "• SAMU / Urgences : +261 20 22 235 55"
                )
            },
            confirmButton = {
                Button(
                    onClick = { showSosDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Appeler le 117")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSosDialog = false }) {
                    Text("Fermer")
                }
            }
        )
    }

    // Cancel Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Annuler la course ?") },
            text = { Text("Êtes-vous sûr de vouloir annuler votre demande de transport ?") },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        onResetRide()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirmer l'annulation")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Garder la course")
                }
            }
        )
    }

    // Receipt Dialog
    if (showReceiptDialog) {
        AlertDialog(
            onDismissRequest = { showReceiptDialog = false },
            title = { Text("Reçu de course Guide Me") },
            text = {
                Column {
                    Text("Référence : MP-${System.currentTimeMillis().toString().takeLast(6)}")
                    Text("Date : Aujourd'hui")
                    Text("Départ : ${activeRide?.pickupName ?: "Analakely"}")
                    Text("Arrivée : ${activeRide?.destinationName ?: "Anosy"}")
                    Text("Chauffeur : ${acceptedDriver?.driverName ?: "Jean-Luc R."} (${acceptedDriver?.licensePlate ?: "4521 TAB"})")
                    Text("Paiement : ${selectedPaymentMethod.label}")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "Montant total : ${acceptedDriver?.offeredPriceAr ?: 7000} Ar",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                AppButton(
                    text = "Télécharger (PDF)",
                    onClick = { showReceiptDialog = false }
                )
            }
        )
    }
}

@Composable
fun RequestRideForm(
    pickup: String,
    onPickupChange: (String) -> Unit,
    destination: String,
    onDestinationChange: (String) -> Unit,
    selectedVehicle: VehicleType,
    onVehicleSelect: (VehicleType) -> Unit,
    proposedPrice: Int,
    onPriceChange: (Int) -> Unit,
    passengerCount: Int,
    onPassengerChange: (Int) -> Unit,
    hasAC: Boolean,
    onAcToggle: (Boolean) -> Unit,
    hasLuggage: Boolean,
    onLuggageToggle: (Boolean) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isMalagasy: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        // Route Card (Departure & Destination)
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Departure
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(EmeraldPrimaryLight, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    AppInput(
                        value = pickup,
                        onValueChange = onPickupChange,
                        label = if (isMalagasy) "Toerana fiaingana" else "Point de départ",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Destination
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(AmberSecondaryLight, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    AppInput(
                        value = destination,
                        onValueChange = onDestinationChange,
                        label = if (isMalagasy) "Toerana haleha" else "Destination",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Vehicle Type Selector (Voiture, Moto, Livraison)
        Text(
            text = if (isMalagasy) "Karazana fiara / fitaovam-pitaterana" else "Type de transport",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VehicleType.values().forEach { vehicle ->
                val isSelected = selectedVehicle == vehicle
                val icon = when (vehicle) {
                    VehicleType.CAR -> Icons.Default.DirectionsCar
                    VehicleType.MOTO -> Icons.Default.TwoWheeler
                    VehicleType.DELIVERY -> Icons.Default.LocalShipping
                }
                Surface(
                    onClick = { onVehicleSelect(vehicle) },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                    shadowElevation = 1.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AppIcon(
                            imageVector = icon,
                            contentDescription = vehicle.labelFr,
                            size = 28.dp,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isMalagasy) vehicle.labelMg else vehicle.labelFr,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "~${vehicle.basePriceEstimate} Ar",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Proposed Price Section (Free bidding model)
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = AmberSecondaryContainerLight.copy(alpha = 0.35f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isMalagasy) "Ny tolotra vidinao (Guide Ride)" else "Votre proposition de tarif (Guide Ride)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = AmberSecondaryLight
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AmberSecondaryLight
                    ) {
                        Text(
                            text = "$proposedPrice Ar",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Fourchette recommandée à Tana : ${selectedVehicle.basePriceEstimate - 1500} Ar – ${selectedVehicle.basePriceEstimate + 2500} Ar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Slider(
                    value = proposedPrice.toFloat(),
                    onValueChange = { onPriceChange(it.toInt()) },
                    valueRange = 3000f..25000f,
                    steps = 43,
                    colors = SliderDefaults.colors(
                        thumbColor = AmberSecondaryLight,
                        activeTrackColor = AmberSecondaryLight
                    ),
                    modifier = Modifier.testTag("transport_price_slider")
                )

                // Quick buttons: -1000 Ar, +1000 Ar, +2000 Ar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(-1000, 500, 1000, 2000).forEach { delta ->
                        OutlinedButton(
                            onClick = { onPriceChange((proposedPrice + delta).coerceAtLeast(2000)) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (delta > 0) "+$delta" else "$delta",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Options (Passengers, AC, Luggage)
        Text(
            text = "Options du trajet",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(
                    imageVector = Icons.Default.People,
                    contentDescription = "",
                    size = 20.dp,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nombre de passagers")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { if (passengerCount > 1) onPassengerChange(passengerCount - 1) },
                    enabled = passengerCount > 1
                ) {
                    AppIcon(imageVector = Icons.Default.Remove, contentDescription = "Moins")
                }
                Text(
                    text = passengerCount.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(
                    onClick = { if (passengerCount < 4) onPassengerChange(passengerCount + 1) },
                    enabled = passengerCount < 4
                ) {
                    AppIcon(imageVector = Icons.Default.Add, contentDescription = "Plus")
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(
                    imageVector = Icons.Default.AcUnit,
                    contentDescription = "",
                    size = 20.dp,
                    tint = CyanTertiaryLight
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Climatisation souhaitée")
            }
            Switch(checked = hasAC, onCheckedChange = onAcToggle)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(
                    imageVector = Icons.Default.Luggage,
                    contentDescription = "",
                    size = 20.dp,
                    tint = AmberSecondaryLight
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bagages / Colis volumineux")
            }
            Switch(checked = hasLuggage, onCheckedChange = onLuggageToggle)
        }

        Spacer(modifier = Modifier.height(10.dp))

        AppInput(
            value = note,
            onValueChange = onNoteChange,
            label = "Note au chauffeur (ex: devant la pharmacie)",
            leadingIcon = Icons.Default.EditNote
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppButton(
            text = if (isMalagasy) "Hanome tolotra vidy ($proposedPrice Ar)" else "Proposer mon tarif ($proposedPrice Ar)",
            onClick = onSubmit,
            leadingIcon = Icons.Default.DirectionsCar,
            backgroundColor = EmeraldPrimaryLight,
            modifier = Modifier.fillMaxWidth(),
            testTag = "transport_submit_offer_button"
        )
    }
}

@Composable
fun RadarSearchingView(
    request: RideRequest?,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(110.dp),
            shape = CircleShape,
            color = EmeraldPrimaryContainerLight
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(90.dp),
                    color = EmeraldPrimaryLight,
                    strokeWidth = 4.dp
                )
                AppIcon(
                    imageVector = Icons.Default.Radar,
                    contentDescription = "Radar chauffeurs",
                    size = 46.dp,
                    tint = EmeraldPrimaryLight
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Recherche des chauffeurs à proximité…",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Votre offre de ${request?.proposedPriceAr ?: 7000} Ar a été diffusée aux chauffeurs Guide Ride disponibles autour d'Analakely.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        AppOutlinedButton(
            text = "Annuler la demande",
            onClick = onCancel,
            leadingIcon = Icons.Default.Close
        )
    }
}

@Composable
fun DriverOffersView(
    offers: List<DriverOffer>,
    activeRide: RideRequest?,
    onAccept: (DriverOffer) -> Unit,
    onNegotiate: (DriverOffer) -> Unit,
    onDecline: (DriverOffer) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Offres de chauffeurs reçues",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${offers.size} chauffeurs prêts pour votre trajet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "Votre offre: ${activeRide?.proposedPriceAr ?: 7000} Ar",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(offers) { offer ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("driver_offer_card_${offer.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        AppIcon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = offer.driverName,
                                            size = 24.dp,
                                            tint = Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = offer.driverName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RatingBar(rating = offer.rating)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "· ${offer.tripsCount} courses",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Price offer badge
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (offer.offeredPriceAr <= (activeRide?.proposedPriceAr ?: 7000))
                                    EmeraldPrimaryContainerLight else AmberSecondaryContainerLight
                            ) {
                                Text(
                                    text = "${offer.offeredPriceAr} Ar",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (offer.offeredPriceAr <= (activeRide?.proposedPriceAr ?: 7000))
                                            EmeraldPrimaryLight else AmberSecondaryLight
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${offer.vehicleModel} · ${offer.licensePlate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Arrivée dans ~${offer.distanceEtaMin} min",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actions: Accepter, Négocier, Refuser
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onNegotiate(offer) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Négocier")
                            }

                            Button(
                                onClick = { onAccept(offer) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryLight),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("accept_driver_button_${offer.id}")
                            ) {
                                Text("Accepter")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveRideTrackingView(
    status: RideStatus,
    driver: DriverOffer?,
    ride: RideRequest?,
    onUpdateStatus: (RideStatus) -> Unit,
    onSos: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        // Status Tracker Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        AppIcon(
                            imageVector = when (status) {
                                RideStatus.ACCEPTED, RideStatus.DRIVER_ON_WAY -> Icons.Default.DirectionsCar
                                RideStatus.DRIVER_ARRIVED -> Icons.Default.PinDrop
                                else -> Icons.Default.Navigation
                            },
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = when (status) {
                            RideStatus.ACCEPTED, RideStatus.DRIVER_ON_WAY -> "Chauffeur en route vers vous"
                            RideStatus.DRIVER_ARRIVED -> "Votre chauffeur est arrivé au point de départ"
                            RideStatus.IN_PROGRESS -> "Course en cours vers la destination"
                            else -> "Course active"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = when (status) {
                            RideStatus.ACCEPTED, RideStatus.DRIVER_ON_WAY -> "Arrivée estimée : ~${driver?.distanceEtaMin ?: 3} minutes"
                            RideStatus.DRIVER_ARRIVED -> "Rejoignez la ${driver?.vehicleModel}"
                            else -> "Arrivée dans ~12 minutes"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Simulated Tracking Route Canvas Card
        Card(
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(EmeraldPrimaryLight, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ride?.pickupName ?: "Analakely",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .height(40.dp)
                            .width(2.dp)
                            .background(MaterialTheme.colorScheme.outline)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(AmberSecondaryLight, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ride?.destinationName ?: "Anosy",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }

                // SOS Button (Urgence)
                IconButton(
                    onClick = onSos,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                ) {
                    AppIcon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Bouton SOS",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Driver details
        driver?.let { drv ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = drv.driverName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${drv.vehicleModel} · ${drv.licensePlate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${drv.offeredPriceAr} Ar",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { /* simulated call */ },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier.weight(1f)
                        ) {
                            AppIcon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Appeler chauffeur",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                size = 18.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Appeler",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Button(
                            onClick = { /* simulated message */ },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            AppIcon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Chat chauffeur",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                size = 18.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Message",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Next Step Simulator Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (status) {
                RideStatus.ACCEPTED, RideStatus.DRIVER_ON_WAY -> {
                    AppButton(
                        text = "Simuler : Chauffeur arrivé",
                        onClick = { onUpdateStatus(RideStatus.DRIVER_ARRIVED) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                RideStatus.DRIVER_ARRIVED -> {
                    AppButton(
                        text = "Démarrer la course",
                        onClick = { onUpdateStatus(RideStatus.IN_PROGRESS) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                RideStatus.IN_PROGRESS -> {
                    AppButton(
                        text = "Terminer la course",
                        onClick = { onUpdateStatus(RideStatus.COMPLETED) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun CompletedRideView(
    driver: DriverOffer?,
    ride: RideRequest?,
    selectedPayment: PaymentMethod,
    onSelectPayment: (PaymentMethod) -> Unit,
    rating: Int,
    onRatingChange: (Int) -> Unit,
    comment: String,
    onCommentChange: (String) -> Unit,
    onShowReceipt: () -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(68.dp),
            shape = CircleShape,
            color = EmeraldPrimaryContainerLight
        ) {
            Box(contentAlignment = Alignment.Center) {
                AppIcon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Terminé",
                    size = 40.dp,
                    tint = EmeraldPrimaryLight
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Course terminée avec succès !",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Total à régler : ${driver?.offeredPriceAr ?: 7000} Ar",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Payment Method Picker
        Text(
            text = "Moyen de paiement",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        PaymentMethod.values().forEach { method ->
            val isSelected = selectedPayment == method
            Surface(
                onClick = { onSelectPayment(method) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = method.providerColor,
                            modifier = Modifier.size(24.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = method.label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        )
                    }
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectPayment(method) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Rating
        Text(
            text = "Évaluer votre chauffeur",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.Center) {
            (1..5).forEach { star ->
                IconButton(onClick = { onRatingChange(star) }) {
                    AppIcon(
                        imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "$star étoiles",
                        tint = AmberSecondaryLight,
                        size = 32.dp
                    )
                }
            }
        }

        AppInput(
            value = comment,
            onValueChange = onCommentChange,
            label = "Commentaire (optionnel)",
            leadingIcon = Icons.Default.Comment
        )

        Spacer(modifier = Modifier.height(20.dp))

        AppOutlinedButton(
            text = "Voir le reçu",
            onClick = onShowReceipt,
            leadingIcon = Icons.Default.Receipt,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        AppButton(
            text = "Finaliser",
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth(),
            testTag = "transport_finish_button"
        )
    }
}
