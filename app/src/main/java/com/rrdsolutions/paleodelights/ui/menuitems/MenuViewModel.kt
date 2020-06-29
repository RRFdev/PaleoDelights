package com.rrdsolutions.paleodelights.ui.menuitems

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.rrdsolutions.paleodelights.*
import java.io.Serializable

class MenuViewModel(private var ssh: SavedStateHandle): ViewModel() {

    lateinit var menu: Menu
    fun loadMenu(menuLoaded: (Boolean)->Unit){
            val foodmenu = mutableListOf<MenuItems>()
            val drinkmenu = mutableListOf<MenuItems>()
            val appetizermenu = mutableListOf<MenuItems>()

            val db = FirebaseFirestore.getInstance().collection("menu items")

            var size = 0
            var counter = 0
            db.get()
                .addOnSuccessListener{ documents->
                    size = documents.size()
                    if (size == 0) menuLoaded(false)
                    else{
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
                                "food"-> foodmenu.add(MenuItems(name, price, image, desc, 0,cat))
                                "drink"-> drinkmenu.add(MenuItems(name, price, image, desc, 0,cat))
                                "appetizer"-> appetizermenu.add(MenuItems(name, price, image, desc, 0,cat))
                            }
                            counter++

                            if( counter == size  ){
                                //function finally completes
                                menu = Menu(foodmenu, drinkmenu, appetizermenu)
                                menuLoaded(true)
                            }
                        }
                    }
                }

                .addOnFailureListener{
                    menuLoaded(false)
                }
    }

    val amountempty = ssh.getLiveData<Boolean>("amountempty", true)
    fun check(){
        val nofoods = menu.foodmenu.all{it.amount == 0}
        val nodrinks = menu.drinkmenu.all{it.amount == 0}
        val noappetizers = menu.appetizermenu.all{it.amount == 0}
        ssh.set("amountempty", nofoods && nodrinks && noappetizers)
    }
}