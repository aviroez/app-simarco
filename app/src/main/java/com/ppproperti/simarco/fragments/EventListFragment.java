package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.adapters.EventListRecyclerViewAdapter;
import com.ppproperti.simarco.entities.Apartment;
import com.ppproperti.simarco.entities.Event;
import com.ppproperti.simarco.interfaces.ActivityService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.responses.ResponseEventList;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventListFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = EventListFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Context context;
    private FragmentActivity activity;
    private RecyclerView recyclerView;
    private BootstrapButton buttonAdd;
    private Retrofit retrofit;
    private ActivityService activityService;
    private Call<ResponseEventList> call;
    private List<Event> listEvent = new ArrayList<>();
    private EventListRecyclerViewAdapter adapter;
    private Event event;
    private String message;
    private ProgressBar progressBar;
    private CustomPreferences customPreferences;
    private List<Apartment> listApartment = new ArrayList<>();
    private SearchView searchView;
    private String search = "";

    public EventListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventListFragment newInstance(String param1, String param2) {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
            message = getArguments().getString("message");
        }

        context = getContext();
        activity = getActivity();
        customPreferences = new CustomPreferences(context);
        listApartment = customPreferences.getListApartment();

        if (message != null){
            Helpers.showLongSnackbar(getView(), message);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        buttonAdd = view.findViewById(R.id.button_add);
        progressBar = view.findViewById(R.id.progress_bar);

        buttonAdd.setOnClickListener(this);

        if (activity != null){
            searchView = activity.findViewById(R.id.search_view);
            searchView.setVisibility(View.VISIBLE);
            searchView.setQuery("", false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();
                    search = query;

                    initList(search);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.trim().equals("") && search.length() > 0){
                        search = "";
                        initList(search);
                    }

                    return false;
                }
            });
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

        if(activity != null){
            activity.setTitle(R.string.choose_or_add_event);
            ((HideShowIconInterface) activity).showHamburgerIcon();
        }

        retrofit = Helpers.initRetrofit(context);
        activityService = retrofit.create(ActivityService.class);

        initList(null);
    }

    private void initList(String search) {
//        loadListEvent();
        HashMap<String, String> map = new HashMap<>();
        map.put("order_by_desc[0]", "start_date");
        map.put("order_by_desc[1]", "end_date");
        map.put("active", "1");
        if (search != null) map.put("search", search);
        call = activityService.listEvent(map);
        call.enqueue(new Callback<ResponseEventList>() {
            @Override
            public void onResponse(Call<ResponseEventList> call, Response<ResponseEventList> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getData() != null){
                        listEvent = response.body().getData();
//                        recyclerView.invalidate();
                        loadListEvent();

                        if (listEvent.size() <= 0){
                            Helpers.showLongSnackbar(getView(), R.string.event_list_empty);
                        }
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseEventList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Helpers.showLongSnackbar(getView(), t.getMessage());
            }
        });
    }

    private void loadListEvent(){
        adapter = new EventListRecyclerViewAdapter(context, listEvent, null);
        adapter.setOnItemClickListener(new EventListRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Event event, int position) {
                searchView.setVisibility(View.GONE);
                Bundle bundle = new Bundle();
                bundle.putParcelable("event", event);
                Helpers.moveFragment(context, new EventActivityLogListFragment(), bundle);
            }
        });

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_add){
            searchView.setVisibility(View.GONE);
            Helpers.moveFragment(context, new EventAddFragment(), null);
        }
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
    public interface OnEventListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Event event);
    }
}
