package com.rrdsolutions.paleodelights.ui.location

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.rrdsolutions.paleodelights.R

import android.location.Geocoder
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import java.io.IOException

class LocationFragment: Fragment(), OnMapReadyCallback,  GoogleMap.OnMarkerClickListener {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        activity?.findViewById<Toolbar>(R.id.appbar_main_toolbar)?.title = "Location"
        val root = inflater.inflate(R.layout.fragment_location, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity().baseContext)
        activity?.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.loadingscreenmain2)?.visibility =
            View.GONE
    }

    override fun onMapReady(p0: GoogleMap) {

        map = p0
        //declare this Fragment as target when user clicks marker
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        if (ActivityCompat.checkSelfPermission(this.context as Activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.context as Activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return
        }

        else{
            val store = LatLng(3.803649, 103.324294)
            val desc = "Paleo Delights Sdn Bhd"
            val desc2 = "No.302 Tingkat 2, Kompleks Teruntum Kuantan"
            map.addMarker(MarkerOptions().position(store).title(desc).snippet(desc2)).showInfoWindow()
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(store, 18f))
        }
    }
    override fun onMarkerClick(p0: Marker?) = false
}