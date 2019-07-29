package com.grd.cookit;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.grd.cookit.repositories.RecipeRepository;

public class AddEditRecipeFragment extends Fragment {

    ImageView imageView;
    ProgressBar progressBar;
    EditText editText;
    Bitmap imageBitmap;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    final static int RESULT_SUCESS = -1;

    private static String[] PERMISSIONS = {
            Manifest.permission.CAMERA
    };

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
        progressBar = v.findViewById(R.id.progressBar);
        imageView = v.findViewById(R.id.recipeImage);
        editText = v.findViewById(R.id.recipeNameEdit);
        progressBar.setVisibility(View.INVISIBLE);

        Button btnTakePicture = v.findViewById(R.id.takePictureBtn);
        Button btnPublish = v.findViewById(R.id.publishBtn);

        btnPublish.setOnClickListener((vi) -> {
            publishRecipe();
        });
        btnTakePicture.setOnClickListener((vi) -> {
            takePicture();
        });

        return v;
    }

    public void takePicture() {
        if(verifyCameraPermission(getActivity())){
        Intent takePictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }else{
            Toast.makeText(getActivity(),R.string.take_picture_permission,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE &&
                resultCode == RESULT_SUCESS) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }else{
            Toast.makeText(getActivity(),R.string.take_picture_error,Toast.LENGTH_SHORT).show();
        }
    }

    public void publishRecipe() {
        progressBar.setVisibility(View.VISIBLE);
        RecipeRepository.saveRecipe(editText.getText().toString(),imageBitmap,(e)->{
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(),"succeed",Toast.LENGTH_SHORT).show();
        });
    }

    private boolean verifyCameraPermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, 2);
            return false;
        }
        return true;
    }
}
