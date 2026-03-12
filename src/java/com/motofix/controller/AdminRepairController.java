package com.motofix.controller;

import com.motofix.dao.ServiceDAO;
import com.motofix.dao.PartDAO;
import com.motofix.dao.RepairTicketDAO;
import com.motofix.dao.TicketItemDAO;
import com.motofix.dao.partDetailDAO;
import com.motofix.dao.serviceDetailDAO;
import com.motofix.model.Service;
import com.motofix.model.Part;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminRepairController extends HttpServlet {

    private final RepairTicketDAO ticketDAO = new RepairTicketDAO();
    private final TicketItemDAO itemDAO = new TicketItemDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final PartDAO partDAO = new PartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("ticketId");
        try {
            request.setAttribute("tickets", ticketDAO.listForRepair());
            if (idParam != null) {
                int ticketId = Integer.parseInt(idParam);
                request.setAttribute("ticket", ticketDAO.findById(ticketId));
                request.setAttribute("items", itemDAO.listByTicket(ticketId));
                request.setAttribute("services", serviceDAO.listAll());
                request.setAttribute("parts", partDAO.listAll());
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Không thể tải dữ liệu sửa chữa.");
        }
        request.getRequestDispatcher("/views/admin/repairs.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String idParam = request.getParameter("ticketId");

        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/admin/repairs");
            return;
        }

        int ticketId = Integer.parseInt(idParam);

        // THÊM MỤC
        if ("addItem".equals(action)) {
            try {
                String mode = request.getParameter("mode");
                int quantity = Integer.parseInt(request.getParameter("quantity"));

                if ("catalog".equals(mode)) {
                    String catalogIdStr = request.getParameter("catalogId");

                    if (catalogIdStr != null && catalogIdStr.contains("_")) {
                        String[] parts = catalogIdStr.split("_");
                        String type = parts[0];
                        int id = Integer.parseInt(parts[1]);

                        if ("SERVICE".equalsIgnoreCase(type)) {
                            Service s = serviceDAO.findById(id);
                            if (s != null) {
                                serviceDetailDAO sDao = new serviceDetailDAO();
                                sDao.addService(ticketId, s.getServiceId(),
                                        quantity, s.getPrice());
                            }
                        } else if ("PART".equalsIgnoreCase(type)) {
                            Part p = partDAO.findById(id);
                            if (p != null) {
                                partDetailDAO pDao = new partDetailDAO();
                                pDao.addPart(ticketId, p.getPartId(),
                                        quantity, p.getSellingPrice());
                            }
                        }
                    }
                }

                // CẬP NHẬT LẠI TOTAL
                ticketDAO.updateTotals(ticketId);

            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
            }
        } else {
            try {
                String newStatus = request.getParameter("status");
                if (newStatus != null && !newStatus.isEmpty()) {
                    ticketDAO.updateStatus(ticketId, newStatus); 
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("error", "Lỗi cập nhật trạng thái: " + e.getMessage());
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/repairs?ticketId=" + ticketId);
    }
}
