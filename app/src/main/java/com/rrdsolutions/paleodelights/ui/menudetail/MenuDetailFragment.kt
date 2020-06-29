package com.rrdsolutions.paleodelights.ui.menudetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.rrdsolutions.paleodelights.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_menudetail.*
import java.math.BigDecimal
import java.math.RoundingMode

class MenuDetailFragment : Fragment() {

    val vm: MenuDetailViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.findViewById<Toolbar>(R.id.appbar_main_toolbar)?.title = "Menu Items"
        val root = inflater.inflate(R.layout.fragment_menudetail, container, false)
        return root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cat = activity?.getPreferences(0)?.getString("cat", "")
        val i = activity?.getPreferences(0)?.getInt("index", 0) as Int
        var savedmenu = activity?.getPreferences(0)?.getString("savedmenu", "")

        vm.menu = Gson().fromJson(savedmenu, Menu::class.java)

        lateinit var menuitem: MenuItems
        when (cat){
            "food"-> menuitem = vm.menu.foodmenu[i]
            "drink"-> menuitem = vm.menu.drinkmenu[i]
            "appetizer"-> menuitem = vm.menu.appetizermenu[i]
        }

        Picasso.get().load(menuitem.image)
            .resize(110,110)
            .into(image)

        name.text = menuitem.name
        desc.text = menuitem.desc
        val product = menuitem.price
        val product2 = BigDecimal(product).setScale(2, RoundingMode.HALF_EVEN)
        price.text = "RM $product2 per serving."

        if (vm.bool == false){
            vm.counter.value = menuitem.amount
            vm.setbool(true)
        }

        vm.counter.observe(viewLifecycleOwner, Observer{
            amount.text = vm.counter.value.toString()
        })

        plusbutton.setOnClickListener{
            vm.increaseValue()
        }
        minusbutton.setOnClickListener{
            vm.decreaseValue()
        }

        addtocartbtn.setOnClickListener{
            when (cat){
                "food"-> vm.menu.foodmenu[i].amount = vm.counter.value as Int
                "drink"-> vm.menu.drinkmenu[i].amount = vm.counter.value as Int
                "appetizer"-> vm.menu.appetizermenu[i].amount = vm.counter.value as Int
            }
            savedmenu = Gson().toJson(vm.menu)
            activity?.getPreferences(0)?.edit()?.putString("savedmenu",savedmenu)?.apply()

            goBack()
        }

        getActivity()?.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.loadingscreenmain2)?.visibility =
            View.GONE
    }

    fun goBack(){
        view?.let {
            Navigation.findNavController(it)
                .navigate(R.id.returntomenu) }

    }


}






