package com.motofix.controller;

import com.motofix.dao.InvoiceDAO;
import com.motofix.dao.RepairTicketDAO;
import com.motofix.dao.TicketItemDAO;
import com.motofix.model.RepairTicket;
import com.motofix.model.RevenueSummary;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminInvoiceController extends HttpServlet {

    private final RepairTicketDAO repairTicketDAO = new RepairTicketDAO();
    private final TicketItemDAO itemDAO = new TicketItemDAO();
    private final InvoiceDAO invoiceDao = new InvoiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("ticketId");
        String value = request.getParameter("value");

        int pageSize = 10;
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException ignored) {
            }
        }

        // --- Revenue Date Range Filter Logic ---
        String filterType = request.getParameter("filterType");
        String customStart = request.getParameter("startDate");
        String customEnd = request.getParameter("endDate");

        if (filterType == null || filterType.isEmpty()) {
            filterType = "this_month";
        }

        LocalDate startDate;
        LocalDate endDate;
        LocalDate today = LocalDate.now();

        switch (filterType) {
            case "last_month":
                YearMonth lastMonth = YearMonth.from(today).minusMonths(1);
                startDate = lastMonth.atDay(1);
                endDate = lastMonth.atEndOfMonth().plusDays(1);
                break;
            case "this_year":
                startDate = today.withDayOfYear(1);
                endDate = today.withDayOfYear(1).plusYears(1);
                break;
            case "last_year":
                startDate = today.minusYears(1).withDayOfYear(1);
                endDate = today.withDayOfYear(1);
                break;
            case "custom":
                try {
                    startDate = LocalDate.parse(customStart);
                    endDate = LocalDate.parse(customEnd).plusDays(1); // inclusive end
                } catch (Exception e) {
                    // fallback to this_month on invalid dates
                    filterType = "this_month";
                    YearMonth cm = YearMonth.from(today);
                    startDate = cm.atDay(1);
                    endDate = cm.atEndOfMonth().plusDays(1);
                }
                break;
            default: // this_month
                filterType = "this_month";
                YearMonth currentMonth = YearMonth.from(today);
                startDate = currentMonth.atDay(1);
                endDate = currentMonth.atEndOfMonth().plusDays(1);
                break;
        }

        RevenueSummary summary = invoiceDao.getRevenueSummary(startDate, endDate);

        try {
            int totalInvoices = (value != null && !value.isBlank())
                    ? invoiceDao.getInvoicesCount(value)
                    : invoiceDao.getInvoicesCount();
            int totalPages = totalInvoices > 0 ? (int) Math.ceil(totalInvoices / (double) pageSize) : 1;
            if (page < 1) {
                page = 1;
            } else if (page > totalPages) {
                page = totalPages;
            }

            request.setAttribute("invoices", (value != null && !value.isBlank())
                    ? invoiceDao.getInvoices(value, page, pageSize)
                    : invoiceDao.getInvoices(page, pageSize));
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalInvoices", totalInvoices);
            request.setAttribute("value", value);

            // Existing static summary cards
            request.setAttribute("revenueMonth", invoiceDao.getRevenueMonth());
            request.setAttribute("invoiceCount", invoiceDao.getInvoiceCount());
            request.setAttribute("revenueToday", invoiceDao.getRevenueToday());

            // Dynamic filtered summary
            request.setAttribute("revenueSummary", summary);
            request.setAttribute("filterType", filterType);
            request.setAttribute("customStartDate", customStart);
            request.setAttribute("customEndDate", customEnd);

            if (idParam != null) {
                int ticketId = Integer.parseInt(idParam);
                RepairTicket ticket = repairTicketDAO.findById(ticketId);
                request.setAttribute("ticket", ticket);
                request.setAttribute("items", itemDAO.listByTicket(ticketId));
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Không thể tải danh sách hóa đơn.");
        }
        request.getRequestDispatcher("/views/admin/invoices.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
