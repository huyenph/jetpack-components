package com.utildev.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        setupBottomNavigation()
        setupDrawer()
        setupToolbar()
    }

    private fun setupBottomNavigation() {
//        val appBarConfiguration = AppBarConfiguration(bottomView.menu)
        NavigationUI.setupWithNavController(bottomView, navController)
    }

    private fun setupDrawer() {
        nav_view.setupWithNavController(navController)
    }

    private fun setupToolbar() {
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.mainFragment, R.id.detailFragment, R.id.infoFragment), drawer)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}
