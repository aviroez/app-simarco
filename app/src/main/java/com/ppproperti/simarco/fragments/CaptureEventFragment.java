package com.ppproperti.simarco.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.Activity;
import com.ppproperti.simarco.entities.Event;
import com.ppproperti.simarco.entities.EventActivity;
import com.ppproperti.simarco.entities.EventActivityLog;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.interfaces.ActivityService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.ImageService;
import com.ppproperti.simarco.responses.ResponseEventActivityLog;
import com.ppproperti.simarco.utils.Constants;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.GPSTracker;
import com.ppproperti.simarco.utils.Helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CaptureEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CaptureEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CaptureEventFragment extends Fragment implements View.OnClickListener, LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = CaptureEventFragment.class.getSimpleName();
    private static final String ARG_PARAM2 = "param2";
    private static final long MIN_TIME_BW_UPDATES = 5000 * 60; // 5 minutes
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Activity activity = new Activity();
    private Event event = new Event();
    private String activityName;
    private String eventName;
    private String startDate;
    private String endDate;
    private String addressActivity;
    private CameraView camera;
    private TextView textViewWatermark;
    private ProgressBar progressBar;
    private String absolutePath;
    private FloatingActionButton floatingCapture;
    private FloatingActionButton buttonBack;
    private FloatingActionButton buttonNext;
    private boolean capture;
    private Context context;
    private android.app.Activity currentActivity;
    private Retrofit retrofit;
    private ImageService serviceImage;
    private Call<ResponseEventActivityLog> call;
    private ActivityService activityService;
    private Call<ResponseEventActivityLog> callActivity;
    private double latitude;
    private double longitude;
    private EventActivity eventActivity;
    private Geocoder geocoder;
    private LocationManager locationManager;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private Location location;
    private GPSTracker gpsTracker;
    private User user;
    private String addressLocation;
//    private ImageView imageViewWatermark;

    public CaptureEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CaptureEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CaptureEventFragment newInstance(String param1, String param2) {
        CaptureEventFragment fragment = new CaptureEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activity = getArguments().getParcelable("activity");
            event = getArguments().getParcelable("event");
            activityName = getArguments().getString("activity_name");
            eventName = getArguments().getString("event_name");
            startDate = getArguments().getString("start_date");
            endDate = getArguments().getString("end_date");
            addressActivity = getArguments().getString("address");

            eventActivity = getArguments().getParcelable("event_activity");
//            latitude = getArguments().getDouble("latitude");
//            longitude = getArguments().getDouble("longitude");
        }

        context = getContext();
        currentActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_capture_event, container, false);
        camera = view.findViewById(R.id.camera);
        textViewWatermark = view.findViewById(R.id.text_view_watermark);
