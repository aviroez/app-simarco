package com.ppproperti.simarco.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.PaymentSchema;
import com.ppproperti.simarco.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

public class SpinnerPaymentSchemaAdapter extends BaseAdapter {
    private Context context;
    private List<PaymentSchema> list = new ArrayList<>();

    public SpinnerPaymentSchemaAdapter(Context context, List<PaymentSchema> list) {
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
        PaymentSchema paymentSchema = list.get(i);
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_payment_schema, null);

        TextView textViewName = (TextView) view.findViewById(R.id.text_name);
        TextView textViewDescription = (TextView) view.findViewById(R.id.text_description);

        textViewName.setText(paymentSchema.getName());
        String description = "";
        if (paymentSchema.getDpPercent() > 0){
            description += context.getString(R.string.dp_label)+" "+ Helpers.digitToString(paymentSchema.getDpPercent())+"%, ";
        }
        description += context.getString(R.string.installment_label)+" "+paymentSchema.getInstallmentNumber()+"x";
        textViewDescription.setText(description);
        return view;
    }
}
