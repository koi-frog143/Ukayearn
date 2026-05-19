package com.citu.ukayearn

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Find the container that holds the floating background
        val bottomNavContainer = findViewById<FrameLayout>(R.id.bottom_nav_container)

        bottomNav.setupWithNavController(navController)

        // Magic trick: Hide the nav bar if we are on splash or login!
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.nav_splash || destination.id == R.id.nav_login) {
                bottomNavContainer.visibility = View.GONE
            } else {
                bottomNavContainer.visibility = View.VISIBLE
            }
        }
    }
}