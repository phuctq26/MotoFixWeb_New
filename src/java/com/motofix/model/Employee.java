package com.motofix.model;

/**
 * Maps to the Employees table in MotoFixDBNew.
 * NOTE: Run once → ALTER TABLE Employees ADD FullName NVARCHAR(100), Phone
 * VARCHAR(20);
 */
public class Employee {
    private int employeeId;
    private String fullName; // requires ALTER TABLE (see above)
    private String phone; // requires ALTER TABLE (see above)
    private String position;
    private long salary;
    private String hireDate; // yyyy-MM-dd
    private int status; // maps to Status BIT column

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int v) {
        this.employeeId = v;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String v) {
        this.fullName = v;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String v) {
        this.phone = v;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String v) {
        this.position = v;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long v) {
        this.salary = v;
    }

    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(String v) {
        this.hireDate = v;
    }

    /** Status BIT: true = đang làm, false = đã nghỉ */
    public int isActive() {
        return status;
    }
    
    public void setActive(int v) {
        this.status = v;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    
}
