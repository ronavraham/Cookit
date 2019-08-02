package com.grd.cookit.activities;

import androidx.core.view.LayoutInflaterCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.grd.cookit.R;
import com.grd.cookit.fragments.ProfileFragment;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.profile_actionbar_title);
        setSupportActionBar(toolbar);

        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        ProfileFragment profileFragment = new ProfileFragment();

        fragmentTransaction.add(R.id.profile_content, profileFragment);
        fragmentTransaction.commit();
    }
}