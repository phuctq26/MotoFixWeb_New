package com.motofix.controller;

import com.motofix.dao.InvoiceDAO;
import com.motofix.dao.RepairTicketDAO;
import com.motofix.model.Invoice;
import com.motofix.model.RepairTicket;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminRevenueController extends HttpServlet {

    private final RepairTicketDAO repairTicketDAO = new RepairTicketDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("recentInvoices", repairTicketDAO.listRecentInvoices());
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải dữ liệu doanh thu: " + e.getMessage());
        }
        request.getRequestDispatcher("/views/admin/revenue.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            String action = request.getParameter("action");
            String id = request.getParameter("id");
            if (action.equals("pay")) {
                InvoiceDAO invoiceDao = new InvoiceDAO();
                RepairTicketDAO rpDao = new RepairTicketDAO();

                rpDao.updateStatus(Integer.parseInt(id), "DONE");
                RepairTicket repair = rpDao.findById(Integer.parseInt(id));
               

                Invoice invoiceData = rpDao.getDataForInvoice(Integer.parseInt(id));

                if (invoiceData != null) {
                    invoiceData.setDiscount(0); 
                    invoiceData.setFinalAmount(invoiceData.getTotalAmount() - invoiceData.getDiscount());
                    invoiceData.setPaymentStatus("PAID");
                    invoiceDao.createInvoice(invoiceData);
                }
                
            } else {
                RepairTicketDAO ticketDAO = new RepairTicketDAO();
                ticketDAO.updateStatus(Integer.parseInt(id), "IN_PROGRESS");
            }

            request.setAttribute("recentInvoices", repairTicketDAO.listRecentInvoices());
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải dữ liệu doanh thu: " + e.getMessage());
        }
        request.getRequestDispatcher("/views/admin/revenue.jsp").forward(request, response);

    }
}
