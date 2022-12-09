package com.enos.totalsns.nearby;

import static com.enos.totalsns.data.Constants.INVALID_POSITION;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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

import com.enos.totalsns.ArticleRenderer;
import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.databinding.FragmentNearbyArticleBinding;
import com.enos.totalsns.timeline.detail.TimelineDetailActivity;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.ViewModelFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NearbyArticleFragment extends Fragment
        implements OnMapReadyCallback, OnSuccessListener<Location>, OnFailureListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnMyLocationButtonClickListener, ClusterManager.OnClusterClickListener<Article>, ClusterManager.OnClusterInfoWindowClickListener<Article>, ClusterManager.OnClusterItemClickListener<Article>, ClusterManager.OnClusterItemInfoWindowClickListener<Article> {

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

    private ClusterManager<Article> mClusterManager;

    private List<Article> originalArticles;

    private List<Article> tempArticleList;

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

        originalArticles = new ArrayList<>();
        tempArticleList = new ArrayList<>();
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
                    mClusterManager.clearItems();
                    mClusterManager.cluster(); // 아이템을 clear 한 후에 바로 클러스터 해줘야 오류가 안생긴다.
                    clearLines();
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
        originalArticles.clear();
        for (Article article : list) {
            if (article.getLatitude() == INVALID_POSITION || article.getLongitude() == INVALID_POSITION)
                continue;
            try {
                originalArticles.add(article.clone());
                mClusterManager.addItem(article.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
//            GlideUtils.loadProfileImageWithTarget(getContext(), article.getProfileImg(), pixels, new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                    map.addMarker(new MarkerOptions().position(new LatLng(article.getLatitude(), article.getLongitude()))
//                            .icon(BitmapDescriptorFactory.fromBitmap(resource)).title(article.getUserId()).snippet(article.getMessage())).setTag(article);
//                }
//            });
        }
        mClusterManager.cluster();
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
//            finish();
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
//        map.setOnInfoWindowClickListener(marker -> {
//            Object obj = marker.getTag();
//            if (obj != null && obj instanceof Article) {
//                Article article = (Article) obj;
//                TimelineDetailActivity.start((AppCompatActivity) getActivity(), article);
//            }
//        });

        mClusterManager = new ClusterManager<>(requireContext(), map);
        mClusterManager.setRenderer(new ArticleRenderer(getContext(), map, mClusterManager));
        map.setOnCameraIdleListener(mClusterManager);
        map.setOnMarkerClickListener(mClusterManager);
        map.setOnInfoWindowClickListener(mClusterManager);
        map.setOnMapClickListener(latLng -> {
            restorePosition();
        });
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        mClusterManager.cluster();
    }

    private void restorePosition() {
        for (Article article : originalArticles) {
            try {
                mClusterManager.updateItem(article.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        clearLines();
        tempArticleList.clear();
        mClusterManager.cluster();
    }

    private void drawLines() {
        if (originalArticles == null || tempArticleList == null) return;
        for (Article temp : tempArticleList) {
            for (Article original : originalArticles) {
                SingletonToast.getInstance().log("line", temp + "\n" + original);
                if (Objects.equals(original.getArticleId(), temp.getArticleId())) {
                    SingletonToast.getInstance().log("line equal", temp + "\n" + original);
                    drawLine(original.getPosition(), temp.getPosition());
                }
            }
        }
    }

    private void drawLine(LatLng startLatLng, LatLng endLatLng) {
        PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(2).color(Color.BLACK).geodesic(true);
        polylines.add(map.addPolyline(options));
    }

    private void clearLines() {
        for (Polyline polyline : polylines) {
            polyline.remove();
        }
        polylines.clear();
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

        if (!isMapMoved) {
            updateMyLocation();
        }
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

    private List<Article> getOriginalList(Cluster<Article> cluster) {
        ArrayList<Article> newList = new ArrayList<>();
        for (Article temp : cluster.getItems()) {
            for (Article original : originalArticles) {
                if (temp.getArticleId() == original.getArticleId()) {
                    try {
                        newList.add(original.clone());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return newList;
    }

    @Override
    public boolean onClusterClick(Cluster<Article> cluster) {

        restorePosition();
        List<Article> originList = getOriginalList(cluster);
        LatLngBounds bounds = getLatLngBounds(originList);

        LatLng center = bounds.getCenter();
        int size = (int) Math.floor(Math.sqrt(cluster.getSize()));
        double gapBeetweenMarker = Math.max(0.0001, 0.0000005 * getRadiusFromProgress(mBinding.seekBar.getProgress()));
        int horizontalOrder = -size / 2;
        int verticalOrder = -size / 2;
        int count = 0;

        LatLngBounds.Builder builder = LatLngBounds.builder();

        for (Article temp : originList) {
            if (bounds.contains(temp.getPosition())) {
                int h = horizontalOrder + (size > 0 ? (count % size) : count);
                int v = verticalOrder + (size > 0 ? (count / size) : 0);
                LatLng latLng = new LatLng(center.latitude + gapBeetweenMarker * h, center.longitude + gapBeetweenMarker * v);
                Article item = null;
                try {
                    item = temp.clone();
                    item.setPosition(latLng);
                    builder.include(new LatLng(item.getLatitude(), item.getLongitude()));
                    mClusterManager.updateItem(item.clone());

                    tempArticleList.add(item.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                Log.i("latlng", count + ":" + latLng);
                count++;
            }
        }

        LatLngBounds newBounds = builder.build();

        drawLines();

        // Animate camera to the bounds
        try {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(newBounds, 15));
//            map.animateCamera(CameraUpdateFactory.newLatLngZoom(newBounds.getCenter(), map.getMaxZoomLevel()));
//            map.moveCamera(CameraUpdateFactory.newLatLngZoom(newBounds.getCenter(), map.getMaxZoomLevel())); //설정시 위치가 같지 않은 항목이 있을 경우 가끔 오류생김

        } catch (Exception e) {
            e.printStackTrace();
        }
        mClusterManager.cluster();

        return false;
    }

    private LatLngBounds getLatLngBounds(List<Article> list) {
        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (Article item : list) {
            builder.include(new LatLng(item.getLatitude(), item.getLongitude()));
        }
        // Get the LatLngBounds
        return builder.build();
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Article> cluster) {
        // Does nothing, but you could go to a list of the users.
        mClusterManager.cluster();
    }

    @Override
    public boolean onClusterItemClick(Article item) {
        // Does nothing, but you could go into the user's profile page, for example.
        mClusterManager.cluster();
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Article item) {
        // Does nothing, but you could go into the user's profile page, for example.

        TimelineDetailActivity.start((AppCompatActivity) getActivity(), item);

    }
}