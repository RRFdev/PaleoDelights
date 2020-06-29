package com.rrdsolutions.paleodelights

import android.view.View

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

data class Order(
    var number: String,
    var phonenumber: String,
    var time: String,
    var eta: String,
    var itemlist: List<String>,
    var address:String,
    var status: String
)

data class Views(
    var foodview: View? = null,
    var drinkview: View? = null,
    var appetizerview: View? = null
)