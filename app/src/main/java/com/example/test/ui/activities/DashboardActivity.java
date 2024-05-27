package com.example.test.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.view.Menu;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.test.R;
import com.example.test.databinding.ActivityDashboardBinding;
import com.example.test.models.UserRole;
import com.example.test.ui.adapters.DashboardItemsListAdapter;
import com.example.test.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends BaseActivity {

    private ActivityDashboardBinding binding;
    private String userRole;
    private DashboardItemsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.app_gradient_color_background));
        }

        BottomNavigationView navView = binding.navView;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_dashboard);
        if (navHostFragment != null) {
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_products,
                    R.id.navigation_dashboard,
                    R.id.navigation_orders,
                    R.id.navigation_sold_products
            ).build();

            NavigationUI.setupActionBarWithNavController(this, navHostFragment.getNavController(), appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navHostFragment.getNavController());
        }

        userRole = getIntent().getStringExtra(Constants.USER_ROLE);
        if (userRole == null) {
            userRole = getUserRoleFromSharedPreferences();
        }

        if (UserRole.ADMIN.name().equals(userRole)) {
            navView.getMenu().findItem(R.id.navigation_products).setVisible(true);
        } else {
            navView.getMenu().findItem(R.id.navigation_products).setVisible(false);
        }
    }

    private String getUserRoleFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE);
        return sharedPreferences.getString(Constants.USER_ROLE, null);
    }



    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        doubleBackToExit();
    }
}
