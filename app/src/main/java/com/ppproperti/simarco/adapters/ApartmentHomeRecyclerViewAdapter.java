package com.ppproperti.simarco.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.Apartment;
import com.ppproperti.simarco.entities.DummyContent.DummyItem;
import com.ppproperti.simarco.fragments.ApartmentTowerFragment;
import com.ppproperti.simarco.fragments.HomeFragment.OnListFragmentInteractionListener;
import com.ppproperti.simarco.utils.Helpers;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ApartmentHomeRecyclerViewAdapter extends RecyclerView.Adapter<ApartmentHomeRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private static String TAG = ApartmentHomeRecyclerViewAdapter.class.getSimpleName();
    //    private ActivityService activity;
    private List<Apartment> list = new ArrayList<>();
    private OnListFragmentInteractionListener mListener;

    public ApartmentHomeRecyclerViewAdapter(Context context, List<Apartment> list, OnListFragmentInteractionListener listener) {
        this.context = context;
//        this.activity = activity;
        this.list = list;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apartment_home, parent, false);
        ImageView imageView = view.findViewById(R.id.image_view);
        ImageView imageLocation = view.findViewById(R.id.image_location);
        TextView textTitle = view.findViewById(R.id.text_title);
        TextView textAddress = view.findViewById(R.id.text_address);
        return new ViewHolder(view, imageView, imageLocation, textTitle, textAddress);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Apartment apartment = list.get(position);
        Log.d(TAG, "onBindViewHolder:" + apartment.getName());
        String url = null;

        if (apartment.getImages() != null && apartment.getImages().size() > 0) {
//            url = context.getString(R.string.url) + "storage/" + apartment.getImages().get(0).getUrl();
            url = Helpers.imageUrl(context, apartment.getImages().get(0).getUrl().toString(), holder.imageView, R.drawable.header_bg);
            Log.d(TAG, "url:"+url);
//            holder.imageView.setImageBitmap(null);
//            Picasso.get()
//                    .load(url)
//                    .load(R.drawable.header_bg)
//                    .placeholder(R.drawable.user_placeholder)
//                    .error(R.drawable.user_placeholder_error)
//                    .into(holder.imageView);
//            Glide.with(context).load(url).into(glideTarget(context, "apartments", FilenameUtils.getName(apartment.getImages().get(0).getUrl())));
//            Glide.with(context).load(url).into(holder.imageView);
        }
        holder.textTitle.setText(apartment.getName());
        holder.textAddress.setText(apartment.getAddress());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("apartment", apartment);

                Helpers.moveFragment(context, new ApartmentTowerFragment(), bundle);
            }
        });

        holder.imageLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private com.bumptech.glide.request.target.Target glideTarget(Context context, final String imageDir, final String imageName) {
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        return new com.bumptech.glide.request.target.Target() {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {

            }

            @Override
            public void onResourceReady(@NonNull Object resource, @Nullable Transition transition) {

            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void getSize(@NonNull SizeReadyCallback cb) {

            }

            @Override
            public void removeCallback(@NonNull SizeReadyCallback cb) {

            }

            @Override
            public void setRequest(@Nullable Request request) {

            }

            @Nullable
            @Override
            public Request getRequest() {
                return null;
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onStop() {

            }

            @Override
            public void onDestroy() {

            }
        };
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView imageView;
        ImageView imageLocation;
        TextView textTitle;
        TextView textAddress;

        public ViewHolder(@NonNull View view, ImageView imageView, ImageView imageLocation, TextView textTitle, TextView textAddress) {
            super(view);
            this.view = view;
            this.imageView = imageView;
            this.imageLocation = imageLocation;
            this.textTitle = textTitle;
            this.textAddress = textAddress;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + view.getId() + "'";
        }
    }
}
