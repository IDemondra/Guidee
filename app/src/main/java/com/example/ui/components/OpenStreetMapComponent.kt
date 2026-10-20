package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.Establishment
import com.example.ui.theme.AmberSecondaryLight
import com.example.ui.theme.EmeraldPrimaryLight
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

@Composable
fun OpenStreetMapComponent(
    establishments: List<Establishment>,
    selectedRadiusKm: Double,
    selectedEstablishment: Establishment?,
    onEstablishmentClick: (Establishment) -> Unit,
    recenterTrigger: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val centerLat = -18.9080
    val centerLng = 47.5260
    val centerPoint = remember { GeoPoint(centerLat, centerLng) }

    // Remember MapView instance
    var mapViewInstance by remember { mutableStateOf<MapView?>(null) }

    // Initialize OSMDroid config once
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("osmdroid_prefs", Context.MODE_PRIVATE)
        Configuration.getInstance().load(context, prefs)
        Configuration.getInstance().userAgentValue = "GuideMe/1.0 (com.guide.me; Android)"
    }

    // Handle Recenter trigger
    LaunchedEffect(recenterTrigger) {
        if (recenterTrigger > 0) {
            mapViewInstance?.controller?.animateTo(centerPoint, 15.0, 600L)
        }
    }

    // Handle selected establishment camera animation
    LaunchedEffect(selectedEstablishment) {
        selectedEstablishment?.let { est ->
            mapViewInstance?.controller?.animateTo(GeoPoint(est.lat, est.lng), 16.5, 600L)
        }
    }

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                isTilesScaledToDpi = true
                controller.setZoom(15.0)
                controller.setCenter(centerPoint)
                mapViewInstance = this
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            // 1. Draw Search Radius Circle (OpenStreetMap Polygon circle)
            val radiusMeters = selectedRadiusKm * 1000.0
            val circlePoints = Polygon.pointsAsCircle(centerPoint, radiusMeters)
            val circleOverlay = Polygon(mapView).apply {
                points = circlePoints
                fillPaint.color = android.graphics.Color.argb(35, 0, 108, 76)
                outlinePaint.color = android.graphics.Color.argb(160, 0, 108, 76)
                outlinePaint.strokeWidth = 3f
            }
            mapView.overlays.add(circleOverlay)

            // 2. User GPS Location Marker (Analakely)
            val userMarker = Marker(mapView).apply {
                position = centerPoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                title = "Votre position"
                snippet = "Analakely, Antananarivo"
                icon = createUserLocationIcon(context)
            }
            mapView.overlays.add(userMarker)

            // 3. Add Establishment Markers
            establishments.forEach { est ->
                val isSelected = selectedEstablishment?.id == est.id
                val estMarker = Marker(mapView).apply {
                    position = GeoPoint(est.lat, est.lng)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = est.name
                    snippet = "${est.category.titleFr} · ${est.shortAddress}"
                    icon = createCategoryMarkerIcon(context, est, isSelected)
                    setOnMarkerClickListener { _, _ ->
                        onEstablishmentClick(est)
                        true
                    }
                }
                mapView.overlays.add(estMarker)
            }

            mapView.invalidate()
        },
        modifier = modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        onDispose {
            mapViewInstance?.onDetach()
        }
    }
}

/**
 * Creates custom user GPS pin for OpenStreetMap
 */
private fun createUserLocationIcon(context: Context): Drawable {
    val size = 56
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.argb(60, 25, 118, 210)
        style = Paint.Style.FILL
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, wavePaint)

    val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
    }
    canvas.drawCircle(size / 2f, size / 2f, 16f, whitePaint)

    val bluePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(25, 118, 210)
        style = Paint.Style.FILL
    }
    canvas.drawCircle(size / 2f, size / 2f, 12f, bluePaint)

    return BitmapDrawable(context.resources, bitmap)
}

/**
 * Creates category pin for OpenStreetMap markers
 */
private fun createCategoryMarkerIcon(
    context: Context,
    establishment: Establishment,
    isSelected: Boolean
): Drawable {
    val width = if (isSelected) 56 else 44
    val height = if (isSelected) 72 else 58
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val categoryColor = establishment.category.badgeColor.toArgb()

    // Outer shadow / pin pointer
    val pinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = categoryColor
        style = Paint.Style.FILL
    }

    val headRadius = width / 2.3f
    val centerX = width / 2f
    val centerY = headRadius + 4f

    // Pin Head
    canvas.drawCircle(centerX, centerY, headRadius, pinPaint)

    // Pin Tip triangle
    val path = android.graphics.Path().apply {
        moveTo(centerX - headRadius * 0.7f, centerY + headRadius * 0.3f)
        lineTo(centerX, height - 2f)
        lineTo(centerX + headRadius * 0.7f, centerY + headRadius * 0.3f)
        close()
    }
    canvas.drawPath(path, pinPaint)

    // White Inner Circle
    val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
    }
    canvas.drawCircle(centerX, centerY, headRadius * 0.65f, whitePaint)

    // Selection ring
    if (isSelected) {
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AmberSecondaryLight.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        canvas.drawCircle(centerX, centerY, headRadius + 2f, strokePaint)
    }

    return BitmapDrawable(context.resources, bitmap)
}
