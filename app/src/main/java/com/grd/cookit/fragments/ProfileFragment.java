package com.grd.cookit.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.grd.cookit.R;
import com.grd.cookit.model.ui.UIRecipe;
import com.grd.cookit.recycler.adapters.PostAdapter;
import com.grd.cookit.viewModels.RecipeViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    @BindView(R.id.plus_fav)
    FloatingActionButton plusBtn;

    @BindView(R.id.profile_busy)
    ProgressBar progressBar;

    @BindView(R.id.posts_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.empty_view)
    TextView emptyView;

    private RecipeViewModel recipeViewModel;
    private LinearLayoutManager layoutManager;
    private PostAdapter postAdapter;
    private List<UIRecipe> currRecipes;

    private boolean isBinded = false;

    public ProfileFragment() {
        currRecipes = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        recipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
        bindAdapterToLivedata();
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
                        recipeViewModel.deletePost(currRecipes.get(position).uid);
                    }

                    @Override
                    public void onInfoClick(View v, int position) {
                        recipeViewModel.selectRecipe(currRecipes.get(position));
                        Navigation.findNavController(getView()).navigate(R.id.action_profileFragment_to_recipeInfoFragment);
                    }
                });


        turnOffProgressBar();

        recyclerView.setAdapter(postAdapter);


        return view;
    }

    private void bindAdapterToLivedata() {
        if (!isBinded) {
            isBinded = true;

            recipeViewModel.profileBusy.observe(this, (isBusy) -> {
                if (isBusy) {
                    turnOnProgressBar();
                } else {
                    turnOffProgressBar();
                }
            });

            recipeViewModel.getAllProfilePosts(
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
        if (posts.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        postAdapter.Posts = posts;
        currRecipes = posts;
        postAdapter.notifyDataSetChanged();
        recipeViewModel.profileBusy.setValue(false);
        turnOffProgressBar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recipeViewModel.profileBusy.removeObservers(this);
        recipeViewModel.recipesForProfile.removeObservers(this);
        isBinded = false;
    }

    private AlertDialog AskOption(int position)
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(getActivity())
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.ic_warning)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        recipeViewModel.deletePost(currRecipes.get(position).uid);
                        dialog.dismiss();
                    }

                })



                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }
}