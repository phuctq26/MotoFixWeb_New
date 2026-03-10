package com.motofix.controller;

import com.motofix.dao.ServiceDAO;
import com.motofix.dao.PartDAO;
import com.motofix.dao.RepairTicketDAO;
import com.motofix.dao.TicketItemDAO;
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
        int ticketId;
        try {
            ticketId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/repairs");
            return;
        }

        if ("addItem".equals(action)) {
            // Use shared connection from ticketDAO (DBContext-based transaction)
            Connection conn = ticketDAO.connection;
            try {
                conn.setAutoCommit(false);
                try {
                    String mode = request.getParameter("mode");
                    int quantity = Integer.parseInt(request.getParameter("quantity"));
                    boolean success = true;

                    if ("catalog".equals(mode)) {
                        String catalogIdStr = request.getParameter("catalogId"); // Format: "SERVICE_1" or "PART_1"
                        if (catalogIdStr != null && catalogIdStr.contains("_")) {
                            String[] parts = catalogIdStr.split("_");
                            String type = parts[0];
                            int id = Integer.parseInt(parts[1]);

                            if ("SERVICE".equalsIgnoreCase(type)) {
                                Service s = serviceDAO.findById(id);
                                if (s != null) {
                                    itemDAO.addItem(ticketId, s.getServiceId(), s.getServiceName(),
                                            quantity, s.getPrice(), "SERVICE");
                                }
                            } else if ("PART".equalsIgnoreCase(type)) {
                                Part p = partDAO.findById(id);
                                if (p != null) {
                                    itemDAO.addItem(ticketId, p.getPartId(), p.getPartName(),
                                            quantity, p.getSellingPrice(), "PART");
                                }
                            }
                        }
                    } else {
                        String itemName = request.getParameter("itemName");
                        double unitPrice = Double.parseDouble(request.getParameter("unitPrice"));
                        String type = request.getParameter("type");
                        itemDAO.addItem(ticketId, null, itemName, quantity, unitPrice, type);
                    }

                    if (success) {
                        ticketDAO.updateTotals(ticketId);
                        conn.commit();
                    } else {
                        conn.rollback();
                    }
                } catch (Exception e) {
                    conn.rollback();
                    e.printStackTrace();
                    request.getSession().setAttribute("error", "Lỗi xử lý: " + e.getMessage());
                } finally {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                request.getSession().setAttribute("error", "Lỗi kết nối CSDL: " + e.getMessage());
            }
        } else if ("updateStatus".equals(action)) {
            try {
                String status = request.getParameter("status");
                ticketDAO.updateStatus(ticketId, status);

                if ("COMPLETED".equals(status)) {
                    response.sendRedirect(request.getContextPath() + "/admin/checkout?ticketId=" + ticketId);
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                request.getSession().setAttribute("error", "Lỗi cập nhật trạng thái: " + e.getMessage());
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/repairs?ticketId=" + ticketId);
    }
}
