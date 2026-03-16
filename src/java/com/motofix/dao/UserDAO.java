package com.motofix.dao;

import com.motofix.model.User;
import java.sql.*;

public class UserDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT AccountID, Username, firstName, lastName, PasswordHash, Role, Email, IsActive "
                + "FROM Accounts WHERE Username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAccount(rs);
                }
            }
        }
        return null;
    }

    /**
     * Legacy — kept for compatibility
     */
    public User findByPhone(String phone) throws SQLException {
        try {
            String sql = """
            select 
                            a.AccountID,
                            c.CustomerID,
                            CONCAT(a.lastName, ' ', a.firstName) as FullName,
                            a.Username,
                            a.PasswordHash,
                            a.Role,
                            c.Address,
                            a.Email,
                            a.AvatarUrl,
                            a.IsActive
                        from Accounts a
                        join Customers c on a.AccountID = c.AccountID
                        where a.Username = ?
        """;

            st = connection.prepareStatement(sql);
            st.setString(1, phone);

            rs = st.executeQuery();

            if (rs.next()) {
                int accountId = rs.getInt("CustomerID");
                String fullName = rs.getString("FullName");
                String phoneNumber = rs.getString("Username");
                String passwordHash = rs.getString("PasswordHash");
                String role = rs.getString("Role");
                String address = rs.getString("Address");
                String email = rs.getString("Email");
                String avatar = rs.getString("AvatarUrl");
                boolean active = rs.getBoolean("IsActive");

                return new User(accountId, fullName, phoneNumber, passwordHash, role, address, email, avatar, active);
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public User findById(int userId) throws SQLException {
        try {
            String sql = """
            select 
                            a.AccountID,
                            CONCAT(a.lastName, ' ', a.firstName) as FullName,
                            a.Username,
                            a.PasswordHash,
                            a.Role,
                            c.Address,
                            a.Email,
                            a.AvatarUrl,
                            a.IsActive
                        from Accounts a
                        join Customers c on a.AccountID = c.AccountID
                        where c.CustomerID = ?
        """;

            st = connection.prepareStatement(sql);
            st.setInt(1, userId);

            rs = st.executeQuery();

            if (rs.next()) {
                int accountId = rs.getInt("AccountID");
                String fullName = rs.getString("FullName");
                String phoneNumber = rs.getString("Username");
                String passwordHash = rs.getString("PasswordHash");
                String role = rs.getString("Role");
                String address = rs.getString("Address");
                String email = rs.getString("Email");
                String avatar = rs.getString("AvatarUrl");
                boolean active = rs.getBoolean("IsActive");

                return new User(accountId, fullName, phoneNumber, passwordHash, role, address, email, avatar, active);
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int createCustomer(String fullName, String phone) throws SQLException {

        int CustomerID = -1;
        int accountId = -1;
        try {
            String sql = """
                INSERT INTO Accounts 
                    (lastName, firstName, Username, PasswordHash, Role, Email, AvatarUrl, IsActive)
                VALUES
                    (?, ?, ?, ?, 'Customer', ?, null, 1);
                """;

            String[] strings = fullName.split("\\s+");
            String lastName = strings[strings.length - 1];
            StringBuilder stt = new StringBuilder();
            for (int i = 0; i < strings.length - 1; i++) {
                stt.append(strings[i]).append(" ");
            }
            String firstName = stt.toString().trim();
            String mail = lastName + "@gmail.com";
            st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, lastName);
            st.setString(2, firstName);
            st.setString(3, phone);
            st.setString(4, phone);
            st.setString(5, mail);
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                accountId = rs.getInt(1);
            }

            if (accountId != -1) {
                String sqlCus = "INSERT INTO Customers (AccountID, Address) VALUES (?, '')";
                // CHỖ SỬA 1: Thêm Statement.RETURN_GENERATED_KEYS
                PreparedStatement st2 = connection.prepareStatement(sqlCus, Statement.RETURN_GENERATED_KEYS);
                st2.setInt(1, accountId);
                st2.executeUpdate();

                // CHỖ SỬA 2: Lấy ID từ st2
                ResultSet rs2 = st2.getGeneratedKeys();
                if (rs2.next()) {
                    CustomerID = rs2.getInt(1); // Đây mới là ID thật của Customer
                }
            }
            return CustomerID;

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return -1;
    }

    public int createCustomerWithPassword(String fullName, String phone, String password) throws SQLException {

        String sql = "INSERT INTO Accounts (Username, firstName, lastName, Email, PasswordHash, Role, IsActive) VALUES (?, ?, ?, ?, ?, 'CUSTOMER', 1)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, phone);      // Username dùng phone
            stmt.setString(2, fullName);   // firstName
            stmt.setString(3, "");         // lastName
            stmt.setString(4, "");
            stmt.setString(5, password);

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {

                int accountId = rs.getInt(1);

                // Tạo record trong bảng Customers
                String sqlCustomer = "INSERT INTO Customers(AccountID) VALUES (?)";

                PreparedStatement stmt2 = connection.prepareStatement(sqlCustomer);

                stmt2.setInt(1, accountId);

                stmt2.executeUpdate();

                return accountId;
            }
        }

        throw new SQLException("Create customer failed");
    }

    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT AccountID, Username, firstName, lastName, PasswordHash, Role, Email, IsActive "
                + "FROM Accounts WHERE Username = ? AND IsActive = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("PasswordHash");
                    if (storedPassword != null && storedPassword.equals(password)) {
                        return mapAccount(rs);
                    }
                }
            }
        }
        return null;
    }

    // Helper to map ResultSet from Accounts table to User
    private User mapAccount(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("AccountID"));
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String full = ((lastName != null ? lastName : "") + " " + (firstName != null ? firstName : "")).trim();
        user.setFullName(full);
        user.setEmail(rs.getString("Email"));
        user.setPasswordHash(rs.getString("PasswordHash"));
        user.setRole(rs.getString("Role"));
        user.setActive(rs.getBoolean("IsActive"));
        try {
            user.setPhone(rs.getString("Username"));
        } catch (SQLException ignored) {
        }
        return user;
    }

    public java.util.List<User> listByRole(String role) throws SQLException {
        String sql = "SELECT UserID, FullName, Phone, Role, Address, Email FROM Users "
                + "WHERE Role = ? ORDER BY UserID DESC";
        java.util.List<User> list = new java.util.ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("UserID"));
                    user.setFullName(rs.getString("FullName"));
                    user.setPhone(rs.getString("Phone"));
                    user.setRole(rs.getString("Role"));
                    user.setAddress(rs.getString("Address"));
                    try {
                        user.setEmail(rs.getString("Email"));
                    } catch (SQLException ignored) {
                    }
                    list.add(user);
                }
            }
        }
        return list;
    }

    /**
     * Create customer with all fields: fullName, phone, password, email,
     * address.
     */
    public void createCustomerFull(String fullName, String phone, String password,
            String email, String address) throws SQLException {
        String sql = "INSERT INTO Accounts (FullName, Phone, PasswordHash, Role, Email, Address) "
                + "VALUES (?, ?, ?, 'CUSTOMER', ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            stmt.setString(2, phone);
            stmt.setString(3, password);
            stmt.setString(4, email != null ? email : "");
            stmt.setString(5, address != null ? address : "");
            stmt.executeUpdate();
        }
    }

    public void createStaff(String fullName, String phone, String password, String role) throws SQLException {
        String sql = "INSERT INTO Users (FullName, Phone, PasswordHash, Role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            stmt.setString(2, phone);
            stmt.setString(3, password);
            stmt.setString(4, role);
            stmt.executeUpdate();
        }
    }

    public void update(int userId, String fullName, String phone, String role, String address) throws SQLException {
        String sql = "UPDATE Users SET FullName = ?, Phone = ?, Role = ?, Address = ? WHERE UserID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            stmt.setString(2, phone);
            stmt.setString(3, role);
            stmt.setString(4, address);
            stmt.setInt(5, userId);
            stmt.executeUpdate();
        }
    }

    public void update(int userId, String fullName, String phone, String role, String address, String email)
            throws SQLException {
        String sql = "UPDATE Users SET FullName = ?, Phone = ?, Role = ?, Address = ?, Email = ? WHERE UserID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            stmt.setString(2, phone);
            stmt.setString(3, role);
            stmt.setString(4, address);
            stmt.setString(5, email);
            stmt.setInt(6, userId);
            stmt.executeUpdate();
        }
    }

    public void updateProfile(int userId, String fullName, String phone, String address) throws SQLException {

        // update account
        String sqlAccount = "UPDATE Accounts SET firstName = ?, Username = ? WHERE AccountID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sqlAccount)) {

            stmt.setString(1, fullName);
            stmt.setString(2, phone);
            stmt.setInt(3, userId);

            stmt.executeUpdate();
        }

        // update address
        String sqlCustomer = "UPDATE Customers SET Address = ? WHERE AccountID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sqlCustomer)) {

            stmt.setString(1, address);
            stmt.setInt(2, userId);

            stmt.executeUpdate();
        }
    }

    public void changePassword(int userId, String newPassword) throws SQLException {

        String sql = "UPDATE Accounts SET PasswordHash = ? WHERE AccountID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);

            stmt.executeUpdate();
        }
    }

    /**
     * Hard-delete a customer and ALL related data in the correct FK order:
     * TicketItems → RepairTickets → Bookings → Vehicles → User
     */
    public void delete(int userId) throws SQLException {
        connection.setAutoCommit(false);
        try {
            exec("DELETE ti FROM TicketItems ti "
                    + "JOIN RepairTickets rt ON ti.TicketID = rt.TicketID "
                    + "WHERE rt.CustomerID = ?", userId);
            exec("DELETE FROM RepairTickets WHERE CustomerID = ?", userId);
            exec("DELETE FROM Bookings WHERE CustomerID = ?", userId);
            exec("DELETE FROM Vehicles WHERE OwnerID = ?", userId);
            exec("DELETE FROM Accounts WHERE UserID = ?", userId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public UserDAO() {
        super();
    }

    public UserDAO(Connection conn) {
        this.connection = conn;
    }

    /**
     * Helper: run a single-param int DELETE
     */
    private void exec(String sql, int param) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, param);
            stmt.executeUpdate();
        }
    }

    public int getNewUserToday() {
        try {
            String sql = """
                            SELECT COUNT(*) AS TotalNewAccounts
                                FROM Accounts
                                WHERE CAST(CreatedAt AS DATE) = CAST(GETDATE() AS DATE);
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql

            rs = st.executeQuery();
            if (rs.next()) {
                int SoLuongHoaDon = rs.getInt("TotalNewAccounts");
                return SoLuongHoaDon;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }
}
