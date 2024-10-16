package com.ppproperti.simarco.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ppproperti.simarco.R;
import com.ppproperti.simarco.entities.PaymentSchema;
import com.ppproperti.simarco.entities.PaymentSchemaCalculation;

public class PaymentSchemaDetailFragment extends Fragment {

    private PaymentSchema paymentSchema;
    private PaymentSchemaCalculation paymentSchemaCalculation;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paymentSchema = getArguments().getParcelable("paymentSchema");
            paymentSchemaCalculation = getArguments().getParcelable("paymentSchemaCalculation");
        }

        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_schema, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
