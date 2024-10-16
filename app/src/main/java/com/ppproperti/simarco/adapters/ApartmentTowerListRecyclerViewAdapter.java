package com.ppproperti.simarco.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.Apartment;
import com.ppproperti.simarco.entities.ApartmentTower;
import com.ppproperti.simarco.fragments.ApartmentTowerFragment.OnListFragmentInteractionListener;
import com.ppproperti.simarco.fragments.ApartmentUnitFragment;
import com.ppproperti.simarco.utils.Helpers;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ApartmentTower} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ApartmentTowerListRecyclerViewAdapter extends RecyclerView.Adapter<ApartmentTowerListRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final List<ApartmentTower> list;
    private final OnListFragmentInteractionListener listener;

    public ApartmentTowerListRecyclerViewAdapter(Context context, List<ApartmentTower> list, OnListFragmentInteractionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apartment_tower, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.apartmentTower = list.get(position);
        if (holder.apartmentTower != null){
            holder.text_name.setText(holder.apartmentTower.getName());
            holder.text_description.setText(holder.apartmentTower.getDescription());
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("apartmentTower", holder.apartmentTower);
                bundle.putParcelable("apartment", holder.apartmentTower.getApartment());
                Helpers.moveFragment(context, new ApartmentUnitFragment(), bundle);

                if (null != listener) {
                    listener.onListFragmentInteraction(holder.apartmentTower);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView text_name;
        public final TextView text_description;
        public ApartmentTower apartmentTower = new ApartmentTower();

        public ViewHolder(View view) {
            super(view);
            mView = view;
            text_name = (TextView) view.findViewById(R.id.text_name);
            text_description = (TextView) view.findViewById(R.id.text_description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + apartmentTower.getName() + "'";
        }
    }
}
