package com.grd.cookit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AddEditRecipeFragment extends Fragment {

    public AddEditRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // change toolbar title
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.add_recipe_title);

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_edit_recipe, container, false);
        return v;
    }
}
