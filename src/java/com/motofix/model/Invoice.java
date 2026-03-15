/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motofix.model;

import java.util.Date;

/**
 *
 * @author TRUONG
 */
public class Invoice {
    private int InvoiceID, OrderID, CustomerID, EmployeeID;
    private double TotalAmount, Discount, FinalAmount;
    private String PaymentStatus;
    private Date CreatedDate;
    private String fullNameCustomer, plateNumber;
    public Invoice() {
    }

    public Invoice(int InvoiceID, int OrderID, int CustomerID, int EmployeeID, double TotalAmount, double Discount, double FinalAmount, String PaymentStatus, Date CreatedDate) {
        this.InvoiceID = InvoiceID;
        this.OrderID = OrderID;
        this.CustomerID = CustomerID;
        this.EmployeeID = EmployeeID;
        this.TotalAmount = TotalAmount;
        this.Discount = Discount;
        this.FinalAmount = FinalAmount;
        this.PaymentStatus = PaymentStatus;
        this.CreatedDate = CreatedDate;
    }

    public String getFullNameCustomer() {
        return fullNameCustomer;
    }

    public void setFullNameCustomer(String fullNameCustomer) {
        this.fullNameCustomer = fullNameCustomer;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
    
    public int getInvoiceID() {
        return InvoiceID;
    }

    public void setInvoiceID(int InvoiceID) {
        this.InvoiceID = InvoiceID;
    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int OrderID) {
        this.OrderID = OrderID;
    }

    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int CustomerID) {
        this.CustomerID = CustomerID;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int EmployeeID) {
        this.EmployeeID = EmployeeID;
    }

    public double getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(double TotalAmount) {
        this.TotalAmount = TotalAmount;
    }

    public double getDiscount() {
        return Discount;
    }

    public void setDiscount(double Discount) {
        this.Discount = Discount;
    }

    public double getFinalAmount() {
        return FinalAmount;
    }

    public void setFinalAmount(double FinalAmount) {
        this.FinalAmount = FinalAmount;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(String PaymentStatus) {
        this.PaymentStatus = PaymentStatus;
    }

    public Date getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(Date CreatedDate) {
        this.CreatedDate = CreatedDate;
    }
    
    
}
