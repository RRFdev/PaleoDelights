package com.rrdsolutions.paleodelights

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object MenuModel {

    data class MenuItems(
        var name: String,
        var price: Double,
        var image: String,
        var desc:String,
        var amount: Int = 0,
        var cat: String
    )

    data class Menu(
        var foodmenu: MutableList<MenuItems>,
        var drinkmenu: MutableList<MenuItems>,
        var appetizermenu: MutableList<MenuItems>
    )
    lateinit var menu:Menu

    var dataloaded = false
    var emptyorderLiveData = MutableLiveData<Boolean>()
    var nofoods = false
    var nodrinks = false
    var noappetizers = false

    fun check(){

        //TODO: False doesn't appear!

        for (i in 0 until menu.foodmenu.size){
            if (menu.foodmenu[i].amount == 0) {
                nofoods = true
            }
            else {
                nofoods = false
                break
            }
        }
        for (i in 0 until menu.drinkmenu.size){
            if (menu.drinkmenu[i].amount == 0) {
                nodrinks = true
            }
            else {
                nodrinks = false
                break
            }
        }
        for (i in 0 until menu.appetizermenu.size){
            if (menu.appetizermenu[i].amount == 0) {
                noappetizers = true
            }
            else {
                noappetizers = false
                break
            }
        }

        emptyorderLiveData.value = nofoods && nodrinks && noappetizers


    }
}