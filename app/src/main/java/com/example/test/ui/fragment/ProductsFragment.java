package com.example.test.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.test.R;
import com.example.test.databinding.FragmentProductsBinding;
import com.example.test.firestoreclass.FirestoreClassKT;
import com.example.test.models.Product;
import com.example.test.ui.activities.AddProductActivity;
import com.example.test.ui.adapters.MyProductsListAdapter;

import java.util.ArrayList;

public class ProductsFragment extends BaseFragment {

    private FragmentProductsBinding binding;
    private RecyclerView rv_my_product_items;
    private TextView tv_no_products_found;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        rv_my_product_items = root.findViewById(R.id.rv_my_product_items);
        tv_no_products_found = root.findViewById(R.id.tv_no_products_found);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getProductListFromFireStore();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_product_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void getProductListFromFireStore() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        new FirestoreClassKT().getProductsList(this);
    }

    public void successProductsListFromFireStore(ArrayList<Product> productsList) {
        hideProgressDialog();
        if (productsList.size() > 0) {
            rv_my_product_items.setVisibility(View.VISIBLE);
            tv_no_products_found.setVisibility(View.GONE);
            rv_my_product_items.setLayoutManager(new LinearLayoutManager(getActivity()));
            rv_my_product_items.setHasFixedSize(true);

            MyProductsListAdapter adapterProducts = new MyProductsListAdapter(requireActivity(), productsList, this);
            rv_my_product_items.setAdapter(adapterProducts);
        } else {
            rv_my_product_items.setVisibility(View.GONE);
            tv_no_products_found.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_products) {
            startActivity(new Intent(getActivity(), AddProductActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteProduct(String productID) {
        showAlertDialogToDeleteProduct(productID);
    }

    public void productDeleteSuccess() {
        hideProgressDialog();
        Toast.makeText(requireActivity(), getResources().getString(R.string.product_delete_success_message), Toast.LENGTH_SHORT).show();
        getProductListFromFireStore();
    }

    private void showAlertDialogToDeleteProduct(String productID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(getResources().getString(R.string.delete_dialog_title));
        builder.setMessage(getResources().getString(R.string.delete_dialog_message));
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, i) -> {
            showProgressDialog(getResources().getString(R.string.please_wait));
            new FirestoreClassKT().deleteProduct(this, productID);
            dialogInterface.dismiss();
        });
        builder.setNegativeButton(getResources().getString(R.string.no), (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}