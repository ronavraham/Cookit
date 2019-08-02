package com.grd.cookit.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.grd.cookit.R;
import com.grd.cookit.recycler.adapters.PostAdapter;
import com.grd.cookit.viewModels.RecipeViewModel;

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

    public FeedFragment() {
        // Required empty public constructor
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
        postAdapter = new PostAdapter(null);

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

