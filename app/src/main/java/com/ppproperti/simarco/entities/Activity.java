package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Activity implements Parcelable {
    private int id;
    private String name;
    private String description;

    public Activity() {
    }

    public Activity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Activity(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    protected Activity(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel in) {
            return new Activity(in);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
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
}
