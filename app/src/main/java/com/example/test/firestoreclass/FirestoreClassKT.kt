package com.example.test.firestoreclass

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.test.R
import com.example.test.models.Address
import com.example.test.models.Cart
import com.example.test.models.Order
import com.example.test.models.Product
import com.example.test.models.SizeProduct
import com.example.test.models.SoldProduct
import com.example.test.ui.activities.LoginActivity
import com.example.test.ui.activities.RegisterActivity
import com.example.test.ui.activities.UserProfileActivity
import com.example.test.models.User
import com.example.test.ui.activities.AddEditAddressActivity
import com.example.test.ui.activities.AddProductActivity
import com.example.test.ui.activities.AddressListActivity
import com.example.test.ui.activities.CartListActivity
import com.example.test.ui.activities.CheckoutActivity
import com.example.test.ui.activities.EditProductActivity
import com.example.test.ui.activities.ProductDetailsActivity
import com.example.test.ui.activities.SettingsActivity
import com.example.test.ui.fragment.DashboardFragment
import com.example.test.ui.fragment.OrdersFragment
import com.example.test.ui.fragment.ProductsFragment
import com.example.test.ui.fragment.SoldProductsFragment
import com.example.test.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClassKT {
    private val mFireStore = FirebaseFirestore.getInstance()
    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }
    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)!!
                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.MYSHOPPAL_PREFERENCES,
                        Context.MODE_PRIVATE
                    )
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()
                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>,id:String) {
        mFireStore.collection(Constants.USERS)
            .document(id)
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }

                            is AddProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is EditProductActivity->{
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }

                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                    is EditProductActivity->{
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun getProductsList(fragment: Fragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products List", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFireStore(productsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.hideProgressDialog()
                    }
                }

                Log.e("Get Product List", "Error while getting product list.", e)
            }
    }
    fun getAllProductsList(activity: Activity) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products List", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }
                    is CheckoutActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e("Get Product List", "Error while getting all product list.", e)
            }
    }
    fun getDashboardItemsList(fragment: DashboardFragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    productsList.add(product)
                }
                fragment.successDashboardItemsList(productsList)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting dashboard items list.", e)
            }
    }
    fun deleteProduct(fragment: ProductsFragment, productId: String) {

        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product.",
                    e
                )
            }
    }
    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) { // Kiểm tra document tồn tại
                    val product = document.toObject(Product::class.java)
                    product?.let {
                        mFireStore.collection(Constants.SIZE_PRODUCTS)
                            .whereEqualTo("product_id", productId)
                            .get()
                            .addOnSuccessListener { sizeQuerySnapshot ->
                                val sizeQuantityMap = mutableMapOf<String, Long>()
                                for (sizeDocument in sizeQuerySnapshot.documents) {
                                    val sizeProduct = sizeDocument.toObject(SizeProduct::class.java)
                                    sizeProduct?.let {
                                        sizeQuantityMap[sizeProduct.size.toString()] = sizeProduct.quantity.toLong()
                                    }
                                }
                                activity.productDetailsSuccess(product!!, sizeQuantityMap)
                            }
                            .addOnFailureListener { e ->
                                activity.hideProgressDialog()
                                Log.e(activity.javaClass.simpleName, "Error while getting size product details.", e)
                            }
                    }
                } else {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar(activity.resources.getString(R.string.err_msg_product_not_found), true)
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting product details.", e)
            }
    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: Cart) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document().set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item.",
                    e
                )
            }
    }
