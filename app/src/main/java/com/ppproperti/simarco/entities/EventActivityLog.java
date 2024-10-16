package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EventActivityLog implements Parcelable {
    private int id;

    @SerializedName("event_activity_id")
    private int eventActivityId;

    @SerializedName("customer_id")
    private int customerId;
    private double latitude;
    private double longitude;
    private String location;

    @SerializedName("event_id")
    private int eventId;

    @SerializedName("activity_id")
    private int activityId;

    @SerializedName("event_activity")
    private EventActivity eventActivity = new EventActivity();
    private Customer customer = new Customer();
    private Event event = new Event();
    private Activity activity = new Activity();

    @SerializedName("created_at")
    private String createdAt;
    private List<Image> images = new ArrayList<>();

    public EventActivityLog() {
    }

    protected EventActivityLog(Parcel in) {
        id = in.readInt();
        eventActivityId = in.readInt();
        customerId = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        location = in.readString();
        eventId = in.readInt();
        activityId = in.readInt();
        eventActivity = in.readParcelable(EventActivity.class.getClassLoader());
        customer = in.readParcelable(Customer.class.getClassLoader());
        event = in.readParcelable(Event.class.getClassLoader());
        activity = in.readParcelable(Activity.class.getClassLoader());
        createdAt = in.readString();
        images = in.createTypedArrayList(Image.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(eventActivityId);
        dest.writeInt(customerId);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(location);
        dest.writeInt(eventId);
        dest.writeInt(activityId);
        dest.writeParcelable(eventActivity, flags);
        dest.writeParcelable(customer, flags);
        dest.writeParcelable(event, flags);
        dest.writeParcelable(activity, flags);
        dest.writeString(createdAt);
        dest.writeTypedList(images);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EventActivityLog> CREATOR = new Creator<EventActivityLog>() {
        @Override
        public EventActivityLog createFromParcel(Parcel in) {
            return new EventActivityLog(in);
        }

        @Override
        public EventActivityLog[] newArray(int size) {
            return new EventActivityLog[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventActivityId() {
        return eventActivityId;
    }

    public void setEventActivityId(int eventActivityId) {
        this.eventActivityId = eventActivityId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    public EventActivity getEventActivity() {
        return eventActivity;
    }

    public void setEventActivity(EventActivity eventActivity) {
        this.eventActivity = eventActivity;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }
}
