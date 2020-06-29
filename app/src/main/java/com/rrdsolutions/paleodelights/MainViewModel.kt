package com.rrdsolutions.paleodelights

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel(private var ssh: SavedStateHandle): ViewModel() {

    var phonenumber = ""

    fun setPhoneNumber(i: String){
        val PHONENUMBER = "phonenumber"
        ssh.set(PHONENUMBER, i)
        phonenumber = ssh.get(PHONENUMBER)!!
    }

    fun checkDelivery(deliveryPresent:(Boolean)->Unit){

        val db = FirebaseFirestore.getInstance()
            .collection("customer orders")

        db.whereEqualTo("phonenumber", phonenumber)
            .whereEqualTo("status", Status().IN_PROGRESS)
            .get()

            .addOnSuccessListener{documents ->
                if (documents.isEmpty){
                    deliveryPresent(false)
                }
                else {
                    deliveryPresent(true)
                }
            }

            .addOnFailureListener{
                deliveryPresent(false)
            }
    }

}