package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.gson.Gson;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.ApartmentUnit;
import com.ppproperti.simarco.entities.Customer;
import com.ppproperti.simarco.entities.Order;
import com.ppproperti.simarco.entities.PaymentSchema;
import com.ppproperti.simarco.entities.PaymentSchemaCalculation;
import com.ppproperti.simarco.entities.ValueCalculation;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.OrderService;
import com.ppproperti.simarco.interfaces.PaymentService;
import com.ppproperti.simarco.responses.ResponseOrder;
import com.ppproperti.simarco.responses.ResponsePaymentSchemaCalculation;
import com.ppproperti.simarco.utils.Constants;
import com.ppproperti.simarco.utils.Helpers;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InstallmentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InstallmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InstallmentFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = InstallmentFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;
    private Order order;
    private Customer customer;
    private ApartmentUnit apartmentUnit;
    private PaymentSchema paymentSchema;
    private PaymentSchemaCalculation paymentSchemaCalculation;
    private Context context;
    private TextView textBookingNo;
    private TextView textName;
    private TextView textNik;
    private TextView textAddress;
    private TextView textAddressCorespondent;
    private TextView textPhoneNumber;
    private TextView textTower;
    private TextView textUnitNo;
    private TextView textPrice;
    private TextView textDiscount;
    private TextView textPriceAfterDiscount;
    private TextView textTax;
    private TextView textFinalPrice;
    private TextView textAmountInWords;
    private TableLayout tableInstallment;
    private BootstrapButton buttonNext;
    private Retrofit retrofit;
    private FragmentActivity activity;
    private String ktpPath;
    private String npwpPath;
    private TextView textbookingFee;
    private BootstrapButton buttonBack;
    private OrderService orderService;
    private HashMap<String, String> hashMap = new HashMap<>();

    public InstallmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InstallmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InstallmentFragment newInstance(String param1, String param2) {
        InstallmentFragment fragment = new InstallmentFragment();
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
            order = getArguments().getParcelable("order");
            customer = getArguments().getParcelable("customer");
            apartmentUnit = getArguments().getParcelable("apartmentUnit");
            paymentSchema = getArguments().getParcelable("paymentSchema");
            paymentSchemaCalculation = getArguments().getParcelable("paymentSchemaCalculation");
            ktpPath = getArguments().getString("ktpPath");
            npwpPath = getArguments().getString("npwpPath");
        }
        context = getContext();
        activity = getActivity();

        retrofit = Helpers.initRetrofit(context);
        orderService = retrofit.create(OrderService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_installment, container, false);

        textBookingNo = view.findViewById(R.id.text_booking_no);
        textName = view.findViewById(R.id.text_name);
        textNik = view.findViewById(R.id.text_nik);
        textAddress = view.findViewById(R.id.text_address);
        textAddressCorespondent = view.findViewById(R.id.text_address_corespondent);
        textPhoneNumber = view.findViewById(R.id.text_phone_number);

        textTower = view.findViewById(R.id.text_tower);
        textUnitNo = view.findViewById(R.id.text_unit_no);

        textPrice = view.findViewById(R.id.text_price);
        textDiscount = view.findViewById(R.id.text_discount);
        textPriceAfterDiscount = view.findViewById(R.id.text_price_after_discount);
        textTax = view.findViewById(R.id.text_tax);
        textFinalPrice = view.findViewById(R.id.text_final_price);
        textbookingFee = view.findViewById(R.id.text_booking_fee);
        textAmountInWords = view.findViewById(R.id.text_amount_in_words);

        tableInstallment = view.findViewById(R.id.table_installment);
        buttonBack = view.findViewById(R.id.button_back);
        buttonNext = view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(this);
        buttonBack.setOnClickListener(this);

        parseInstallments();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.detail_sps);
            ((HideShowIconInterface) activity).showBackIcon();
        }

        if (order != null){
            textBookingNo.setText(order.getBookingNo());
            if (customer != null){
                String nik = customer.getNik();
                if (customer.getNpwp() != null && customer.getNpwp().length() > 0){
                    nik += " / NPWP: "+customer.getNpwp();
                }
                String phoneNumber = customer.getHandphone();
                if (customer.getEmail() != null){
                    phoneNumber += " / Email: "+customer.getEmail();
                }
                textName.setText(customer.getName());
                textNik.setText(nik);
                textAddress.setText(customer.getAddress());
                textAddressCorespondent.setText(customer.getAddressCorrespondent());
                textPhoneNumber.setText(phoneNumber);
            } else {
                String nik = order.getNik();
                if (order.getNpwp() != null && order.getNpwp().length() > 0){
                    nik += " / NPWP: "+order.getNpwp();
                }
                String phoneNumber = order.getPhoneNumber();
                if (order.getEmail() != null){
                    phoneNumber += " / Email: "+order.getEmail();
                }
                textName.setText(order.getName());
                textNik.setText(nik);
                textAddress.setText(order.getAddress());
                textAddressCorespondent.setText("-");
                textPhoneNumber.setText(phoneNumber);
            }

            textPrice.setText(Helpers.currency(order.getPrice()));
            textDiscount.setText(Helpers.currency(order.getDiscount()));
            textPriceAfterDiscount.setText(Helpers.currency(order.getTotalPrice()));
            textTax.setText(Helpers.currency(order.getTax()));
            textbookingFee.setText(Helpers.currency(order.getbookingFee()));
            textFinalPrice.setText(Helpers.currency(order.getFinalPrice()));
            textAmountInWords.setText("-");

            if (order.getApartmentUnit() != null){
                String tower = "1";
                if (order.getApartmentUnit().getApartmentTower() != null) {
                    tower = order.getApartmentUnit().getApartmentTower().getName();
                }

                if (order.getApartmentUnit().getApartmentFloor() != null) {
                    tower += "   Lantai:"+order.getApartmentUnit().getApartmentFloor().getNumber();
                }

                String unitNo = String.valueOf(order.getApartmentUnit().getUnitNumber());
//                if (order.getApartmentUnit().getApartmentType() != null) {
//                    unitNo += " " + order.getApartmentUnit().getApartmentType().getBedroomCount() + " KT ";
//                    unitNo += " Luas Gross:" + order.getApartmentUnit().getApartmentType().getSurfacesGross() + " m2 ";
//                    unitNo += " Luas Nett:" + order.getApartmentUnit().getApartmentType().getSurfacesNett() + " m2 ";
//                }
                textTower.setText(tower);
                textUnitNo.setText(unitNo);
            }

//            calculateSchema();
            calculateSchemaDetail();
        }
    }

    private void calculateSchemaDetail() {
        orderService.show(order.getId(), new HashMap<String, String>()).enqueue(new Callback<ResponseOrder>() {
            @Override
            public void onResponse(Call<ResponseOrder> call, Response<ResponseOrder> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        order = response.body().getData();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseOrder> call, Throwable t) {

            }
        });

    }

    private void calculateSchema() {
        PaymentService service = retrofit.create(PaymentService.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("amount", String.valueOf(order.getFinalPrice()-order.getbookingFee()));
        map.put("dp_percent", String.valueOf(order.getDpPercent()));
        map.put("dp_installment", String.valueOf(order.getDpInstallment()));

        if (order.getPaymentTypeId() == Constants.KPA_ID){
            map.put("payment_type_id", String.valueOf(order.getPaymentTypeId()));
        }
        Call<ResponsePaymentSchemaCalculation> call = service.calculate(paymentSchema.getId(), map);

        call.enqueue(new Callback<ResponsePaymentSchemaCalculation>() {
            @Override
            public void onResponse(Call<ResponsePaymentSchemaCalculation> call, Response<ResponsePaymentSchemaCalculation> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null){
                    paymentSchemaCalculation = response.body().getData();
                    Calendar cal = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_LOCAL_M);

                    int number = 0;
                    if (paymentSchemaCalculation.getDp() != null){
                        ValueCalculation dpCalc = paymentSchemaCalculation.getDp();
                        int dpIndex = 0;
                        for (int i = 0; i < dpCalc.getInstallment(); i++) {
                            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                            params.setMargins(5, 0, 5, 0);

                            TableRow tr = new TableRow(context);
                            TextView tv = new TextView(context);
                            cal.add(Calendar.MONTH, 1);

                            tv.setText(String.valueOf(++number));
                            tv.setLayoutParams(params);
                            tr.addView(tv);

                            params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                            params.setMargins(5, 0, 5, 0);

                            tv = new TextView(context);
                            if (dpCalc.getInstallment() > 1){
                                tv.setText("DP "+(++dpIndex));
                            } else {
                                tv.setText("DP ");
                            }
                            tv.setLayoutParams(params);
                            tr.addView(tv);

                            tv = new TextView(context);
                            tv.setText(Helpers.currency(dpCalc.getValue()));
                            tv.setLayoutParams(params);
                            tr.addView(tv);

                            tv = new TextView(context);
                            tv.setText(df.format(cal.getTime()));
                            tv.setLayoutParams(params);
                            tr.addView(tv);
                            tableInstallment.addView(tr);
                        }
                    }

                    if (paymentSchemaCalculation.getInstallment() != null){
                        ValueCalculation installmentCalc = paymentSchemaCalculation.getInstallment();
                        int instNumber = 0;
                        for (int i = 0; i < installmentCalc.getInstallment(); i++) {
                            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                            params.setMargins(5, 0, 5, 0);

                            TableRow tr = new TableRow(context);
                            TextView tv = new TextView(context);
                            cal.add(Calendar.MONTH, 1);

                            tv.setText(String.valueOf(++number));
                            tv.setLayoutParams(params);
                            tr.addView(tv);

                            params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                            params.setMargins(5, 0, 5, 0);

                            tv = new TextView(context);
                            tv.setText("Installment "+(++instNumber));
                            tv.setLayoutParams(params);
                            tr.addView(tv);

                            tv = new TextView(context);
                            tv.setText(Helpers.currency(installmentCalc.getValue()));
                            tv.setLayoutParams(params);
                            tr.addView(tv);

                            if (order.getPaymentTypeId() == Constants.LIBUR_BAYAR_ID){
                                tv = new TextView(context);
                                tv.setText("31 Des 2020");
                                tv.setLayoutParams(params);
                                tr.addView(tv);
                            } else {
                                tv = new TextView(context);
                                tv.setText(df.format(cal.getTime()));
                                tv.setLayoutParams(params);
                                tr.addView(tv);
                            }

                            tableInstallment.addView(tr);
                        }
                    }

                    if (order.getPaymentTypeId() == Constants.KPA_ID){
                        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                        params.setMargins(5, 0, 5, 0);

                        TableRow tr = new TableRow(context);
                        TextView tv = new TextView(context);

                        tv.setText(String.valueOf(++number));
                        tv.setLayoutParams(params);
                        tr.addView(tv);

                        params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                        params.setMargins(5, 0, 5, 0);

                        tv = new TextView(context);
                        tv.setText("Sisa KPA");
                        tv.setLayoutParams(params);
                        tr.addView(tv);

                        tv = new TextView(context);
                        tv.setText(Helpers.currency(order.getFinalPrice() - (order.getFinalPrice() * (order.getDpPercent()/100))));
                        tv.setLayoutParams(params);
                        tr.addView(tv);

                        tv = new TextView(context);
                        tv.setText("Dari Bank");
                        tv.setLayoutParams(params);
                        tr.addView(tv);
                        tableInstallment.addView(tr);
                    }
//                    showSchemaUi();
                    Log.d(TAG, "paymentSchemaCalculation:"+new Gson().toJson(paymentSchemaCalculation));
                }
            }

            @Override
            public void onFailure(Call<ResponsePaymentSchemaCalculation> call, Throwable t) {

                Log.d(TAG, "paymentSchemaCalculation:onFailure:"+t.getMessage());
            }
        });
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
        if (view.getId() == buttonNext.getId()){
            nextAction();
        } else if (view.getId() == buttonBack.getId()){
            backAction();
        }
    }

    private void backAction() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("order", order);
        bundle.putParcelable("customer", customer);
        bundle.putParcelable("apartmentUnit", apartmentUnit);
        bundle.putParcelable("paymentSchema", paymentSchema);
        bundle.putParcelable("paymentSchemaCalculation", paymentSchemaCalculation);
        bundle.putString("ktpPath", ktpPath);
        bundle.putString("npwpPath", npwpPath);

        Helpers.moveFragment(context, new CameraFragment(), bundle);
    }

    private void nextAction() {
        orderService.installments(order.getId(), hashMap).enqueue(new Callback<ResponseOrder>() {
            @Override
            public void onResponse(Call<ResponseOrder> call, Response<ResponseOrder> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("order", order);
                        bundle.putParcelable("customer", customer);
                        bundle.putParcelable("apartmentUnit", apartmentUnit);
                        bundle.putParcelable("paymentSchema", paymentSchema);
                        bundle.putParcelable("paymentSchemaCalculation", paymentSchemaCalculation);
                        bundle.putString("ktpPath", ktpPath);
                        bundle.putString("npwpPath", npwpPath);

                        Helpers.moveFragment(context, new TosFragment(), bundle);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseOrder> call, Throwable t) {

            }
        });
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

    private void parseInstallments() {
        Calendar cal = Calendar.getInstance();

        hashMap = new HashMap<>();

        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_SQL);
        DecimalFormat decimalFormat = new DecimalFormat("#");
        decimalFormat.setMaximumFractionDigits(15);

        if (order != null){
            int number = 0;
            double finalPrice = order.getFinalPrice();
            double calculationPrice = 0;
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 0, 5, 0);
            if (order.getBookingFee() > 0){
                TableRow tr = new TableRow(context);
                TextView tv = new TextView(context);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 3);

                hashMap.put("description["+number+"]", "Booking Fee 1");
                hashMap.put("installment_price["+number+"]", decimalFormat.format(order.getBookingFee()));
                hashMap.put("due_date["+number+"]", df.format(calendar.getTime()));
                hashMap.put("type["+number+"]", "booking_fee");

                tv.setText(String.valueOf(++number));
                tv.setLayoutParams(params);
                tr.addView(tv);

                params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                params.setMargins(5, 0, 5, 0);

                tv = new TextView(context);
                tv.setText("Booking Fee 1");
                tv.setLayoutParams(params);
                tr.addView(tv);

                tv = new TextView(context);
                tv.setText(Helpers.currency(order.getBookingFee()));
                tv.setLayoutParams(params);
                tr.addView(tv);

                tv = new TextView(context);
                tv.setText(df.format(calendar.getTime()));
                tv.setLayoutParams(params);
                tr.addView(tv);
                tableInstallment.addView(tr);

                calculationPrice += order.getBookingFee();
            }

            if (order.getDpPercent() > 0 && order.getDpInstallment() > 0){
                double value = Math.floor((finalPrice-calculationPrice) * (order.getDpPercent() / 100) / order.getDpInstallment());
                for (int i = 0; i < order.getDpInstallment(); i++) {
                    TableRow tr = new TableRow(context);
                    TextView tv = new TextView(context);
                    cal.add(Calendar.MONTH, 1);

                    hashMap.put("description["+number+"]", "Down Payment "+(i+1));
                    hashMap.put("installment_price["+number+"]", decimalFormat.format(value));
                    hashMap.put("due_date["+number+"]", df.format(cal.getTime()));
                    hashMap.put("type["+number+"]", "dp");

                    tv.setText(String.valueOf(++number));
                    tv.setLayoutParams(params);
                    tr.addView(tv);

                    params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    params.setMargins(5, 0, 5, 0);

                    tv = new TextView(context);
                    tv.setText("Down Payment "+(i+1));
                    tv.setLayoutParams(params);
                    tr.addView(tv);

                    tv = new TextView(context);
                    tv.setText(Helpers.currency(value));
                    tv.setLayoutParams(params);
                    tr.addView(tv);

                    tv = new TextView(context);
                    tv.setText(df.format(cal.getTime()));
                    tv.setLayoutParams(params);
                    tr.addView(tv);
                    tableInstallment.addView(tr);

                    calculationPrice += value;
                }
            }

            if (order.getInstallmentNumber() > 0){
                double firstValue = 0;
                double value = Math.floor((finalPrice - calculationPrice) / order.getInstallmentNumber());
                if (order.getInstallmentNumber() > 1){
                    firstValue = (finalPrice - calculationPrice) - ((finalPrice - calculationPrice) * (order.getInstallmentNumber()-1));
                }

                for (int i = 0; i < order.getInstallmentNumber(); i++) {
                    TableRow tr = new TableRow(context);
                    TextView tv = new TextView(context);
                    cal.add(Calendar.MONTH, 1);

                    hashMap.put("description["+number+"]", "Installment "+(i+1));
                    if (i == 0 && firstValue > value) hashMap.put("installment_price["+number+"]", decimalFormat.format(firstValue));
                    else hashMap.put("installment_price["+number+"]", decimalFormat.format(value));

                    hashMap.put("due_date["+number+"]", df.format(cal.getTime()));
                    hashMap.put("type["+number+"]", "installment");

                    tv.setText(String.valueOf(++number));
                    tv.setLayoutParams(params);
                    tr.addView(tv);

                    params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    params.setMargins(5, 0, 5, 0);

                    tv = new TextView(context);
                    tv.setText("Installment "+(i+1));
                    tv.setLayoutParams(params);
                    tr.addView(tv);

                    tv = new TextView(context);
                    if (i == 0 && firstValue > value){
                        calculationPrice += firstValue;
                        tv.setText(Helpers.currency(firstValue));
                    } else {
                        calculationPrice += value;
                        tv.setText(Helpers.currency(value));
                    }
                    tv.setLayoutParams(params);
                    tr.addView(tv);

                    tv = new TextView(context);
                    tv.setText(df.format(cal.getTime()));
                    tv.setLayoutParams(params);
                    tr.addView(tv);
                    tableInstallment.addView(tr);
                }
            }

            if (order.getCashNumber() > 0){
                double firstValue = 0;
                double value = Math.floor((finalPrice - calculationPrice) / order.getCashNumber());
                if (order.getCashNumber() > 1){
                    firstValue = (finalPrice - calculationPrice) - ((finalPrice - calculationPrice) * (order.getCashNumber()-1));
                }

                for (int i = 0; i < order.getCashNumber(); i++) {
                    TableRow tr = new TableRow(context);
                    TextView tv = new TextView(context);
                    cal.add(Calendar.MONTH, 1);

                    hashMap.put("description["+number+"]", "Cash "+(i+1));
                    if (i == 0 && firstValue > value) hashMap.put("installment_price["+number+"]", decimalFormat.format(firstValue));
                    else hashMap.put("installment_price["+number+"]", decimalFormat.format(value));
                    hashMap.put("due_date["+number+"]", df.format(cal.getTime()));
                    hashMap.put("type["+number+"]", "tunai");

                    tv.setText(String.valueOf(++number));
                    tv.setLayoutParams(params);
                    tr.addView(tv);

                    params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    params.setMargins(5, 0, 5, 0);

                    tv = new TextView(context);
                    tv.setText("Cash "+(i+1));
                    tv.setLayoutParams(params);
                    tr.addView(tv);

                    tv = new TextView(context);
                    if (i == 0 && firstValue > value){
                        calculationPrice += firstValue;
                        tv.setText(Helpers.currency(firstValue));
                    } else {
                        calculationPrice += value;
                        tv.setText(Helpers.currency(value));
                    }
                    tv.setLayoutParams(params);
                    tr.addView(tv);

                    tv = new TextView(context);
                    tv.setText(df.format(cal.getTime()));
                    tv.setLayoutParams(params);
                    tr.addView(tv);
                    tableInstallment.addView(tr);

                    calculationPrice += value;
                }
            }
        }
    }
}
