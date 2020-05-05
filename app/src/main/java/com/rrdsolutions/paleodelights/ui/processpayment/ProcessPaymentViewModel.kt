package com.rrdsolutions.paleodelights.ui.processpayment

import android.app.Activity
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.common.primitives.UnsignedBytes.toInt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import com.rrdsolutions.paleodelights.MenuModel
import kotlinx.android.synthetic.main.fragment_processpayment.*
import java.text.SimpleDateFormat
import java.util.*

class ProcessPaymentViewModel: ViewModel() {

    lateinit var menu: MenuModel.Menu

    //var phonenumber = ""
    var time = ""
    var address =""
    var eta =""

    fun saveOrder(callback: (Boolean)-> Unit){

        val db = FirebaseFirestore.getInstance()
            .collection("customer orders")

        db.document("count")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val count = task.result?.get("count") as Long

                    val number = "Order #"+(count + 1).toString()


                    val itemlist = mutableListOf<String>()
                    fun fillitemlist(array: MutableList<MenuModel.MenuItems>){
                        for (i in 0 until array.size){
                            if (array[i].amount > 0){
                                val item = array[i].name + " x " + array[i].amount.toString()
                                itemlist.add(item)
                            }
                        }
                    }
                    fillitemlist(menu.foodmenu)
                    fillitemlist(menu.drinkmenu)
                    fillitemlist(menu.appetizermenu)

                    val order = hashMapOf(
                        "phonenumber" to FirebaseAuth.getInstance().currentUser?.phoneNumber as String,
                        "time" to time,
                        "eta" to eta,
                        "itemlist" to itemlist,
                        "address" to address,
                        "status" to "IN PROGRESS",
                        "rider" to ""
                    )

                    db.document(number).set(order)
                        .addOnCompleteListener{
                            Log.d("viewmodel", "document write successful")
                            val numberhash = hashMapOf(
                                "count" to (count + 1)
                            )
                            db.document("count").set(numberhash)
                                .addOnCompleteListener{
                                    Log.d("viewmodel", "number updated")
                                    callback(true)
                                }
                        }
                        .addOnFailureListener{
                            Log.d("viewmodel", "document write failed")
                            callback(false)
                        }
                    //callback(result)
                }
            }
            .addOnFailureListener{
                Log.d("viewmodel", "no number found")
                callback(false)
            }
    }

    fun checkDelivery(callback:(Boolean)->Unit){
        Log.d("checkdelivery", "start")
        val db = FirebaseFirestore.getInstance()
            .collection("customer orders")

        db.whereEqualTo("phonenumber", FirebaseAuth.getInstance().currentUser?.phoneNumber as String)
            .whereEqualTo("status", "IN PROGRESS")
            .get()
            .addOnSuccessListener{documents ->
                Log.d("checkdelivery", "success")

                if (documents.isEmpty){
                    Log.d("checkdelivery", "documents are empty")
                    callback(true)
                }
                else Log.d("checkdelivery", "documents are not empty")
                for (document in documents){
                    val id = document.id
                    Log.d("checkdelivery", "id " + id)
                    callback(false)
                }

            }

            .addOnFailureListener{documents ->
                Log.d("checkdelivery", "failure")

            }



    }






}