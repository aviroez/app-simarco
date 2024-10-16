package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ApartmentUnit implements Parcelable {
    private int id;
    private String name;
    private String desc;

    @SerializedName("unit_number")
    private String unitNumber;

    @SerializedName("markup_percent")
    private double markupPercent;
    private double price;
    private double discount;
    private double tax;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("price_cash")
    private double priceCash;

    @SerializedName("price_installment")
    private double priceInstallment;

    @SerializedName("price_kpa")
    private double priceKpa;

    @SerializedName("tax_cash")
    private double taxCash;

    @SerializedName("tax_installment")
    private double taxInstallment;

    @SerializedName("tax_kpa")
    private double taxKpa;

    @SerializedName("total_price_cash")
    private double totalPriceCash;

    @SerializedName("total_price_installment")
    private double totalPriceInstallment;

    @SerializedName("total_price_kpa")
    private double totalPriceKpa;

    @SerializedName("markup_price")
    private double markupPrice;

    @SerializedName("group_unit")
    private int groupUnit;

    @SerializedName("apartment_id")
    private int apartmentId;

    @SerializedName("apartment_tower_id")
    private int apartmentTowerId;

    @SerializedName("apartment_type_id")
    private int apartmentTypeId;

    @SerializedName("apartment_type_desc")
    private String apartmentTypeDesc;

    @SerializedName("apartment_view_id")
    private int apartmentViewId;

    @SerializedName("apartment_floor_id")
    private int apartmentFloorId;
    private String status;

    @SerializedName("apartment_tower")
    private ApartmentTower apartmentTower = new ApartmentTower();

    @SerializedName("apartment_type")
    private ApartmentType apartmentType = new ApartmentType();

    @SerializedName("apartment_view")
    private ApartmentView apartmentView = new ApartmentView();

    @SerializedName("apartment_floor")
    private ApartmentFloor apartmentFloor = new ApartmentFloor();
    private List<Image> images;
    private List<ApartmentUnit> grouped = new ArrayList<>();

    public ApartmentUnit() {
    }

    protected ApartmentUnit(Parcel in) {
        id = in.readInt();
        name = in.readString();
        desc = in.readString();
        unitNumber = in.readString();
        markupPercent = in.readDouble();
        price = in.readDouble();
        discount = in.readDouble();
        tax = in.readDouble();
        totalPrice = in.readDouble();
        priceCash = in.readDouble();
        priceInstallment = in.readDouble();
        priceKpa = in.readDouble();
        taxCash = in.readDouble();
        taxInstallment = in.readDouble();
        taxKpa = in.readDouble();
        totalPriceCash = in.readDouble();
        totalPriceInstallment = in.readDouble();
        totalPriceKpa = in.readDouble();
        markupPrice = in.readDouble();
        groupUnit = in.readInt();
        apartmentId = in.readInt();
        apartmentTowerId = in.readInt();
        apartmentTypeId = in.readInt();
        apartmentTypeDesc = in.readString();
        apartmentViewId = in.readInt();
        apartmentFloorId = in.readInt();
        status = in.readString();
        apartmentTower = in.readParcelable(ApartmentTower.class.getClassLoader());
        apartmentType = in.readParcelable(ApartmentType.class.getClassLoader());
        apartmentView = in.readParcelable(ApartmentView.class.getClassLoader());
        apartmentFloor = in.readParcelable(ApartmentFloor.class.getClassLoader());
        images = in.createTypedArrayList(Image.CREATOR);
        grouped = in.createTypedArrayList(ApartmentUnit.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(unitNumber);
        dest.writeDouble(markupPercent);
        dest.writeDouble(price);
        dest.writeDouble(discount);
        dest.writeDouble(tax);
        dest.writeDouble(totalPrice);
        dest.writeDouble(priceCash);
        dest.writeDouble(priceInstallment);
        dest.writeDouble(priceKpa);
        dest.writeDouble(taxCash);
        dest.writeDouble(taxInstallment);
        dest.writeDouble(taxKpa);
        dest.writeDouble(totalPriceCash);
        dest.writeDouble(totalPriceInstallment);
        dest.writeDouble(totalPriceKpa);
        dest.writeDouble(markupPrice);
        dest.writeInt(groupUnit);
        dest.writeInt(apartmentId);
        dest.writeInt(apartmentTowerId);
        dest.writeInt(apartmentTypeId);
        dest.writeString(apartmentTypeDesc);
        dest.writeInt(apartmentViewId);
        dest.writeInt(apartmentFloorId);
        dest.writeString(status);
        dest.writeParcelable(apartmentTower, flags);
        dest.writeParcelable(apartmentType, flags);
        dest.writeParcelable(apartmentView, flags);
        dest.writeParcelable(apartmentFloor, flags);
        dest.writeTypedList(images);
        dest.writeTypedList(grouped);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ApartmentUnit> CREATOR = new Creator<ApartmentUnit>() {
        @Override
        public ApartmentUnit createFromParcel(Parcel in) {
            return new ApartmentUnit(in);
        }

        @Override
        public ApartmentUnit[] newArray(int size) {
            return new ApartmentUnit[size];
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

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public double getMarkupPercent() {
        return markupPercent;
    }

    public void setMarkupPercent(double markupPercent) {
        this.markupPercent = markupPercent;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(long discount) {
        this.discount = discount;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(long tax) {
        this.tax = tax;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getGroupUnit() {
        return groupUnit;
    }

    public void setGroupUnit(int groupUnit) {
        this.groupUnit = groupUnit;
    }

    public int getApartmentTowerId() {
        return apartmentTowerId;
    }

    public void setApartmentTowerId(int apartmentTowerId) {
        this.apartmentTowerId = apartmentTowerId;
    }

    public int getApartmentTypeId() {
        return apartmentTypeId;
    }

    public void setApartmentTypeId(int apartmentTypeId) {
        this.apartmentTypeId = apartmentTypeId;
    }

    public int getApartmentViewId() {
        return apartmentViewId;
    }

    public void setApartmentViewId(int apartmentViewId) {
        this.apartmentViewId = apartmentViewId;
    }

    public int getApartmentFloorId() {
        return apartmentFloorId;
    }

    public void setApartmentFloorId(int apartmentFloorId) {
        this.apartmentFloorId = apartmentFloorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ApartmentTower getApartmentTower() {
        return apartmentTower;
    }

    public void setApartmentTower(ApartmentTower apartmentTower) {
        this.apartmentTower = apartmentTower;
    }

    public ApartmentType getApartmentType() {
        return apartmentType;
    }

    public void setApartmentType(ApartmentType apartmentType) {
        this.apartmentType = apartmentType;
    }

    public ApartmentView getApartmentView() {
        return apartmentView;
    }

    public void setApartmentView(ApartmentView apartmentView) {
        this.apartmentView = apartmentView;
    }

    public ApartmentFloor getApartmentFloor() {
        return apartmentFloor;
    }

    public void setApartmentFloor(ApartmentFloor apartmentFloor) {
        this.apartmentFloor = apartmentFloor;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<ApartmentUnit> getGrouped() {
        return grouped;
    }

    public void setGrouped(List<ApartmentUnit> grouped) {
        this.grouped = grouped;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getApartmentTypeDesc() {
        return apartmentTypeDesc;
    }

    public void setApartmentTypeDesc(String apartmentTypeDesc) {
        this.apartmentTypeDesc = apartmentTypeDesc;
    }

    public double getPriceCash() {
        return priceCash;
    }

    public void setPriceCash(double priceCash) {
        this.priceCash = priceCash;
    }

    public double getPriceInstallment() {
        return priceInstallment;
    }

    public void setPriceInstallment(double priceInstallment) {
        this.priceInstallment = priceInstallment;
    }

    public double getPriceKpa() {
        return priceKpa;
    }

    public void setPriceKpa(double priceKpa) {
        this.priceKpa = priceKpa;
    }

    public double getTaxCash() {
        return taxCash;
    }

    public void setTaxCash(double taxCash) {
        this.taxCash = taxCash;
    }

    public double getTaxInstallment() {
        return taxInstallment;
    }

    public void setTaxInstallment(double taxInstallment) {
        this.taxInstallment = taxInstallment;
    }

    public double getTaxKpa() {
        return taxKpa;
    }

    public void setTaxKpa(double taxKpa) {
        this.taxKpa = taxKpa;
    }

    public double getTotalPriceCash() {
        return totalPriceCash;
    }

    public void setTotalPriceCash(double totalPriceCash) {
        this.totalPriceCash = totalPriceCash;
    }

    public double getTotalPriceInstallment() {
        return totalPriceInstallment;
    }

    public void setTotalPriceInstallment(double totalPriceInstallment) {
        this.totalPriceInstallment = totalPriceInstallment;
    }

    public double getTotalPriceKpa() {
        return totalPriceKpa;
    }

    public void setTotalPriceKpa(double totalPriceKpa) {
        this.totalPriceKpa = totalPriceKpa;
    }

    public double getMarkupPrice() {
        return markupPrice;
    }

    public void setMarkupPrice(double markupPrice) {
        this.markupPrice = markupPrice;
    }

    public int getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
    }
}
