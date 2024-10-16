package com.ppproperti.simarco.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.adapters.SpinnerApartmentAdapter;
import com.ppproperti.simarco.entities.Apartment;
import com.ppproperti.simarco.entities.Event;
import com.ppproperti.simarco.interfaces.ActivityService;
import com.ppproperti.simarco.interfaces.ApartmentService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.responses.ResponseApartmentList;
import com.ppproperti.simarco.responses.ResponseEvent;
import com.ppproperti.simarco.utils.Constants;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventAddFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventAddFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final Calendar startCalendar = Calendar.getInstance();
    private final Calendar endCalendar = Calendar.getInstance();

    private OnFragmentInteractionListener mListener;
    private TextInputEditText textEventName;
    private TextInputEditText textStartDate;
    private TextInputEditText textEndDate;
    private TextInputEditText textAddressEvent;
    private TextInputEditText textDescriptionEvent;
    private Context context;
    private FragmentActivity activity;
    private BootstrapButton buttonBack;
    private BootstrapButton buttonAdd;
    private String name;
    private String address;
    private String description;
    private String startDateString;
    private String endDateString;

    SimpleDateFormat sdf;
    private DatePickerDialog.OnDateSetListener startDateListener;
    private DatePickerDialog.OnDateSetListener endDateListener;
    private Retrofit retrofit;
    private ActivityService activityService;
    private double latitude;
    private double longitude;
    private Spinner spinnerApartment;
    private ApartmentService apartmentService;
    private List<Apartment> listApartment = new ArrayList<>();
    private Apartment apartment = new Apartment();
    private CustomPreferences customPreferences;

    public EventAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventAddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventAddFragment newInstance(String param1, String param2) {
        EventAddFragment fragment = new EventAddFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        context = getContext();
        activity = getActivity();

        retrofit = Helpers.initRetrofit(context);
        activityService = retrofit.create(ActivityService.class);
        apartmentService = retrofit.create(ApartmentService.class);

        customPreferences = new CustomPreferences(context);

        sdf = new SimpleDateFormat(Constants.DATE_FORMAT_LOCAL_S, Constants.locale);

        startDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                startCalendar.set(Calendar.YEAR, year);
                startCalendar.set(Calendar.MONTH, monthOfYear);
                startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                textStartDate.setText(sdf.format(startCalendar.getTime()));
            }
        };

        endDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, monthOfYear);
                endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                textEndDate.setText(sdf.format(endCalendar.getTime()));
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_add, container, false);
        spinnerApartment = view.findViewById(R.id.spinner_apartment);
        textEventName = view.findViewById(R.id.text_event_name);
        textStartDate = view.findViewById(R.id.text_start_date);
        textEndDate = view.findViewById(R.id.text_end_date);
        textAddressEvent = view.findViewById(R.id.text_address_event);
        textDescriptionEvent = view.findViewById(R.id.text_description_event);
        buttonBack = view.findViewById(R.id.button_back);
        buttonAdd = view.findViewById(R.id.button_add);

        buttonBack.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        textStartDate.setOnClickListener(this);
        textEndDate.setOnClickListener(this);

        listApartment = customPreferences.getListApartment();
        listApartment.add(0, new Apartment(0, "[Pilih Apartment]"));
        SpinnerApartmentAdapter arrayAdapter = new SpinnerApartmentAdapter(context, listApartment);
        spinnerApartment.setAdapter(arrayAdapter);
        spinnerApartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                apartment = listApartment.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.add_event);
            ((HideShowIconInterface) activity).showBackIcon();
        }
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
    public void onClick(View view) {
        if (view.getId() == buttonBack.getId()){
            backAction(view);
        } else if (view.getId() == buttonAdd.getId()){
            addAction(view);
        } else if (view.getId() == textStartDate.getId()){
            startDateAction(view);
        } else if (view.getId() == textEndDate.getId()){
            endDateAction(view);
        }
    }

    private void endDateAction(View view) {
        new DatePickerDialog(context, endDateListener, endCalendar
                .get(Calendar.YEAR), endCalendar.get(Calendar.MONTH),
                endCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void startDateAction(View view) {
        new DatePickerDialog(context, startDateListener, startCalendar
                .get(Calendar.YEAR), startCalendar.get(Calendar.MONTH),
                startCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void addAction(View view) {
        if (validation()){
            SimpleDateFormat sqlDf = new SimpleDateFormat(Constants.DATE_FORMAT_SQL, Constants.locale);
            HashMap<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("description", description);
            map.put("address", address);
            if (!startDateString.isEmpty()){
                map.put("start_date", sqlDf.format(startCalendar.getTime()));
            }
            if (!endDateString.isEmpty()){
                map.put("end_date", sqlDf.format(endCalendar.getTime()));
            }
            if (latitude != 0 && longitude != 0){
                map.put("latitude", String.valueOf(latitude));
                map.put("longitude", String.valueOf(longitude));
            }
            if (apartment.getId() > 0) {
                map.put("apartment_id", String.valueOf(apartment.getId()));
            }
            Call<ResponseEvent> call = activityService.addEvent(map);
            call.enqueue(new Callback<ResponseEvent>() {
                @Override
                public void onResponse(Call<ResponseEvent> call, Response<ResponseEvent> response) {
                    if (response.isSuccessful()){
                        Bundle bundle = new Bundle();
//                        {"code":422,"messages":{"name":["name harus diisi."]}}
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                Event event = response.body().getData();
                                bundle.putParcelable("event", event);
                                bundle.putString("message", getString(R.string.event_is_added_successfully));
                                Helpers.moveFragment(context, new EventListFragment(), bundle);
                            } else if (response.body().getMessage().length() > 0) {
                                Helpers.showLongSnackbar(getView(), response.body().getMessage());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseEvent> call, Throwable t) {

                }
            });
        }
    }

    private boolean validation() {
        if (spinnerApartment !=  null){
            apartment = listApartment.get(spinnerApartment.getSelectedItemPosition());
        }
        name = textEventName.getText().toString();
        address = textAddressEvent.getText().toString();
        description = textDescriptionEvent.getText().toString();
        startDateString = textStartDate.getText().toString();
        endDateString = textEndDate.getText().toString();

        if (apartment.getId() <= 0) {
            Helpers.showLongSnackbar(getView(), getString(R.string.apartment_is_required));
            return false;
        } else if (name.isEmpty()){
            Helpers.showLongSnackbar(getView(), R.string.event_name_is_required);
            textEventName.requestFocus();
            return false;
        } else if (startDateString.isEmpty() && endDateString.isEmpty()){
            Helpers.showLongSnackbar(getView(), R.string.date_is_required);
            textStartDate.requestFocus();
            return false;
        } else if (startCalendar.getTimeInMillis() > endCalendar.getTimeInMillis()){
            Helpers.showLongSnackbar(getView(), R.string.start_sould_not_more_than_end_date);
            textStartDate.requestFocus();
            return false;
        }
        return true;
    }

    private void backAction(View view) {
        Helpers.moveFragment(context, new EventListFragment(), null);
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
}
