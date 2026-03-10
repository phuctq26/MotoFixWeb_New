package com.motofix.controller;

import com.motofix.dao.BookingDAO;
import com.motofix.dao.UserDAO;
import com.motofix.dao.VehicleDAO;
import com.motofix.model.User;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BookingController extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser != null) {
            try {
                request.setAttribute("vehicles", vehicleDAO.listByOwner(sessionUser.getUserId()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        request.getRequestDispatcher("/views/customer/booking.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        User sessionUser = null;
        if (request.getSession(false) != null) {
            sessionUser = (User) request.getSession(false).getAttribute("user");
        }

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");

        String vehicleOption = request.getParameter("vehicleOption"); // "existing" or "new"
        String plateNumber = request.getParameter("plateNumber");
        String brand = request.getParameter("brand");
        String model = request.getParameter("model");

        String date = request.getParameter("bookingDate");
        String time = request.getParameter("bookingTime");
        String note = request.getParameter("note");

        // Use the shared connection from bookingDAO (DBContext-based transaction)
        Connection conn = bookingDAO.connection;
        try {
            conn.setAutoCommit(false);
            try {
                int customerId;
                if (sessionUser != null) {
                    customerId = sessionUser.getUserId();
                } else {
                    User user = userDAO.findByPhone(phone);
                    customerId = (user == null) ? userDAO.createCustomer(fullName, phone) : user.getUserId();
                }

                Integer vehicleId = null;

                if ("existing".equals(vehicleOption) && sessionUser != null) {
                    String vIdParam = request.getParameter("vehicleId");
                    if (vIdParam != null && !vIdParam.isEmpty()) {
                        vehicleId = Integer.parseInt(vIdParam);
                    }
                } else {
                    if (plateNumber != null && !plateNumber.trim().isEmpty()) {
                        vehicleId = vehicleDAO.findByOwnerAndPlate(customerId, plateNumber);
                        if (vehicleId == null) {
                            vehicleId = vehicleDAO.create(customerId, plateNumber, brand, model);
                        }
                    }
                }

                LocalDateTime dateTime = LocalDateTime.parse(date + " " + time,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                Timestamp bookingDate = Timestamp.valueOf(dateTime);

                bookingDAO.create(customerId, vehicleId, bookingDate, note);
                conn.commit();
                request.setAttribute("success", "Đặt lịch thành công! Chúng tôi sẽ liên hệ xác nhận.");
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                request.setAttribute("error", "Không thể đặt lịch. Vui lòng thử lại.");
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi kết nối hệ thống.");
        }

        // Re-fetch vehicles for the view
        if (sessionUser != null) {
            try {
                request.setAttribute("vehicles", vehicleDAO.listByOwner(sessionUser.getUserId()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        request.getRequestDispatcher("/views/customer/booking.jsp").forward(request, response);
    }
}
