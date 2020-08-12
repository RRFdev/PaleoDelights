package com.rrdsolutions.paleodelights.ui.processpayment

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rrdsolutions.paleodelights.*

class ProcessPaymentViewModel: ViewModel() {

    lateinit var menu: Menu

    var time = ""
    var address =""
    var eta =""


    fun test(){
        val user = FirebaseAuth.getInstance().currentUser
        var eta = user?.uid
    }

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
                    fun fillitemlist(array: MutableList<MenuItems>){
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
                        "status" to Status().IN_PROGRESS,
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
                                    callback(true)
                                }
                        }
                        .addOnFailureListener{
                            callback(false)
                        }
                }
            }
            .addOnFailureListener{
                callback(false)
            }
    }

    fun checkDelivery(callback:(Boolean)->Unit){
        val db = FirebaseFirestore.getInstance()
            .collection("customer orders")




        db.whereEqualTo("phonenumber", FirebaseAuth.getInstance().currentUser?.phoneNumber as String)
            .whereEqualTo("status", Status().IN_PROGRESS)
            .get()
            .addOnSuccessListener{documents ->
                if (documents.isEmpty){
                    callback(true)
                }
                else callback(false)
            }
            .addOnFailureListener{documents ->
                callback(false)
            }
    }

}