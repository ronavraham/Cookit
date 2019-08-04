package com.grd.cookit.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.grd.cookit.R;
import com.grd.cookit.model.ui.UIRecipe;
import com.grd.cookit.viewModels.RecipeViewModel;
import com.mikepenz.iconics.view.IconicsButton;

import org.ocpsoft.prettytime.PrettyTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecipeInfoFragment extends Fragment {

    RecipeViewModel recipeViewModel;
    UIRecipe recipe;

    @BindView(R.id.post_user_name)
    TextView userName;

    @BindView(R.id.info_profile_image)
    CircleImageView userImage;

    @BindView(R.id.post_time_ago)
    TextView timestamp;

    @BindView(R.id.info_recipe_image)
    ImageView imageRecipe;

    @BindView(R.id.recipe_info_desc)
    TextView recipeDesc;

    @BindView(R.id.recipe_info_name)
    TextView recipeName;

    @BindView(R.id.edit_recipe_button)
    IconicsButton editBtn;

    @BindView(R.id.remove_recipe_button)
    IconicsButton removeBtn;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        recipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recipe_info_fragment, container, false);

        ButterKnife.bind(this, v);

        recipeViewModel.getselectedRecipe().observe(this,(selectedRecipe)->{
            recipe = selectedRecipe;
            userName.setText(selectedRecipe.userName);
            userImage.setBackgroundDrawable(selectedRecipe.userProfileImage);
            timestamp.setText(new PrettyTime().format(selectedRecipe.timestamp));
            imageRecipe.setBackgroundDrawable(selectedRecipe.recipeImage);
            recipeDesc.setText(selectedRecipe.description);
            recipeName.setText(selectedRecipe.name);
            if(!selectedRecipe.userGoogleId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                editBtn.setVisibility(View.GONE);
                removeBtn.setVisibility(View.GONE);
            }else{
                editBtn.setOnClickListener((view)->onEditButton());
                removeBtn.setOnClickListener(view->onDeleteButton());
            }
        });

        return v;
    }

    private void onDeleteButton(){
        recipeViewModel.deletePost(recipe.uid);
        Navigation.findNavController(getView()).popBackStack();
    }

    private void onEditButton(){
        RecipeInfoFragmentDirections.ActionRecipeInfoFragmentToAddEditRecipeFragment action = RecipeInfoFragmentDirections.actionRecipeInfoFragmentToAddEditRecipeFragment();
        action.setType("edit");
        Navigation.findNavController(getView()).navigate(action);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recipeViewModel.selectRecipe(null);
        recipeViewModel.getselectedRecipe().removeObservers(this);
        recipeViewModel = null;
    }
}
