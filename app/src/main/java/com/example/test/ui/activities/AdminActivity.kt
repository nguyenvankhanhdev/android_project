package com.example.test.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.test.R
import com.example.test.databinding.ActivityAdminBinding
import com.example.test.databinding.ActivityDashboardBinding
import com.example.test.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : BaseActivity() {
    private lateinit var binding: ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setBackgroundDrawable(ContextCompat.getDrawable(this@AdminActivity, R.drawable.app_gradient_color_background))
        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_dashboard) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(

                R.id.navigation_dashboard,
                R.id.navigation_user,
                R.id.navigation_statistical,
                R.id.navigation_products

                )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        doubleBackToExit()
    }
}