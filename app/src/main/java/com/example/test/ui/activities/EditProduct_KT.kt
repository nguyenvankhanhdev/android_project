package com.example.test.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import com.example.test.firestoreclass.FirestoreClass
import com.example.test.firestoreclass.FirestoreClassKT
import com.example.test.models.Product
import com.example.test.models.Product_KT
import com.example.test.models.SizeProduct
import com.example.test.models.SizeProduct_KT
import com.example.test.utils.Constants
import com.example.test.utils.Constants.PRODUCT_IMAGE
import com.example.test.utils.GlideLoader
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException

class EditProduct_KT : BaseActivity(), View.OnClickListener {
    private lateinit var toolbar_add_product_activity: Toolbar
    private lateinit var etProductTitle: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var iv_edit_update_product: ImageView
    private lateinit var etProductDescription: EditText
    private lateinit var spinnerSize: Spinner
    private lateinit var etQuantity: EditText
    private lateinit var btnAddSize: Button
    private lateinit var sizesContainer: LinearLayout
    private lateinit var btnSubmit: Button
    private var mSelectedImageFileUri: Uri? = null
    private var mProductImageURL: String = ""
    private lateinit var iv_edit_product_image: ImageView
    private lateinit var spinner_type_edit:Spinner
    private lateinit var product: Product
    private lateinit var db: FirebaseFirestore
    private lateinit var productRef: CollectionReference
    private lateinit var sizeProductRef: CollectionReference
    private val sizeQuantityList: MutableList<Pair<String, Int>> = mutableListOf()
    private val typeList: MutableList<Pair<String, String>> = mutableListOf()

