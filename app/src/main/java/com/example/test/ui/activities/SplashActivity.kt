package com.example.test.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.test.R
import com.example.test.firestoreclass.FirestoreClass
import com.example.test.firestoreclass.FirestoreClassKT
import com.example.test.models.UserRole
import com.example.test.utils.Constants

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        Handler().postDelayed({
            val currentUserID = FirestoreClass.getCurrentUserID()
            if (currentUserID.isNotEmpty()) {
                val sharedPreferences = getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE)
                val userRole = sharedPreferences.getString(Constants.USER_ROLE, null)

                val intent = when (userRole) {
                    UserRole.ADMIN.name -> Intent(this@SplashActivity, AdminActivity::class.java)
                    UserRole.USER.name -> Intent(this@SplashActivity, DashboardActivity::class.java)
                    else -> Intent(this@SplashActivity, LoginActivity::class.java)
                }
                startActivity(intent)
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()
        }, 1500)
    }
}