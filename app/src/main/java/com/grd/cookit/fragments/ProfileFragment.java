package com.grd.cookit.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.grd.cookit.R;
// import com.grd.cookit.activities.A;
import com.grd.cookit.recycler.adapters.PostAdapter;
import com.grd.cookit.model.ui.UIRecipe;
import com.grd.cookit.viewModels.RecipeViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileFragment extends Fragment {

    @BindView(R.id.plus_fav)
    FloatingActionButton plusBtn;

    @BindView(R.id.profile_busy)
    ProgressBar progressBar;

    @BindView(R.id.posts_recycler)
    RecyclerView recyclerView;

    private RecipeViewModel postViewModel;
    private LinearLayoutManager layoutManager;
    private PostAdapter postAdapter;
    private List<UIRecipe> currRecipes;

    private boolean isBinded = false;

    public ProfileFragment() {
        currRecipes = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        plusBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_profileFragment_to_addEditRecipeFragment));

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        postAdapter = new PostAdapter(getContext(),
                currRecipes,
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                new PostAdapter.OnItemClickListener() {
                    @Override
                    public void onDeleteClick(View v, int position) {
                        postViewModel.deletePost(currRecipes.get(position).uid);
                    }

                    @Override
                    public void onInfoClick(View v, int position) {
                        postViewModel.selectRecipe(currRecipes.get(position));
                        Navigation.findNavController(getView()).navigate(R.id.action_profileFragment_to_recipeInfoFragment);
                    }
                });


        turnOffProgressBar();

        recyclerView.setAdapter(postAdapter);


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        postViewModel = RecipeViewModel.instance;
        bindAdapterToLivedata();
    }

    private void bindAdapterToLivedata() {
        if (!isBinded) {
            isBinded = true;

            postViewModel.profileBusy.observe(this, (isBusy) -> {
                if (isBusy) {
                    turnOnProgressBar();
                } else {
                    turnOffProgressBar();
                }
            });

            postViewModel.getAllProfilePosts(
                    FirebaseAuth.getInstance().getCurrentUser().getUid()).observe(this, this::updatePosts);
        }
    }

    private void turnOnProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        plusBtn.hide();
        recyclerView.setVisibility(View.GONE);
    }

    private void turnOffProgressBar() {
        progressBar.setVisibility(View.GONE);
        plusBtn.show();
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void updatePosts(List<UIRecipe> posts) {
        postAdapter.Posts = posts;
        currRecipes = posts;
        postAdapter.notifyDataSetChanged();
        postViewModel.profileBusy.setValue(false);
        turnOffProgressBar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        postViewModel.profileBusy.removeObservers(this);
        postViewModel.recipesForProfile.removeObservers(this);
        isBinded = false;
    }
}