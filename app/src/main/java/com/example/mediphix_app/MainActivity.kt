package com.example.mediphix_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navBottom: com.google.android.material.bottomnavigation.BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(1000)
        installSplashScreen()

        setContentView(R.layout.activity_main)

        navBottom = findViewById(R.id.bottom_nav)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.home, R.id.newDrug, R.id.profile)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        navBottom.setupWithNavController(navController)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        bottomNavigationView.visibility = View.GONE
    }

    override fun onBackPressed() {
        // Back button always navigates to home page
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        val navController = findNavController(R.id.nav_host_fragment)

        if (navController.currentDestination?.id == R.id.login) {
            return
        }else if (bottomNavigationView.visibility == View.VISIBLE) {
            val action = NavGraphDirections.actionGlobalHome()
            findNavController(R.id.nav_host_fragment).navigate(action)
        } else {
            // If on the home fragment, navigate to the home fragment using the back stack
            navController.popBackStack()
        }
    }

    fun navigateToHomePage() {
        // Sets the bottom nav to visible
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.visibility = View.VISIBLE
    }
}