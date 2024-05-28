package com.example.test.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import com.example.test.R
import com.example.test.firestoreclass.FirestoreClassKT
import com.example.test.models.Address
import com.example.test.utils.Constants
import com.google.android.material.textfield.TextInputLayout

class AddEditAddressActivity : BaseActivity() {

    private var mAddressDetails: Address? = null
    private lateinit var toolbar_add_edit_address_activity: Toolbar
    private lateinit var et_full_name: EditText
    private lateinit var et_phone_number: EditText
    private lateinit var et_address: EditText
    private lateinit var et_zip_code: EditText
    private lateinit var rb_other: RadioButton
    private lateinit var tv_title:TextView
    private lateinit var et_additional_note: EditText
    private lateinit var et_other_details: EditText
    private lateinit var rb_home: RadioButton
    private lateinit var rb_office: RadioButton
    private lateinit var rg_type: RadioGroup
    private lateinit var til_other_details: TextInputLayout
    private lateinit var btn_submit_address: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_edit_address)
        toolbar_add_edit_address_activity = findViewById(R.id.toolbar_add_edit_address_activity)
        et_full_name = findViewById(R.id.et_full_name)
        et_phone_number = findViewById(R.id.et_phone_number)
        et_address = findViewById(R.id.et_address)
        et_zip_code = findViewById(R.id.et_zip_code)
        rb_other = findViewById(R.id.rb_other)
        tv_title =findViewById(R.id.tv_title)
        rg_type = findViewById(R.id.rg_type)
        til_other_details =findViewById(R.id.til_other_details)
        btn_submit_address = findViewById(R.id.btn_submit_address)
        et_additional_note = findViewById(R.id.et_additional_note)
        et_other_details = findViewById(R.id.et_other_details)
        rb_home = findViewById(R.id.rb_home)
        rb_office = findViewById(R.id.rb_office)


        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mAddressDetails =
                intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)!!
        }

        if (mAddressDetails != null) {
            if (mAddressDetails!!.id.isNotEmpty()) {

                tv_title.text = resources.getString(R.string.title_edit_address)
                btn_submit_address.text = resources.getString(R.string.btn_lbl_update)

                et_full_name.setText(mAddressDetails?.name)
                et_phone_number.setText(mAddressDetails?.mobileNumber)
                et_address.setText(mAddressDetails?.address)
                et_zip_code.setText(mAddressDetails?.zipCode)
                et_additional_note.setText(mAddressDetails?.additionalNote)

                when (mAddressDetails?.type) {
                    Constants.HOME -> {
                        rb_home.isChecked = true
                    }
                    Constants.OFFICE -> {
                        rb_office.isChecked = true
                    }
                    else -> {
                        rb_other.isChecked = true
                        til_other_details.visibility = View.VISIBLE
                        et_other_details.setText(mAddressDetails?.otherDetails)
                    }
                }
            }
        }

        setupActionBar()
        btn_submit_address.setOnClickListener {
            saveAddressToFirestore()
        }

        rg_type.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other) {
                til_other_details.visibility=View.VISIBLE
            }
            else{
                til_other_details.visibility=View.GONE
            }
        }
    }
    private fun setupActionBar() {

        setSupportActionBar(toolbar_add_edit_address_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_add_edit_address_activity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun validateData(): Boolean {
        return when {
            TextUtils.isEmpty(et_full_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }
            TextUtils.isEmpty(et_phone_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }
            TextUtils.isEmpty(et_address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }
            TextUtils.isEmpty(et_zip_code.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            rb_other.isChecked && TextUtils.isEmpty(
                et_zip_code.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }
    private fun saveAddressToFirestore() {
        val fullName: String = et_full_name.text.toString().trim { it <= ' ' }
        val phoneNumber: String = et_phone_number.text.toString().trim { it <= ' ' }
        val address: String = et_address.text.toString().trim { it <= ' ' }
        val zipCode: String = et_zip_code.text.toString().trim { it <= ' ' }
        val additionalNote: String = et_additional_note.text.toString().trim { it <= ' ' }
        val otherDetails: String = et_other_details.text.toString().trim { it <= ' ' }
        if (validateData()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val addressType: String = when {
                rb_home.isChecked -> {
                    Constants.HOME
                }
                rb_office.isChecked -> {
                    Constants.OFFICE
                }

                else -> {
                    Constants.OTHER
                }
            }
            val addressModel = Address(
                FirestoreClassKT().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )
            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                FirestoreClassKT().updateAddress(
                    this@AddEditAddressActivity,
                    addressModel,
                    mAddressDetails!!.id
                )
            } else {
                FirestoreClassKT().addAddress(this@AddEditAddressActivity, addressModel)
            }
        }
    }
    fun addUpdateAddressSuccess() {
        hideProgressDialog()
        val notifySuccessMessage:String =if (mAddressDetails!=null && mAddressDetails!!.id.isNotEmpty()){
            resources.getString(R.string.msg_your_address_update_successfully)
        }else{
            resources.getString(R.string.err_your_address_added_successfully)
        }
        Toast.makeText(
            this@AddEditAddressActivity,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()
        setResult(RESULT_OK)

        finish()
    }


}