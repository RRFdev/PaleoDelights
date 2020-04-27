package com.rrdsolutions.paleodelights.ui.processpayment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rrdsolutions.paleodelights.ui.deliverystatus.DNService
import com.rrdsolutions.paleodelights.MenuModel
import com.rrdsolutions.paleodelights.R
import com.rrdsolutions.paleodelights.ui.deliverystatus.DeliveryStatusFragment
//import com.rrdsolutions.paleodelights.repositories.MenuObject
//import com.rrdsolutions.paleodelights.repositories.MenuObject2.saveToFirebase
import com.rrdsolutions.paleodelights.ui.getaddress.GetAddressFragment
import kotlinx.android.synthetic.main.fragment_processpayment.*
import kotlinx.android.synthetic.main.menuitempurchased.view.*
import kotlinx.android.synthetic.main.menuitempurchasedtotal.view.*
import java.lang.Math.round
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

//test11


class ProcessPaymentFragment : Fragment() {

    lateinit var vm: ProcessPaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //ViewModelProviders.of(this).get(ProcessPaymentViewModel::class.java)
        vm = ViewModelProvider(this).get(ProcessPaymentViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_processpayment, container, false)

        activity?.findViewById<Toolbar>(R.id.appbar_main_toolbar)?.title = "Process Payment"
        return root

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getActivity()?.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.loadingscreenmain2)?.visibility =
            View.INVISIBLE

        vm.menu = MenuModel.menu

        buildPurchaseDetails(vm.menu.foodmenu)
        buildPurchaseDetails(vm.menu.drinkmenu)
        buildPurchaseDetails(vm.menu.appetizermenu)
        buildPurchaseTotal()

        val fromGetAdressFragment = activity?.getSharedPreferences("address", 0)?.getString("address", "")
        edt_address.setText(fromGetAdressFragment)

        locationbutton.setOnClickListener {
            moveToGetAddress()
        }

        confirmorderbutton.setOnClickListener {

            if (edt_address.text.toString() == ""){
                Toast.makeText(activity, "Please enter address before submitting order", Toast.LENGTH_SHORT).show()
            }
            else{

                getActivity()?.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.loadingscreenmain2)?.visibility =
                    View.VISIBLE

                vm.checkDelivery{ empty->
                    if (empty == false){
                        val text = "To ensure our riders are not overburdened, please do not order more than one delivery at a time."
                        Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
                        getActivity()?.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.loadingscreenmain2)?.visibility =
                            View.INVISIBLE
                    }
                    else{

                        @SuppressLint("SimpleDateFormat")
                        fun getDate(): String{
                            val date = Date()
                            val formatter = SimpleDateFormat("MMM dd yyyy HH:mma")
                            return formatter.format(date)
                        }
                        vm.time = getDate()
                        vm.address = edt_address.text.toString()
                        getCoordinateFromAddress(edt_address.text.toString())

                        vm.saveOrder{ taskCompleted->
                            if (taskCompleted){

                                for (i in 0 until MenuModel.menu.foodmenu.size) {
                                    MenuModel.menu.foodmenu[i].amount = 0
                                }
                                for (i in 0 until MenuModel.menu.drinkmenu.size) {
                                    MenuModel.menu.drinkmenu[i].amount = 0
                                }
                                for (i in 0 until MenuModel.menu.appetizermenu.size) {
                                    MenuModel.menu.appetizermenu[i].amount = 0
                                }
                                activity?.startService(Intent(activity, DNService::class.java))
                                moveToDeliveryStatus()
                                activity?.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.loadingscreenmain2)?.visibility =
                                    View.INVISIBLE
                            }
                        }

                    }

                }

            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun buildPurchaseDetails(array: MutableList<MenuModel.MenuItems>) {

        for (i in array.indices) {
            if (array[i].amount != 0) {
                val fooditempurchased = layoutInflater.inflate(R.layout.menuitempurchased, null)
                fooditempurchased.itemname.text = array[i].name +
                        " x " + array[i].amount.toString()
                val product = array[i].price * array[i].amount
                val product2 = BigDecimal(product).setScale(2, RoundingMode.HALF_EVEN)
                fooditempurchased.itemprice.text = "RM " + product2.toString()

                layout_purchasedetails.addView(fooditempurchased)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun buildPurchaseTotal() {
        val purchasetotal = layoutInflater.inflate(R.layout.menuitempurchasedtotal, null)
        val producttotal = calculateProductTotal()
        val producttotal2 = BigDecimal(producttotal).setScale(2, RoundingMode.HALF_EVEN)
        purchasetotal.total.text = "Total RM " + producttotal2.toString()
        layout_purchasedetails.addView(purchasetotal)
    }

    private fun calculateProductTotal(): Double {
        var totalproduct = 0.0

        totalproduct = (calculateProductFor(vm.menu.foodmenu)
                + calculateProductFor(vm.menu.drinkmenu)
                + calculateProductFor(vm.menu.appetizermenu))

        return totalproduct
    }

    private fun calculateProductFor(array: MutableList<MenuModel.MenuItems>): Double {
        var product = 0.0
        for (i in array.indices) {
            product += (array[i].price * array[i].amount)
        }
        return product
    }

    private fun moveToDeliveryStatus() {

        val fm = fragmentManager
        fm?.beginTransaction()
            ?.replace(R.id.content_main_nav_host_fragment, DeliveryStatusFragment())
            ?.commit()
    }

    private fun moveToGetAddress(){

        val fm = fragmentManager
        fm?.beginTransaction()
            ?.replace(R.id.content_main_nav_host_fragment, GetAddressFragment())
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun getCoordinateFromAddress(myLocation:String){
        val geocoder = Geocoder(this.context as Activity, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocationName(myLocation, 1)
        if (addresses.size == 0){
            //coordinate not found
            vm.eta = "45 mins"
        }
        else {
            //coordinate found
            val address: Address = addresses[0]
            val longitude: Double = address.getLongitude()
            val latitude: Double = address.getLatitude()
            val home = Location("home")
            home.setLatitude(latitude)
            home.setLongitude(longitude)
            val cafe = Location("cafe")
            cafe.setLatitude(3.803639)
            cafe.setLongitude(103.324294)
            val distance = home.distanceTo(cafe)

            Log.d("coor", "distance = "+distance)

            //distance is in meters, using a straight line between two locations
            val riderspeed = 100
            // 100 meter/min as average speed of rider
            val time = distance/riderspeed
            vm.eta = round(time).toString() + " mins"
            Log.d("coor", "eta = "+vm.eta)
        }



    }








}


