package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PaymentSchemaCalculation implements Parcelable {
    @SerializedName("dp")
    private ValueCalculation dp;

    @SerializedName("installment")
    private ValueCalculation installment;

    public PaymentSchemaCalculation() {
    }

    protected PaymentSchemaCalculation(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PaymentSchemaCalculation> CREATOR = new Creator<PaymentSchemaCalculation>() {
        @Override
        public PaymentSchemaCalculation createFromParcel(Parcel in) {
            return new PaymentSchemaCalculation(in);
        }

        @Override
        public PaymentSchemaCalculation[] newArray(int size) {
            return new PaymentSchemaCalculation[size];
        }
    };

    public ValueCalculation getDp() {
        return dp;
    }

    public void setDp(ValueCalculation dp) {
        this.dp = dp;
    }

    public ValueCalculation getInstallment() {
        return installment;
    }

    public void setInstallment(ValueCalculation installment) {
        this.installment = installment;
    }
}