    private var selectedType:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_product_kt)

        // Ánh xạ view từ layout
        toolbar_add_product_activity = findViewById(R.id.toolbar_add_product_activity)
        etProductTitle = findViewById(R.id.et_edit_product_title)
        etProductPrice = findViewById(R.id.et_edit_product_price)
        etProductDescription = findViewById(R.id.et_edit_product_description)
        spinnerSize = findViewById(R.id.spinner_edit_size)
        etQuantity = findViewById(R.id.et_edit_quantity)
        btnAddSize = findViewById(R.id.btn_edit_add_size)
        sizesContainer = findViewById(R.id.sizes_edit_container)
        spinner_type_edit = findViewById(R.id.spinner_type_edit)
        btnSubmit = findViewById(R.id.btn_submit_edit_product)
        iv_edit_update_product = findViewById(R.id.iv_edit_update_product)
        iv_edit_product_image = findViewById(R.id.iv_edit_product_image)
        setupActionBar()
        setupSpinner()

        db = FirebaseFirestore.getInstance()
        productRef = db.collection("products")
        sizeProductRef = db.collection("size_product")

        spinner_type_edit.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected( parent: AdapterView<*>,  view: View?,  position: Int, id: Long)
            {
                selectedType = parent.getItemAtPosition(position)as String
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            val productId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            loadProductDetails(productId)
        }
        iv_edit_update_product.setOnClickListener(this)
        btnAddSize.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
        setSpinnerType()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    iv_edit_update_product.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_vector_edit
                        )
                    )
                    mSelectedImageFileUri = data.data!!
                    try {
                        GlideLoader(this).loadUserPicture(
                            mSelectedImageFileUri!!,
                            iv_edit_product_image
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_edit_update_product -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Constants.showImageChooser(this@EditProduct_KT)
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        Constants.READ_STORAGE_PERMISSION_CODE
                    )
                }
            }

            R.id.btn_edit_add_size -> {
                addSizeItem()

            }
            R.id.btn_submit_edit_product -> {
                if (validateProductDetails()) {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    if (mSelectedImageFileUri != null) {
                        uploadProductImage()
                    } else {
                        updateProduct()
                    }
                }
            }
        }
    }

    private fun addSizeItem() {
        val selectedSize = spinnerSize.selectedItem.toString()
        val quantity = etQuantity.text.toString().trim()
        if (selectedSize.isNotEmpty() && quantity.isNotEmpty()) {
            addSizeView(selectedSize, quantity.toInt())
            etQuantity.text.clear()
        } else {
            Toast.makeText(this, "Please select a size and enter quantity", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addSizeView(size: String, quantity: Int) {
        val sizeView = layoutInflater.inflate(R.layout.item_size, null)
        sizeView.findViewById<TextView>(R.id.tv_size).text = size
        sizeView.findViewById<TextView>(R.id.tv_quantity).text = quantity.toString()
        sizesContainer.addView(sizeView)
        sizeQuantityList.add(Pair(size, quantity))
    }

    private fun updateProduct() {
        val title = etProductTitle.text.toString().trim()
        val price = etProductPrice.text.toString().trim()
        val description = etProductDescription.text.toString().trim()
        val selectedTypeName = selectedType
        val selectedTypeId = typeList.firstOrNull { it.first == selectedTypeName }?.second

        if (selectedTypeId == null) {
            showErrorSnackBar("Invalid product type selected.", true)
            return
        }
        if (title.isNotEmpty() && price.isNotEmpty() && description.isNotEmpty()) {
            val updatedProduct = Product_KT(
                product_id = product.product_id,
                title = title,
                price = price,
                description = description,
                image = product.image,
                shoeTypeId = selectedTypeId
            )
            productRef.document(product.product_id)
                .set(updatedProduct, SetOptions.merge())
                .addOnSuccessListener {
                    updateSizes()
                }
                .addOnFailureListener { e ->
                    hideProgressDialog()
                    showErrorSnackBar(e.message.toString(), true)
                }
        } else {
            hideProgressDialog()
            showErrorSnackBar("Please enter all the details.", true)
        }
    }

    private fun updateSizes() {
        lifecycleScope.launch {
            for ((size, quantity) in sizeQuantityList) {
                val existingSize = checkSizeExistence(size)
                if (existingSize != null) {
                    updateSize(existingSize, quantity)
                } else {
                    addNewSize(size, quantity)
                }
            }
            hideProgressDialog()
            val intent = Intent(this@EditProduct_KT, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private suspend fun checkSizeExistence(size: String): SizeProduct? {
        return withContext(Dispatchers.IO) {
            val querySnapshot = sizeProductRef
                .whereEqualTo("product_id", product.product_id)
                .whereEqualTo("size_id", size)
                .get()
                .await()
            if (!querySnapshot.isEmpty) {
                querySnapshot.documents[0].toObject(SizeProduct::class.java)
            } else {
                null
            }
        }
    }
    private fun updateSize(sizeProduct: SizeProduct, quantity: Int) {
        val newQuantity = sizeProduct.quantity + quantity
        sizeProductRef.document(sizeProduct.size_id).update("quantity", newQuantity)
            .addOnSuccessListener {
                Log.i("Update Size", "Successfully updated size: ${sizeProduct.size_id}")
            }
            .addOnFailureListener { e ->
                Log.e("Update Size", "Failed to update size: ${sizeProduct.size_id}", e)
            }
    }
    private fun addNewSize(size: String, quantity: Int) {
        val newSizeProduct = SizeProduct_KT(
            size_id = "",
            size = size.toInt(),
            quantity = quantity,
            product_id = product.product_id
        )
        sizeProductRef.add(newSizeProduct)
            .addOnSuccessListener {
                Log.i("Add Size", "Successfully added size: $size")
            }
            .addOnFailureListener { e ->
                Log.e("Add Size", "Failed to add size: $size", e)
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
    private fun setupSpinner() {
        val sizes = (36..43).map { it.toString() }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sizes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSize.adapter = adapter
    }
    private fun loadProductDetails(productId: String) {
        productRef.document(productId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    product = document.toObject(Product::class.java)!!
                    GlideLoader(this@EditProduct_KT).loadProductPicture(
                        product.image,
                        iv_edit_product_image
                    )
                    etProductTitle.setText(product.title)
                    etProductPrice.setText(product.price)
                    etProductDescription.setText(product.description)
                }
            }
            .addOnFailureListener { exception ->
                showErrorSnackBar(exception.message.toString(), true)
            }
    }
    private fun validateProductDetails(): Boolean {
        return when {
            mSelectedImageFileUri == null && product.image.isEmpty() -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(etProductTitle.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(etProductPrice.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(etProductDescription.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }

            else -> {
                true
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
        spinner_type_edit.adapter = adapter
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@EditProduct_KT)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    private fun uploadProductDetails() {
        val product = Product_KT(
            product_id = product.product_id,
            title = etProductTitle.text.toString().trim(),
            price = etProductPrice.text.toString().trim(),
            description = etProductDescription.text.toString().trim(),
            image = mProductImageURL,
        )
        productRef.document(product.product_id)
            .set(product, SetOptions.merge())
            .addOnSuccessListener {
                updateSizes()
            }
            .addOnFailureListener { e ->
                hideProgressDialog()
                showErrorSnackBar(e.message.toString(), true)
            }
    }


    fun imageUploadSuccess(imageURL: String) {
        mProductImageURL = imageURL
        uploadProductDetails()
    }

    private fun uploadProductImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClassKT().uploadImageToCloudStorage(
            this@EditProduct_KT,
            mSelectedImageFileUri,
            Constants.PRODUCT_IMAGE
        )
    }
}
