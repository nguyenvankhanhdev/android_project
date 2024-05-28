package com.example.test.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

public class Constants {
    public static final String USERS = "users";
    public static final String MYSHOPPAL_PREFERENCES = "MyShopPalPrefs";
    public static final String LOGGED_IN_USERNAME = "logged_in_username";
    public static final String EXTRA_USER_DETAILS = "extra_user_details";
    public static final int READ_STORAGE_PERMISSION_CODE = 10;
    public static final String SIZE_PRODUCTS = "size_product";
    public static final String SOLD_PRODUCTS = "sold_products";
    public static final String APP_PREFERENCES = "appPreferences";
    public static final int PICK_IMAGE_REQUEST_CODE = 2;
    public static final String MALE = "Male";
    public static final String FEMALE = "Female";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String MOBILE = "mobile";
    public static final String GENDER = "gender";
    public static final String PRODUCT_IMAGE = "Product_image";
    public static final String PRODUCTS = "products";
    public static final String IMAGE = "image";
    public static final String USER_ID = "user_id";
    public static final String USER_PROFILE_IMAGE = "User_Profile_Image";
    public static final String COMPLETE_PROFILE = "profileCompleted";
    public static final String EXTRA_PRODUCT_ID = "extra_product_id";
    public static final String EXTRA_PRODUCT_OWNER_ID = "extra_product_owner_id";
    public static final String CART_ITEMS = "cart_items";
    public static final String DEFAULT_CART_QUANTITY = "1";
    public static final String CART_QUANTITY = "cart_quantity";
    public static final String PRODUCT_ID = "product_id";
    public static final String EXTRA_ADDRESS_DETAILS = "AddressDetails";
    public static final String EXTRA_SOLD_PRODUCT_DETAILS = "extra_sold_product_details";
    public static final String HOME = "Home";
    public static final String OFFICE = "Office";
    public static final String OTHER = "Other";
    public static final String ADDRESSES = "address";
    public static final int ADD_ADDRESS_REQUEST_CODE = 121;
    public static final String EXTRA_SELECT_ADDRESS = "extra_select_address";
    public static final String EXTRA_SELECTED_ADDRESS = "extra_selected_address";
    public static final String ORDERS = "orders";
    public static final String STOCK_QUANTITY = "stock_quantity";
    public static final String EXTRA_MY_ORDER_DETAILS = "extra_MY_ORDER_DETAILS";
    public static final String USER_ROLE = "user_role";
    public static final String ADMIN_ROLE = "Admin";
    public static final String USER_ROLE_DEFAULT = "User";
    public static final String TYPE = "shoe_type";


    public static void showImageChooser(Activity activity) {
        Intent galleryIntent = new Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE);
    }

    public static String getFileExtension(Activity activity, Uri uri) {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.getContentResolver().getType(uri));
    }
}
