package com.ppproperti.simarco.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.Event;
import com.ppproperti.simarco.entities.EventActivityLog;
import com.ppproperti.simarco.entities.Image;
import com.ppproperti.simarco.fragments.EventListFragment.OnEventListFragmentInteractionListener;
import com.ppproperti.simarco.fragments.dummy.DummyContent.DummyItem;
import com.ppproperti.simarco.interfaces.ActivityService;
import com.ppproperti.simarco.responses.ResponseEventActivityLog;
import com.ppproperti.simarco.utils.Helpers;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnEventListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class EventActivityLogListRecyclerViewAdapter extends RecyclerView.Adapter<EventActivityLogListRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final List<EventActivityLog> list;
    private final OnEventListFragmentInteractionListener listener;
    private static OnItemClickListener onItemClickListener;
    private final Retrofit retrofit;
    private final ActivityService activityService;

    public EventActivityLogListRecyclerViewAdapter(Context context, List<EventActivityLog> list, OnEventListFragmentInteractionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        retrofit = Helpers.initRetrofit(context, true);
        activityService = retrofit.create(ActivityService.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_activity_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final EventActivityLog eventActivityLog = list.get(position);
        Event event = new Event();
        if (eventActivityLog != null){
            holder.eventActivityLog = eventActivityLog;

            holder.textActivityName.setVisibility(View.GONE);
            holder.textCreatedAt.setText(Helpers.reformatDate(eventActivityLog.getCreatedAt(), null, null));

            if (eventActivityLog.getEventActivity() != null){
                if (eventActivityLog.getEventActivity().getEvent() != null){
                    event = eventActivityLog.getEventActivity().getEvent();
                }
                if (event != null){
                    event  = eventActivityLog.getEvent();
                }

                if (event != null){
                    holder.textAddressName.setText(event.getAddress());
                    holder.textDescription.setText(event.getDescription());
                }
            }

            if (eventActivityLog.getActivity() != null){
                holder.textActivityName.setVisibility(View.VISIBLE);
                holder.textActivityName.setText(eventActivityLog.getActivity().getName());
            }

            if (eventActivityLog.getLatitude() != 0 && eventActivityLog.getLongitude() != 0 && eventActivityLog.getLocation() == null){
                String addressLocation = Helpers.getCompleteAddressString(context, eventActivityLog.getLatitude(), eventActivityLog.getLongitude());
                holder.textAddressName.setVisibility(View.VISIBLE);
                holder.textAddressName.setText(addressLocation);
                if (addressLocation != null){
                    HashMap<String, String> map = new HashMap<>();
                    map.put("location", addressLocation);
                    activityService.updateEventActivityLog(eventActivityLog.getId(), map).enqueue(new Callback<ResponseEventActivityLog>() {
                        @Override
                        public void onResponse(Call<ResponseEventActivityLog> call, Response<ResponseEventActivityLog> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseEventActivityLog> call, Throwable t) {

                        }
                    });
                }
            } else if (eventActivityLog.getLocation() != null){
                holder.textAddressName.setVisibility(View.VISIBLE);
                holder.textAddressName.setText(eventActivityLog.getLocation());
            }

            if (eventActivityLog.getImages().size() > 0){
                for (Image image: eventActivityLog.getImages()) {
                    String url = Helpers.imageUrl(context, image.getUrl(), holder.imageView);
                    Log.d("eventActivityLog", url);
//                    Picasso.get().load(url).into(holder.imageView);
//                    Glide.with(context).load(url).into();
                    break;
                }
            }
        }
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
        public final TextView textActivityName;
        public final TextView textAddressName;
        public final TextView textDescription;
        public final AwesomeTextView textCreatedAt;
        public final ImageView imageView;
        public EventActivityLog eventActivityLog;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            textActivityName = (TextView) view.findViewById(R.id.text_activity_name);
            textAddressName = (TextView) view.findViewById(R.id.text_address_name);
            textDescription = (TextView) view.findViewById(R.id.text_description);
            textCreatedAt = (AwesomeTextView) view.findViewById(R.id.text_created_at);
            imageView = (ImageView) view.findViewById(R.id.image_view);

            view.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " ";
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(view, eventActivityLog, getAdapterPosition());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        EventActivityLogListRecyclerViewAdapter.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, EventActivityLog eventActivityLog, int position);
    }
}
