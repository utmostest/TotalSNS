package com.enos.totalsns.nearby;

import static com.enos.totalsns.data.Constants.INVALID_POSITION;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.databinding.FragmentNearbyArticleBinding;
import com.enos.totalsns.timeline.detail.TimelineDetailActivity;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.GlideUtils;
import com.enos.totalsns.util.ViewModelFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class NearbyArticleFragment extends Fragment
        implements OnMapReadyCallback, OnSuccessListener<Location>, OnFailureListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnMyLocationButtonClickListener {

    private static final int REQUEST_USED_PERMISSION = 2810;

    private static final String[] needPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private static final LatLng DEFAULT_LOCATION = new LatLng(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE);
    private static final int DEFAULT_ZOOM = 15;

    private static final int INTERVAL_TIME = 5000;
    private static final int FASTEST_INTERVAL_TIME = 2000;

    private GoogleMap map;

    private LatLng lastKnownLocation;

    private boolean isMapMoved = false;

    private Circle searchRadiusCircle;

    private NearbyArticleViewModel viewModel;

    private FragmentNearbyArticleBinding mBinding;

    private List<Marker> markerList;
    private List<Polyline> polylines;

    public static NearbyArticleFragment newInstance() {
        return new NearbyArticleFragment();
    }

    public NearbyArticleFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, (ViewModelProvider.Factory) ViewModelFactory.getInstance(getContext())).get(NearbyArticleViewModel.class);
        markerList = new ArrayList<>();
        polylines = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_nearby_article, parent, false);

        mBinding.map.onCreate(savedInstanceState);
        mBinding.map.getMapAsync(this);
        mBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (searchRadiusCircle != null) {
                    searchRadiusCircle.setRadius(getRadiusFromProgress(seekBar.getProgress()));
                    updateZoomLevel(null);
                }
            }
        });

        viewModel.getNearbySearchList().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                addMarkers(list);
            }
        });
        mBinding.fabNearby.inflate(R.menu.speed_dial_nearby);
        mBinding.fabNearby.setOnActionSelectedListener(speedDialActionItem -> {
            switch (speedDialActionItem.getId()) {
                case R.id.sd_nearby_more:
                    viewModel.fetchNearbyPast();
                    return false; // true to keep the Speed Dial open
                case R.id.sd_nearby_new:
                    searchRadiusCircle = null;
                    clearLines();
                    markerList.clear();
                    map.clear();
                    addSearchRadiusCircle();
                    viewModel.fetchNearbyFirst(searchRadiusCircle.getCenter(), searchRadiusCircle.getRadius() / 1000);
                    return false;
                default:
                    return false;
            }
        });
        return mBinding.getRoot();
    }

    private synchronized void addMarkers(List<Article> list) {
        int pixels = getContext().getResources().getDimensionPixelSize(R.dimen.custom_profile_image);
        for (Article article : list) {
            if (article.getLatitude() == INVALID_POSITION || article.getLongitude() == INVALID_POSITION)
                continue;
            GlideUtils.loadProfileImageWithTarget(getContext(), article.getProfileImg(), pixels, new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(article.getLatitude(), article.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromBitmap(resource)).title(article.getUserId()).snippet(article.getMessage()));
                    if (marker != null) {
                        marker.setTag(article);
                        markerList.add(marker);
                    }
                }
            });
        }
    }

    private LatLngBounds scatterMarkersOnSamePosition(LatLng pos, List<Marker> markers) {
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        bounds.include(pos);
        boolean isFirst = true;
        for (Marker marker : markers) {
            if (pos.latitude == marker.getPosition().latitude && pos.longitude == marker.getPosition().longitude) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                LatLng original = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                LatLng tempPosition = getTempPosition(original);
                bounds.include(tempPosition);
                marker.setPosition(tempPosition);
                drawLine(original, tempPosition);
            }
        }
        return bounds.build();
    }

    private void clearLines() {
        for (Polyline polyline : polylines) {
            polyline.remove();
        }
        polylines.clear();
    }

    private void drawLine(LatLng startLatLng, LatLng endLatLng) {
        PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(2).color(Color.BLACK).geodesic(true);
        polylines.add(map.addPolyline(options));
    }

    private LatLng getTempPosition(LatLng original) {
        double lat = 0.0007 * Math.random() - 0.00035;
        double lon = 0.0007 * Math.random() - 0.00035;
        return new LatLng(original.latitude + lat, original.longitude + lon);
    }

    private void restoreMarkerPosition(List<Marker> markers) {
        clearLines();
        for (Marker marker : markers) {
            if (marker.getTag() != null && marker.getTag() instanceof Article) {
                Article article = (Article) marker.getTag();
                LatLng current = marker.getPosition();
                LatLng original = new LatLng(article.getLatitude(), article.getLongitude());
                if (current.latitude != original.latitude || current.longitude == original.longitude) {
                    marker.setPosition(original);
                }
            }
        }
    }

    private double getRadiusFromProgress(int progress) {
        double radiusMin = 500;
        double radiusMax = 5000;
        int progressMax = 100;
        double result = radiusMin + (radiusMax - radiusMin) * progress / progressMax;
        return result;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestPermissionIfNeeded();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean permissionToLocationAccepted = true;

        switch (requestCode) {
            case REQUEST_USED_PERMISSION:

                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        permissionToLocationAccepted = false;
                        break;
                    }
                }
                break;
        }

        if (!permissionToLocationAccepted) {
            Toast.makeText(getContext(), R.string.gps_permission_warning, Toast.LENGTH_SHORT).show();
        } else {
            getMyLocation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBinding.map != null) {
            mBinding.map.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mBinding.map != null) {
            mBinding.map.onPause();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mBinding.map != null) {
            mBinding.map.onStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mBinding.map != null) {
            try {
                mBinding.map.onDestroy();
            } catch (NullPointerException e) {
            }
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mBinding.map != null) {
            mBinding.map.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBinding.map != null) {
            mBinding.map.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        initGoogleMap(googleMap);

        updateZoomLevel(DEFAULT_LOCATION);

        getMyLocation();
    }

    private void initGoogleMap(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraMoveStartedListener(this);

        map.setOnMarkerClickListener(marker -> {
            LatLng position = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            LatLngBounds bounds = scatterMarkersOnSamePosition(position, markerList);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 16));
            marker.showInfoWindow();
//            if (marker.isVisible()) {
//                LatLng current = marker.getPosition();
//                marker.setPosition(new LatLng(current.latitude + 0.01, current.longitude + 0.01));
//            }
            return true;
        });

        map.setOnMapClickListener(latLng -> {
            restoreMarkerPosition(markerList);
        });

        map.setOnInfoWindowClickListener(marker -> {
            Object obj = marker.getTag();
            if (obj != null && obj instanceof Article) {
                Article article = (Article) obj;
                TimelineDetailActivity.start((AppCompatActivity) getActivity(), article);
            }
        });

    }

    private void addSearchRadiusCircle() {
        if (searchRadiusCircle == null) {
            //radius unit is meter
            searchRadiusCircle = map.addCircle(new CircleOptions()
                    .center(lastKnownLocation == null ? DEFAULT_LOCATION : lastKnownLocation)
                    .radius(getRadiusFromProgress(mBinding.seekBar == null ? 0 : mBinding.seekBar.getProgress()))
                    .strokeColor(getResources().getColor(R.color.colorPrimary)));
        }
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        addSearchRadiusCircle();

        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setOnMyLocationButtonClickListener(this);

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(this);
        task.addOnFailureListener(this);
    }

    @Override
    public void onSuccess(Location location) {
        if (location == null) return;
        lastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (searchRadiusCircle != null)
            searchRadiusCircle.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));

        updateZoomLevel(new LatLng(location.getLatitude(), location.getLongitude()));

