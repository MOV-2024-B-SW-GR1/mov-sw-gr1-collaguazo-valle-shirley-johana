package com.example.deber01

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private var nombreJardin: String = "Jardín"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Recibir coordenadas y nombre enviados por el intent
        lat = intent.getDoubleExtra("latitud", 0.0)
        lon = intent.getDoubleExtra("longitud", 0.0)
        nombreJardin = intent.getStringExtra("nombreJardin") ?: "Jardín"

        // Inicializar el fragmento del mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val location = LatLng(lat, lon)
        googleMap.addMarker(MarkerOptions().position(location).title(nombreJardin))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}
