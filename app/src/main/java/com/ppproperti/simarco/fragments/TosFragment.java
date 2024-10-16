package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.ApartmentUnit;
import com.ppproperti.simarco.entities.Customer;
import com.ppproperti.simarco.entities.Order;
import com.ppproperti.simarco.entities.PaymentSchema;
import com.ppproperti.simarco.entities.PaymentSchemaCalculation;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.utils.Helpers;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TosFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Order order;
    private Customer customer;
    private ApartmentUnit apartmentUnit;
    private PaymentSchema paymentSchema;
    private PaymentSchemaCalculation paymentSchemaCalculation;
    private String ktpPath;
    private String npwpPath;
    private Context context;
    private BootstrapButton buttonNext;
    private BootstrapButton buttonBack;
    private FragmentActivity activity;

    public TosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TosFragment newInstance(String param1, String param2) {
        TosFragment fragment = new TosFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tos, container, false);
        buttonBack = view.findViewById(R.id.button_back);
        buttonNext = view.findViewById(R.id.button_next);

        buttonBack.setOnClickListener(this);
        buttonNext.setOnClickListener(this);

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.tos);
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
        if (view.getId() == buttonBack.getId()){
            backAction();
        } else if (view.getId() == buttonNext.getId()){
            nextAction();
        }
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

    private void backAction() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("order", order);
        bundle.putParcelable("customer", customer);
        bundle.putParcelable("apartmentUnit", apartmentUnit);
        bundle.putParcelable("paymentSchema", paymentSchema);
        bundle.putParcelable("paymentSchemaCalculation", paymentSchemaCalculation);
        bundle.putString("ktpPath", ktpPath);
        bundle.putString("npwpPath", npwpPath);

        Helpers.moveFragment(context, new InstallmentFragment(), bundle);
    }

    private void nextAction() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("order", order);
        bundle.putParcelable("customer", customer);
        bundle.putParcelable("apartmentUnit", apartmentUnit);
        bundle.putParcelable("paymentSchema", paymentSchema);
        bundle.putParcelable("paymentSchemaCalculation", paymentSchemaCalculation);
        bundle.putString("ktpPath", ktpPath);
        bundle.putString("npwpPath", npwpPath);

        Helpers.moveFragment(context, new SignatureFragment(), bundle);
    }
}