//        if (!isMapMoved) {
//            updateMyLocation();
//        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        e.printStackTrace();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        updateZoomLevel(DEFAULT_LOCATION);

        map.setMyLocationEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private void updateMyLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(INTERVAL_TIME);
        locationRequest.setFastestInterval(FASTEST_INTERVAL_TIME);

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();

                if (!isMapMoved) {
                    updateZoomLevel(new LatLng(location.getLatitude(), location.getLongitude()));
                }

            }
        }, null);
    }

    @Override
    public void onCameraMoveStarted(int i) {
        if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            isMapMoved = true;
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        if (!requestPermissionIfNeeded()) {
            isMapMoved = false;
            getMyLocation();
        }
        return false;
    }

    private boolean requestPermissionIfNeeded() {
        return ActivityUtils.requestPermissionIfNeeded((AppCompatActivity) getActivity(), REQUEST_USED_PERMISSION, needPermissions);
    }

    private void updateZoomLevel(LatLng location) {
        if (searchRadiusCircle != null)
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(getLatLngBoundsFromCircle(searchRadiusCircle), DEFAULT_ZOOM));
        else if (location != null)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
    }

    public LatLngBounds getLatLngBoundsFromCircle(Circle circle) {
        if (circle != null) {
            return new LatLngBounds.Builder()
                    .include(SphericalUtil.computeOffset(circle.getCenter(), circle.getRadius() * Math.sqrt(2), 45))
                    .include(SphericalUtil.computeOffset(circle.getCenter(), circle.getRadius() * Math.sqrt(2), 225))
                    .build();
        }
        return null;
    }
}