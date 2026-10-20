package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppButton
import com.example.ui.components.AppIcon
import com.example.ui.components.AppOutlinedButton
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1800)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(EmeraldPrimaryLight, Color(0xFF004D36), Color(0xFF00291D))
                )
            )
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(96.dp),
                shape = RoundedCornerShape(26.dp),
                color = Color.White.copy(alpha = 0.15f),
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    AppIcon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "Guide Me Logo",
                        size = 52.dp,
                        tint = AmberSecondaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Guide Me",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Services de proximité & Transport",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Madagascar · Antananarivo",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 2.sp
                ),
                color = AmberSecondaryDark
            )
        }
    }
}

data class OnboardingStep(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconBgColor: Color
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val steps = listOf(
        OnboardingStep(
            title = "Découvrez tous les services autour de vous",
            description = "Explorez restaurants, hôtels, pharmacies de garde et commerces sur une carte interactive en temps réel à Madagascar.",
            icon = Icons.Default.Explore,
            iconBgColor = EmeraldPrimaryLight
        ),
        OnboardingStep(
            title = "Commandez un transport au juste prix",
            description = "Proposez votre propre tarif en Ariary pour votre course ou livraison et négociez équitablement avec les chauffeurs.",
            icon = Icons.Default.DirectionsCar,
            iconBgColor = AmberSecondaryLight
        ),
        OnboardingStep(
            title = "Négociez & Payez en toute simplicité",
            description = "Recevez les offres des chauffeurs, négociez en direct et réglez facilement en Espèces, MVola ou Orange Money.",
            icon = Icons.Default.PriceCheck,
            iconBgColor = CyanTertiaryLight
        ),
        OnboardingStep(
            title = "Activez votre localisation GPS",
            description = "Permettez à Guide Me de localiser les services, menus, hôtels et chauffeurs les plus proches de votre position avec précision.",
            icon = Icons.Default.LocationOn,
            iconBgColor = EmeraldPrimaryLight
        )
    )

    val pagerState = rememberPagerState { steps.size }
    val scope = rememberCoroutineScope()
    var showPermissionDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("onboarding_screen")
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (pagerState.currentPage < steps.size - 1) {
                    TextButton(
                        onClick = onFinished,
                        modifier = Modifier.testTag("onboarding_skip_button")
                    ) {
                        Text(
                            text = "Passer",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val step = steps[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = RoundedCornerShape(32.dp),
                        color = step.iconBgColor.copy(alpha = 0.12f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            AppIcon(
                                imageVector = step.icon,
                                contentDescription = step.title,
                                size = 64.dp,
                                tint = step.iconBgColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )
                }
            }

            // Indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                repeat(steps.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .height(8.dp)
                            .width(if (isSelected) 28.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            // Bottom action buttons
            if (pagerState.currentPage < steps.size - 1) {
                AppButton(
                    text = "Continuer",
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    trailingIcon = Icons.Default.ArrowForward,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "onboarding_next_button"
                )
            } else {
                AppButton(
                    text = "Activer le GPS & Démarrer",
                    onClick = { showPermissionDialog = true },
                    leadingIcon = Icons.Default.MyLocation,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "onboarding_start_button"
                )
            }
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = {
                showPermissionDialog = false
                onFinished()
            },
            icon = {
                AppIcon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Permission GPS",
                    tint = EmeraldPrimaryLight,
                    size = 36.dp
                )
            },
            title = {
                Text(
                    text = "Autorisation de localisation",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Guide Me a besoin de votre position GPS pour afficher les restaurants, hôtels, commerces, pharmacies de garde et chauffeurs disponibles autour de vous à Madagascar.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                AppButton(
                    text = "Autoriser",
                    onClick = {
                        showPermissionDialog = false
                        onFinished()
                    },
                    testTag = "permission_allow_button"
                )
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        onFinished()
                    }
                ) {
                    Text("Plus tard")
                }
            }
        )
    }
}
