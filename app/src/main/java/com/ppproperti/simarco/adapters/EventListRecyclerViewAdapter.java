package com.ppproperti.simarco.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.view.BootstrapTextView;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.Event;
import com.ppproperti.simarco.fragments.EventListFragment.OnEventListFragmentInteractionListener;
import com.ppproperti.simarco.fragments.dummy.DummyContent.DummyItem;
import com.ppproperti.simarco.utils.Helpers;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnEventListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class EventListRecyclerViewAdapter extends RecyclerView.Adapter<EventListRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final List<Event> list;
    private final OnEventListFragmentInteractionListener listener;
    private static OnItemClickListener onItemClickListener;

    public EventListRecyclerViewAdapter(Context context, List<Event> list, OnEventListFragmentInteractionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Event event = list.get(position);
        holder.event = event;
        BootstrapText bootstrapText = new BootstrapText.Builder(context)
                .addText(event.getName())
                .build();

        holder.textTitle.setBootstrapText(bootstrapText);
        holder.textActivityName.setVisibility(View.GONE);

        if (event.getStartDate() != null){
            holder.textStartDate.setText(Helpers.formatDate(event.getStartDate()));
        } else {
            holder.layoutStartDate.setVisibility(View.GONE);
        }

        if (event.getEndDate() != null){
            holder.textEndDate.setText(Helpers.formatDate(event.getEndDate()));
        } else {
            holder.layoutEndDate.setVisibility(View.GONE);
        }

        holder.textAddressName.setText(event.getAddress());
        holder.textDescription.setText(event.getDescription());

//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != listener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    listener.onListFragmentInteraction(holder.event);
//                }
//            }
//        });
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final View view;
        public final BootstrapTextView textTitle;
        public final TextView textActivityName;
        public final TextView textAddressName;
        public final TextView textDescription;
        public final View layoutStartDate;
        public final View layoutEndDate;
        public final TextView textStartDate;
        public final TextView textEndDate;
        public Event event;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            textTitle = (BootstrapTextView) view.findViewById(R.id.text_title_name);
            textActivityName = (TextView) view.findViewById(R.id.text_activity_name);
            textAddressName = (TextView) view.findViewById(R.id.text_address_name);
            textDescription = (TextView) view.findViewById(R.id.text_description);
            layoutStartDate = view.findViewById(R.id.layout_start_date);
            layoutEndDate = view.findViewById(R.id.layout_end_date);
            textStartDate = (TextView) view.findViewById(R.id.text_start_date);
            textEndDate = (TextView) view.findViewById(R.id.text_end_date);

            view.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " ";
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(view, event, getAdapterPosition());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        EventListRecyclerViewAdapter.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Event event, int position);
    }
}
