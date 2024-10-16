package com.ppproperti.simarco.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.Customer;

import java.util.ArrayList;
import java.util.List;

public class SpinnerCustomerAdapter extends BaseAdapter {
    private Context context;
    private List<Customer> list = new ArrayList<>();

    public SpinnerCustomerAdapter(Context context, List<Customer> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return list.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Customer customer = list.get(i);
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_customer_spinner, null);

        TextView textViewName = (TextView) view.findViewById(R.id.text_name);
        TextView textViewDescription = (TextView) view.findViewById(R.id.text_description);
        View separator = (View) view.findViewById(R.id.separator);

        textViewName.setText(customer.getName());
        if (customer.getHandphone() != null){
            textViewDescription.setText("No HP: "+customer.getHandphone());
        } else {
            textViewDescription.setVisibility(View.GONE);
        }

        if (i < getCount() - 1){
            separator.setVisibility(View.VISIBLE);
        } else {
            separator.setVisibility(View.GONE);
        }
        return view;
    }
}
