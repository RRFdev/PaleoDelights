package com.rrdsolutions.paleodelights

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel: ViewModel() {
    var phonenumber = ""
    fun checkDelivery(phonenumber: String, callback:(Boolean)->Unit){
        Log.d("Maincheckdelivery", "start")
        val db = FirebaseFirestore.getInstance()
            .collection("customer orders")

        db.whereEqualTo("phonenumber", phonenumber)
            .whereEqualTo("status", "IN PROGRESS")
            .get()
            .addOnSuccessListener{documents ->
                Log.d("Maincheckdelivery", "success")

                if (documents.isEmpty){
                    Log.d("Maincheckdelivery", "documents are empty")
                    callback(true)
                }
                else Log.d("Maincheckdelivery", "documents are not empty")
                for (document in documents){
                    val id = document.id
                    Log.d("Maincheckdelivery", "id " + id)
                    callback(false)
                }

            }

            .addOnFailureListener{documents ->
                Log.d("checkdelivery", "failure")

            }



    }
}