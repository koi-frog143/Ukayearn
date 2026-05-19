package com.citu.ukayearn

import android.os.Bundle
import com.citu.ukayearn.data.Database
import android.view.View
import android.view.ViewGroup
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
        compactBottomNavSpacing(bottomNav)
        refreshChatBadge()

        // Magic trick: Hide the nav bar if we are on splash, login, or checkout!
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.nav_splash ||
                destination.id == R.id.nav_login ||
                destination.id == R.id.nav_checkout
            ) {
                bottomNavContainer.visibility = View.GONE
            } else {
                bottomNavContainer.visibility = View.VISIBLE
            }
            refreshChatBadge()
        }
    }

    fun refreshChatBadge() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val unreadCount = Database.unreadMessageCountForCurrentUser()
        if (unreadCount > 0) {
            bottomNav.getOrCreateBadge(R.id.nav_chat).apply {
                number = unreadCount
                isVisible = true
                backgroundColor = getColor(R.color.error)
                badgeTextColor = getColor(R.color.white)
            }
        } else {
            bottomNav.removeBadge(R.id.nav_chat)
        }
    }

    private fun compactBottomNavSpacing(bottomNav: BottomNavigationView) {
        bottomNav.post {
            val menuView = bottomNav.getChildAt(0) as? ViewGroup ?: return@post
            bottomNav.clipChildren = false
            bottomNav.clipToPadding = false
            menuView.clipChildren = false
            menuView.clipToPadding = false
            for (index in 0 until menuView.childCount) {
                val itemView = menuView.getChildAt(index) as? ViewGroup ?: continue
                val icon = itemView.findViewById<View>(
                    com.google.android.material.R.id.navigation_bar_item_icon_view
                )
                val smallLabel = itemView.findViewById<View>(
                    com.google.android.material.R.id.navigation_bar_item_small_label_view
                )
                val largeLabel = itemView.findViewById<View>(
                    com.google.android.material.R.id.navigation_bar_item_large_label_view
                )

                itemView.minimumHeight = 0
                itemView.clipChildren = false
                itemView.clipToPadding = false
                itemView.setPadding(0, 0, 0, 0)
                icon?.translationY = 4.dp()
                smallLabel?.translationY = (-3).dp()
                largeLabel?.translationY = (-3).dp()
            }
        }
    }

    private fun Int.dp(): Float = this * resources.displayMetrics.density
}
