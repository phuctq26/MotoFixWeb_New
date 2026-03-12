/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motofix.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author TRUONG
 */
public class partDetailDAO extends DBContext{

    PreparedStatement st;
    ResultSet rs;

    public void addPart(int ticketId, int partId, int quantity, double sellingPrice) {
        try {
            String sql = """
                            INSERT INTO RepairPartDetails (OrderID, PartID, Quantity, UnitPrice)
                                        VALUES (?, ?, ?, ?)
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            st.setInt(1, ticketId);
            st.setInt(2, partId);
            st.setInt(3, quantity);
            st.setDouble(4, sellingPrice);
            st.executeUpdate();

        } catch (Exception e) {

        }
    }

}
