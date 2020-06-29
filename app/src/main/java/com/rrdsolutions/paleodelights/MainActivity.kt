package com.rrdsolutions.paleodelights

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.rrdsolutions.paleodelights.ui.deliverystatus.DNService

//import com.rrdsolutions.paleodelights.repositories.MenuItemsDatabase

import kotlinx.android.synthetic.main.activitymain.*
import kotlinx.android.synthetic.main.appbar_main.*

class MainActivity : AppCompatActivity() {

    lateinit var appBarConfiguration: AppBarConfiguration
    val vm:MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activitymain)

        setSupportActionBar(appbar_main_toolbar)

        signup()

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_menu, R.id.nav_deliverystatus, R.id.nav_aboutus, R.id.nav_location),
            activitymain_drawer_layout)

        val navController = findNavController(R.id.content_main_nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfiguration)
        activitymain_navview.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.content_main_nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    //for starting up DNService
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val phonenumber = user.phoneNumber as String

                    vm.setPhoneNumber(phonenumber)

                    vm.checkDelivery{deliveryPresent->
                        if (deliveryPresent){
                            startService(Intent(this, DNService::class.java))
                        }
                    }
                }

            }

        }
    }
    
    fun signup(){
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null){
            val provider = arrayListOf(
                AuthUI.IdpConfig.PhoneBuilder().build()
            )

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(provider)
                    .setTheme(R.style.LoginTheme2)
                    .build(), 1)
        }
    }

}
