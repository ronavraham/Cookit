package com.grd.cookit;

import android.os.Bundle;
import android.view.MenuItem;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.material.navigation.NavigationView;
import com.grd.cookit.fragments.MainFragment;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = "MAIN_ACTIVITY";

    private NavController navController;
    AppBarConfiguration appBarConfiguration;
    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private boolean actionBarAlreadySet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.actionBarAlreadySet = false;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        MainFragment mainFragment = new MainFragment();

        fragmentTransaction.add(R.id.auth_content, mainFragment);
        fragmentTransaction.commit();
    }

    public void setupNavigation() {
        if (!actionBarAlreadySet) {
            actionBarAlreadySet = true;
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            drawerLayout = findViewById(R.id.drawer_layout);

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            navigationView = findViewById(R.id.nav_view);

            navController = Navigation.findNavController(this, R.id.nav_host_fragment);

            NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.feedFragment).setDrawerLayout(drawerLayout).build();

            NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);

            navigationView.setNavigationItemSelectedListener((this::onNavigationItemSelected));
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, drawerLayout);
//        return NavigationUI.navigateUp(drawerLayout, Navigation.findNavController(this, R.id.nav_host_fragment));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        menuItem.setChecked(true);

        drawerLayout.closeDrawers();

        int id = menuItem.getItemId();

        switch (id) {
            case R.id.navigate_to_maps:
                navController.navigate(R.id.action_feedFragment_to_mapsFragment);
                break;

            case R.id.navigate_to_add_recipe:
                navController.navigate(R.id.action_feedFragment_to_addEditRecipeFragment);
                break;

            case R.id.navigate_to_user_profile:
                navController.navigate(R.id.action_feedFragment_to_profileFragment);
                break;

            case R.id.navigate_to_logout:
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                break;
        }

        return true;

    }

}
