package com.honey.wear.ui.components

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.honey.wear.data.SharedLocation
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions

@Composable
fun PartnerMapView(
    location: SharedLocation,
    partnerName: String,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapView = remember(context) {
        MapLibre.getInstance(context.applicationContext)
        MapView(context).apply { onCreate(null) }
    }
    var map by remember(mapView) { mutableStateOf<MapLibreMap?>(null) }
    var marker by remember(mapView) { mutableStateOf<Marker?>(null) }
    var styleReady by remember(mapView) { mutableStateOf(false) }

    DisposableEffect(mapView) {
        mapView.onStart()
        mapView.onResume()
        mapView.getMapAsync { loadedMap ->
            loadedMap.uiSettings.isAttributionEnabled = true
            loadedMap.uiSettings.isCompassEnabled = false
            loadedMap.uiSettings.isLogoEnabled = false
            map = loadedMap
        }
        onDispose {
            marker?.remove()
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    LaunchedEffect(map, isDarkTheme) {
        val loadedMap = map ?: return@LaunchedEffect
        styleReady = false
        loadedMap.setStyle(if (isDarkTheme) DARK_STYLE else LIGHT_STYLE) {
            styleReady = true
        }
    }

    LaunchedEffect(map, styleReady, location.latitude, location.longitude, partnerName) {
        val loadedMap = map ?: return@LaunchedEffect
        if (!styleReady) return@LaunchedEffect
        val point = LatLng(location.latitude, location.longitude)
        marker?.remove()
        marker = loadedMap.addMarker(
            MarkerOptions().position(point).title(partnerName)
        )
        loadedMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 14.0))
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { view ->
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    )
}

private const val LIGHT_STYLE = "https://tiles.openfreemap.org/styles/positron"
private const val DARK_STYLE = "https://tiles.openfreemap.org/styles/dark"
