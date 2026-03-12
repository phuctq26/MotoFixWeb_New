package com.motofix.model;

/**
 * Maps to the Parts table in MotoFixDBNew.
 */
public class Part {
    private int partId;
    private String partName;
    private String description;
    private double importPrice;
    private double sellingPrice;
    private int stockQuantity;
    private String imageUrl;
    private boolean isActive;

    public Part() {
    }

    public Part(int partId, String partName, String description, double importPrice, double sellingPrice, int stockQuantity, String imageUrl, boolean isActive) {
        this.partId = partId;
        this.partName = partName;
        this.description = description;
        this.importPrice = importPrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
    }
    
    
    
    public int getPartId() {
        return partId;
    }

    public void setPartId(int v) {
        this.partId = v;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String v) {
        this.partName = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        this.description = v;
    }

    public double getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(double v) {
        this.importPrice = v;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double v) {
        this.sellingPrice = v;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int v) {
        this.stockQuantity = v;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String v) {
        this.imageUrl = v;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean v) {
        this.isActive = v;
    }
}
