package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class ApartmentView implements Parcelable {
    private int id;
    private String name;
    private String description;
    private double compensationPercent;

    public ApartmentView() {
    }

    protected ApartmentView(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        compensationPercent = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(compensationPercent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ApartmentView> CREATOR = new Creator<ApartmentView>() {
        @Override
        public ApartmentView createFromParcel(Parcel in) {
            return new ApartmentView(in);
        }

        @Override
        public ApartmentView[] newArray(int size) {
            return new ApartmentView[size];
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

    public double getCompensationPercent() {
        return compensationPercent;
    }

    public void setCompensationPercent(double compensationPercent) {
        this.compensationPercent = compensationPercent;
    }
}
