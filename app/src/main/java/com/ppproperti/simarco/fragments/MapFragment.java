package com.ppproperti.simarco.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.adapters.ApartmentRecyclerViewAdapter;
import com.ppproperti.simarco.entities.Apartment;
import com.ppproperti.simarco.interfaces.ApartmentService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.responses.ResponseApartmentList;
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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = MapFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FusedLocationProviderClient fusedLocationClient;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Context context = getContext();
    private Activity activity = getActivity();
    private GoogleMap googleMap = null;
    private Marker myMarker;
    private List<Apartment> listApartment = new ArrayList<>();
    private Retrofit retrofit;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.our_location);
            ((HideShowIconInterface) activity).showHamburgerIcon();
        }

        checkApplicationPermissions();
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
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        markerMyLocation(location);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.clear(); //clear old markers

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

                        boolean animateTo = false;

                        for (Apartment apartment: listApartment) {
                            Log.d(TAG, "Apartment:"+new Gson().toJson(apartment));
                            if (apartment.getLatitude() != 0 && apartment.getLongitude() != 0){
                                addMarker(apartment.getLatitude(),apartment.getLongitude(), apartment.getName(), apartment.getAddress(), R.drawable.header_bg);

                                if (!animateTo) {
                                    animateTo = true;

                                    LatLng latLng = new LatLng(apartment.getLatitude(),apartment.getLongitude());

                                    CameraPosition cameraPosition = CameraPosition.builder()
                                            .target(latLng)
                                            .zoom(10)
                                            .bearing(0)
                                            .tilt(45)
                                            .build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);

                                    if (listApartment.size() > 1){
                                        fusedLocationClient.getLastLocation()
                                                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                                                    @Override
                                                    public void onSuccess(Location location) {
                                                        if (location != null){
                                                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                                                            CameraPosition cameraPosition = CameraPosition.builder()
                                                                    .target(latLng)
                                                                    .zoom(10)
                                                                    .bearing(0)
                                                                    .tilt(45)
                                                                    .build();
                                                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Log.d(ApartmentFragment.class.getSimpleName(), "onResponse:"+response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseApartmentList> call, Throwable t) {
                t.printStackTrace();
            }
        });

//        LatLng jogja = new LatLng(-7.8032076,110.3573354);
//
//        CameraPosition googlePlex = CameraPosition.builder()
//                .target(jogja)
//                .zoom(10)
//                .bearing(0)
//                .tilt(45)
//                .build();
//
//        addMarker(-7.7707475,110.4168131, "Tana Babarsari", "Jl. Babarsari TB 17/88, Tambak Bayan, Caturtunggal, Kec. Depok, Kabupaten Sleman, Daerah Istimewa Yogyakarta 55281", R.drawable.header_bg);
//
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);
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

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private MarkerOptions addMarker(double lat, double lng, String title, String desc, int icon){
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(lat, lng));
        markerOption.title(title);
        markerOption.snippet(desc);
//        markerOption.icon();
        googleMap.addMarker(markerOption);

        return markerOption;
    }

    private void markerMyLocation(Location location){
        if (myMarker != null){
            myMarker.remove();
        }

        myMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("My Location"));
    }



    private void checkApplicationPermissions() {
        Log.d(TAG, "checkApplicationPermissions:ACCESS_FINE_LOCATION:"+(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED));
        Log.d(TAG, "checkApplicationPermissions:ACCESS_COARSE_LOCATION:"+(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED));
        Log.d(TAG, "checkApplicationPermissions:INTERNET:"+(ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED));

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
    }
}
