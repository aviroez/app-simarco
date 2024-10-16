package com.ppproperti.simarco.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.material.textfield.TextInputEditText;
import com.ppproperti.simarco.R;
import com.ppproperti.simarco.interfaces.HideShowIconInterface;
import com.ppproperti.simarco.utils.Helpers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoanFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoanFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = LoanFragment.class.getSimpleName();
    private Context context;
    private Activity activity;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TextInputEditText textAmount;
    private TextInputEditText textPercent;
    private TextInputEditText textLoanTerm;
    private BootstrapButton buttonCalculate;
    private TextView textInstallment;
    private TextView textResult;
    private double amount;
    private double percent;
    private double loanTerm;

    public LoanFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoanFragment newInstance(String param1, String param2) {
        LoanFragment fragment = new LoanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        context = getContext();
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loan, container, false);
        textAmount = view.findViewById(R.id.text_amount);
        textPercent = view.findViewById(R.id.text_percent);
        textLoanTerm = view.findViewById(R.id.text_loan_term);
        buttonCalculate = view.findViewById(R.id.button_calculate);
        textInstallment = view.findViewById(R.id.text_installment);
        textResult = view.findViewById(R.id.text_result);
        textAmount.addTextChangedListener(new NumberTextWatcherForThousand(textAmount));

        buttonCalculate.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activity != null){
            activity.setTitle(R.string.calculator_loan);
            ((HideShowIconInterface) activity).showHamburgerIcon();
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
        if (view.getId() == R.id.button_calculate){
            calculateAction(view);
        }
    }

    private void calculateAction(View view) {
        if (validation()){
            calculate();
        }
    }
    private boolean validation() {
//        amount = Helpers.parseDouble(textAmount.getText().toString());
        amount = Helpers.parseDouble(Helpers.removeNonDigit(textAmount.getText().toString()));
        percent = Helpers.parseDouble(textPercent.getText().toString());
        loanTerm = Helpers.parseDouble(textLoanTerm.getText().toString());
        if (amount <= 0){
            Helpers.showLongSnackbar(getView(), R.string.amount_is_required);
            textAmount.requestFocus();
            return false;
        } else if (loanTerm <= 0){
            Helpers.showLongSnackbar(getView(), R.string.loan_term_is_required);
            textLoanTerm.requestFocus();
            return false;
        }else if (percent <= 0){
            Helpers.showLongSnackbar(getView(), R.string.percent_is_required);
            textPercent.requestFocus();
            return false;
        }
        return true;
    }


    //***********************************************************
    //              INTEREST * ((1 + INTEREST) ^ TOTALPAYMENTS)
    // PMT = LOAN * -------------------------------------------
    //                  ((1 + INTEREST) ^ TOTALPAYMENTS) - 1
    //***********************************************************
//    private void calculate(){
//        double interest = percent/100;
//        double top = interest * Math.pow(1+interest, loanTerm*12);
//        double bottom = Math.pow(1+interest, loanTerm*12) - 1;
//
//        double result = amount * (top / bottom);
//        Log.d(TAG, "validation:");
//        textInstallment.setText("Jumlah Cicilan: "+loanTerm*12+"x");
//        textResult.setText("Cicilan per bulan: "+Helpers.currency(result));
//    }

    /*
    P = pokok pinjaman
    i = suku bunga per tahun
    t = lama kredit dalam bulan

                i/12
    P * ----------------------
           1-(1+(i/12))^-t
     */

    private void calculate(){
        double interest = percent/100;
        double top = interest/12;
        double bottom = 1 - Math.pow(1+(interest/12), -loanTerm*12);

        double result = amount * (top / bottom);
        Log.d(TAG, "validation:");
        textInstallment.setText("Jumlah Cicilan: "+Helpers.parseInteger(loanTerm*12)+"x");
        textResult.setText("Cicilan per bulan: "+Helpers.currency(result));
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

    private class NumberTextWatcherForThousand implements TextWatcher {
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
                        editText.setText(Helpers.currency(Helpers.removeNonDigit(value), true));
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
        }
    }
}
