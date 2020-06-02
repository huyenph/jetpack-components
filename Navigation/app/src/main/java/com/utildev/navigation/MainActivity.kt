package com.utildev.navigation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationAdapter.AdapterItemListener {

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

        Intent.FLAG_ACTIVITY_NEW_TASK

        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.main_nav_graph)
            .setDestination(R.id.mainFragment)
            .createPendingIntent()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "channelId",
                    "Deep link",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }
        val builder = NotificationCompat.Builder(this, "channelId")
            .setContentTitle("Navigation")
            .setContentText("Deep link to Android")
            .setSmallIcon(R.drawable.ic_home_black_24dp)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        notificationManager.notify(0, builder.build())
    }

    private fun setupBottomNavigation() {
//        val appBarConfiguration = AppBarConfiguration(bottomView.menu)
        NavigationUI.setupWithNavController(bottomView, navController)
    }

    private fun setupDrawer() {
//        nav_view.setupWithNavController(navController)
        val menu: List<String> = listOf("Menu 1", "Menu 2", "Menu 3")
        val navAdapter = NavigationAdapter(menu, this)
        rcv.run {
            adapter = navAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        NavigationUI.setupWithNavController(nav_view, navController)
    }

    private fun setupToolbar() {
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.mainFragment, R.id.detailFragment, R.id.infoFragment),
            drawer
        )
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onItemClick(position: Int) {
        Log.d("aaa", "$position")
        when (position) {
            0 -> navController.navigate(R.id.mainFragment)
            1 -> navController.navigate(R.id.detailFragment)
            2 -> navController.navigate(R.id.infoFragment)
        }
        drawer.closeDrawer(GravityCompat.START)
    }

    override fun onSupportNavigateUp(): Boolean = true

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
