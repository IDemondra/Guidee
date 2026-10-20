package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.model.Establishment
import com.example.model.ServiceCategory
import com.example.ui.theme.*
import kotlin.math.sqrt

@Composable
fun InteractiveMapCanvas(
    establishments: List<Establishment>,
    selectedRadiusKm: Double,
    selectedEstablishment: Establishment?,
    onEstablishmentClick: (Establishment) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseProgress"
    )

    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val isDark = MaterialTheme.colorScheme.background == BackgroundDark

    // Base coordinate reference for Antananarivo center (Analakely)
    val centerLat = -18.9080
    val centerLng = 47.5260

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(establishments, selectedEstablishment) {
                detectTapGestures { tapOffset ->
                    val w = size.width.toFloat()
                    val h = size.height.toFloat()

                    // Check which pin was clicked
                    var closestEst: Establishment? = null
                    var minDistance = Float.MAX_VALUE

                    establishments.forEach { est ->
                        val pinPos = latLngToScreen(est.lat, est.lng, centerLat, centerLng, w, h)
                        val dx = tapOffset.x - pinPos.x
                        val dy = tapOffset.y - pinPos.y
                        val dist = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                        if (dist < 50f && dist < minDistance) {
                            minDistance = dist
                            closestEst = est
                        }
                    }

                    if (closestEst != null) {
                        onEstablishmentClick(closestEst!!)
                    }
                }
            }
    ) {
        val w = size.width
        val h = size.height

        // 1. Draw Map Background (Roads, Lac Anosy, Hills)
        drawAntananarivoMap(w, h, isDark)

        // 2. User GPS position (Analakely)
        val userScreenPos = Offset(w * 0.5f, h * 0.52f)

        // Draw Search Radius Circle (500m to 10km mapped to canvas pixels)
        val radiusPx = (selectedRadiusKm / 5.0 * (w * 0.42f)).toFloat().coerceIn(60f, w * 0.46f)

        // Translucent radius coverage area
        drawCircle(
            color = EmeraldPrimaryLight.copy(alpha = 0.08f),
            radius = radiusPx,
            center = userScreenPos
        )
        drawCircle(
            color = EmeraldPrimaryLight.copy(alpha = 0.35f),
            radius = radiusPx,
            center = userScreenPos,
            style = Stroke(width = 2f)
        )

        // Pulsing radar wave from user GPS
        val currentPulseRadius = 20f + (pulseProgress * 55f)
        drawCircle(
            color = EmeraldPrimaryLight.copy(alpha = (1f - pulseProgress) * 0.6f),
            radius = currentPulseRadius,
            center = userScreenPos
        )

        // User GPS Pin (Blue dot with white ring)
        drawCircle(
            color = Color.White,
            radius = 12f,
            center = userScreenPos
        )
        drawCircle(
            color = Color(0xFF1976D2),
            radius = 9f,
            center = userScreenPos
        )

        // 3. Draw Category Pins
        establishments.forEach { est ->
            val pinPos = latLngToScreen(est.lat, est.lng, centerLat, centerLng, w, h)
            val isSelected = selectedEstablishment?.id == est.id
            drawEstablishmentPin(
                position = pinPos,
                establishment = est,
                isSelected = isSelected
            )
        }
    }
}

private fun latLngToScreen(
    lat: Double,
    lng: Double,
    centerLat: Double,
    centerLng: Double,
    screenWidth: Float,
    screenHeight: Float
): Offset {
    val latDelta = (lat - centerLat)
    val lngDelta = (lng - centerLng)

    val scaleX = screenWidth * 16.0
    val scaleY = screenHeight * 14.0

    val x = (screenWidth * 0.5f) + (lngDelta * scaleX).toFloat()
    val y = (screenHeight * 0.52f) - (latDelta * scaleY).toFloat()

    return Offset(
        x = x.coerceIn(30f, screenWidth - 30f),
        y = y.coerceIn(40f, screenHeight - 40f)
    )
}

