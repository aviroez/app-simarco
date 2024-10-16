package com.ppproperti.simarco.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.Event;
import com.ppproperti.simarco.utils.Constants;
import com.ppproperti.simarco.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

public class SpinnerEventAdapter extends BaseAdapter {
    private Context context;
    private List<Event> list = new ArrayList<>();

    public SpinnerEventAdapter(Context context, List<Event> list) {
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
        if (list.size() > i) return list.get(i).getId();
        return 0;
    }

    public void updateItems(List<Event> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Event event = list.get(i);
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_customer_spinner, null);

        TextView textViewName = (TextView) view.findViewById(R.id.text_name);
        TextView textViewDescription = (TextView) view.findViewById(R.id.text_description);
        View separator = (View) view.findViewById(R.id.separator);

        textViewName.setText(event.getName());
        if (event.getStartDate() != null && event.getEndDate() != null){
            textViewDescription.setText(Helpers.reformatDate(event.getStartDate(), Constants.DATE_FORMAT_SQL, Constants.DATE_FORMAT_LOCAL) + " - " +Helpers.reformatDate(event.getEndDate(), Constants.DATE_FORMAT_SQL, Constants.DATE_FORMAT_LOCAL));
        } else if (event.getStartDate() != null){
            textViewDescription.setText(Helpers.reformatDate(event.getStartDate(), Constants.DATE_FORMAT_SQL, Constants.DATE_FORMAT_LOCAL));
        } else if (event.getEndDate() != null){
            textViewDescription.setText(Helpers.reformatDate(event.getEndDate(), Constants.DATE_FORMAT_SQL, Constants.DATE_FORMAT_LOCAL));
        } else if (event.getDescription() != null){
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
