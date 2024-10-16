package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ApartmentType implements Parcelable {
    private int id;
    private String name;
    private String description;

    @SerializedName("bedroom_count")
    private int bedroomCount;

    @SerializedName("surfaces_nett")
    private double surfacesNett;

    @SerializedName("surfaces_gross")
    private double surfacesGross;

    @SerializedName("price_per_m2")
    private double pricePerM2;

    @SerializedName("prepayment_furnish")
    private double prepaymentFurnish;

    public ApartmentType() {
    }

    protected ApartmentType(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        bedroomCount = in.readInt();
        surfacesNett = in.readDouble();
        surfacesGross = in.readDouble();
        pricePerM2 = in.readDouble();
        prepaymentFurnish = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(bedroomCount);
        dest.writeDouble(surfacesNett);
        dest.writeDouble(surfacesGross);
        dest.writeDouble(pricePerM2);
        dest.writeDouble(prepaymentFurnish);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ApartmentType> CREATOR = new Creator<ApartmentType>() {
        @Override
        public ApartmentType createFromParcel(Parcel in) {
            return new ApartmentType(in);
        }

        @Override
        public ApartmentType[] newArray(int size) {
            return new ApartmentType[size];
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

    public int getBedroomCount() {
        return bedroomCount;
    }

    public void setBedroomCount(int bedroomCount) {
        this.bedroomCount = bedroomCount;
    }

    public double getSurfacesNett() {
        return surfacesNett;
    }

    public void setSurfacesNett(double surfacesNett) {
        this.surfacesNett = surfacesNett;
    }

    public double getSurfacesGross() {
        return surfacesGross;
    }

    public void setSurfacesGross(double surfacesGross) {
        this.surfacesGross = surfacesGross;
    }

    public double getPricePerM2() {
        return pricePerM2;
    }

    public void setPricePerM2(double pricePerM2) {
        this.pricePerM2 = pricePerM2;
    }

    public double getPrepaymentFurnish() {
        return prepaymentFurnish;
    }

    public void setPrepaymentFurnish(double prepaymentFurnish) {
        this.prepaymentFurnish = prepaymentFurnish;
    }
}
