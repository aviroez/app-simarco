package com.ppproperti.simarco.adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.ApartmentUnit;
import com.ppproperti.simarco.fragments.ApartmentUnitFragment.OnListFragmentInteractionListener;
import com.ppproperti.simarco.fragments.PaymentSchemaFragment;
import com.ppproperti.simarco.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ApartmentUnit} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ApartmentUnitRecyclerViewAdapter extends RecyclerView.Adapter<ApartmentUnitRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private final List<ApartmentUnit> list;
    private OnListFragmentInteractionListener listener;

    public ApartmentUnitRecyclerViewAdapter(Context context, List<ApartmentUnit> list, OnListFragmentInteractionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apartment_unit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ApartmentUnit apartmentUnit = list.get(position);
        holder.textTitle.setVisibility(View.GONE);
//        holder.textDescription.setVisibility(View.GONE);
        String floor = context.getString(R.string.floor_label)+" ";
        if (apartmentUnit.getApartmentFloor() != null){
            floor += apartmentUnit.getApartmentFloor().getNumber();
        }

        String view = context.getString(R.string.view_label)+" ";
        if (apartmentUnit.getApartmentView() != null){
            view += apartmentUnit.getApartmentView().getName();
        }

        String bedroom = context.getString(R.string.bedroom_label)+" ";
        if (apartmentUnit.getApartmentType() != null){
            bedroom += apartmentUnit.getApartmentType().getBedroomCount();
        }
        holder.textFloor.setText(floor);
        holder.textView.setText(view);
        holder.textType.setText(bedroom);
        holder.textPrice.setText(Helpers.currency(apartmentUnit.getTotalPrice()));
        Log.d(ApartmentRecyclerViewAdapter.class.getSimpleName(), "onBindViewHolder:"+new Gson().toJson(apartmentUnit));
        List<ApartmentUnit> listGroup = apartmentUnit.getGrouped();

        if (listGroup != null && listGroup.size() > 0){
            ArrayList<CharSequence> list = new ArrayList<>();
            String description = "Unit Ready:";
            for (ApartmentUnit unit: listGroup) {
                if (unit.getStatus().equals("ready")){
                    list.add(String.valueOf(unit.getUnitNumber()));
                }
            }
            description += TextUtils.join(",", list);

            holder.textDescription.setText(description);
        }
//        holder.mContentView.setText(list.get(position).content);
//
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PaymentSchemaFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("apartmentUnit", apartmentUnit);
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.layout_navigation_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView textTitle;
        public final TextView textDescription;
        public final TextView textFloor;
        public final TextView textView;
        public final TextView textType;
        public final TextView textPrice;
        public ApartmentUnit apartmentUnit = new ApartmentUnit();

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            textTitle = (TextView) view.findViewById(R.id.text_title);
            textDescription = (TextView) view.findViewById(R.id.text_description);
            textFloor = (TextView) view.findViewById(R.id.text_floor);
            textView = (TextView) view.findViewById(R.id.text_view);
            textType = (TextView) view.findViewById(R.id.text_type);
            textPrice = (TextView) view.findViewById(R.id.text_price);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + apartmentUnit.toString() + "'";
        }
    }
}
