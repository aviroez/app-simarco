package com.ppproperti.simarco.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.PaymentType;

import java.util.ArrayList;
import java.util.List;

public class SpinnerPaymentTypeAdapter extends BaseAdapter {
    private Context context;
    private List<PaymentType> list = new ArrayList<>();

    public SpinnerPaymentTypeAdapter(Context context, List<PaymentType> list) {
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
        PaymentType paymentType = list.get(i);
        LayoutInflater inflater = (LayoutInflater.from(context));
        view = inflater.inflate(R.layout.item_payment_type, null);
//        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView textViewName = (TextView) view.findViewById(R.id.text_name);
//        icon.setImageResource(flags[i]);
        textViewName.setText(paymentType.getName());
        return view;
    }
}
