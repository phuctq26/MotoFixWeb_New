package com.motofix.controller;

import com.motofix.dao.BookingDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;

public class AdminBookingController extends HttpServlet {
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("bookings", bookingDAO.listAll());
        } catch (SQLException e) {
            request.setAttribute("error", "Không thể tải danh sách đặt lịch.");
        }
        request.getRequestDispatcher("/views/admin/bookings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String idParam = request.getParameter("bookingId");
        if (action != null && idParam != null) {
            try {
                int bookingId = Integer.parseInt(idParam);
                if ("approve".equalsIgnoreCase(action)) {
                    bookingDAO.updateStatus(bookingId, "CONFIRMED");
                } else if ("reject".equalsIgnoreCase(action)) {
                    bookingDAO.updateStatus(bookingId, "CANCELLED");
                }
            } catch (Exception e) {
                // ignore and redirect
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/bookings");
    }
}
