package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Customer implements Parcelable {
    private int id;
    private String name;
    private String nik;
    private String sim;
    private String npwp;
    private String address;

    @SerializedName("address_correspondent")
    private String addressCorrespondent;

    @SerializedName("phone_number")
    private String phoneNumber;
    private String fax;
    private String handphone;
    private String email;
    private String validity;
    private String gender;

    @SerializedName("birth_date")
    private String birthDate;

    @SerializedName("marketing_id")
    private int marketingId;
    private User marketing = new User();

    @SerializedName("apartment_id")
    private int apartmentId;
    private Apartment apartment = new Apartment();

    @SerializedName("event_id")
    private int eventId;
    private Event event = new Event();

    @SerializedName("activity_id")
    private int activityId;
    private Activity activity = new Activity();

    private List<Order> orders = new ArrayList<>();

    public Customer() {
    }

    public Customer(int id, String name, String handphone) {
        this.id = id;
        this.name = name;
        this.handphone = handphone;
    }

    protected Customer(Parcel in) {
        id = in.readInt();
        name = in.readString();
        nik = in.readString();
        sim = in.readString();
        npwp = in.readString();
        address = in.readString();
        addressCorrespondent = in.readString();
        phoneNumber = in.readString();
        fax = in.readString();
        handphone = in.readString();
        email = in.readString();
        validity = in.readString();
        gender = in.readString();
        birthDate = in.readString();
        marketingId = in.readInt();
        marketing = in.readParcelable(User.class.getClassLoader());
        apartmentId = in.readInt();
        apartment = in.readParcelable(Apartment.class.getClassLoader());
        eventId = in.readInt();
        event = in.readParcelable(Event.class.getClassLoader());
        activityId = in.readInt();
        activity = in.readParcelable(Activity.class.getClassLoader());
        orders = in.createTypedArrayList(Order.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(nik);
        dest.writeString(sim);
        dest.writeString(npwp);
        dest.writeString(address);
        dest.writeString(addressCorrespondent);
        dest.writeString(phoneNumber);
        dest.writeString(fax);
        dest.writeString(handphone);
        dest.writeString(email);
        dest.writeString(validity);
        dest.writeString(gender);
        dest.writeString(birthDate);
        dest.writeInt(marketingId);
        dest.writeParcelable(marketing, flags);
        dest.writeInt(apartmentId);
        dest.writeParcelable(apartment, flags);
        dest.writeInt(eventId);
        dest.writeParcelable(event, flags);
        dest.writeInt(activityId);
        dest.writeParcelable(activity, flags);
        dest.writeTypedList(orders);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
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

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
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

    public String getAddressCorrespondent() {
        return addressCorrespondent;
    }

    public void setAddressCorrespondent(String addressCorrespondent) {
        this.addressCorrespondent = addressCorrespondent;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public int getMarketingId() {
        return marketingId;
    }

    public void setMarketingId(int marketingId) {
        this.marketingId = marketingId;
    }

    public User getMarketing() {
        return marketing;
    }

    public void setMarketing(User marketing) {
        this.marketing = marketing;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public int getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
