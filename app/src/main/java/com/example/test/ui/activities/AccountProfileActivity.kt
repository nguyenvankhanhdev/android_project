package com.example.test.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.test.R
import com.example.test.firestoreclass.FirestoreClass
import com.example.test.firestoreclass.FirestoreClassKT
import com.example.test.models.User
import com.example.test.utils.Constants
import com.example.test.utils.GlideLoader
import java.io.IOException



class AccountProfileActivity : BaseActivity(), View.OnClickListener  {
    private lateinit var et_first_name: EditText
    private lateinit var et_last_name: EditText
    private lateinit var et_email: EditText
    private lateinit var iv_user_photo: ImageView
    private lateinit var et_mobile_number: EditText
    private lateinit var btn_save: Button
    private lateinit var rb_male: RadioButton
    private lateinit var rb_female: RadioButton
    private lateinit var rb_user: RadioButton
    private lateinit var rb_admin: RadioButton
    private lateinit var mUserDetails: User
    private lateinit var tv_title: TextView
    private lateinit var toolbar_user_profile_activity: Toolbar

    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_profile)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        toolbar_user_profile_activity = findViewById(R.id.toolbar_user_profile_activity_admin)
        rb_male = findViewById(R.id.rb_male_admin)
        rb_female = findViewById(R.id.rb_female_admin)
        et_mobile_number = findViewById(R.id.et_mobile_number_admin)
        et_first_name = findViewById(R.id.et_first_name_admin)
        et_last_name = findViewById(R.id.et_last_name_admin)
        et_email = findViewById(R.id.et_email_admin)
        iv_user_photo = findViewById(R.id.iv_user_photo)
        btn_save = findViewById(R.id.btn_save)
        rb_user=findViewById(R.id.rb_user_admin)
        rb_admin=findViewById(R.id.rb_admin_admin)
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        } else {
            Toast.makeText(this@AccountProfileActivity, "Missing user details!", Toast.LENGTH_SHORT)
                .show()
            finish()
        }
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }
        if(mUserDetails.image == null){
            mUserDetails.image = R.drawable.ic_user_placeholder.toString()
        }
        tv_title = findViewById(R.id.tv_title_admin)
        if (mUserDetails.profileCompleted == 0) {
            tv_title.text = resources.getString(R.string.title_complete_profile)
            et_first_name.isEnabled = false
            et_first_name.setText(mUserDetails?.firstName ?: "")

            et_last_name.isEnabled = false
            et_last_name.setText(mUserDetails?.lastName ?: "")
            et_email.isEnabled = false
            et_email.setText(mUserDetails?.email ?: "")
        } else {
            setupActionBar()
            tv_title.text = resources.getString(R.string.title_complete_profile)

            GlideLoader(this@AccountProfileActivity).loadUserPicture(mUserDetails.image, iv_user_photo)
            et_first_name.setText(mUserDetails.firstName)
            et_last_name.setText(mUserDetails.lastName)
            et_email.isEnabled = false
            et_email.setText(mUserDetails.email)
            if (mUserDetails.mobile != 0L) {
                et_mobile_number.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == Constants.MALE) {
                rb_male.isChecked = true
            } else {
                rb_female.isChecked = true
            }
            if(mUserDetails.role=="USER")
            {
                rb_user.isChecked=true
            }
            else
            {
                rb_admin.isChecked=true
            }
        }
        iv_user_photo.setOnClickListener(this@AccountProfileActivity)

        btn_save.setOnClickListener(this@AccountProfileActivity)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_user_profile_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_user_profile_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_MEDIA_IMAGES
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this@AccountProfileActivity)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_save -> {
                    if (validateUserProfileDetailsAdmin()) {
                        showProgressDialog(resources.getString(R.string.please_wait))
                        if (mSelectedImageFileUri != null) {
                            FirestoreClassKT().uploadImageToCloudStorage(
                                this,
                                mSelectedImageFileUri,
                                Constants.USER_PROFILE_IMAGE
                            )
                        } else {
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()
        val firstName = et_first_name.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        val lastName = et_last_name.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }
        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }
        val gender = if (rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        val role:String
        if(rb_user.isChecked)
        {
            role="USER"
        }
        else
        {
            role="ADMIN"
        }

        if (mUserProfileImageURl.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURl
        }
        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }

        if(role.isNotEmpty() && role != mUserDetails.role){
            userHashMap[Constants.USER_ROLE] = role
        }

        if (mUserDetails.profileCompleted == 0) {
            userHashMap[Constants.COMPLETE_PROFILE] = 1
        }
        FirestoreClassKT().updateUserProfileData(
            this@AccountProfileActivity,
            userHashMap,mUserDetails.id
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AccountProfileActivity)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mSelectedImageFileUri = data.data!!
                        GlideLoader(this@AccountProfileActivity).loadUserPicture(
                            mSelectedImageFileUri!!,
                            iv_user_photo
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@AccountProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateUserProfileDetailsAdmin(): Boolean {
        return when {
            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }

            else -> {
                true
            }
        }
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@AccountProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()
        startActivity(Intent(this@AccountProfileActivity, AdminActivity::class.java))
        finish()
    }

    fun imageUploadSuccess(imageURL: String) {
        mUserProfileImageURl = imageURL
        updateUserProfileDetails()
    }

}