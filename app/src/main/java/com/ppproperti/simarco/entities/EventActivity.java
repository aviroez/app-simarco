package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class EventActivity implements Parcelable {
    private int id;
    private String name;
    private String description;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;
    private Event event = new Event();
    private Activity activity = new Activity();

    public EventActivity() {
    }

    protected EventActivity(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        event = in.readParcelable(Event.class.getClassLoader());
        activity = in.readParcelable(Activity.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeParcelable(event, flags);
        dest.writeParcelable(activity, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EventActivity> CREATOR = new Creator<EventActivity>() {
        @Override
        public EventActivity createFromParcel(Parcel in) {
            return new EventActivity(in);
        }

        @Override
        public EventActivity[] newArray(int size) {
            return new EventActivity[size];
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
}
