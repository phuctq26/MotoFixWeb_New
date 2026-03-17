package com.motofix.controller;

import com.motofix.dao.BookingDAO;
import com.motofix.dao.UserDAO;
import com.motofix.dao.VehicleDAO;
import com.motofix.dao.RepairTicketDAO;
import com.motofix.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BookingController extends HttpServlet {

    private BookingDAO bookingDAO;
    private UserDAO userDAO;
    private VehicleDAO vehicleDAO;
    private RepairTicketDAO repairTicketDAO;

    @Override
    public void init() {

        bookingDAO = new BookingDAO();

        userDAO = new UserDAO(bookingDAO.connection);

        vehicleDAO = new VehicleDAO(bookingDAO.connection);

        repairTicketDAO = new RepairTicketDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User sessionUser = (User) request.getSession().getAttribute("user");

        if (sessionUser != null) {
            try {
                request.setAttribute("vehicles",
                        vehicleDAO.listByOwner(sessionUser.getUserId()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        request.getRequestDispatcher("/views/customer/booking.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User sessionUser=null;
        if (request.getSession(false) != null) {
            sessionUser = (User) request.getSession(false).getAttribute("user");
        }

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String plateNumber = request.getParameter("plateNumber");
        if (plateNumber != null) {
            plateNumber = plateNumber.trim();
        }
        String brand = request.getParameter("brand");
        String model = request.getParameter("model");
        String date = request.getParameter("bookingDate");
        String time = request.getParameter("bookingTime");
        String note = request.getParameter("note");
            try {

                int customerId=userDAO.getCustomerIdByAccountId(sessionUser.getUserId());
                Integer vehicleId = null;

                String vehicleOption = request.getParameter("vehicleOption");
                String vIdParam = request.getParameter("vehicleId");

                if ("existing".equals(vehicleOption)) {
                    if (vIdParam != null && !vIdParam.isEmpty()) {
                        vehicleId = Integer.parseInt(vIdParam);
                    }
                }

                // ===== XE MỚI =====
                if (vehicleId == null && plateNumber != null && !plateNumber.isEmpty()) {
                    if (vehicleId == null) {
                        vehicleId = vehicleDAO.create(
                                customerId,
                                plateNumber,
                                brand == null ? "" : brand,
                                model == null ? "" : model
                        );
                    }
                }

                

                // ===== DATE TIME =====
                LocalDate bookingDay = LocalDate.parse(date);

                LocalTime bookingTime = LocalTime.parse(
                        (time == null || time.isEmpty()) ? "09:00" : time
                );

                LocalDateTime bookingDateTime = LocalDateTime.of(bookingDay, bookingTime);

                Timestamp bookingDate = Timestamp.valueOf(bookingDateTime);
                // ===== CREATE BOOKING =====
                bookingDAO.create(customerId, vehicleId, bookingDate, note);
                request.setAttribute("success",
                        "Đặt lịch thành công! Chúng tôi sẽ liên hệ xác nhận.");
            } catch (NumberFormatException | SQLException e) {
                request.setAttribute("error",
                        "Không thể đặt lịch. Vui lòng thử lại.");
            } 

        if (sessionUser != null) {
            try {
                request.setAttribute("vehicles",
                        vehicleDAO.listByOwner(sessionUser.getUserId()));
            } catch (SQLException e) {
            }
        }
        request.getRequestDispatcher("/views/customer/booking.jsp")
                .forward(request, response);
    }
}
