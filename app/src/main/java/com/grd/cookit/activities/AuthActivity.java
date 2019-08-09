package com.grd.cookit.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.grd.cookit.R;
import com.grd.cookit.fragments.AuthFragment;

public class AuthActivity extends AppCompatActivity {
    private final static int RC_SIGN_IN = 1;
    private final static String TAG = "AUTH_ACTIVITY";

    private GoogleApiClient mGoogleSignInClient;
    private OnCompleteListener<AuthResult> mFirebaseResultListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
//        try {
//            DrawerUtils.getDrawer(this, toolbar);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();

        AuthFragment authFragment = new AuthFragment();

        fragmentTransaction.add(R.id.auth_content, authFragment);
        fragmentTransaction.commit();
    }


}
