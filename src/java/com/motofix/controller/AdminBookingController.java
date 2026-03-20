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
        String filter = request.getParameter("filter");
        if (filter == null || filter.isEmpty()) {
            filter = "pending"; 
        }
        
        String searchValue = request.getParameter("value");
        if (searchValue == null) searchValue = "";
        
        String pageStr = request.getParameter("page");
        int page = 1;
        int pageSize = 10;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException ignored) {}
        }
        
        try {
            int totalBookings = bookingDAO.countBookings(filter, searchValue);
            int totalPages = (int) Math.ceil((double) totalBookings / pageSize);
            int offset = (page - 1) * pageSize;
            
            request.setAttribute("bookings", bookingDAO.listBookingsPaged(filter, searchValue, offset, pageSize));
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("currentFilter", filter);
            request.setAttribute("currentSearch", searchValue);
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
               
            }
        }
        
        String filter = request.getParameter("filter");
        String page = request.getParameter("page");
        String searchValue = request.getParameter("value");
        
        String redirectUrl = request.getContextPath() + "/admin/bookings";
        boolean hasQuery = false;
        if (filter != null && !filter.isEmpty()) { 
            redirectUrl += "?filter=" + filter; 
            hasQuery = true; 
        }
        if (searchValue != null && !searchValue.isEmpty()) { 
            redirectUrl += (hasQuery ? "&" : "?") + "value=" + java.net.URLEncoder.encode(searchValue, "UTF-8"); 
            hasQuery = true; 
        }
        if (page != null && !page.isEmpty()) { 
            redirectUrl += (hasQuery ? "&" : "?") + "page=" + page; 
        }
        response.sendRedirect(redirectUrl);
    }
}
