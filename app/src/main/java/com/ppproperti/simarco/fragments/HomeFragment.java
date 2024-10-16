package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.gson.Gson;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.activities.MainActivity;
import com.ppproperti.simarco.adapters.ApartmentHomeRecyclerViewAdapter;
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
public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private List<Apartment> listApartment = new ArrayList<>();
    private OnListFragmentInteractionListener listener;
    private ApartmentHomeRecyclerViewAdapter apartmentAdapter;
    private RecyclerView recyclerView;
    private Context context;
    private String message;
    private BootstrapButton buttonOrder;
    private Apartment apartment;
    private ImageView imageViewCustomer;
    private ImageView imageViewEvent;
    private CustomPreferences customPreferences;
    private FragmentActivity activity;
    private Retrofit retrofit;
    private ApartmentService apartmentService;
    private LinearLayoutManager horizontalLayoutManager;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HomeFragment newInstance(int columnCount) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
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
        customPreferences = new CustomPreferences(context, "user");

        listApartment = customPreferences.getListApartment();
        apartment = customPreferences.getApartment();

        if (apartment != null){
            Log.d(TAG, "apartment:"+new Gson().toJson(apartment));
        }

        retrofit = Helpers.initRetrofit(context);
        apartmentService = retrofit.create(ApartmentService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.list);
        buttonOrder = (BootstrapButton) view.findViewById(R.id.button_order);
        imageViewCustomer = (ImageView) view.findViewById(R.id.image_view_customer);
        imageViewEvent = (ImageView) view.findViewById(R.id.image_view_event);

        imageViewCustomer.setOnClickListener(this);
        imageViewEvent.setOnClickListener(this);
        buttonOrder.setOnClickListener(this);
        updateAdapter();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.app_name);
            ((HideShowIconInterface) activity).showHamburgerIcon();
        }

        if (apartment != null){
            listApartment = new ArrayList<>();
            listApartment.add(apartment);

            updateAdapter();
        } else if (listApartment.size() > 0){
            if (listApartment.size() == 1){
                apartment = listApartment.get(0);
                customPreferences.setApartment(apartment);
            }
            updateAdapter();
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("with", "images");
            Call<ResponseApartmentList> call = apartmentService.listApartments(params);
            call.enqueue(new Callback<ResponseApartmentList>() {
                @Override
                public void onResponse(Call<ResponseApartmentList> call, Response<ResponseApartmentList> response) {
                    if(response.isSuccessful()){
                        if (response.body() != null && response.body().getData() != null){
                            listApartment = response.body().getData();
                            customPreferences.setListApartment(listApartment);

                            if (listApartment.size() == 1){
                                apartment = listApartment.get(0);
                                customPreferences.setApartment(apartment);
                            }
                            updateAdapter();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseApartmentList> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (message != null){
            Helpers.showLongSnackbar(getView(), message);
        }
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
    public void onClick(View view) {
        if (view.getId() == R.id.image_view_customer){
            Helpers.moveFragment(context, new CustomerListFragment(), null);
        } else if (view.getId() == R.id.image_view_event){
//            Bundle bundle = new Bundle();
//            if (apartment != null){
//                bundle.putParcelable("apartment", apartment);
//            }
//            Helpers.moveFragment(context, new ApartmentTowerFragment(), bundle);
            Helpers.moveFragment(context, new EventListFragment(), null);
        } else if (view.getId() == R.id.button_order){
            if (apartment != null){
                moveToApartmentTower(apartment);
            } else {
                loadApartment();
            }
        }
    }

    private void moveToApartmentTower(Apartment apartment) {
        Bundle bundle = new Bundle();
        if (apartment != null){
            bundle.putParcelable("apartment", apartment);
        }
        Helpers.moveFragment(context, new ApartmentTowerFragment(), bundle);
    }

    private void loadApartment() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(R.mipmap.ic_simarco);
        builderSingle.setTitle("Pilih apartment:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);
        for (Apartment apartment: listApartment) {
            arrayAdapter.add(apartment.getName());
        }

        builderSingle.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                String strName = arrayAdapter.getItem(position);
                Apartment apartment = listApartment.get(position);
                customPreferences.setApartment(apartment);
                moveToApartmentTower(apartment);
                dialog.dismiss();
            }
        });
        builderSingle.show();
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

    private void updateAdapter(){
        apartmentAdapter = new ApartmentHomeRecyclerViewAdapter(context, listApartment, listener);

        horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(apartmentAdapter);
    }

    public void scrollTo(int position){
        recyclerView.getLayoutManager().scrollToPosition(position);
    }

    OnHeadlineSelectedListener callback;

    public void setOnHeadlineSelectedListener(OnHeadlineSelectedListener callback) {
        this.callback = callback;
    }

    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(Apartment apartment);
    }
}
