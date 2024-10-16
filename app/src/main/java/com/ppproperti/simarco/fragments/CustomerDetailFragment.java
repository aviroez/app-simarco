package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.Customer;
import com.ppproperti.simarco.entities.CustomerLead;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.interfaces.OnBackPressed;
import com.ppproperti.simarco.utils.Constants;
import com.ppproperti.simarco.utils.Helpers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomerDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomerDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerDetailFragment extends Fragment implements View.OnClickListener, OnBackPressed {

    private OnFragmentInteractionListener mListener;
    private Customer customer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private Context context;
    private ImageView imageCustomer;
    private TextView textName;
    private TextView textHandphone;
    private TextView textEmail;
    private TextView textGender;
    private TextView textAddress;
    private TextView textNik;
    private FloatingActionButton buttonBack;
    private FragmentActivity activity;
    private FloatingActionButton buttonUpdate;
    private FloatingActionButton buttonMessagel;
    private FloatingActionButton buttonWhatsapp;
    private FloatingActionButton buttonPhone;
    private CustomerLead customerLead;
    private SearchView searchView;

    public CustomerDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerDetailFragment newInstance(String param1, String param2) {
        CustomerDetailFragment fragment = new CustomerDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customer = getArguments().getParcelable("customer");
            customerLead = getArguments().getParcelable("customer_lead");
        }

        context = getContext();
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_detail, container, false);
        imageCustomer = view.findViewById(R.id.image_customer);
        textName = view.findViewById(R.id.text_name);
        textHandphone = view.findViewById(R.id.text_handphone);
        textEmail = view.findViewById(R.id.text_email);
        textGender = view.findViewById(R.id.text_gender);
        textAddress = view.findViewById(R.id.text_address);
        textNik = view.findViewById(R.id.text_nik);
        buttonBack = view.findViewById(R.id.button_back);
        buttonUpdate = view.findViewById(R.id.button_update);
        buttonMessagel = view.findViewById(R.id.button_message);
        buttonWhatsapp = view.findViewById(R.id.button_whatsapp);
        buttonPhone = view.findViewById(R.id.button_phone);

        if (activity != null){
            searchView = activity.findViewById(R.id.search_view);
            searchView.setVisibility(View.GONE);
        }
//        buttonBack.setOnClickListener(this);
        buttonUpdate.setOnClickListener(this);
        buttonMessagel.setOnClickListener(this);
        buttonWhatsapp.setOnClickListener(this);
        buttonPhone.setOnClickListener(this);

        if (customer != null){
            activity.setTitle(R.string.customer);
            textName.setText(customer.getName());
            textHandphone.setText(customer.getHandphone());
            textEmail.setText(customer.getEmail());
            textNik.setText(customer.getNik());

            if (customer.getAddress() != null){
                textAddress.setText(customer.getAddress());
            } else if (customer.getAddressCorrespondent() != null){
                textAddress.setText(customer.getAddressCorrespondent());
            }
            if (customer.getGender() != null && customer.getGender().equals(Constants.MALE)){
                textGender.setText(R.string.male);
            } else if (customer.getGender() != null && customer.getGender().equals(Constants.FEMALE)){
                textGender.setText(R.string.female);
            } else {
                textGender.setText("-");
            }
        } else if (customerLead != null){
            activity.setTitle(R.string.customer_candidate);
            textName.setText(customerLead.getName());
            textHandphone.setText(customerLead.getHandphone());
            textEmail.setText(customerLead.getEmail());
            textNik.setText(customerLead.getNik());

            if (customerLead.getAddress() != null){
                textAddress.setText(customerLead.getAddress());
            } else if (customerLead.getAddressCorrespondent() != null){
                textAddress.setText(customerLead.getAddressCorrespondent());
            }
            if (customerLead.getGender() != null && customerLead.getGender().equals(Constants.MALE)){
                textGender.setText(R.string.male);
            } else if (customerLead.getGender() != null && customerLead.getGender().equals(Constants.FEMALE)){
                textGender.setText(R.string.female);
            } else {
                textGender.setText("-");
            }
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.customer_detail);
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
    }

    @Override
    public void onClick(View view) {
//        if (view.getId() == buttonBack.getId()){
//            backAction();
//        } else
        if (view.getId() == buttonUpdate.getId()){
            updateAction();
        } else if (view.getId() == buttonMessagel.getId()){
            smsAction(view);
        } else if (view.getId() == buttonWhatsapp.getId()){
            whatsappAction(view);
        } else if (view.getId() == buttonPhone.getId()){
            phoneAction(view);
        }
    }

    private void phoneAction(View view) {
        if (customer != null && customer.getHandphone() != null){
            Helpers.call(context, customer.getHandphone());
        } else if (customerLead != null && customerLead.getHandphone() != null){
            Helpers.call(context, customerLead.getHandphone());
        }
    }

    private void whatsappAction(View view) {
        if (customer != null && customer.getHandphone() != null) {
            Helpers.whatsapp(context, customer.getHandphone(), "");
        } else if (customerLead != null && customerLead.getHandphone() != null) {
            Helpers.whatsapp(context, customerLead.getHandphone(), "");

        }
    }

    private void smsAction(View view) {
        if (customer != null && customer.getHandphone() != null) {
            Helpers.sms(context, customer.getHandphone(), "");
        } else if (customerLead != null && customerLead.getHandphone() != null){
            Helpers.sms(context, customerLead.getHandphone(), "");

        }
    }

    private void updateAction() {
        if (customerLead != null){
            Bundle bundle = new Bundle();
            bundle.putParcelable("customer_lead", customerLead);
            Helpers.moveFragment(context, new CustomerLeadAddFragment(), bundle);
        } else if (customer != null){
            Bundle bundle = new Bundle();
            bundle.putParcelable("customer", customer);
            Helpers.moveFragment(context, new CustomerEditFragment(), bundle);
        }
    }

    private void backAction() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("customer", customer);
        Helpers.moveFragment(context, new CustomerListFragment(), bundle);
    }

    @Override
    public void onBackPressed() {
        //backAction();
        activity.setTitle(R.string.customers);
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
}
