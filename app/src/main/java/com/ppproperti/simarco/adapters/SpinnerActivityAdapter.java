package com.ppproperti.simarco.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.Activity;

import java.util.ArrayList;
import java.util.List;

public class SpinnerActivityAdapter extends BaseAdapter {
    private Context context;
    private List<Activity> list = new ArrayList<>();

    public SpinnerActivityAdapter(Context context, List<Activity> list) {
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
        Activity event = list.get(i);
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_customer_spinner, null);

        TextView textViewName = (TextView) view.findViewById(R.id.text_name);
        TextView textViewDescription = (TextView) view.findViewById(R.id.text_description);
        View separator = view.findViewById(R.id.separator);

        textViewName.setText(event.getName());
        if (event.getDescription() != null){
            textViewDescription.setText(event.getDescription());
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
