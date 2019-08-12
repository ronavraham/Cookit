package com.grd.cookit.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grd.cookit.PermissionUtils;
import com.grd.cookit.R;
import com.grd.cookit.model.ui.UIRecipe;
import com.grd.cookit.viewModels.RecipeViewModel;
import com.mikepenz.iconics.view.IconicsButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location currentLocation;
    private RecipeViewModel recipeViewModel;

    @BindView(R.id.current_location_btn)
    IconicsButton currentLocationBtn;

    @BindView(R.id.progress_circular)
    ProgressBar progressBar;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        recipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.bind(this, v);
        progressBar.setVisibility(View.VISIBLE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        currentLocationBtn.setOnClickListener(v1 -> getCurrentLocation());
        mapFragment.getMapAsync(this);

        // Inflate the layout for this fragment
        return v;
    }

    private void getCurrentLocation() {
        if (currentLocationPermissionGranted()) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), location -> {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            currentLocation = location;
                            moveMap(location);
                        }
                    });
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        initMap();

        recipeViewModel.getAllRecipes().observe(this, recipes -> {
            mMap.clear();
            progressBar.setVisibility(View.GONE);
            if (currentLocation != null) {
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        .title("Current Location"))
                        .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }

            recipes.forEach(recipe -> {
                LatLng latLng = new LatLng(recipe.latitude, recipe.longitude);

                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        .title(recipe.name))
                        .setTag(recipe);
            });
        });
    }

    private void initMap() {
        mMap.clear();
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(progressBar));
        mMap.setOnInfoWindowClickListener(this);
        if (currentLocationPermissionGranted()) {
            getCurrentLocation();
        }else{
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void moveMap(Location location) {
        /**
         * Creating the latlng object to store lat, long coordinates
         * adding marker to map
         * move the camera with animation
         */
        if (mMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }

    private boolean currentLocationPermissionGranted() {
        return (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (PermissionUtils.verifyPermissions(grantResults)) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(getActivity(), R.string.ask_current_location_map_permission, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        UIRecipe recipe = (UIRecipe) marker.getTag();

        if (recipe != null) {
            recipeViewModel.selectRecipe(recipe);
            Navigation.findNavController(getView()).navigate(R.id.action_mapsFragment_to_recipeInfoFragment);
        }
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mContents;
        private ProgressBar progressBar;

        CustomInfoWindowAdapter(ProgressBar progressBar) {
            this.progressBar = progressBar;
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            if (marker.getTag() != null) {
                render(marker, mContents);
                return mContents;
            }

            return null;
        }

        private void render(Marker marker, View view) {
            UIRecipe recipe = (UIRecipe) marker.getTag();

            TextView titleUi = view.findViewById(R.id.title);
            ImageView img = view.findViewById(R.id.badge);

            progressBar.setVisibility(View.VISIBLE);
            titleUi.setText(recipe.name);

            recipe.recipeImageRequestCreator.resize(300, 300).into(img, new MarkerCallback(marker, progressBar));
        }

        public class MarkerCallback implements Callback {
            Marker marker;
            ProgressBar progressBar;

            MarkerCallback(Marker marker, ProgressBar progressBar) {
                this.progressBar = progressBar;
                this.marker = marker;
            }

            @Override
            public void onError(Exception error) {
                progressBar.setVisibility(View.GONE);
                error.printStackTrace();
            }

            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                if (marker != null && marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                    marker.showInfoWindow();
                }
            }
        }
    }
}
