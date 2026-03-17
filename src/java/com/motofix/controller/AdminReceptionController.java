package com.motofix.controller;

import com.motofix.dao.RepairTicketDAO;
import com.motofix.dao.UserDAO;
import com.motofix.dao.VehicleDAO;
import com.motofix.model.User;
import com.motofix.model.Vehicle;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class AdminReceptionController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final RepairTicketDAO ticketDAO = new RepairTicketDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("todayOrders", ticketDAO.getTodayRepairOrders());
        request.getRequestDispatcher("/views/admin/reception.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("search".equals(action)) {
                handleSearch(request);
            } else if ("createTicketCombined".equals(action)) {
                handleCreateTicketCombined(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        request.getRequestDispatcher("/views/admin/reception.jsp").forward(request, response);
    }

    private void handleSearch(HttpServletRequest request) throws SQLException {
        String phone = request.getParameter("phone");
        request.setAttribute("searchedPhone", phone);
        User customer = userDAO.findByPhone(phone);
        if (customer == null) {
            request.setAttribute("notFound", true);
            return;
        }
        request.setAttribute("customer", customer);
        List<Vehicle> vehicles = vehicleDAO.listByOwner(customer.getUserId());
        request.setAttribute("vehicles", vehicles);

        Integer bookingVehicleId = vehicleDAO.getVehicleFromBooking(customer.getUserId());
        request.setAttribute("bookingVehicleId", bookingVehicleId);
        
        request.setAttribute("searchedPhone", customer.getPhone());
    }

    private void handleCreateTicketCombined(HttpServletRequest request) throws SQLException {
        String customerType = request.getParameter("customerType"); // "existing" or "new"
        int customerId;
        // 1. Determine Customer
        if ("existing".equals(customerType)) {
            customerId = Integer.parseInt(request.getParameter("customerId"));
            
        } else {
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");
            customerId = userDAO.createCustomer(fullName, phone);
        }

        // 2. Determine Vehicle
        int vehicleId;
        String vehicleOption = request.getParameter("vehicleOption"); // "existing" or "new"

        if ("existing".equals(vehicleOption) && "existing".equals(customerType)) {
            vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
        } else {
            // Create new vehicle (for new customer OR existing customer adding new vehicle)
            String plateNumber = request.getParameter("plateNumber");
            String brand = request.getParameter("brand");
            String model = request.getParameter("model");
            vehicleId = vehicleDAO.create(customerId, plateNumber, brand, model);
        }

        // 3. Create Ticket
        String diagnosis = request.getParameter("diagnosis");
        int ticketId = ticketDAO.create(customerId, vehicleId, diagnosis);

        // 4. Set attributes for success view 
        User customer = userDAO.findById(customerId);
        request.setAttribute("customer", customer);
        request.setAttribute("vehicles", vehicleDAO.listByOwner(customerId));
        
        request.setAttribute("message", "Đã tiếp nhận xe thành công! Mã phiếu: TK-" + ticketId);
        request.setAttribute("ticketId", ticketId);

        
    }
}
