package com.rrdsolutions.paleodelights.ui.deliverystatus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.AutoTransition
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.google.firebase.auth.FirebaseAuth
import com.rrdsolutions.paleodelights.Order
import com.rrdsolutions.paleodelights.R
//import kotlinx.android.synthetic.main.ecv_bottomcard.view.botlayout
import kotlinx.android.synthetic.main.fragment_deliverystatus.*
import kotlinx.android.synthetic.main.notificationcard.view.*
//import kotlinx.android.synthetic.main.ordercard.view.*
import kotlinx.android.synthetic.main.ordercard.view.hiddenlayout
import kotlinx.android.synthetic.main.ordercard.view.numbertxt
import kotlinx.android.synthetic.main.ordercard.view.statustxt
import kotlinx.android.synthetic.main.ordercard.view.expandimage
import kotlinx.android.synthetic.main.ordercard.view.*
import kotlinx.android.synthetic.main.simpletext.view.*

class DeliveryStatusFragment : Fragment() {

    val vm: DeliveryStatusViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<Toolbar>(R.id.appbar_main_toolbar)?.title = "Delivery Status"
        return inflater.inflate(R.layout.fragment_deliverystatus, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.phonenumber = FirebaseAuth.getInstance().currentUser?.phoneNumber as String

        button3.setOnClickListener {
            cardholder.removeAllViews()
            if (vm.text == "View Previous Orders") {
                vm.text = "View Current Order"

            } else {
                vm.text = "View Previous Orders"

            }
            vm.buttontext.value = vm.text
        }

        vm.buttontext.observe(viewLifecycleOwner, Observer {
            button3.text = vm.buttontext.value

            vm.loadOrders(vm.buttontext.value as String) { taskCompleted ->
                if (taskCompleted) buildOrderCards()
                else noOrders()

                activity?.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.loadingscreenmain2)?.visibility =
                    View.GONE
            }

        })

        class MyBroadcastReceiver: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("_broadcast", "broadcast received")
                cardholder.removeAllViews()
                vm.loadOrders(vm.buttontext.value as String) { taskCompleted ->
                    if (taskCompleted) buildOrderCards()
                    else noOrders()

                    activity?.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.loadingscreenmain2)?.visibility =
                        View.GONE
                    }
            }

        }

        fun setupReceiver(){
            val intentfiller = IntentFilter().apply {addAction("DeliveryStatusFragment")}
            val receiver = MyBroadcastReceiver()
            activity?.registerReceiver(receiver, intentfiller)
        }
        setupReceiver()
    }



    fun buildOrderCards() {

        when (vm.buttontext.value as String){
            "View Previous Orders"->{
                for (i in 0 until vm.orderlist.size) {
                    val ordercard = layoutInflater.inflate(R.layout.ordercard2, null)

                    ordercard.numbertxt.text = vm.orderlist[i].number
                    ordercard.statustxt.text = vm.orderlist[i].status

                    for (element in vm.orderlist[i].itemlist) {
                        val purchasedetails = layoutInflater.inflate(R.layout.simpletext, null)
                        purchasedetails.txt.text = element
                        ordercard.purchasedetailslayout.addView(purchasedetails)
                    }
                    ordercard.datetxt.text = vm.orderlist[i].time
                    ordercard.etatxt.text = vm.orderlist[i].eta
                    ordercard.addresstxt.text = vm.orderlist[i].address

                    cardholder.addView(ordercard)
                    vm.orderlist = arrayListOf<Order>()
                }
            }
            "View Current Order"->{
                for (i in 0 until vm.orderlist.size) {
                    val ordercard = layoutInflater.inflate(R.layout.ordercard, null)

                    ordercard.numbertxt.text = vm.orderlist[i].number
                    ordercard.statustxt.text = vm.orderlist[i].status

                    ordercard.setOnClickListener {
                        if (ordercard.hiddenlayout.visibility == View.GONE) {
                            TransitionManager.beginDelayedTransition(
                                ordercard as ViewGroup,
                                AutoTransition()
                            )
                            ordercard.hiddenlayout.visibility = View.VISIBLE

                            ordercard.expandimage.animate().rotation(180f).start()
                        } else {
                            TransitionManager.beginDelayedTransition(
                                ordercard as ViewGroup,
                                Fade().setDuration(300)
                            )
                            ordercard.hiddenlayout.visibility = View.GONE

                            ordercard.expandimage.animate().rotation(0f).start()
                        }
                    }

                    for (element in vm.orderlist[i].itemlist) {
                        val purchasedetails = layoutInflater.inflate(R.layout.simpletext, null)
                        purchasedetails.txt.text = element
                        ordercard.purchasedetailslayout.addView(purchasedetails)
                    }
                    ordercard.datetxt.text = vm.orderlist[i].time
                    ordercard.etatxt.text = vm.orderlist[i].eta
                    ordercard.addresstxt.text = vm.orderlist[i].address

                    cardholder.addView(ordercard)
                    vm.orderlist = arrayListOf<Order>()
                }
            }
        }
    }

    fun noOrders() {

        val notificationcard = layoutInflater.inflate(R.layout.notificationcard, null)
        notificationcard.notificationtext.text = "No deliveries present"

        cardholder.addView(notificationcard)

    }




}