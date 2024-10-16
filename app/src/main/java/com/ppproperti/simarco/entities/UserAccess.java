package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserAccess implements Parcelable {
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("apartment_id")
    private int apartmentId;

    @SerializedName("user_position_id")
    private int userPositionId;

    private User user = new User();
    private Apartment apartment = new Apartment();

    @SerializedName("user_position")
    private UserPosition userPosition = new UserPosition();

    public UserAccess() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
    }

    public int getUserPositionId() {
        return userPositionId;
    }

    public void setUserPositionId(int userPositionId) {
        this.userPositionId = userPositionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public UserPosition getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(UserPosition userPosition) {
        this.userPosition = userPosition;
    }

    protected UserAccess(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        apartmentId = in.readInt();
        userPositionId = in.readInt();
        user = in.readParcelable(User.class.getClassLoader());
        apartment = in.readParcelable(Apartment.class.getClassLoader());
        userPosition = in.readParcelable(UserPosition.class.getClassLoader());
    }

    public static final Creator<UserAccess> CREATOR = new Creator<UserAccess>() {
        @Override
        public UserAccess createFromParcel(Parcel in) {
            return new UserAccess(in);
        }

        @Override
        public UserAccess[] newArray(int size) {
            return new UserAccess[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(userId);
        parcel.writeInt(apartmentId);
        parcel.writeInt(userPositionId);
        parcel.writeParcelable(user, i);
        parcel.writeParcelable(apartment, i);
        parcel.writeParcelable(userPosition, i);
    }
}
