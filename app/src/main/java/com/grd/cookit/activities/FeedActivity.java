package com.grd.cookit.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.grd.cookit.R;
import com.grd.cookit.fragments.FeedFragment;

import java.io.IOException;

public class FeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.feed_actionbar_title);
        setSupportActionBar(toolbar);
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        FeedFragment feedFragment= new FeedFragment();

        fragmentTransaction.add(R.id.feed_content, feedFragment);
        fragmentTransaction.commit();
    }
}
