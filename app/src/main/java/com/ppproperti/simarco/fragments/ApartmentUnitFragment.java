package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.beardedhen.androidbootstrap.BootstrapBadge;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.adapters.ApartmentUnitRecyclerViewAdapter;
import com.ppproperti.simarco.entities.Apartment;
import com.ppproperti.simarco.entities.ApartmentFloor;
import com.ppproperti.simarco.entities.ApartmentTower;
import com.ppproperti.simarco.entities.ApartmentUnit;
import com.ppproperti.simarco.entities.Order;
import com.ppproperti.simarco.interfaces.ApartmentFloorService;
import com.ppproperti.simarco.interfaces.ApartmentService;
import com.ppproperti.simarco.interfaces.ApartmentUnitService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.OrderService;
import com.ppproperti.simarco.responses.ResponseApartmentFloorList;
import com.ppproperti.simarco.responses.ResponseApartmentList;
import com.ppproperti.simarco.responses.ResponseApartmentUnitList;
import com.ppproperti.simarco.responses.ResponseOrder;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

import java.util.ArrayList;
import java.util.Arrays;
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
public class ApartmentUnitFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    // TODO: Customize parameter argument names
    private static final String TAG = ApartmentUnitFragment.class.getSimpleName();
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private static Apartment apartment = new Apartment();
    private static ApartmentTower apartmentTower;
    private OnListFragmentInteractionListener listener;
    private Context context;
    private List<ApartmentUnit> listApartmentUnit = new ArrayList<>();
    private List<ApartmentUnit> listApartmentUnitGroup = new ArrayList<>();
    private ApartmentUnitRecyclerViewAdapter apartmentUnitAdapter;
    private CustomPreferences customPreferences;
    private String json;
    private Spinner spinnerView;
    private View scrollViewNorth;
    private View scrollViewSouth;
    private View scrollViewCustom;
    private BootstrapButton buttonClear;
    private BootstrapButton buttonProcess;
    private BootstrapButton lastButton = null;
    private ApartmentUnit lastUnit = null;
    private FragmentActivity activity;
    private int dangerCount = 0;
    private int emptyCount = 0;
    private int warningCount = 0;
    private int disabledCount = 0;
    private TableLayout customTableLayout;
    private Retrofit retrofit;
    private ApartmentUnitService apartmentUnitService;
    private ApartmentFloorService apartmentFloorService;
    private int page = 0;
    private List<ApartmentUnit> listUnitNumber = new ArrayList<>();
    private List<ApartmentFloor> listApartmentFloor = new ArrayList<>();
//    private ApartmentUnit selectedUnit = new ApartmentUnit();
    private BootstrapButton selectedButton = null;
    private OrderService orderService;
    private Order order = new Order();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ApartmentUnitFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ApartmentUnitFragment newInstance(int columnCount) {
        ApartmentUnitFragment fragment = new ApartmentUnitFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelable("apartment", apartment);
        args.putParcelable("apartmentTower", apartmentTower);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();

        if (getArguments() != null) {
            apartment = getArguments().getParcelable("apartment");
            apartmentTower = getArguments().getParcelable("apartmentTower");
        }
    }

    private void initLastOrder() {
        if (apartment == null) return;
        HashMap<String, String> map = new HashMap<>();
        map.put("apartment_id", String.valueOf(apartment.getId()));
        orderService.last(map).enqueue(new Callback<ResponseOrder>() {
            @Override
            public void onResponse(Call<ResponseOrder> call, Response<ResponseOrder> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        order = response.body().getData();

                        if (order != null){
                            new AlertDialog.Builder(context, R.style.AlertDialogTheme)
                                    .setTitle(R.string.confirmation)
                                    .setMessage(R.string.previous_order_already_exist)
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Bundle bundle = new Bundle();
                                            bundle.putParcelable("apartmentUnit", order.getApartmentUnit());
                                            bundle.putParcelable("apartment", order.getApartment());
                                            bundle.putParcelable("apartmentTower", order.getApartmentUnit().getApartmentTower());
                                            bundle.putParcelable("order", order);
                                            Helpers.moveFragment(context, new PaymentSchemaFragment(), bundle);
                                        }
                                    })

                                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })

                                    .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            orderService.delete(order.getId()).enqueue(new Callback<ResponseOrder>() {
                                                @Override
                                                public void onResponse(Call<ResponseOrder> call, Response<ResponseOrder> response) {

                                                }

                                                @Override
                                                public void onFailure(Call<ResponseOrder> call, Throwable t) {
                                                    t.printStackTrace();
                                                }
                                            });
                                            dialogInterface.dismiss();
                                        }
                                    })

                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseOrder> call, Throwable t) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apartment_unit_list, container, false);

        spinnerView = (Spinner) view.findViewById(R.id.spinner_view);
        scrollViewNorth = view.findViewById(R.id.scroll_view_north);
        scrollViewSouth = view.findViewById(R.id.scroll_view_south);
        scrollViewCustom = view.findViewById(R.id.scroll_view_custom);
        customTableLayout = view.findViewById(R.id.custom_table_layout);
        buttonClear = view.findViewById(R.id.button_clear);
        buttonProcess = view.findViewById(R.id.button_process);

        buttonClear.setOnClickListener(this);
        buttonProcess.setOnClickListener(this);

        scrollViewNorth.setVisibility(View.GONE);
        scrollViewSouth.setVisibility(View.GONE);
//        initButtons(view);

        spinnerView.setOnItemSelectedListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.choose_apartment_unit);
            ((HideShowIconInterface) activity).showBackIcon();
        }

        showView();

