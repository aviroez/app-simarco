package com.ppproperti.simarco.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ppproperti.simarco.entities.LocationSchemaDetail;

import java.util.ArrayList;
import java.util.List;

public class LocationSchemaAdapter extends BaseAdapter {
    private Context context;
    private List<LocationSchemaDetail> list = new ArrayList<>();

    public LocationSchemaAdapter(Context context, List<LocationSchemaDetail> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public LocationSchemaDetail getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return list.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LocationSchemaDetail location = list.get(i);
        View layout = new LinearLayout(context);
//        layout.add
        TextView dummyTextView = new TextView(context);
        dummyTextView.setText(location.getX()+","+location.getY());
        return dummyTextView;
    }
}
