package com.grd.cookit.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.grd.cookit.R;
import com.grd.cookit.model.ui.UIRecipe;
import com.grd.cookit.recycler.adapters.PostAdapter;
import com.grd.cookit.viewModels.RecipeViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedFragment extends Fragment {

    @BindView(R.id.feed_busy)
    ProgressBar progressBar;

    @BindView(R.id.posts_recycler)
    RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;
    private RecipeViewModel postViewModel;
    private PostAdapter postAdapter;
    private List<UIRecipe> currRecipes;

    public FeedFragment() {
        currRecipes = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        postViewModel = RecipeViewModel.instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this, view);

        RecyclerView rv = view.findViewById(R.id.posts_recycler);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);
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
                        Navigation.findNavController(getView()).navigate(R.id.action_feedFragment_to_recipeInfoFragment);
                    }
                });

        postViewModel.feedBusy.observe(this, (isBusy) -> {
            if (isBusy) {
                turnOnProgressBar();
            } else {
                turnOffProgressBar();
            }
        });

        bindAdapterToLivedata();
        rv.setAdapter(postAdapter);
        return view;
    }


    private void bindAdapterToLivedata() {
        turnOnProgressBar();
        postViewModel.getAllRecipes().observe(this, (posts) -> {
            postAdapter.Posts = posts;
            currRecipes = posts;
            postAdapter.notifyDataSetChanged();
            postViewModel.feedBusy.setValue(false);
            turnOffProgressBar();
        });
    }

    private void turnOnProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void turnOffProgressBar() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

    }
}

