package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class ValueCalculation implements Parcelable {
    private int installment;
    private double interest;
    private double value;

    public ValueCalculation() {
    }

    protected ValueCalculation(Parcel in) {
        installment = in.readInt();
        interest = in.readDouble();
        value = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(installment);
        dest.writeDouble(interest);
        dest.writeDouble(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ValueCalculation> CREATOR = new Creator<ValueCalculation>() {
        @Override
        public ValueCalculation createFromParcel(Parcel in) {
            return new ValueCalculation(in);
        }

        @Override
        public ValueCalculation[] newArray(int size) {
            return new ValueCalculation[size];
        }
    };

    public int getInstallment() {
        return installment;
    }

    public void setInstallment(int installment) {
        this.installment = installment;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
