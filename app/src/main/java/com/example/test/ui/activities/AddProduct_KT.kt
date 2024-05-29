package com.example.test.ui.activities

import android.os.Bundle
import android.view.View
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.test.R
import com.example.test.firestoreclass.FirestoreClass
import com.example.test.firestoreclass.FirestoreClassKT
import com.example.test.models.Product_KT
import com.example.test.models.SizeProduct
import com.example.test.models.SizeProduct_KT
import com.example.test.utils.Constants
import com.example.test.utils.GlideLoader
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.IOException

class AddProduct_KT : BaseActivity(), View.OnClickListener {
    private var mSelectedImageFileUri: Uri? = null
    private var mProductImageURL: String = ""
    private lateinit var toolbar_add_product_activity: Toolbar
    private lateinit var iv_add_update_product: ImageView
    private lateinit var iv_product_image: ImageView
    private lateinit var et_product_title: EditText
    private lateinit var et_product_price: EditText
    private lateinit var et_product_description: EditText
    private lateinit var spinnerSize: Spinner
    private lateinit var et_quantity: EditText
    private lateinit var btn_add_size: Button
    private lateinit var sizes_container: LinearLayout
    private lateinit var btn_submit_add_product: Button
    private lateinit var spinner_type:Spinner
    private var selectedSize: String? = null
    private val sizeQuantityList: MutableList<Pair<String, Int>> = mutableListOf()
    private val mFireStore = FirebaseFirestore.getInstance()
    private var selectedType:String? = null
    private val typeList: MutableList<Pair<String, String>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product_kt)

        toolbar_add_product_activity = findViewById(R.id.toolbar_add_product_activity)
        iv_add_update_product = findViewById(R.id.iv_add_update_product)
        iv_product_image = findViewById(R.id.iv_product_image)
        et_product_title = findViewById(R.id.et_product_title)
        et_product_price = findViewById(R.id.et_product_price)
        et_product_description = findViewById(R.id.et_product_description)
        spinnerSize = findViewById(R.id.spinner_size)
        et_quantity = findViewById(R.id.et_quantity)
        btn_add_size = findViewById(R.id.btn_add_size)
        sizes_container = findViewById(R.id.sizes_container)
        spinner_type= findViewById(R.id.spinner_type)
        btn_submit_add_product = findViewById(R.id.btn_submit_add_product)

        setupActionBar()
        iv_add_update_product.setOnClickListener(this)
        btn_add_size.setOnClickListener(this)
        btn_submit_add_product.setOnClickListener(this)

        setupSpinner()
        setSpinnerType()
        spinnerSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedSize = parent.getItemAtPosition(position) as String
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        spinner_type.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected( parent: AdapterView<*>,  view: View?,  position: Int, id: Long)
            {
                selectedType = parent.getItemAtPosition(position)as String
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
    private fun setupActionBar() {
        setSupportActionBar(toolbar_add_product_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_add_product_activity.setNavigationOnClickListener { onBackPressed() }
    }
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_add_update_product -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_MEDIA_IMAGES
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this@AddProduct_KT)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_add_size -> {
                    addSizeItem()
                }
                R.id.btn_submit_add_product -> {
                    if (validateProductDetails()) {
                        uploadProductImage()
                    }
                }
            }
        }
    }

    private fun addSizeItem() {
        val size = selectedSize
        val quantity = et_quantity.text.toString().trim()
        if (!size.isNullOrEmpty() && quantity.isNotEmpty()) {
            addSizeView(size, quantity.toInt())
            et_quantity.text.clear()
        } else {
            Toast.makeText(this, "Please select a size and enter quantity", Toast.LENGTH_SHORT).show()
        }
    }


    private fun addSizeView(size: String?, quantity: Int) {
        val sizeView = layoutInflater.inflate(R.layout.item_size, null)
        sizeView.findViewById<TextView>(R.id.tv_size).text = size
        sizeView.findViewById<TextView>(R.id.tv_quantity).text = quantity.toString()
        sizes_container.addView(sizeView)
        sizeQuantityList.add(Pair(size!!, quantity))
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddProduct_KT)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    iv_add_update_product.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_vector_edit
                        )
                    )
                    mSelectedImageFileUri = data.data!!
                    try {
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, iv_product_image)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }
    private fun uploadProductImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClassKT().uploadImageToCloudStorage(
            this@AddProduct_KT,
            mSelectedImageFileUri,
            Constants.PRODUCT_IMAGE
        )
    }
    private fun validateProductDetails(): Boolean {
        return when {
            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }
            TextUtils.isEmpty(et_product_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }
            TextUtils.isEmpty(et_product_price.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }
            TextUtils.isEmpty(et_product_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }
            sizes_container.childCount == 0 -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_sizes), true)
                false
            }
            else -> {
                true
            }
        }
    }
    fun imageUploadSuccess(imageURL: String) {
        mProductImageURL = imageURL
        uploadProductDetails()
    }

    private fun uploadProductDetails() {
        val selectedTypeName = selectedType
        val selectedTypeId = typeList.firstOrNull { it.first == selectedTypeName }?.second
        if (selectedTypeId == null) {
            showErrorSnackBar("Invalid product type selected.", true)
            return
        }

        val product = Product_KT(
            product_id = "",
            title = et_product_title.text.toString().trim(),
            price = et_product_price.text.toString().trim(),
            description = et_product_description.text.toString().trim(),
            image = mProductImageURL,
            shoeTypeId = selectedTypeId
        )
        val documentReference = mFireStore.collection(Constants.PRODUCTS).document()
        product.product_id = documentReference.id
        documentReference.set(product, SetOptions.merge())
            .addOnSuccessListener {
                val productId = documentReference.id
                uploadSizeProductDetails(productId)
                productUploadSuccess()
            }
            .addOnFailureListener { e ->
                hideProgressDialog()
                Log.e(this.javaClass.simpleName, "Error while uploading the product details.", e)
            }
    }

    private fun uploadSizeProductDetails(productId: String) {
        for ((size, quantity) in sizeQuantityList) {
            val sizeInt = size.toIntOrNull()
            if (sizeInt != null) {
                val documentReference = mFireStore.collection(Constants.SIZE_PRODUCTS).document()
                val sizeProduct = SizeProduct_KT(
                    size_id = documentReference.id,
                    size = sizeInt,
                    quantity = quantity,
                    product_id = productId
                )
                documentReference.set(sizeProduct)
                    .addOnFailureListener { e ->
                        hideProgressDialog()
                        Log.e(this.javaClass.simpleName, "Error while uploading size product details.", e)
                    }
            } else {
                Log.e(this.javaClass.simpleName, "Invalid size: $size")
            }
        }
    }

    private fun setSpinnerType() {
        FirestoreClassKT().getType { types ->
            typeList.clear()
            typeList.addAll(types)
            setupSpinner(typeList.map { it.first })
        }
    }

    private fun setupSpinner(typeNames: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_type.adapter = adapter
    }


    private fun setupSpinner() {
        val sizes = (36..43).map { it.toString() }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sizes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSize.adapter = adapter
    }

    fun productUploadSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@AddProduct_KT,
            resources.getString(R.string.product_uploaded_success_message),
            Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(this@AddProduct_KT, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }


}
