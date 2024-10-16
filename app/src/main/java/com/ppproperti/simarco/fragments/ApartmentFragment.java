package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ppproperti.simarco.R;
import com.ppproperti.simarco.adapters.ApartmentRecyclerViewAdapter;
import com.ppproperti.simarco.entities.Apartment;
import com.ppproperti.simarco.interfaces.ApartmentService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.responses.ResponseApartmentList;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ApartmentFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private List<Apartment> listApartment = new ArrayList<>();
    private OnListFragmentInteractionListener listener;
    private ApartmentRecyclerViewAdapter apartmentAdapter;
    private RecyclerView recyclerView;
    private Context context;
    private String message;
    private FragmentActivity activity;
    private CustomPreferences customPreferences;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ApartmentFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ApartmentFragment newInstance(int columnCount) {
        ApartmentFragment fragment = new ApartmentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString("message");
        }

        context = getContext();
        activity = getActivity();
        customPreferences = new CustomPreferences(context);

        if (message != null && getView() != null){
            Helpers.showLongSnackbar(getView(), message);
        }

        listApartment = customPreferences.getListApartment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apartment_list, container, false);

        context = view.getContext();
        recyclerView = view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.choose_apartment);
            ((HideShowIconInterface) activity).showHamburgerIcon();
        }

        Retrofit retrofit = Helpers.initRetrofit(context);

        ApartmentService service = retrofit.create(ApartmentService.class);
        Map<String, String> params = new HashMap<>();
        params.put("with", "images");
        Call<ResponseApartmentList> call = service.listApartments(params);
        call.enqueue(new Callback<ResponseApartmentList>() {
            @Override
            public void onResponse(Call<ResponseApartmentList> call, Response<ResponseApartmentList> response) {
                if(response.isSuccessful()){
                    if (response.body() != null && response.body().getData() != null){
                        listApartment = response.body().getData();

                        customPreferences.setListApartment(listApartment);

                        apartmentAdapter = new ApartmentRecyclerViewAdapter(context, listApartment, listener);

                        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                        recyclerView.setAdapter(apartmentAdapter);

                        apartmentAdapter.notifyItemChanged(listApartment.size() - 1);
                        recyclerView.smoothScrollToPosition(listApartment.size() - 1);

                        Log.d(ApartmentFragment.class.getSimpleName(), "onResponse:isSuccessful:"+listApartment.size());
                    }
                } else {
                    Log.d(ApartmentFragment.class.getSimpleName(), "onResponse:"+response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseApartmentList> call, Throwable t) {

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            listener = (OnListFragmentInteractionListener) context;
        } else {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Apartment apartment);
    }
}
