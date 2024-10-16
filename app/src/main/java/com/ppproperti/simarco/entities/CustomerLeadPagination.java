package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CustomerLeadPagination implements Parcelable {
    private int from;
    private int to;
    private int total;
    @SerializedName("current_page")
    private int currentPage;
    @SerializedName("last_page")
    private int lastPage;
    @SerializedName("per_page")
    private int perPage;

    private List<CustomerLead> data = new ArrayList<>();

    @SerializedName("first_page_url")
    private String firstPageUrl;

    @SerializedName("prev_page_url")
    private String prevPageUrl;

    @SerializedName("last_page_url")
    private String lastPageUrl;

    @SerializedName("next_page_url")
    private String nextPageUrl;
    private String path;

    public CustomerLeadPagination() {
    }

    protected CustomerLeadPagination(Parcel in) {
        from = in.readInt();
        to = in.readInt();
        total = in.readInt();
        currentPage = in.readInt();
        lastPage = in.readInt();
        perPage = in.readInt();
        data = in.createTypedArrayList(CustomerLead.CREATOR);
        firstPageUrl = in.readString();
        prevPageUrl = in.readString();
        lastPageUrl = in.readString();
        nextPageUrl = in.readString();
        path = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(from);
        dest.writeInt(to);
        dest.writeInt(total);
        dest.writeInt(currentPage);
        dest.writeInt(lastPage);
        dest.writeInt(perPage);
        dest.writeTypedList(data);
        dest.writeString(firstPageUrl);
        dest.writeString(prevPageUrl);
        dest.writeString(lastPageUrl);
        dest.writeString(nextPageUrl);
        dest.writeString(path);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CustomerLeadPagination> CREATOR = new Creator<CustomerLeadPagination>() {
        @Override
        public CustomerLeadPagination createFromParcel(Parcel in) {
            return new CustomerLeadPagination(in);
        }

        @Override
        public CustomerLeadPagination[] newArray(int size) {
            return new CustomerLeadPagination[size];
        }
    };

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public List<CustomerLead> getData() {
        return data;
    }

    public void setData(List<CustomerLead> data) {
        this.data = data;
    }

    public String getFirstPageUrl() {
        return firstPageUrl;
    }

    public void setFirstPageUrl(String firstPageUrl) {
        this.firstPageUrl = firstPageUrl;
    }

    public String getPrevPageUrl() {
        return prevPageUrl;
    }

    public void setPrevPageUrl(String prevPageUrl) {
        this.prevPageUrl = prevPageUrl;
    }

    public String getLastPageUrl() {
        return lastPageUrl;
    }

    public void setLastPageUrl(String lastPageUrl) {
        this.lastPageUrl = lastPageUrl;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
