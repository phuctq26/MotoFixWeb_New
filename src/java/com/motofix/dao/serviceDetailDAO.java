/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motofix.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author TRUONG
 */
public class serviceDetailDAO extends DBContext{
    PreparedStatement st;
        ResultSet rs;
        
        
    public  void addService(int ticketId, int serviceId, int quantity, double price) {
       
        try{
            String sql = """
                            INSERT INTO RepairServiceDetails (OrderID, ServiceID, Quantity, UnitPrice)
                                        VALUES (?, ?, ?, ?)
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            st.setInt(1, ticketId);
            st.setInt(2, serviceId);
            st.setInt(3, quantity);
            st.setDouble(4, price);
            st.executeUpdate();
            
                    
        }catch(SQLException e){
            
        }
    }
    
}
