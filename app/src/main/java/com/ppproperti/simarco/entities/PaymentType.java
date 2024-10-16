package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class PaymentType implements Parcelable {
    private int id;
    private String code;
    private String name;
    private String description;

    public PaymentType() {
    }

    protected PaymentType(Parcel in) {
        id = in.readInt();
        code = in.readString();
        name = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(code);
        dest.writeString(name);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PaymentType> CREATOR = new Creator<PaymentType>() {
        @Override
        public PaymentType createFromParcel(Parcel in) {
            return new PaymentType(in);
        }

        @Override
        public PaymentType[] newArray(int size) {
            return new PaymentType[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
}
