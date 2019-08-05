package com.grd.cookit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.SignInButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.grd.cookit.activities.AuthActivity;
import com.grd.cookit.activities.MapsActivity;

public class MainActivity extends AppCompatActivity implements
                                    NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = "MAIN_ACTIVITY";

    private NavController navController;
    AppBarConfiguration appBarConfiguration;
    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
        } else {
            Log.d(TAG, "YAYYY");
            setupNavigation();
//            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
//            navController = Navigation.findNavController(this,R.id.nav_host_fragment);
//            appBarConfiguration =
//                    new AppBarConfiguration.Builder(navController.getGraph()).setDrawerLayout(drawerLayout).build();
//            NavigationView navView = findViewById(R.id.nav_view);
//            NavigationUI.setupWithNavController(navView, navController);
//            NavigationUI.setupActionBarWithNavController(this,navController);
//            NavigationUI.setupWithNavController(toolbar,navController);
//            NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration);
        }
    }

    private void setupNavigation() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        navigationView = findViewById(R.id.nav_view);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener((this::onNavigationItemSelected));

    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController,drawerLayout);
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
        }
//            case R.id.navigate_to_logout:
//                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//                firebaseAuth.signOut();
//                Intent intent = getIntent();
//                finish();
//                startActivity(intent);
//                break;



        return true;

    }

}
