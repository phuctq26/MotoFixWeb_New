/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motofix.model;

/**
 *
 * @author TRUONG
 */
public class Activity {
    private String type;     
    private String category; 
    private String mainInfo; 
    private String subInfo;  
    private String timeAgo;  

    public Activity() {
    }

    public Activity(String type, String category, String mainInfo, String subInfo, String timeAgo) {
        this.type = type;
        this.category = category;
        this.mainInfo = mainInfo;
        this.subInfo = subInfo;
        this.timeAgo = timeAgo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMainInfo() {
        return mainInfo;
    }

    public void setMainInfo(String mainInfo) {
        this.mainInfo = mainInfo;
    }

    public String getSubInfo() {
        return subInfo;
    }

    public void setSubInfo(String subInfo) {
        this.subInfo = subInfo;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
    
    
}
