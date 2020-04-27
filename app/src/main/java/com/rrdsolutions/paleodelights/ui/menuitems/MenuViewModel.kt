package com.rrdsolutions.paleodelights.ui.menuitems

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.rrdsolutions.paleodelights.MenuModel

class MenuViewModel: ViewModel() {

//    lateinit var menu: MenuModel.Menu
//    var dataloaded = false

    fun loadOnline(callback: (Boolean)->Unit){

        val foodmenu = mutableListOf<MenuModel.MenuItems>()
        val drinkmenu = mutableListOf<MenuModel.MenuItems>()
        val appetizermenu = mutableListOf<MenuModel.MenuItems>()
        val db = FirebaseFirestore.getInstance().collection("menu items")
        var size = 0
        var counter = 0
        db.get()
            .addOnSuccessListener{ documents->

                Log.d("loadOnline", "Success")
                size = documents.size()
                if (size == 0) callback(false)
                else{
                    Log.d("loadOnline", "size is " + size)
                    for (document in documents){
                        val name = document.id
                        val cat = document.data["cat"] as String
                        val price = (document.data["price"] as Double)
                        val imagestring = document.data["image"] as String
                        val prefix = "https://firebasestorage.googleapis.com/v0/b/paleo-delights.appspot.com/o/"
                        val suffix = "?alt=media&token"
                        val image = prefix+imagestring+suffix
                        val desc = document.data["desc"] as String

                        when (cat){
                            "food"-> foodmenu.add(MenuModel.MenuItems(name, price, image, desc, 0,cat))
                            "drink"-> drinkmenu.add(MenuModel.MenuItems(name, price, image, desc, 0,cat))
                            "appetizer"-> appetizermenu.add(MenuModel.MenuItems(name, price, image, desc, 0,cat))
                        }
                        counter++
                        if( counter == size  ){
                            //function finally completes
                            MenuModel.menu = MenuModel.Menu(foodmenu, drinkmenu, appetizermenu)
                            MenuModel.dataloaded = true

                            callback(true)
                        }

                }

                }

            }
            .addOnFailureListener{
                Log.d("loadOnline", "Failure")
                callback(false)
            }

    }

    fun loadOffline(callback: (Boolean) ->Unit){
//        menu = MenuModel.menu
//        dataloaded = MenuModel.dataloaded
        callback(true)
    }

}