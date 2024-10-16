package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.adapters.EventListRecyclerViewAdapter;
import com.ppproperti.simarco.adapters.SpinnerActivityAdapter;
import com.ppproperti.simarco.adapters.SpinnerEventAdapter;
import com.ppproperti.simarco.entities.Activity;
import com.ppproperti.simarco.entities.Customer;
import com.ppproperti.simarco.entities.Event;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.interfaces.ActivityService;
import com.ppproperti.simarco.interfaces.CustomerService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.OnBackPressed;
import com.ppproperti.simarco.responses.ResponseActivityList;
import com.ppproperti.simarco.responses.ResponseCustomer;
import com.ppproperti.simarco.responses.ResponseEventList;
import com.ppproperti.simarco.utils.Constants;
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
 * {@link CustomerEditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomerEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerEditFragment extends Fragment implements View.OnClickListener, OnBackPressed {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = CustomerEditFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Context context;
    private Retrofit retrofit;
    private TextInputEditText textFullName;
    private TextInputEditText textNik;
    private TextInputEditText textNpwp;
    private TextInputEditText textHandphone;
    private TextInputEditText textEmail;
    private BootstrapButton buttonAdd;
    private Customer customer;
    private String fullName;
    private String nik;
    private String npwp;
    private String handphone;
    private String email;
    private CustomPreferences customPreferences;
    private User user;
    private ActivityService activityService;
    private List<Event> listEvent = new ArrayList<>();
    private EventListRecyclerViewAdapter adapter;
    private Spinner spinnerEvent;
    private Spinner spinnerActivity;
    private Event event = new Event();
    private RadioGroup radioGroup;
    private RadioButton radioMale;
    private RadioButton radioFemale;
    private String gender;
    private List<Activity> listActivity = new ArrayList<>();
    private Activity activity;
    private TextView textAddress;
    private String address;
    private FragmentActivity currentActivity;

    public CustomerEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param customer Customer.
     * @return A new instance of fragment CustomerEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerEditFragment newInstance(Customer customer) {
        CustomerEditFragment fragment = new CustomerEditFragment();
        Bundle args = new Bundle();
        args.putParcelable("customer", customer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            customer = getArguments().getParcelable("customer");
        }
        context = getContext();
        currentActivity = getActivity();
        customPreferences = new CustomPreferences(context);
        user = customPreferences.getUser();
        retrofit = Helpers.initRetrofit(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_edit, container, false);
        spinnerEvent = view.findViewById(R.id.spinner_event);
        spinnerActivity = view.findViewById(R.id.spinner_activity);
        textFullName = view.findViewById(R.id.text_full_name);
        textNik = view.findViewById(R.id.text_nik);
        textNpwp = view.findViewById(R.id.text_npwp);
        textHandphone = view.findViewById(R.id.text_hand_phone);
        textEmail = view.findViewById(R.id.text_email);
        textAddress = view.findViewById(R.id.text_address);
        radioGroup = view.findViewById(R.id.radio_group);
        radioMale = view.findViewById(R.id.radio_male);
        radioFemale = view.findViewById(R.id.radio_female);
        buttonAdd = view.findViewById(R.id.button_add);

        radioGroup.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);

        spinnerEvent.setVisibility(View.GONE);
        spinnerActivity.setVisibility(View.GONE);

        if (customer != null){
            textFullName.setText(customer.getName());
            textNik.setText(customer.getNik());
            textNpwp.setText(customer.getNpwp());
            textHandphone.setText(customer.getHandphone());
            textEmail.setText(customer.getEmail());
            textAddress.setText(customer.getAddress());

            if (customer.getGender() != null){
                if (customer.getGender().equalsIgnoreCase(Constants.MALE)){
                    radioMale.setChecked(true);
                } else if (customer.getGender().equalsIgnoreCase(Constants.FEMALE)){
                    radioFemale.setChecked(true);
                }
            }
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (currentActivity != null){
            currentActivity.setTitle(R.string.add_customer);
            ((HideShowIconInterface) currentActivity).showBackIcon();
        }

        retrofit = Helpers.initRetrofit(context);
        activityService = retrofit.create(ActivityService.class);

        initList();
    }

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
        if (view.getId() == R.id.button_add){
            addAction();
        }
    }

    @Override
    public void onBackPressed() {
        backAction();
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

    private void addAction() {
        if (validation() && customer != null){
            HashMap<String, String> map = new HashMap<>();
            map.put("name", fullName);
            map.put("nik", nik);
            map.put("npwp", npwp);
            map.put("handphone", handphone);
            map.put("email", email);
            map.put("address", address);
            map.put("marketing_id", String.valueOf(user.getId()));
            if (event != null && event.getId() > 0){
                map.put("event_id", String.valueOf(event.getId()));
            }
            if (activity != null && activity.getId() > 0){
                map.put("activity_id", String.valueOf(activity.getId()));
            }
            if (gender != null){
                map.put("gender", gender);
            }

            CustomerService service = retrofit.create(CustomerService.class);

            Call<ResponseCustomer> call = service.updateCustomer(customer.getId(), map);
            call.enqueue(new Callback<ResponseCustomer>() {
                @Override
                public void onResponse(Call<ResponseCustomer> call, Response<ResponseCustomer> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getData() != null) {
                            customer = response.body().getData();
                            Bundle bundle = new Bundle();
                            bundle.putString("message", context.getString(R.string.customer_is_added_successfully));
                            Helpers.moveFragment(context, new CustomerListFragment(), bundle);
                        } else if (response.body().getMessage().length() > 0){
                            Helpers.showLongSnackbar(getView(), response.body().getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseCustomer> call, Throwable t) {
                    Log.d(TAG, t.getMessage());
                }
            });
        }
    }

    private void backAction(){
        Bundle bundle = new Bundle();
        bundle.putParcelable("customer", customer);
        Helpers.moveFragment(context, new CustomerDetailFragment(), bundle);
    }

    private boolean validation() {
        if (spinnerEvent !=  null){
            event = listEvent.get(spinnerEvent.getSelectedItemPosition());
        }
        if (spinnerActivity !=  null){
            activity = listActivity.get(spinnerActivity.getSelectedItemPosition());
        }
        fullName = textFullName.getText().toString();
        nik = textNik.getText().toString();
        npwp = textNpwp.getText().toString();
        handphone = Helpers.formatPhone(textHandphone.getText().toString());
        email = textEmail.getText().toString();
        address = textAddress.getText().toString();
        textHandphone.setText(handphone);

        if (radioMale.isChecked()){
            gender = Constants.MALE;
        } else if(radioFemale.isChecked()){
            gender = Constants.FEMALE;
        }

        if (fullName.length() <= 0){
            Helpers.showLongSnackbar(textFullName, getString(R.string.full_name_is_required));
            textFullName.requestFocus();
            return false;
        }
//        else if (nik.length() <= 0){
//            Helpers.showLongSnackbar(textNik, R.string.nik_is_required);
//            textNik.requestFocus();
//            return false;
//        }
        else if (handphone.length() <= 0){
            Helpers.showLongSnackbar(textHandphone, R.string.handphone_is_required);
            textHandphone.requestFocus();
            return false;
        } else if (handphone.length() <= 5){
            Helpers.showLongSnackbar(textHandphone, R.string.handphone_is_invalid);
            textHandphone.requestFocus();
            return false;
        }
//        else if (email.length() <= 0){
//            Helpers.showLongSnackbar(textEmail, R.string.email_is_required);
//            textEmail.requestFocus();
//            return false;
//        }
        return true;
    }

    private void initList() {
//        loadListEvent();
        HashMap<String, String> map = new HashMap<>();
        map.put("order_by_desc[0]", "start_date");
        map.put("order_by_desc[1]", "end_date");
        map.put("active", "1");
        Call<ResponseEventList> call = activityService.listEvent(map);
        call.enqueue(new Callback<ResponseEventList>() {
            @Override
            public void onResponse(Call<ResponseEventList> call, Response<ResponseEventList> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getData() != null){
                        listEvent = response.body().getData();
                    }

                    listEvent.add(0, new Event(0, "[Tidak Ada Event]"));

                    SpinnerEventAdapter arrayAdapter = new SpinnerEventAdapter(context, listEvent);
                    spinnerEvent.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onFailure(Call<ResponseEventList> call, Throwable t) {

            }
        });

        HashMap<String, String> hashMap = new HashMap<>();
        Call<ResponseActivityList> callActivity = activityService.listActivity(hashMap);
        callActivity.enqueue(new Callback<ResponseActivityList>() {
            @Override
            public void onResponse(Call<ResponseActivityList> call, Response<ResponseActivityList> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getData() != null){
                        listActivity = response.body().getData();

                        listActivity.add(0, new Activity(0, "[Tidak Ada Activity]"));

                        SpinnerActivityAdapter arrayAdapter = new SpinnerActivityAdapter(context, listActivity);
                        spinnerActivity.setAdapter(arrayAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseActivityList> call, Throwable t) {

            }
        });
    }
}
