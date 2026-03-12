
package com.motofix.model;

import java.util.Date;

public class consulation {
    private int ConsultationID;
    private String Name, Phone, Content;
    private Date CreatedAt;
    private boolean Status;

    public consulation() {
    }

    public consulation(int ConsultationID, String Name, String Phone, String Content, Date CreatedAt, boolean Status) {
        this.ConsultationID = ConsultationID;
        this.Name = Name;
        this.Phone = Phone;
        this.Content = Content;
        this.CreatedAt = CreatedAt;
        this.Status = Status;
    }

    public int getConsultationID() {
        return ConsultationID;
    }

    public void setConsultationID(int ConsultationID) {
        this.ConsultationID = ConsultationID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public Date getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Date CreatedAt) {
        this.CreatedAt = CreatedAt;
    }

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean Status) {
        this.Status = Status;
    }
    
    
}
