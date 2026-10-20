package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.components.AppButton
import com.example.ui.components.AppCard
import com.example.ui.components.AppIcon
import com.example.ui.components.AppInput
import com.example.ui.components.AppOutlinedButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: (UserRole) -> Unit,
    onContinueAsGuest: () -> Unit
) {
    var isLoginTab by remember { mutableStateOf(true) }
    var isOtpStep by remember { mutableStateOf(false) }

    var phoneNumber by remember { mutableStateOf("34 12 345 67") }
    var email by remember { mutableStateOf("rakoto@gmail.com") }
    var password by remember { mutableStateOf("••••••••") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("Andry Rakotomalala") }
    var city by remember { mutableStateOf("Antananarivo (Analakely)") }
    var selectedRole by remember { mutableStateOf(UserRole.CLIENT) }
    var otpCode by remember { mutableStateOf("4829") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Guide Me",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    TextButton(
                        onClick = onContinueAsGuest,
                        modifier = Modifier.testTag("auth_guest_button")
                    ) {
                        Text("Accès invité")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            if (!isOtpStep) {
                // Tab Row for Connexion / Inscription
                PrimaryTabRow(
                    selectedTabIndex = if (isLoginTab) 0 else 1,
                    containerColor = Color.Transparent,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Tab(
                        selected = isLoginTab,
                        onClick = { isLoginTab = true },
                        text = {
                            Text(
                                "Connexion",
                                fontWeight = if (isLoginTab) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = !isLoginTab,
                        onClick = { isLoginTab = false },
                        text = {
                            Text(
                                "Inscription",
                                fontWeight = if (!isLoginTab) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }

                Text(
                    text = if (isLoginTab) "Bienvenue sur Guide Me" else "Créez votre compte",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isLoginTab)
                        "Accédez aux services de proximité et transports au juste prix"
                    else "Rejoignez le réseau local de Madagascar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                // Role Selector
                Text(
                    text = "Votre profil :",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UserRole.values().forEach { role ->
                        val isSelected = selectedRole == role
                        val roleIcon = when (role) {
                            UserRole.CLIENT -> Icons.Default.Person
                            UserRole.DRIVER -> Icons.Default.DirectionsCar
                            UserRole.MERCHANT -> Icons.Default.Storefront
                        }
                        val shortLabel = when (role) {
                            UserRole.CLIENT -> "Client"
                            UserRole.DRIVER -> "Chauffeur"
                            UserRole.MERCHANT -> "Commerce"
                        }
                        Surface(
                            onClick = { selectedRole = role },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 48.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AppIcon(
                                    imageVector = roleIcon,
                                    contentDescription = shortLabel,
                                    size = 20.dp,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = shortLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (!isLoginTab) {
                    AppInput(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Nom complet",
                        leadingIcon = Icons.Default.Badge,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    AppInput(
                        value = city,
                        onValueChange = { city = it },
                        label = "Ville / Quartier",
                        leadingIcon = Icons.Default.LocationCity,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Phone number (+261 Madagascar format)
                Text(
                    text = "Numéro de téléphone (Madagascar +261)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .height(56.dp)
                            .padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIcon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = "Drapeau Madagascar",
                                size = 20.dp,
                                tint = EmeraldPrimaryLight
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+261",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    AppInput(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = "Ex: 34 12 345 67",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Email optionnel
                AppInput(
                    value = email,
                    onValueChange = { email = it },
                    label = "Adresse email (optionnel)",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Password
                AppInput(
                    value = password,
                    onValueChange = { password = it },
                    label = "Mot de passe",
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            AppIcon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Afficher mot de passe",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    },
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                AppButton(
                    text = if (isLoginTab) "Se connecter (SMS OTP)" else "S'inscrire et recevoir OTP",
                    onClick = { isOtpStep = true },
                    leadingIcon = Icons.Default.Send,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "auth_submit_button"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Google Sign-In button
                AppOutlinedButton(
                    text = "Continuer avec Google",
                    onClick = { onAuthSuccess(selectedRole) },
                    leadingIcon = Icons.Default.AccountCircle,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "auth_google_button"
                )
            } else {
                // OTP Verification Step
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        color = EmeraldPrimaryContainerLight
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            AppIcon(
                                imageVector = Icons.Default.Message,
                                contentDescription = "SMS OTP",
                                size = 36.dp,
                                tint = EmeraldPrimaryLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Vérification OTP",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Un code de confirmation par SMS a été envoyé au\n+261 $phoneNumber",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AppInput(
                        value = otpCode,
                        onValueChange = { otpCode = it },
                        label = "Code à 4 chiffres",
                        leadingIcon = Icons.Default.Security,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(220.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AppButton(
                        text = "Valider et Accéder",
                        onClick = { onAuthSuccess(selectedRole) },
                        leadingIcon = Icons.Default.CheckCircle,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "auth_validate_otp_button"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { isOtpStep = false }
                    ) {
                        Text("Modifier le numéro")
                    }
                }
            }
        }
    }
}