//        imageViewWatermark = view.findViewById(R.id.image_view_watermark);
        floatingCapture = view.findViewById(R.id.floating_capture);
        buttonBack = view.findViewById(R.id.floating_back);
        buttonNext = view.findViewById(R.id.floating_next);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        buttonBack.hide();
        buttonNext.hide();

        floatingCapture.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        camera.setLifecycleOwner(getViewLifecycleOwner());
        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                result.toBitmap(new BitmapCallback() {
                    @Override
                    public void onBitmapReady(@Nullable Bitmap bitmap) {
                        capture = true;
                        camera.close();
                        String activityStr = "";
                        String eventStr = "";

                        if (activity.getId() > 0){
                            activityStr = activity.getName();
                        } else if (activityName.length() > 0){
                            activityStr = activityName;
                        }

                        if (event.getId() > 0){
                            eventStr = event.getName();
                        } else if (eventName.length() > 0){
                            eventStr = eventName;
                        }

                        String fileName = Helpers.replaceUniqueChar(eventStr, "_")+"_"+Helpers.replaceUniqueChar(activityStr, "_")+"_"+System.currentTimeMillis();
                        fileName += ".jpg";
                        try {
                            File file = new ParseFileCapture(bitmap, fileName).execute().get();
                            absolutePath = file.getAbsolutePath();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onPictureTaken:"+absolutePath);
                    }
                });
            }
        });

        parseWatermark();

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
            currentActivity.setTitle(R.string.capture_event);
            ((HideShowIconInterface) currentActivity).showBackIcon();
        }

        checkApplicationPermissions();

        geocoder = new Geocoder(context);
        checkGps();

        CustomPreferences customPreferences = new CustomPreferences(context);
        user = customPreferences.getUser();

        retrofit = Helpers.initRetrofit(context);
        serviceImage = retrofit.create(ImageService.class);
        activityService = retrofit.create(ActivityService.class);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == floatingCapture.getId()){
            if (capture) {
                captureRefresh();
            } else {
                captureImage();
            }
        } else if(view.getId() == buttonBack.getId()){
            Bundle bundle = new Bundle();
            bundle.putParcelable("event", event);
            Helpers.moveFragment(context, new ActivityListFragment(), bundle);
        } else if(view.getId() == buttonNext.getId()){
            nextAction(view);
        }
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
        void onFragmentInteraction(Uri uri);
    }

    private void nextAction(View view) {
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
        callActivity = activityService.addEventActivityLog(map);

        callActivity.enqueue(new Callback<ResponseEventActivityLog>() {
            @Override
            public void onResponse(Call<ResponseEventActivityLog> call, Response<ResponseEventActivityLog> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getData() != null){
                        EventActivityLog log = response.body().getData();
                        uploadFile(log, absolutePath);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseEventActivityLog> call, Throwable t) {

            }
        });
    }

    private void captureImage() {
        progressBar.setVisibility(View.VISIBLE);

        if (camera.isOpened()){
            camera.takePicture();
        }
    }

    private void captureRefresh() {
        floatingCapture.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_retro));
        floatingCapture.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bootstrap_brand_danger)));

        Helpers.showLongSnackbar(getView(), R.string.capture_image_refreshed);

        if (!camera.isOpened()){
            camera.open();
        }
        capture = false;
    }

    private void parseWatermark() {
        String activityStr = "";
        String eventStr = "";
        String watermark ="";
        String timer = Helpers.formatDate(new Date(), Constants.DATE_TIME_FORMAT_LOCAL_M);

        if (eventActivity != null){
            if (eventActivity.getActivity() != null){
                this.activity = eventActivity.getActivity();
                if (eventActivity.getActivity().getId() > 0){
                    activityStr = eventActivity.getActivity().getName();
                } else if (activityName.length() > 0){
                    activityStr = activityName;
                }
            }
            if (eventActivity.getEvent() != null){
                this.event = eventActivity.getEvent();
                if (eventActivity.getEvent().getId() > 0){
                    eventStr = eventActivity.getEvent().getName();
                } else if (eventName.length() > 0){
                    eventStr = eventName;
                }
            }
        } else {
            if (event != null){
                eventStr = event.getName();
            }
            if (activity != null){
                activityStr = activity.getName();
            }
        }

        watermark = eventStr+": "+activityStr;
        watermark += "\n"+timer;
        textViewWatermark.setText(watermark);
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
            progressBar.setVisibility(View.GONE);
            floatingCapture.setImageDrawable(getResources().getDrawable(R.drawable.ic_undo_alt));
            floatingCapture.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bootstrap_brand_success)));

            buttonBack.show();
            buttonNext.show();
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
                call = serviceImage.eventactivitylog(eventActivityLog.getId(), listPart, listBody, listCode);

                call.enqueue(new Callback<ResponseEventActivityLog>() {
                    @Override
                    public void onResponse(Call<ResponseEventActivityLog> call, Response<ResponseEventActivityLog> response) {
                        progressBar.setProgress(100);
                        successAction(eventActivityLog);
                    }

                    @Override
                    public void onFailure(Call<ResponseEventActivityLog> call, Throwable t) {
                        t.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        successAction(eventActivityLog);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void successAction(EventActivityLog eventActivityLog) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("event", event);
        bundle.putParcelable("activity", activity);
        bundle.putParcelable("event_activity_log", eventActivityLog);
        bundle.putString("message", getString(R.string.event_activity_is_successfully_updated));
        Helpers.moveFragment(context, new EventActivityLogListFragment(), bundle);
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

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.d(TAG, "getLocation:"+isGPSEnabled+":"+isNetworkEnabled);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                Log.d(TAG, "getLocation:"+ Build.VERSION.SDK_INT+":"+Build.VERSION_CODES.M);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d(TAG, "getLocation:"+(context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)+":"+(context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED));
                    if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }
                }

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        getMyLocation(location);
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            getMyLocation(location);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
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
    }

}
