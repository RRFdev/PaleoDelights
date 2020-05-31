package com.rrdsolutions.paleodelights

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
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


    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activitymain)

        setSupportActionBar(appbar_main_toolbar)
        FirebaseApp.initializeApp(this)
        signup()

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_menu, R.id.nav_deliverystatus, R.id.nav_aboutus, R.id.nav_location),
            activitymain_drawer_layout)

        val navController = findNavController(R.id.content_main_nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfiguration)
        activitymain_navview.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.content_main_nav_host_fragment)

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {

                    val phonenumber = user.phoneNumber as String
                    Log.d("MainActivity", "phone " + phonenumber)
                    val vm = ViewModelProvider(this).get(MainViewModel::class.java)
                    vm.phonenumber = phonenumber

                    vm.checkDelivery(vm.phonenumber){noDeliveriesInProgress->
                        if (noDeliveriesInProgress == false){
                            startService(Intent(this, DNService::class.java))
                            Log.d("MainActivity", "Delivery in progress detected. DNService started")
                        }
                        else{
                            Log.d("MainActivity", "No delivery in progress detected. DNService not started")
                        }
                    }


                    // User is signed in
                } else {
                    // No user is signed in
                }
                // ...
            }
            else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
    fun signup(){
        val provider = arrayListOf(
            AuthUI.IdpConfig.PhoneBuilder().build()
        )

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null){
            Log.d("_MainActivity", "user already detected")
            val phonenumber = user.phoneNumber as String
            Log.d("_MainActivity", "phone " + phonenumber)
        }
        else{
            Log.d("_MainActivity", "no user detected")
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(provider)
                    .setTheme(R.style.LoginTheme2)
                    .build(), 1)
        }

    }









}
