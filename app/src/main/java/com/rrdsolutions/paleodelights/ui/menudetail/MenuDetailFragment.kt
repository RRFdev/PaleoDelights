package com.rrdsolutions.paleodelights.ui.menudetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rrdsolutions.paleodelights.MenuModel
import com.rrdsolutions.paleodelights.R
import com.rrdsolutions.paleodelights.ui.menuitems.MenuFragment
import com.rrdsolutions.paleodelights.ui.processpayment.ProcessPaymentFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_menudetail.*
import kotlinx.android.synthetic.main.menucard.view.*
import kotlinx.android.synthetic.main.menucard.view.image
import java.math.BigDecimal
import java.math.RoundingMode


class MenuDetailFragment : Fragment() {

    lateinit var vm: MenuDetailViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_menudetail, container, false)
        vm = ViewModelProvider(this).get(MenuDetailViewModel::class.java)
        activity?.findViewById<Toolbar>(R.id.appbar_main_toolbar)?.title = "Menu"

        return root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val cat = activity?.getPreferences(0)?.getString("cat", "")
        val i = activity?.getPreferences(0)?.getInt("index", 0) as Int
        Log.d("_MenuDetailFragment", "cat = $cat, index = "+i.toString())

        lateinit var menuitem:MenuModel.MenuItems

        when (cat){
            "food"-> menuitem = MenuModel.menu.foodmenu[i]
            "drink"-> menuitem = MenuModel.menu.drinkmenu[i]
            "appetizer"-> menuitem = MenuModel.menu.appetizermenu[i]
        }

        Picasso.get().load(menuitem.image)
            .resize(110,110)
            .into(image)
        name.text = menuitem.name
        desc.text = menuitem.desc
        val product = menuitem.price
        val product2 = BigDecimal(product).setScale(2, RoundingMode.HALF_EVEN)
        price.text = "RM $product2 per serving."
        vm.counter.value = menuitem.amount

        vm.counter.observe(viewLifecycleOwner, Observer{
            amount.text = vm.counter.value.toString()
        })
        plusbutton.setOnClickListener{
            var counter = vm.counter.value as Int
            counter++
            vm.counter.value = counter
        }
        minusbutton.setOnClickListener{
            var counter = vm.counter.value as Int
            if (counter>0){
                counter--
                vm.counter.value = counter
            }
        }

        addtocartbtn.setOnClickListener{
            menuitem.amount = vm.counter.value as Int

            goBack()

        }

        getActivity()?.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.loadingscreenmain2)?.visibility =
            View.INVISIBLE

    }

    fun goBack(){

//        val fm = fragmentManager
//        fm?.beginTransaction()
//            ?.replace(R.id.content_main_nav_host_fragment, MenuFragment())
//            //addToBackStack(null)
//            ?.commit()

        val fm = fragmentManager
        fm?.popBackStackImmediate()
    }


}






