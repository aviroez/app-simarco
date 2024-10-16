package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.Order;
import com.ppproperti.simarco.entities.OrderInstallment;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.utils.Helpers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDetailFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Order order;
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
    private TextView textbookingFee;
    private TextView textAmountInWords;
    private TableLayout tableInstallment;
    private TextView textTitle;
    private Context context;
    private BootstrapButton buttonBack;
    private FragmentActivity activity;

    public OrderDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderDetailFragment newInstance(String param1, String param2) {
        OrderDetailFragment fragment = new OrderDetailFragment();
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
            order = getArguments().getParcelable("order");
        }

        context = getContext();
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);

        textTitle = view.findViewById(R.id.text_title);
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

        buttonBack.setOnClickListener(this);

        if (order != null){
            if (order.getOrderNo() != null){
                textBookingNo.setText(getString(R.string.order_no_with, order.getOrderNo()));
                textTitle.setText(R.string.booking_letter);
            } else if (order.getBookingNo() != null){
                textBookingNo.setText(getString(R.string.booking_no_with, order.getBookingNo()));
                textTitle.setText(R.string.order_letter);
            }

            textName.setText(order.getName());
            textNik.setText(order.getNik());
            textAddress.setText(order.getAddress());
            textAddressCorespondent.setText(order.getAddressCorrespondent());
            textPhoneNumber.setText(order.getHandphone());

            if (order.getApartmentUnit() != null){
                if (order.getApartmentUnit().getApartmentTower() != null){
                    textTower.setText(order.getApartmentUnit().getApartmentTower().getName());
                }
                if (order.getApartmentUnit().getApartmentFloor() != null){
                    textTower.setText(order.getApartmentUnit().getApartmentFloor().getName());
                }
                textUnitNo.setText(String.valueOf(order.getApartmentUnit().getUnitNumber()));
            }

            textPrice.setText(Helpers.currency(order.getPrice()));
            textDiscount.setText(Helpers.currency(order.getDiscount()));
            textTax.setText(Helpers.currency(order.getTax()));
            textFinalPrice.setText(Helpers.currency(order.getFinalPrice()));
            textbookingFee.setText(Helpers.currency(order.getbookingFee()));
            textAmountInWords.setVisibility(View.GONE);

            if (order.getOrderInstallments() != null){
                for (OrderInstallment oi: order.getOrderInstallments()){
                    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    params.setMargins(5, 0, 5, 0);

                    TableRow tr = new TableRow(context);
                    TextView tv = new TextView(context);

                    tv.setText(String.valueOf(order.getOrderInstallments().indexOf(oi)+1));
                    tv.setLayoutParams(params);
                    tr.addView(tv);

                    params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    params.setMargins(5, 0, 5, 0);

                    tv = new TextView(context);
                    tv.setText(oi.getDescription());
                    tv.setLayoutParams(params);
                    tr.addView(tv);

                    tv = new TextView(context);
                    tv.setText(Helpers.currency(oi.getPrice()));
                    tv.setLayoutParams(params);
                    tr.addView(tv);

                    tv = new TextView(context);
                    tv.setText(Helpers.formatDate(oi.getDueDate()));
                    tv.setLayoutParams(params);
                    tr.addView(tv);
                    tableInstallment.addView(tr);
                }
            }
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.order_detail);
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
        if (view.getId() == R.id.button_back){
            Helpers.moveFragment(context, new OrderListFragment(), null);
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
}
