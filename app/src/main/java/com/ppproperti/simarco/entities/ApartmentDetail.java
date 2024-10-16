package com.ppproperti.simarco.entities;

public class ApartmentDetail {
    private int id;
    private String name;
    private String desc;
    private int apartmentId;
    private Apartment apartment;
    private int floor;
    private int unitNumber;
    private int apartmentTypeId;
    private ApartmentType apartmentType;
    private double surfaceNet;
    private double surfaceGross;
    private double price;
    private double discount;
    private double tax;
    private double totalPrice;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(int unitNumber) {
        this.unitNumber = unitNumber;
    }

    public ApartmentType getApartmentType() {
        return apartmentType;
    }

    public void setApartmentType(ApartmentType apartmentType) {
        this.apartmentType = apartmentType;
    }

    public double getSurfaceNet() {
        return surfaceNet;
    }

    public void setSurfaceNet(double surfaceNet) {
        this.surfaceNet = surfaceNet;
    }

    public double getSurfaceGross() {
        return surfaceGross;
    }

    public void setSurfaceGross(double surfaceGross) {
        this.surfaceGross = surfaceGross;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
    }

    public int getApartmentTypeId() {
        return apartmentTypeId;
    }

    public void setApartmentTypeId(int apartmentTypeId) {
        this.apartmentTypeId = apartmentTypeId;
    }
}
