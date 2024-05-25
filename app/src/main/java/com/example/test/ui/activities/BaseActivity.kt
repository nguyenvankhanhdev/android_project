package com.example.test.ui.activities

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.preference.PreferenceManager

import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.test.R
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {
    private lateinit var mProcessDialog: Dialog

    private lateinit var tv_progress_text: TextView

    private var doubleBackToExitPressedOnce= false

    fun showErrorSnackBar(message: String, errorMessage: Boolean){
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        val snackBarView = snackBar.view;
        if (errorMessage){
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this@BaseActivity,R.color.colorSnackBarError)
            )
        }
        else{
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this@BaseActivity,R.color.colorSnackBarSuccess)
            )
        }
        snackBar.show();
    }
    fun showProgressDialog(text: String) {

        val dialogView = layoutInflater.inflate(R.layout.dialog_progress, null)
        tv_progress_text = dialogView.findViewById(R.id.tv_progress_text)

        mProcessDialog = Dialog(this)
        mProcessDialog.setContentView(dialogView)
        mProcessDialog.setCancelable(false)
        mProcessDialog.setCanceledOnTouchOutside(false)

        tv_progress_text.text = text

        mProcessDialog.show()
    }
    fun hideProgressDialog(){
        mProcessDialog.dismiss()
    }
    fun doubleBackToExit() {

        if (doubleBackToExitPressedOnce) {
            onBackPressedDispatcher.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true

        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        @Suppress("DEPRECATION")
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }



}