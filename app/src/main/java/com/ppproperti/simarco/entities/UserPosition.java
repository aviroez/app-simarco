package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class UserPosition implements Parcelable {
    private int id;
    private String name;
    private String code;
    private String description;

    public UserPosition() {
    }

    protected UserPosition(Parcel in) {
        id = in.readInt();
        name = in.readString();
        code = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(code);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserPosition> CREATOR = new Creator<UserPosition>() {
        @Override
        public UserPosition createFromParcel(Parcel in) {
            return new UserPosition(in);
        }

        @Override
        public UserPosition[] newArray(int size) {
            return new UserPosition[size];
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
