package com.ppproperti.simarco.fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.adapters.SpinnerActivityAdapter;
import com.ppproperti.simarco.adapters.SpinnerEventAdapter;
import com.ppproperti.simarco.entities.Activity;
import com.ppproperti.simarco.entities.Event;
import com.ppproperti.simarco.interfaces.ActivityService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.responses.ResponseActivityList;
import com.ppproperti.simarco.responses.ResponseEventList;
import com.ppproperti.simarco.utils.Constants;
import com.ppproperti.simarco.utils.GPSTracker;
import com.ppproperti.simarco.utils.Helpers;

import java.io.IOException;
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
 * {@link EventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFragment extends Fragment implements LocationListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = EventFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Context context;
    private Retrofit retrofit;
    private ActivityService service;
    private List<Activity> listActivity = new ArrayList<>();
    private List<Event> listEvent = new ArrayList<>();
    private Spinner spinnerActivity;
    private Spinner spinnerEvent;
    private android.app.Activity activity;
    private GPSTracker gpsTracker;
    private double latitude;
    private double longitude;
    private List<Address> listAddress;
    private Geocoder geocoder;
    private LocationManager locationManager;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private boolean canGetLocation;
    private Location location;
    private Address address;
    private EditText textAddress;
    private BootstrapEditText textActivityName;
    private BootstrapEditText textEventName;
    private BootstrapEditText textStartDate;
    private BootstrapEditText textEndDate;
    private BootstrapButton buttonBack;
    private BootstrapButton buttonNext;
    private long activityId;
    private long eventId;
    private String activityName;
    private String eventName;
    private String startDate;
    private String endDate;
    private String addressActivity;
    private View layoutEventName;
    private View layoutActivityName;

    public EventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventFragment newInstance(String param1, String param2) {
        EventFragment fragment = new EventFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        spinnerActivity = view.findViewById(R.id.spinner_activity);
        spinnerEvent = view.findViewById(R.id.spinner_event);
        textActivityName = view.findViewById(R.id.text_activity_name);
        textEventName = view.findViewById(R.id.text_event_name);
        textStartDate = view.findViewById(R.id.text_start_date);
        textEndDate = view.findViewById(R.id.text_end_date);
        textAddress = view.findViewById(R.id.text_address);
        buttonBack = view.findViewById(R.id.button_back);
        buttonNext = view.findViewById(R.id.button_next);
        layoutEventName = view.findViewById(R.id.layout_event_name);
        layoutActivityName = view.findViewById(R.id.layout_activity_name);

        layoutEventName.setVisibility(View.GONE);
        layoutActivityName.setVisibility(View.GONE);

        buttonBack.setOnClickListener(this);
        buttonNext.setOnClickListener(this);

        spinnerActivity.setOnItemSelectedListener(this);
        spinnerEvent.setOnItemSelectedListener(this);

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

        if (activity != null){
            activity.setTitle(R.string.list_event);
            ((HideShowIconInterface) activity).showBackIcon();
        }

        checkApplicationPermissions();

        geocoder = new Geocoder(context);

        retrofit = Helpers.initRetrofit(context);
        service = retrofit.create(ActivityService.class);

//        checkGps();
        initData();
    }

    private void checkGps() {
        gpsTracker = new GPSTracker(context);

        // Check if GPS enabled
        if (gpsTracker.canGetLocation()) {
            getMyLocation(gpsTracker.getLocation());
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gpsTracker.showSettingsAlert();
        }
    }

    private void getMyLocation(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            try {
                listAddress = geocoder.getFromLocation(latitude, longitude, 10);

                if (listAddress.size() > 0){
                    for (Address add: listAddress) {
                        if (!add.getAddressLine(0).toLowerCase().contains("unnamed")){
                            address = add;
                            break;
                        }
                        Log.d(TAG, "getMyLocation:addressLine:"+address.getAddressLine(0));
                        if (address.getAdminArea() != null){
                            Log.d(TAG, "getMyLocation:adminArea"+address.getAdminArea());
                        }
                        if (address.getSubAdminArea() != null){
                            Log.d(TAG, "getMyLocation:getSubAdminArea"+address.getSubAdminArea());
                        }
                        if (address.getSubLocality() != null){
                            Log.d(TAG, "getMyLocation:getSubLocality"+address.getSubLocality());
                        }
                        if (address.getSubThoroughfare() != null){
                            Log.d(TAG, "getMyLocation:getSubThoroughfare"+address.getSubThoroughfare());
                        }
                        if (address.getThoroughfare() != null){
                            Log.d(TAG, "getMyLocation:getThoroughfare"+address.getThoroughfare());
                        }
                    }
                }

                if (address != null){
                    textAddress.setText(address.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // \n is for new line
            Helpers.showLongSnackbar(getView(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude);
        }
        Log.d(TAG, "getMyLocation:"+location.getLatitude()+","+location.getLongitude());
    }

    private void initData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("order_by_desc[0]", "start_date");
        map.put("order_by_desc[1]", "end_date");
        map.put("active", "1");
        Call<ResponseEventList> callEvent = service.listEvent(map);
        callEvent.enqueue(new Callback<ResponseEventList>() {
            @Override
            public void onResponse(Call<ResponseEventList> call, Response<ResponseEventList> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getData() != null) {
                        listEvent = response.body().getData();
                    }
                }
                listEvent.add(0, new Event(0, "[Pilih Event]"));
                listEvent.add(new Event(-1, "[Tambah Event Baru]"));

                SpinnerEventAdapter arrayAdapter = new SpinnerEventAdapter(context, listEvent);
                spinnerEvent.setAdapter(arrayAdapter);
            }

            @Override
            public void onFailure(Call<ResponseEventList> call, Throwable t) {

            }
        });

        Call<ResponseActivityList> callActivity = service.listActivity(map);
        callActivity.enqueue(new Callback<ResponseActivityList>() {
            @Override
            public void onResponse(Call<ResponseActivityList> call, Response<ResponseActivityList> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getData() != null) {
                        listActivity = response.body().getData();
                    }
                }
                listActivity.add(0, new Activity(0, "[Pilih Aktivitas]", null));
                listActivity.add(new Activity(-1, "[Tambah Aktivitas Baru]", null));

                SpinnerActivityAdapter arrayAdapter = new SpinnerActivityAdapter(context, listActivity);
                spinnerActivity.setAdapter(arrayAdapter);
            }

            @Override
            public void onFailure(Call<ResponseActivityList> call, Throwable t) {

            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        getMyLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_back){
            backAction(view);
        } else if (view.getId() == R.id.button_next){
            nextAction(view);
        }
    }

    private void backAction(View view) {
        Helpers.moveFragment(context, new HomeFragment(), null);
    }

    private void nextAction(View view) {
        if (validate()){
            Activity activity = listActivity.get(spinnerActivity.getSelectedItemPosition());
            Event event = listEvent.get(spinnerEvent.getSelectedItemPosition());
            Bundle bundle = new Bundle();
            bundle.putParcelable("activity", activity);
            bundle.putParcelable("event", event);
            bundle.putString("activity_name", activityName);
            bundle.putString("event_name", eventName);
            bundle.putString("start_date", startDate);
            bundle.putString("end_date", endDate);
            bundle.putString("address", addressActivity);
            Helpers.moveFragment(context, new CaptureEventFragment(), bundle);
        }
    }

    private boolean validate() {
        activityId = spinnerActivity.getSelectedItemId();
        eventId = spinnerEvent.getSelectedItemId();

        activityName = textActivityName.getText().toString();
        eventName = textEventName.getText().toString();

        startDate = textStartDate.getText().toString();
        endDate = textEndDate.getText().toString();
        addressActivity = textAddress.getText().toString();

        if (activityId == 0){
            Helpers.showLongSnackbar(getView(), R.string.activity_is_required);
            spinnerActivity.requestFocus();
            return false;
        } else if (activityId == -1 && activityName.length() <= 0){
            Helpers.showLongSnackbar(getView(), R.string.activity_is_required);
            textActivityName.requestFocus();
            return false;
        } else if (eventId == 0){
            Helpers.showLongSnackbar(getView(), R.string.event_is_required);
            spinnerEvent.requestFocus();
            return false;
        } else if (eventId == -1 && eventName.length() <= 0){
            Helpers.showLongSnackbar(getView(), R.string.event_is_required);
            textEventName.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (view.getId() == R.id.spinner_activity){
            if (l == -1){

            }
        } else if (view.getId() == R.id.spinner_event){
            if (l == -1){

            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
        Log.d(TAG, "checkApplicationPermissions:ACCESS_FINE_LOCATION:"+(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED));
        Log.d(TAG, "checkApplicationPermissions:ACCESS_COARSE_LOCATION:"+(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED));
        Log.d(TAG, "checkApplicationPermissions:INTERNET:"+(ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED));
        Log.d(TAG, "checkApplicationPermissions:ACCESS_WIFI_STATE:"+(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED));

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 102);
            }
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.INTERNET)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.INTERNET}, 103);
            }
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_WIFI_STATE)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 106);
            }
        }
    }
}
