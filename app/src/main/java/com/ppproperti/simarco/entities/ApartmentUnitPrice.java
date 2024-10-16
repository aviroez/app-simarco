package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ApartmentUnitPrice implements Parcelable {
    @SerializedName("surface_per_m2")
    private long surfacePerM2;

    @SerializedName("floor_price")
    private long floorPrice;

    @SerializedName("view_price")
    private long viewPrice;

    @SerializedName("nett_price")
    private long nettPrice;

    @SerializedName("prepayment_furnish")
    private long prepaymentFurnish;

    @SerializedName("basic_selling_price")
    private long basicSellingPrice;

    @SerializedName("mark_up_price")
    private long markUpPrice;

    @SerializedName("result_price")
    private long resultPrice;

    @SerializedName("final_price")
    private long finalPrice;

    public ApartmentUnitPrice() {
    }

    protected ApartmentUnitPrice(Parcel in) {
        surfacePerM2 = in.readLong();
        floorPrice = in.readLong();
        viewPrice = in.readLong();
        nettPrice = in.readLong();
        prepaymentFurnish = in.readLong();
        basicSellingPrice = in.readLong();
        markUpPrice = in.readLong();
        resultPrice = in.readLong();
        finalPrice = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(surfacePerM2);
        dest.writeLong(floorPrice);
        dest.writeLong(viewPrice);
        dest.writeLong(nettPrice);
        dest.writeLong(prepaymentFurnish);
        dest.writeLong(basicSellingPrice);
        dest.writeLong(markUpPrice);
        dest.writeLong(resultPrice);
        dest.writeLong(finalPrice);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ApartmentUnitPrice> CREATOR = new Creator<ApartmentUnitPrice>() {
        @Override
        public ApartmentUnitPrice createFromParcel(Parcel in) {
            return new ApartmentUnitPrice(in);
        }

        @Override
        public ApartmentUnitPrice[] newArray(int size) {
            return new ApartmentUnitPrice[size];
        }
    };

    public long getSurfacePerM2() {
        return surfacePerM2;
    }

    public void setSurfacePerM2(long surfacePerM2) {
        this.surfacePerM2 = surfacePerM2;
    }

    public long getFloorPrice() {
        return floorPrice;
    }

    public void setFloorPrice(long floorPrice) {
        this.floorPrice = floorPrice;
    }

    public long getViewPrice() {
        return viewPrice;
    }

    public void setViewPrice(long viewPrice) {
        this.viewPrice = viewPrice;
    }

    public long getNettPrice() {
        return nettPrice;
    }

    public void setNettPrice(long nettPrice) {
        this.nettPrice = nettPrice;
    }

    public long getPrepaymentFurnish() {
        return prepaymentFurnish;
    }

    public void setPrepaymentFurnish(long prepaymentFurnish) {
        this.prepaymentFurnish = prepaymentFurnish;
    }

    public long getBasicSellingPrice() {
        return basicSellingPrice;
    }

    public void setBasicSellingPrice(long basicSellingPrice) {
        this.basicSellingPrice = basicSellingPrice;
    }

    public long getMarkUpPrice() {
        return markUpPrice;
    }

    public void setMarkUpPrice(long markUpPrice) {
        this.markUpPrice = markUpPrice;
    }

    public long getResultPrice() {
        return resultPrice;
    }

    public void setResultPrice(long resultPrice) {
        this.resultPrice = resultPrice;
    }

    public long getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(long finalPrice) {
        this.finalPrice = finalPrice;
    }
}
