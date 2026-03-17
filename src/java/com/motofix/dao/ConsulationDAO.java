package com.motofix.dao;

import com.motofix.model.consulation;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.*;

public class ConsulationDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public List<consulation> getAllConsulations() {
        List<consulation> consulations = new ArrayList<>();
        try {
            String sql = """
                         select c.ConsultationID, c.Name, c.Phone, c.Content, c.CreatedAt, c.Status
                         from Consultation as c
                         
                         ORDER BY c.Status ASC, c.CreatedAt DESC;
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            rs = st.executeQuery(); // select
            while (rs.next()) {
                int ConsultationID = rs.getInt("ConsultationID");
                String Name = rs.getString("Name");
                String Phone = rs.getString("Phone");
                String Content = rs.getString("Content");
                Date CreatedAt = rs.getDate("CreatedAt");
                boolean Status = rs.getBoolean("Status");
                consulation consu = new consulation(ConsultationID, Name, Phone, Content, CreatedAt, Status);
                consulations.add(consu);
            }
            return consulations;
        } catch (Exception e) {
            return null;
        }
    }

    public List<consulation> getAllConsulations1() {
        List<consulation> consulations = new ArrayList<>();
        try {
            String sql = """
                         SELECT 
                             c.ConsultationID, 
                             c.Name, 
                             c.Phone, 
                             c.Content, 
                             c.CreatedAt, 
                             c.Status
                         FROM Consultation AS c
                         where Status = 0
                         ORDER BY c.Status ASC, c.CreatedAt DESC;
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            rs = st.executeQuery(); // select
            while (rs.next()) {
                int ConsultationID = rs.getInt("ConsultationID");
                String Name = rs.getString("Name");
                String Phone = rs.getString("Phone");
                String Content = rs.getString("Content");
                Date CreatedAt = rs.getDate("CreatedAt");
                boolean Status = rs.getBoolean("Status");
                consulation consu = new consulation(ConsultationID, Name, Phone, Content, CreatedAt, Status);
                consulations.add(consu);
            }
            return consulations;
        } catch (Exception e) {
            return null;
        }
    }

    public List<consulation> getByStatus(boolean b) {
        List<consulation> consulations = new ArrayList<>();
        try {
            String sql = """
                     select c.ConsultationID, c.Name, c.Phone, c.Content, c.CreatedAt, c.Status
                     from Consultation as c
                     where c.Status = ?
                     """;

            st = connection.prepareStatement(sql);
            st.setBoolean(1, b);

            rs = st.executeQuery();

            while (rs.next()) {
                consulation consu = new consulation(
                        rs.getInt("ConsultationID"),
                        rs.getString("Name"),
                        rs.getString("Phone"),
                        rs.getString("Content"),
                        rs.getDate("CreatedAt"),
                        rs.getBoolean("Status")
                );
                consulations.add(consu);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return consulations;
    }

    public void updateStatus(int id, boolean b) {
        try {
            String sql = "UPDATE Consultation SET Status = ? WHERE ConsultationID = ?";
            st = connection.prepareStatement(sql);
            st.setBoolean(1, b);
            st.setInt(2, id);
            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        try {
            String sql = "DELETE FROM Consultation WHERE ConsultationID = ?";
            st = connection.prepareStatement(sql);
            st.setInt(1, id);
            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createConsulation(String fullName, String phoneNumber, String content) {
        try {
            String sql = """
                            insert into Consultation
                            (Name, Phone, Content, CreatedAt, Status)
                            values
                            (?, ?, ?, GETDATE(), 0)
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            st.setString(1, fullName);
            st.setString(2, phoneNumber);
            st.setString(3,content);

            st.executeUpdate();
        } catch (SQLException e) {

        }
    }

}
