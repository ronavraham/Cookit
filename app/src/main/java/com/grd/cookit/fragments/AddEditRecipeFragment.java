package com.grd.cookit.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.grd.cookit.R;
import com.grd.cookit.model.ui.UIRecipe;
import com.grd.cookit.repositories.RecipeRepository;
import com.grd.cookit.viewModels.RecipeViewModel;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class AddEditRecipeFragment extends Fragment {

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int PERMISSION_ALL = 1;
    private static final String TAG = "AddEditRecipeFragment";
    private static final String APP_NAME = "coockit";
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private static String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @BindView(R.id.recipeImage)
    ImageView imageView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.recipeNameEdit)
    EditText editText;
    @BindView(R.id.descriptionEdit)
    EditText descriptionText;
    @BindView(R.id.takePictureBtn)
    Button btnTakePicture;
    @BindView(R.id.publishBtn)
    Button btnPublish;

    private String type;
    private UIRecipe currRecipe;
    private RecipeViewModel recipeViewModel;
    private File tempFile;

    private boolean locationPermissionGranted;

    public AddEditRecipeFragment() {
        recipeViewModel = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        recipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (type.equals("edit")) {
            recipeViewModel.getselectedRecipe().removeObservers(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_edit_recipe, container, false);
        ButterKnife.bind(this, v);
        // change toolbar title
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        progressBar.setVisibility(View.GONE);

        String type = AddEditRecipeFragmentArgs.fromBundle(getArguments()).getType();
        this.type = type;

        if (type.equals("add")) {
            toolbar.setTitle(R.string.add_recipe_title);

            btnPublish.setOnClickListener((vi) -> {
                publishRecipe();
            });

        } else {
            toolbar.setTitle(R.string.edit_recipe_title);
            recipeViewModel.getselectedRecipe().observe(this, (recipe) -> {
                currRecipe = recipe;
                imageView.setBackgroundDrawable(recipe.recipeImage);
                editText.setText(recipe.name);
                descriptionText.setText(recipe.description);
            });

            btnPublish.setOnClickListener(vi -> {
                updateRecipe();
            });
        }

        btnTakePicture.setOnClickListener((vi) -> {
            takePicture();
        });
        return v;
    }

    public void takePicture() {
        Log.d(TAG, "takePicture: check if app has permissions");
        if (hasPermissions()) {
            Log.d(TAG, "takePicture: start camera activity");
            Intent takePictureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                tempFile = createTmpFile();
                if (tempFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            "com.grd.cookit.fileprovider",
                            tempFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        } else {
            Log.d(TAG, "takePicture: ask user for permissions");
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
    }

    private void updateRecipe() {
        String recipeName = editText.getText().toString();
        String recipeDesc = descriptionText.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        recipeViewModel.updateRecipe(currRecipe, tempFile, recipeName, recipeDesc, (e) -> {
                    Toast.makeText(getActivity(), "succeed", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(getView()).popBackStack();
                    Navigation.findNavController(getView()).popBackStack();
                },
                (e) -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), R.string.general_error_message, Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: get result from camera");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE &&
                resultCode == RESULT_OK) {
            // add image to gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(tempFile);
            mediaScanIntent.setData(contentUri);
            getActivity().sendBroadcast(mediaScanIntent);
            imageView.setImageURI(contentUri);
        } else {
            Log.e(TAG, "onActivityResult: could not get camera data");
            tempFile.delete();
            tempFile = null;
            Toast.makeText(getActivity(), R.string.take_picture_error, Toast.LENGTH_SHORT).show();
        }
    }

    public void publishRecipe() {
        progressBar.setVisibility(View.VISIBLE);
        getPhoneLocation((location) -> {
            RecipeRepository.saveRecipe(editText.getText().toString(), descriptionText.getText().toString(), tempFile, location, (e) -> {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), "succeed", Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.popBackStack();
            }, (error) -> {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), R.string.general_error_message, Toast.LENGTH_SHORT).show();
            });
        });
    }

    private boolean hasPermissions() {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private File createTmpFile() {
        String fileName = UUID.randomUUID().toString();
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_NAME);
        if (!isExternalStorageWritable()) {
            Log.e(TAG, "external storage is not writable");
            Toast.makeText(getActivity(), "something went wrong", Toast.LENGTH_LONG).show();
            return null;
        }
        if (!isExternalStorageReadable()) {
            Log.e(TAG, "external storage is not readable");
            Toast.makeText(getActivity(), "something went wrong", Toast.LENGTH_LONG).show();
            return null;
        }
        // make sure dir is exist
        storageDir.mkdirs();
        Log.d(TAG, "createTmpFile: create temp file");
        try {
            File image = File.createTempFile(
                    fileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            return image;
        } catch (IOException error) {
            Log.e(TAG, "createTmpFile: could not create temp file", error);
            Toast.makeText(getActivity(), "something went wrong", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void getPhoneLocation(OnSuccessListener<Location> listener) {
        getLocationPermission();
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationClient.getLastLocation().addOnSuccessListener(listener);
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
}
