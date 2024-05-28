package com.example.test.firestoreclass;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.example.test.models.Address;
import com.example.test.models.Cart;
import com.example.test.models.Order;
import com.example.test.models.Product;
import com.example.test.models.SizeProduct;
import com.example.test.models.SoldProduct;
import com.example.test.models.User;
import com.example.test.ui.activities.AddEditAddressActivity;
import com.example.test.ui.activities.AddProductActivity;
import com.example.test.ui.activities.AddressListActivity;
import com.example.test.ui.activities.CartListActivity;
import com.example.test.ui.activities.CheckoutActivity;
import com.example.test.ui.activities.LoginActivity;
import com.example.test.ui.activities.ProductDetailsActivity;
import com.example.test.ui.activities.RegisterActivity;
import com.example.test.ui.activities.SettingsActivity;
import com.example.test.ui.activities.UserProfileActivity;
import com.example.test.ui.fragment.DashboardFragment;
import com.example.test.ui.fragment.OrdersFragment;
import com.example.test.ui.fragment.ProductsFragment;
import com.example.test.ui.fragment.SoldProductsFragment;
import com.example.test.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirestoreClass {
    private static final FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    private final CollectionReference sizesCollection = mFireStore.collection("size");


    public static String getCurrentUserID() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserID = "";
        if (auth.getCurrentUser() != null) {
            currentUserID = auth.getCurrentUser().getUid();
        }
        return currentUserID;
    }

    public static void getUserDetails(Activity activity) {
        mFireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener(document -> {
                    Log.i(activity.getClass().getSimpleName(), Objects.requireNonNull(document.getData()).toString());
                    User user = document.toObject(User.class);
                    if (user != null) {
                        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                                Constants.MYSHOPPAL_PREFERENCES,
                                Context.MODE_PRIVATE
                        );
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(
                                Constants.LOGGED_IN_USERNAME,
                                user.getFirstName() + " " + user.getLastName()
                        );
                        editor.apply();
                        if (activity instanceof LoginActivity) {
                            ((LoginActivity) activity).userLoggedInSuccess(user);
                        } else if (activity instanceof SettingsActivity) {
                            ((SettingsActivity) activity).userDetailsSuccess(user);
                        } else if(activity instanceof CheckoutActivity) {
                            ((CheckoutActivity) activity).userDetailsSuccess(user);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (activity instanceof LoginActivity) {
                        ((LoginActivity) activity).hideProgressDialog();
                    } else if (activity instanceof SettingsActivity) {
                        ((SettingsActivity) activity).hideProgressDialog();
                    } else if(activity instanceof CheckoutActivity) {
                        ((CheckoutActivity) activity).hideProgressDialog();
                    }
                    Log.e(
                            activity.getClass().getSimpleName(),
                            "Error while getting user details.",
                            e
                    );
                });
    }


    public static void getAllProductsList(Activity activity) {
        mFireStore.collection(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener(document -> {
                    Log.e("Products List", document.getDocuments().toString());
                    ArrayList<Product> productsList = new ArrayList<>();
                    for (DocumentSnapshot i : document.getDocuments()) {
                        Product product = i.toObject(Product.class);
                        product.setProduct_id(i.getId());
                        productsList.add(product);
                    }

                    if (activity instanceof CartListActivity) {
                        ((CartListActivity) activity).successProductsListFromFireStore(productsList);
                    } else if (activity instanceof CheckoutActivity) {
                        ((CheckoutActivity) activity).successProductsListFromFireStore(productsList);
                    }
                })
                .addOnFailureListener(e -> {
                    if (activity instanceof CartListActivity) {
                        ((CartListActivity) activity).hideProgressDialog();
                    } else if (activity instanceof CheckoutActivity) {
                        ((CheckoutActivity) activity).hideProgressDialog();
                    }
                    Log.e("Get Product List", "Error while getting all product list.", e);
                });
    }

    public void getDashboardItemsList(DashboardFragment fragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener(document -> {
            Log.e(fragment.getClass().getSimpleName(), document.getDocuments().toString());
            ArrayList<Product> productsList = new ArrayList<>();
            for (DocumentSnapshot i : document.getDocuments()) {
                Product product = i.toObject(Product.class);
                assert product != null;
                product.setProduct_id(i.getId());
                productsList.add(product);
            }
            fragment.successDashboardItemsList(productsList);
        })
        .addOnFailureListener(e -> {
            fragment.hideProgressDialog();
            Log.e(fragment.getClass().getSimpleName(), "Error while getting dashboard items list.", e);
        });
    }





    public static void updateCheckboxStateInFirebase(String cartItemId, boolean isChecked) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(Constants.CART_ITEMS).document(cartItemId);

        docRef
                .update("checked", isChecked)
                .addOnSuccessListener(aVoid -> Log.d("FirestoreClass", "Checkbox state updated successfully"))
                .addOnFailureListener(e -> Log.e("FirestoreClass", "Error updating checkbox state", e));
    }

    public static void getCartList(Activity activity) {
        mFireStore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get()
                .addOnSuccessListener(document -> {
                    Log.e(activity.getClass().getSimpleName(), document.getDocuments().toString());
                    ArrayList<Cart> cartList = new ArrayList<>();
                    ArrayList<SizeProduct> sizeProductList = new ArrayList<>();
                    for (DocumentSnapshot i : document.getDocuments()) {
                        Cart cartItem = i.toObject(Cart.class);
                        cartItem.setId(i.getId());
                        cartList.add(cartItem);
                    }
                    mFireStore.collection(Constants.SIZE_PRODUCTS)
                            .get()
                            .addOnSuccessListener(sizeDocument -> {
                                for (DocumentSnapshot sizeSnapshot : sizeDocument.getDocuments()) {
                                    SizeProduct sizeProduct = sizeSnapshot.toObject(SizeProduct.class);
                                    sizeProductList.add(sizeProduct);
                                }
                                if (activity instanceof CartListActivity) {
                                    ((CartListActivity) activity).successCartItemsList(cartList, sizeProductList);
                                } else if (activity instanceof CheckoutActivity) {
                                    ((CheckoutActivity) activity).successCartItemsList(cartList, sizeProductList);
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (activity instanceof CartListActivity) {
                                    ((CartListActivity) activity).hideProgressDialog();
                                } else if (activity instanceof CheckoutActivity) {
                                    ((CheckoutActivity) activity).hideProgressDialog();
                                }
                                Log.e(activity.getClass().getSimpleName(), "Error while getting the size products.", e);
                            });
                })
                .addOnFailureListener(e -> {
                    if (activity instanceof CartListActivity) {
                        ((CartListActivity) activity).hideProgressDialog();
                    } else if (activity instanceof CheckoutActivity) {
                        ((CheckoutActivity) activity).hideProgressDialog();
                    }
                    Log.e(activity.getClass().getSimpleName(), "Error while getting the cart list items.", e);
                });
    }

    public static void getSelectedCartList(Activity activity) {
        FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
        mFireStore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .whereEqualTo("checked", true)
                .get()
                .addOnSuccessListener(document -> {
                    Log.e(activity.getClass().getSimpleName(), document.getDocuments().toString());
                    ArrayList<Cart> cartList = new ArrayList<>();
                    for (DocumentSnapshot i : document.getDocuments()) {
                        Cart cartItem = i.toObject(Cart.class);
                        cartItem.setId(i.getId());
                        cartList.add(cartItem);
                    }

                    mFireStore.collection(Constants.SIZE_PRODUCTS)
                            .get()
                            .addOnSuccessListener(sizeDocument -> {
                                ArrayList<SizeProduct> sizeProductList = new ArrayList<>();
                                for (DocumentSnapshot sizeSnapshot : sizeDocument.getDocuments()) {
                                    SizeProduct sizeProduct = sizeSnapshot.toObject(SizeProduct.class);
                                    sizeProductList.add(sizeProduct);
                                }

                                if (activity instanceof CheckoutActivity) {
                                    ((CheckoutActivity) activity).successCartItemsList(cartList, sizeProductList);
                                    ((CheckoutActivity) activity).buildHtmlContent(cartList, sizeProductList);                                }
                            })
                            .addOnFailureListener(e -> {
                                if (activity instanceof CheckoutActivity) {
                                    ((CheckoutActivity) activity).hideProgressDialog();
                                }
                                Log.e(activity.getClass().getSimpleName(), "Error while getting the size products.", e);
                            });
                })
                .addOnFailureListener(e -> {
                    if (activity instanceof CheckoutActivity) {
                        ((CheckoutActivity) activity).hideProgressDialog();
                    }
                    Log.e(activity.getClass().getSimpleName(), "Error while getting the cart list items.", e);
                });
    }




    public static void removeItemFromCart(Context context, String cart_id) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener(aVoid -> {
            if (context instanceof CartListActivity) {
                ((CartListActivity) context).itemRemovedSuccess();
            }
        })
        .addOnFailureListener(e -> {
            if (context instanceof CartListActivity) {
                ((CartListActivity) context).hideProgressDialog();
            }
            Log.e(context.getClass().getSimpleName(), "Error while removing the item from the cart list.", e);
        });
    }

    public static void updateMyCart(Context context, String cart_id, HashMap<String, Object> itemHashMap) {

        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener(aVoid -> {
            if (context instanceof CartListActivity) {
                ((CartListActivity) context).itemUpdateSuccess();
            }
        })
        .addOnFailureListener(e -> {
            if (context instanceof CartListActivity) {
                ((CartListActivity) context).hideProgressDialog();
            }
            Log.e(context.getClass().getSimpleName(), "Error while updating the cart item.", e);
        });
    }
    public static void addAddress(AddEditAddressActivity activity, Address addressInfo) {

        mFireStore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener(aVoid -> activity.addUpdateAddressSuccess())
        .addOnFailureListener(e -> {
            activity.hideProgressDialog();
            Log.e(activity.getClass().getSimpleName(), "Error while adding the address.", e);
        });
    }

    public void getAddressesList(AddressListActivity activity) {

        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener(documentSnapshots -> {
            Log.e(activity.getClass().getSimpleName(), documentSnapshots.getDocuments().toString());
            ArrayList<Address> addressList = new ArrayList<>();

            for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
            Address address = document.toObject(Address.class);
            if (address != null) {
                address.setId(document.getId());
                addressList.add(address);
            }
        }

            activity.successAddressListFromFirestore(addressList);
        })
        .addOnFailureListener(e -> {
            activity.hideProgressDialog();
            Log.e(activity.getClass().getSimpleName(), "Error while getting the address list.", e);
        });
    }
    public static void updateAddress(AddEditAddressActivity activity, Address addressInfo, String addressId) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener(aVoid -> activity.addUpdateAddressSuccess())
        .addOnFailureListener(e -> {
            activity.hideProgressDialog();
            Log.e(activity.getClass().getSimpleName(), "Error while updating the Address.", e);
        });
    }


    public static void placeOrder(final CheckoutActivity activity, Order order) {
        mFireStore.collection(Constants.ORDERS)
                .document()
                .set(order, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        activity.orderPlacedSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activity.hideProgressDialog();
                        Log.e(activity.getClass().getSimpleName(), "Error while placing an order.", e);
                    }
                });
    }


    public void updateAllDetails(final CheckoutActivity activity, ArrayList<Cart> cartList, Order order) {
        FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
        WriteBatch writeBatch = mFireStore.batch();


        for (Cart cart : cartList) {
            DocumentReference documentReference = mFireStore.collection(Constants.SOLD_PRODUCTS)
                    .document();
            String newSoldProductId = documentReference.getId();
            SoldProduct soldProduct = new SoldProduct(
                    FirestoreClass.getCurrentUserID(),
                    cart.getTitle(),
                    cart.getPrice(),
                    cart.getCart_quantity(),
                    cart.getImage(),
                    order.getSize(),
                    order.getId(),
                    order.getOrder_datetime(),
                    order.getSub_total_amount(),
                    order.getShipping_charge(),
                    order.getTotal_amount(),
                    order.getAddress(),
                    newSoldProductId
            );


            writeBatch.set(documentReference, soldProduct);
        }

        for (Cart cart : cartList) {
            Map<String, Object> productHashMap = new HashMap<>();
            productHashMap.put(Constants.STOCK_QUANTITY,
                    String.valueOf(Integer.parseInt(cart.getStock_quantity()) - Integer.parseInt(cart.getCart_quantity())));

            DocumentReference documentReference = mFireStore.collection(Constants.PRODUCTS)
                    .document(cart.getProduct_id());
            writeBatch.update(documentReference, productHashMap);
        }

        for (Cart cart : cartList) {
            DocumentReference documentReference = mFireStore.collection(Constants.CART_ITEMS)
                    .document(cart.getId());
            writeBatch.delete(documentReference);
        }

        writeBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                activity.allDetailsUpdatedSuccessfully();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                activity.hideProgressDialog();
                Log.e(activity.getClass().getSimpleName(),
                        "Error while updating all the details after order placed.", e);
            }
        });
    }



    public static void updateSizeProductQuantity(String productId, int size, int quantityToSubtract) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.SIZE_PRODUCTS)
                .whereEqualTo("product_id", productId)
                .whereEqualTo("size", size)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot document : documents) {
                        SizeProduct sizeProduct = document.toObject(SizeProduct.class);
                        if (sizeProduct != null) {
                            long currentQuantity = sizeProduct.getQuantity();
                            long newQuantity = Math.max(0, currentQuantity - quantityToSubtract);

                            db.collection(Constants.SIZE_PRODUCTS)
                                    .document(document.getId())
                                    .update("quantity", newQuantity)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Quantity updated successfully.");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error updating quantity.", e);
                                    });
                        }
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting documents: ", e);
                });
    }

    public static void getType(Callback<List<Pair<String, String>>> callback) {
        mFireStore.collection("shoe_type")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Pair<String, String>> types = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String typeName = document.getString("type_name");
                        String typeId = document.getId();
                        if (typeName != null) {
                            types.add(new Pair<>(typeName, typeId));
                        }
                    }
                    callback.onResult(types);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreClass", "Error while getting types.", e);
                    callback.onResult(new ArrayList<>());
                });
    }

    public static void getTypeNameById(String typeId, Callback<String> callback) {
        mFireStore.collection("shoe_type")
                .document(typeId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String typeName = documentSnapshot.getString("type_name");
                        callback.onResult(typeName);
                    } else {
                        callback.onResult(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreClass", "Error while getting type name.", e);
                    callback.onResult(null);
                });
    }

    public interface Callback<T> {
        void onResult(T result);
    }


}
