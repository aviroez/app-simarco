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
import com.ppproperti.simarco.adapters.ApartmentTowerListRecyclerViewAdapter;
import com.ppproperti.simarco.entities.Apartment;
import com.ppproperti.simarco.entities.ApartmentTower;
import com.ppproperti.simarco.interfaces.ApartmentService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.responses.ResponseApartmentTowerList;
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
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ApartmentTowerFragment extends Fragment {

    private static final String TAG = ApartmentTowerFragment.class.getSimpleName();
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private List<ApartmentTower> listApartmentTower = new ArrayList<>();
    private RecyclerView recyclerView;
    private Context context;
    private Retrofit retrofit;
    private Apartment apartment;
    private OnListFragmentInteractionListener listener;
    private ApartmentTowerListRecyclerViewAdapter apartmentTowerAdapter;
    private CustomPreferences customPreferences;
    private String json;
    private FragmentActivity activity;
    private List<Apartment> listApartment = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ApartmentTowerFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ApartmentTowerFragment newInstance(int columnCount) {
        ApartmentTowerFragment fragment = new ApartmentTowerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            apartment = getArguments().getParcelable("apartment");
        }
        context = getContext();
        activity = getActivity();
        customPreferences = new CustomPreferences(context);
        listApartment = customPreferences.getListApartment();

        Log.d(TAG, "onCreate:"+ json);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apartmenttower_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new ApartmentTowerListRecyclerViewAdapter(context, listApartmentTower, listener));

        return view;
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

    @Override
    public void onStart() {
        super.onStart();

        activity.setTitle(R.string.choose_apartment_tower);
        retrofit = Helpers.initRetrofit(context);

        ApartmentService service = retrofit.create(ApartmentService.class);
        HashMap<String, String> map = new HashMap<>();
        if (apartment != null){
            map.put("apartment_id", String.valueOf(apartment.getId()));
            ((HideShowIconInterface) activity).showBackIcon();
        } else {
            ((HideShowIconInterface) activity).showHamburgerIcon();
            if (listApartment.size() == 1) {
                apartment = listApartment.get(0);
                map.put("apartment_id", String.valueOf(apartment.getId()));
            }
        }
        map.put("with[0]", "apartment");
        Call<ResponseApartmentTowerList> call = service.listApartmentTowers(map);
        call.enqueue(new Callback<ResponseApartmentTowerList>() {
            @Override
            public void onResponse(Call<ResponseApartmentTowerList> call, Response<ResponseApartmentTowerList> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null){
                    listApartmentTower = response.body().getData();

                    recyclerView.setLayoutManager(new LinearLayoutManager(context));

                    apartmentTowerAdapter = new ApartmentTowerListRecyclerViewAdapter(context, listApartmentTower, listener);
                    recyclerView.setAdapter(apartmentTowerAdapter);
                }
            }

            @Override
            public void onFailure(Call<ResponseApartmentTowerList> call, Throwable t) {

            }
        });
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
        void onListFragmentInteraction(ApartmentTower apartmentTower);
    }
}
