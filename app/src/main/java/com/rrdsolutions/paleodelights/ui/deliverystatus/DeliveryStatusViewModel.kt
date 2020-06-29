package com.rrdsolutions.paleodelights.ui.deliverystatus

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.rrdsolutions.paleodelights.Order
import com.rrdsolutions.paleodelights.Status
import com.rrdsolutions.paleodelights.ui.processpayment.ProcessPaymentViewModel
import org.json.JSONArray

@Suppress("UNCHECKED_CAST")
class DeliveryStatusViewModel: ViewModel() {

    var phonenumber = FirebaseAuth.getInstance().currentUser?.phoneNumber as String

    var orderlist = arrayListOf<Order>()
    var buttontext = MutableLiveData<String>().apply { postValue("View Previous Orders")}
    var text = "View Previous Orders"


    fun loadOrders(buttontext:String, callback: (Boolean)->Unit){
        fun loadCurrentOrders(callback: (Boolean)-> Unit){

            Log.d("_delivery", "Loading current orders")

            val db = FirebaseFirestore.getInstance()
                .collection("customer orders")
                .whereEqualTo("phonenumber", phonenumber)
                .whereEqualTo("status", Status().IN_PROGRESS)

            db.get()
                .addOnSuccessListener{documents ->

                    if (documents.size()== 0) callback(false)
                    else{
                        for (document in documents){
                            val order = Order(
                                document.id,
                                document.data["phonenumber"] as String,
                                document.data["time"] as String,
                                document.data["eta"] as String,
                                document.data["itemlist"] as List<String>,
                                document.data["address"] as String,
                                document.data["status"] as String
                            )
                            orderlist.add(order)
                            //callback(true)
                        }
                        callback(true)
                    }

                }
                .addOnFailureListener{
                    callback(false)
                }
        }
        fun loadPreviousOrders(callback: (Boolean)->Unit){

            Log.d("_delivery", "Loading previous orders")

            val db = FirebaseFirestore.getInstance()
                .collection("customer orders")
                .whereEqualTo("phonenumber", phonenumber)
                .whereIn("status", listOf(Status().DELIVERED, Status().CANCELED))

            db.get()
                .addOnSuccessListener{documents ->
                    if (documents.size()== 0) callback(false)
                    else{
                        for (document in documents){
                            val order = Order(
                                document.id,
                                document.data["phonenumber"] as String,
                                document.data["time"] as String,
                                document.data["eta"] as String,
                                document.data["itemlist"] as List<String>,
                                document.data["address"] as String,
                                document.data["status"] as String
                            )
                            //!!
                            orderlist.add(order)
                            callback(true)
                        }
                    }
                }
                .addOnFailureListener{
                    callback(false)
                }
        }

        when (buttontext){
            "View Previous Orders"->{
                loadCurrentOrders{taskCompleted->
                    if (taskCompleted) callback(true)
                    else callback (false)
                }
            }
            "View Current Order"->{
                loadPreviousOrders{taskCompleted->
                    if (taskCompleted) callback(true)
                    else callback (false)
                }
            }
        }
    }
}