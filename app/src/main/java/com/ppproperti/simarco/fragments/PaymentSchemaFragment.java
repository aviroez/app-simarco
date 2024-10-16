package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.adapters.SpinnerPaymentSchemaAdapter;
import com.ppproperti.simarco.adapters.SpinnerPaymentTypeAdapter;
import com.ppproperti.simarco.entities.ApartmentUnit;
import com.ppproperti.simarco.entities.Order;
import com.ppproperti.simarco.entities.PaymentSchema;
import com.ppproperti.simarco.entities.PaymentSchemaCalculation;
import com.ppproperti.simarco.entities.PaymentType;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.interfaces.ApartmentUnitService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.OrderService;
import com.ppproperti.simarco.interfaces.PaymentService;
import com.ppproperti.simarco.responses.ResponseApartmentUnit;
import com.ppproperti.simarco.responses.ResponseOrder;
import com.ppproperti.simarco.responses.ResponsePaymentSchemaCalculation;
import com.ppproperti.simarco.responses.ResponsePaymentSchemaList;
import com.ppproperti.simarco.responses.ResponsePaymentTypeList;
import com.ppproperti.simarco.utils.Constants;
import com.ppproperti.simarco.utils.CustomPreferences;
import com.ppproperti.simarco.utils.Helpers;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PaymentSchemaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentSchemaFragment extends Fragment implements View.OnClickListener {
    private Context context;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = PaymentSchemaFragment.class.getSimpleName();

    private BootstrapButton selectedButton = null;

    private OnFragmentInteractionListener mListener;
    private static ApartmentUnit apartmentUnit;
    private List<ApartmentUnit> listGrouped;
    private TextView textFloor;
    private TextView textBedroom;
    private TextView textView;
    private TextView textUnit;
    private TextView textPrice;
    private BootstrapButton buttonClear;
    private BootstrapButton buttonProcess;
    private Call<ResponsePaymentTypeList> call;
    private List<PaymentType> listPaymentType = new ArrayList<>();
    private List<PaymentSchema> listPaymentSchema = new ArrayList<>();
    private Spinner spinnerType;
    private Spinner spinnerSchema;
    private Retrofit retrofit;
    private PaymentService paymentService;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    private View dialogView;
    private TextView textViewTitle;
    private TextView textDp;
    private TextView textInstallment;
    private TextView textCashLabel;
    private PaymentSchema paymentSchema;
    private PaymentSchemaCalculation paymentSchemaCalculation;
    private OrderService serviceOrder;
    private ApartmentUnit lastSelected;
    private BootstrapButton lastButton;
    private View contentView;
    private double dpPercent;
    private double basePrice = 0;
    private int installmentNumber;
    private TextInputEditText textDpPercent;
    private TextInputEditText textCash;
    private TextInputEditText textInstallmentNumber;
    private TextInputEditText textDpInstallment;
    private BootstrapButton buttonCountInstallment;
    private TextInputEditText textOfferedPrice;
    private TextInputEditText textbookingFee;
    private double offeredPrice;
    private double bookingFee;
    private FragmentActivity activity;
    private LinearLayout linearLayoutDp;
    private LinearLayout linearLayoutKpa;
    private LinearLayout linearLayoutAngsuran;
    private LinearLayout linearLayoutCash;
    private boolean parsedCalculation = true;
    private PaymentType paymentType = new PaymentType();
    private boolean counted = false;
    private BootstrapButton buttonBack;
    private double maxDiscount = Constants.MAX_DISCOUNT;
    private ApartmentUnitService apartmentUnitService;
    private double price;
    private int cashNumber;
    private int dpInstallment;
    private Order order = new Order();

    public PaymentSchemaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaymentSchemaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentSchemaFragment newInstance(String param1, String param2) {
        PaymentSchemaFragment fragment = new PaymentSchemaFragment();
        Bundle args = new Bundle();
        args.putParcelable("apartmentUnit", apartmentUnit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            apartmentUnit = getArguments().getParcelable("apartmentUnit");
            order = getArguments().getParcelable("order");

            if (apartmentUnit == null && order != null && order.getApartmentUnit() != null) apartmentUnit = order.getApartmentUnit();

            listGrouped = apartmentUnit.getGrouped();
            basePrice = apartmentUnit.getTotalPrice();
        }

        context = getContext();
        activity = getActivity();

        retrofit = Helpers.initRetrofit(context);
        paymentService = retrofit.create(PaymentService.class);
        apartmentUnitService = retrofit.create(ApartmentUnitService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_schema, container, false);
        contentView = view;

        textFloor = view.findViewById(R.id.text_floor);
        textBedroom = view.findViewById(R.id.text_bedroom);
        textView = view.findViewById(R.id.text_view);
        textUnit = view.findViewById(R.id.text_unit);
        textPrice = view.findViewById(R.id.text_price);
        textDp = view.findViewById(R.id.text_dp);
        textInstallment = view.findViewById(R.id.text_installment);
        textCashLabel = view.findViewById(R.id.text_cash_label);

        buttonClear = view.findViewById(R.id.button_clear);
        buttonProcess = view.findViewById(R.id.button_process);
        buttonBack = view.findViewById(R.id.button_back);

        spinnerType = view.findViewById(R.id.spinner_type);
        spinnerSchema = view.findViewById(R.id.spinner_schema);
        textDpPercent = view.findViewById(R.id.text_dp_percent);
        textDpInstallment = view.findViewById(R.id.text_dp_installment);
        textCash = view.findViewById(R.id.text_cash);
        textInstallmentNumber = view.findViewById(R.id.text_installment_number);
        buttonCountInstallment = view.findViewById(R.id.button_count_installment);
        textOfferedPrice = view.findViewById(R.id.text_offered_price);
        textbookingFee = view.findViewById(R.id.text_booking_fee);
        linearLayoutDp = view.findViewById(R.id.linear_layout_dp);
        linearLayoutKpa = view.findViewById(R.id.linear_layout_kpa);
        linearLayoutAngsuran = view.findViewById(R.id.linear_layout_angsuran);
        linearLayoutCash = view.findViewById(R.id.linear_layout_cash);

//        linearLayoutKpa.setVisibility(View.GONE);
//        linearLayoutAngsuran.setVisibility(View.GONE);

//        textOfferedPrice.addTextChangedListener(new MoneyTextWatcher(textOfferedPrice));
        textOfferedPrice.addTextChangedListener(new NumberTextWatcherForThousand(textOfferedPrice));
//        textbookingFee.addTextChangedListener(new MoneyTextWatcher(textbookingFee));
        textbookingFee.addTextChangedListener(new NumberTextWatcherForThousand(textbookingFee));

        buttonProcess.setEnabled(false);
        buttonProcess.setOnClickListener(this);
        buttonBack.setOnClickListener(this);

//        buttonCountInstallment.setOnClickListener(this);
        buttonCountInstallment.setVisibility(View.GONE);

        parseOrder();

        textDpPercent.addTextChangedListener(new PaymentSchemaCalculationWatcher(textDpPercent));
        textDpInstallment.addTextChangedListener(new PaymentSchemaCalculationWatcher(textDpInstallment));
        textInstallmentNumber.addTextChangedListener(new PaymentSchemaCalculationWatcher(textInstallmentNumber));
        textCash.addTextChangedListener(new PaymentSchemaCalculationWatcher(textCash));

        if (apartmentUnit != null){
            if (apartmentUnit.getApartmentType() != null){
                textBedroom.setText(String.valueOf(apartmentUnit.getApartmentType().getBedroomCount()));
            } else {
                textBedroom.setText("-");
            }
            if (apartmentUnit.getApartmentFloor() != null) {
                textFloor.setText(String.valueOf(apartmentUnit.getApartmentFloor().getNumber()));
            } else {
                textFloor.setText("-");
            }
            if (apartmentUnit.getApartmentView() != null) {
                textView.setText(String.valueOf(apartmentUnit.getApartmentView().getName()));
            } else {
                textView.setText("-");
            }
            textUnit.setText(String.valueOf(apartmentUnit.getUnitNumber()));

            initApartmentUnit();
        }
        initPaymentType();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.choose_payment_schema);
            ((HideShowIconInterface) activity).showBackIcon();
        }

        Log.d(TAG, "onStart:"+listGrouped.size());

        /*
        - Hide init spinner
        HashMap<String, String> map = new HashMap<>();
        map.put("status", "1");
        map.put("order_by_asc", "sequence");
        call = paymentService.listPaymentTypes(map);
        call.enqueue(new Callback<ResponsePaymentTypeList>() {
            @Override
            public void onResponse(Call<ResponsePaymentTypeList> call, Response<ResponsePaymentTypeList> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null){
                    listPaymentType = response.body().getData();
                    Log.d(TAG, "onResponse:listPaymentType:"+listPaymentType.size());

                    initSpinnerType();
                }
            }

            @Override
            public void onFailure(Call<ResponsePaymentTypeList> call, Throwable t) {
                Log.d(TAG, "onFailure:"+t.getMessage());
            }
        });
         */
    }

    private void initPaymentType() {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", "1,2,3");
        paymentService.listPaymentTypes(map).enqueue(new Callback<ResponsePaymentTypeList>() {
            @Override
            public void onResponse(Call<ResponsePaymentTypeList> call, Response<ResponsePaymentTypeList> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        listPaymentType = response.body().getData();

                        if (listPaymentType.size() > 0) {
                            paymentType = listPaymentType.get(0);
                            if (order != null && order.getPaymentType() != null){
                                for (PaymentType pt: listPaymentType) {
                                    if (paymentType.getId() == pt.getId()){
                                        paymentType = pt;
                                        break;
                                    }
                                }
                            }
                            parsePaymentType(paymentType, null);
                        }

                        SpinnerPaymentTypeAdapter arrayAdapter = new SpinnerPaymentTypeAdapter (context, listPaymentType);
                        spinnerType.setAdapter(arrayAdapter);
                        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                paymentType = listPaymentType.get(i);
                                parsePaymentType(paymentType, null);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponsePaymentTypeList> call, Throwable t) {

            }
        });
    }

    private void parsePaymentType(PaymentType paymentType, TextInputEditText editText) {
        Log.d(TAG, "onItemSelected:"+new Gson().toJson(paymentType));
        buttonProcess.setEnabled(false);
        linearLayoutDp.setVisibility(View.GONE);
        linearLayoutAngsuran.setVisibility(View.GONE);
        linearLayoutCash.setVisibility(View.GONE);
//        linearLayoutKpa.setVisibility(View.GONE);
        offeredPrice = Helpers.parseDouble(Helpers.removeNonDigit(textOfferedPrice.getText().toString()));
        if (paymentType != null && paymentType.getCode() != null){
            price = Helpers.parseDouble(Helpers.removeNonDigit(textPrice.getText().toString()));
            Log.d(TAG, "afterTextChanged:paymentType:"+paymentType.getCode());
            dpPercent = 0;
            dpInstallment = 0;
            cashNumber = 0;
            dpInstallment = 0;
            textCashLabel.setVisibility(View.GONE);
            textDp.setVisibility(View.GONE);
            textInstallment.setVisibility(View.GONE);

            if (paymentType.getCode().equals("cash")){
                linearLayoutDp.setVisibility(View.VISIBLE);
                linearLayoutCash.setVisibility(View.VISIBLE);
                if (apartmentUnit != null && apartmentUnit.getTotalPriceCash() > 0 && price <= 0){
                    textPrice.setText(Helpers.currency(apartmentUnit.getTotalPriceCash()));
                    textOfferedPrice.setText(Helpers.currency(apartmentUnit.getTotalPriceCash()));
                }
                dpPercent = Helpers.parseInteger(textDpPercent.getText().toString());
                dpInstallment = Helpers.parseInteger(textDpInstallment.getText().toString());
                cashNumber = Helpers.parseInteger(textCash.getText().toString());

                textDp.setText("DP: "+Helpers.toString(dpPercent)+"% ("+dpInstallment+"x bayar)");
                textDp.setVisibility(View.VISIBLE);

                textCashLabel.setText("Tunai: "+cashNumber+"x bayar");
                textCashLabel.setVisibility(View.VISIBLE);
                Log.d(TAG, "afterTextChanged:dpPercent:"+dpPercent+" dpInstallment:"+dpInstallment+" cashNumber:"+cashNumber);

                if (offeredPrice > 0) buttonProcess.setEnabled(true);
            } else if (paymentType.getCode().equals("installment")){
                linearLayoutDp.setVisibility(View.VISIBLE);
                linearLayoutAngsuran.setVisibility(View.VISIBLE);
                if (apartmentUnit != null && apartmentUnit.getTotalPriceInstallment() > 0 && price == apartmentUnit.getTotalPriceCash()){
                    textPrice.setText(Helpers.currency(apartmentUnit.getTotalPriceInstallment()));
                    textOfferedPrice.setText(Helpers.currency(apartmentUnit.getTotalPriceInstallment()));
                }
                dpPercent = Helpers.parseInteger(textDpPercent.getText().toString());
                dpInstallment = Helpers.parseInteger(textDpInstallment.getText().toString());
                installmentNumber = Helpers.parseInteger(textInstallmentNumber.getText().toString());

                textDp.setText("DP: "+dpPercent+"% ("+dpInstallment+"x bayar)");
                textDp.setVisibility(View.VISIBLE);

                textInstallment.setText("Cicilan: "+installmentNumber+"x");
                textInstallment.setVisibility(View.VISIBLE);

                if (offeredPrice > 0) buttonProcess.setEnabled(true);
            } else if (Helpers.inArray(paymentType.getCode(), "bank", "kpa")){
                linearLayoutDp.setVisibility(View.VISIBLE);
                linearLayoutCash.setVisibility(View.VISIBLE);
                if (apartmentUnit != null && apartmentUnit.getTotalPriceKpa() > 0 && price == apartmentUnit.getTotalPriceCash()){
                    textPrice.setText(Helpers.currency(apartmentUnit.getTotalPriceKpa()));
                    textOfferedPrice.setText(Helpers.currency(apartmentUnit.getTotalPriceKpa()));
                }
                dpPercent = Helpers.parseInteger(textDpPercent.getText().toString());
                dpInstallment = Helpers.parseInteger(textDpInstallment.getText().toString());
                cashNumber = 1;

                textDp.setText("DP: "+dpPercent+"% ("+dpInstallment+"x bayar)");
                textDp.setVisibility(View.VISIBLE);

                textCashLabel.setText("KPA "+cashNumber);
                textCashLabel.setVisibility(View.VISIBLE);

                if (offeredPrice > 0) buttonProcess.setEnabled(true);
            }

            if (editText != null) editText.requestFocus();
        }
    }

    private void initApartmentUnit() {
        HashMap<String, String> map = new HashMap<>();
        map.put("with[0]", "apartment");
        map.put("with[1]", "apartment_tower.apartment");
        map.put("with[2]", "apartment_type");
        map.put("with[3]", "apartment_view");
        map.put("with[4]", "apartment_floor");
        map.put("with[5]", "customer");
        apartmentUnitService.get(apartmentUnit.getId(), map).enqueue(new Callback<ResponseApartmentUnit>() {
            @Override
            public void onResponse(Call<ResponseApartmentUnit> call, Response<ResponseApartmentUnit> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getData() != null){
                        apartmentUnit = response.body().getData();

                        textUnit.setText(String.valueOf(apartmentUnit.getUnitNumber()));
                        if (apartmentUnit.getApartmentType() != null){
                            textBedroom.setText(String.valueOf(apartmentUnit.getApartmentType().getDescription()));
                        } else {
                            textBedroom.setText("-");
                        }

                        if (apartmentUnit.getApartmentFloor() != null) {
                            textFloor.setText(String.valueOf(apartmentUnit.getApartmentFloor().getNumber()));
                        } else {
                            textFloor.setText("-");
                        }

                        if (apartmentUnit.getApartmentView() != null) {
                            textView.setText(String.valueOf(apartmentUnit.getApartmentView().getName()));
                        } else {
                            textView.setText("-");
                        }

                        parsePaymentType(paymentType, null);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseApartmentUnit> call, Throwable t) {

            }
        });
    }

    private void initSpinnerType() {
        SpinnerPaymentTypeAdapter arrayAdapter = new SpinnerPaymentTypeAdapter (context, listPaymentType);
        spinnerType.setAdapter(arrayAdapter);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PaymentType type = listPaymentType.get(i);
                paymentType = type;

                if (paymentType.getCode().equals("kpa")){
                    parseDpKpa();
                } else {
                    textDpPercent.setText("0");
                    textInstallmentNumber.setText("0");
//                    linearLayoutKpa.setVisibility(View.GONE);
                    spinnerSchema.setVisibility(View.VISIBLE);

                    HashMap<String, String> map = new HashMap<>();
                    map.put("payment_type_id", String.valueOf(type.getId()));
                    map.put("with", "payment_type");
                    Call<ResponsePaymentSchemaList> callSchema = paymentService.listPaymentSchemas(map);
                    callSchema.enqueue(new Callback<ResponsePaymentSchemaList>() {
                        @Override
                        public void onResponse(Call<ResponsePaymentSchemaList> call, Response<ResponsePaymentSchemaList> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null){
                                listPaymentSchema = response.body().getData();

                                SpinnerPaymentSchemaAdapter arrayAdapter = new SpinnerPaymentSchemaAdapter (context, listPaymentSchema);
                                spinnerSchema.setAdapter(arrayAdapter);

//                                showSchema();

                                spinnerSchema.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        PaymentSchema paymentSchema = listPaymentSchema.get(i);
                                        if (!paymentType.getCode().equals("kpa")) {
                                            getSchemaPrice(null);
                                            parsePaymentSchema(paymentSchema);
                                        }
//                                        showSchema();

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
//                                        showSchema();

                                        getSchemaPrice(null);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponsePaymentSchemaList> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void parseLiburBayar() {
        double startPercent = 20;
        double maxPercent = 50;

        dpPercent = Helpers.parseDouble(Helpers.removeNonDigit(textDpPercent.getText().toString()));
        installmentNumber = 1;

        double tempPrice = 0;
        if (apartmentUnit != null){
            tempPrice = apartmentUnit.getTotalPrice();
        }
        linearLayoutKpa.setVisibility(View.VISIBLE);
        linearLayoutAngsuran.setVisibility(View.GONE);
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

        Helpers.showSoftwareKeyboard(activity, false);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick:"+view.getId());
        if (view.getId() == R.id.button_back){
            backAction();
        } else if (view.getId() == R.id.button_process){
            processAction();
        } else if (view.getId() == R.id.button_count_installment){
            countInstallmentAction();
        } else {
            lastButton = selectedButton;

            textBedroom.setText("");
            textFloor.setText("");
            textUnit.setText("");
            textView.setText("");
            textPrice.setText("");
        }
    }

    private void backAction() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("order", order);
        bundle.putParcelable("apartmentUnit", apartmentUnit);
        Helpers.moveFragment(context, new ApartmentUnitFragment(), bundle);
    }

    private double currentAmount  = 0;
    private void countInstallmentAction() {
        final boolean kpa = paymentType.getId() == Constants.KPA_ID;
        dpPercent = Helpers.parseDouble(Helpers.removeNonDigit(textDpPercent.getText().toString()));
        installmentNumber = Helpers.parseInteger(Helpers.removeNonDigit(textInstallmentNumber.getText().toString()));

        HashMap<String, String> map = new HashMap<>();
        map.put("dp_percent", String.valueOf(dpPercent));
        map.put("dp_installment", String.valueOf(installmentNumber));

        offeredPrice = Helpers.parseDouble(Helpers.removeNonDigit(textOfferedPrice.getText().toString()));
        bookingFee = Helpers.parseDouble(Helpers.removeNonDigit(textbookingFee.getText().toString()));
        Log.d(TAG, "currentAmount:"+currentAmount);
        if (currentAmount != (offeredPrice-bookingFee) && (offeredPrice-bookingFee) > 0){
            currentAmount = offeredPrice-bookingFee;
            map.put("amount", String.valueOf(currentAmount));
            Call<ResponsePaymentSchemaCalculation> call = paymentService.calculate(paymentSchema.getId(), map);

            call.enqueue(new Callback<ResponsePaymentSchemaCalculation>() {
                @Override
                public void onResponse(Call<ResponsePaymentSchemaCalculation> call, Response<ResponsePaymentSchemaCalculation> response) {
                    if (response.isSuccessful()){
                        paymentSchemaCalculation = response.body().getData();
                        showSchemaUi(kpa);
                        Log.d(TAG, "paymentSchemaCalculation:"+new Gson().toJson(paymentSchemaCalculation));
                        counted = false;
                        countInstallmentAction();
                    }
                }

                @Override
                public void onFailure(Call<ResponsePaymentSchemaCalculation> call, Throwable t) {

                    Log.d(TAG, "paymentSchemaCalculation:onFailure:"+t.getMessage());
                }
            });
        } else {
            counted = false;
        }
    }

    private void showSchema() {
        textDp.setText(R.string.dp_label);
        textInstallment.setText(R.string.installment_label);

        offeredPrice = Helpers.parseDouble(Helpers.removeNonDigit(textOfferedPrice.getText().toString()));
        bookingFee = Helpers.parseDouble(Helpers.removeNonDigit(textbookingFee.getText().toString()));
        double amount = offeredPrice-bookingFee;
        if (paymentType.getCode().equals("kpa")){
            showSchemaUi(true);
        } else {
            if (listPaymentSchema.size() <= 0){
                return;
            }

            try {
                paymentSchema = listPaymentSchema.get(spinnerSchema.getSelectedItemPosition());
            } catch (ArrayIndexOutOfBoundsException ex){

            }

            buttonCountInstallment.setVisibility(View.GONE);
//            linearLayoutKpa.setVisibility(View.GONE);
            spinnerSchema.setVisibility(View.VISIBLE);
            buttonProcess.setEnabled(false);

            if (paymentSchema != null && apartmentUnit != null){
                buttonProcess.setEnabled(true);

                if (paymentSchema.getPaymentType().getCode().equals("kpa")){
                    linearLayoutKpa.setVisibility(View.VISIBLE);
                    spinnerSchema.setVisibility(View.GONE);
                    countInstallmentAction();
                } else {
                    if (paymentSchema.getPaymentType().getCode().equals("libur_bayar")) {
                        linearLayoutKpa.setVisibility(View.VISIBLE);
                        dpPercent = Helpers.parseDouble(Helpers.removeNonDigit(textDpPercent.getText().toString()));
                    }
                    if (parsedCalculation){
                        parsedCalculation = false;
                        offeredPrice = Helpers.parseDouble(Helpers.removeNonDigit(textOfferedPrice.getText().toString()));
                        bookingFee = Helpers.parseDouble(Helpers.removeNonDigit(textbookingFee.getText().toString()));
                        HashMap<String, String> map = new HashMap<>();
                        map.put("amount", String.valueOf(amount));
                        if (paymentSchema.getPaymentType().getCode().equals("libur_bayar")) {
                            map.put("dp_percent", String.valueOf(dpPercent));
                        }
                        Call<ResponsePaymentSchemaCalculation> call = paymentService.calculate(paymentSchema.getId(), map);

                        call.enqueue(new Callback<ResponsePaymentSchemaCalculation>() {
                            @Override
                            public void onResponse(Call<ResponsePaymentSchemaCalculation> call, Response<ResponsePaymentSchemaCalculation> response) {
                                if (response.isSuccessful()){
                                    paymentSchemaCalculation = response.body().getData();
                                    showSchemaUi(false);
                                    Log.d(TAG, "paymentSchemaCalculation:"+new Gson().toJson(paymentSchemaCalculation));
                                    parsedCalculation = true;
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponsePaymentSchemaCalculation> call, Throwable t) {

                                Log.d(TAG, "paymentSchemaCalculation:onFailure:"+t.getMessage());
                            }
                        });
                    }
                }
            }
        }
    }

    private void showSchemaUi(boolean kpa) {
        if (kpa){
            double startPercent = 7;

            dpPercent = Helpers.parseDouble(Helpers.removeNonDigit(textDpPercent.getText().toString()));
            installmentNumber = Helpers.parseInteger(Helpers.removeNonDigit(textInstallmentNumber.getText().toString()));

            double amount = (offeredPrice - bookingFee);

            double addition = installmentNumber / 4;
            double dpKpa = amount * (dpPercent/100);

            Log.d(TAG, "parseDpKpa:"+dpKpa+","+(1 + ((startPercent+addition)/100)));

            if (dpKpa > 0){
                dpKpa = Math.ceil(dpKpa/100000)*100000;;
            }

            String dpValue = getString(R.string.dp_label) + " "
                    +Helpers.currency(dpKpa)
                    + " (" + textInstallmentNumber.getText() + "x bayar)";
            textDp.setText(dpValue);
            String installmentValue = getString(R.string.installment_label) + " "
                    +Helpers.currency(amount - dpKpa)
                    + " (via KPA)";
            textInstallment.setText(installmentValue);
        } else if (paymentSchemaCalculation.getDp() != null){
            String dpValue = getString(R.string.dp_label) + " "
                    +Helpers.currency(paymentSchemaCalculation.getDp().getValue())
                    + " (" + paymentSchemaCalculation.getDp().getInstallment() + "x bayar)";
            textDp.setText(dpValue);

            if (paymentSchemaCalculation.getInstallment() != null){
                String installmentValue = getString(R.string.installment_label) + " "
                        +Helpers.currency(paymentSchemaCalculation.getInstallment().getValue())
                        + " (" + paymentSchemaCalculation.getInstallment().getInstallment() + "x bayar)";
                textInstallment.setText(installmentValue);
            } else if(paymentSchema.getPaymentTo().equals("bank")){
                String installmentValue = getString(R.string.installment_label) + Helpers.currency(apartmentUnit.getTotalPrice() - (apartmentUnit.getTotalPrice()*dpPercent/100))+" (Melalui bank)";
                textInstallment.setText(installmentValue);
            }
        } else {
            if (paymentSchemaCalculation.getInstallment() != null){
                String installmentValue = getString(R.string.installment_label) + " "
                        +Helpers.currency(paymentSchemaCalculation.getInstallment().getValue())
                        + " (" + paymentSchemaCalculation.getInstallment().getInstallment() + "x bayar)";
                textInstallment.setText(installmentValue);
            } else if(paymentSchema.getPaymentTo().equals("bank")){
                String installmentValue = getString(R.string.installment_label) + Helpers.currency(apartmentUnit.getTotalPrice() - (apartmentUnit.getTotalPrice()*dpPercent/100))+" (Melalui bank)";
                textInstallment.setText(installmentValue);
            }
        }
    }

    private void processAction() {
        Helpers.showSoftwareKeyboard(activity, false);
        Log.d(TAG, "processAction:"+new Gson().toJson(apartmentUnit));

        if (apartmentUnit != null && validation()){
            String[] statuses = { "empty", "progress", "ready", "available", "hold" };

            if (Arrays.asList(statuses).contains(apartmentUnit.getStatus())) {
                dpPercent = Helpers.parseDouble(Helpers.removeNonDigit(textDpPercent.getText().toString()));
                installmentNumber = Helpers.parseInteger(Helpers.removeNonDigit(textInstallmentNumber.getText().toString()));

                int totalPrice = Helpers.parseInteger(Helpers.removeNonDigit(textOfferedPrice.getText().toString()));
                bookingFee = Helpers.parseInteger(Helpers.removeNonDigit(textbookingFee.getText().toString()));

                // true
                DateFormat df = new DateFormat();
                serviceOrder = retrofit.create(OrderService.class);
                HashMap<String, String> map = new HashMap<>();
                if (order != null && order.getId() > 0) map.put("id", String.valueOf(order.getId()));
                map.put("apartment_id", String.valueOf(apartmentUnit.getApartmentId()));
                map.put("apartment_unit_id", String.valueOf(apartmentUnit.getId()));
                int tax = (int) Math.ceil(getVatAmountFromGross(totalPrice, 0.1));
                map.put("tax", String.valueOf(tax));
                int price = (int) (totalPrice-tax);
                map.put("price", String.valueOf(price));
                map.put("booking_fee", String.valueOf((int) bookingFee));
//                double discount = basePrice - offeredPrice;
//                if (discount > 1){
//                    map.put("discount", String.valueOf(discount));
//                }
//                int totalPrice = (int) (price+tax);
                map.put("total_price", String.valueOf(totalPrice));
//                map.put("final_price", String.valueOf((int) (totalPrice-discount)));
                map.put("final_price", String.valueOf(totalPrice));

                map.put("order_date", String.valueOf(df.format(Constants.DATE_FORMAT_SQL, new Date())));
                if (paymentSchema != null) map.put("payment_schema_id", String.valueOf(paymentSchema.getId()));
                map.put("payment_type_id", String.valueOf(paymentType.getId()));
                if (dpPercent > 0) map.put("dp_percent", String.valueOf(dpPercent));
                if (dpInstallment > 0) map.put("dp_installment", String.valueOf(dpInstallment));
                if (installmentNumber > 0) map.put("installment_number", String.valueOf(installmentNumber));
                if (cashNumber > 0) map.put("cash_number", String.valueOf(cashNumber));

                CustomPreferences customPreferences = new CustomPreferences(context, "user");
                String userJson = customPreferences.getPreference("user", null);
                User user = new Gson().fromJson(userJson, User.class);
                if (user != null){
                    map.put("marketing_id", String.valueOf(user.getId()));
                }
                Call<ResponseOrder> callOrder = serviceOrder.order(map);

                callOrder.enqueue(new Callback<ResponseOrder>() {
                    @Override
                    public void onResponse(Call<ResponseOrder> call, Response<ResponseOrder> response) {
                        if (response.isSuccessful()){
                            Order order = response.body().getData();

                            CustomPreferences customPreferences = new CustomPreferences(context, "order");
                            customPreferences.savePreferences("order", new Gson().toJson(order));
                            customPreferences.savePreferences("apartmentUnit", new Gson().toJson(apartmentUnit));
                            customPreferences.savePreferences("paymentSchema", new Gson().toJson(paymentSchema));
                            customPreferences.savePreferences("paymentSchemaCalculation", new Gson().toJson(paymentSchemaCalculation));

                            Bundle bundle = new Bundle();
                            bundle.putParcelable("order", order);
                            bundle.putParcelable("apartmentUnit", apartmentUnit);
                            bundle.putParcelable("paymentSchema", paymentSchema);
                            bundle.putParcelable("paymentSchemaCalculation", paymentSchemaCalculation);
                            Helpers.moveFragment(context, new CustomerIdentityFragment(), bundle);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseOrder> call, Throwable t) {
                        Log.d(TAG, "onFailure:"+t.getMessage());
                    }
                });

            } else {
                // false
            }
        }
    }

    private boolean validation() {
        offeredPrice = Helpers.parseDouble(Helpers.removeNonDigit(textOfferedPrice.getText().toString()));
        bookingFee = Helpers.parseDouble(Helpers.removeNonDigit(textbookingFee.getText().toString()));

        if (offeredPrice <= 0){
            Helpers.showLongSnackbar(getView(), R.string.offered_price_is_required);
            textOfferedPrice.requestFocus();
            return false;
        }
//        else if ((basePrice - offeredPrice) >= maxDiscount){
//            Helpers.showLongSnackbar(getView(), getString(R.string.offered_price_sould_not_be_more_than, Helpers.currency(maxDiscount)));
//            textOfferedPrice.requestFocus();
//            return false;
//        }
        else if ((basePrice - offeredPrice) >= maxDiscount){
            Helpers.showLongSnackbar(getView(), getString(R.string.offered_price_sould_not_be_more_than, Helpers.currency(maxDiscount)));
            textOfferedPrice.requestFocus();
            return false;
        } else if (bookingFee <= 0){
            Helpers.showLongSnackbar(getView(), R.string.booking_fee_is_required);
            return false;
        } else if (paymentType.getCode().equals("kpa")){
            dpPercent = Helpers.parseDouble(Helpers.removeNonDigit(textDpPercent.getText().toString()));
            installmentNumber = Helpers.parseInteger(Helpers.removeNonDigit(textInstallmentNumber.getText().toString()));

            if (dpPercent <= 0){
                Helpers.showLongSnackbar(getView(), R.string.dp_percent_should_be_more_than_0);
                return false;
            } else if (installmentNumber <= 0 || installmentNumber > 12){
                Helpers.showLongSnackbar(getView(), R.string.installment_should_be_between_1_and_12);
                return false;
            }
        } else if (paymentType.getCode().equals("libur_bayar")){
            dpPercent = Helpers.parseDouble(Helpers.removeNonDigit(textDpPercent.getText().toString()));

            if (dpPercent < 20 || dpPercent > 50){
                Helpers.showLongSnackbar(getView(), getString(R.string.dp_percent_should_be_between, 20, 50));
//                textDpPercent.requestFocus();
                return false;
            }
        }
        return true;
    }

    public void getSchemaPrice(PaymentSchema paymentSchema) {
        double tempPrice = 0;
        if (apartmentUnit != null){
            tempPrice = apartmentUnit.getTotalPrice();
        }
        if (paymentSchema == null){
            paymentSchema = listPaymentSchema.get(spinnerSchema.getSelectedItemPosition());
        }
        if (paymentSchema != null){
            tempPrice = tempPrice + (tempPrice * (paymentSchema.getNominalRate()/100));
        }

        basePrice = Math.ceil(tempPrice/100000)*100000;;
        textPrice.setText(Helpers.currency(basePrice));
        offeredPrice = basePrice;
        textOfferedPrice.setText(String.format("%.0f", offeredPrice));
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
    public class MoneyTextWatcher implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;
        private String formatted = "";
        private String current = "";

        public MoneyTextWatcher(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
//            String value = editable.toString());
//            String formatted = Helpers.currency(Helpers.parseDouble(value));

//            editText.setText(formatted);
//            editText.setSelection(formatted.length());
            EditText editText = editTextWeakReference.get();
            int selectionStart = editText.getSelectionStart();
            int selectionEnd = editText.getSelectionEnd();
            if (!editable.toString().equals(current)) {
                editText.removeTextChangedListener(this);

//                String symbol = NumberFormat.getCurrencyInstance().getCurrency().getSymbol();
//                String symbol = "Rp";

                String replaceable = String.format("[%s,.\\s]", "");
                String cleanString = editable.toString().replaceAll(replaceable, "");

                double parsed;
                try {
                    parsed = Double.parseDouble(cleanString);
                } catch (NumberFormatException e) {
                    parsed = 0.00;
                }
                NumberFormat formatter = NumberFormat.getCurrencyInstance(Constants.locale);
                formatter.setMaximumFractionDigits(0);
                formatted = formatter.format((parsed));

                current = formatted;
                Log.d(TAG, "selectionStart:"+selectionStart+",selectionEnd:"+selectionEnd+",formatted:"+formatted.length());
                editText.setText(formatted);

                if (selectionStart > formatted.length()){
                    editText.setSelection(formatted.length());
                } else {
                    editText.setSelection(selectionStart);
                }

//                if (editText.getText().toString().startsWith(symbol)){
//                    if (selectionStart >= formatted.length()){
//                        editText.setSelection(formatted.length());
//                    } else {
//                        editText.setSelection(selectionStart);
//                    }
//                }  else if (selectionStart < symbol.length()){
//                    editText.setSelection(symbol.length());
//                }
                editText.addTextChangedListener(this);

                showSchema();
            }
        }
    }

    private TextWatcher parseKpaDp(final String append) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                showSchema();
            }
        };
    }

    private TextWatcher parseKpaInstallment(final String append) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                showSchema();
            }
        };
    }

    private double getVatAmountFromGross(double grossAmount, double taxRate){
        return grossAmount / (1 + (1 / taxRate));
    }

    private void parsePaymentSchema(PaymentSchema paymentSchema){
        if (paymentSchema != null){
            textDpPercent.setText("");
            textInstallmentNumber.setText("");

            if (parsedCalculation){
                parsedCalculation = false;
                offeredPrice = Helpers.parseDouble(Helpers.removeNonDigit(textOfferedPrice.getText().toString()));
                bookingFee = Helpers.parseDouble(Helpers.removeNonDigit(textbookingFee.getText().toString()));
                HashMap<String, String> map = new HashMap<>();
                map.put("amount", String.valueOf(offeredPrice-bookingFee));
                Call<ResponsePaymentSchemaCalculation> call = paymentService.calculate(paymentSchema.getId(), map);

                call.enqueue(new Callback<ResponsePaymentSchemaCalculation>() {
                    @Override
                    public void onResponse(Call<ResponsePaymentSchemaCalculation> call, Response<ResponsePaymentSchemaCalculation> response) {
                        if (response.isSuccessful()){
                            paymentSchemaCalculation = response.body().getData();
                            showSchemaUi(false);
                            Log.d(TAG, "paymentSchemaCalculation:"+new Gson().toJson(paymentSchemaCalculation));
                            parsedCalculation = true;
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsePaymentSchemaCalculation> call, Throwable t) {

                        Log.d(TAG, "paymentSchemaCalculation:onFailure:"+t.getMessage());
                    }
                });
            }
        }
    }

    private void parseDpKpa() {
        double startPercent = 8;

        dpPercent = Helpers.parseDouble(Helpers.removeNonDigit(textDpPercent.getText().toString()));
        installmentNumber = Helpers.parseInteger(Helpers.removeNonDigit(textInstallmentNumber.getText().toString()));

        double tempPrice = 0;
        if (apartmentUnit != null){
            tempPrice = apartmentUnit.getTotalPrice();
        }
        if (paymentType.getId() == Constants.KPA_ID){
            double addition = installmentNumber / 4;
            tempPrice = tempPrice * (1 + ((startPercent+addition)/100));

            Log.d(TAG, "parseDpKpa:"+tempPrice+","+(1 + ((startPercent+addition)/100)));

            if (tempPrice > 0){
                basePrice = Math.ceil(tempPrice/100000)*100000;;
                textPrice.setText(Helpers.currency(basePrice));
                offeredPrice = basePrice;
                textOfferedPrice.setText(String.format("%.0f", offeredPrice));
            }

            linearLayoutKpa.setVisibility(View.VISIBLE);
            linearLayoutAngsuran.setVisibility(View.VISIBLE);
            spinnerSchema.setVisibility(View.GONE);
        }
    }

    private class NumberTextWatcherForThousand implements TextWatcher{
        EditText editText;

        public NumberTextWatcherForThousand(EditText editText) {
            this.editText = editText;


        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            try {
                int selectionStart = editText.getSelectionStart();
                int selectionEnd = editText.getSelectionEnd();
                int startLength = editText.getText().length();

                editText.removeTextChangedListener(this);
                String value = editText.getText().toString();
//                editText.setText(Helpers.currency(value));
//                editText.addTextChangedListener(this);

                if (value != null && !value.equals("")) {
                    if(value.startsWith(".")){
                        editText.setText("0.");
                    }
                    if(value.startsWith("0") && !value.startsWith("0.")){
                        editText.setText("");
                    }

                    String str = editText.getText().toString().replaceAll(",", "");
                    if (value.length() > 0) {
                        String nonDigitValue = Helpers.removeNonDigit(value);
                        if (editText == textOfferedPrice && Helpers.parseInteger(nonDigitValue) > 0) {
                            buttonProcess.setEnabled(true);
                        }
                        editText.setText(Helpers.currency(nonDigitValue, true));
                    }
//                    editText.setSelection(editText.getText().toString().length());

                    int currentLength = editText.getText().length();
                    int diffLength = currentLength - startLength;
                    int nextStart = selectionStart + diffLength;
                    if (nextStart > 0){
                        if (nextStart > editText.getText().toString().length()){
                            editText.setSelection(editText.getText().toString().length());
                        } else {
                            editText.setSelection(nextStart);
                        }
                    } else {
                        editText.setSelection(0);
                    }
                }
                editText.addTextChangedListener(this);
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
                editText.addTextChangedListener(this);
            }
            showSchema();
        }
    }

    private class PaymentSchemaCalculationWatcher implements TextWatcher {
        private TextInputEditText editText;

        public PaymentSchemaCalculationWatcher(TextInputEditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            parsePaymentType(paymentType, editText);
        }
    }

    private void parseOrder(){
        if (order != null){
            if (order.getApartmentUnit() != null){
                if (order.getApartmentUnit().getApartmentFloor() != null){
                    textFloor.setText(order.getApartmentUnit().getApartmentFloor().getName());
                }

                if (order.getApartmentUnit().getApartmentType() != null){
                    textBedroom.setText(order.getApartmentUnit().getApartmentType().getDescription());
                } else if (order.getApartmentUnit().getApartmentTypeDesc() != null){
                    textBedroom.setText(order.getApartmentUnit().getApartmentTypeDesc());
                } else {
                    textBedroom.setText("-");
                }

                if (order.getApartmentUnit().getApartmentView() != null){
                    textView.setText(order.getApartmentUnit().getApartmentView().getName());
                } else {
                    textView.setText("-");
                }

                if (order.getPaymentType() != null && paymentType == null) paymentType = order.getPaymentType();

                textUnit.setText(order.getApartmentUnit().getUnitNumber());
                textPrice.setText(Helpers.currency(order.getTotalPrice()));
                textOfferedPrice.setText(Helpers.currency(order.getTotalPrice()));
                textbookingFee.setText(Helpers.currency(order.getBookingFee()));
                textDpPercent.setText(Helpers.toString(order.getDpPercent()));
                textDpInstallment.setText(String.valueOf(order.getDpInstallment()));
                textInstallmentNumber.setText(String.valueOf(order.getInstallmentNumber()));
                textCash.setText(String.valueOf(order.getCashNumber()));
            }
        }
    }
}
