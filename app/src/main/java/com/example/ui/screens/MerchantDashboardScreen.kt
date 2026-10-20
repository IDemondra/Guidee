package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantDashboardScreen(
    modifier: Modifier = Modifier
) {
    var businessName by remember { mutableStateOf("Café & Grill Le Zébu d'Or") }
    var businessCategory by remember { mutableStateOf("Restaurants & Cafés") }
    var businessAddress by remember { mutableStateOf("12 Rue Rainitovo, Antsahavola, Antananarivo") }
    var businessPhone by remember { mutableStateOf("+261 34 88 990 12") }
    var isOpenNow by remember { mutableStateOf(true) }
    var openingHours by remember { mutableStateOf("07:30 - 21:30 (Tous les jours)") }
    var showEditDialog by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Espace Partenaire Commerce",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
        },
        modifier = modifier.testTag("merchant_dashboard_screen")
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 80.dp)
        ) {
            // Profile & Status Header
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
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
                                text = businessName,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = businessCategory,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Open/Closed Toggle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isOpenNow) "Ouvert" else "Fermé",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isOpenNow) EmeraldPrimaryLight else MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = isOpenNow,
                                onCheckedChange = { isOpenNow = it }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = businessAddress,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showEditDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AppIcon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            size = 18.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Modifier ma fiche établissement",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            statusMessage?.let { msg ->
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
                            imageVector = Icons.Default.Check,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(msg, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Statistics (Vues, Appels, Itinéraires, Favoris)
            Text(
                text = "Statistiques de performance (Ce mois)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Vues
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        AppIcon(imageVector = Icons.Default.Visibility, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("1 420", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        Text("Vues sur la carte", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Appels
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        AppIcon(imageVector = Icons.Default.Phone, contentDescription = "", tint = AmberSecondaryLight)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("84", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        Text("Appels clients", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Itinéraires
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        AppIcon(imageVector = Icons.Default.Directions, contentDescription = "", tint = CyanTertiaryLight)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("192", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        Text("Itinéraires & Courses", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Favoris
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        AppIcon(imageVector = Icons.Default.Favorite, contentDescription = "", tint = Color.Red)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("65", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        Text("Ajouts aux favoris", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Services & Tarifs Management
            Text(
                text = "Services & Tarifs proposés",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            listOf(
                Pair("Menu Zébu du Terroir", "18 000 Ar"),
                Pair("Café Malagasy & Mofo Gasy", "4 000 Ar"),
                Pair("Plateau Dégustation Royale", "32 000 Ar"),
                Pair("Livraison à domicile express", "5 000 Ar")
            ).forEach { (srv, price) ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(srv, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                        Text(price, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Modifier la fiche établissement") },
            text = {
                Column {
                    AppInput(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = "Nom du commerce"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppInput(
                        value = businessPhone,
                        onValueChange = { businessPhone = it },
                        label = "Téléphone de contact"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppInput(
                        value = openingHours,
                        onValueChange = { openingHours = it },
                        label = "Horaires d'ouverture"
                    )
                }
            },
            confirmButton = {
                AppButton(
                    text = "Enregistrer",
                    onClick = {
                        statusMessage = "Fiche mise à jour et synchronisée avec la carte !"
                        showEditDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
