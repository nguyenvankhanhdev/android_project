package com.example.test.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.test.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var btn_submit: Button
    private lateinit var et_email: EditText
    private lateinit var toolbar_forgot_password_activity: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)


        btn_submit = findViewById(R.id.btn_submit)
        et_email = findViewById(R.id.et_email)
        toolbar_forgot_password_activity = findViewById(R.id.toolbar_forgot_password_activity)
        btn_submit.setOnClickListener {
            val email: String = et_email.text.toString().trim { it <= ' ' }
            if (email.isEmpty()) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Vui lòng nhập Email ",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        run {
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    resources.getString(R.string.email_sent_success),
                                    Toast.LENGTH_LONG
                                ).show()

                                finish()

                            } else {
                                showErrorSnackBar(task.exception!!.message.toString(), true)

                            }
                        }
                    }
            }
        }
        setupActionBar()


    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_forgot_password_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_forgot_password_activity.setNavigationOnClickListener { onBackPressed() }
    }
}