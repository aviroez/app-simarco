package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Apartment implements Parcelable {
    private int id;
    private String code;
    private String name;

    @SerializedName("full_name")
    private String fullName;
    private String address;
    private String status;
    private String description;
    private double latitude;
    private double longitude;

    @SerializedName("apartment_tower_id")
    private int apartmentTowerId;

    @SerializedName("apartment_tower")
    private ApartmentTower apartmentTower;
    private List<Image> images = new ArrayList<>();

    public Apartment() {
    }

    public Apartment(int id, String name) {
        this.id = id;
        this.name = name;
    }

    protected Apartment(Parcel in) {
        id = in.readInt();
        code = in.readString();
        name = in.readString();
        fullName = in.readString();
        address = in.readString();
        status = in.readString();
        description = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        apartmentTowerId = in.readInt();
        apartmentTower = in.readParcelable(ApartmentTower.class.getClassLoader());
        images = in.createTypedArrayList(Image.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(code);
        dest.writeString(name);
        dest.writeString(fullName);
        dest.writeString(address);
        dest.writeString(status);
        dest.writeString(description);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(apartmentTowerId);
        dest.writeParcelable(apartmentTower, flags);
        dest.writeTypedList(images);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Apartment> CREATOR = new Creator<Apartment>() {
        @Override
        public Apartment createFromParcel(Parcel in) {
            return new Apartment(in);
        }

        @Override
        public Apartment[] newArray(int size) {
            return new Apartment[size];
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

    public ApartmentTower getApartmentTower() {
        return apartmentTower;
    }

    public void setApartmentTower(ApartmentTower apartmentTower) {
        this.apartmentTower = apartmentTower;
    }

    public int getApartmentTowerId() {
        return apartmentTowerId;
    }

    public void setApartmentTowerId(int apartmentTowerId) {
        this.apartmentTowerId = apartmentTowerId;
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

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
