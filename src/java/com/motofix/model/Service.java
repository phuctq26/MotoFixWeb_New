package com.motofix.model;

/**
 * Maps to the Services table in MotoFixDBNew.
 */
public class Service {
    private int serviceId;
    private String serviceName;
    private String description;
    private double price;
    private boolean isActive;

    public Service() {
    }

    public Service(int serviceId, String serviceName, String description, double price, boolean isActive) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.description = description;
        this.price = price;
        this.isActive = isActive;
    }
    
    
    
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int v) {
        this.serviceId = v;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String v) {
        this.serviceName = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        this.description = v;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double v) {
        this.price = v;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean v) {
        this.isActive = v;
    }
}