private fun DrawScope.drawAntananarivoMap(w: Float, h: Float, isDark: Boolean) {
    val bgMap = if (isDark) Color(0xFF1A231E) else Color(0xFFEBF1ED)
    val roadColor = if (isDark) Color(0xFF2C3932) else Color(0xFFFFFFFF)
    val highwayColor = if (isDark) Color(0xFF3E4F47) else Color(0xFFFDE8D0)
    val waterColor = if (isDark) Color(0xFF13323B) else Color(0xFFBCE3EB)
    val parkColor = if (isDark) Color(0xFF1E3326) else Color(0xFFD4E8DC)

    // Base background
    drawRect(color = bgMap)

    // Parks (Mahamasina, Anosy green belt)
    drawOval(
        color = parkColor,
        topLeft = Offset(w * 0.35f, h * 0.65f),
        size = Size(w * 0.3f, h * 0.18f)
    )
    drawRect(
        color = parkColor,
        topLeft = Offset(w * 0.72f, h * 0.35f),
        size = Size(w * 0.22f, h * 0.25f)
    )

    // Lac Anosy (Heart-shaped body of water south of Analakely)
    val lakePath = Path().apply {
        moveTo(w * 0.45f, h * 0.70f)
        cubicTo(w * 0.38f, h * 0.66f, w * 0.38f, h * 0.76f, w * 0.45f, h * 0.82f)
        cubicTo(w * 0.52f, h * 0.76f, w * 0.52f, h * 0.66f, w * 0.45f, h * 0.70f)
        close()
    }
    drawPath(path = lakePath, color = waterColor)

    // Lac Behoririka (Northeast water)
    drawOval(
        color = waterColor,
        topLeft = Offset(w * 0.58f, h * 0.28f),
        size = Size(w * 0.18f, h * 0.10f)
    )

    // Major Road Network: Avenue de l'Indépendance (Avenue Soarano -> Analakely)
    drawLine(
        color = highwayColor,
        start = Offset(w * 0.50f, h * 0.15f),
        end = Offset(w * 0.50f, h * 0.62f),
        strokeWidth = 14f
    )

    // Boulevard de l'Europe / Route des Hydrocarbures (Ankorondrano)
    drawLine(
        color = highwayColor,
        start = Offset(w * 0.15f, h * 0.25f),
        end = Offset(w * 0.85f, h * 0.20f),
        strokeWidth = 10f
    )

    // Ring roads & Secondary streets
    val streetStrokes = listOf(
        Pair(Offset(w * 0.2f, h * 0.45f), Offset(w * 0.8f, h * 0.48f)),
        Pair(Offset(w * 0.3f, h * 0.35f), Offset(w * 0.5f, h * 0.62f)),
        Pair(Offset(w * 0.5f, h * 0.62f), Offset(w * 0.45f, h * 0.88f)),
        Pair(Offset(w * 0.5f, h * 0.62f), Offset(w * 0.75f, h * 0.75f)),
        Pair(Offset(w * 0.2f, h * 0.80f), Offset(w * 0.85f, h * 0.85f)),
        Pair(Offset(w * 0.25f, h * 0.15f), Offset(w * 0.35f, h * 0.65f)),
        Pair(Offset(w * 0.65f, h * 0.20f), Offset(w * 0.68f, h * 0.85f))
    )

    streetStrokes.forEach { (start, end) ->
        drawLine(
            color = roadColor,
            start = start,
            end = end,
            strokeWidth = 6f
        )
    }
}

private fun DrawScope.drawEstablishmentPin(
    position: Offset,
    establishment: Establishment,
    isSelected: Boolean
) {
    val pinColor = establishment.category.badgeColor
    val pinRadius = if (isSelected) 18f else 14f

    // Shadow
    drawCircle(
        color = Color(0x33000000),
        radius = pinRadius + 3f,
        center = position.copy(y = position.y + 4f)
    )

    // Outer circle
    drawCircle(
        color = Color.White,
        radius = pinRadius + 2f,
        center = position
    )

    // Inner category color
    drawCircle(
        color = pinColor,
        radius = pinRadius,
        center = position
    )

    // Inner symbol dot
    drawCircle(
        color = Color.White,
        radius = pinRadius * 0.42f,
        center = position
    )

    // Selection ring
    if (isSelected) {
        drawCircle(
            color = AmberSecondaryLight,
            radius = pinRadius + 7f,
            center = position,
            style = Stroke(width = 3.5f)
        )
    }
}
