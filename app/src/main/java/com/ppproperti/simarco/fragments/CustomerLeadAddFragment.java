package com.ppproperti.simarco.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import com.ppproperti.simarco.entities.CustomerLeadDetail;
import com.ppproperti.simarco.entities.Event;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.interfaces.ActivityService;
import com.ppproperti.simarco.interfaces.ApartmentService;
import com.ppproperti.simarco.interfaces.CustomerLeadDetailService;
import com.ppproperti.simarco.interfaces.CustomerLeadService;
import com.ppproperti.simarco.interfaces.CustomerService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.OnBackPressed;
import com.ppproperti.simarco.responses.ResponseActivityList;
import com.ppproperti.simarco.responses.ResponseCustomer;
import com.ppproperti.simarco.responses.ResponseCustomerLead;
import com.ppproperti.simarco.responses.ResponseCustomerLeadDetail;
import com.ppproperti.simarco.responses.ResponseCustomerLeadDetailList;
import com.ppproperti.simarco.responses.ResponseCustomerLeadList;
import com.ppproperti.simarco.responses.ResponseEventList;
import com.ppproperti.simarco.utils.Constants;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomerLeadAddFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomerLeadAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerLeadAddFragment extends Fragment implements View.OnClickListener, OnBackPressed {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = CustomerLeadAddFragment.class.getSimpleName();
    private static final String POSITION = "position";
    private static final String CUSTOMER_LEAD = "customer_lead";

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
    private CustomerLead customerLead = new CustomerLead();
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
    private SpinnerEventAdapter eventAdapter;
    private SimpleDateFormat sdf;
    private TextInputEditText textBirthDate;
    private Calendar calendar = Calendar.getInstance();
    private Calendar calendarBirth = Calendar.getInstance();
    private int position = 0;
    private String birthDate;
    private View layoutContentCustomer;
    private View layoutContentCustomerDetail;
    private CheckBox checkShow;
    private CheckBox checkShowResult;
    private DatePickerDialog.OnDateSetListener buyDateListener;
    private Spinner spinnerClassification;
    private Spinner spinnerResponse;
    private Spinner spinnerProductType;
    private Spinner spinnerProduct;
    private TextInputEditText textInformation;
    private TextInputEditText textBuyDate;
    private TextInputEditText textProduct;
    private TextInputEditText textPrice;
    private TextInputEditText textUnit;
    private String information;
    private String unit;
    private String classification;
    private String responseString;
    private String type;
    private String buyDate;
    private SearchView searchView;
    private String product;
    private String price;
    private CustomerLeadService customerLeadService;
    private CustomerLeadDetailService customerLeadDetailService;
    private List<CustomerLeadDetail> customerLeadDetailList = new ArrayList<>();
    private CustomerLeadDetail customerLeadDetail = new CustomerLeadDetail();

    public CustomerLeadAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Last tab position.
     * @return A new instance of fragment CustomerAddFragment.
     */
    public static CustomerLeadAddFragment newInstance(int position) {
        CustomerLeadAddFragment fragment = new CustomerLeadAddFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION, 0);
            customerLead = getArguments().getParcelable(CUSTOMER_LEAD);
        }
        context = getContext();
        currentActivity = getActivity();

        customPreferences = new CustomPreferences(context);
        user = customPreferences.getUser();
        retrofit = Helpers.initRetrofit(context);
        activityService = retrofit.create(ActivityService.class);
        apartmentService = retrofit.create(ApartmentService.class);
        customerLeadService = retrofit.create(CustomerLeadService.class);
        customerLeadDetailService = retrofit.create(CustomerLeadDetailService.class);
        sdf = new SimpleDateFormat(Constants.DATE_FORMAT_LOCAL_S, Constants.locale);
        calendarBirth.add(Calendar.YEAR, -17);
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
        textBirthDate = view.findViewById(R.id.text_birth_date);
        ccp = view.findViewById(R.id.ccp);
        textHandphone = view.findViewById(R.id.text_hand_phone);
        textEmail = view.findViewById(R.id.text_email);
        textAddress = view.findViewById(R.id.text_address);
        radioGroup = view.findViewById(R.id.radio_group);
        radioMale = view.findViewById(R.id.radio_male);
        radioFemale = view.findViewById(R.id.radio_female);
        buttonAdd = view.findViewById(R.id.button_add);
        layoutContentCustomer = view.findViewById(R.id.layout_content_customer);
        layoutContentCustomerDetail = view.findViewById(R.id.layout_content_customer_detail);
        checkShow = view.findViewById(R.id.check_show);
        checkShowResult = view.findViewById(R.id.check_show_result);
        checkShow = view.findViewById(R.id.check_show);

        spinnerClassification = view.findViewById(R.id.spinner_classification);
        spinnerResponse = view.findViewById(R.id.spinner_response);
        textInformation = view.findViewById(R.id.text_information);
        textProduct = view.findViewById(R.id.text_product);
        textPrice = view.findViewById(R.id.text_price);
        textUnit = view.findViewById(R.id.text_unit);
        textBuyDate = view.findViewById(R.id.text_buy_date);
        spinnerProductType = view.findViewById(R.id.spinner_product_type);
        spinnerProduct = view.findViewById(R.id.spinner_product);

        ccp.registerCarrierNumberEditText(textHandphone);
        ccp.setVisibility(View.GONE);

        radioGroup.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        buttonImport.setOnClickListener(this);

        textBirthDate.setOnClickListener(this);
        
        if (currentActivity != null){
            searchView = currentActivity.findViewById(R.id.search_view);
            searchView.setVisibility(View.GONE);
        }

        eventAdapter = new SpinnerEventAdapter(context, listEvent);
        spinnerEvent.setAdapter(eventAdapter);

        listApartment = customPreferences.getListApartment();
        listApartment.add(0, new Apartment(0, "[Pilih Apartment]"));
        SpinnerApartmentAdapter arrayAdapter = new SpinnerApartmentAdapter(context, listApartment);
        spinnerApartment.setAdapter(arrayAdapter);
        spinnerApartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                apartment = listApartment.get(i);
                initList(apartment.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("apartment_id", String.valueOf(apartment.getId()));
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

                        if (customerLead != null) spinnerActivity.setSelection(getSpinnerActivity(customerLead));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseActivityList> call, Throwable t) {

            }
        });

        checkShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    layoutContentCustomer.setVisibility(View.VISIBLE);
                } else {
                    layoutContentCustomer.setVisibility(View.GONE);
                }
            }
        });

        checkShowResult.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    layoutContentCustomerDetail.setVisibility(View.VISIBLE);
                } else {
                    layoutContentCustomerDetail.setVisibility(View.GONE);
                }
            }
        });

        textBuyDate.setOnClickListener(this);
        buyDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                textBuyDate.setText(sdf.format(calendar.getTime()));
            }
        };

        if (customerLead != null){
            parseData(customerLead);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        checkApplicationPermissions();

        if (currentActivity != null){
            currentActivity.setTitle(R.string.add_customer_list);
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
        } else if (view.getId() == textBirthDate.getId()){
            birthDateAction(view);
        } else if (view.getId() == textBuyDate.getId()){
            buyDateAction(view);
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
            customerLeadValidation();
            HashMap<String, String> map = new HashMap<>();
            if (customerLead != null) map.put("id", String.valueOf(customerLead.getId()));
            map.put("name", fullName);
            map.put("nik", nik);
            map.put("npwp", npwp);
            map.put("handphone", handphone);
            map.put("email", email);
            map.put("address", address);
            if (birthDate != null) map.put("birth_date", birthDate);
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

//            CustomerService service = retrofit.create(CustomerService.class);

            Call<ResponseCustomerLead> call = customerLeadService.addCustomerLeads(map);
            call.enqueue(new Callback<ResponseCustomerLead>() {
                @Override
                public void onResponse(Call<ResponseCustomerLead> call, Response<ResponseCustomerLead> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getCode() == 201){
                            Helpers.showLongSnackbar(getView(), response.body().getMessage());
                        } else {
                            if (response.body().getData() != null) {
                                customerLead = response.body().getData();
                                if (customerLead != null){
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("customer_lead_id", String.valueOf(customerLead.getId()));
                                    map.put("classification", classification);
                                    map.put("response", responseString);
                                    map.put("type", type);
                                    map.put("description", information);
                                    map.put("product", product);
                                    map.put("price", Helpers.removeNonDigit(price));
                                    map.put("unit", unit);
                                    if (buyDate != null) map.put("buy_date", buyDate);
                                    customerLeadDetailService.add(map).enqueue(new Callback<ResponseCustomerLeadDetail>() {
                                        @Override
                                        public void onResponse(Call<ResponseCustomerLeadDetail> call, Response<ResponseCustomerLeadDetail> response) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("message", context.getString(R.string.customer_lead_is_added_successfully));
                                            bundle.putInt("position", position);
                                            Helpers.moveFragment(context, new CustomerListFragment(), bundle);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseCustomerLeadDetail> call, Throwable t) {
                                            Helpers.showLongSnackbar(getView(), t.getMessage());
                                        }
                                    });
                                } else {
                                    Helpers.showLongSnackbar(getView(), R.string.customer_lead_is_failed_to_add);
                                }
                            } else if (response.body().getMessage().length() > 0){
                                Helpers.showLongSnackbar(getView(), response.body().getMessage());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseCustomerLead> call, Throwable t) {
                    Log.d(TAG, t.getMessage());
                }
            });
        }
    }

    private void backAction() {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        Helpers.moveFragment(context, new CustomerListFragment(), bundle);
    }

    private boolean validation() {
        if (spinnerApartment != null){
            apartment = listApartment.get(spinnerApartment.getSelectedItemPosition());
        }
        if (spinnerEvent != null){
            event = listEvent.get(spinnerEvent.getSelectedItemPosition());
        }
        if (spinnerActivity != null){
            activity = listActivity.get(spinnerActivity.getSelectedItemPosition());
        }
        fullName = textFullName.getText().toString();
        nik = textNik.getText().toString();
        npwp = textNpwp.getText().toString();
        handphone = Helpers.formatPhone(textHandphone.getText().toString());
        email = textEmail.getText().toString();
        address = textAddress.getText().toString();
        if (textBirthDate.getText() != null){
            birthDate = Helpers.reformatDate(textBirthDate.getText().toString(), Constants.DATE_FORMAT_LOCAL_S, Constants.DATE_FORMAT_SQL);
        } else birthDate = null;
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

    private boolean customerLeadValidation() {
        information = textInformation.getText().toString();
        product = textProduct.getText().toString();
        price = textPrice.getText().toString();
        unit = textUnit.getText().toString();
        if (spinnerClassification != null) {
            String[] codes = getResources().getStringArray(R.array.classification_codes);
            if (spinnerClassification.getSelectedItemPosition() < codes.length){
                classification = codes[spinnerClassification.getSelectedItemPosition()];
            }
        }
        if (spinnerResponse != null) {
            String[] codes = getResources().getStringArray(R.array.response_codes);
            if (spinnerResponse.getSelectedItemPosition() < codes.length){
                responseString = codes[spinnerResponse.getSelectedItemPosition()];
            }
        }
        if (spinnerProductType != null) {
            String[] codes = getResources().getStringArray(R.array.type_codes);
            if (spinnerProductType.getSelectedItemPosition() < codes.length){
                type = codes[spinnerProductType.getSelectedItemPosition()];
            }
        }
        if (textBuyDate.getText() != null){
            buyDate = Helpers.reformatDate(textBuyDate.getText().toString(), Constants.DATE_FORMAT_LOCAL_S, Constants.DATE_FORMAT_SQL);
        } else buyDate = null;
        return true;
    }

    private void initList(int apartmentId) {
//        loadListEvent();
        HashMap<String, String> map = new HashMap<>();
        map.put("order_by_desc[0]", "start_date");
        map.put("order_by_desc[1]", "end_date");
        map.put("active", "1");
        map.put("apartment_id", String.valueOf(apartmentId));
        Call<ResponseEventList> call = activityService.listEvent(map);
        call.enqueue(new Callback<ResponseEventList>() {
            @Override
            public void onResponse(Call<ResponseEventList> call, Response<ResponseEventList> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getData() != null){
                        listEvent = response.body().getData();
                    }

                    listEvent.add(0, new Event(0, "[Tidak Ada Event]"));
                    eventAdapter.updateItems(listEvent);

                    if (customerLead != null) spinnerEvent.setSelection(getSpinnerEvent(customerLead));
                }
            }

            @Override
            public void onFailure(Call<ResponseEventList> call, Throwable t) {

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

    private DatePickerDialog.OnDateSetListener birthDateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            calendarBirth.set(Calendar.YEAR, year);
            calendarBirth.set(Calendar.MONTH, monthOfYear);
            calendarBirth.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            textBirthDate.setText(sdf.format(calendarBirth.getTime()));
        }
    };

    private void birthDateAction(View view) {
        new DatePickerDialog(context, birthDateListener, calendarBirth
                .get(Calendar.YEAR), calendarBirth.get(Calendar.MONTH),
                calendarBirth.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void buyDateAction(View view) {
        new DatePickerDialog(context, buyDateListener, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void parseData(final CustomerLead cl) {
        HashMap<String, String> map = new HashMap<>();
        customerLeadService.show(cl.getId(), map).enqueue(new Callback<ResponseCustomerLead>() {
            @Override
            public void onResponse(Call<ResponseCustomerLead> call, Response<ResponseCustomerLead> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        if (response.body().getData() != null){
                            customerLead = response.body().getData();

                            spinnerApartment.setSelection(getSpinnerApartment(customerLead));
                            spinnerEvent.setSelection(getSpinnerEvent(customerLead));
                            spinnerActivity.setSelection(getSpinnerActivity(customerLead));
                            textFullName.setText(customerLead.getName());
                            textNik.setText(customerLead.getNik());
                            textNpwp.setText(customerLead.getNpwp());
                            textHandphone.setText(customerLead.getHandphone());
                            textEmail.setText(customerLead.getEmail());
                            textAddress.setText(customerLead.getAddress());
                            if (customerLead.getBirthDate() != null){
                                birthDate = Helpers.reformatDate(customerLead.getBirthDate(), Constants.DATE_FORMAT_SQL, Constants.DATE_FORMAT_LOCAL_S);
                                textBirthDate.setText(birthDate);
                            }
                            if (customerLead.getGender() != null){
                                if (customerLead.getGender().equals("m")) radioMale.setChecked(true);
                                else if (customerLead.getGender().equals("f")) radioFemale.setChecked(true);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseCustomerLead> call, Throwable t) {

            }
        });

        HashMap<String, String> mapDetail = new HashMap<>();
        mapDetail.put("customer_lead_id", String.valueOf(cl.getId()));
        mapDetail.put("limit", "0");
        customerLeadDetailService.get(mapDetail).enqueue(new Callback<ResponseCustomerLeadDetailList>() {
            @Override
            public void onResponse(Call<ResponseCustomerLeadDetailList> call, Response<ResponseCustomerLeadDetailList> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        if (response.body().getData() != null){
                            customerLeadDetailList = response.body().getData();

                            if (customerLeadDetailList.size() > 0){
                                customerLeadDetail = customerLeadDetailList.get(customerLeadDetailList.size()-1);

                                spinnerClassification.setSelection(getSpinnerClassification(customerLeadDetail));
                                spinnerResponse.setSelection(getSpinnerResponse(customerLeadDetail));
                                textInformation.setText(customerLeadDetail.getDescription());
                                textProduct.setText(customerLeadDetail.getProduct());
                                textPrice.setText(Helpers.currency(String.valueOf(customerLeadDetail.getPrice()), false));
                                textUnit.setText(customerLeadDetail.getUnit());
                                if (customerLeadDetail.getBuyDate() != null){
                                    buyDate = Helpers.reformatDate(customerLeadDetail.getBuyDate(), Constants.DATE_FORMAT_SQL, Constants.DATE_FORMAT_LOCAL_S);
                                    textBuyDate.setText(buyDate);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseCustomerLeadDetailList> call, Throwable t) {

            }
        });
    }

    private int getSpinnerApartment(CustomerLead customerLead) {
        if (listApartment != null && listApartment.size() > 0 && customerLead != null){
            for (Apartment apartment: listApartment){
                if (apartment.getId() == customerLead.getApartmentId()) return listApartment.indexOf(apartment);
            }
        }
        return 0;
    }

    private int getSpinnerEvent(CustomerLead customerLead) {
        if (listEvent != null && listEvent.size() > 0 && customerLead != null){
            for (Event event: listEvent){
                if (event.getId() == customerLead.getEventId()) return listEvent.indexOf(event);
            }
        }
        return 0;
    }

    private int getSpinnerActivity(CustomerLead customerLead) {
        if (listActivity != null && listActivity.size() > 0 && customerLead != null){
            for (Activity activity: listActivity){
                if (activity.getId() == customerLead.getActivityId()) return listActivity.indexOf(activity);
            }
        }
        return 0;
    }

    private int getSpinnerClassification(CustomerLeadDetail detail) {
        switch (detail.getClassification()) {
            case "unknown": return 0;
            case "leads": return 1;
            case "prospect": return 2;
            case "hot_prospect": return 3;
            case "customer": return 4;
            case "cancel": return 5;
            case "invalid_number": return 6;
            case "complain": return 7;
            case "no_respond": return 8;
            default: return 0;
        }
    }

    private int getSpinnerResponse(CustomerLeadDetail detail) {
        switch (detail.getResponse()) {
            case "unknown": return 0;
            case "answer_waiting": return 1;
            case "visit_mo": return 2;
            case "deal_processing": return 3;
            case "deal_success": return 4;
            case "not_interested": return 5;
            case "invalid_number": return 6;
            case "complain": return 7;
            case "no_respond": return 8;
            default: return 0;
        }
    }
}
