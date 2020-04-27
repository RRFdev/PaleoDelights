package com.rrdsolutions.paleodelights.ui.getaddress

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
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
import com.rrdsolutions.paleodelights.ui.processpayment.ProcessPaymentFragment
import kotlinx.android.synthetic.main.fragment_getaddress.*
import java.io.IOException

class GetAddressFragment: Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    lateinit var viewmodel: GetAddressViewModel

    lateinit var map: GoogleMap
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var lastLocation: Location

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewmodel = ViewModelProviders.of(this).get(GetAddressViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_getaddress, container, false)
        activity?.findViewById<Toolbar>(R.id.appbar_main_toolbar)?.title = "Get Address"
        return root
        //return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity().baseContext)

        btn.setOnClickListener{
            activity?.getSharedPreferences("address", 0)?.edit()
                ?.putString("address", edt.getText().toString())?.apply()
            Log.e("maptest", "saved string is " + activity?.getSharedPreferences("address", 0)?.getString("address", ""))

            moveBackToProcessPayment()
        }

        activity?.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.loadingscreenmain2)?.visibility =
            View.INVISIBLE
    }

    override fun onMarkerClick(p0: Marker?) = false

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        //declare this Fragment as target when user clicks marker
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        if (ActivityCompat.checkSelfPermission(this.context as Activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this.context as Activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            Log.d("maptest", "User permission not granted")

            return
        }
        //**actual code starts here*//
        else {
            Log.d("maptest", "User permission granted")
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null){
                    lastLocation = it
                    ////
                    val userLoc = LatLng(it.latitude, it.longitude)
                    Log.d("maptest", "lat = " + it.latitude + " lng + " + it.longitude)
                    val titleStr = getAddress(userLoc)
                    map.addMarker(MarkerOptions().position(userLoc).title(titleStr)).showInfoWindow()
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 18f))
                    //17 is almost perfect.
                    Log.d("maptest", "titleStr is " + titleStr)

                    edt.setText(titleStr)
                }
            }
        }
    }

    fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(this.context as Activity)
        var result = ""

        try {
            val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (null != address && !address.isEmpty()) {
                Log.e("maptest", "address is " +address.toString())
                result = address[0].getAddressLine(0)
            }
        }
        catch (e: IOException) {
            Log.e("maptest", "address fetch failed")
        }

        return result
    }

    fun moveBackToProcessPayment(){
        val fm = fragmentManager
        fm?.popBackStackImmediate()
    }



}