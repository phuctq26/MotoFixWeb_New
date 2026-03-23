package com.motofix.controller;

import com.motofix.dao.RepairTicketDAO;
import com.motofix.dao.TicketItemDAO;
import com.motofix.dao.ServiceDAO;
import com.motofix.dao.UserDAO;
import com.motofix.dao.InvoiceDAO; 
import com.motofix.model.Invoice;
import com.motofix.model.RepairTicket;
import com.motofix.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class PageController extends HttpServlet {

    private static final int VEHICLES_PAGE_SIZE = 10;

    private final RepairTicketDAO repairTicketDAO = new RepairTicketDAO();
    private final TicketItemDAO ticketItemDAO = new TicketItemDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final UserDAO userDAO = new UserDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO(); 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session != null && session.getAttribute("user") != null) {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser.getRole() != null && currentUser.getRole().toString().equalsIgnoreCase("ADMIN")) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return; 
            }
        }

        String path = request.getServletPath();

        switch (path) {

            case "/home":
                try {
                    request.setAttribute("topServices", serviceDAO.listTop4());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                request.getRequestDispatcher("/views/customer/home.jsp").forward(request, response);
                break;

            case "/booking":
                request.getRequestDispatcher("/views/customer/booking.jsp").forward(request, response);
                break;

            case "/policy":
                request.getRequestDispatcher("/views/customer/policy.jsp").forward(request, response);
                break;

            case "/contact":
                
                request.getRequestDispatcher("/views/customer/contact.jsp").forward(request, response);
                break;

            case "/emergency":
                request.getRequestDispatcher("/views/customer/emergency.jsp").forward(request, response);
                break;

            case "/register":
                request.getRequestDispatcher("/views/customer/register.jsp").forward(request, response);
                break;

            case "/profile":
                request.getRequestDispatcher("/views/customer/profile.jsp").forward(request, response);
                break;

            case "/change-password":
                request.getRequestDispatcher("/views/customer/change-password.jsp").forward(request, response);
                break;

            case "/forgot-password":
                request.getRequestDispatcher("/views/customer/forgot-password.jsp").forward(request, response);
                break;

            case "/booking-success":
                request.getRequestDispatcher("/views/customer/booking-success.jsp").forward(request, response);
                break;

            case "/booking-detail":
                request.getRequestDispatcher("/views/customer/booking-detail.jsp").forward(request, response);
                break;

            // ================= INVOICE DETAIL =================
            case "/invoice-detail":

                User userInvoice = (User) request.getSession().getAttribute("user");

                if (userInvoice == null) {
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }

                String ticketIdParam = request.getParameter("ticketId");

                if (ticketIdParam != null) {
                    try {
                        int ticketId = Integer.parseInt(ticketIdParam);
                        int customerId = userDAO.getCustomerIdByAccountId(userInvoice.getUserId());

                        request.setAttribute("ticket",
                                repairTicketDAO.findByIdForCustomer(ticketId, customerId));

                        request.setAttribute("items",
                                ticketItemDAO.listByTicket(ticketId));

                    } catch (Exception e) {
                        request.setAttribute("error", "Không thể tải chi tiết hóa đơn.");
                    }
                }

                request.getRequestDispatcher("/views/customer/invoice-detail.jsp")
                        .forward(request, response);

                break;

            // ================= VEHICLES =================
            case "/vehicles":

                User user = (User) request.getSession().getAttribute("user");

                if (user == null) {
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }

                try {
                    int customerId = userDAO.getCustomerIdByAccountId(user.getUserId());

                    List<RepairTicket> allTickets = new ArrayList<>(repairTicketDAO.listByCustomer(customerId));

                    allTickets.sort((x, y) -> {
                        if (x.getCreatedAt() == null && y.getCreatedAt() == null) return 0;
                        if (x.getCreatedAt() == null) return 1;
                        if (y.getCreatedAt() == null) return -1;
                        return y.getCreatedAt().compareTo(x.getCreatedAt());
                    });

                    applyVehiclesSearchAndPagination(request, allTickets);

                } catch (SQLException e) {
                    request.setAttribute("error", "Không thể tải tiến độ sửa xe.");
                }

                request.getRequestDispatcher("/views/customer/vehicles.jsp")
                        .forward(request, response);

                break;

            // ================= INVOICES =================
            case "/invoices":

                User userInvoices = (User) request.getSession().getAttribute("user");

                if (userInvoices == null) {
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }

                try {
                    int customerId = userDAO.getCustomerIdByAccountId(userInvoices.getUserId());

                 
                    request.setAttribute("invoices",
                            invoiceDAO.getInvoicesByCustomer(customerId));

                } catch (Exception e) {
                    request.setAttribute("error", "Không thể tải hóa đơn.");
                }

                request.getRequestDispatcher("/views/customer/invoices.jsp")
                        .forward(request, response);

                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void applyVehiclesSearchAndPagination(HttpServletRequest request, List<RepairTicket> all) {
        if (all == null) {
            all = Collections.emptyList();
        }
        String qRaw = request.getParameter("q");
        String q = qRaw != null ? qRaw.trim() : "";
        String stRaw = request.getParameter("status");
        String status = stRaw != null ? stRaw.trim() : "";
        int page = parsePositivePage(request.getParameter("page"));

        List<RepairTicket> filtered = filterVehiclesTickets(all, q, status);
        int total = filtered.size();
        int totalPages = total <= 0 ? 1 : (int) Math.ceil((double) total / VEHICLES_PAGE_SIZE);
        if (page > totalPages) {
            page = totalPages;
        }
        int from = (page - 1) * VEHICLES_PAGE_SIZE;
        List<RepairTicket> slice = from >= total
                ? Collections.emptyList()
                : new ArrayList<>(filtered.subList(from, Math.min(from + VEHICLES_PAGE_SIZE, total)));

        request.setAttribute("tickets", slice);
        request.setAttribute("repairSearchQ", q);
        request.setAttribute("repairSearchStatus", status);
        request.setAttribute("repairPagerPage", page);
        request.setAttribute("repairPagerTotalPages", totalPages);
        request.setAttribute("repairPagerTotalCount", total);
    }

    private static int parsePositivePage(String s) {
        if (s == null || s.isEmpty()) {
            return 1;
        }
        try {
            int v = Integer.parseInt(s.trim());
            return v < 1 ? 1 : v;
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private static List<RepairTicket> filterVehiclesTickets(List<RepairTicket> all, String q, String status) {
        String qNorm = normalizeVehiclesQuery(q);
        boolean filterText = !qNorm.isEmpty();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
        List<RepairTicket> out = new ArrayList<>();
        for (RepairTicket t : all) {
            String tStatus = nullToEmpty(t.getStatus());

            boolean showOnlyTwoGroups = status == null || status.isEmpty() || "ALL".equalsIgnoreCase(status);
            boolean inProgressGroup = "IN_PROGRESS".equalsIgnoreCase(tStatus)
                    || "RECEIVED".equalsIgnoreCase(tStatus)
                    || "OPEN".equalsIgnoreCase(tStatus)
                    || "TESTING".equalsIgnoreCase(tStatus);
            boolean completedGroup = "COMPLETED".equalsIgnoreCase(tStatus)
                    || "DONE".equalsIgnoreCase(tStatus);

            if (showOnlyTwoGroups) {
                // Luôn chỉ hiển thị: đang sửa hoặc hoàn thành (không hiện các trạng thái bị từ chối).
                if (!(inProgressGroup || completedGroup)) {
                    continue;
                }
            } else if ("IN_PROGRESS".equalsIgnoreCase(status)) {
                if (!inProgressGroup) {
                    continue;
                }
            } else if ("COMPLETED".equalsIgnoreCase(status)) {
                if (!completedGroup) {
                    continue;
                }
            } else {
                // Trường hợp filter lạ: không hiển thị
                continue;
            }

            if (filterText && !matchesVehiclesSearch(t, qNorm, fmt)) {
                continue;
            }
            out.add(t);
        }
        return out;
    }

    private static boolean matchesVehiclesSearch(RepairTicket t, String qNorm, SimpleDateFormat fmt) {
        String plate = nullToEmpty(t.getPlateNumber()).toLowerCase(Locale.ROOT).replace(" ", "");
        if (plate.contains(qNorm)) {
            return true;
        }
        if (t.getCreatedAt() != null) {
            String d = fmt.format(t.getCreatedAt()).toLowerCase(Locale.ROOT);
            if (d.contains(qNorm)) {
                return true;
            }
        }
        return false;
    }

    private static String normalizeVehiclesQuery(String q) {
        if (q == null) {
            return "";
        }
        return q.trim().toLowerCase(Locale.ROOT).replace(" ", "");
    }

    private static String nullToEmpty(String s) {
        return s != null ? s : "";
    }

    /**
     * Lọc hóa đơn theo tham số GET {@code q}: mã (HD + số), ngày dd/MM/yyyy, biển số.
     */
    private void applyCustomerInvoiceSearch(HttpServletRequest request, List<Invoice> all) {
        if (all == null) {
            all = Collections.emptyList();
        }
        String qRaw = request.getParameter("q");
        String q = qRaw != null ? qRaw.trim() : "";
        request.setAttribute("invoiceSearchQ", q);
        if (q.isEmpty()) {
            request.setAttribute("invoices", all);
            return;
        }
        String qNorm = q.toLowerCase(Locale.ROOT).replace(" ", "");
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
        List<Invoice> out = new ArrayList<>();
        for (Invoice inv : all) {
            if (matchesCustomerInvoiceSearch(inv, qNorm, fmt)) {
                out.add(inv);
            }
        }
        request.setAttribute("invoices", out);
    }

    private static boolean matchesCustomerInvoiceSearch(Invoice inv, String qNorm, SimpleDateFormat fmt) {
        String plateNorm = nullToEmpty(inv.getPlateNumber()).toLowerCase(Locale.ROOT).replace(" ", "");
        if (!plateNorm.isEmpty() && plateNorm.contains(qNorm)) {
            return true;
        }
        if (inv.getCreatedDate() != null) {
            String d = fmt.format(inv.getCreatedDate()).toLowerCase(Locale.ROOT).replace(" ", "");
            if (d.contains(qNorm)) {
                return true;
            }
        }
        String idStr = String.valueOf(inv.getInvoiceID());
        String codeNorm = "hd" + idStr;
        if (qNorm.length() >= 3 && codeNorm.startsWith(qNorm)) {
            return true;
        }
        if (qNorm.startsWith("hd") && qNorm.length() > 2) {
            String suffix = qNorm.substring(2);
            if (suffix.matches("\\d+") && idStr.startsWith(suffix)) {
                return true;
            }
        }
        return qNorm.matches("\\d+") && idStr.equals(qNorm);
    }
}