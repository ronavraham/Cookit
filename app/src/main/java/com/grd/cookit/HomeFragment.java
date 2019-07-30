package com.grd.cookit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        Button addRecipeBtn = v.findViewById(R.id.addRecipeBtn);
        Button gotoMaps = v.findViewById(R.id.gotoMaps);
        addRecipeBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_addEditRecipeFragment));
        gotoMaps.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_mapsFragment));

        return v;
    }
}
