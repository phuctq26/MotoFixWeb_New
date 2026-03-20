package com.motofix.model;


public class RevenueSummary {

    private double totalRevenue;
    private int totalInvoices;
    private double averageOrderValue;

    public RevenueSummary() {
    }

    public RevenueSummary(double totalRevenue, int totalInvoices, double averageOrderValue) {
        this.totalRevenue = totalRevenue;
        this.totalInvoices = totalInvoices;
        this.averageOrderValue = averageOrderValue;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(int totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public double getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(double averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }
}
