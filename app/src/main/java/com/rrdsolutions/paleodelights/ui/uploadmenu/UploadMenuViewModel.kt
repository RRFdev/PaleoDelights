package com.rrdsolutions.paleodelights.ui.uploadmenu

import androidx.lifecycle.ViewModel
import com.rrdsolutions.paleodelights.R


class UploadMenuViewModel: ViewModel(){

    data class menuitem (
        val name:String,
        val price: Double,
        val image: String,
        var amount:Int = 0
    )

    data class Menu(
        var version: Int,
        var foodItems: Array<menuitem> ,
        var drinkItems: Array<menuitem> ,
        var appetizerItems: Array<menuitem>
    )


    val menuitem_food = arrayOf(
        UploadMenuViewModel.menuitem("Foodnew1", 5.00, "squarepaleo1.jpg"),
        UploadMenuViewModel.menuitem("Foodnew2", 10.00, "zaatar-chicken.jpg")
    )

    val menuitem_drinks= arrayOf(
        UploadMenuViewModel.menuitem("Drinksnew1", 15.00, "zaatar-chicken.jpg"),
        UploadMenuViewModel.menuitem("Drinksnew2", 20.00, "squarepaleo1.jpg")
    )

    val menuitem_appetizers= arrayOf(
        UploadMenuViewModel.menuitem("Appetizersnew1", 1.51, "squarepaleo1.jpg"),
        UploadMenuViewModel.menuitem("Appetizersnew2", 0.90, "zaatar-chicken.jpg")
    )

    val menu = Menu(1, menuitem_food, menuitem_drinks, menuitem_appetizers)





}
