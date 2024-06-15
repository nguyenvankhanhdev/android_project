package com.example.test.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.databinding.FragmentDashboardBinding;
import com.example.test.firestoreclass.FirestoreClass;
import com.example.test.firestoreclass.FirestoreClassKT;
import com.example.test.models.Cart;
import com.example.test.models.Order;
import com.example.test.models.Price;
import com.example.test.models.Product;
import com.example.test.models.ProductQuantity;
import com.example.test.models.Type;
import com.example.test.ui.activities.CartListActivity;
import com.example.test.ui.activities.LoginActivity;
import com.example.test.ui.activities.ProductDetailsActivity;
import com.example.test.ui.activities.SettingsActivity;
import com.example.test.ui.adapters.AdapterPrice;
import com.example.test.ui.adapters.AdapterType;
import com.example.test.ui.adapters.DashboardItemsListAdapter;
import com.example.test.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class DashboardFragment extends BaseFragment {
    private static final FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    private FragmentDashboardBinding _binding;
    private RecyclerView rv_dashboard_items;
    private RecyclerView rv_dashboard_items_hot;
    private RecyclerView rv_dashboard_items_bestseller;
    private TextView tv_no_dashboard_items_found;
    private TextView txt_list;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private boolean isToggleOn = false;
    private DashboardItemsListAdapter adapter;
    private DashboardItemsListAdapter adapterBestSeller;
    private DashboardItemsListAdapter adapterHot;
    private SearchView searchView;
    private Spinner spinner_price;
    private Spinner spinner_type;
    private AdapterType adapterType;
    private AdapterPrice adapterPrice;
    private ArrayList<Price> prices = new ArrayList<>();
    private ArrayList<Type> typeList = new ArrayList<>();
    private FirebaseFirestore db;
    private ArrayList<ProductQuantity> productQuantities = new ArrayList<>();
    private List<String> productNames = new ArrayList<>();
    private ArrayList<Product> productBestSeller = new ArrayList<>();
    public ArrayList<Product> getBestSeller() {
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<Order> orders = new ArrayList<>();
        ArrayList<Product> productsReturn = new ArrayList<>();


        mFireStore.collection("products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot document) {
                        for (DocumentSnapshot i : document.getDocuments()) {
                            Product product = i.toObject(Product.class);
                            if (product != null) {
                                product.setProduct_id(i.getId());
                                products.add(product);
                            }
                        }
                    }
                });
        mFireStore.collection("orders")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot document) {
                        for (DocumentSnapshot i : document.getDocuments()) {
                            Order order = i.toObject(Order.class);
                            if (order != null) {
                                order.setId(i.getId());
                                List<HashMap<String, Object>> items = (List<HashMap<String, Object>>) i.get("items");
                                ArrayList<Cart> carts = new ArrayList<>();

                                for (HashMap<String, Object> item : items) {
                                    Cart cart = new Cart();
                                    cart.setProduct_id((String) item.get("product_id"));
                                    cart.setTitle((String)item.get("title"));
                                    carts.add(cart);
                                }

                                for (Cart cart : carts) {
                                    for (Product product : products) {
                                        for (ProductQuantity productQuantity : productQuantities){
                                            int soLuong = 0;
                                            if(productQuantity.getProductName().equals(product.getTitle())){
                                                soLuong += productQuantity.getTotalQuantity();
                                            }
                                            else{
                                                soLuong = productQuantity.getTotalQuantity();
                                            }
                                            productQuantities.add(new ProductQuantity(product.getTitle(), soLuong));
                                        }
                                    }
                                }
                                orders.add(order);
                            }
                        }
                    }
                });

        for (int i = 0; i < 6; i++){
            for (Product product : products){
                if(product.getTitle() == productQuantities.get(i).getProductName()){
                    productsReturn.add(product);
                }
            }
        }
        return productsReturn;
    }

    private void fetchTypeNamesFromFirestore() {
        typeList.add(new Type("-1", "Chọn"));
        mFireStore.collection(Constants.TYPE).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                Type type = document.toObject(Type.class);
                                if (type != null) {
                                    type.setTypeID(document.getId());
                                    String typeName = document.getString("type_name");
                                    type.setTenType(typeName);
                                    typeList.add(type);
                                }
                            }
                            setupSpinner(typeList);
                        } else {
                            Log.d("E", "No documents found.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("E", "Error getting documents: ", e);
                    }
                });
    }
    private ArrayList<String> getTypeNames(ArrayList<Type> types) {
        ArrayList<String> typeNames = new ArrayList<>();
        for (Type type : types) {
            typeNames.add(type.getTenType());
        }
        return typeNames;
    }

    private void setupSpinner(ArrayList<Type> data) {
        adapterType=new
                AdapterType(this,R.layout.layout_custom_spinner_type,
                data);
        spinner_type.setAdapter(adapterType);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    private void getProductListFromFireStore() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        new FirestoreClassKT().getProductsList(this);
    }
    private void sortTheoDieuKien(int price, String typeID) {
        if(typeID.equals("-1")){
            mFireStore.collection(Constants.PRODUCTS)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot document) {
                            Log.e(DashboardFragment.this.getClass().getSimpleName(), document.getDocuments().toString());
                            ArrayList<Product> productsList = new ArrayList<>();
                            for (DocumentSnapshot i : document.getDocuments()) {
                                Product product = i.toObject(Product.class);
                                if (product != null) {
                                    product.setProduct_id(i.getId());
                                    productsList.add(product);
                                }
                            }

                            if (price == 1) {
                                Collections.sort(productsList, new Comparator<Product>() {
                                    @Override
                                    public int compare(Product p1, Product p2) {
                                        return Double.compare(Double.parseDouble(p1.getPrice()), Double.parseDouble(p2.getPrice()));
                                    }
                                });
                            } else if (price == 2) {
                                Collections.sort(productsList, new Comparator<Product>() {
                                    @Override
                                    public int compare(Product p1, Product p2) {
                                        return Double.compare(Double.parseDouble(p2.getPrice()), Double.parseDouble(p1.getPrice()));
                                    }
                                });
                            }
                            successDashboardItemsList(productsList);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            Log.e(DashboardFragment.this.getClass().getSimpleName(), "Error while getting dashboard items list.", e);
                        }
                    });
        }
        else {
            mFireStore.collection(Constants.PRODUCTS)
                    .whereEqualTo("shoeTypeId", typeID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot document) {
                            Log.e(DashboardFragment.this.getClass().getSimpleName(), document.getDocuments().toString());
                            ArrayList<Product> productsList = new ArrayList<>();
                            for (DocumentSnapshot i : document.getDocuments()) {
                                Product product = i.toObject(Product.class);
                                if (product != null) {
                                    product.setProduct_id(i.getId());
                                    productsList.add(product);
                                }
                            }

                            if (price == 1) {
                                Collections.sort(productsList, new Comparator<Product>() {
                                    @Override
                                    public int compare(Product p1, Product p2) {
                                        return Double.compare(Double.parseDouble(p1.getPrice()), Double.parseDouble(p2.getPrice()));
                                    }
                                });
                            } else if (price == 2) {
                                Collections.sort(productsList, new Comparator<Product>() {
                                    @Override
                                    public int compare(Product p1, Product p2) {
                                        return Double.compare(Double.parseDouble(p2.getPrice()), Double.parseDouble(p1.getPrice()));
                                    }
                                });
                            }
                            successDashboardItemsList(productsList);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            Log.e(DashboardFragment.this.getClass().getSimpleName(), "Error while getting dashboard items list.", e);
                        }
                    });
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();
        rv_dashboard_items = root.findViewById(R.id.rv_dashboard_items);
        rv_dashboard_items_hot = root.findViewById(R.id.rv_dashboard_items_hot);
        rv_dashboard_items_bestseller = root.findViewById(R.id.rv_dashboard_items_bestseller);
        tv_no_dashboard_items_found = root.findViewById(R.id.tv_no_dashboard_items_found);
        drawerLayout = root.findViewById(R.id.drawer_layout);
        navView = root.findViewById(R.id.nav_view);
        spinner_price = root.findViewById(R.id.spinner_price);
        spinner_type = root.findViewById(R.id.spinner_type);


        productBestSeller = getBestSeller();


        typeList.clear();
        db = FirebaseFirestore.getInstance();

        fetchTypeNamesFromFirestore();


        final int[] selectedPricePosition = {0};
        final int[] selectedTypePosition = {0};

        prices = Price.initPrice();
        adapterPrice=new
                AdapterPrice(this,R.layout.layout_custom_spinner_price,
                prices);
        spinner_price.setAdapter(adapterPrice);

        spinner_price.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPricePosition[0] = position;
                String selectedTypeID = typeList.get(selectedTypePosition[0]).getTypeID();
                sortTheoDieuKien(selectedPricePosition[0], selectedTypeID);
                rv_dashboard_items_hot.setVisibility(View.GONE);
                rv_dashboard_items_bestseller.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTypePosition[0] = position;
                String selectedTypeID = typeList.get(selectedTypePosition[0]).getTypeID();
                sortTheoDieuKien(selectedPricePosition[0], selectedTypeID);
                rv_dashboard_items_hot.setVisibility(View.GONE);
                rv_dashboard_items_bestseller.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        navView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();

            if (id == R.id.action_account) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.action_logout) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(requireContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                requireActivity().finish();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            } else if (id == R.id.action_toggle_off) {
                isToggleOn = true;
                updateToggleIcon();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                getActivity().recreate();
                return true;
            } else if (id == R.id.action_toggle_on) {
                isToggleOn = false;
                updateToggleIcon();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                getActivity().recreate();
                return true;
            } else {
                return false;
            }
        });
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDashboardItemsList();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            updateNavHeader(user);
        }
    }

    private void updateToggleIcon() {
        MenuItem toggleOffItem = navView.getMenu().findItem(R.id.action_toggle_off);
        MenuItem toggleOnItem = navView.getMenu().findItem(R.id.action_toggle_on);
        if (isToggleOn) {
            toggleOffItem.setVisible(false);
            toggleOnItem.setVisible(true);
        } else {
            toggleOffItem.setVisible(true);
            toggleOnItem.setVisible(false);
        }
    }

    private void updateNavHeader(FirebaseUser user) {
        View headerView = navView.getHeaderView(0);
        ImageView imageView = headerView.findViewById(R.id.imageView);
        TextView textViewName = headerView.findViewById(R.id.textView);
        TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);

        if (user != null) {
            String fullName = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUri = user.getPhotoUrl();

            if (fullName != null) {
                textViewName.setText(fullName);
            } else {
                textViewName.setText("");
            }

            if (email != null) {
                textViewEmail.setText(email);
            } else {
                textViewEmail.setText("");
            }

            if (photoUri != null) {
                Picasso.get().load(photoUri).into(imageView);
            } else {
                imageView.setImageDrawable(null);
            }
        } else {
            textViewName.setText("");
            textViewEmail.setText("");
            imageView.setImageDrawable(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cart) {
            startActivity(new Intent(getActivity(), CartListActivity.class));
            return true;
        } else if (id == R.id.action_sidebar) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        } else if (id == R.id.action_search) {
            showSearchDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dashboard_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }


    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Search Product");

        // Set up the input
        EditText input = new EditText(requireContext());
        input.setHint("Enter product name");
        builder.setView(input);
        builder.setPositiveButton("Search", (dialog, which) -> {
            String searchQuery = input.getText().toString();
            performSearch(searchQuery);
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void performSearch(String query) {
        Toast.makeText(requireContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
    }

    private void getDashboardItemsList() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        new FirestoreClassKT().getDashboardItemsList(this);
    }

    public ArrayList<ProductQuantity> getTop5Products() {
        ArrayList<ProductQuantity> sortedProducts = new ArrayList<>(productQuantities);
        Collections.sort(sortedProducts, new Comparator<ProductQuantity>() {
            @Override
            public int compare(ProductQuantity o1, ProductQuantity o2) {
                return Integer.compare(o2.getTotalQuantity(), o1.getTotalQuantity());
            }
        });

        ArrayList<ProductQuantity> top5Products = new ArrayList<>();
        for (int i = 0; i < Math.min(5, sortedProducts.size()); i++) {
            top5Products.add(sortedProducts.get(i));
        }

        return top5Products;
    }
    public void successDashboardItemsList(ArrayList<Product> dashboardItemsList) {
        hideProgressDialog();
        if (dashboardItemsList.size() > 0) {
            rv_dashboard_items.setVisibility(View.VISIBLE);
            rv_dashboard_items_bestseller.setVisibility(View.VISIBLE);
            rv_dashboard_items_hot.setVisibility(View.VISIBLE);
            tv_no_dashboard_items_found.setVisibility(View.GONE);
            rv_dashboard_items.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            rv_dashboard_items_bestseller.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            rv_dashboard_items_hot.setLayoutManager(new GridLayoutManager(getActivity(), 2));

            //rv_dashboard_items.setHasFixedSize(true);
            adapter = new DashboardItemsListAdapter(requireActivity(), dashboardItemsList);
            adapterBestSeller = new DashboardItemsListAdapter(requireActivity(), productBestSeller);
            rv_dashboard_items.setAdapter(adapter);
            rv_dashboard_items_bestseller.setAdapter(adapterBestSeller);
            rv_dashboard_items_hot.setAdapter(adapter);
            adapterBestSeller.setOnClickListener((position, productBestSeller) -> {
                Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, productBestSeller.getProduct_id());
                startActivity(intent);
            });
            adapter.setOnClickListener((position, product) -> {
                Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.getProduct_id());
                startActivity(intent);
            });
        } else {
            rv_dashboard_items.setVisibility(View.GONE);
            tv_no_dashboard_items_found.setVisibility(View.VISIBLE);
        }
    }

    private void productBestSeller(FirestoreClass.BestSellerCallback bestSeller) {
    }
}
