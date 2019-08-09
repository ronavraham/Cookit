package com.grd.cookit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.grd.cookit.R;

public class MainFragment extends Fragment {
    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        if (auth.getCurrentUser() == null) {
            navController.navigate(R.id.action_mainFragment_to_authFragment);
        } else {
            navController.navigate(R.id.action_mainFragment_to_feedFragment);
//            Picasso.get().setIndicatorsEnabled(true);
//            setupNavigation();
        }
    }
}
