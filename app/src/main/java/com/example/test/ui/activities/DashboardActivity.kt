package com.example.test.ui.activities

import android.os.Bundle
import android.annotation.SuppressLint
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.test.R
import com.example.test.databinding.ActivityDashboardBinding
import com.example.test.models.UserRole
import com.example.test.utils.Constants

class DashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private var userRole: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setBackgroundDrawable(ContextCompat.getDrawable(this@DashboardActivity, R.drawable.app_gradient_color_background))
        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_dashboard) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_products,
                R.id.navigation_dashboard,
                R.id.navigation_orders,
                R.id.navigation_sold_products
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        userRole = intent.getStringExtra(Constants.USER_ROLE) ?: getUserRoleFromSharedPreferences()

        if (userRole == UserRole.ADMIN.name) {
            navView.menu.findItem(R.id.navigation_products).isVisible = true
        } else {
            navView.menu.findItem(R.id.navigation_products).isVisible = false
        }
    }
    private fun getUserRoleFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE)
        return sharedPreferences.getString(Constants.USER_ROLE, null)
    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        doubleBackToExit()
    }




}
