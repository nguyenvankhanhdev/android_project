package com.example.test.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.example.test.R
import com.example.test.firestoreclass.FirestoreClassKT
import com.example.test.models.User
import com.example.test.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var tv_register: TextView
    private lateinit var et_email: EditText
    private lateinit var et_password: EditText
    private lateinit var tv_forgot_password: TextView
    private lateinit var btn_login: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        addControl()
        tv_register.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        tv_forgot_password.setOnClickListener(this)
    }
    fun addControl() {
        tv_register = findViewById(R.id.tv_register)
        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)
        tv_forgot_password = findViewById(R.id.tv_forgot_password)
        btn_login = findViewById(R.id.btn_login)
    }
    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.tv_forgot_password -> {
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }
                R.id.btn_login -> {
                    logInRegisteredUser()
                }
                R.id.tv_register -> {
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false }
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false }
            else -> { true }
        }
    }
    private fun saveUserRoleToSharedPreferences(role: String) {
        val sharedPreferences = getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.USER_ROLE, role)
        editor.apply()
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        val intent: Intent
        saveUserRoleToSharedPreferences(user.role)
        if (user.profileCompleted == 0) {
            intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            intent.putExtra(Constants.USER_ROLE, user.role)
        } else {
            intent = Intent(this@LoginActivity, DashboardActivity::class.java)
            intent.putExtra(Constants.USER_ROLE, user.role)

        }
        startActivity(intent)
        finish()
    }
    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val email = et_email.text.toString().trim { it <= ' ' }
            val password = et_password.text.toString().trim { it <= ' ' }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirestoreClassKT().getUserDetails(this@LoginActivity)
                    } else {
                        Log.e("Login Error", "Failed to sign in: ${task.exception?.message}")
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }
}