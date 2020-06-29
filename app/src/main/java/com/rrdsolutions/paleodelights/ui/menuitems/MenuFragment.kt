package com.rrdsolutions.paleodelights.ui.menuitems

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import com.google.gson.Gson
import com.rrdsolutions.paleodelights.*

import com.rrdsolutions.paleodelights.ui.menudetail.MenuDetailFragment
//import com.rrdsolutions.paleodelights.repositories.MenuObject
import com.rrdsolutions.paleodelights.ui.processpayment.ProcessPaymentFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.menucard.view.*
import kotlinx.android.synthetic.main.menucard.view.amount
import kotlinx.android.synthetic.main.menucard.view.image
import kotlinx.android.synthetic.main.menucard.view.name
import kotlinx.android.synthetic.main.menucard.view.price
import kotlinx.android.synthetic.main.notificationcard.view.*
import java.math.BigDecimal
import java.math.RoundingMode

import androidx.lifecycle.observe
import androidx.navigation.Navigation

class MenuFragment : Fragment() {

    lateinit var vm: MenuViewModel
    //val vm: MenuViewModel by viewModels()

    override fun onCreateView
                (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_menu, container, false)
        vm = ViewModelProvider(this).get(MenuViewModel::class.java)

        activity?.findViewById<Toolbar>(R.id.appbar_main_toolbar)?.title = "Menu Items"
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val savedmenu = activity?.getPreferences(0)?.getString("savedmenu", "")
        if (savedmenu == ""){
            vm.loadMenu{menuLoaded->
                if(menuLoaded){
                    val savedmenu = Gson().toJson(vm.menu)
                    activity?.getPreferences(0)?.edit()?.putString("savedmenu",savedmenu)?.apply()
                    buildMenu()
                }
                else noMenu()
            }
        }
        else{
            val menu = Gson().fromJson(savedmenu, Menu::class.java)
            vm.menu = menu
            buildMenu()
        }

        vm.amountempty.observe(viewLifecycleOwner){it->
            when (it){
                true->  checkoutbutton.visibility = View.GONE
                false-> checkoutbutton.visibility = View.VISIBLE
            }
        }

        checkoutbutton.setOnClickListener {
            moveToProcessPayment()
        }

    }

    fun buildMenu(){

        fun buildMenu2(array: MutableList<MenuItems>): View {
            val fragment_menu_items = layoutInflater.inflate(R.layout.fragment_menu_items, null)

            for (i in 0 until array.size) {
                val menucard = layoutInflater.inflate(R.layout.menucard, null)

                menucard.name.text = array[i].name
                val product = array[i].price
                val product2 = BigDecimal(product).setScale(2, RoundingMode.HALF_EVEN)
                menucard.price.text = "RM " + product2.toString()
                Picasso.get().load(array[i].image)
                    .resize(110,110)
                    .into(menucard.image)
                menucard.amount.text = array[i].amount.toString()
                if (array[i].amount > 0){
                    menucard.amount.visibility = View.VISIBLE
                }
                else{menucard.amount.visibility =
                    View.GONE}

                menucard.cardview.setOnClickListener{
                    activity?.getPreferences(0)?.edit()?.putString("cat", array[i].cat)?.apply()
                    activity?.getPreferences(0)?.edit()?.putInt("index", i)?.apply()
                    moveToMenuDetail()
                }
                fragment_menu_items.findViewById<LinearLayout>(R.id.layout)
                    .addView(menucard)
            }

            return fragment_menu_items
        }

        val foodview = buildMenu2(vm.menu.foodmenu)
        val drinkview = buildMenu2(vm.menu.drinkmenu)
        val appetizerview = buildMenu2(vm.menu.appetizermenu)
        val views = Views(foodview, drinkview, appetizerview)

        vm.check()
        setupViewPager(views)
    }

    fun noMenu(){

        fun noMenu2():View{
            val fragment_menu_items = layoutInflater.inflate(R.layout.fragment_menu_items, null)

            val notificationcard = layoutInflater.inflate(R.layout.notificationcard, null)
            notificationcard.notificationtext.text = "App is only usable online. Please check your internet connection"
            fragment_menu_items.findViewById<LinearLayout>(R.id.layout)
                .addView(notificationcard)

            return fragment_menu_items
        }

        val views = Views(noMenu2(), noMenu2(), noMenu2())

        setupViewPager(views)

    }
    @SuppressLint("SetTextI18n")
    fun setupViewPager(views:Views) {
        foodvp.adapter = VPAdapter(views)
        foodvp.currentItem = 0
        foodvp.offscreenPageLimit = 3
        getActivity()?.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.loadingscreenmain2)?.visibility =
            View.GONE
    }

    class VPAdapter(views: Views): PagerAdapter(){

        val foodview = views.foodview
        val drinkview = views.drinkview
        val appetizerview = views.appetizerview

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Foods"
                1 -> "Drinks"
                2 -> "Appetizers"
                else -> "Food"
            }
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            lateinit var vg:ViewGroup
            when (position) {
                0 -> vg = foodview as ViewGroup
                1 -> vg = drinkview as ViewGroup
                2 -> vg = appetizerview as ViewGroup
                else -> vg = foodview as ViewGroup
            }
            container.addView(vg)
            return vg
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
        }
    }

    private fun moveToProcessPayment() {
        view?.let {
            Navigation.findNavController(it)
                .navigate(R.id.toprocesspayment) }
    }

    private fun moveToMenuDetail(){
        view?.let {
            Navigation.findNavController(it)
                .navigate(R.id.tomenudetail) }
    }



}






