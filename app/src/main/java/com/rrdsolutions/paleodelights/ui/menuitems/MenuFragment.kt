package com.rrdsolutions.paleodelights.ui.menuitems

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Fade

import com.rrdsolutions.paleodelights.MenuModel
import com.rrdsolutions.paleodelights.R

import com.rrdsolutions.paleodelights.ui.getaddress.GetAddressFragment
import com.rrdsolutions.paleodelights.ui.menudetail.MenuDetailFragment

//import com.rrdsolutions.paleodelights.repositories.MenuObject
import com.rrdsolutions.paleodelights.ui.processpayment.ProcessPaymentFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.fragment_menu_items.view.*
import kotlinx.android.synthetic.main.fragment_menudetail.view.*
import kotlinx.android.synthetic.main.menucard.view.*
import kotlinx.android.synthetic.main.menucard.view.amount
import kotlinx.android.synthetic.main.menucard.view.image
import kotlinx.android.synthetic.main.menucard.view.name
import kotlinx.android.synthetic.main.menucard.view.price

import kotlinx.android.synthetic.main.notificationcard.view.*
import java.math.BigDecimal
import java.math.RoundingMode


class MenuFragment : Fragment() {

    lateinit var vm: MenuViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_menu, container, false)
        vm = ViewModelProvider(this).get(MenuViewModel::class.java)

        activity?.findViewById<Toolbar>(R.id.appbar_main_toolbar)?.title = "Menu"
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.loadingscreenmain2)?.visibility =
            View.VISIBLE

        if (MenuModel.dataloaded == true){
            vm.loadOffline{ taskCompleted->
                if (taskCompleted) buildMenu()
                else {
                    noInternet()
                    getActivity()?.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.loadingscreenmain2)?.visibility =
                    View.INVISIBLE
                }
            }
        }
        if (MenuModel.dataloaded == false){
            vm.loadOnline{ taskCompleted->
                if (taskCompleted) buildMenu()
                else {
                    noInternet()
                    getActivity()?.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.loadingscreenmain2)?.visibility =
                        View.INVISIBLE
                }
            }
        }

        MenuModel.emptyorderLiveData.observe(viewLifecycleOwner, Observer<Boolean> {
            if (it == true) checkoutbutton.visibility = View.GONE
            if (it == false) checkoutbutton.visibility = View.VISIBLE
        })

        checkoutbutton.setOnClickListener {
            moveToCheckout()
        }

    }

    fun buildMenu(){
        checkoutbutton.visibility = View.GONE
        fun buildMenu2(array: MutableList<MenuModel.MenuItems>): View {
            val fragment_menu_items = layoutInflater.inflate(R.layout.fragment_menu_items, null)

            for (i in 0 until array.size) {
                val menucard = layoutInflater.inflate(R.layout.menucard2, null)

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
        FObject.drinkview = buildMenu2(MenuModel.menu.drinkmenu)
        FObject.foodview = buildMenu2(MenuModel.menu.foodmenu)
        FObject.appetizerview = buildMenu2(MenuModel.menu.appetizermenu)

        MenuModel.check()
        setupViewPager()
    }



    fun noInternet(){
        Log.d("menu", "noInternet() executed")
        fun noInternet2():View{
            val fragment_menu_items = layoutInflater.inflate(R.layout.fragment_menu_items, null)

            val notificationcard = layoutInflater.inflate(R.layout.notificationcard, null)
            notificationcard.notificationtext.text = "App is only usable online. Please check your internet connection"
            fragment_menu_items.findViewById<LinearLayout>(R.id.layout)
                .addView(notificationcard)

            return fragment_menu_items
        }
        FObject.drinkview = noInternet2()
        FObject.foodview = noInternet2()
        FObject.appetizerview =noInternet2()
        setupViewPager()

    }
    @SuppressLint("SetTextI18n")
    fun setupViewPager() {

        foodvp.adapter = ViewPagerAdapter2(childFragmentManager)
        foodvp.currentItem = 0
        foodvp.offscreenPageLimit = 3
        getActivity()?.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.loadingscreenmain2)?.visibility =
            View.INVISIBLE
    }

    class FoodFrag : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return FObject.foodview
        }
    }

    class DrinkFrag : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return FObject.drinkview
        }
    }

    class AppetizerFrag : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return FObject.appetizerview
        }
    }

    class ViewPagerAdapter2 internal constructor(fm: FragmentManager) :
        FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int {
            //set number of pages
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            //set page titles. ViewPager MUST have PagerTabStrip inside to show page titles
            return when (position) {
                0 -> "Foods"
                1 -> "Drinks"
                2 -> "Appetizers"
                else -> "Food"
            }
        }

        override fun getItem(position: Int): Fragment {

            return when (position) {
                0 -> FoodFrag()
                1 -> DrinkFrag()
                2 -> AppetizerFrag()
                else -> FoodFrag()
            }

        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
        }
    }


    private fun moveToCheckout() {
        fragmentManager?.beginTransaction()
            ?.replace(R.id.content_main_nav_host_fragment, ProcessPaymentFragment(), "Tag1")
            ?.addToBackStack("Tag1")
            ?.commit()
    }

    private fun moveToMenuDetail(){
        //DO NOT USE THIS METHOD! THIS METHOD CAUSES CRASH WHEN SCREEN ROTATES!
//        activity?.supportFragmentManager?.beginTransaction()
//            ?.replace(R.id.content_main_nav_host_fragment, DetailFragment())
//            ?.addToBackStack(null)
//            ?.commit()

        val fm = fragmentManager
        fm?.beginTransaction()
            ?.replace(R.id.content_main_nav_host_fragment, MenuDetailFragment())
            ?.addToBackStack(null)
            ?.commit()

    }

    object FObject {
        var foodview: View? = null
        var drinkview: View? = null
        var appetizerview: View? = null
    }

}






