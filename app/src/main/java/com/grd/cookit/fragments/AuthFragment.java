package com.grd.cookit.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.grd.cookit.R;
import com.grd.cookit.model.entities.User;
import com.grd.cookit.viewModels.UserViewModel;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthFragment extends Fragment {

    private final static int RC_SIGN_IN = 1;
    private final static String TAG = "AUTH_ACTIVITY";

    private GoogleApiClient mGoogleSignInClient;
    private OnCompleteListener<AuthResult> mFirebaseResultListener;

    private View view;
    private UserViewModel userViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
    }

    public AuthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_auth, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.sign_in_btn)
    public void signIn() {
        signIn(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "login was successful", Toast.LENGTH_SHORT);

                User newUser = new User(FirebaseAuth.getInstance().getCurrentUser());

                userViewModel.saveUser(newUser);

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_authFragment_to_feedFragment);
            }
        }, getActivity());
    }

    public void initGoogleSignin(FragmentActivity fragmentActivity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .enableAutoManage(fragmentActivity,
                        new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Toast.makeText(getActivity(),
                                        "Failed to connect to google",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(getActivity(), mFirebaseResultListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getActivity(),
                        "Failed to connect to google",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void signIn(OnCompleteListener<AuthResult> listener, FragmentActivity fragmentActivity) {
        initGoogleSignin(fragmentActivity);
        mFirebaseResultListener = listener;
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}