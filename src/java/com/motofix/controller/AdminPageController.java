package com.motofix.controller;

import com.motofix.dao.ActivityDAO;
import com.motofix.dao.BookingDAO;
import com.motofix.dao.ConsulationDAO;
import com.motofix.dao.InvoiceDAO;
import com.motofix.dao.RepairTicketDAO;
import com.motofix.dao.TicketItemDAO;
import com.motofix.dao.UserDAO;
import com.motofix.model.consulation;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public class AdminPageController extends HttpServlet {

    private final RepairTicketDAO repairTicketDAO = new RepairTicketDAO();
    private final TicketItemDAO itemDAO = new TicketItemDAO();
    private final InvoiceDAO invoiceDao = new InvoiceDAO();
    private final BookingDAO bookingDao = new BookingDAO();
    private final UserDAO userDao = new UserDAO();
    private final ActivityDAO actiDao = new ActivityDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/admin/dashboard":
                request.setAttribute("revenueMonth", invoiceDao.getRevenueMonth());
                request.setAttribute("invoiceCount", invoiceDao.getInvoiceCount());
                request.setAttribute("revenueToday", invoiceDao.getRevenueToday());
                request.setAttribute("vehicleInprogress", repairTicketDAO.getNumberVehicleInprogress());
                request.setAttribute("vehiclePending", bookingDao.getVehiclePending());
                request.setAttribute("newUser", userDao.getNewUserToday());
                request.setAttribute("recentActivities", actiDao.getRecentActivities());
                
                request.setAttribute("chartLabels", invoiceDao.getRevenueLabelsLast7Days().toString());
                request.setAttribute("chartData", invoiceDao.getRevenueDataLast7Days().toString());
                
                request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);
                break;
            case "/admin/consultations":
                ConsulationDAO consulDAO = new ConsulationDAO();
                List<consulation> consulations = consulDAO.getAllConsulations1();
                request.setAttribute("consulations", consulations);
                request.getRequestDispatcher("/views/admin/consultations.jsp").forward(request, response);
                break;
            case "/admin/reception":
                request.getRequestDispatcher("/views/admin/reception.jsp").forward(request, response);
                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
}
