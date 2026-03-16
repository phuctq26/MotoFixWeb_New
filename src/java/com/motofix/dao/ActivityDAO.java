/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motofix.dao;

import com.motofix.model.Activity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
/**
 *
 * @author TRUONG
 */
public class ActivityDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    private String formatTimeAgo(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        long diff = System.currentTimeMillis() - timestamp.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (seconds < 60) {
            return "Vừa xong";
        }
        if (minutes < 60) {
            return minutes + " phút trước";
        }
        if (hours < 24) {
            return hours + " giờ trước";
        }
        return days + " ngày trước";
    }

    public List<Activity> getRecentActivities() {
        List<Activity> list = new ArrayList<>();

        
        String sql = """
        SELECT TOP 5 * FROM (
            
            SELECT 
                N'Yêu cầu mới' AS ActivityType, 
                A.firstName + ' ' + A.lastName AS MainInfo, 
                B.CreatedAt AS ActivityTime, 
                B.Note AS SubInfo, 
                'warning' AS Category 
            FROM Bookings B 
            JOIN Customers C ON B.CustomerID = C.CustomerID 
            JOIN Accounts A ON C.AccountID = A.AccountID 
            WHERE B.Status = 'PENDING'
            
            UNION ALL 
            
            
            SELECT 
                N'Hoàn thành' AS ActivityType, 
                V.PlateNumber AS MainInfo, 
                RO.CompletedAt AS ActivityTime, 
                N'KTV ' + E.FullName AS SubInfo, 
                'success' AS Category 
            FROM RepairOrders RO 
            JOIN Vehicles V ON RO.VehicleID = V.VehicleID 
            JOIN Employees E ON RO.EmployeeID = E.EmployeeID 
            WHERE RO.Status IN ('COMPLETED', 'DONE') 
            
            UNION ALL 
            
            
            SELECT 
                N'Cảnh báo kho' AS ActivityType, 
                P.PartName + N' sắp hết' AS MainInfo, 
                GETDATE() AS ActivityTime, 
                N'Còn lại: ' + CAST(P.StockQuantity AS NVARCHAR(10)) AS SubInfo, 
                'danger' AS Category 
            FROM Parts P 
            WHERE P.StockQuantity < 5 AND P.IsActive = 1
        ) AS RecentActivities 
        ORDER BY ActivityTime DESC
        """;
        
        
        try  {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                Activity act = new Activity();
                act.setType(rs.getNString("ActivityType"));
                act.setMainInfo(rs.getNString("MainInfo"));
                act.setSubInfo(rs.getNString("SubInfo"));
                act.setCategory(rs.getString("Category"));

                Timestamp time = rs.getTimestamp("ActivityTime");
                act.setTimeAgo(formatTimeAgo(time));

                list.add(act);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
