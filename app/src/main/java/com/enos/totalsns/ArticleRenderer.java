package com.enos.totalsns;

/**
 * @author utmostest
 * Created 2022-12-08 at 오후 6:40
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.enos.totalsns.custom.MultiDrawable;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.databinding.MultiProfileBinding;
import com.enos.totalsns.util.GlideUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws profile photos inside markers (using IconGenerator).
 * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
 */
public class ArticleRenderer extends DefaultClusterRenderer<Article> {
    private final IconGenerator mIconGenerator;
    private final IconGenerator mClusterIconGenerator;
    private final ImageView mImageView;
    private final int mDimension;
    private final Context mContext;
    private final MultiProfileBinding mBinding;
    private final ClusterManager mClusterManager;
    private final GoogleMap mMap;
    private boolean isMaxZoom = false;

    public ArticleRenderer(Context context, GoogleMap map, ClusterManager clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        mClusterManager = clusterManager;
        mMap = map;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBinding = MultiProfileBinding.inflate(layoutInflater);
        mClusterIconGenerator = new IconGenerator(context);
        mClusterIconGenerator.setContentView(mBinding.getRoot());
        mIconGenerator = new IconGenerator(context);

        mImageView = new ImageView(context);
        mDimension = (int) context.getResources().getDimension(R.dimen.custom_profile_image);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
        mIconGenerator.setContentView(mImageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull Article article, MarkerOptions markerOptions) {
        // Draw a single Article - show their profile photo and set the info window to show their name

        markerOptions
                .icon(getItemIcon(article, markerOptions))
                .title(article.getTitle())
                .snippet(article.getSnippet());
    }

    @Override
    protected void onClusterItemUpdated(@NonNull Article article, Marker marker) {
        // Same implementation as onBeforeClusterItemRendered() (to update cached markers)
        marker.setIcon(getItemIcon(article, marker));
        marker.setTitle(article.getTitle());
        marker.setSnippet(article.getSnippet());
    }

    /**
     * Get a descriptor for a single Article (i.e., a marker outside a cluster) from their
     * profile photo to be used for a marker icon
     *
     * @param article Article to return an BitmapDescriptor for
     * @return the Article's profile photo as a BitmapDescriptor
     */
    private synchronized BitmapDescriptor getItemIcon(Article article, Marker marker) {
        GlideUtils.loadNearByProfileImage(mContext.getApplicationContext(), article.getProfileImg(), new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                mImageView.setImageDrawable(resource);
                Bitmap icon = mIconGenerator.makeIcon();
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
            }
        });
        Bitmap icon = mIconGenerator.makeIcon();
        return BitmapDescriptorFactory.fromBitmap(icon);
    }

    /**
     * Get a descriptor for a single Article (i.e., a marker outside a cluster) from their
     * profile photo to be used for a marker icon
     *
     * @param article Article to return an BitmapDescriptor for
     * @return the Article's profile photo as a BitmapDescriptor
     */
    private synchronized BitmapDescriptor getItemIcon(Article article, MarkerOptions markerOptions) {
        GlideUtils.loadNearByProfileImage(mContext.getApplicationContext(), article.getProfileImg(), new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                mImageView.setImageDrawable(resource);
                Bitmap icon = mIconGenerator.makeIcon();
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
            }
        });
        Bitmap icon = mIconGenerator.makeIcon();
        return BitmapDescriptorFactory.fromBitmap(icon);
    }

    @Override
    protected void onBeforeClusterRendered(@NonNull Cluster<Article> cluster, MarkerOptions markerOptions) {
        // Draw multiple people.
        // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
        markerOptions.icon(getClusterIcon(cluster, markerOptions));
    }

    @Override
    protected void onClusterUpdated(@NonNull Cluster<Article> cluster, Marker marker) {
        // Same implementation as onBeforeClusterRendered() (to update cached markers)
        marker.setIcon(getClusterIcon(cluster, marker));
    }

    /**
     * Get a descriptor for multiple people (a cluster) to be used for a marker icon. Note: this
     * method runs on the UI thread. Don't spend too much time in here (like in this example).
     *
     * @param cluster cluster to draw a BitmapDescriptor for
     * @return a BitmapDescriptor representing a cluster
     */
    private synchronized BitmapDescriptor getClusterIcon(Cluster<Article> cluster, Marker marker) {
        List<Drawable> profilePhotos = new ArrayList<>(Math.min(4, cluster.getSize()));
        int width = mDimension;
        int height = mDimension;

        MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
        multiDrawable.setBounds(0, 0, width, height);

        int count = 0;
        for (Article p : cluster.getItems()) {
            // Draw 4 at most.
            if (count == 4) break;
            GlideUtils.loadNearByProfileImage(mContext.getApplicationContext(), p.getProfileImg(), new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    resource.setBounds(0, 0, width, height);
                    multiDrawable.getDrawableList().add(resource);
                    mBinding.image.setImageDrawable(multiDrawable);
                    Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
//                    profilePhotos.add(resource);
                }
            });
            count++;
        }

        mBinding.image.setImageDrawable(multiDrawable);
        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        return BitmapDescriptorFactory.fromBitmap(icon);
    }

    /**
     * Get a descriptor for multiple people (a cluster) to be used for a marker icon. Note: this
     * method runs on the UI thread. Don't spend too much time in here (like in this example).
     *
     * @param cluster cluster to draw a BitmapDescriptor for
     * @return a BitmapDescriptor representing a cluster
     */
    private synchronized BitmapDescriptor getClusterIcon(Cluster<Article> cluster, MarkerOptions markerOptions) {
        List<Drawable> profilePhotos = new ArrayList<>(Math.min(4, cluster.getSize()));
        int width = mDimension;
        int height = mDimension;

        MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
        multiDrawable.setBounds(0, 0, width, height);

        int count = 0;
        for (Article p : cluster.getItems()) {
            // Draw 4 at most.
            if (count == 4) break;
            GlideUtils.loadNearByProfileImage(mContext.getApplicationContext(), p.getProfileImg(), new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    resource.setBounds(0, 0, width, height);
                    multiDrawable.getDrawableList().add(resource);
                    Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
//                    profilePhotos.add(resource);
                }
            });
            count++;
        }

        mBinding.image.setImageDrawable(multiDrawable);
        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        return BitmapDescriptorFactory.fromBitmap(icon);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {

        AppExecutors appExecutors = new AppExecutors();
        appExecutors.mainThread().execute(() -> isMaxZoom = (mMap.getMaxZoomLevel() - 1f) <= mMap.getCameraPosition().zoom);
        // max zoom 이었을 때 아이템으로 전환
        return !isMaxZoom && (cluster.getSize() > 1);
    }
}