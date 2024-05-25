package com.example.test.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.databinding.FragmentDashboardBinding
import com.example.test.firestoreclass.FirestoreClass
import com.example.test.models.Product
import com.example.test.ui.activities.CartListActivity
import com.example.test.ui.activities.LoginActivity
import com.example.test.ui.activities.ProductDetailsActivity
import com.example.test.ui.activities.SettingsActivity
import com.example.test.ui.adapters.DashboardItemsListAdapter
import com.example.test.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class DashboardFragment : BaseFragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var rv_dashboard_items: RecyclerView
    private lateinit var tv_no_dashboard_items_found: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private val binding get() = _binding!!
    private var isToggleOn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        rv_dashboard_items = root.findViewById(R.id.rv_dashboard_items)
        tv_no_dashboard_items_found = root.findViewById(R.id.tv_no_dashboard_items_found)
        drawerLayout = root.findViewById(R.id.drawer_layout)
        navView = root.findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_account -> {
                    startActivity(Intent(activity, SettingsActivity::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.action_logout->{
                    AlertDialog.Builder(requireContext())
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                        .setPositiveButton("Đồng ý") { dialog, _ ->
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(requireContext(), LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            requireActivity().finish()
                            dialog.dismiss()
                        }
                        .setNegativeButton("Hủy") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                    true
                }
                R.id.action_toggle_off -> {
                    isToggleOn = true
                    updateToggleIcon()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    activity?.recreate()
                    true
                }
                R.id.action_toggle_on -> {
                    isToggleOn = false
                    updateToggleIcon()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    activity?.recreate()
                    true
                }
                else -> false

            }
        }
        setHasOptionsMenu(true)
        return root
    }
    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            updateNavHeader(it)
        }
    }
    private fun updateToggleIcon() {
        val toggleOffItem = navView.menu.findItem(R.id.action_toggle_off)
        val toggleOnItem = navView.menu.findItem(R.id.action_toggle_on)
        if (isToggleOn) {
            toggleOffItem.isVisible = false
            toggleOnItem.isVisible = true
        } else {
            toggleOffItem.isVisible = true
            toggleOnItem.isVisible = false
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    private fun updateNavHeader(user: FirebaseUser?) {
        val headerView = navView.getHeaderView(0)
        val imageView = headerView.findViewById<ImageView>(R.id.imageView)
        val textViewName = headerView.findViewById<TextView>(R.id.textView)
        val textViewEmail = headerView.findViewById<TextView>(R.id.textViewEmail)

        user?.let {
            val fullName = user.displayName
            val email = user.email
            val img = user.photoUrl
            val fullNameParts = fullName?.split(" ")
            val firstName = fullNameParts?.getOrNull(0) ?: ""
            val lastName = fullNameParts?.getOrNull(1) ?: ""
            textViewName.text = "$firstName $lastName"
            textViewEmail.text = email
            Picasso.get().load(img).into(imageView)
        } ?: run {
            textViewName.text = ""
            textViewEmail.text = ""
            imageView.setImageDrawable(null)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_cart -> {
                startActivity(Intent(activity, CartListActivity::class.java))
                return true
            }
            R.id.action_sidebar -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
                return true
            }
            R.id.action_search->{
                showSearchDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showSearchDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Search Product")

        // Set up the input
        val input = EditText(requireContext())
        input.hint = "Enter product name"
        builder.setView(input)
        builder.setPositiveButton("Search") { dialog, _ ->
            val searchQuery = input.text.toString()
            performSearch(searchQuery)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }
    private fun performSearch(query: String) {

        Toast.makeText(requireContext(), "Searching for: $query", Toast.LENGTH_SHORT).show()
    }
    private fun getDashboardItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getDashboardItemsList(this@DashboardFragment)
    }
    fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>) {
        hideProgressDialog()
        if (dashboardItemsList.size > 0) {
            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_dashboard_items_found.visibility = View.GONE
            rv_dashboard_items.layoutManager = GridLayoutManager(activity, 2)
            rv_dashboard_items.setHasFixedSize(true)
            val adapter = DashboardItemsListAdapter(requireActivity(), dashboardItemsList)
            rv_dashboard_items.adapter = adapter
            adapter.setOnClickListener(object :
                DashboardItemsListAdapter.OnClickListener {
                override fun onClick(position: Int, product: Product) {
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
                    startActivity(intent)
                }
            })


        } else {
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }
    }

}
