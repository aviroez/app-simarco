package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ApartmentFloor implements Parcelable {
    private int id;
    private String name;
    private String description;
    private String number;
    private double compensationPercent;

    @SerializedName("apartment_units")
    private List<ApartmentUnit> listApartmentUnit = new ArrayList<>();

    public ApartmentFloor() {
    }

    protected ApartmentFloor(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        number = in.readString();
        compensationPercent = in.readDouble();
        listApartmentUnit = in.createTypedArrayList(ApartmentUnit.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(number);
        dest.writeDouble(compensationPercent);
        dest.writeTypedList(listApartmentUnit);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ApartmentFloor> CREATOR = new Creator<ApartmentFloor>() {
        @Override
        public ApartmentFloor createFromParcel(Parcel in) {
            return new ApartmentFloor(in);
        }

        @Override
        public ApartmentFloor[] newArray(int size) {
            return new ApartmentFloor[size];
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getCompensationPercent() {
        return compensationPercent;
    }

    public void setCompensationPercent(double compensationPercent) {
        this.compensationPercent = compensationPercent;
    }

    public List<ApartmentUnit> getListApartmentUnit() {
        return listApartmentUnit;
    }

    public void setListApartmentUnit(List<ApartmentUnit> listApartmentUnit) {
        this.listApartmentUnit = listApartmentUnit;
    }
}
