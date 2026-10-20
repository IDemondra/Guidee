package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VehicleType
import com.example.ui.components.*
import com.example.ui.theme.*

data class IncomingPassengerRequest(
    val id: String,
    val passengerName: String,
    val rating: Float,
    val pickup: String,
    val destination: String,
    val passengerPriceAr: Int,
    val distanceKm: Double,
    val vehicleType: VehicleType
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    isOnline: Boolean,
    onToggleOnline: () -> Unit,
    todayEarningsAr: Int,
    completedTrips: Int,
    modifier: Modifier = Modifier
) {
    var requests by remember {
        mutableStateOf(
            listOf(
                IncomingPassengerRequest(
                    id = "req-1",
                    passengerName = "Mamy R.",
                    rating = 4.9f,
                    pickup = "Avenue de l'Indépendance (Analakely)",
                    destination = "Ankorondrano (Zone Galaxy)",
                    passengerPriceAr = 6000,
                    distanceKm = 1.8,
                    vehicleType = VehicleType.CAR
                ),
                IncomingPassengerRequest(
                    id = "req-2",
                    passengerName = "Soa Rasoanaivo",
                    rating = 5.0f,
                    pickup = "Gare Soarano",
                    destination = "Hôtel Carlton Anosy",
                    passengerPriceAr = 7500,
                    distanceKm = 2.2,
                    vehicleType = VehicleType.CAR
                ),
                IncomingPassengerRequest(
                    id = "req-3",
                    passengerName = "Tahina K.",
                    rating = 4.8f,
                    pickup = "Isoraka centre",
                    destination = "Mahamasina Stade",
                    passengerPriceAr = 5000,
                    distanceKm = 1.1,
                    vehicleType = VehicleType.CAR
                )
            )
        )
    }

    var showWithdrawDialog by remember { mutableStateOf(false) }
    var notificationFeedback by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (isOnline) EmeraldPrimaryLight else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(10.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mode Chauffeur Guide Ride",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = if (isOnline) "En ligne" else "Hors ligne",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isOnline) EmeraldPrimaryLight else MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = isOnline,
                            onCheckedChange = { onToggleOnline() }
                        )
                    }
                }
            )
        },
        modifier = modifier.testTag("driver_dashboard_screen")
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 80.dp)
        ) {
            // Stats Row: Gains, Courses, Note
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Gains du jour
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1.2f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppIcon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "",
                                size = 18.dp,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Gains du jour",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$todayEarningsAr Ar",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { showWithdrawDialog = true },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Retirer (MVola) >",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Courses & Note
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Courses du jour", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = "$completedTrips terminées",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = AmberSecondaryContainerLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Note chauffeur", style = MaterialTheme.typography.labelSmall)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AppIcon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "",
                                    size = 18.dp,
                                    tint = AmberSecondaryLight
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "4.9 / 5.0",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AmberSecondaryLight
                                    )
                                )
                            }
                        }
                    }
                }
            }

            notificationFeedback?.let { msg ->
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIcon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = msg, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Incoming Passenger Requests Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Demandes de courses entrantes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isOnline) EmeraldPrimaryContainerLight else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (isOnline) "${requests.size} disponibles" else "Hors ligne",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!isOnline) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AppIcon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "",
                            size = 40.dp,
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Vous êtes actuellement hors ligne",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Activez votre statut en ligne pour recevoir les propositions de courses des passagers autour de vous.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                requests.forEach { req ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            AppIcon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "",
                                                size = 20.dp,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = req.passengerName,
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                        )
                                        RatingBar(rating = req.rating)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = AmberSecondaryContainerLight
                                ) {
                                    Text(
                                        text = "${req.passengerPriceAr} Ar",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = AmberSecondaryLight
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(EmeraldPrimaryLight, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = req.pickup, style = MaterialTheme.typography.bodySmall)
                                }
                                Box(
                                    modifier = Modifier
                                        .padding(start = 3.dp)
                                        .height(16.dp)
                                        .width(2.dp)
                                        .background(MaterialTheme.colorScheme.outline)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(AmberSecondaryLight, RoundedCornerShape(1.dp))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = req.destination, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Actions Chauffeur : Accepter, Négocier (+1000), Refuser
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        requests = requests.filter { it.id != req.id }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Refuser")
                                }

                                Button(
                                    onClick = {
                                        notificationFeedback = "Contre-offre de ${req.passengerPriceAr + 1000} Ar envoyée à ${req.passengerName}."
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondaryLight),
                                    modifier = Modifier.weight(1.2f)
                                ) {
                                    Text("Proposer +1000")
                                }

                                Button(
                                    onClick = {
                                        requests = requests.filter { it.id != req.id }
                                        notificationFeedback = "Course acceptée ! Dirigez-vous vers ${req.pickup}."
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryLight),
                                    modifier = Modifier.weight(1.2f)
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

    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = { Text("Retrait des gains vers Mobile Money") },
            text = {
                Column {
                    Text("Solde disponible : $todayEarningsAr Ar")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Numéro Telma MVola associé : +261 34 11 223 34")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Transfert instantané sans frais de retrait partenaire.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                AppButton(
                    text = "Transférer $todayEarningsAr Ar",
                    onClick = {
                        notificationFeedback = "Retrait de $todayEarningsAr Ar effectué sur votre compte MVola."
                        showWithdrawDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
