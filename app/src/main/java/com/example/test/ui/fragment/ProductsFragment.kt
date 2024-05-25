package com.example.test.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.databinding.FragmentProductsBinding
import com.example.test.firestoreclass.FirestoreClass
import com.example.test.models.Product
import com.example.test.ui.activities.AddProductActivity
import com.example.test.ui.adapters.MyProductsListAdapter


class ProductsFragment : BaseFragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var rv_my_product_items: RecyclerView
    private lateinit var tv_no_products_found: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        rv_my_product_items = root.findViewById(R.id.rv_my_product_items)
        tv_no_products_found = root.findViewById(R.id.tv_no_products_found)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFireStore()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    private fun getProductListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductsList(this@ProductsFragment)
    }
    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        hideProgressDialog()
        if (productsList.size > 0) {
            rv_my_product_items.visibility = View.VISIBLE
            tv_no_products_found.visibility = View.GONE
            rv_my_product_items.layoutManager = LinearLayoutManager(activity)
            rv_my_product_items.setHasFixedSize(true)
            val adapterProducts =
                MyProductsListAdapter(requireActivity(), productsList, this@ProductsFragment)
            rv_my_product_items.adapter = adapterProducts
        } else {
            rv_my_product_items.visibility = View.GONE
            tv_no_products_found.visibility = View.VISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {

            R.id.action_add_products -> {
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun deleteProduct(productID: String) {
        showAlertDialogToDeleteProduct(productID)
    }

    fun productDeleteSuccess() {
        hideProgressDialog()
        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.product_delete_success_message),
            Toast.LENGTH_SHORT
        ).show()
        getProductListFromFireStore()
    }

    private fun showAlertDialogToDeleteProduct(productID: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().deleteProduct(this@ProductsFragment, productID)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


}