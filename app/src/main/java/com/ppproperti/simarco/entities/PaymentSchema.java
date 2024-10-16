package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PaymentSchema implements Parcelable {
    private int id;
    private String name;
    private String description;

    @SerializedName("dp_percent")
    private double dpPercent;

    @SerializedName("dp_installment")
    private int dpInstallment;

    @SerializedName("dp_interest_rate")
    private double dpInterestRate;

    @SerializedName("installment_number")
    private int installmentNumber;

    @SerializedName("installment_interest_rate")
    private double installmentInterestRate;

    @SerializedName("nominal_rate")
    private double nominalRate;

    @SerializedName("payment_to")
    private String paymentTo;

    @SerializedName("payment_type_id")
    private int paymentTypeId;

    @SerializedName("payment_type")
    private PaymentType paymentType;

    public PaymentSchema() {
    }

    protected PaymentSchema(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        dpPercent = in.readDouble();
        dpInstallment = in.readInt();
        dpInterestRate = in.readDouble();
        installmentNumber = in.readInt();
        installmentInterestRate = in.readDouble();
        nominalRate = in.readDouble();
        paymentTo = in.readString();
        paymentTypeId = in.readInt();
        paymentType = in.readParcelable(PaymentType.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(dpPercent);
        dest.writeInt(dpInstallment);
        dest.writeDouble(dpInterestRate);
        dest.writeInt(installmentNumber);
        dest.writeDouble(installmentInterestRate);
        dest.writeDouble(nominalRate);
        dest.writeString(paymentTo);
        dest.writeInt(paymentTypeId);
        dest.writeParcelable(paymentType, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PaymentSchema> CREATOR = new Creator<PaymentSchema>() {
        @Override
        public PaymentSchema createFromParcel(Parcel in) {
            return new PaymentSchema(in);
        }

        @Override
        public PaymentSchema[] newArray(int size) {
            return new PaymentSchema[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDpPercent() {
        return dpPercent;
    }

    public void setDpPercent(double dpPercent) {
        this.dpPercent = dpPercent;
    }

    public int getDpInstallment() {
        return dpInstallment;
    }

    public void setDpInstallment(int dpInstallment) {
        this.dpInstallment = dpInstallment;
    }

    public double getDpInterestRate() {
        return dpInterestRate;
    }

    public void setDpInterestRate(double dpInterestRate) {
        this.dpInterestRate = dpInterestRate;
    }

    public int getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(int installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public double getInstallmentInterestRate() {
        return installmentInterestRate;
    }

    public void setInstallmentInterestRate(double installmentInterestRate) {
        this.installmentInterestRate = installmentInterestRate;
    }

    public String getPaymentTo() {
        return paymentTo;
    }

    public void setPaymentTo(String paymentTo) {
        this.paymentTo = paymentTo;
    }

    public int getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(int paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public double getNominalRate() {
        return nominalRate;
    }

    public void setNominalRate(double nominalRate) {
        this.nominalRate = nominalRate;
    }
}
