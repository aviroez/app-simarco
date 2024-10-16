package com.ppproperti.simarco.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.beardedhen.androidbootstrap.BootstrapBadge;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.Order;
import com.ppproperti.simarco.fragments.OrderDetailFragment;
import com.ppproperti.simarco.fragments.OrderListFragment.OnListFragmentInteractionListener;
import com.ppproperti.simarco.fragments.dummy.DummyContent.DummyItem;
import com.ppproperti.simarco.utils.Helpers;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final List<Order> list;
    private final OnListFragmentInteractionListener mListener;

    public OrderRecyclerViewAdapter(Context context, List<Order> list, OnListFragmentInteractionListener listener) {
        this.context = context;
        this.list = list;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Order order = list.get(position);
        String orderNo = "No SPS: "+order.getBookingNo();
        if (order.getOrderNo() != null && order.getOrderNo().length() > 0){
            orderNo = "No SP: "+order.getOrderNo();
        }
        String description = order.getName();
        if (order.getApartmentUnit() != null && order.getApartmentUnit().getApartmentFloor() != null && order.getApartmentUnit().getApartmentTower() != null){
            description += " (";
            if (order.getApartmentUnit().getApartmentTower() != null) {
                description += order.getApartmentUnit().getApartmentTower().getName();
            }
            if (order.getApartmentUnit().getApartmentFloor() != null) {
                description += ", Floor: "+order.getApartmentUnit().getApartmentFloor().getNumber();
            }
            description += ", Unit No: "+order.getApartmentUnit().getUnitNumber()+")";
        }
        holder.textOrderNo.setText(orderNo);
        holder.textStatus.setBadgeText(Helpers.getStatus(order.getStatus()));
        holder.textStatus.setBootstrapBrand(Helpers.getStatusLabel(order.getStatus()));
        holder.textDescription.setText(description);
        holder.textDate.setText(Helpers.formatDate(order.getOrderDate()));
        holder.textPrice.setText(Helpers.currency(order.getFinalPrice()));
        holder.order = order;

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("order", holder.order);
                Helpers.moveFragment(context, new OrderDetailFragment(), bundle);

                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(order);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView textOrderNo;
        public final BootstrapBadge textStatus;
        public final TextView textDescription;
        public final TextView textDate;
        public final TextView textPrice;
        public Order order = new Order();

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            textOrderNo = (TextView) view.findViewById(R.id.text_order_no);
            textStatus = (BootstrapBadge) view.findViewById(R.id.text_status);
            textDescription = (TextView) view.findViewById(R.id.description);
            textDate = (TextView) view.findViewById(R.id.text_date);
            textPrice = (TextView) view.findViewById(R.id.text_price);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + order.getName() + "'";
        }
    }
}