//    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String) {
//        mFireStore.collection(Constants.CART_ITEMS)
//            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
//            .whereEqualTo(Constants.PRODUCT_ID, productId)
//            .get()
//            .addOnSuccessListener { document ->
//                Log.e(activity.javaClass.simpleName, document.documents.toString())
//                if (document.documents.size > 0) {
//                    activity.productExistsInCart()
//                } else {
//                    activity.hideProgressDialog()
//                }
//            }
//            .addOnFailureListener { e ->
//                activity.hideProgressDialog()
//
//                Log.e(
//                    activity.javaClass.simpleName,
//                    "Error while checking the existing cart list.",
//                    e
//                )
//            }
//    }
    fun getCartList(activity: Activity) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<Cart> = ArrayList()
                for (i in document.documents) {
                    val cartItem = i.toObject(Cart::class.java)!!
                    cartItem.id = i.id
                    list.add(cartItem)
                }
                mFireStore.collection(Constants.SIZE_PRODUCTS)
                    .get()
                    .addOnSuccessListener { sizeDocument ->
                        val sizeProductList: ArrayList<SizeProduct> = ArrayList()
                        for (sizeItem in sizeDocument.documents) {
                            val sizeProduct = sizeItem.toObject(SizeProduct::class.java)!!
                            sizeProductList.add(sizeProduct)
                        }
                        when (activity) {
                            is CartListActivity -> {
                                activity.successCartItemsList(list, sizeProductList)
                            }
                            is CheckoutActivity -> {
                                activity.successCartItemsList(list, sizeProductList)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        when (activity) {
                            is CartListActivity -> {
                                activity.hideProgressDialog()
                            }
                            is CheckoutActivity -> {
                                activity.hideProgressDialog()
                            }
                        }
                        Log.e(activity.javaClass.simpleName, "Error while getting the size product list.", e)
                    }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.", e)
            }
    }

    fun removeItemFromCart(context: Context, cart_id: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    e
                )
            }
    }
    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
                    e
                )
            }
    }
    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address.",
                    e
                )
            }
    }
    fun getAddressesList(activity: AddressListActivity) {
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val addressList: ArrayList<Address> = ArrayList()
                for (i in document.documents) {

                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id

                    addressList.add(address)
                }

                activity.successAddressListFromFirestore(addressList)
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the address list.", e)
            }
    }
    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )
            }
    }
    fun deleteAddress(activity: AddressListActivity, addressId: String) {
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address.",
                    e
                )
            }
    }
    fun placeOrder(activity: CheckoutActivity, order: Order) {

        mFireStore.collection(Constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlacedSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while placing an order.",
                    e
                )
            }
    }
    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<Cart>, order: Order) {
        val writeBatch = mFireStore.batch()
        for (cart in cartList) {
            val soldProduct = SoldProduct(
                FirestoreClassKT().getCurrentUserID(),
                cart.title,
                cart.price,
                cart.cart_quantity,
                cart.image,
                order.size,
                order.title,
                order.order_datetime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address
            )
            val documentReference = mFireStore.collection(Constants.SOLD_PRODUCTS)
                .document()
            writeBatch.set(documentReference, soldProduct)
        }
        for (cart in cartList) {

            val productHashMap = HashMap<String, Any>()

            productHashMap[Constants.STOCK_QUANTITY] =
                (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()

            val documentReference = mFireStore.collection(Constants.PRODUCTS)
                .document(cart.product_id)

            writeBatch.update(documentReference, productHashMap)
        }

        // Delete the list of cart items
        for (cart in cartList) {

            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                .document(cart.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {

            activity.allDetailsUpdatedSuccessfully()

        }.addOnFailureListener { e ->
            // Here call a function of base activity for transferring the result to it.
            activity.hideProgressDialog()

            Log.e(
                activity.javaClass.simpleName,
                "Error while updating all the details after order placed.",
                e
            )
        }
    }
    fun getMyOrdersList(fragment: OrdersFragment) {
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<Order> = ArrayList()

                for (i in document.documents) {

                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id

                    list.add(orderItem)
                }

                fragment.populateOrdersListInUI(list)
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.

                fragment.hideProgressDialog()

                Log.e(fragment.javaClass.simpleName, "Error while getting the orders list.", e)
            }
    }

    fun getSoldProductsList(fragment: SoldProductsFragment) {
        mFireStore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<SoldProduct> = ArrayList()
                for (i in document.documents) {
                    val soldProduct = i.toObject(SoldProduct::class.java)!!
                    soldProduct.id = i.id
                    list.add(soldProduct)
                }
                fragment.successSoldProductsList(list)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting the list of sold products.",
                    e
                )
            }
    }

    fun getType(callback: (List<Pair<String, String>>) -> Unit) {
        mFireStore.collection("shoe_type")
            .get()
            .addOnSuccessListener { documents ->
                val types = documents.mapNotNull {
                    val typeName = it.getString("type_name")
                    val typeId = it.id
                    if (typeName != null) Pair(typeName, typeId) else null
                }
                callback(types)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreClass", "Error while getting types.", e)
                callback(emptyList())
            }
    }


    fun getTypeNameById(typeId: String, callback: (String?) -> Unit) {
        mFireStore.collection("shoe_type")
            .document(typeId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val typeName = document.getString("type_name")
                    callback(typeName)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreClass", "Error while getting type name.", e)
                callback(null)
            }
    }

    fun getUsersInfo(callback: (List<User>) -> Unit) {
        mFireStore.collection(Constants.USERS)
            .get()
            .addOnSuccessListener { documents ->
                val userList: ArrayList<User> = ArrayList()
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    userList.add(user)
                }
                Log.d("FirestoreClass", "Users loaded successfully: ${userList.size} users")
                callback(userList)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreClass", "Error loading users", e)
            }
    }

    fun deleteUserByEmail(userEmailToDelete: String, userPassword: String, callback: (Boolean) -> Unit) {
        // Xóa người dùng từ Firestore
        mFireStore.collection(Constants.USERS)
            .whereEqualTo("email", userEmailToDelete)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
                FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmailToDelete, userPassword)
                    .addOnSuccessListener { authResult ->
                        val currentUser = authResult.user
                        currentUser?.delete()
                            ?.addOnCompleteListener { deleteTask ->
                                if (deleteTask.isSuccessful) {
                                    callback(true)
                                } else {
                                    Log.e("Delete User", "Error deleting user from auth.", deleteTask.exception)
                                    callback(false)
                                }
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Delete User", "Error signing in to delete user from auth.", e)
                        callback(false)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Delete User", "Error deleting user from Firestore.", e)
                callback(false)
            }
    }





}
