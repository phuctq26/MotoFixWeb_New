package com.motofix.dao;

import com.motofix.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO extends DBContext {

    private Customer map(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("CustomerID"));
        c.setAccountId(rs.getInt("AccountID"));
        c.setUsername(rs.getString("Username"));
        c.setFirstName(rs.getString("firstName"));
        c.setLastName(rs.getString("lastName"));
        c.setEmail(rs.getString("Email"));
        c.setAddress(rs.getString("Address"));
        c.setActive(rs.getBoolean("IsActive"));
        try {
            c.setAvatarUrl(rs.getString("AvatarUrl"));
        } catch (SQLException ignored) {
        }
        return c;
    }

    public List<Customer> listAll() throws SQLException {
        String sql = "SELECT c.CustomerID, a.AccountID, a.Username, a.firstName, a.lastName, "
                + "a.Email, a.AvatarUrl, a.IsActive, c.Address "
                + "FROM Customers c JOIN Accounts a ON c.AccountID = a.AccountID "
                + "ORDER BY c.CustomerID DESC";
        List<Customer> list = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public int countAll(String searchValue, String statusFilter) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Customers c JOIN Accounts a ON c.AccountID = a.AccountID WHERE 1=1";
        if ("active".equals(statusFilter)) {
            sql += " AND a.IsActive = 1";
        } else if ("inactive".equals(statusFilter)) {
            sql += " AND a.IsActive = 0";
        }
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            sql += " AND (a.firstName LIKE ? OR a.lastName LIKE ? OR a.Username LIKE ? OR c.Address LIKE ?)";
        }
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                String k = "%" + searchValue.trim() + "%";
                stmt.setString(1, k);
                stmt.setString(2, k);
                stmt.setString(3, k);
                stmt.setString(4, k);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<Customer> listPaged(String searchValue, String statusFilter, int offset, int limit) throws SQLException {
        String sql = "SELECT c.CustomerID, a.AccountID, a.Username, a.firstName, a.lastName, "
                + "a.Email, a.AvatarUrl, a.IsActive, c.Address "
                + "FROM Customers c JOIN Accounts a ON c.AccountID = a.AccountID WHERE 1=1";
        if ("active".equals(statusFilter)) {
            sql += " AND a.IsActive = 1";
        } else if ("inactive".equals(statusFilter)) {
            sql += " AND a.IsActive = 0";
        }
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            sql += " AND (a.firstName LIKE ? OR a.lastName LIKE ? OR a.Username LIKE ? OR c.Address LIKE ?)";
        }
        sql += " ORDER BY c.CustomerID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        List<Customer> list = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int paramIdx = 1;
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                String k = "%" + searchValue.trim() + "%";
                stmt.setString(paramIdx++, k);
                stmt.setString(paramIdx++, k);
                stmt.setString(paramIdx++, k);
                stmt.setString(paramIdx++, k);
            }
            stmt.setInt(paramIdx++, offset);
            stmt.setInt(paramIdx, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public Customer findById(int customerId) throws SQLException {
        String sql = "SELECT c.CustomerID, a.AccountID, a.Username, a.firstName, a.lastName, "
                + "a.Email, a.AvatarUrl, a.IsActive, c.Address "
                + "FROM Customers c JOIN Accounts a ON c.AccountID = a.AccountID "
                + "WHERE c.CustomerID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public Customer findByUsername(String username) throws SQLException {
        String sql = "SELECT c.CustomerID, a.AccountID, a.Username, a.firstName, a.lastName, "
                + "a.Email, a.AvatarUrl, a.IsActive, c.Address "
                + "FROM Customers c JOIN Accounts a ON c.AccountID = a.AccountID "
                + "WHERE a.Username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void create(String username, String firstName, String lastName,
            String email, String password, String address) throws SQLException {
        String passToStore = (password != null && !password.isEmpty())
                ? password
                : "123";

        connection.setAutoCommit(false);
        try {
            int accountId;
            String sqlAcc = "INSERT INTO Accounts (Username, PasswordHash, firstName, lastName, Email, Role) "
                    + "VALUES (?, ?, ?, ?, ?, 'CUSTOMER')";
            try (PreparedStatement stmt = connection.prepareStatement(sqlAcc, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, username);
                stmt.setString(2, passToStore);
                stmt.setString(3, firstName);
                stmt.setString(4, lastName);
                stmt.setString(5, email);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to get AccountID");
                    }
                    accountId = rs.getInt(1);
                }
            }
            String sqlCust = "INSERT INTO Customers (AccountID, Address) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sqlCust)) {
                stmt.setInt(1, accountId);
                stmt.setString(2, address != null ? address : "");
                stmt.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void update(int customerId, String firstName, String lastName,
            String email, String address, boolean isActive) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String sqlAcc = "UPDATE Accounts SET firstName=?, lastName=?, Email=?, IsActive=? "
                    + "WHERE AccountID = (SELECT AccountID FROM Customers WHERE CustomerID=?)";
            try (PreparedStatement stmt = connection.prepareStatement(sqlAcc)) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setString(3, email);
                stmt.setBoolean(4, isActive);
                stmt.setInt(5, customerId);
                stmt.executeUpdate();
            }
            String sqlCust = "UPDATE Customers SET Address=? WHERE CustomerID=?";
            try (PreparedStatement stmt = connection.prepareStatement(sqlCust)) {
                stmt.setString(1, address != null ? address : "");
                stmt.setInt(2, customerId);
                stmt.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void deactivate(int customerId) throws SQLException {
        String sql = "UPDATE Accounts SET IsActive=0 "
                + "WHERE AccountID = (SELECT AccountID FROM Customers WHERE CustomerID=?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.executeUpdate();
        }
    }

    public void activate(int customerId) throws SQLException {
        String sql = "UPDATE Accounts SET IsActive=1 "
                + "WHERE AccountID = (SELECT AccountID FROM Customers WHERE CustomerID=?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.executeUpdate();
        }
    }
}