//        Retrofit retrofit = Helpers.initRetrofit(context);
//
//        final ApartmentService service = retrofit.create(ApartmentService.class);
//        final Map<String, String> params = new HashMap<>();
////        params.put("group", "1");
//        params.put("with[0]", "apartment_type");
//        params.put("with[1]", "apartment_view");
//        params.put("with[2]", "apartment_floor");
////        params.put("with[3]", "apartment_tower.apartment");
//        params.put("limit", "0");
//        if (apartmentTower != null){
//            params.put("apartment_tower_id", String.valueOf(apartmentTower.getId()));
//        }
//        Call<ResponseApartmentUnitList> call1 = service.listApartmentUnits(params);
//        call1.enqueue(new Callback<ResponseApartmentUnitList>() {
//            @Override
//            public void onResponse(Call<ResponseApartmentUnitList> call, Response<ResponseApartmentUnitList> response) {
//                if(response.isSuccessful()){
//                    if (response.body() != null && response.body().getData() != null){
//                        listApartmentUnit = response.body().getData();
////                        initStockUnits();
//                    }
//                } else {
//                    Log.d(TAG, "onResponse:"+response.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseApartmentUnitList> call, Throwable t) {
//                Log.d(TAG, "onResponse:onFailure:"+t.getMessage());
//                t.printStackTrace();
//            }
//        });
    }

    private void parseUnitGroup() {
        if (listApartmentUnitGroup.size() > 0 && listApartmentUnit.size() > 0){
            int index = 0;
            for (ApartmentUnit unitGroup: listApartmentUnitGroup){
                for (ApartmentUnit unit: listApartmentUnit) {
                    if (unit.getApartmentTowerId() == unitGroup.getApartmentTowerId()
                            && unit.getApartmentFloorId() == unitGroup.getApartmentFloorId()){
                        unitGroup.getGrouped().add(unit);
                        listApartmentUnitGroup.set(index, unitGroup);
                    }
                }
                index++;
            }
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        showView();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        showView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_clear){
            backAction();
        } else if(view.getId() == R.id.button_process) {
            processAction();
        } if (clickAction(view)){
            return;
        }
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
        void onListFragmentInteraction(ApartmentUnit apartmentUnit);
    }

    private void processAction() {
        if (lastUnit == null){
            if (getView() != null) {
                Helpers.showLongSnackbar(getView(), R.string.choose_apartment_unit_first);
            }
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable("apartmentUnit", lastUnit);
        bundle.putParcelable("apartment", apartment);
        bundle.putParcelable("apartmentTower", apartmentTower);
        Helpers.moveFragment(context, new PaymentSchemaFragment(), bundle);
    }

    private void clearAction() {
        if (lastButton != null){
            lastButton.setBootstrapBrand(DefaultBootstrapBrand.SECONDARY);
            lastButton = null;
            lastUnit = null;

            buttonProcess.setEnabled(false);
        }
    }

    private void backAction() {
        Helpers.moveFragment(context, new HomeFragment(), null);
    }

    private void showView(){
        customPreferences = new CustomPreferences(context, "session");

        if (apartmentTower != null){
            json = new Gson().toJson(apartmentTower);
            customPreferences.savePreferences("apartmentTower", json);
            Log.d(TAG, "onCreate:apartmentTower:"+json);
        } else {
            json = customPreferences.getParcelableJson("apartmentTower");
            apartmentTower = new Gson().fromJson(json, ApartmentTower.class);
            Log.d(TAG, "onCreate:json:"+json);
        }

        if (apartmentTower == null){
            Helpers.moveFragment(context, new ApartmentFragment(), null);
        }
        Log.d(TAG, "onCreate:"+json);
        retrofit = Helpers.initRetrofit(context);
        apartmentUnitService = retrofit.create(ApartmentUnitService.class);
        apartmentFloorService = retrofit.create(ApartmentFloorService.class);
        orderService = retrofit.create(OrderService.class);

        initLastOrder();

        HashMap<String, String> map = new HashMap<>();
        if (apartmentTower != null){
            map.put("apartment_tower_id", String.valueOf(apartmentTower.getId()));
        } else if (apartment != null){
            map.put("apartment_id", String.valueOf(apartment.getId()));
        }
        apartmentUnitService.number(map).enqueue(new Callback<ResponseApartmentUnitList>() {
            @Override
            public void onResponse(Call<ResponseApartmentUnitList> call, Response<ResponseApartmentUnitList> response) {
                if (response.isSuccessful()){
                    TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                    TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    if (response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
                        listUnitNumber = response.body().getData();
                        TableRow row = new TableRow(context);
                        row.setLayoutParams(tableParams);

                        if (listUnitNumber.size() > 0){
                            TextView textView = new TextView(context);
                            row.addView(textView);
                            for (ApartmentUnit apartmentUnit : listUnitNumber) {
                                textView = new TextView(context);
                                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                                textView.setLayoutParams(rowParams);
                                textView.setText(apartmentUnit.getUnitNumber());

                                row.addView(textView);
                            }

                            customTableLayout.addView(row);
                        }
                    }

                    page = 0;
                    initApartmentUnit();
                }
            }

            @Override
            public void onFailure(Call<ResponseApartmentUnitList> call, Throwable t) {

            }
        });
    }

    private void initButtons(View view){
        BootstrapButton button_1_1 = view.findViewById(R.id.button_1_1);
        BootstrapButton button_1_2 = view.findViewById(R.id.button_1_2);
        BootstrapButton button_1_3 = view.findViewById(R.id.button_1_3);
        BootstrapButton button_1_5 = view.findViewById(R.id.button_1_5);
        BootstrapButton button_1_6 = view.findViewById(R.id.button_1_6);
        BootstrapButton button_1_7 = view.findViewById(R.id.button_1_7);
        BootstrapButton button_1_8 = view.findViewById(R.id.button_1_8);
        BootstrapButton button_1_9 = view.findViewById(R.id.button_1_9);
        BootstrapButton button_1_10 = view.findViewById(R.id.button_1_10);
        BootstrapButton button_1_11 = view.findViewById(R.id.button_1_11);
        BootstrapButton button_1_12 = view.findViewById(R.id.button_1_12);
        BootstrapButton button_1_15 = view.findViewById(R.id.button_1_15);
        BootstrapButton button_1_16 = view.findViewById(R.id.button_1_16);
        BootstrapButton button_1_1e = view.findViewById(R.id.button_1_1e);
        BootstrapButton button_1_1d = view.findViewById(R.id.button_1_1d);
        BootstrapButton button_1_1c = view.findViewById(R.id.button_1_1c);
        BootstrapButton button_1_1b = view.findViewById(R.id.button_1_1b);
        BootstrapButton button_1_1a = view.findViewById(R.id.button_1_1a);

        BootstrapButton button_2_1 = view.findViewById(R.id.button_2_1);
        BootstrapButton button_2_2 = view.findViewById(R.id.button_2_2);
        BootstrapButton button_2_3 = view.findViewById(R.id.button_2_3);
        BootstrapButton button_2_5 = view.findViewById(R.id.button_2_5);
        BootstrapButton button_2_6 = view.findViewById(R.id.button_2_6);
        BootstrapButton button_2_7 = view.findViewById(R.id.button_2_7);
        BootstrapButton button_2_8 = view.findViewById(R.id.button_2_8);
        BootstrapButton button_2_9 = view.findViewById(R.id.button_2_9);
        BootstrapButton button_2_10 = view.findViewById(R.id.button_2_10);
        BootstrapButton button_2_11 = view.findViewById(R.id.button_2_11);
        BootstrapButton button_2_12 = view.findViewById(R.id.button_2_12);
        BootstrapButton button_2_15 = view.findViewById(R.id.button_2_15);
        BootstrapButton button_2_16 = view.findViewById(R.id.button_2_16);
        BootstrapButton button_2_17 = view.findViewById(R.id.button_2_17);
        BootstrapButton button_2_18 = view.findViewById(R.id.button_2_18);
        BootstrapButton button_2_19 = view.findViewById(R.id.button_2_19);
        BootstrapButton button_2_20 = view.findViewById(R.id.button_2_20);
        BootstrapButton button_2_21 = view.findViewById(R.id.button_2_21);
        BootstrapButton button_2_23 = view.findViewById(R.id.button_2_23);
        BootstrapButton button_2_25 = view.findViewById(R.id.button_2_25);
        BootstrapButton button_2_26 = view.findViewById(R.id.button_2_26);
        BootstrapButton button_2_27 = view.findViewById(R.id.button_2_27);
        BootstrapButton button_2_28 = view.findViewById(R.id.button_2_28);
        BootstrapButton button_2_29 = view.findViewById(R.id.button_2_29);
        BootstrapButton button_2_30 = view.findViewById(R.id.button_2_30);
        BootstrapButton button_2_32 = view.findViewById(R.id.button_2_32);
        BootstrapButton button_2_33 = view.findViewById(R.id.button_2_33);
        BootstrapButton button_2_35 = view.findViewById(R.id.button_2_35);

        BootstrapButton button_3_1 = view.findViewById(R.id.button_3_1);
        BootstrapButton button_3_2 = view.findViewById(R.id.button_3_2);
        BootstrapButton button_3_3 = view.findViewById(R.id.button_3_3);
        BootstrapButton button_3_5 = view.findViewById(R.id.button_3_5);
        BootstrapButton button_3_6 = view.findViewById(R.id.button_3_6);
        BootstrapButton button_3_7 = view.findViewById(R.id.button_3_7);
        BootstrapButton button_3_8 = view.findViewById(R.id.button_3_8);
        BootstrapButton button_3_9 = view.findViewById(R.id.button_3_9);
        BootstrapButton button_3_10 = view.findViewById(R.id.button_3_10);
        BootstrapButton button_3_11 = view.findViewById(R.id.button_3_11);
        BootstrapButton button_3_12 = view.findViewById(R.id.button_3_12);
        BootstrapButton button_3_15 = view.findViewById(R.id.button_3_15);
        BootstrapButton button_3_16 = view.findViewById(R.id.button_3_16);
        BootstrapButton button_3_17 = view.findViewById(R.id.button_3_17);
        BootstrapButton button_3_18 = view.findViewById(R.id.button_3_18);
        BootstrapButton button_3_19 = view.findViewById(R.id.button_3_19);
        BootstrapButton button_3_20 = view.findViewById(R.id.button_3_20);
        BootstrapButton button_3_21 = view.findViewById(R.id.button_3_21);
        BootstrapButton button_3_23 = view.findViewById(R.id.button_3_23);
        BootstrapButton button_3_25 = view.findViewById(R.id.button_3_25);
        BootstrapButton button_3_26 = view.findViewById(R.id.button_3_26);
        BootstrapButton button_3_27 = view.findViewById(R.id.button_3_27);
        BootstrapButton button_3_28 = view.findViewById(R.id.button_3_28);
        BootstrapButton button_3_29 = view.findViewById(R.id.button_3_29);
        BootstrapButton button_3_30 = view.findViewById(R.id.button_3_30);
        BootstrapButton button_3_32 = view.findViewById(R.id.button_3_32);
        BootstrapButton button_3_33 = view.findViewById(R.id.button_3_33);
        BootstrapButton button_3_35 = view.findViewById(R.id.button_3_35);

        BootstrapButton button_5_1 = view.findViewById(R.id.button_5_1);
        BootstrapButton button_5_2 = view.findViewById(R.id.button_5_2);
        BootstrapButton button_5_3 = view.findViewById(R.id.button_5_3);
        BootstrapButton button_5_5 = view.findViewById(R.id.button_5_5);
        BootstrapButton button_5_6 = view.findViewById(R.id.button_5_6);
        BootstrapButton button_5_7 = view.findViewById(R.id.button_5_7);
        BootstrapButton button_5_8 = view.findViewById(R.id.button_5_8);
        BootstrapButton button_5_9 = view.findViewById(R.id.button_5_9);
        BootstrapButton button_5_10 = view.findViewById(R.id.button_5_10);
        BootstrapButton button_5_11 = view.findViewById(R.id.button_5_11);
        BootstrapButton button_5_12 = view.findViewById(R.id.button_5_12);
        BootstrapButton button_5_15 = view.findViewById(R.id.button_5_15);
        BootstrapButton button_5_16 = view.findViewById(R.id.button_5_16);
        BootstrapButton button_5_17 = view.findViewById(R.id.button_5_17);
        BootstrapButton button_5_18 = view.findViewById(R.id.button_5_18);
        BootstrapButton button_5_19 = view.findViewById(R.id.button_5_19);
        BootstrapButton button_5_20 = view.findViewById(R.id.button_5_20);
        BootstrapButton button_5_21 = view.findViewById(R.id.button_5_21);
        BootstrapButton button_5_23 = view.findViewById(R.id.button_5_23);
        BootstrapButton button_5_25 = view.findViewById(R.id.button_5_25);
        BootstrapButton button_5_26 = view.findViewById(R.id.button_5_26);
        BootstrapButton button_5_27 = view.findViewById(R.id.button_5_27);
        BootstrapButton button_5_28 = view.findViewById(R.id.button_5_28);
        BootstrapButton button_5_29 = view.findViewById(R.id.button_5_29);
        BootstrapButton button_5_30 = view.findViewById(R.id.button_5_30);
        BootstrapButton button_5_32 = view.findViewById(R.id.button_5_32);
        BootstrapButton button_5_33 = view.findViewById(R.id.button_5_33);
        BootstrapButton button_5_35 = view.findViewById(R.id.button_5_35);

        BootstrapButton button_6_1 = view.findViewById(R.id.button_6_1);
        BootstrapButton button_6_2 = view.findViewById(R.id.button_6_2);
        BootstrapButton button_6_3 = view.findViewById(R.id.button_6_3);
        BootstrapButton button_6_5 = view.findViewById(R.id.button_6_5);
        BootstrapButton button_6_6 = view.findViewById(R.id.button_6_6);
        BootstrapButton button_6_7 = view.findViewById(R.id.button_6_7);
        BootstrapButton button_6_8 = view.findViewById(R.id.button_6_8);
        BootstrapButton button_6_9 = view.findViewById(R.id.button_6_9);
        BootstrapButton button_6_10 = view.findViewById(R.id.button_6_10);
        BootstrapButton button_6_11 = view.findViewById(R.id.button_6_11);
        BootstrapButton button_6_12 = view.findViewById(R.id.button_6_12);
        BootstrapButton button_6_15 = view.findViewById(R.id.button_6_15);
        BootstrapButton button_6_16 = view.findViewById(R.id.button_6_16);
        BootstrapButton button_6_17 = view.findViewById(R.id.button_6_17);
        BootstrapButton button_6_18 = view.findViewById(R.id.button_6_18);
        BootstrapButton button_6_19 = view.findViewById(R.id.button_6_19);
        BootstrapButton button_6_20 = view.findViewById(R.id.button_6_20);
        BootstrapButton button_6_21 = view.findViewById(R.id.button_6_21);
        BootstrapButton button_6_23 = view.findViewById(R.id.button_6_23);
        BootstrapButton button_6_25 = view.findViewById(R.id.button_6_25);
        BootstrapButton button_6_26 = view.findViewById(R.id.button_6_26);
        BootstrapButton button_6_27 = view.findViewById(R.id.button_6_27);
        BootstrapButton button_6_28 = view.findViewById(R.id.button_6_28);
        BootstrapButton button_6_29 = view.findViewById(R.id.button_6_29);
        BootstrapButton button_6_30 = view.findViewById(R.id.button_6_30);
        BootstrapButton button_6_32 = view.findViewById(R.id.button_6_32);
        BootstrapButton button_6_33 = view.findViewById(R.id.button_6_33);
        BootstrapButton button_6_35 = view.findViewById(R.id.button_6_35);

        BootstrapButton button_7_1 = view.findViewById(R.id.button_7_1);
        BootstrapButton button_7_2 = view.findViewById(R.id.button_7_2);
        BootstrapButton button_7_3 = view.findViewById(R.id.button_7_3);
        BootstrapButton button_7_5 = view.findViewById(R.id.button_7_5);
        BootstrapButton button_7_6 = view.findViewById(R.id.button_7_6);
        BootstrapButton button_7_7 = view.findViewById(R.id.button_7_7);
        BootstrapButton button_7_8 = view.findViewById(R.id.button_7_8);
        BootstrapButton button_7_9 = view.findViewById(R.id.button_7_9);
        BootstrapButton button_7_10 = view.findViewById(R.id.button_7_10);
        BootstrapButton button_7_11 = view.findViewById(R.id.button_7_11);
        BootstrapButton button_7_12 = view.findViewById(R.id.button_7_12);
        BootstrapButton button_7_15 = view.findViewById(R.id.button_7_15);
        BootstrapButton button_7_16 = view.findViewById(R.id.button_7_16);
        BootstrapButton button_7_17 = view.findViewById(R.id.button_7_17);
        BootstrapButton button_7_18 = view.findViewById(R.id.button_7_18);
        BootstrapButton button_7_19 = view.findViewById(R.id.button_7_19);
        BootstrapButton button_7_20 = view.findViewById(R.id.button_7_20);
        BootstrapButton button_7_21 = view.findViewById(R.id.button_7_21);
        BootstrapButton button_7_23 = view.findViewById(R.id.button_7_23);
        BootstrapButton button_7_25 = view.findViewById(R.id.button_7_25);
        BootstrapButton button_7_26 = view.findViewById(R.id.button_7_26);
        BootstrapButton button_7_27 = view.findViewById(R.id.button_7_27);
        BootstrapButton button_7_28 = view.findViewById(R.id.button_7_28);
        BootstrapButton button_7_29 = view.findViewById(R.id.button_7_29);
        BootstrapButton button_7_30 = view.findViewById(R.id.button_7_30);
        BootstrapButton button_7_32 = view.findViewById(R.id.button_7_32);
        BootstrapButton button_7_33 = view.findViewById(R.id.button_7_33);
        BootstrapButton button_7_35 = view.findViewById(R.id.button_7_35);

        BootstrapButton button_8_1 = view.findViewById(R.id.button_8_1);
        BootstrapButton button_8_2 = view.findViewById(R.id.button_8_2);
        BootstrapButton button_8_3 = view.findViewById(R.id.button_8_3);
        BootstrapButton button_8_5 = view.findViewById(R.id.button_8_5);
        BootstrapButton button_8_6 = view.findViewById(R.id.button_8_6);
        BootstrapButton button_8_7 = view.findViewById(R.id.button_8_7);
        BootstrapButton button_8_8 = view.findViewById(R.id.button_8_8);
        BootstrapButton button_8_9 = view.findViewById(R.id.button_8_9);
        BootstrapButton button_8_10 = view.findViewById(R.id.button_8_10);
        BootstrapButton button_8_11 = view.findViewById(R.id.button_8_11);
        BootstrapButton button_8_12 = view.findViewById(R.id.button_8_12);
        BootstrapButton button_8_15 = view.findViewById(R.id.button_8_15);
        BootstrapButton button_8_16 = view.findViewById(R.id.button_8_16);
        BootstrapButton button_8_17 = view.findViewById(R.id.button_8_17);
        BootstrapButton button_8_18 = view.findViewById(R.id.button_8_18);
        BootstrapButton button_8_19 = view.findViewById(R.id.button_8_19);
        BootstrapButton button_8_20 = view.findViewById(R.id.button_8_20);
        BootstrapButton button_8_21 = view.findViewById(R.id.button_8_21);
        BootstrapButton button_8_23 = view.findViewById(R.id.button_8_23);
        BootstrapButton button_8_25 = view.findViewById(R.id.button_8_25);
        BootstrapButton button_8_26 = view.findViewById(R.id.button_8_26);
        BootstrapButton button_8_27 = view.findViewById(R.id.button_8_27);
        BootstrapButton button_8_28 = view.findViewById(R.id.button_8_28);
        BootstrapButton button_8_29 = view.findViewById(R.id.button_8_29);
        BootstrapButton button_8_30 = view.findViewById(R.id.button_8_30);
        BootstrapButton button_8_32 = view.findViewById(R.id.button_8_32);
        BootstrapButton button_8_33 = view.findViewById(R.id.button_8_33);
        BootstrapButton button_8_35 = view.findViewById(R.id.button_8_35);

        BootstrapButton button_9_1 = view.findViewById(R.id.button_9_1);
        BootstrapButton button_9_2 = view.findViewById(R.id.button_9_2);
        BootstrapButton button_9_3 = view.findViewById(R.id.button_9_3);
        BootstrapButton button_9_5 = view.findViewById(R.id.button_9_5);
        BootstrapButton button_9_6 = view.findViewById(R.id.button_9_6);
        BootstrapButton button_9_7 = view.findViewById(R.id.button_9_7);
        BootstrapButton button_9_8 = view.findViewById(R.id.button_9_8);
        BootstrapButton button_9_9 = view.findViewById(R.id.button_9_9);
        BootstrapButton button_9_10 = view.findViewById(R.id.button_9_10);
        BootstrapButton button_9_11 = view.findViewById(R.id.button_9_11);
        BootstrapButton button_9_12 = view.findViewById(R.id.button_9_12);
        BootstrapButton button_9_15 = view.findViewById(R.id.button_9_15);
        BootstrapButton button_9_16 = view.findViewById(R.id.button_9_16);
        BootstrapButton button_9_17 = view.findViewById(R.id.button_9_17);
        BootstrapButton button_9_18 = view.findViewById(R.id.button_9_18);
        BootstrapButton button_9_19 = view.findViewById(R.id.button_9_19);
        BootstrapButton button_9_20 = view.findViewById(R.id.button_9_20);
        BootstrapButton button_9_21 = view.findViewById(R.id.button_9_21);
        BootstrapButton button_9_23 = view.findViewById(R.id.button_9_23);
        BootstrapButton button_9_25 = view.findViewById(R.id.button_9_25);
        BootstrapButton button_9_26 = view.findViewById(R.id.button_9_26);
        BootstrapButton button_9_27 = view.findViewById(R.id.button_9_27);
        BootstrapButton button_9_28 = view.findViewById(R.id.button_9_28);
        BootstrapButton button_9_29 = view.findViewById(R.id.button_9_29);
        BootstrapButton button_9_30 = view.findViewById(R.id.button_9_30);
        BootstrapButton button_9_32 = view.findViewById(R.id.button_9_32);
        BootstrapButton button_9_33 = view.findViewById(R.id.button_9_33);
        BootstrapButton button_9_35 = view.findViewById(R.id.button_9_35);

        BootstrapButton button_10_1 = view.findViewById(R.id.button_10_1);
        BootstrapButton button_10_2 = view.findViewById(R.id.button_10_2);
        BootstrapButton button_10_3 = view.findViewById(R.id.button_10_3);
        BootstrapButton button_10_5 = view.findViewById(R.id.button_10_5);
        BootstrapButton button_10_6 = view.findViewById(R.id.button_10_6);
        BootstrapButton button_10_7 = view.findViewById(R.id.button_10_7);
        BootstrapButton button_10_8 = view.findViewById(R.id.button_10_8);
        BootstrapButton button_10_9 = view.findViewById(R.id.button_10_9);
        BootstrapButton button_10_10 = view.findViewById(R.id.button_10_10);
        BootstrapButton button_10_11 = view.findViewById(R.id.button_10_11);
        BootstrapButton button_10_12 = view.findViewById(R.id.button_10_12);
        BootstrapButton button_10_15 = view.findViewById(R.id.button_10_15);
        BootstrapButton button_10_16 = view.findViewById(R.id.button_10_16);
        BootstrapButton button_10_17 = view.findViewById(R.id.button_10_17);
        BootstrapButton button_10_18 = view.findViewById(R.id.button_10_18);
        BootstrapButton button_10_19 = view.findViewById(R.id.button_10_19);
        BootstrapButton button_10_20 = view.findViewById(R.id.button_10_20);
        BootstrapButton button_10_21 = view.findViewById(R.id.button_10_21);
        BootstrapButton button_10_23 = view.findViewById(R.id.button_10_23);
        BootstrapButton button_10_25 = view.findViewById(R.id.button_10_25);
        BootstrapButton button_10_26 = view.findViewById(R.id.button_10_26);
        BootstrapButton button_10_27 = view.findViewById(R.id.button_10_27);
        BootstrapButton button_10_28 = view.findViewById(R.id.button_10_28);
        BootstrapButton button_10_29 = view.findViewById(R.id.button_10_29);
        BootstrapButton button_10_30 = view.findViewById(R.id.button_10_30);
        BootstrapButton button_10_32 = view.findViewById(R.id.button_10_32);
        BootstrapButton button_10_33 = view.findViewById(R.id.button_10_33);
        BootstrapButton button_10_35 = view.findViewById(R.id.button_10_35);

        BootstrapButton button_11_1 = view.findViewById(R.id.button_11_1);
        BootstrapButton button_11_2 = view.findViewById(R.id.button_11_2);
        BootstrapButton button_11_3 = view.findViewById(R.id.button_11_3);
        BootstrapButton button_11_5 = view.findViewById(R.id.button_11_5);
        BootstrapButton button_11_6 = view.findViewById(R.id.button_11_6);
        BootstrapButton button_11_7 = view.findViewById(R.id.button_11_7);
        BootstrapButton button_11_8 = view.findViewById(R.id.button_11_8);
        BootstrapButton button_11_9 = view.findViewById(R.id.button_11_9);
        BootstrapButton button_11_10 = view.findViewById(R.id.button_11_10);
        BootstrapButton button_11_11 = view.findViewById(R.id.button_11_11);
        BootstrapButton button_11_12 = view.findViewById(R.id.button_11_12);
        BootstrapButton button_11_15 = view.findViewById(R.id.button_11_15);
        BootstrapButton button_11_16 = view.findViewById(R.id.button_11_16);
        BootstrapButton button_11_17 = view.findViewById(R.id.button_11_17);
        BootstrapButton button_11_18 = view.findViewById(R.id.button_11_18);
        BootstrapButton button_11_19 = view.findViewById(R.id.button_11_19);
        BootstrapButton button_11_20 = view.findViewById(R.id.button_11_20);
        BootstrapButton button_11_21 = view.findViewById(R.id.button_11_21);
        BootstrapButton button_11_23 = view.findViewById(R.id.button_11_23);
        BootstrapButton button_11_25 = view.findViewById(R.id.button_11_25);
        BootstrapButton button_11_26 = view.findViewById(R.id.button_11_26);
        BootstrapButton button_11_27 = view.findViewById(R.id.button_11_27);
        BootstrapButton button_11_28 = view.findViewById(R.id.button_11_28);
        BootstrapButton button_11_29 = view.findViewById(R.id.button_11_29);
        BootstrapButton button_11_30 = view.findViewById(R.id.button_11_30);
        BootstrapButton button_11_32 = view.findViewById(R.id.button_11_32);
        BootstrapButton button_11_33 = view.findViewById(R.id.button_11_33);
        BootstrapButton button_11_35 = view.findViewById(R.id.button_11_35);

        BootstrapButton button_12_1 = view.findViewById(R.id.button_12_1);
        BootstrapButton button_12_2 = view.findViewById(R.id.button_12_2);
        BootstrapButton button_12_3 = view.findViewById(R.id.button_12_3);
        BootstrapButton button_12_5 = view.findViewById(R.id.button_12_5);
        BootstrapButton button_12_6 = view.findViewById(R.id.button_12_6);
        BootstrapButton button_12_7 = view.findViewById(R.id.button_12_7);
        BootstrapButton button_12_8 = view.findViewById(R.id.button_12_8);
        BootstrapButton button_12_9 = view.findViewById(R.id.button_12_9);
        BootstrapButton button_12_10 = view.findViewById(R.id.button_12_10);
        BootstrapButton button_12_11 = view.findViewById(R.id.button_12_11);
        BootstrapButton button_12_12 = view.findViewById(R.id.button_12_12);
        BootstrapButton button_12_15 = view.findViewById(R.id.button_12_15);
        BootstrapButton button_12_16 = view.findViewById(R.id.button_12_16);
        BootstrapButton button_12_17 = view.findViewById(R.id.button_12_17);
        BootstrapButton button_12_18 = view.findViewById(R.id.button_12_18);
        BootstrapButton button_12_19 = view.findViewById(R.id.button_12_19);
        BootstrapButton button_12_20 = view.findViewById(R.id.button_12_20);
        BootstrapButton button_12_21 = view.findViewById(R.id.button_12_21);
        BootstrapButton button_12_23 = view.findViewById(R.id.button_12_23);
        BootstrapButton button_12_25 = view.findViewById(R.id.button_12_25);
        BootstrapButton button_12_26 = view.findViewById(R.id.button_12_26);
        BootstrapButton button_12_27 = view.findViewById(R.id.button_12_27);
        BootstrapButton button_12_28 = view.findViewById(R.id.button_12_28);
        BootstrapButton button_12_29 = view.findViewById(R.id.button_12_29);
        BootstrapButton button_12_30 = view.findViewById(R.id.button_12_30);
        BootstrapButton button_12_32 = view.findViewById(R.id.button_12_32);
        BootstrapButton button_12_33 = view.findViewById(R.id.button_12_33);
        BootstrapButton button_12_35 = view.findViewById(R.id.button_12_35);


        button_1_1.setOnClickListener(this);
        button_1_2.setOnClickListener(this);
        button_1_3.setOnClickListener(this);
        button_1_5.setOnClickListener(this);
        button_1_6.setOnClickListener(this);
        button_1_7.setOnClickListener(this);
        button_1_8.setOnClickListener(this);
        button_1_9.setOnClickListener(this);
        button_1_10.setOnClickListener(this);
        button_1_11.setOnClickListener(this);
        button_1_12.setOnClickListener(this);
        button_1_15.setOnClickListener(this);
        button_1_16.setOnClickListener(this);
        button_1_1e.setOnClickListener(this);
        button_1_1d.setOnClickListener(this);
        button_1_1c.setOnClickListener(this);
        button_1_1b.setOnClickListener(this);
        button_1_1a.setOnClickListener(this);

        button_2_1.setOnClickListener(this);
        button_2_2.setOnClickListener(this);
        button_2_3.setOnClickListener(this);
        button_2_5.setOnClickListener(this);
        button_2_6.setOnClickListener(this);
        button_2_7.setOnClickListener(this);
        button_2_8.setOnClickListener(this);
        button_2_9.setOnClickListener(this);
        button_2_10.setOnClickListener(this);
        button_2_11.setOnClickListener(this);
        button_2_12.setOnClickListener(this);
        button_2_15.setOnClickListener(this);
        button_2_16.setOnClickListener(this);
        button_2_17.setOnClickListener(this);
        button_2_18.setOnClickListener(this);
        button_2_19.setOnClickListener(this);
        button_2_20.setOnClickListener(this);
        button_2_21.setOnClickListener(this);
        button_2_23.setOnClickListener(this);
        button_2_25.setOnClickListener(this);
        button_2_26.setOnClickListener(this);
        button_2_27.setOnClickListener(this);
        button_2_28.setOnClickListener(this);
        button_2_29.setOnClickListener(this);
        button_2_30.setOnClickListener(this);
        button_2_32.setOnClickListener(this);
        button_2_33.setOnClickListener(this);
        button_2_35.setOnClickListener(this);

        button_3_1.setOnClickListener(this);
        button_3_2.setOnClickListener(this);
        button_3_3.setOnClickListener(this);
        button_3_5.setOnClickListener(this);
        button_3_6.setOnClickListener(this);
        button_3_7.setOnClickListener(this);
        button_3_8.setOnClickListener(this);
        button_3_9.setOnClickListener(this);
        button_3_10.setOnClickListener(this);
        button_3_11.setOnClickListener(this);
        button_3_12.setOnClickListener(this);
        button_3_15.setOnClickListener(this);
        button_3_16.setOnClickListener(this);
        button_3_17.setOnClickListener(this);
        button_3_18.setOnClickListener(this);
        button_3_19.setOnClickListener(this);
        button_3_20.setOnClickListener(this);
        button_3_21.setOnClickListener(this);
        button_3_23.setOnClickListener(this);
        button_3_25.setOnClickListener(this);
        button_3_26.setOnClickListener(this);
        button_3_27.setOnClickListener(this);
        button_3_28.setOnClickListener(this);
        button_3_29.setOnClickListener(this);
        button_3_30.setOnClickListener(this);
        button_3_32.setOnClickListener(this);
        button_3_33.setOnClickListener(this);
        button_3_35.setOnClickListener(this);

        button_5_1.setOnClickListener(this);
        button_5_2.setOnClickListener(this);
        button_5_3.setOnClickListener(this);
        button_5_5.setOnClickListener(this);
        button_5_6.setOnClickListener(this);
        button_5_7.setOnClickListener(this);
        button_5_8.setOnClickListener(this);
        button_5_9.setOnClickListener(this);
        button_5_10.setOnClickListener(this);
        button_5_11.setOnClickListener(this);
        button_5_12.setOnClickListener(this);
        button_5_15.setOnClickListener(this);
        button_5_16.setOnClickListener(this);
        button_5_17.setOnClickListener(this);
        button_5_18.setOnClickListener(this);
        button_5_19.setOnClickListener(this);
        button_5_20.setOnClickListener(this);
        button_5_21.setOnClickListener(this);
        button_5_23.setOnClickListener(this);
        button_5_25.setOnClickListener(this);
        button_5_26.setOnClickListener(this);
        button_5_27.setOnClickListener(this);
        button_5_28.setOnClickListener(this);
        button_5_29.setOnClickListener(this);
        button_5_30.setOnClickListener(this);
        button_5_32.setOnClickListener(this);
        button_5_33.setOnClickListener(this);
        button_5_35.setOnClickListener(this);

        button_6_1.setOnClickListener(this);
        button_6_2.setOnClickListener(this);
        button_6_3.setOnClickListener(this);
        button_6_5.setOnClickListener(this);
        button_6_6.setOnClickListener(this);
        button_6_7.setOnClickListener(this);
        button_6_8.setOnClickListener(this);
        button_6_9.setOnClickListener(this);
        button_6_10.setOnClickListener(this);
        button_6_11.setOnClickListener(this);
        button_6_12.setOnClickListener(this);
        button_6_15.setOnClickListener(this);
        button_6_16.setOnClickListener(this);
        button_6_17.setOnClickListener(this);
        button_6_18.setOnClickListener(this);
        button_6_19.setOnClickListener(this);
        button_6_20.setOnClickListener(this);
        button_6_21.setOnClickListener(this);
        button_6_23.setOnClickListener(this);
        button_6_25.setOnClickListener(this);
        button_6_26.setOnClickListener(this);
        button_6_27.setOnClickListener(this);
        button_6_28.setOnClickListener(this);
        button_6_29.setOnClickListener(this);
        button_6_30.setOnClickListener(this);
        button_6_32.setOnClickListener(this);
        button_6_33.setOnClickListener(this);
        button_6_35.setOnClickListener(this);

        button_7_1.setOnClickListener(this);
        button_7_2.setOnClickListener(this);
        button_7_3.setOnClickListener(this);
        button_7_5.setOnClickListener(this);
        button_7_6.setOnClickListener(this);
        button_7_7.setOnClickListener(this);
        button_7_8.setOnClickListener(this);
        button_7_9.setOnClickListener(this);
        button_7_10.setOnClickListener(this);
        button_7_11.setOnClickListener(this);
        button_7_12.setOnClickListener(this);
        button_7_15.setOnClickListener(this);
        button_7_16.setOnClickListener(this);
        button_7_17.setOnClickListener(this);
        button_7_18.setOnClickListener(this);
        button_7_19.setOnClickListener(this);
        button_7_20.setOnClickListener(this);
        button_7_21.setOnClickListener(this);
        button_7_23.setOnClickListener(this);
        button_7_25.setOnClickListener(this);
        button_7_26.setOnClickListener(this);
        button_7_27.setOnClickListener(this);
        button_7_28.setOnClickListener(this);
        button_7_29.setOnClickListener(this);
        button_7_30.setOnClickListener(this);
        button_7_32.setOnClickListener(this);
        button_7_33.setOnClickListener(this);
        button_7_35.setOnClickListener(this);

        button_8_1.setOnClickListener(this);
        button_8_2.setOnClickListener(this);
        button_8_3.setOnClickListener(this);
        button_8_5.setOnClickListener(this);
        button_8_6.setOnClickListener(this);
        button_8_7.setOnClickListener(this);
        button_8_8.setOnClickListener(this);
        button_8_9.setOnClickListener(this);
        button_8_10.setOnClickListener(this);
        button_8_11.setOnClickListener(this);
        button_8_12.setOnClickListener(this);
        button_8_15.setOnClickListener(this);
        button_8_16.setOnClickListener(this);
        button_8_17.setOnClickListener(this);
        button_8_18.setOnClickListener(this);
        button_8_19.setOnClickListener(this);
        button_8_20.setOnClickListener(this);
        button_8_21.setOnClickListener(this);
        button_8_23.setOnClickListener(this);
        button_8_25.setOnClickListener(this);
        button_8_26.setOnClickListener(this);
        button_8_27.setOnClickListener(this);
        button_8_28.setOnClickListener(this);
        button_8_29.setOnClickListener(this);
        button_8_30.setOnClickListener(this);
        button_8_32.setOnClickListener(this);
        button_8_33.setOnClickListener(this);
        button_8_35.setOnClickListener(this);

//        button_9_1.setOnClickListener(this);
//        button_9_2.setOnClickListener(this);
//        button_9_3.setOnClickListener(this);
//        button_9_5.setOnClickListener(this);
//        button_9_6.setOnClickListener(this);
//        button_9_7.setOnClickListener(this);
//        button_9_8.setOnClickListener(this);
//        button_9_9.setOnClickListener(this);
//        button_9_10.setOnClickListener(this);
//        button_9_11.setOnClickListener(this);
//        button_9_12.setOnClickListener(this);
//        button_9_15.setOnClickListener(this);
//        button_9_16.setOnClickListener(this);
//        button_9_17.setOnClickListener(this);
//        button_9_18.setOnClickListener(this);
//        button_9_19.setOnClickListener(this);
//        button_9_20.setOnClickListener(this);
//        button_9_21.setOnClickListener(this);
//        button_9_23.setOnClickListener(this);
//        button_9_25.setOnClickListener(this);
//        button_9_26.setOnClickListener(this);
//        button_9_27.setOnClickListener(this);
//        button_9_28.setOnClickListener(this);
//        button_9_29.setOnClickListener(this);
//        button_9_30.setOnClickListener(this);
//        button_9_32.setOnClickListener(this);
//        button_9_33.setOnClickListener(this);
//        button_9_35.setOnClickListener(this);

        button_10_1.setOnClickListener(this);
        button_10_2.setOnClickListener(this);
        button_10_3.setOnClickListener(this);
        button_10_5.setOnClickListener(this);
        button_10_6.setOnClickListener(this);
        button_10_7.setOnClickListener(this);
        button_10_8.setOnClickListener(this);
        button_10_9.setOnClickListener(this);
        button_10_10.setOnClickListener(this);
        button_10_11.setOnClickListener(this);
        button_10_12.setOnClickListener(this);
        button_10_15.setOnClickListener(this);
        button_10_16.setOnClickListener(this);
        button_10_17.setOnClickListener(this);
        button_10_18.setOnClickListener(this);
        button_10_19.setOnClickListener(this);
        button_10_20.setOnClickListener(this);
        button_10_21.setOnClickListener(this);
        button_10_23.setOnClickListener(this);
        button_10_25.setOnClickListener(this);
        button_10_26.setOnClickListener(this);
        button_10_27.setOnClickListener(this);
        button_10_28.setOnClickListener(this);
        button_10_29.setOnClickListener(this);
        button_10_30.setOnClickListener(this);
        button_10_32.setOnClickListener(this);
        button_10_33.setOnClickListener(this);
        button_10_35.setOnClickListener(this);

//        button_11_1.setOnClickListener(this);
//        button_11_2.setOnClickListener(this);
//        button_11_3.setOnClickListener(this);
//        button_11_5.setOnClickListener(this);
//        button_11_6.setOnClickListener(this);
//        button_11_7.setOnClickListener(this);
//        button_11_8.setOnClickListener(this);
//        button_11_9.setOnClickListener(this);
//        button_11_10.setOnClickListener(this);
//        button_11_11.setOnClickListener(this);
//        button_11_12.setOnClickListener(this);
//        button_11_15.setOnClickListener(this);
//        button_11_16.setOnClickListener(this);
//        button_11_17.setOnClickListener(this);
//        button_11_18.setOnClickListener(this);
//        button_11_19.setOnClickListener(this);
//        button_11_20.setOnClickListener(this);
//        button_11_21.setOnClickListener(this);
//        button_11_23.setOnClickListener(this);
//        button_11_25.setOnClickListener(this);
//        button_11_26.setOnClickListener(this);
//        button_11_27.setOnClickListener(this);
//        button_11_28.setOnClickListener(this);
//        button_11_29.setOnClickListener(this);
//        button_11_30.setOnClickListener(this);
//        button_11_32.setOnClickListener(this);
//        button_11_33.setOnClickListener(this);
//        button_11_35.setOnClickListener(this);

        button_12_1.setOnClickListener(this);
        button_12_2.setOnClickListener(this);
        button_12_3.setOnClickListener(this);
        button_12_5.setOnClickListener(this);
        button_12_6.setOnClickListener(this);
        button_12_7.setOnClickListener(this);
        button_12_8.setOnClickListener(this);
        button_12_9.setOnClickListener(this);
        button_12_10.setOnClickListener(this);
        button_12_11.setOnClickListener(this);
        button_12_12.setOnClickListener(this);
        button_12_15.setOnClickListener(this);
        button_12_16.setOnClickListener(this);
        button_12_17.setOnClickListener(this);
        button_12_18.setOnClickListener(this);
        button_12_19.setOnClickListener(this);
        button_12_20.setOnClickListener(this);
        button_12_21.setOnClickListener(this);
        button_12_23.setOnClickListener(this);
        button_12_25.setOnClickListener(this);
        button_12_26.setOnClickListener(this);
        button_12_27.setOnClickListener(this);
        button_12_28.setOnClickListener(this);
        button_12_29.setOnClickListener(this);
        button_12_30.setOnClickListener(this);
        button_12_32.setOnClickListener(this);
        button_12_33.setOnClickListener(this);
        button_12_35.setOnClickListener(this);
    }

    private boolean clickAction(View view){
        if (R.id.button_1_1 == view.getId()) {
            return checkStockUnit(1, "1", R.id.button_1_1);
        } else if (R.id.button_1_2 == view.getId()) {
            return checkStockUnit(1, "2", R.id.button_1_2);
        } else if (R.id.button_1_3 == view.getId()) {
            return checkStockUnit(1, "6", R.id.button_1_3);
        } else if (R.id.button_1_5 == view.getId()) {
            return checkStockUnit(1, "5", R.id.button_1_5);
        } else if (R.id.button_1_6 == view.getId()) {
            return checkStockUnit(1, "6", R.id.button_1_6);
        } else if (R.id.button_1_7 == view.getId()) {
            return checkStockUnit(1, "7", R.id.button_1_7);
        } else if (R.id.button_1_8 == view.getId()) {
            return checkStockUnit(1, "8", R.id.button_1_8);
        } else if (R.id.button_1_9 == view.getId()) {
            return checkStockUnit(1, "9", R.id.button_1_9);
        } else if (R.id.button_1_10 == view.getId()) {
            return checkStockUnit(1, "10", R.id.button_1_10);
        } else if (R.id.button_1_11 == view.getId()) {
            return checkStockUnit(1, "11", R.id.button_1_11);
        } else if (R.id.button_1_12 == view.getId()) {
            return checkStockUnit(1, "12", R.id.button_1_12);
        } else if (R.id.button_1_15 == view.getId()) {
            return checkStockUnit(1, "15", R.id.button_1_15);
        } else if (R.id.button_1_16 == view.getId()) {
            return checkStockUnit(1, "16", R.id.button_1_16);
        } else if (R.id.button_1_1e == view.getId()) {
            return checkStockUnit(1, "1D", R.id.button_1_1e);
        } else if (R.id.button_1_1d == view.getId()) {
            return checkStockUnit(1, "1D", R.id.button_1_1d);
        } else if (R.id.button_1_1c == view.getId()) {
            return checkStockUnit(1, "1C", R.id.button_1_1c);
        } else if (R.id.button_1_1b == view.getId()) {
            return checkStockUnit(1, "1B", R.id.button_1_1b);
        } else if (R.id.button_1_1a == view.getId()) {
            return checkStockUnit(1, "1A", R.id.button_1_1a);

        } else if (R.id.button_2_1 == view.getId()) {
            return checkStockUnit(2, "1", R.id.button_2_1);
        } else if (R.id.button_2_2 == view.getId()) {
            return checkStockUnit(2, "2", R.id.button_2_2);
        } else if (R.id.button_2_3 == view.getId()) {
            return checkStockUnit(2, "3", R.id.button_2_3);
        } else if (R.id.button_2_5 == view.getId()) {
            return checkStockUnit(2, "5", R.id.button_2_5);
        } else if (R.id.button_2_6 == view.getId()) {
            return checkStockUnit(2, "6", R.id.button_2_6);
        } else if (R.id.button_2_7 == view.getId()) {
            return checkStockUnit(2, "7", R.id.button_2_7);
        } else if (R.id.button_2_8 == view.getId()) {
            return checkStockUnit(2, "8", R.id.button_2_8);
        } else if (R.id.button_2_9 == view.getId()) {
            return checkStockUnit(2, "9", R.id.button_2_9);
        } else if (R.id.button_2_10 == view.getId()) {
            return checkStockUnit(2, "10", R.id.button_2_10);
        } else if (R.id.button_2_11 == view.getId()) {
            return checkStockUnit(2, "11", R.id.button_2_11);
        } else if (R.id.button_2_12 == view.getId()) {
            return checkStockUnit(2, "12", R.id.button_2_12);
        } else if (R.id.button_2_15 == view.getId()) {
            return checkStockUnit(2, "15", R.id.button_2_15);
        } else if (R.id.button_2_16 == view.getId()) {
            return checkStockUnit(2, "16", R.id.button_2_16);
        } else if (R.id.button_2_17 == view.getId()) {
            return checkStockUnit(2, "17", R.id.button_2_17);
        } else if (R.id.button_2_18 == view.getId()) {
            return checkStockUnit(2, "18", R.id.button_2_18);
        } else if (R.id.button_2_19 == view.getId()) {
            return checkStockUnit(2, "19", R.id.button_2_19);
        } else if (R.id.button_2_20 == view.getId()) {
            return checkStockUnit(2, "20", R.id.button_2_20);
        } else if (R.id.button_2_21 == view.getId()) {
            return checkStockUnit(2, "21", R.id.button_2_21);
        } else if (R.id.button_2_23 == view.getId()) {
            return checkStockUnit(2, "23", R.id.button_2_23);
        } else if (R.id.button_2_25 == view.getId()) {
            return checkStockUnit(2, "25", R.id.button_2_25);
        } else if (R.id.button_2_26 == view.getId()) {
            return checkStockUnit(2, "26", R.id.button_2_26);
        } else if (R.id.button_2_27 == view.getId()) {
            return checkStockUnit(2, "27", R.id.button_2_27);
        } else if (R.id.button_2_28 == view.getId()) {
            return checkStockUnit(2, "28", R.id.button_2_28);
        } else if (R.id.button_2_29 == view.getId()) {
            return checkStockUnit(2, "29", R.id.button_2_29);
        } else if (R.id.button_2_30 == view.getId()) {
            return checkStockUnit(2, "30", R.id.button_2_30);
        } else if (R.id.button_2_32 == view.getId()) {
            return checkStockUnit(2, "32", R.id.button_2_32);
        } else if (R.id.button_2_33 == view.getId()) {
            return checkStockUnit(2, "33", R.id.button_2_33);
        } else if (R.id.button_2_35 == view.getId()) {
            return checkStockUnit(2, "35", R.id.button_2_35);

        } else if (R.id.button_3_1 == view.getId()) {
            return checkStockUnit(3, "1", R.id.button_3_1);
        } else if (R.id.button_3_2 == view.getId()) {
            return checkStockUnit(3, "2", R.id.button_3_2);
        } else if (R.id.button_3_3 == view.getId()) {
            return checkStockUnit(3, "3", R.id.button_3_3);
        } else if (R.id.button_3_5 == view.getId()) {
            return checkStockUnit(3, "5", R.id.button_3_5);
        } else if (R.id.button_3_6 == view.getId()) {
            return checkStockUnit(3, "6", R.id.button_3_6);
        } else if (R.id.button_3_7 == view.getId()) {
            return checkStockUnit(3, "7", R.id.button_3_7);
        } else if (R.id.button_3_8 == view.getId()) {
            return checkStockUnit(3, "8", R.id.button_3_8);
        } else if (R.id.button_3_9 == view.getId()) {
            return checkStockUnit(3, "9", R.id.button_3_9);
        } else if (R.id.button_3_10 == view.getId()) {
            return checkStockUnit(3, "10", R.id.button_3_10);
        } else if (R.id.button_3_11 == view.getId()) {
            return checkStockUnit(3, "11", R.id.button_3_11);
        } else if (R.id.button_3_12 == view.getId()) {
            return checkStockUnit(3, "12", R.id.button_3_12);
        } else if (R.id.button_3_15 == view.getId()) {
            return checkStockUnit(3, "15", R.id.button_3_15);
        } else if (R.id.button_3_16 == view.getId()) {
            return checkStockUnit(3, "16", R.id.button_3_16);
        } else if (R.id.button_3_17 == view.getId()) {
            return checkStockUnit(3, "17", R.id.button_3_17);
        } else if (R.id.button_3_18 == view.getId()) {
            return checkStockUnit(3, "18", R.id.button_3_18);
        } else if (R.id.button_3_19 == view.getId()) {
            return checkStockUnit(3, "19", R.id.button_3_19);
        } else if (R.id.button_3_20 == view.getId()) {
            return checkStockUnit(3, "20", R.id.button_3_20);
        } else if (R.id.button_3_21 == view.getId()) {
            return checkStockUnit(3, "21", R.id.button_3_21);
        } else if (R.id.button_3_23 == view.getId()) {
            return checkStockUnit(3, "23", R.id.button_3_23);
        } else if (R.id.button_3_25 == view.getId()) {
            return checkStockUnit(3, "25", R.id.button_3_25);
        } else if (R.id.button_3_26 == view.getId()) {
            return checkStockUnit(3, "26", R.id.button_3_26);
        } else if (R.id.button_3_27 == view.getId()) {
            return checkStockUnit(3, "27", R.id.button_3_27);
        } else if (R.id.button_3_28 == view.getId()) {
            return checkStockUnit(3, "28", R.id.button_3_28);
        } else if (R.id.button_3_29 == view.getId()) {
            return checkStockUnit(3, "29", R.id.button_3_29);
        } else if (R.id.button_3_30 == view.getId()) {
            return checkStockUnit(3, "30", R.id.button_3_30);
        } else if (R.id.button_3_32 == view.getId()) {
            return checkStockUnit(3, "32", R.id.button_3_32);
        } else if (R.id.button_3_33 == view.getId()) {
            return checkStockUnit(3, "33", R.id.button_3_33);
        } else if (R.id.button_3_35 == view.getId()) {
            return checkStockUnit(3, "35", R.id.button_3_35);

        } else if (R.id.button_5_1 == view.getId()) {
            return checkStockUnit(5, "1", R.id.button_5_1);
        } else if (R.id.button_5_2 == view.getId()) {
            return checkStockUnit(5, "2", R.id.button_5_2);
        } else if (R.id.button_5_3 == view.getId()) {
            return checkStockUnit(5, "3", R.id.button_5_3);
        } else if (R.id.button_5_5 == view.getId()) {
            return checkStockUnit(5, "5", R.id.button_5_5);
        } else if (R.id.button_5_6 == view.getId()) {
            return checkStockUnit(5, "6", R.id.button_5_6);
        } else if (R.id.button_5_7 == view.getId()) {
            return checkStockUnit(5, "7", R.id.button_5_7);
        } else if (R.id.button_5_8 == view.getId()) {
            return checkStockUnit(5, "8", R.id.button_5_8);
        } else if (R.id.button_5_9 == view.getId()) {
            return checkStockUnit(5, "9", R.id.button_5_9);
        } else if (R.id.button_5_10 == view.getId()) {
            return checkStockUnit(5, "10", R.id.button_5_10);
        } else if (R.id.button_5_11 == view.getId()) {
            return checkStockUnit(5, "11", R.id.button_5_11);
        } else if (R.id.button_5_12 == view.getId()) {
            return checkStockUnit(5, "12", R.id.button_5_12);
        } else if (R.id.button_5_15 == view.getId()) {
            return checkStockUnit(5, "15", R.id.button_5_15);
        } else if (R.id.button_5_16 == view.getId()) {
            return checkStockUnit(5, "16", R.id.button_5_16);
        } else if (R.id.button_5_17 == view.getId()) {
            return checkStockUnit(5, "17", R.id.button_5_17);
        } else if (R.id.button_5_18 == view.getId()) {
            return checkStockUnit(5, "18", R.id.button_5_18);
        } else if (R.id.button_5_19 == view.getId()) {
            return checkStockUnit(5, "19", R.id.button_5_19);
        } else if (R.id.button_5_20 == view.getId()) {
            return checkStockUnit(5, "20", R.id.button_5_20);
        } else if (R.id.button_5_21 == view.getId()) {
            return checkStockUnit(5, "21", R.id.button_5_21);
        } else if (R.id.button_5_23 == view.getId()) {
            return checkStockUnit(5, "23", R.id.button_5_23);
        } else if (R.id.button_5_25 == view.getId()) {
            return checkStockUnit(5, "25", R.id.button_5_25);
        } else if (R.id.button_5_26 == view.getId()) {
            return checkStockUnit(5, "26", R.id.button_5_26);
        } else if (R.id.button_5_27 == view.getId()) {
            return checkStockUnit(5, "27", R.id.button_5_27);
        } else if (R.id.button_5_28 == view.getId()) {
            return checkStockUnit(5, "28", R.id.button_5_28);
        } else if (R.id.button_5_29 == view.getId()) {
            return checkStockUnit(5, "29", R.id.button_5_29);
        } else if (R.id.button_5_30 == view.getId()) {
            return checkStockUnit(5, "30", R.id.button_5_30);
        } else if (R.id.button_5_32 == view.getId()) {
            return checkStockUnit(5, "32", R.id.button_5_32);
        } else if (R.id.button_5_33 == view.getId()) {
            return checkStockUnit(5, "33", R.id.button_5_33);
        } else if (R.id.button_5_35 == view.getId()) {
            return checkStockUnit(5, "35", R.id.button_5_35);

        } else if (R.id.button_6_1 == view.getId()) {
            return checkStockUnit(6, "1", R.id.button_6_1);
        } else if (R.id.button_6_2 == view.getId()) {
            return checkStockUnit(6, "2", R.id.button_6_2);
        } else if (R.id.button_6_3 == view.getId()) {
            return checkStockUnit(6, "3", R.id.button_6_3);
        } else if (R.id.button_6_5 == view.getId()) {
            return checkStockUnit(6, "5", R.id.button_6_5);
        } else if (R.id.button_6_6 == view.getId()) {
            return checkStockUnit(6, "6", R.id.button_6_6);
        } else if (R.id.button_6_7 == view.getId()) {
            return checkStockUnit(6, "7", R.id.button_6_7);
        } else if (R.id.button_6_8 == view.getId()) {
            return checkStockUnit(6, "8", R.id.button_6_8);
        } else if (R.id.button_6_9 == view.getId()) {
            return checkStockUnit(6, "9", R.id.button_6_9);
        } else if (R.id.button_6_10 == view.getId()) {
            return checkStockUnit(6, "10", R.id.button_6_10);
        } else if (R.id.button_6_11 == view.getId()) {
            return checkStockUnit(6, "11", R.id.button_6_11);
        } else if (R.id.button_6_12 == view.getId()) {
            return checkStockUnit(6, "12", R.id.button_6_12);
        } else if (R.id.button_6_15 == view.getId()) {
            return checkStockUnit(6, "15", R.id.button_6_15);
        } else if (R.id.button_6_16 == view.getId()) {
            return checkStockUnit(6, "16", R.id.button_6_16);
        } else if (R.id.button_6_17 == view.getId()) {
            return checkStockUnit(6, "17", R.id.button_6_17);
        } else if (R.id.button_6_18 == view.getId()) {
            return checkStockUnit(6, "18", R.id.button_6_18);
        } else if (R.id.button_6_19 == view.getId()) {
            return checkStockUnit(6, "19", R.id.button_6_19);
        } else if (R.id.button_6_20 == view.getId()) {
            return checkStockUnit(6, "20", R.id.button_6_20);
        } else if (R.id.button_6_21 == view.getId()) {
            return checkStockUnit(6, "21", R.id.button_6_21);
        } else if (R.id.button_6_23 == view.getId()) {
            return checkStockUnit(6, "23", R.id.button_6_23);
        } else if (R.id.button_6_25 == view.getId()) {
            return checkStockUnit(6, "25", R.id.button_6_25);
        } else if (R.id.button_6_26 == view.getId()) {
            return checkStockUnit(6, "26", R.id.button_6_26);
        } else if (R.id.button_6_27 == view.getId()) {
            return checkStockUnit(6, "27", R.id.button_6_27);
        } else if (R.id.button_6_28 == view.getId()) {
            return checkStockUnit(6, "28", R.id.button_6_28);
        } else if (R.id.button_6_29 == view.getId()) {
            return checkStockUnit(6, "29", R.id.button_6_29);
        } else if (R.id.button_6_30 == view.getId()) {
            return checkStockUnit(6, "30", R.id.button_6_30);
        } else if (R.id.button_6_32 == view.getId()) {
            return checkStockUnit(6, "32", R.id.button_6_32);
        } else if (R.id.button_6_33 == view.getId()) {
            return checkStockUnit(6, "33", R.id.button_6_33);
        } else if (R.id.button_6_35 == view.getId()) {
            return checkStockUnit(6, "35", R.id.button_6_35);

        } else if (R.id.button_7_1 == view.getId()) {
            return checkStockUnit(7, "1", R.id.button_7_1);
        } else if (R.id.button_7_2 == view.getId()) {
            return checkStockUnit(7, "2", R.id.button_7_2);
        } else if (R.id.button_7_3 == view.getId()) {
            return checkStockUnit(7, "3", R.id.button_7_3);
        } else if (R.id.button_7_5 == view.getId()) {
            return checkStockUnit(7, "5", R.id.button_7_5);
        } else if (R.id.button_7_6 == view.getId()) {
            return checkStockUnit(7, "6", R.id.button_7_6);
        } else if (R.id.button_7_7 == view.getId()) {
            return checkStockUnit(7, "7", R.id.button_7_7);
        } else if (R.id.button_7_8 == view.getId()) {
            return checkStockUnit(7, "8", R.id.button_7_8);
        } else if (R.id.button_7_9 == view.getId()) {
            return checkStockUnit(7, "9", R.id.button_7_9);
        } else if (R.id.button_7_10 == view.getId()) {
            return checkStockUnit(7, "10", R.id.button_7_10);
        } else if (R.id.button_7_11 == view.getId()) {
            return checkStockUnit(7, "11", R.id.button_7_11);
        } else if (R.id.button_7_12 == view.getId()) {
            return checkStockUnit(7, "12", R.id.button_7_12);
        } else if (R.id.button_7_15 == view.getId()) {
            return checkStockUnit(7, "15", R.id.button_7_15);
        } else if (R.id.button_7_16 == view.getId()) {
            return checkStockUnit(7, "16", R.id.button_7_16);
        } else if (R.id.button_7_17 == view.getId()) {
            return checkStockUnit(7, "17", R.id.button_7_17);
        } else if (R.id.button_7_18 == view.getId()) {
            return checkStockUnit(7, "18", R.id.button_7_18);
        } else if (R.id.button_7_19 == view.getId()) {
            return checkStockUnit(7, "19", R.id.button_7_19);
        } else if (R.id.button_7_20 == view.getId()) {
            return checkStockUnit(7, "20", R.id.button_7_20);
        } else if (R.id.button_7_21 == view.getId()) {
            return checkStockUnit(7, "21", R.id.button_7_21);
        } else if (R.id.button_7_23 == view.getId()) {
            return checkStockUnit(7, "23", R.id.button_7_23);
        } else if (R.id.button_7_25 == view.getId()) {
            return checkStockUnit(7, "25", R.id.button_7_25);
        } else if (R.id.button_7_26 == view.getId()) {
            return checkStockUnit(7, "26", R.id.button_7_26);
        } else if (R.id.button_7_27 == view.getId()) {
            return checkStockUnit(7, "27", R.id.button_7_27);
        } else if (R.id.button_7_28 == view.getId()) {
            return checkStockUnit(7, "28", R.id.button_7_28);
        } else if (R.id.button_7_29 == view.getId()) {
            return checkStockUnit(7, "29", R.id.button_7_29);
        } else if (R.id.button_7_30 == view.getId()) {
            return checkStockUnit(7, "30", R.id.button_7_30);
        } else if (R.id.button_7_32 == view.getId()) {
            return checkStockUnit(7, "32", R.id.button_7_32);
        } else if (R.id.button_7_33 == view.getId()) {
            return checkStockUnit(7, "33", R.id.button_7_33);
        } else if (R.id.button_7_35 == view.getId()) {
            return checkStockUnit(7, "35", R.id.button_7_35);

        } else if (R.id.button_8_1 == view.getId()) {
            return checkStockUnit(8, "1", R.id.button_8_1);
        } else if (R.id.button_8_2 == view.getId()) {
            return checkStockUnit(8, "2", R.id.button_8_2);
        } else if (R.id.button_8_3 == view.getId()) {
            return checkStockUnit(8, "3", R.id.button_8_3);
        } else if (R.id.button_8_5 == view.getId()) {
            return checkStockUnit(8, "5", R.id.button_8_5);
        } else if (R.id.button_8_6 == view.getId()) {
            return checkStockUnit(8, "6", R.id.button_8_6);
        } else if (R.id.button_8_7 == view.getId()) {
            return checkStockUnit(8, "7", R.id.button_8_7);
        } else if (R.id.button_8_8 == view.getId()) {
            return checkStockUnit(8, "8", R.id.button_8_8);
        } else if (R.id.button_8_9 == view.getId()) {
            return checkStockUnit(8, "9", R.id.button_8_9);
        } else if (R.id.button_8_10 == view.getId()) {
            return checkStockUnit(8, "10", R.id.button_8_10);
        } else if (R.id.button_8_11 == view.getId()) {
            return checkStockUnit(8, "11", R.id.button_8_11);
        } else if (R.id.button_8_12 == view.getId()) {
            return checkStockUnit(8, "12", R.id.button_8_12);
        } else if (R.id.button_8_15 == view.getId()) {
            return checkStockUnit(8, "15", R.id.button_8_15);
        } else if (R.id.button_8_16 == view.getId()) {
            return checkStockUnit(8, "16", R.id.button_8_16);
        } else if (R.id.button_8_17 == view.getId()) {
            return checkStockUnit(8, "17", R.id.button_8_17);
        } else if (R.id.button_8_18 == view.getId()) {
            return checkStockUnit(8, "18", R.id.button_8_18);
        } else if (R.id.button_8_19 == view.getId()) {
            return checkStockUnit(8, "19", R.id.button_8_19);
        } else if (R.id.button_8_20 == view.getId()) {
            return checkStockUnit(8, "20", R.id.button_8_20);
        } else if (R.id.button_8_21 == view.getId()) {
            return checkStockUnit(8, "21", R.id.button_8_21);
        } else if (R.id.button_8_23 == view.getId()) {
            return checkStockUnit(8, "23", R.id.button_8_23);
        } else if (R.id.button_8_25 == view.getId()) {
            return checkStockUnit(8, "25", R.id.button_8_25);
        } else if (R.id.button_8_26 == view.getId()) {
            return checkStockUnit(8, "26", R.id.button_8_26);
        } else if (R.id.button_8_27 == view.getId()) {
            return checkStockUnit(8, "27", R.id.button_8_27);
        } else if (R.id.button_8_28 == view.getId()) {
            return checkStockUnit(8, "28", R.id.button_8_28);
        } else if (R.id.button_8_29 == view.getId()) {
            return checkStockUnit(8, "29", R.id.button_8_29);
        } else if (R.id.button_8_30 == view.getId()) {
            return checkStockUnit(8, "30", R.id.button_8_30);
        } else if (R.id.button_8_32 == view.getId()) {
            return checkStockUnit(8, "32", R.id.button_8_32);
        } else if (R.id.button_8_33 == view.getId()) {
            return checkStockUnit(8, "33", R.id.button_8_33);
        } else if (R.id.button_8_35 == view.getId()) {
            return checkStockUnit(8, "35", R.id.button_8_35);

        } else if (R.id.button_9_1 == view.getId()) {
            return checkStockUnit(9, "1", R.id.button_9_1);
        } else if (R.id.button_9_2 == view.getId()) {
            return checkStockUnit(9, "2", R.id.button_9_2);
        } else if (R.id.button_9_3 == view.getId()) {
            return checkStockUnit(9, "3", R.id.button_9_3);
        } else if (R.id.button_9_5 == view.getId()) {
            return checkStockUnit(9, "5", R.id.button_9_5);
        } else if (R.id.button_9_6 == view.getId()) {
            return checkStockUnit(9, "6", R.id.button_9_6);
        } else if (R.id.button_9_7 == view.getId()) {
            return checkStockUnit(9, "7", R.id.button_9_7);
        } else if (R.id.button_9_8 == view.getId()) {
            return checkStockUnit(9, "8", R.id.button_9_8);
        } else if (R.id.button_9_9 == view.getId()) {
            return checkStockUnit(9, "9", R.id.button_9_9);
        } else if (R.id.button_9_10 == view.getId()) {
            return checkStockUnit(9, "10", R.id.button_9_10);
        } else if (R.id.button_9_11 == view.getId()) {
            return checkStockUnit(9, "11", R.id.button_9_11);
        } else if (R.id.button_9_12 == view.getId()) {
            return checkStockUnit(9, "12", R.id.button_9_12);
        } else if (R.id.button_9_15 == view.getId()) {
            return checkStockUnit(9, "15", R.id.button_9_15);
        } else if (R.id.button_9_16 == view.getId()) {
            return checkStockUnit(9, "16", R.id.button_9_16);
        } else if (R.id.button_9_17 == view.getId()) {
            return checkStockUnit(9, "17", R.id.button_9_17);
        } else if (R.id.button_9_18 == view.getId()) {
            return checkStockUnit(9, "18", R.id.button_9_18);
        } else if (R.id.button_9_19 == view.getId()) {
            return checkStockUnit(9, "19", R.id.button_9_19);
        } else if (R.id.button_9_20 == view.getId()) {
            return checkStockUnit(9, "20", R.id.button_9_20);
        } else if (R.id.button_9_21 == view.getId()) {
            return checkStockUnit(9, "21", R.id.button_9_21);
        } else if (R.id.button_9_23 == view.getId()) {
            return checkStockUnit(9, "23", R.id.button_9_23);
        } else if (R.id.button_9_25 == view.getId()) {
            return checkStockUnit(9, "25", R.id.button_9_25);
        } else if (R.id.button_9_26 == view.getId()) {
            return checkStockUnit(9, "26", R.id.button_9_26);
        } else if (R.id.button_9_27 == view.getId()) {
            return checkStockUnit(9, "27", R.id.button_9_27);
        } else if (R.id.button_9_28 == view.getId()) {
            return checkStockUnit(9, "28", R.id.button_9_28);
        } else if (R.id.button_9_29 == view.getId()) {
            return checkStockUnit(9, "29", R.id.button_9_29);
        } else if (R.id.button_9_30 == view.getId()) {
            return checkStockUnit(9, "30", R.id.button_9_30);
        } else if (R.id.button_9_32 == view.getId()) {
            return checkStockUnit(9, "32", R.id.button_9_32);
        } else if (R.id.button_9_33 == view.getId()) {
            return checkStockUnit(9, "33", R.id.button_9_33);
        } else if (R.id.button_9_35 == view.getId()) {
            return checkStockUnit(9, "35", R.id.button_9_35);

        } else if (R.id.button_10_1 == view.getId()) {
            return checkStockUnit(10, "1", R.id.button_10_1);
        } else if (R.id.button_10_2 == view.getId()) {
            return checkStockUnit(10, "2", R.id.button_10_2);
        } else if (R.id.button_10_3 == view.getId()) {
            return checkStockUnit(10, "3", R.id.button_10_3);
        } else if (R.id.button_10_5 == view.getId()) {
            return checkStockUnit(10, "5", R.id.button_10_5);
        } else if (R.id.button_10_6 == view.getId()) {
            return checkStockUnit(10, "6", R.id.button_10_6);
        } else if (R.id.button_10_7 == view.getId()) {
            return checkStockUnit(10, "7", R.id.button_10_7);
        } else if (R.id.button_10_8 == view.getId()) {
            return checkStockUnit(10, "8", R.id.button_10_8);
        } else if (R.id.button_10_9 == view.getId()) {
            return checkStockUnit(10, "9", R.id.button_10_9);
        } else if (R.id.button_10_10 == view.getId()) {
            return checkStockUnit(10, "10", R.id.button_10_10);
        } else if (R.id.button_10_11 == view.getId()) {
            return checkStockUnit(10, "11", R.id.button_10_11);
        } else if (R.id.button_10_12 == view.getId()) {
            return checkStockUnit(10, "12", R.id.button_10_12);
        } else if (R.id.button_10_15 == view.getId()) {
            return checkStockUnit(10, "15", R.id.button_10_15);
        } else if (R.id.button_10_16 == view.getId()) {
            return checkStockUnit(10, "16", R.id.button_10_16);
        } else if (R.id.button_10_17 == view.getId()) {
            return checkStockUnit(10, "17", R.id.button_10_17);
        } else if (R.id.button_10_18 == view.getId()) {
            return checkStockUnit(10, "18", R.id.button_10_18);
        } else if (R.id.button_10_19 == view.getId()) {
            return checkStockUnit(10, "19", R.id.button_10_19);
        } else if (R.id.button_10_20 == view.getId()) {
            return checkStockUnit(10, "20", R.id.button_10_20);
        } else if (R.id.button_10_21 == view.getId()) {
            return checkStockUnit(10, "21", R.id.button_10_21);
        } else if (R.id.button_10_23 == view.getId()) {
            return checkStockUnit(10, "23", R.id.button_10_23);
        } else if (R.id.button_10_25 == view.getId()) {
            return checkStockUnit(10, "25", R.id.button_10_25);
        } else if (R.id.button_10_26 == view.getId()) {
            return checkStockUnit(10, "26", R.id.button_10_26);
        } else if (R.id.button_10_27 == view.getId()) {
            return checkStockUnit(10, "27", R.id.button_10_27);
        } else if (R.id.button_10_28 == view.getId()) {
            return checkStockUnit(10, "28", R.id.button_10_28);
        } else if (R.id.button_10_29 == view.getId()) {
            return checkStockUnit(10, "29", R.id.button_10_29);
        } else if (R.id.button_10_30 == view.getId()) {
            return checkStockUnit(10, "30", R.id.button_10_30);
        } else if (R.id.button_10_32 == view.getId()) {
            return checkStockUnit(10, "32", R.id.button_10_32);
        } else if (R.id.button_10_33 == view.getId()) {
            return checkStockUnit(10, "33", R.id.button_10_33);
        } else if (R.id.button_10_35 == view.getId()) {
            return checkStockUnit(10, "35", R.id.button_10_35);

        } else if (R.id.button_11_1 == view.getId()) {
            return checkStockUnit(11, "1", R.id.button_11_1);
        } else if (R.id.button_11_2 == view.getId()) {
            return checkStockUnit(11, "2", R.id.button_11_2);
        } else if (R.id.button_11_3 == view.getId()) {
            return checkStockUnit(11, "3", R.id.button_11_3);
        } else if (R.id.button_11_5 == view.getId()) {
            return checkStockUnit(11, "5", R.id.button_11_5);
        } else if (R.id.button_11_6 == view.getId()) {
            return checkStockUnit(11, "6", R.id.button_11_6);
        } else if (R.id.button_11_7 == view.getId()) {
            return checkStockUnit(11, "7", R.id.button_11_7);
        } else if (R.id.button_11_8 == view.getId()) {
            return checkStockUnit(11, "8", R.id.button_11_8);
        } else if (R.id.button_11_9 == view.getId()) {
            return checkStockUnit(11, "9", R.id.button_11_9);
        } else if (R.id.button_11_10 == view.getId()) {
            return checkStockUnit(11, "10", R.id.button_11_10);
        } else if (R.id.button_11_11 == view.getId()) {
            return checkStockUnit(11, "11", R.id.button_11_11);
        } else if (R.id.button_11_12 == view.getId()) {
            return checkStockUnit(11, "12", R.id.button_11_12);
        } else if (R.id.button_11_15 == view.getId()) {
            return checkStockUnit(11, "15", R.id.button_11_15);
        } else if (R.id.button_11_16 == view.getId()) {
            return checkStockUnit(11, "16", R.id.button_11_16);
        } else if (R.id.button_11_17 == view.getId()) {
            return checkStockUnit(11, "17", R.id.button_11_17);
        } else if (R.id.button_11_18 == view.getId()) {
            return checkStockUnit(11, "18", R.id.button_11_18);
        } else if (R.id.button_11_19 == view.getId()) {
            return checkStockUnit(11, "19", R.id.button_11_19);
        } else if (R.id.button_11_20 == view.getId()) {
            return checkStockUnit(11, "20", R.id.button_11_20);
        } else if (R.id.button_11_21 == view.getId()) {
            return checkStockUnit(11, "21", R.id.button_11_21);
        } else if (R.id.button_11_23 == view.getId()) {
            return checkStockUnit(11, "23", R.id.button_11_23);
        } else if (R.id.button_11_25 == view.getId()) {
            return checkStockUnit(11, "25", R.id.button_11_25);
        } else if (R.id.button_11_26 == view.getId()) {
            return checkStockUnit(11, "26", R.id.button_11_26);
        } else if (R.id.button_11_27 == view.getId()) {
            return checkStockUnit(11, "27", R.id.button_11_27);
        } else if (R.id.button_11_28 == view.getId()) {
            return checkStockUnit(11, "28", R.id.button_11_28);
        } else if (R.id.button_11_29 == view.getId()) {
            return checkStockUnit(11, "29", R.id.button_11_29);
        } else if (R.id.button_11_30 == view.getId()) {
            return checkStockUnit(11, "30", R.id.button_11_30);
        } else if (R.id.button_11_32 == view.getId()) {
            return checkStockUnit(11, "32", R.id.button_11_32);
        } else if (R.id.button_11_33 == view.getId()) {
            return checkStockUnit(11, "33", R.id.button_11_33);
        } else if (R.id.button_11_35 == view.getId()) {
            return checkStockUnit(11, "35", R.id.button_11_35);

        } else if (R.id.button_12_1 == view.getId()) {
            return checkStockUnit(12, "1", R.id.button_12_1);
        } else if (R.id.button_12_2 == view.getId()) {
            return checkStockUnit(12, "2", R.id.button_12_2);
        } else if (R.id.button_12_3 == view.getId()) {
            return checkStockUnit(12, "3", R.id.button_12_3);
        } else if (R.id.button_12_5 == view.getId()) {
            return checkStockUnit(12, "5", R.id.button_12_5);
        } else if (R.id.button_12_6 == view.getId()) {
            return checkStockUnit(12, "6", R.id.button_12_6);
        } else if (R.id.button_12_7 == view.getId()) {
            return checkStockUnit(12, "7", R.id.button_12_7);
        } else if (R.id.button_12_8 == view.getId()) {
            return checkStockUnit(12, "8", R.id.button_12_8);
        } else if (R.id.button_12_9 == view.getId()) {
            return checkStockUnit(12, "9", R.id.button_12_9);
        } else if (R.id.button_12_10 == view.getId()) {
            return checkStockUnit(12, "10", R.id.button_12_10);
        } else if (R.id.button_12_11 == view.getId()) {
            return checkStockUnit(12, "11", R.id.button_12_11);
        } else if (R.id.button_12_12 == view.getId()) {
            return checkStockUnit(12, "12", R.id.button_12_12);
        } else if (R.id.button_12_15 == view.getId()) {
            return checkStockUnit(12, "15", R.id.button_12_15);
        } else if (R.id.button_12_16 == view.getId()) {
            return checkStockUnit(12, "16", R.id.button_12_16);
        } else if (R.id.button_12_17 == view.getId()) {
            return checkStockUnit(12, "17", R.id.button_12_17);
        } else if (R.id.button_12_18 == view.getId()) {
            return checkStockUnit(12, "18", R.id.button_12_18);
        } else if (R.id.button_12_19 == view.getId()) {
            return checkStockUnit(12, "19", R.id.button_12_19);
        } else if (R.id.button_12_20 == view.getId()) {
            return checkStockUnit(12, "20", R.id.button_12_20);
        } else if (R.id.button_12_21 == view.getId()) {
            return checkStockUnit(12, "21", R.id.button_12_21);
        } else if (R.id.button_12_23 == view.getId()) {
            return checkStockUnit(12, "23", R.id.button_12_23);
        } else if (R.id.button_12_25 == view.getId()) {
            return checkStockUnit(12, "25", R.id.button_12_25);
        } else if (R.id.button_12_26 == view.getId()) {
            return checkStockUnit(12, "26", R.id.button_12_26);
        } else if (R.id.button_12_27 == view.getId()) {
            return checkStockUnit(12, "27", R.id.button_12_27);
        } else if (R.id.button_12_28 == view.getId()) {
            return checkStockUnit(12, "28", R.id.button_12_28);
        } else if (R.id.button_12_29 == view.getId()) {
            return checkStockUnit(12, "29", R.id.button_12_29);
        } else if (R.id.button_12_30 == view.getId()) {
            return checkStockUnit(12, "30", R.id.button_12_30);
        } else if (R.id.button_12_32 == view.getId()) {
            return checkStockUnit(12, "32", R.id.button_12_32);
        } else if (R.id.button_12_33 == view.getId()) {
            return checkStockUnit(12, "33", R.id.button_12_33);
        } else if (R.id.button_12_35 == view.getId()) {
            return checkStockUnit(12, "35", R.id.button_12_35);
        }
        return false;
    }

    private boolean checkStockUnit(int floor, String unitNumber, int id) {
        Log.d(TAG, "checkStockUnit:"+floor+","+unitNumber+":"+id);
        for (ApartmentUnit unit: listApartmentUnit) {
            if (unit.getUnitNumber().equals(unitNumber) && unit.getApartmentFloor().getNumber().equals(String.valueOf(floor))){
                try {
                    BootstrapButton button = getView().findViewById(id);

                    if (Arrays.asList(new String[]{"disabled", "empty", "progress", "ready", "cancel"}).contains(unit.getStatus())){
                        if (lastButton != null){
                            lastButton.setBootstrapBrand(DefaultBootstrapBrand.SECONDARY);
                        }
                        button.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);

                        lastButton = button;
                        lastUnit = unit;
                        buttonProcess.setEnabled(true);
                    } else if (unit.getStatus().equals("disabled")){
                        Helpers.showLongSnackbar(getView(), R.string.apartment_unit_is_already_booked);
                    } else if (unit.getStatus().equals("hold")){
                        Helpers.showLongSnackbar(getView(), R.string.apartment_unit_is_already_booked);
                    } else if (unit.getStatus().equals("booked")){
                        Helpers.showLongSnackbar(getView(), R.string.apartment_unit_is_already_booked);
                    } else if (unit.getStatus().equals("sold")){
                        Helpers.showLongSnackbar(getView(), R.string.apartment_unit_is_already_booked);
                    }
                    return true;
                } catch (NullPointerException e){

                }
            }
        }
        return false;
    }

    private boolean getStockUnit(int floor, String unitNumber, int id) {
        for (ApartmentUnit unit: listApartmentUnit) {
            if (unit.getUnitNumber().equals(unitNumber) && unit.getApartmentFloor().getNumber().equals(floor)){
                try {
                    BootstrapButton button = getView().findViewById(id);

                    if (unit.getStatus() != null){
                        if (unit.getStatus().equals("disabled")){
                            button.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                            disabledCount ++;
                        } else if (unit.getStatus().equals("empty")){
                            button.setBootstrapBrand(DefaultBootstrapBrand.SECONDARY);
                        } else if (unit.getStatus().equals("progress")){
                            button.setBootstrapBrand(DefaultBootstrapBrand.SECONDARY);
                        } else if (unit.getStatus().equals("ready")){
                            button.setBootstrapBrand(DefaultBootstrapBrand.SECONDARY);
                        } else if (unit.getStatus().equals("hold")){
                            button.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                            warningCount ++;
                        } else if (unit.getStatus().equals("booked")){
                            button.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                            dangerCount ++;
                        } else if (unit.getStatus().equals("sold")){
                            button.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                            dangerCount ++;
                        } else if (unit.getStatus().equals("cancel")){
                            button.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                            disabledCount ++;
                        } else {
                            button.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                            disabledCount ++;
                        }
                    } else {
                        disabledCount ++;
                    }
                    return true;
                } catch (NullPointerException e){

                }
            }
        }
        return false;
    }

    private void initStockUnits(){
        getStockUnit(1, "1", R.id.button_1_1);
        getStockUnit(1, "2", R.id.button_1_2);
        getStockUnit(1, "3", R.id.button_1_3);
        getStockUnit(1, "5", R.id.button_1_5);
        getStockUnit(1, "6", R.id.button_1_6);
        getStockUnit(1, "7", R.id.button_1_7);
        getStockUnit(1, "8", R.id.button_1_8);
        getStockUnit(1, "9", R.id.button_1_9);
        getStockUnit(1, "10", R.id.button_1_10);
        getStockUnit(1, "11", R.id.button_1_11);
        getStockUnit(1, "12", R.id.button_1_12);
        getStockUnit(1, "15", R.id.button_1_15);
        getStockUnit(1, "16", R.id.button_1_16);
        getStockUnit(1, "1E", R.id.button_1_1e);
        getStockUnit(1, "1D", R.id.button_1_1d);
        getStockUnit(1, "1C", R.id.button_1_1c);
        getStockUnit(1, "1B", R.id.button_1_1b);
        getStockUnit(1, "1A", R.id.button_1_1a);

        getStockUnit(2, "1", R.id.button_2_1);
        getStockUnit(2, "2", R.id.button_2_2);
        getStockUnit(2, "3", R.id.button_2_3);
        getStockUnit(2, "5", R.id.button_2_5);
        getStockUnit(2, "6", R.id.button_2_6);
        getStockUnit(2, "7", R.id.button_2_7);
        getStockUnit(2, "8", R.id.button_2_8);
        getStockUnit(2, "9", R.id.button_2_9);
        getStockUnit(2, "10", R.id.button_2_10);
        getStockUnit(2, "11", R.id.button_2_11);
        getStockUnit(2, "12", R.id.button_2_12);
        getStockUnit(2, "15", R.id.button_2_15);
        getStockUnit(2, "16", R.id.button_2_16);
        getStockUnit(2, "17", R.id.button_2_17);
        getStockUnit(2, "18", R.id.button_2_18);
        getStockUnit(2, "19", R.id.button_2_19);
        getStockUnit(2, "20", R.id.button_2_20);
        getStockUnit(2, "21", R.id.button_2_21);
        getStockUnit(2, "23", R.id.button_2_23);
        getStockUnit(2, "25", R.id.button_2_25);
        getStockUnit(2, "26", R.id.button_2_26);
        getStockUnit(2, "27", R.id.button_2_27);
        getStockUnit(2, "28", R.id.button_2_28);
        getStockUnit(2, "29", R.id.button_2_29);
        getStockUnit(2, "30", R.id.button_2_30);
        getStockUnit(2, "32", R.id.button_2_32);
        getStockUnit(2, "33", R.id.button_2_33);
        getStockUnit(2, "35", R.id.button_2_35);

        getStockUnit(3, "1", R.id.button_3_1);
        getStockUnit(3, "2", R.id.button_3_2);
        getStockUnit(3, "3", R.id.button_3_3);
        getStockUnit(3, "5", R.id.button_3_5);
        getStockUnit(3, "6", R.id.button_3_6);
        getStockUnit(3, "7", R.id.button_3_7);
        getStockUnit(3, "8", R.id.button_3_8);
        getStockUnit(3, "9", R.id.button_3_9);
        getStockUnit(3, "10", R.id.button_3_10);
        getStockUnit(3, "11", R.id.button_3_11);
        getStockUnit(3, "12", R.id.button_3_12);
        getStockUnit(3, "15", R.id.button_3_15);
        getStockUnit(3, "16", R.id.button_3_16);
        getStockUnit(3, "17", R.id.button_3_17);
        getStockUnit(3, "18", R.id.button_3_18);
        getStockUnit(3, "19", R.id.button_3_19);
        getStockUnit(3, "20", R.id.button_3_20);
        getStockUnit(3, "21", R.id.button_3_21);
        getStockUnit(3, "23", R.id.button_3_23);
        getStockUnit(3, "25", R.id.button_3_25);
        getStockUnit(3, "26", R.id.button_3_26);
        getStockUnit(3, "27", R.id.button_3_27);
        getStockUnit(3, "28", R.id.button_3_28);
        getStockUnit(3, "29", R.id.button_3_29);
        getStockUnit(3, "30", R.id.button_3_30);
        getStockUnit(3, "32", R.id.button_3_32);
        getStockUnit(3, "33", R.id.button_3_33);
        getStockUnit(3, "35", R.id.button_3_35);

        getStockUnit(5, "1", R.id.button_5_1);
        getStockUnit(5, "2", R.id.button_5_2);
        getStockUnit(5, "3", R.id.button_5_3);
        getStockUnit(5, "5", R.id.button_5_5);
        getStockUnit(5, "6", R.id.button_5_6);
        getStockUnit(5, "7", R.id.button_5_7);
        getStockUnit(5, "8", R.id.button_5_8);
        getStockUnit(5, "9", R.id.button_5_9);
        getStockUnit(5, "10", R.id.button_5_10);
        getStockUnit(5, "11", R.id.button_5_11);
        getStockUnit(5, "12", R.id.button_5_12);
        getStockUnit(5, "15", R.id.button_5_15);
        getStockUnit(5, "16", R.id.button_5_16);
        getStockUnit(5, "17", R.id.button_5_17);
        getStockUnit(5, "18", R.id.button_5_18);
        getStockUnit(5, "19", R.id.button_5_19);
        getStockUnit(5, "20", R.id.button_5_20);
        getStockUnit(5, "21", R.id.button_5_21);
        getStockUnit(5, "23", R.id.button_5_23);
        getStockUnit(5, "25", R.id.button_5_25);
        getStockUnit(5, "26", R.id.button_5_26);
        getStockUnit(5, "27", R.id.button_5_27);
        getStockUnit(5, "28", R.id.button_5_28);
        getStockUnit(5, "29", R.id.button_5_29);
        getStockUnit(5, "30", R.id.button_5_30);
        getStockUnit(5, "32", R.id.button_5_32);
        getStockUnit(5, "33", R.id.button_5_33);
        getStockUnit(5, "35", R.id.button_5_35);

        getStockUnit(6, "1", R.id.button_6_1);
        getStockUnit(6, "2", R.id.button_6_2);
        getStockUnit(6, "3", R.id.button_6_3);
        getStockUnit(6, "5", R.id.button_6_5);
        getStockUnit(6, "6", R.id.button_6_6);
        getStockUnit(6, "7", R.id.button_6_7);
        getStockUnit(6, "8", R.id.button_6_8);
        getStockUnit(6, "9", R.id.button_6_9);
        getStockUnit(6, "10", R.id.button_6_10);
        getStockUnit(6, "11", R.id.button_6_11);
        getStockUnit(6, "12", R.id.button_6_12);
        getStockUnit(6, "15", R.id.button_6_15);
        getStockUnit(6, "16", R.id.button_6_16);
        getStockUnit(6, "17", R.id.button_6_17);
        getStockUnit(6, "18", R.id.button_6_18);
        getStockUnit(6, "19", R.id.button_6_19);
        getStockUnit(6, "20", R.id.button_6_20);
        getStockUnit(6, "21", R.id.button_6_21);
        getStockUnit(6, "23", R.id.button_6_23);
        getStockUnit(6, "25", R.id.button_6_25);
        getStockUnit(6, "26", R.id.button_6_26);
        getStockUnit(6, "27", R.id.button_6_27);
        getStockUnit(6, "28", R.id.button_6_28);
        getStockUnit(6, "29", R.id.button_6_29);
        getStockUnit(6, "30", R.id.button_6_30);
        getStockUnit(6, "32", R.id.button_6_32);
        getStockUnit(6, "33", R.id.button_6_33);
        getStockUnit(6, "35", R.id.button_6_35);

        getStockUnit(7, "1", R.id.button_7_1);
        getStockUnit(7, "2", R.id.button_7_2);
        getStockUnit(7, "3", R.id.button_7_3);
        getStockUnit(7, "5", R.id.button_7_5);
        getStockUnit(7, "6", R.id.button_7_6);
        getStockUnit(7, "7", R.id.button_7_7);
        getStockUnit(7, "8", R.id.button_7_8);
        getStockUnit(7, "9", R.id.button_7_9);
        getStockUnit(7, "10", R.id.button_7_10);
        getStockUnit(7, "11", R.id.button_7_11);
        getStockUnit(7, "12", R.id.button_7_12);
        getStockUnit(7, "15", R.id.button_7_15);
        getStockUnit(7, "16", R.id.button_7_16);
        getStockUnit(7, "17", R.id.button_7_17);
        getStockUnit(7, "18", R.id.button_7_18);
        getStockUnit(7, "19", R.id.button_7_19);
        getStockUnit(7, "20", R.id.button_7_20);
        getStockUnit(7, "21", R.id.button_7_21);
        getStockUnit(7, "23", R.id.button_7_23);
        getStockUnit(7, "25", R.id.button_7_25);
        getStockUnit(7, "26", R.id.button_7_26);
        getStockUnit(7, "27", R.id.button_7_27);
        getStockUnit(7, "28", R.id.button_7_28);
        getStockUnit(7, "29", R.id.button_7_29);
        getStockUnit(7, "30", R.id.button_7_30);
        getStockUnit(7, "32", R.id.button_7_32);
        getStockUnit(7, "33", R.id.button_7_33);
        getStockUnit(7, "35", R.id.button_7_35);

        getStockUnit(8, "1", R.id.button_8_1);
        getStockUnit(8, "2", R.id.button_8_2);
        getStockUnit(8, "3", R.id.button_8_3);
        getStockUnit(8, "5", R.id.button_8_5);
        getStockUnit(8, "6", R.id.button_8_6);
        getStockUnit(8, "7", R.id.button_8_7);
        getStockUnit(8, "8", R.id.button_8_8);
        getStockUnit(8, "9", R.id.button_8_9);
        getStockUnit(8, "10", R.id.button_8_10);
        getStockUnit(8, "11", R.id.button_8_11);
        getStockUnit(8, "12", R.id.button_8_12);
        getStockUnit(8, "15", R.id.button_8_15);
        getStockUnit(8, "16", R.id.button_8_16);
        getStockUnit(8, "17", R.id.button_8_17);
        getStockUnit(8, "18", R.id.button_8_18);
        getStockUnit(8, "19", R.id.button_8_19);
        getStockUnit(8, "20", R.id.button_8_20);
        getStockUnit(8, "21", R.id.button_8_21);
        getStockUnit(8, "23", R.id.button_8_23);
        getStockUnit(8, "25", R.id.button_8_25);
        getStockUnit(8, "26", R.id.button_8_26);
        getStockUnit(8, "27", R.id.button_8_27);
        getStockUnit(8, "28", R.id.button_8_28);
        getStockUnit(8, "29", R.id.button_8_29);
        getStockUnit(8, "30", R.id.button_8_30);
        getStockUnit(8, "32", R.id.button_8_32);
        getStockUnit(8, "33", R.id.button_8_33);
        getStockUnit(8, "35", R.id.button_8_35);

        getStockUnit(9, "1", R.id.button_9_1);
        getStockUnit(9, "2", R.id.button_9_2);
        getStockUnit(9, "3", R.id.button_9_3);
        getStockUnit(9, "5", R.id.button_9_5);
        getStockUnit(9, "6", R.id.button_9_6);
        getStockUnit(9, "7", R.id.button_9_7);
        getStockUnit(9, "8", R.id.button_9_8);
        getStockUnit(9, "9", R.id.button_9_9);
        getStockUnit(9, "10", R.id.button_9_10);
        getStockUnit(9, "11", R.id.button_9_11);
        getStockUnit(9, "12", R.id.button_9_12);
        getStockUnit(9, "15", R.id.button_9_15);
        getStockUnit(9, "16", R.id.button_9_16);
        getStockUnit(9, "17", R.id.button_9_17);
        getStockUnit(9, "18", R.id.button_9_18);
        getStockUnit(9, "19", R.id.button_9_19);
        getStockUnit(9, "20", R.id.button_9_20);
        getStockUnit(9, "21", R.id.button_9_21);
        getStockUnit(9, "23", R.id.button_9_23);
        getStockUnit(9, "25", R.id.button_9_25);
        getStockUnit(9, "26", R.id.button_9_26);
        getStockUnit(9, "27", R.id.button_9_27);
        getStockUnit(9, "28", R.id.button_9_28);
        getStockUnit(9, "29", R.id.button_9_29);
        getStockUnit(9, "30", R.id.button_9_30);
        getStockUnit(9, "32", R.id.button_9_32);
        getStockUnit(9, "33", R.id.button_9_33);
        getStockUnit(9, "35", R.id.button_9_35);

        getStockUnit(10, "1", R.id.button_10_1);
        getStockUnit(10, "2", R.id.button_10_2);
        getStockUnit(10, "3", R.id.button_10_3);
        getStockUnit(10, "5", R.id.button_10_5);
        getStockUnit(10, "6", R.id.button_10_6);
        getStockUnit(10, "7", R.id.button_10_7);
        getStockUnit(10, "8", R.id.button_10_8);
        getStockUnit(10, "9", R.id.button_10_9);
        getStockUnit(10, "10", R.id.button_10_10);
        getStockUnit(10, "11", R.id.button_10_11);
        getStockUnit(10, "12", R.id.button_10_12);
        getStockUnit(10, "15", R.id.button_10_15);
        getStockUnit(10, "16", R.id.button_10_16);
        getStockUnit(10, "17", R.id.button_10_17);
        getStockUnit(10, "18", R.id.button_10_18);
        getStockUnit(10, "19", R.id.button_10_19);
        getStockUnit(10, "20", R.id.button_10_20);
        getStockUnit(10, "21", R.id.button_10_21);
        getStockUnit(10, "23", R.id.button_10_23);
        getStockUnit(10, "25", R.id.button_10_25);
        getStockUnit(10, "26", R.id.button_10_26);
        getStockUnit(10, "27", R.id.button_10_27);
        getStockUnit(10, "28", R.id.button_10_28);
        getStockUnit(10, "29", R.id.button_10_29);
        getStockUnit(10, "30", R.id.button_10_30);
        getStockUnit(10, "32", R.id.button_10_32);
        getStockUnit(10, "33", R.id.button_10_33);
        getStockUnit(10, "35", R.id.button_10_35);

        getStockUnit(11, "1", R.id.button_11_1);
        getStockUnit(11, "2", R.id.button_11_2);
        getStockUnit(11, "3", R.id.button_11_3);
        getStockUnit(11, "5", R.id.button_11_5);
        getStockUnit(11, "6", R.id.button_11_6);
        getStockUnit(11, "7", R.id.button_11_7);
        getStockUnit(11, "8", R.id.button_11_8);
        getStockUnit(11, "9", R.id.button_11_9);
        getStockUnit(11, "10", R.id.button_11_10);
        getStockUnit(11, "11", R.id.button_11_11);
        getStockUnit(11, "12", R.id.button_11_12);
        getStockUnit(11, "15", R.id.button_11_15);
        getStockUnit(11, "16", R.id.button_11_16);
        getStockUnit(11, "17", R.id.button_11_17);
        getStockUnit(11, "18", R.id.button_11_18);
        getStockUnit(11, "19", R.id.button_11_19);
        getStockUnit(11, "20", R.id.button_11_20);
        getStockUnit(11, "21", R.id.button_11_21);
        getStockUnit(11, "23", R.id.button_11_23);
        getStockUnit(11, "25", R.id.button_11_25);
        getStockUnit(11, "26", R.id.button_11_26);
        getStockUnit(11, "27", R.id.button_11_27);
        getStockUnit(11, "28", R.id.button_11_28);
        getStockUnit(11, "29", R.id.button_11_29);
        getStockUnit(11, "30", R.id.button_11_30);
        getStockUnit(11, "32", R.id.button_11_32);
        getStockUnit(11, "33", R.id.button_11_33);
        getStockUnit(11, "35", R.id.button_11_35);

        getStockUnit(12, "1", R.id.button_12_1);
        getStockUnit(12, "2", R.id.button_12_2);
        getStockUnit(12, "3", R.id.button_12_3);
        getStockUnit(12, "5", R.id.button_12_5);
        getStockUnit(12, "6", R.id.button_12_6);
        getStockUnit(12, "7", R.id.button_12_7);
        getStockUnit(12, "8", R.id.button_12_8);
        getStockUnit(12, "9", R.id.button_12_9);
        getStockUnit(12, "10", R.id.button_12_10);
        getStockUnit(12, "11", R.id.button_12_11);
        getStockUnit(12, "12", R.id.button_12_12);
        getStockUnit(12, "15", R.id.button_12_15);
        getStockUnit(12, "16", R.id.button_12_16);
        getStockUnit(12, "17", R.id.button_12_17);
        getStockUnit(12, "18", R.id.button_12_18);
        getStockUnit(12, "19", R.id.button_12_19);
        getStockUnit(12, "20", R.id.button_12_20);
        getStockUnit(12, "21", R.id.button_12_21);
        getStockUnit(12, "23", R.id.button_12_23);
        getStockUnit(12, "25", R.id.button_12_25);
        getStockUnit(12, "26", R.id.button_12_26);
        getStockUnit(12, "27", R.id.button_12_27);
        getStockUnit(12, "28", R.id.button_12_28);
        getStockUnit(12, "29", R.id.button_12_29);
        getStockUnit(12, "30", R.id.button_12_30);
        getStockUnit(12, "32", R.id.button_12_32);
        getStockUnit(12, "33", R.id.button_12_33);
        getStockUnit(12, "35", R.id.button_12_35);

//        getStockRed(11, 1", R.id.button_11_1);
//        getStockRed(11, 2", R.id.button_11_2);
//        getStockRed(11, 3", R.id.button_11_3);
//        getStockRed(11, 5", R.id.button_11_5);
//        getStockRed(11, 6", R.id.button_11_6);
//        getStockRed(11, 7", R.id.button_11_7);
//        getStockRed(11, 8", R.id.button_11_8);
//        getStockRed(11, 9", R.id.button_11_9);
//        getStockRed(11, 10", R.id.button_11_10);
//        getStockRed(11, 11", R.id.button_11_11);
    }

    private boolean getStockRed(int floor, String unitNumber, int id) {
        for (ApartmentUnit unit: listApartmentUnit) {
            if (unit.getUnitNumber().equals(unitNumber) && unit.getApartmentFloor().getNumber().equals(String.valueOf(floor))){
                BootstrapButton button = getView().findViewById(id);

                button.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                return true;
            }
        }
        return false;
    }

    private void getStockWhite(int floor, String unitNumber, int id) {
        BootstrapButton button = getView().findViewById(id);

        button.setBootstrapBrand(DefaultBootstrapBrand.SECONDARY);
    }

    private void initApartmentUnit() {
        page++;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("page", String.valueOf(page));
        map.put("order_by_desc[0]", "sequence");
        map.put("order_by_desc[1]", "number");
        if (apartmentTower != null){
            map.put("apartment_tower_id", String.valueOf(apartmentTower.getId()));
        } else if (apartment != null){
            map.put("apartment_id", String.valueOf(apartment.getId()));
        }
        apartmentFloorService.populate(map).enqueue(new Callback<ResponseApartmentFloorList>() {
            @Override
            public void onResponse(Call<ResponseApartmentFloorList> call, Response<ResponseApartmentFloorList> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.MATCH_PARENT);
                        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
                        if (response.body().getData() != null && response.body().getData().size() > 0) {
                            List<ApartmentFloor> list = response.body().getData();
                            final ApartmentFloor apartmentFloor = list.get(0);

                            int index = listApartmentFloor.size()-1;

                            for (ApartmentFloor af: listApartmentFloor) {
//                                index = listApartmentFloor.indexOf(af);
//                                listApartmentFloor.get(index).setListApartmentUnit(af.getListApartmentUnit());
//                                if (apartmentFloor.getNumber().compareTo(af.getNumber()) < 0){
//                                    index = listApartmentFloor.indexOf(af);
//                                    listApartmentFloor.get(index).setListApartmentUnit(af.getListApartmentUnit());
//                                    break;
//                                }
                            }

                            TableRow row = new TableRow(context);
                            row.setLayoutParams(tableParams);

                            TextView textView = new TextView(context);
                            textView.setLayoutParams(rowParams);
                            textView.setText(apartmentFloor.getNumber());

                            row.addView(textView);

                            for (final ApartmentUnit unitNumber: listUnitNumber) {
                                ApartmentUnit apartmentUnit = null;

                                for (ApartmentUnit unit: apartmentFloor.getListApartmentUnit()) {
                                    if (unit.getUnitNumber().equals(unitNumber.getUnitNumber())) {
                                        apartmentUnit = unit;
                                        break;
                                    }
                                }

                                BootstrapButton button = new BootstrapButton(context);
                                button.setLayoutParams(rowParams);
                                if (apartmentUnit != null && apartmentUnit.getId() > 0){
                                    button.setId(apartmentUnit.getId());
                                    button.setTag(apartmentUnit.getId());
                                    if (apartmentUnit.getApartmentTypeDesc() != null){
                                        button.setText(apartmentUnit.getApartmentTypeDesc());
                                    } else {
                                        button.setText(apartmentUnit.getUnitNumber());
                                    }

                                    if (Helpers.inArray(apartmentUnit.getStatus(), "empty", "available", "progress", "ready")) {
                                        button.setBootstrapBrand(DefaultBootstrapBrand.SECONDARY);
                                        button.setEnabled(true);
                                    } else {
                                        button.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                                        button.setEnabled(false);
                                    }
                                } else {
                                    button.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                                    button.setText("-");
                                    button.setEnabled(false);
                                }

                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        int id = (int) view.getTag();
                                        Log.d(TAG, "onClick:"+id);
                                        buttonProcess.setEnabled(false);

                                        if (id > 0){
                                            if (lastUnit != null && lastUnit.getId() > 0){
                                                selectedButton.setBootstrapBrand(DefaultBootstrapBrand.SECONDARY);
                                            }

                                            for (ApartmentUnit unit: apartmentFloor.getListApartmentUnit()) {
                                                if (unit.getId() == id) {
                                                    lastUnit = unit;
                                                    selectedButton = (BootstrapButton) view;
                                                    selectedButton.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                                                    buttonProcess.setEnabled(true);
                                                    break;
                                                }
                                            }

                                        }
                                    }
                                });
                                row.addView(button);
                            }

                            customTableLayout.addView(row, index);
                            initApartmentUnit();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseApartmentFloorList> call, Throwable t) {

            }
        });
    }
}
