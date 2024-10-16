package com.ppproperti.simarco.fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.adapters.EventActivityLogListRecyclerViewAdapter;
import com.ppproperti.simarco.entities.Event;
import com.ppproperti.simarco.entities.EventActivityLog;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.interfaces.ActivityService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.responses.ResponseEventActivityLogList;
import com.ppproperti.simarco.utils.Constants;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventActivityLogListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventActivityLogListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventActivityLogListFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String TAG = EventActivityLogListFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String message;

    private OnFragmentInteractionListener mListener;
    private Context context;
    private FragmentActivity currentActivity;
    private EventActivityLog eventActivityLog;
    private Retrofit retrofit;
    private ActivityService activityService;
    private List<EventActivityLog> listEventActivityLog = new ArrayList<>();
    private User user = new User();
    private RecyclerView recyclerView;
    private EventActivityLogListRecyclerViewAdapter adapter;
    private Event event;
    private BootstrapButton buttonAdd;
    private Date now;
    private boolean showButton = false;
    private ProgressBar progressBar;

    public EventActivityLogListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param message Parameter 1.
     * @return A new instance of fragment EventActivityLogListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventActivityLogListFragment newInstance(String message, EventActivityLog eventActivityLog) {
        EventActivityLogListFragment fragment = new EventActivityLogListFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putParcelable("event_activity_log", eventActivityLog);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString("message");
            eventActivityLog = getArguments().getParcelable("event_activity_log");
            event = getArguments().getParcelable("event");
        }

        context = getContext();
        currentActivity = getActivity();

        now = new Date();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_activity_log_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        buttonAdd = view.findViewById(R.id.button_add);
        progressBar = view.findViewById(R.id.progress_bar);

        buttonAdd.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
//        Date startDate = Helpers.parseDate(event.getStartDate());
//        Date endDate = Helpers.parseDate(event.getEndDate());


        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_SQL);
        Date startDate = null;
        Date endDate = null;
        try {
            calendar.setTime(sdf.parse(event.getStartDate()));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            startDate = calendar.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            calendar.setTime(sdf.parse(event.getEndDate()));
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            endDate = calendar.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (startDate != null && endDate != null){
            showButton = (now.equals(startDate) || now.after(startDate)) && (now.equals(endDate) || now.before(endDate));
        } else if (event.getStartDate() != null){
            showButton = now.equals(startDate) || now.after(startDate);
        } else if (endDate != null){
            showButton = now.equals(endDate) || now.before(endDate);
        }

        if (!showButton){
            buttonAdd.setVisibility(View.GONE);
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (currentActivity != null){
            if (event != null) {
                currentActivity.setTitle(event.getName());
            } else {
                currentActivity.setTitle(R.string.list_activity_log);
            }
            ((HideShowIconInterface) currentActivity).showBackIcon();
        }

        checkApplicationPermissions();

        retrofit = Helpers.initRetrofit(context);
        activityService = retrofit.create(ActivityService.class);

        CustomPreferences customPreferences = new CustomPreferences(context);
        user = customPreferences.getUser();

        initData();

        if (message != null && message.length() > 0){
            Helpers.showLongSnackbar(getView(), message);
        }
    }

    private void initData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("marketing_id", String.valueOf(user.getId()));
        map.put("order_by_desc", "created_at");

        if (eventActivityLog != null){
            if (eventActivityLog.getEventId() > 0){
                map.put("event_id", String.valueOf(eventActivityLog.getEventId()));

                if (eventActivityLog.getEvent() != null) {
                    map.put("apartment_id", String.valueOf(eventActivityLog.getEvent()));
                }
            }
        } else if (event != null){
            map.put("event_id", String.valueOf(event.getId()));
            map.put("apartment_id", String.valueOf(event.getApartmentId()));
        }
        Call<ResponseEventActivityLogList> call = activityService.listEventActivityLog(map);
        call.enqueue(new Callback<ResponseEventActivityLogList>() {
            @Override
            public void onResponse(Call<ResponseEventActivityLogList> call, Response<ResponseEventActivityLogList> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getData() != null){
                        listEventActivityLog = response.body().getData();
                        loadListEvent();
                    }
                }

                SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME_FORMAT_SQL, Constants.locale);
                if (listEventActivityLog.size() > 0){
                    boolean added = false;
                    for (EventActivityLog log: listEventActivityLog){
                        try {
                            Date createdDate = sdf.parse(log.getCreatedAt());
                            if (DateUtils.isSameDay(createdDate, now)){
                                added = true;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!added && showButton) {
                        Helpers.showLongSnackbar(getView(), R.string.event_Activity_log_id_not_added);
                    }
                } else if (showButton){
                    Helpers.showLongSnackbar(getView(), R.string.event_Activity_log_id_not_added);
                } else {
                    try {
                        sdf = new SimpleDateFormat(Constants.DATE_FORMAT_SQL, Constants.locale);
                        Date startDate = null;
                        Date endDate = null;
                        if (event.getStartDate() != null){
                            startDate = sdf.parse(event.getStartDate());
                        }
                        if (event.getEndDate() != null){
                            endDate = sdf.parse(event.getEndDate());
                        }

                        if (startDate != null && now.before(startDate)){
                            Helpers.showLongSnackbar(getView(), R.string.event_is_not_yet_started);
                        } else if (endDate != null && now.after(endDate)){
                            Helpers.showLongSnackbar(getView(), R.string.event_is_already_finished);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseEventActivityLogList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadListEvent(){
        adapter = new EventActivityLogListRecyclerViewAdapter(context, listEventActivityLog, null);
        adapter.setOnItemClickListener(new EventActivityLogListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, EventActivityLog eventActivityLog, int position) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("event_activity_log", eventActivityLog);
                Helpers.moveFragment(context, new EventActivityLogDetailFragment(), bundle);
            }
        });

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == buttonAdd.getId()){
            addEventActivityLogAction(view);
        }
    }

    private void addEventActivityLogAction(View view) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("event", event);
        Helpers.moveFragment(context, new ActivityListFragment(), bundle);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void checkApplicationPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(currentActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 104);
            }
        }
    }
}
