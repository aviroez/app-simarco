package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.adapters.CustomerLeadJoinAdapter;
import com.ppproperti.simarco.adapters.SpinnerCustomerAdapter;
import com.ppproperti.simarco.entities.ApartmentUnit;
import com.ppproperti.simarco.entities.Customer;
import com.ppproperti.simarco.entities.CustomerLead;
import com.ppproperti.simarco.entities.CustomerLeadJoin;
import com.ppproperti.simarco.entities.CustomerLeadPagination;
import com.ppproperti.simarco.entities.CustomerPagination;
import com.ppproperti.simarco.entities.Order;
import com.ppproperti.simarco.entities.PaymentSchema;
import com.ppproperti.simarco.entities.PaymentSchemaCalculation;
import com.ppproperti.simarco.entities.User;
import com.ppproperti.simarco.interfaces.CustomerLeadService;
import com.ppproperti.simarco.interfaces.CustomerService;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.OrderService;
import com.ppproperti.simarco.responses.ResponseCustomer;
import com.ppproperti.simarco.responses.ResponseCustomerLeadList;
import com.ppproperti.simarco.responses.ResponseCustomerLeadListPagination;
import com.ppproperti.simarco.responses.ResponseCustomerList;
import com.ppproperti.simarco.responses.ResponseCustomerListPagination;
import com.ppproperti.simarco.responses.ResponseOrder;
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
 * {@link CustomerIdentityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomerIdentityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerIdentityFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = CustomerIdentityFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ApartmentUnit apartmentUnit;
    private PaymentSchema paymentSchema;
    private PaymentSchemaCalculation paymentSchemaCalculation;
    private Order order;
    private Customer customer;
    private Context context;
    private TextInputEditText textFullName;
    private TextInputEditText textNik;
    private TextInputEditText textNpwp;
    private TextInputEditText textHandphone;
    private TextInputEditText textEmail;
    private Spinner spinnerCustomer;
    private Retrofit retrofit;
    private User user = new User();
    private List<Customer> listCustomer = new ArrayList<>();
    private List<CustomerLead> listCustomerLead = new ArrayList<>();
    private Customer customerSelected;
    private String fullName;
    private String nik;
    private String npwp;
    private String handphone;
    private String email;
    private FragmentActivity activity;
    private TextInputEditText textAddressCorrespondent;
    private String addressCorrespondent;
    private TextInputEditText textAddress;
    private String address;
    private RadioGroup radioGroup;
    private RadioButton radioMale;
    private RadioButton radioFemale;
    private String gender;
    private OrderService orderService;
    private MaterialAutoCompleteTextView textCustomerList;
    private String search = "";
    private boolean customerProcess = false;
    private boolean customerLeadProcess = false;
    private CustomerPagination customerPagination = new CustomerPagination();
    private CustomerLeadPagination customerLeadPagination = new CustomerLeadPagination();
    private List<CustomerLeadJoin> listCustomerLeadJoin = new ArrayList<>();
    private CustomerLeadJoin customerLeadJoin = new CustomerLeadJoin();

    public CustomerIdentityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new customer of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerIdentityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerIdentityFragment newInstance(String param1, String param2) {
        CustomerIdentityFragment fragment = new CustomerIdentityFragment();
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
        }

        context = getContext();
        activity = getActivity();

        user = new CustomPreferences(context).getUser();
        retrofit = Helpers.initRetrofit(context);
        orderService = retrofit.create(OrderService.class);

        if (customer == null){
            if (order.getCustomer() != null){
                customer = order.getCustomer();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_identity, container, false);
        textCustomerList = view.findViewById(R.id.text_customer_list);
        spinnerCustomer = view.findViewById(R.id.spinner_customer);
        textFullName = view.findViewById(R.id.text_full_name);
        textNik = view.findViewById(R.id.text_nik);
        textNpwp = view.findViewById(R.id.text_npwp);
        textHandphone = view.findViewById(R.id.text_hand_phone);
        textEmail = view.findViewById(R.id.text_email);
        textAddress = view.findViewById(R.id.text_address);
        textAddressCorrespondent = view.findViewById(R.id.text_address_correspondent);
        radioGroup = view.findViewById(R.id.radio_group);
        radioMale = view.findViewById(R.id.radio_male);
        radioFemale = view.findViewById(R.id.radio_female);
        BootstrapButton buttonNext = view.findViewById(R.id.button_next);
        BootstrapButton buttonBack = view.findViewById(R.id.button_back);

        radioGroup.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
        buttonNext.setOnClickListener(this);

//        textCustomerList.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                if (!search.equalsIgnoreCase(textCustomerList.getText().toString())){
//                    search = textCustomerList.getText().toString();
//
//                    if (!customerProcess) parseCustomer();
//                    if (!customerLeadProcess) parseCustomerLead();
//                    Log.d(TAG, "onKey:"+search);
//                }
//                return false;
//            }
//        });

        parseCustomer();
        parseCustomerLead();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.customer_identity);
            ((HideShowIconInterface) activity).showBackIcon();
        }
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
        if (view.getId() == R.id.button_back) {
            backAction();
        } else if (view.getId() == R.id.button_next){
            nextAction();
        }
    }

    private void backAction(){
        Bundle bundle = new Bundle();
        bundle.putParcelable("order", order);
        bundle.putParcelable("customer", customer);
        bundle.putParcelable("apartmentUnit", apartmentUnit);
        bundle.putParcelable("paymentSchema", paymentSchema);
        bundle.putParcelable("paymentSchemaCalculation", paymentSchemaCalculation);

        Helpers.moveFragment(context, new PaymentSchemaFragment(), bundle);

    }

    private void nextAction() {
        Helpers.showSoftwareKeyboard(activity, false);

        if (validation()){
            Log.d(TAG, "nextAction:validation:");
            Helpers.showSoftwareKeyboard(activity, false);
            customer = new Customer();
            customer.setName(fullName);
            customer.setNik(nik);
            customer.setNpwp(npwp);
            customer.setHandphone(handphone);
            customer.setEmail(email);
            customer.setAddress(address);
            customer.setAddressCorrespondent(addressCorrespondent);
            customer.setGender(gender);

            if (customerSelected != null && customerSelected.getId() > 0) {
                customer.setId(customerSelected.getId());
                order.setCustomerId(customerSelected.getId());
            }
            HashMap<String, String> map = new HashMap<>();
            map.put("name", customer.getName());
            map.put("nik", customer.getNik());
            map.put("npwp", customer.getNpwp());
            map.put("handphone", customer.getHandphone());
            map.put("address", customer.getAddress());
            map.put("address_correspondent", customer.getAddressCorrespondent());
            map.put("email", customer.getEmail());
            map.put("gender", customer.getGender());
            if (customerLeadJoin != null){
                if (customerLeadJoin.getCustomerId() > 0){
                    map.put("customer_id", String.valueOf(customerLeadJoin.getCustomerId()));
                } else if(customerLeadJoin.getCustomerLead() != null){
                    map.put("customer_lead_id", String.valueOf(customerLeadJoin.getCustomerLeadId()));
                }
            }

//            if (customer != null && customer.getId() > 0){
//                map.put("customer_id", String.valueOf(customer.getId()));
//            }

            if (user != null){
                map.put("marketing_id", String.valueOf(user.getId()));
            }

            orderService.identity(order.getId(), map).enqueue(new Callback<ResponseOrder>() {
                @Override
                public void onResponse(Call<ResponseOrder> call, Response<ResponseOrder> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null){
                            order = response.body().getData();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("order", order);
                            bundle.putParcelable("customer", customer);
                            bundle.putParcelable("apartmentUnit", apartmentUnit);
                            bundle.putParcelable("paymentSchema", paymentSchema);
                            bundle.putParcelable("paymentSchemaCalculation", paymentSchemaCalculation);

                            Helpers.moveFragment(context, new CameraFragment(), bundle);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseOrder> call, Throwable t) {

                }
            });
        } else {
                /*
                CustomerService service = retrofit.create(CustomerService.class);
                HashMap<String, String> map = new HashMap<>();
                map.put("handphone", handphone);
                map.put("email", email);
                map.put("nik", nik);
                Call<ResponseCustomer> call = service.check(map);
                call.enqueue(new Callback<ResponseCustomer>() {
                    @Override
                    public void onResponse(Call<ResponseCustomer> call, Response<ResponseCustomer> response) {
                        if (response.isSuccessful()){
                            Customer respCust = response.body().getData();

                            if (respCust != null && respCust.getMarketingId() > 0 && respCust.getMarketingId() != user.getId()){
                                if (respCust.getNik().equals(customer.getNik())){
                                    Helpers.showLongSnackbar(getView(), R.string.nik_is_already_registered_with_different_marketing);
                                    return;
                                } else if (respCust.getHandphone().equals(customer.getHandphone()) || respCust.getHandphone().equals(Helpers.formatPhone(customer.getHandphone()))){
                                    Helpers.showLongSnackbar(getView(), R.string.handphone_is_already_registered_with_different_marketing);
                                    return;
                                } else if(respCust.getEmail().equals(customer.getEmail())){
                                    Helpers.showLongSnackbar(getView(), R.string.email_is_already_registered_with_different_marketing);
                                    return;
                                }
                            }

                            Bundle bundle = new Bundle();
                            bundle.putParcelable("order", order);
                            bundle.putParcelable("customer", customer);
                            bundle.putParcelable("apartmentUnit", apartmentUnit);
                            bundle.putParcelable("paymentSchema", paymentSchema);
                            bundle.putParcelable("paymentSchemaCalculation", paymentSchemaCalculation);

                            Helpers.moveFragment(context, new CameraFragment(), bundle);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseCustomer> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                 */

        }
    }

    private boolean validation() {
        customerSelected = null;
        try {
            customerSelected = listCustomer.get(spinnerCustomer.getSelectedItemPosition());
        } catch (Exception e){

        }

        fullName = textFullName.getText().toString();
        nik = textNik.getText().toString();
        npwp = textNpwp.getText().toString();
        handphone = textHandphone.getText().toString();
        email = textEmail.getText().toString();
        address = textAddress.getText().toString();
        addressCorrespondent = textAddressCorrespondent.getText().toString();

        if (radioMale.isChecked()){
            gender = Constants.MALE;
        } else if(radioFemale.isChecked()){
            gender = Constants.FEMALE;
        }

        if (fullName.length() <= 0){
            Helpers.showLongSnackbar(getView(), R.string.full_name_is_required);
            textFullName.requestFocus();
            return false;
        } else if (nik.length() <= 0){
            Helpers.showLongSnackbar(getView(), R.string.nik_is_required);
            textNik.requestFocus();
            return false;
        } else if (handphone.length() <= 0){
            Helpers.showLongSnackbar(getView(), R.string.handphone_is_required);
            textHandphone.requestFocus();
            return false;
        } else if (email.length() <= 0){
            Helpers.showLongSnackbar(getView(), R.string.email_is_required);
            textEmail.requestFocus();
            return false;
        } else if (gender == null || gender.length() <= 0){
            Helpers.showLongSnackbar(getView(), R.string.gender_is_required);
            radioGroup.requestFocus();
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

    private void parseCustomer() {
        customerProcess = true;
        CustomerService service = retrofit.create(CustomerService.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("marketing_id", String.valueOf(user.getId()));
        if (search.length() > 0) map.put("search", search);
        Call<ResponseCustomerListPagination> call = service.customers(map, 5);
        call.enqueue(new Callback<ResponseCustomerListPagination>() {
            @Override
            public void onResponse(Call<ResponseCustomerListPagination> call, Response<ResponseCustomerListPagination> response) {
                listCustomer = new ArrayList<>();
                if (response.isSuccessful()){
                    if (response.body() != null) {
                        if (response.body().getData() != null) {
                            customerPagination = response.body().getData();
                            if (customerPagination.getData() != null){
                                listCustomer = response.body().getData().getData();
                            }
                        }
                    }
/*
                    listCustomer.add(0, new Customer(0, "Tambah Pelanggan Baru", null));
                    SpinnerCustomerAdapter arrayAdapter = new SpinnerCustomerAdapter (context, listCustomer);
                    spinnerCustomer.setAdapter(arrayAdapter);
                    Log.d(TAG, "onResponse:"+order.getCustomerId());

                    if (order.getCustomerId() > 0){
                        for (Customer c: listCustomer) {
                            if (c.getId() == order.getCustomerId()){
                                setCustomerSelected(c);
                                spinnerCustomer.setSelection(listCustomer.indexOf(c));
                                break;
                            }
                        }
                    }

                    radioGroup.clearCheck();

                    spinnerCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            customerSelected = listCustomer.get(i);

                            setCustomerSelected(customerSelected);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
 */
                }
                customerProcess = false;
                joinCustomerLead();
            }

            @Override
            public void onFailure(Call<ResponseCustomerListPagination> call, Throwable t) {
                customerProcess = false;
            }
        });
    }

    private void parseCustomerLead() {
        customerLeadProcess = true;
        listCustomerLead = new ArrayList<>();
        CustomerLeadService service = retrofit.create(CustomerLeadService.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("marketing_id", String.valueOf(user.getId()));
        if (search.length() > 0) map.put("search", search);
        Call<ResponseCustomerLeadListPagination> call = service.get(map, 5);
        call.enqueue(new Callback<ResponseCustomerLeadListPagination>() {
            @Override
            public void onResponse(Call<ResponseCustomerLeadListPagination> call, Response<ResponseCustomerLeadListPagination> response) {
                if (response.isSuccessful()){
                    if (response.body() != null) {
                        if (response.body().getData() != null) {
                            customerLeadPagination = response.body().getData();
                            if (customerLeadPagination.getData() != null){
                                listCustomerLead = customerLeadPagination.getData();
                            }
                        }
                    }

                }
                customerLeadProcess = false;
                joinCustomerLead();
            }

            @Override
            public void onFailure(Call<ResponseCustomerLeadListPagination> call, Throwable t) {
                customerLeadProcess = false;
            }
        });
    }

    private void joinCustomerLead() {
        if (!customerProcess && !customerLeadProcess){
            CustomerLeadJoin clj = null;

            for (Customer customer: listCustomer){
                clj = new CustomerLeadJoin();
                clj.setCustomerId(customer.getId());
                clj.setName(customer.getName());
                clj.setCustomer(customer);
                listCustomerLeadJoin.add(clj);
            }

            for (CustomerLead customerLead: listCustomerLead){
                clj = new CustomerLeadJoin();
                clj.setCustomerLeadId(customerLead.getId());
                clj.setName(customerLead.getName());
                clj.setCustomerLead(customerLead);
                listCustomerLeadJoin.add(clj);
            }
            final CustomerLeadJoinAdapter adapter = new CustomerLeadJoinAdapter(getContext(), R.layout.item_customer_spinner, listCustomerLeadJoin);
            textCustomerList.setAdapter(adapter);
            textCustomerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    listCustomerLeadJoin = adapter.getItems();
                    customerLeadJoin = listCustomerLeadJoin.get(i);
                    Log.d(TAG, "customerLeadJoin:"+new Gson().toJson(customerLeadJoin));
                    if (customerLeadJoin.getCustomerId() > 0){
                        setCustomerSelected(customerLeadJoin.getCustomer());
                    } else if (customerLeadJoin.getCustomerLeadId() > 0){
                        setCustomerLeadSelected(customerLeadJoin.getCustomerLead());
                    }
                }
            });
        }
    }

    private void setCustomerSelected(Customer customerSelected) {
        textFullName.setText("");
        textNik.setText("");
        textNpwp.setText("");
        textHandphone.setText("");
        textEmail.setText("");
        textAddressCorrespondent.setText("");
        radioMale.setChecked(false);
        radioFemale.setChecked(false);

        if (customerSelected != null && customerSelected.getId() > 0){
            textFullName.setText(customerSelected.getName());
            textNik.setText(customerSelected.getNik());
            textNpwp.setText(customerSelected.getNpwp());
            textHandphone.setText(customerSelected.getHandphone());
            textEmail.setText(customerSelected.getEmail());
            textAddress.setText(customerSelected.getAddress());
            textAddressCorrespondent.setText(customerSelected.getAddressCorrespondent());

            if(customerSelected.getGender() != null){
                radioMale.setChecked(customerSelected.getGender().equals(Constants.MALE));
                radioFemale.setChecked(customerSelected.getGender().equals(Constants.FEMALE));
            }
        }
    }

    private void setCustomerLeadSelected(CustomerLead customerSelected) {
        textFullName.setText("");
        textNik.setText("");
        textNpwp.setText("");
        textHandphone.setText("");
        textEmail.setText("");
        textAddressCorrespondent.setText("");
        radioMale.setChecked(false);
        radioFemale.setChecked(false);

        if (customerSelected != null && customerSelected.getId() > 0){
            textFullName.setText(customerSelected.getName());
            textNik.setText(customerSelected.getNik());
            textNpwp.setText(customerSelected.getNpwp());
            textHandphone.setText(customerSelected.getHandphone());
            textEmail.setText(customerSelected.getEmail());
            textAddress.setText(customerSelected.getAddress());
            textAddressCorrespondent.setText(customerSelected.getAddressCorrespondent());

            if(customerSelected.getGender() != null){
                radioMale.setChecked(customerSelected.getGender().equals(Constants.MALE));
                radioFemale.setChecked(customerSelected.getGender().equals(Constants.FEMALE));
            }
        }
    }
}
