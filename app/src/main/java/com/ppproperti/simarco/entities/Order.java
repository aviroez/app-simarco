package com.ppproperti.simarco.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Order implements Parcelable {
    private int id;

    @SerializedName("user_id")
    private int userId;
    private User user;

    @SerializedName("customer_id")
    private int customerId;
    private Customer customer;

    @SerializedName("apartment_unit_id")
    private int apartmentUnitId;

    @SerializedName("apartment")
    private Apartment apartment = new Apartment();

    @SerializedName("apartment_unit")
    private ApartmentUnit apartmentUnit = new ApartmentUnit();

    @SerializedName("booking_no")
    private String bookingNo;

    @SerializedName("order_no")
    private String orderNo;

    @SerializedName("order_date")
    private String orderDate;
    private String name;
    private String nik;
    private String sim;
    private String npwp;
    private String address;
    private String district;
    private String regency;
    private String province;
    private String country;

    @SerializedName("address_correspondent")
    private String addressCorrespondent;

    @SerializedName("phone_number")
    private String phoneNumber;
    private String fax;
    private String handphone;
    private String email;

    @SerializedName("marketing_id")
    private int marketingId;
    private User marketing;

    private double price;
    private double tax;
    private double discount;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("booking_fee")
    private double bookingFee;

    @SerializedName("final_price")
    private double finalPrice;
    private String signature;
    private String gender;

    @SerializedName("signature_image_id")
    private int signatureImageId;
    private String status;

    @SerializedName("payment_schema_id")
    private int paymentSchemaId;

    @SerializedName("payment_schema")
    private PaymentSchema paymentSchema;

    @SerializedName("payment_type_id")
    private int paymentTypeId;

    @SerializedName("payment_type")
    private PaymentType paymentType;

    @SerializedName("dp_percent")
    private double dpPercent;

    @SerializedName("dp_installment")
    private int dpInstallment;

    @SerializedName("installment_number")
    private int installmentNumber;

    @SerializedName("cash_number")
    private int cashNumber;

    @SerializedName("order_installments")
    private List<OrderInstallment> orderInstallments = new ArrayList<>();

    public Order() {
    }

    protected Order(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        user = in.readParcelable(User.class.getClassLoader());
        customerId = in.readInt();
        customer = in.readParcelable(User.class.getClassLoader());
        apartmentUnitId = in.readInt();
        apartment = in.readParcelable(Apartment.class.getClassLoader());
        apartmentUnit = in.readParcelable(ApartmentUnit.class.getClassLoader());
        bookingNo = in.readString();
        orderNo = in.readString();
        orderDate = in.readString();
        name = in.readString();
        nik = in.readString();
        sim = in.readString();
        npwp = in.readString();
        address = in.readString();
        district = in.readString();
        regency = in.readString();
        province = in.readString();
        country = in.readString();
        addressCorrespondent = in.readString();
        phoneNumber = in.readString();
        fax = in.readString();
        handphone = in.readString();
        email = in.readString();
        marketingId = in.readInt();
        marketing = in.readParcelable(User.class.getClassLoader());
        price = in.readDouble();
        tax = in.readDouble();
        discount = in.readDouble();
        totalPrice = in.readDouble();
        bookingFee = in.readDouble();
        finalPrice = in.readDouble();
        signature = in.readString();
        gender = in.readString();
        signatureImageId = in.readInt();
        status = in.readString();
        paymentSchemaId = in.readInt();
        paymentSchema = in.readParcelable(PaymentSchema.class.getClassLoader());
        paymentTypeId = in.readInt();
        paymentType = in.readParcelable(PaymentType.class.getClassLoader());
        dpPercent = in.readDouble();
        dpInstallment = in.readInt();
        installmentNumber = in.readInt();
        cashNumber = in.readInt();
        orderInstallments = in.createTypedArrayList(OrderInstallment.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeParcelable(user, flags);
        dest.writeInt(customerId);
        dest.writeParcelable(customer, flags);
        dest.writeInt(apartmentUnitId);
        dest.writeParcelable(apartment, flags);
        dest.writeParcelable(apartmentUnit, flags);
        dest.writeString(bookingNo);
        dest.writeString(orderNo);
        dest.writeString(orderDate);
        dest.writeString(name);
        dest.writeString(nik);
        dest.writeString(sim);
        dest.writeString(npwp);
        dest.writeString(address);
        dest.writeString(district);
        dest.writeString(regency);
        dest.writeString(province);
        dest.writeString(country);
        dest.writeString(addressCorrespondent);
        dest.writeString(phoneNumber);
        dest.writeString(fax);
        dest.writeString(handphone);
        dest.writeString(email);
        dest.writeInt(marketingId);
        dest.writeParcelable(marketing, flags);
        dest.writeDouble(price);
        dest.writeDouble(tax);
        dest.writeDouble(discount);
        dest.writeDouble(totalPrice);
        dest.writeDouble(bookingFee);
        dest.writeDouble(finalPrice);
        dest.writeString(signature);
        dest.writeString(gender);
        dest.writeInt(signatureImageId);
        dest.writeString(status);
        dest.writeInt(paymentSchemaId);
        dest.writeParcelable(paymentSchema, flags);
        dest.writeInt(paymentTypeId);
        dest.writeParcelable(paymentType, flags);
        dest.writeDouble(dpPercent);
        dest.writeInt(dpInstallment);
        dest.writeInt(installmentNumber);
        dest.writeInt(cashNumber);
        dest.writeTypedList(orderInstallments);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getApartmentUnitId() {
        return apartmentUnitId;
    }

    public void setApartmentUnitId(int apartmentUnitId) {
        this.apartmentUnitId = apartmentUnitId;
    }

    public ApartmentUnit getApartmentUnit() {
        return apartmentUnit;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public double getBookingFee() {
        return bookingFee;
    }

    public void setBookingFee(double bookingFee) {
        this.bookingFee = bookingFee;
    }

    public void setApartmentUnit(ApartmentUnit apartmentUnit) {
        this.apartmentUnit = apartmentUnit;
    }

    public String getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(String bookingNo) {
        this.bookingNo = bookingNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
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

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getRegency() {
        return regency;
    }

    public void setRegency(String regency) {
        this.regency = regency;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getSignatureImageId() {
        return signatureImageId;
    }

    public void setSignatureImageId(int signatureImageId) {
        this.signatureImageId = signatureImageId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPaymentSchemaId() {
        return paymentSchemaId;
    }

    public void setPaymentSchemaId(int paymentSchemaId) {
        this.paymentSchemaId = paymentSchemaId;
    }

    public PaymentSchema getPaymentSchema() {
        return paymentSchema;
    }

    public void setPaymentSchema(PaymentSchema paymentSchema) {
        this.paymentSchema = paymentSchema;
    }

    public double getDpPercent() {
        return dpPercent;
    }

    public void setDpPercent(double dpPercent) {
        this.dpPercent = dpPercent;
    }

    public int getDpInstallment() {
        return dpInstallment;
    }

    public void setDpInstallment(int dpInstallment) {
        this.dpInstallment = dpInstallment;
    }

    public List<OrderInstallment> getOrderInstallments() {
        return orderInstallments;
    }

    public void setOrderInstallments(List<OrderInstallment> orderInstallments) {
        this.orderInstallments = orderInstallments;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getbookingFee() {
        return bookingFee;
    }

    public void setbookingFee(double bookingFee) {
        this.bookingFee = bookingFee;
    }

    public int getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(int paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getAddressCorrespondent() {
        return addressCorrespondent;
    }

    public void setAddressCorrespondent(String addressCorrespondent) {
        this.addressCorrespondent = addressCorrespondent;
    }

    public int getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(int installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public int getCashNumber() {
        return cashNumber;
    }

    public void setCashNumber(int cashNumber) {
        this.cashNumber = cashNumber;
    }
}
