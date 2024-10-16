package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {
    private int id;
    private String name;
    private String alias;
    private String email;

    @SerializedName("email_verified_at")
    private String emailVerifiedAt;
    private String password;
    private String ktp;
    private String sim;
    private String npwp;
    private String address;

    @SerializedName("phone_number")
    private String phoneNumber;
    private String fax;
    private String handphone;

    @SerializedName("user_position_id")
    private int userPositionId;

    @SerializedName("remember_token")
    private String rememberToken;

    @SerializedName("api_token")
    private String apiToken;

    @SerializedName("user_position")
    private UserPosition userPosition;

    @SerializedName("user_accesses")
    private List<UserAccess> userAccesses = new ArrayList<>();

    private String signature;

    public User() {
    }

    protected User(Parcel in) {
        id = in.readInt();
        name = in.readString();
        alias = in.readString();
        email = in.readString();
        emailVerifiedAt = in.readString();
        password = in.readString();
        ktp = in.readString();
        sim = in.readString();
        npwp = in.readString();
        address = in.readString();
        phoneNumber = in.readString();
        fax = in.readString();
        handphone = in.readString();
        userPositionId = in.readInt();
        rememberToken = in.readString();
        apiToken = in.readString();
        userPosition = in.readParcelable(UserPosition.class.getClassLoader());
        userAccesses = in.createTypedArrayList(UserAccess.CREATOR);
        signature = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(alias);
        dest.writeString(email);
        dest.writeString(emailVerifiedAt);
        dest.writeString(password);
        dest.writeString(ktp);
        dest.writeString(sim);
        dest.writeString(npwp);
        dest.writeString(address);
        dest.writeString(phoneNumber);
        dest.writeString(fax);
        dest.writeString(handphone);
        dest.writeInt(userPositionId);
        dest.writeString(rememberToken);
        dest.writeString(apiToken);
        dest.writeParcelable(userPosition, flags);
        dest.writeTypedList(userAccesses);
        dest.writeString(signature);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(String emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKtp() {
        return ktp;
    }

    public void setKtp(String ktp) {
        this.ktp = ktp;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getNpwp() {
        return npwp;
    }

    public void setNpwp(String npwp) {
        this.npwp = npwp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getHandphone() {
        return handphone;
    }

    public void setHandphone(String handphone) {
        this.handphone = handphone;
    }

    public int getUserPositionId() {
        return userPositionId;
    }

    public void setUserPositionId(int userPositionId) {
        this.userPositionId = userPositionId;
    }

    public String getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(String rememberToken) {
        this.rememberToken = rememberToken;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public UserPosition getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(UserPosition userPosition) {
        this.userPosition = userPosition;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public List<UserAccess> getUserAccesses() {
        return userAccesses;
    }

    public void setUserAccesses(List<UserAccess> userAccesses) {
        this.userAccesses = userAccesses;
    }
}
