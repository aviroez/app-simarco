package com.ppproperti.simarco.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.google.gson.Gson;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.CustomerLead;
import com.ppproperti.simarco.entities.Order;
import com.ppproperti.simarco.entities.OrderInstallment;
import com.ppproperti.simarco.fragments.CustomerDetailFragment;
import com.ppproperti.simarco.fragments.CustomerListFragment.OnListFragmentInteractionListener;
import com.ppproperti.simarco.fragments.dummy.DummyContent.DummyItem;
import com.ppproperti.simarco.utils.Helpers;

import java.util.Arrays;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class CustomerLeadListRecyclerViewAdapter extends RecyclerView.Adapter<CustomerLeadListRecyclerViewAdapter.ViewHolder> {

    private final String TAG = CustomerLeadListRecyclerViewAdapter.class.getSimpleName();
    private final Context context;
    private final List<CustomerLead> list;
    private final OnListFragmentInteractionListener listener;
    private Order order;

    public CustomerLeadListRecyclerViewAdapter(Context context, List<CustomerLead> list, OnListFragmentInteractionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CustomerLead customer = list.get(position);
        Log.d(TAG, "onBindViewHolder"+new Gson().toJson(customer));
        order = new Order();
        OrderInstallment installment = null;

        if (customer.getOrders() != null && customer.getOrders().size() > 0){
            for (Order o:customer.getOrders()) {
                order = o;

                installment = getInstallment(o);
                break;
            }
        }

        holder.customer = customer;
        holder.textName.setText(customer.getName());
        if (order != null){
            holder.textLabelStatus.setText(order.getStatus());
            if (order.getOrderNo() != null){
                holder.textOrderNo.setText("SP no:"+order.getOrderNo());
            } else if (order.getBookingNo() != null){
                holder.textOrderNo.setText("SPS no:"+order.getBookingNo());
            }
        }

        if (customer.getEmail() != null){
            holder.textEmail.setText(customer.getEmail());
        } else {
            holder.textEmail.setText("-");
        }

        if (customer.getHandphone() != null){
            holder.textHandphone.setText(customer.getHandphone());
        } else {
            holder.textHandphone.setText("-");
        }

        if (installment != null){
            String price = Helpers.currency(installment.getPrice()+installment.getFine());
            holder.textInstallmentNumber.setText(installment.getDescription());
            holder.textInstallmentPrice.setText(price);
        }

        if (order.getStatus() != null && order.getStatus().length() > 0){
            holder.textLabelStatus.setBootstrapBrand(Helpers.parseStatus(order.getStatus()));
        }

//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != listener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    listener.onListFragmentInteraction(holder.customer);
//                }
//            }
//        });

        holder.buttonPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helpers.call(context, customer.getHandphone());
            }
        });

        holder.buttonSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helpers.sms(context, customer.getHandphone(), "");
            }
        });

        holder.buttonWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helpers.whatsapp(context, customer.getHandphone(), "");
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("customer_lead", customer);
                Helpers.moveFragment(context, new CustomerDetailFragment(), bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView textName;
        public final BootstrapLabel textLabelStatus;
        public final TextView textOrderNo;
        public final TextView textInstallmentNumber;
        public final TextView textInstallmentPrice;
        public final TextView textEmail;
        public final TextView textHandphone;
        public final BootstrapButton buttonPhone;
        public final BootstrapButton buttonSms;
        public final BootstrapButton buttonWhatsapp;
        public final BootstrapButton buttonView;
        public CustomerLead customer;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            textLabelStatus = (BootstrapLabel) view.findViewById(R.id.taxt_label_status);
            textName = (TextView) view.findViewById(R.id.text_name);
            textOrderNo = (TextView) view.findViewById(R.id.text_order_no);
            textInstallmentNumber = (TextView) view.findViewById(R.id.text_installment_number);
            textInstallmentPrice = (TextView) view.findViewById(R.id.text_installment_price);
            textEmail = (TextView) view.findViewById(R.id.text_email);
            textHandphone = (TextView) view.findViewById(R.id.text_handphone);
            buttonPhone = (BootstrapButton) view.findViewById(R.id.button_phone);
            buttonSms = (BootstrapButton) view.findViewById(R.id.button_sms);
            buttonWhatsapp = (BootstrapButton) view.findViewById(R.id.button_whatsapp);
            buttonView = (BootstrapButton) view.findViewById(R.id.button_view);
        }

        @Override
        public String toString() {
            return super.toString() + " ";
        }
    }

    private OrderInstallment getInstallment(Order o) {
        if (o.getOrderInstallments() != null && o.getOrderInstallments().size() > 0){
            String[] statuses = { "new","sp1","sp2","sp3" };

            for (OrderInstallment ins: o.getOrderInstallments()) {
                if (Arrays.asList(statuses).contains(ins.getStatus())){
                    return ins;
                }
            }
        }


        return null;
    }
}
