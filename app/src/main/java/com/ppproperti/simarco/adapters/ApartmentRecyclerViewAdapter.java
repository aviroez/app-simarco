package com.ppproperti.simarco.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.Apartment;
import com.ppproperti.simarco.entities.DummyContent.DummyItem;
import com.ppproperti.simarco.fragments.ApartmentFragment.OnListFragmentInteractionListener;
import com.ppproperti.simarco.fragments.ApartmentTowerFragment;
import com.ppproperti.simarco.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ApartmentRecyclerViewAdapter extends RecyclerView.Adapter<ApartmentRecyclerViewAdapter.ViewHolder> {

    private Context context;
//    private ActivityService activity;
    private List<Apartment> list = new ArrayList<>();
    private OnListFragmentInteractionListener listener;

    public ApartmentRecyclerViewAdapter(Context context, List<Apartment> list, OnListFragmentInteractionListener listener) {
        this.context = context;
//        this.activity = activity;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apartment, parent, false);
        ImageView imageView = view.findViewById(R.id.image_view);
        ImageView imageLocation = view.findViewById(R.id.image_location);
        TextView textTitle = view.findViewById(R.id.text_title);
        TextView textAddress = view.findViewById(R.id.text_address);
        TextView textDescription = view.findViewById(R.id.text_description);
        View dividerApartment = view.findViewById(R.id.divider_apartment);
        imageView.setVisibility(View.GONE);
        imageLocation.setVisibility(View.GONE);
        dividerApartment.setVisibility(View.GONE);
        return new ViewHolder(view, imageView, imageLocation, textTitle, textAddress, textDescription);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Apartment apartment = list.get(position);
        Log.d(ApartmentRecyclerViewAdapter.class.getSimpleName(), "onBindViewHolder:"+apartment.getName());
        String url = null;
        holder.imageView.setVisibility(View.VISIBLE);

        if (apartment.getImages() != null && apartment.getImages().size() > 0){
//            url = context.getString(R.string.url) + "storage/" + apartment.getImages().get(0).getUrl();
//            url = context.getString(R.string.url) + "/" + apartment.getImages().get(0).getUrl();
            url = Helpers.imageUrl(context, apartment.getImages().get(0).getUrl(), holder.imageView, R.drawable.header_bg);
            Log.d(ApartmentRecyclerViewAdapter.class.getSimpleName(), "url:"+url);
//            holder.imageView.setImageBitmap(null);
//            Picasso.get()
//                    .load(url)
//                    .placeholder(R.drawable.user_placeholder)
//                    .error(R.drawable.user_placeholder_error)
//                    .into(holder.imageView);
//            Glide.with(context).load(url).into(holder.imageView);
        } else {
            Glide.with(context).load(R.drawable.header_bg).into(holder.imageView);
        }
        holder.textTitle.setText(apartment.getName());
        holder.textAddress.setText(apartment.getAddress());
        holder.textDescription.setText(apartment.getDescription());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ApartmentTowerFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("apartment", apartment);
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.layout_navigation_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        holder.imageLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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
        TextView textDescription;

        public ViewHolder(@NonNull View view, ImageView imageView, ImageView imageLocation, TextView textTitle, TextView textAddress, TextView textDescription) {
            super(view);
            this.view = view;
            this.imageView = imageView;
            this.imageLocation = imageLocation;
            this.textTitle = textTitle;
            this.textAddress = textAddress;
            this.textDescription = textDescription;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + view.getId() + "'";
        }
    }
}
