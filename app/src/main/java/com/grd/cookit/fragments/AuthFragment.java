package com.grd.cookit.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.grd.cookit.MainActivity;
import com.grd.cookit.R;
import com.grd.cookit.activities.AuthActivity;
import com.grd.cookit.model.entities.User;
import com.grd.cookit.viewModels.UserViewModel;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthFragment extends Fragment {

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
        AuthActivity activity = (AuthActivity) getActivity();
        activity.signIn(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "login was successful", Toast.LENGTH_SHORT);

                User newUser = new User(FirebaseAuth.getInstance().getCurrentUser());

                userViewModel.saveUser(newUser);

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        }, getActivity());
    }
}