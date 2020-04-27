package com.rrdsolutions.paleodelights.ui.deliverystatus

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import io.karn.notify.Notify

//import io.karn.notify.Notify

@Suppress("NAME_SHADOWING")
class DNService : IntentService("CTService") {

    override fun onHandleIntent(intent: Intent?) {

        val phonenumber = FirebaseAuth.getInstance().currentUser?.phoneNumber as String
        var documentname = ""
        Log.d("DNService", "DNService Started")
        Log.d("DNService", "phone number " + phonenumber)

        getDocumentName(phonenumber) {documentname->
            if (documentname == ""){
                Log.d("_DNService", "ERROR: DNService unable to detect existing delivery")
            }
            else{
               monitorDelivery(documentname){status->

                   if (status == "DELIVERED" || status == "CANCELED"){
                       notify(status, documentname)
                       updateDeliveryStatusOnApp(status)
                       //stopSelf()
                   }
               }
            }
        }
//
//
//
//
//        val db = FirebaseFirestore.getInstance()
//            .collection("customer orders")
//            .whereEqualTo("phonenumber", phonenumber)
//            .whereEqualTo("status", "IN PROGRESS")
//
//        db.get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    documentname = document.id
//                    Log.d("DNService", "document name: $documentname")
//
//                    val db2 = FirebaseFirestore.getInstance()
//                        .collection("customer orders")
//                        .document(documentname)
//
//                    db2.addSnapshotListener { snapshot, e ->
//                        if (e != null) {
//                            Log.d("DNService", "Listen failed.", e)
//                            return@addSnapshotListener
//                        }
//                        if (snapshot != null && snapshot.exists()) {
//                            val status = snapshot.data?.get("status") as String
//                            Log.d("DNService", "current status: $status")
//
//                            notify(status, documentname)
//                            broadcast(status)
//
//                            stopSelf()
//                        } else {
//                            Log.d("DNService", "Current data: null")
//                        }
//                    }
//                }
//
//
//            }
//            .addOnFailureListener {
//                Log.d("DNService", "query failed")
//            }

    }

    fun getDocumentName(phonenumber: String, callback:(String)->Unit){

        val db = FirebaseFirestore.getInstance()
            .collection("customer orders")
            .whereEqualTo("phonenumber", phonenumber)
            .whereEqualTo("status", "IN PROGRESS")

        db.get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    callback(document.id)
                    Log.d("DNService", "query successful. document.id = $document.id")
                }
            }
            .addOnFailureListener{
                callback("")
                Log.d("DNService", "query failed")
            }

    }

    fun monitorDelivery(documentname: String, callback: (String)-> Unit){
        val db = FirebaseFirestore.getInstance()
            .collection("customer orders")
            .document(documentname)

        db.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("DNService", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val status = snapshot.data?.get("status") as String
                callback(status)
                Log.d("DNService", "current status: $status")

//                notify(status, documentname)
//                broadcast(status)
//
//                stopSelf()
            } else {
                callback("")
                Log.d("DNService", "Current data: null")
            }
        }
    }

    fun notify(status: String, documentname:String) {
        var desc: String = ""
        when (status){
            "DELIVERED"->
                desc = "Thank you for your purchase. $documentname has been delivered to your location."
            "CANCELED"->
                desc ="Sorry. $documentname has been canceled."
        }
        Notify
            .with(applicationContext)
            .content {
                text = desc
            }
            .show()

    }

    fun updateDeliveryStatusOnApp(status:String){

        val intent = Intent().apply{ action = "DeliveryStatusFragment" }
        //intent.action = "DeliveryStatusFragment"

        sendBroadcast(intent)



    }
}