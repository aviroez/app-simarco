package com.ppproperti.simarco.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.Activity;
import com.ppproperti.simarco.entities.CustomerLead;
import com.ppproperti.simarco.entities.Event;
import com.ppproperti.simarco.entities.EventActivityLog;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.interfaces.ActivityService;
import com.ppproperti.simarco.interfaces.CustomerLeadDetailService;
import com.ppproperti.simarco.interfaces.CustomerLeadService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.ImageService;
import com.ppproperti.simarco.responses.ResponseActivityList;
import com.ppproperti.simarco.responses.ResponseCustomerLead;
import com.ppproperti.simarco.responses.ResponseCustomerLeadDetail;
import com.ppproperti.simarco.responses.ResponseEventActivityLog;
import com.ppproperti.simarco.utils.Constants;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.GPSTracker;
import com.ppproperti.simarco.utils.Helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActivityListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActivityListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityListFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = ActivityListFragment.class.getSimpleName();
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // 1 minutes
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 5 meters

    private OnFragmentInteractionListener mListener;
    private Event event;
    private Spinner spinnerActivityEvent;
    private Context context;
    private android.app.Activity currentActivity;
    private Retrofit retrofit;
    private ActivityService activityService;
    private Call<ResponseActivityList> call;
    private List<Activity> listActivity = new ArrayList<>();
    private TextView textEventName;
    private ImageView imageCapture;
    private BootstrapButton buttonBack;
    private BootstrapButton buttonCapture;
    private BootstrapButton buttonNext;
    private int activityId = 0;
    private double latitude = 0;
    private double longitude = 0;
    private CustomPreferences customPreferences;
    private User user = new User();
    private String absolutePath;
    private Activity activity = new Activity();
    private GPSTracker gpsTracker;
    private String addressLocation;
    private ImageService serviceImage;

    private CheckBox checkCustomer;
    private View layoutContentCustomer;
    private View layoutDetail;
    private View layoutContentCustomerDetail;
    private View layoutCapture;
    private View layoutApartment;
    private View layoutEvent;
    private View layoutActivity;
    private Spinner spinnerApartment;
    private Spinner spinnerEvent;
    private Spinner spinnerActivity;
    private Button buttonImport;
    private RadioGroup radioGroup;
    private RadioButton radioMale;
    private RadioButton radioFemale;
    private TextInputEditText textFullName;
    private TextInputEditText textBirthDate;
    private TextInputEditText textNik;
    private TextInputEditText textNpwp;
    private TextInputEditText textHandphone;
    private TextInputEditText textEmail;
    private TextView textAddress;
    private CountryCodePicker ccp;
    private CustomerLeadService customerLeadService;
    private CustomerLead customerLead = new CustomerLead();
    private String fullName;
    private String nik;
    private String npwp;
    private String handphone;
    private String email;
    private String address;
    private String birthDate;
    private String information;
    private String gender;
    private String classification;
    private String responseString;
    private String type;
    private CheckBox checkShow;
    private CheckBox checkShowResult;
    private CheckBox checkShowCapture;
    private SimpleDateFormat sdf;
    private final Calendar calendar = Calendar.getInstance();
    private Calendar calendarBirth = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener birthDateListener;
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
    private String product;
    private String price;
    private String unit;
    private String buyDate;
    private CustomerLeadDetailService customerLeadDetailService;
    private EventActivityLog eventActivityLog = new EventActivityLog();

    public ActivityListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param event Parameter event.
     * @return A new instance of fragment ActivityListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivityListFragment newInstance(Event event) {
        ActivityListFragment fragment = new ActivityListFragment();
        Bundle args = new Bundle();
        args.putParcelable("event", event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
        }

        context = getContext();
        currentActivity = getActivity();

        customPreferences = new CustomPreferences(context);
        user = customPreferences.getUser();
        calendarBirth.add(Calendar.YEAR, -17);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity_list, container, false);
        spinnerActivityEvent = view.findViewById(R.id.spinner_activity_event);
        textEventName = view.findViewById(R.id.text_event_name);
        imageCapture = view.findViewById(R.id.image_capture);
        buttonBack = view.findViewById(R.id.button_back);
        buttonCapture = view.findViewById(R.id.button_capture);
        buttonNext = view.findViewById(R.id.button_next);
        buttonNext.setVisibility(View.GONE);

        buttonBack.setOnClickListener(this);
        buttonCapture.setOnClickListener(this);
        buttonNext.setOnClickListener(this);

        // customer view
        checkCustomer = view.findViewById(R.id.check_customer);
        layoutContentCustomer = view.findViewById(R.id.layout_content_customer);
        layoutDetail = view.findViewById(R.id.layout_detail);
        layoutContentCustomerDetail = view.findViewById(R.id.layout_content_customer_detail);
        layoutCapture = view.findViewById(R.id.layout_capture);
        layoutApartment = view.findViewById(R.id.layout_apartment);
        layoutEvent = view.findViewById(R.id.layout_event);
        layoutActivity = view.findViewById(R.id.layout_activity);
        checkShow = view.findViewById(R.id.check_show);
        checkShowResult = view.findViewById(R.id.check_show_result);
        checkShowCapture = view.findViewById(R.id.check_show_capture);
        spinnerApartment = view.findViewById(R.id.spinner_apartment);
        spinnerEvent = view.findViewById(R.id.spinner_event);
        spinnerActivity = view.findViewById(R.id.spinner_activity);
        buttonImport = view.findViewById(R.id.button_import);
        textFullName = view.findViewById(R.id.text_full_name);
        textBirthDate = view.findViewById(R.id.text_birth_date);
        textNik = view.findViewById(R.id.text_nik);
        textNpwp = view.findViewById(R.id.text_npwp);
        ccp = view.findViewById(R.id.ccp);
        textHandphone = view.findViewById(R.id.text_hand_phone);
        textEmail = view.findViewById(R.id.text_email);
        textAddress = view.findViewById(R.id.text_address);
        radioGroup = view.findViewById(R.id.radio_group);
        radioMale = view.findViewById(R.id.radio_male);
        radioFemale = view.findViewById(R.id.radio_female);

        spinnerClassification = view.findViewById(R.id.spinner_classification);
        spinnerResponse = view.findViewById(R.id.spinner_response);
        textInformation = view.findViewById(R.id.text_information);
        textBuyDate = view.findViewById(R.id.text_buy_date);
        spinnerProductType = view.findViewById(R.id.spinner_product_type);
        spinnerProduct = view.findViewById(R.id.spinner_product);
        textProduct = view.findViewById(R.id.text_product);
        textPrice = view.findViewById(R.id.text_price);
        textUnit = view.findViewById(R.id.text_unit);
        checkShow.setVisibility(View.GONE);
        layoutDetail.setVisibility(View.GONE);

        layoutContentCustomer.setVisibility(View.GONE);
        layoutContentCustomerDetail.setVisibility(View.GONE);
        layoutApartment.setVisibility(View.GONE);
        layoutEvent.setVisibility(View.GONE);
        layoutActivity.setVisibility(View.GONE);
        buttonImport.setOnClickListener(this);
        radioGroup.setOnClickListener(this);

        retrofit = Helpers.initRetrofit(context);
        activityService = retrofit.create(ActivityService.class);
        serviceImage = retrofit.create(ImageService.class);
        customerLeadService = retrofit.create(CustomerLeadService.class);
        customerLeadDetailService = retrofit.create(CustomerLeadDetailService.class);

        HashMap<String, String> map =new HashMap<>();
        if (event != null){
            textEventName.setText(event.getName());
//            map.put("event_id", String.valueOf(event.getId()));
//            map.put("apartment_id", String.valueOf(event.getApartmentId()));
        }

        if (user != null){
//            map.put("marketing_id", String.valueOf(user.getId()));
        }

        call = activityService.listActivity(map);
        call.enqueue(new Callback<ResponseActivityList>() {
            @Override
            public void onResponse(Call<ResponseActivityList> call, Response<ResponseActivityList> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getData() != null){
                        listActivity = response.body().getData();

                        ArrayList<String> list = new ArrayList<>();

                        for (Activity act: listActivity) {
                            list.add(act.getName());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(currentActivity,
                                android.R.layout.simple_spinner_dropdown_item, list);
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerActivityEvent.setAdapter(adapter);
                        spinnerActivityEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                Log.d(TAG, "onItemSelected:"+i);
                                activity = listActivity.get(i);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                activity = null;
                            }
                        });
                        Log.d(TAG, "listActivity"+listActivity.size());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseActivityList> call, Throwable t) {

            }
        });

        checkCustomer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                layoutContentCustomer.setVisibility(View.GONE);
                layoutDetail.setVisibility(View.GONE);
                layoutContentCustomerDetail.setVisibility(View.GONE);
                checkShow.setVisibility(View.GONE);
                if (b) {
                    checkShow.setVisibility(View.VISIBLE);
                    layoutContentCustomerDetail.setVisibility(View.VISIBLE);
                    if (checkShow.isChecked()){
                        layoutContentCustomer.setVisibility(View.VISIBLE);
                        layoutDetail.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        checkShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkCustomer.isChecked()){
                    if (checkShow.isChecked()){
                        layoutContentCustomer.setVisibility(View.VISIBLE);
                    } else {
                        layoutContentCustomer.setVisibility(View.GONE);
                    }
                }
            }
        });

        sdf = new SimpleDateFormat(Constants.DATE_FORMAT_LOCAL_S, Constants.locale);
        textBirthDate.setOnClickListener(this);
        birthDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendarBirth.set(Calendar.YEAR, year);
                calendarBirth.set(Calendar.MONTH, monthOfYear);
                calendarBirth.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                textBirthDate.setText(sdf.format(calendarBirth.getTime()));
            }
        };

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

        checkShowResult.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkCustomer.isChecked()){
                    if (checkShowResult.isChecked()){
                        layoutContentCustomerDetail.setVisibility(View.VISIBLE);
                    } else {
                        layoutContentCustomerDetail.setVisibility(View.GONE);
                    }
                }
            }
        });

        checkShowCapture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    imageCapture.setVisibility(View.VISIBLE);
                } else {
                    imageCapture.setVisibility(View.GONE);
                }
            }
        });

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
            currentActivity.setTitle(R.string.choose_activity);
            ((HideShowIconInterface) currentActivity).showBackIcon();
        }

        checkApplicationPermissions();
        checkGps();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_back){
            Helpers.moveFragment(context, new EventListFragment(), null);
        } else if (view.getId() == R.id.button_capture){
            captureCamera();
        } else if (view.getId() == R.id.button_next){
            processAction(view);
        } else if (view.getId() == buttonImport.getId()){
            importAction(view);
        } else if (view.getId() == textBirthDate.getId()){
            birthDateAction(view);
        } else if (view.getId() == textBuyDate.getId()){
            buyDateAction(view);
        }
    }

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

    private void nextAction() {
        if (validation()){
            final Activity activity = getActivityFromList();
            Bundle bundle = new Bundle();
            bundle.putParcelable("event", event);
            bundle.putParcelable("activity", activity);
            bundle.putDouble("latitude", latitude);
            bundle.putDouble("longitude", longitude);
            Helpers.moveFragment(context, new CaptureEventFragment(), bundle);
//            HashMap<String, String> map = new HashMap<>();
//            map.put("activity_id", String.valueOf(activity.getId()));
//            if (event != null){
//                map.put("event_id", String.valueOf(event.getId()));
//            }
//            Call<ResponseEventActivity> callEventActivity = activityService.addEventActivity(map);
//            callEventActivity.enqueue(new Callback<ResponseEventActivity>() {
//                @Override
//                public void onResponse(Call<ResponseEventActivity> call, Response<ResponseEventActivity> response) {
//                    if (response.isSuccessful()){
//                        if (response.body() != null && response.body().getData() != null){
//                            EventActivity eventActivity = response.body().getData();
//                            Bundle bundle = new Bundle();
//                            bundle.putParcelable("event_activity", eventActivity);
//                            bundle.putParcelable("event", event);
//                            bundle.putParcelable("activity", activity);
//                            bundle.putDouble("latitude", latitude);
//                            bundle.putDouble("longitude", longitude);
//                            Helpers.moveFragment(context, new CaptureEventFragment(), bundle);
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseEventActivity> call, Throwable t) {
//
//                }
//            });

        }
    }

    private boolean validation() {
        if (spinnerActivityEvent != null && listActivity.size() > 0){
            activity = listActivity.get(spinnerActivityEvent.getSelectedItemPosition());
        }
        if (activity != null) activityId = activity.getId();
        if (activityId <= 0){
            Helpers.showLongSnackbar(getView(), R.string.please_choose_activity);
            return false;
        }
        return true;
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

    private Activity getActivityFromList() {
        if (listActivity != null && listActivity.size() > 0){
            for (Activity activity: listActivity) {
                if (activity.getId() == activityId){
                    return activity;
                }
            }
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:"+requestCode+":"+resultCode);
        imageCapture.setVisibility(View.GONE);
        if (requestCode == Constants.PICK_IMAGE_CAMERA && resultCode == -1) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageCapture.setImageBitmap(imageBitmap);
            imageCaptured(imageBitmap);
            imageCapture.setVisibility(View.VISIBLE);
            layoutCapture.setVisibility(View.VISIBLE);
        } else if (requestCode == Constants.PICK_PHONE_NUMBER && resultCode == -1 && data != null && data.getData() != null) {
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
                if (number.trim().length() > 0) textHandphone.setText(number.replaceAll("^0+(?=.)", ""));
                if (displayName.trim().length() > 0) textFullName.setText(displayName);
                cursor.close();
                parseEmail(id);
                parseAddress(id);
            }
        }
    }

    private void checkApplicationPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(currentActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 102);
            }
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(currentActivity,
                    Manifest.permission.INTERNET)) {
                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{Manifest.permission.INTERNET}, 103);
            }
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(currentActivity,
                    Manifest.permission.ACCESS_WIFI_STATE)) {
                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 106);
            }
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(currentActivity,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{Manifest.permission.CAMERA}, 107);
            }
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(currentActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 108);
            }
        }
    }

    private void captureCamera(){
        checkApplicationPermissions();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, Constants.PICK_IMAGE_CAMERA);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    private void imageCaptured(Bitmap bitmap){
        activity = getActivityFromList();
        String activityStr = "";
        String eventStr = "";

        if (activity != null && activity.getId() > 0){
            activityStr = activity.getName();
        }
//        else if (activityName.length() > 0){
//            activityStr = activityName;
//        }

        if (event != null && event.getId() > 0){
            eventStr = event.getName();
        }
//        else if (eventName.length() > 0){
//            eventStr = eventName;
//        }

        String fileName = Helpers.replaceUniqueChar(eventStr, "_")+"_"+Helpers.replaceUniqueChar(activityStr, "_")+"_"+System.currentTimeMillis();
        fileName += ".jpg";
        buttonNext.setVisibility(View.GONE);
        try {
            File file = new ParseFileCapture(bitmap, fileName).execute().get();
            absolutePath = file.getAbsolutePath();
            buttonNext.setVisibility(View.VISIBLE);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onPictureTaken:"+absolutePath);
    }

    private void processAction(View view) {
        Log.d(TAG, "processAction");
        if (!validation()) return ;
        if (!customerLeadValidation()) return ;
        HashMap<String, String> map = new HashMap<>();
//        map.put("event_activity_id", String.valueOf(eventActivity.getId()));
        map.put("latitude", String.valueOf(latitude));
        map.put("longitude", String.valueOf(longitude));
        if (user != null){
            map.put("marketing_id", String.valueOf(user.getId()));
        }
        if (event != null){
            map.put("event_id", String.valueOf(event.getId()));
            map.put("apartment_id", String.valueOf(event.getApartmentId()));
        }
        if (activity != null){
            map.put("activity_id", String.valueOf(activity.getId()));
        }
        if (addressLocation != null){
            map.put("location", addressLocation);
        }
        if (eventActivityLog != null){
            map.put("id", String.valueOf(eventActivityLog.getId()));
        }
        Call<ResponseEventActivityLog> callActivity = activityService.addEventActivityLog(map);

        callActivity.enqueue(new Callback<ResponseEventActivityLog>() {
            @Override
            public void onResponse(Call<ResponseEventActivityLog> call, Response<ResponseEventActivityLog> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getData() != null){
                        eventActivityLog = response.body().getData();
                        if (absolutePath != null) uploadFile(eventActivityLog, absolutePath);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseEventActivityLog> call, Throwable t) {

            }
        });
    }

    private class ParseFileCapture extends AsyncTask<String, Integer, File> {
        private Bitmap bitmapImage;
        private String fileName;

        public ParseFileCapture(Bitmap bitmapImage, String fileName) {
            this.bitmapImage = bitmapImage;
            this.fileName = fileName;
        }

        @Override
        protected File doInBackground(String... strings) {
            Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            Boolean isSDSupportedDevice = Environment.isExternalStorageRemovable();
            File dirs = null;
            if(isSDSupportedDevice && isSDPresent) {
            } else {
            }
            dirs = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name)+"/events");

            if (!dirs.exists()){
                dirs.mkdirs();
            }

            // Create imageDir
            File myPath = new File(dirs,fileName);

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(myPath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null){
                        fos.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return myPath;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
//            progressBar.setVisibility(View.GONE);
//            floatingCapture.setImageDrawable(getResources().getDrawable(R.drawable.ic_undo_alt));
//            floatingCapture.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bootstrap_brand_success)));
//
//            buttonBack.show();
//            buttonNext.show();
        }
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
            Log.d(TAG, "getMyLocation:"+location.getLatitude()+","+location.getLongitude());
            addressLocation = Helpers.getCompleteAddressString(context, latitude, longitude);
        }
    }

    private void uploadFile(final EventActivityLog eventActivityLog, String path) {
        try {
            List<MultipartBody.Part> listPart = new ArrayList<>();
            List<RequestBody> listBody = new ArrayList<>();
            ArrayList<String> listCode = new ArrayList<>();

            HashMap<String, String> map = new HashMap<>();
            if (path != null){
                File file = new File(path);
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg;image/png"), file);

                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("images[]", file.getName(), requestBody);
                RequestBody fileName = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                listPart.add(fileToUpload);
                listBody.add(fileName);
                listCode.add("selfie");
            }

            if (listPart.size() > 0){
                Call<ResponseEventActivityLog> callServiceImage = serviceImage.eventactivitylog(eventActivityLog.getId(), listPart, listBody, listCode);

                callServiceImage.enqueue(new Callback<ResponseEventActivityLog>() {
                    @Override
                    public void onResponse(Call<ResponseEventActivityLog> call, Response<ResponseEventActivityLog> response) {
//                        progressBar.setProgress(100);
                        if (checkCustomer.isChecked()){
                            addCustomerLead(eventActivityLog);
                        } else {
                            successAction(eventActivityLog, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseEventActivityLog> call, Throwable t) {
                        t.printStackTrace();
//                        progressBar.setVisibility(View.GONE);
                        if (checkCustomer.isChecked()){
                            addCustomerLead(eventActivityLog);
                        } else {
                            successAction(eventActivityLog, null);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void successAction(EventActivityLog eventActivityLog, CustomerLead customerLead) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("event", event);
        bundle.putParcelable("activity", activity);
        bundle.putParcelable("event_activity_log", eventActivityLog);
        bundle.putParcelable("customer_lead", customerLead);
        bundle.putString("message", getString(R.string.event_activity_is_successfully_updated));
        Helpers.moveFragment(context, new EventActivityLogListFragment(), bundle);
    }

    private void addCustomerLead(final EventActivityLog eventActivityLog){
        HashMap<String, String> map = new HashMap<>();
        map.put("name", fullName);
        map.put("nik", nik);
        map.put("npwp", npwp);
        map.put("handphone", handphone);
        map.put("email", email);
        map.put("address", address);
        if (birthDate != null) map.put("birth_date", birthDate);
        map.put("marketing_id", String.valueOf(user.getId()));
        map.put("event_activity_log_id", String.valueOf(eventActivityLog.getId()));
        if (gender != null){
            map.put("gender", gender);
        }

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
                                map.put("price", price);
                                map.put("unit", unit);
                                if (buyDate != null) map.put("buy_date", buyDate);
                                customerLeadDetailService.add(map).enqueue(new Callback<ResponseCustomerLeadDetail>() {
                                    @Override
                                    public void onResponse(Call<ResponseCustomerLeadDetail> call, Response<ResponseCustomerLeadDetail> response) {
                                        successAction(eventActivityLog, customerLead);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseCustomerLeadDetail> call, Throwable t) {
                                        successAction(eventActivityLog, customerLead);
                                    }
                                });
                            } else {
                                successAction(eventActivityLog, null);
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
    private boolean customerLeadValidation() {
        if (!checkCustomer.isChecked()) return true;

        fullName = textFullName.getText().toString();
        nik = textNik.getText().toString();
        npwp = textNpwp.getText().toString();
        handphone = Helpers.formatPhone(textHandphone.getText().toString());
        email = textEmail.getText().toString();
        address = textAddress.getText().toString();
        if (textBirthDate.getText() != null){
            birthDate = Helpers.reformatDate(textBirthDate.getText().toString(), Constants.DATE_FORMAT_LOCAL_S, Constants.DATE_FORMAT_SQL);
        } else birthDate = null;
        information = textInformation.getText().toString();
        product = textProduct.getText().toString();
        price = textPrice.getText().toString();
        unit = textUnit.getText().toString();
        classification = spinnerClassification.getSelectedItem().toString();
        responseString = spinnerResponse.getSelectedItem().toString();
        type = spinnerProductType.getSelectedItem().toString();
        if (textBuyDate.getText() != null){
            buyDate = Helpers.reformatDate(textBuyDate.getText().toString(), Constants.DATE_FORMAT_LOCAL_S, Constants.DATE_FORMAT_SQL);
        } else buyDate = null;
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

    private void importAction(View view) {
        if (ContextCompat.checkSelfPermission(currentActivity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(currentActivity,
                    Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{Manifest.permission.READ_CONTACTS}, 107);
            }
            return;
        }
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(i, Constants.PICK_PHONE_NUMBER);
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
}
