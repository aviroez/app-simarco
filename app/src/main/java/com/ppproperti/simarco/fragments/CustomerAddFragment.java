package com.ppproperti.simarco.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.adapters.EventListRecyclerViewAdapter;
import com.ppproperti.simarco.adapters.SpinnerActivityAdapter;
import com.ppproperti.simarco.adapters.SpinnerApartmentAdapter;
import com.ppproperti.simarco.adapters.SpinnerEventAdapter;
import com.ppproperti.simarco.entities.Activity;
import com.ppproperti.simarco.entities.Apartment;
import com.ppproperti.simarco.entities.Customer;
import com.ppproperti.simarco.entities.CustomerLead;
import com.ppproperti.simarco.entities.Event;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.interfaces.ActivityService;
import com.ppproperti.simarco.interfaces.ApartmentService;
import com.ppproperti.simarco.interfaces.CustomerService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.OnBackPressed;
import com.ppproperti.simarco.responses.ResponseActivityList;
import com.ppproperti.simarco.responses.ResponseCustomer;
import com.ppproperti.simarco.responses.ResponseCustomerLead;
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
 * {@link CustomerAddFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomerAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerAddFragment extends Fragment implements View.OnClickListener, OnBackPressed {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = CustomerAddFragment.class.getSimpleName();
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
    private TextInputEditText textBirthDate;
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
    private ApartmentService apartmentService;
    private List<Event> listEvent = new ArrayList<>();
    private EventListRecyclerViewAdapter adapter;
    private Spinner spinnerApartment;
    private Spinner spinnerEvent;
    private Spinner spinnerActivity;
    private Button buttonImport;
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
    private List<Apartment> listApartment = new ArrayList<>();
    private Apartment apartment = new Apartment();
    private CountryCodePicker ccp;
    private CustomerService customerService;
    private String birthDate;

    public CustomerAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerAddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerAddFragment newInstance(String param1, String param2) {
        CustomerAddFragment fragment = new CustomerAddFragment();
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
        currentActivity = getActivity();

        customPreferences = new CustomPreferences(context);
        user = customPreferences.getUser();
        retrofit = Helpers.initRetrofit(context);
        activityService = retrofit.create(ActivityService.class);
        apartmentService = retrofit.create(ApartmentService.class);
        customerService = retrofit.create(CustomerService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_add, container, false);
        spinnerApartment = view.findViewById(R.id.spinner_apartment);
        spinnerEvent = view.findViewById(R.id.spinner_event);
        spinnerActivity = view.findViewById(R.id.spinner_activity);
        buttonImport = view.findViewById(R.id.button_import);
        textFullName = view.findViewById(R.id.text_full_name);
        textNik = view.findViewById(R.id.text_nik);
        textNpwp = view.findViewById(R.id.text_npwp);
        ccp = view.findViewById(R.id.ccp);
        textHandphone = view.findViewById(R.id.text_hand_phone);
        textEmail = view.findViewById(R.id.text_email);
        textBirthDate = view.findViewById(R.id.text_birth_date);
        textAddress = view.findViewById(R.id.text_address);
        radioGroup = view.findViewById(R.id.radio_group);
        radioMale = view.findViewById(R.id.radio_male);
        radioFemale = view.findViewById(R.id.radio_female);
        buttonAdd = view.findViewById(R.id.button_add);

        ccp.registerCarrierNumberEditText(textHandphone);
        ccp.setVisibility(View.GONE);

        radioGroup.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        buttonImport.setOnClickListener(this);

        listApartment = customPreferences.getListApartment();
        listApartment.add(0, new Apartment(0, "[Pilih Apartment]"));
        SpinnerApartmentAdapter arrayAdapter = new SpinnerApartmentAdapter(context, listApartment);
        spinnerApartment.setAdapter(arrayAdapter);
        spinnerApartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                apartment = listApartment.get(i);
                initList();
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

        checkApplicationPermissions();

        if (currentActivity != null){
            currentActivity.setTitle(R.string.add_customer);
            ((HideShowIconInterface) currentActivity).showBackIcon();
        }
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
        } else if (view.getId() == buttonImport.getId()){
            importAction(view);
        }
    }

    @Override
    public void onBackPressed() {
        /*backAction();*/
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
        if (validation()){
            HashMap<String, String> map = new HashMap<>();
            map.put("name", fullName);
            map.put("nik", nik);
            map.put("npwp", npwp);
            map.put("handphone", handphone);
            map.put("email", email);
            map.put("address", address);
            map.put("marketing_id", String.valueOf(user.getId()));
            if (apartment != null && apartment.getId() > 0){
                map.put("apartment_id", String.valueOf(apartment.getId()));
            }
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

            Call<ResponseCustomer> call = service.addCustomers(map);
            call.enqueue(new Callback<ResponseCustomer>() {
                @Override
                public void onResponse(Call<ResponseCustomer> call, Response<ResponseCustomer> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getCode() == 201){
                            Helpers.showLongSnackbar(getView(), response.body().getMessage());
                        } else {
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
                }

                @Override
                public void onFailure(Call<ResponseCustomer> call, Throwable t) {
                    Log.d(TAG, t.getMessage());
                }
            });
        }
    }

    private void backAction() {
        Helpers.moveFragment(context, new CustomerListFragment(), null);
    }

    private boolean validation() {
        if (spinnerApartment !=  null){
            apartment = listApartment.get(spinnerApartment.getSelectedItemPosition());
        }
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

        if (apartment.getId() <= 0){
            Helpers.showLongSnackbar(spinnerApartment, getString(R.string.apartment_is_required));
            return false;
        } else if (fullName.length() <= 0){
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
        map.put("apartment_id", String.valueOf(apartment.getId()));
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
        hashMap.put("apartment_id", String.valueOf(apartment.getId()));
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

    private void importAction(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(i, Constants.PICK_PHONE_NUMBER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:"+requestCode+","+resultCode);
        if (requestCode == Constants.PICK_PHONE_NUMBER && resultCode == -1 && data != null && data.getData() != null) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.Data.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
            Cursor cursor = context.getContentResolver().query(contactUri, projection,
                    null, null, null);

            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                // Do something with the phone number
                Log.d(TAG, "onActivityResult:id:" + id);
                Log.d(TAG, "onActivityResult:number:" + number);
                Log.d(TAG, "onActivityResult:displayName:" + displayName);
                Log.d(TAG, "onActivityResult:email:" + email);
                if (number.trim().length() > 0) textHandphone.setText(number);
                if (displayName.trim().length() > 0) textFullName.setText(displayName);
                cursor.close();
                parseEmail(id);
                parseAddress(id);
            }
        }
    }

    private void checkApplicationPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{Manifest.permission.READ_CONTACTS}, 101);
            }
        }
    }

    public String getEmail(String contactId) {
        String emailStr = "";
//        final String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.DATA,
//                ContactsContract.CommonDataKinds.Email.TYPE};
//
//        Cursor emailq = managedQuery(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, ContactsContract.Data.CONTACT_ID + "=?", new String[]{contactId}, null);
//
//        if (emailq.moveToFirst()) {
//            final int contactEmailColumnIndex = emailq.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
//
//            while (!emailq.isAfterLast()) {
//                emailStr = emailStr + emailq.getString(contactEmailColumnIndex) + ";";
//                emailq.moveToNext();
//            }
//        }
        return emailStr;
    }

    private void parseEmail(String id) {
        Cursor emails = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + "="+id, null, null);

        if (emails != null){
            while (emails.moveToNext()) {
                String email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                if (email.trim().length() > 0 && Helpers.isValidEmail(email.trim())) textEmail.setText(email);
            }
            emails.close();

        }
    }

    private void parseAddress(String id){
        Uri postal_uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(postal_uri,null,  ContactsContract.Data.CONTACT_ID + "="+id, null,null);
        if (cursor != null){
            while(cursor.moveToNext())
            {
                String street = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                String city = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                String country = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));

                String fullAddress = "";
                if (street != null){
                    fullAddress += street;
                }
                if (city != null){
                    if (fullAddress.length() > 0) fullAddress += ", ";
                    fullAddress += city;
                }
                if (country != null){
                    if (fullAddress.length() > 0) fullAddress += ", ";
                    fullAddress += country;
                }
                if (fullAddress.trim().length() > 0) textAddress.setText(fullAddress);
            }
            cursor.close();
        }
    }

    private void parseData(final Customer cl) {
        HashMap<String, String> map = new HashMap<>();
        customerService.customer(cl.getId(), map).enqueue(new Callback<ResponseCustomer>() {
            @Override
            public void onResponse(Call<ResponseCustomer> call, Response<ResponseCustomer> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getData() != null) {
                            customer = response.body().getData();

                            spinnerApartment.setSelection(getSpinnerApartment(customer));
                            spinnerEvent.setSelection(getSpinnerEvent(customer));
                            spinnerActivity.setSelection(getSpinnerActivity(customer));
                            textFullName.setText(customer.getName());
                            textNik.setText(customer.getNik());
                            textNpwp.setText(customer.getNpwp());
                            textHandphone.setText(customer.getHandphone());
                            textEmail.setText(customer.getEmail());
                            textAddress.setText(customer.getAddress());
                            if (customer.getBirthDate() != null) {
                                birthDate = Helpers.reformatDate(customer.getBirthDate(), Constants.DATE_FORMAT_SQL, Constants.DATE_FORMAT_LOCAL_S);
                                textBirthDate.setText(birthDate);
                            }
                            if (customer.getGender() != null) {
                                if (customer.getGender().equals("m")) radioMale.setChecked(true);
                                else if (customer.getGender().equals("f"))
                                    radioFemale.setChecked(true);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseCustomer> call, Throwable t) {

            }
        });
    }


    private int getSpinnerApartment(Customer customerLead) {
        if (listApartment != null && listApartment.size() > 0 && customerLead != null){
            for (Apartment apartment: listApartment){
                if (apartment.getId() == customerLead.getApartmentId()) return listApartment.indexOf(apartment);
            }
        }
        return 0;
    }

    private int getSpinnerEvent(Customer customerLead) {
        if (listEvent != null && listEvent.size() > 0 && customerLead != null){
            for (Event event: listEvent){
                if (event.getId() == customerLead.getEventId()) return listEvent.indexOf(event);
            }
        }
        return 0;
    }

    private int getSpinnerActivity(Customer customerLead) {
        if (listActivity != null && listActivity.size() > 0 && customerLead != null){
            for (Activity activity: listActivity){
                if (activity.getId() == customerLead.getActivityId()) return listActivity.indexOf(activity);
            }
        }
        return 0;
    }
}
