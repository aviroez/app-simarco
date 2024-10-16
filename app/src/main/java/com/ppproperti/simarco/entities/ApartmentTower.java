package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class ApartmentTower implements Parcelable {
    private int id;
    private String name;
    private String address;
    private String description;
    private double latitude;
    private double longitude;
    private Apartment apartment;

    public ApartmentTower() {
    }

    protected ApartmentTower(Parcel in) {
        id = in.readInt();
        name = in.readString();
        address = in.readString();
        description = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        apartment = in.readParcelable(Apartment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(description);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeParcelable(apartment, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ApartmentTower> CREATOR = new Creator<ApartmentTower>() {
        @Override
        public ApartmentTower createFromParcel(Parcel in) {
            return new ApartmentTower(in);
        }

        @Override
        public ApartmentTower[] newArray(int size) {
            return new ApartmentTower[size];
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }
}
