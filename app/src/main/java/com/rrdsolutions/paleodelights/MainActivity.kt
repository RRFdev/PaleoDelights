package com.rrdsolutions.paleodelights

//import com.rrdsolutions.paleodelights.repositories.MenuItemsDatabase

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.rrdsolutions.paleodelights.ui.deliverystatus.DNService
import kotlinx.android.synthetic.main.activitymain.*
import kotlinx.android.synthetic.main.appbar_main.*

class MainActivity : AppCompatActivity() {

    lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activitymain)

        setSupportActionBar(appbar_main_toolbar)

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


}
