<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List,java.text.SimpleDateFormat,com.motofix.model.RepairTicket,com.motofix.model.TicketItem,com.motofix.model.Invoice,jakarta.servlet.http.HttpServletRequest" %>
<!doctype html>
<html lang="vi">
    <head>
        <title>Hóa đơn | MotoFix Admin</title>
        <%@ include file="_head.jspf" %>
    </head>
    <body>
        <div class="layout">
            <% request.setAttribute("activeMenu","invoices"); %>
            <% request.setAttribute("pageTitle","Danh sách hóa đơn"); %>
            <%@ include file="_sidebar.jspf" %>
            <main class="content">
                <%@ include file="_topbar.jspf" %>

                <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                <% } %>

<%@ page import="com.motofix.model.RevenueSummary" %>

                <div class="row g-3 mb-3">
                    <div class="col-md-4">
                        <div class="card stat-card">
                            <div class="d-flex align-items-center gap-3">
                                <div class="stat-icon bg-light-primary text-primary"><i class="bi bi-cash"></i></div>
                                <div>
                                    <div class="text-muted">Tổng doanh thu (tháng này)</div>
                                    <div class="fw-bold"><%= String.format("%,.0f", request.getAttribute("revenueMonth") != null ? (Double) request.getAttribute("revenueMonth") : 0.0) %>đ</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card stat-card">
                            <div class="d-flex align-items-center gap-3">
                                <div class="stat-icon bg-light-success text-success"><i class="bi bi-receipt"></i></div>
                                <div>
                                    <div class="text-muted">Số lượng hóa đơn</div>
                                    <div class="fw-bold"><%= request.getAttribute("invoiceCount") != null ? request.getAttribute("invoiceCount") : 0 %></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card stat-card">
                            <div class="d-flex align-items-center gap-3">
                                <div class="stat-icon bg-light-info text-info"><i class="bi bi-calendar"></i></div>
                                <div>
                                    <div class="text-muted">Hôm nay</div>
                                    <div class="fw-bold"><%= String.format("%,.0f", request.getAttribute("revenueToday") != null ? (Double) request.getAttribute("revenueToday") : 0.0) %>đ</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Revenue Date Range Filter -->
                <div class="card p-3 mb-3 shadow-sm">
                    <form action="invoices" method="get" id="revenueFilterForm">
                        <div class="d-flex flex-wrap align-items-end gap-2">
                            <div>
                                <label class="form-label fw-semibold mb-1 small"><i class="bi bi-funnel me-1"></i>Lọc doanh thu theo</label>
                                <select name="filterType" id="filterTypeSelect" class="form-select form-select-sm" style="min-width:160px;" onchange="onFilterChange()">
                                    <option value="this_month" ${filterType == 'this_month' ? 'selected' : ''}>Tháng này</option>
                                    <option value="last_month" ${filterType == 'last_month' ? 'selected' : ''}>Tháng trước</option>
                                    <option value="this_year" ${filterType == 'this_year' ? 'selected' : ''}>Năm nay</option>
                                    <option value="last_year" ${filterType == 'last_year' ? 'selected' : ''}>Năm trước</option>
                                    <option value="custom" ${filterType == 'custom' ? 'selected' : ''}>Tùy chỉnh...</option>
                                </select>
                            </div>
                            <div id="customDateRange" style="display: ${filterType == 'custom' ? 'flex' : 'none'};" class="align-items-end gap-2">
                                <div>
                                    <label class="form-label mb-1 small">Từ ngày</label>
                                    <input type="date" name="startDate" id="startDateInput" class="form-control form-control-sm" value="${customStartDate}" />
                                </div>
                                <div>
                                    <label class="form-label mb-1 small">Đến ngày</label>
                                    <input type="date" name="endDate" id="endDateInput" class="form-control form-control-sm" value="${customEndDate}" />
                                </div>
                            </div>
                            <button type="submit" class="btn btn-primary btn-sm"><i class="bi bi-bar-chart me-1"></i>Xem thống kê</button>
                        </div>
                    </form>

                    <%
                        RevenueSummary revSummary = (RevenueSummary) request.getAttribute("revenueSummary");
                        if (revSummary != null) {
                    %>
                    <div class="row g-3 mt-2">
                        <div class="col-md-4">
                            <div class="border rounded p-2 text-center bg-light">
                                <div class="text-muted small">Doanh thu lọc</div>
                                <div class="fw-bold text-primary fs-6"><%= String.format("%,.0f", revSummary.getTotalRevenue()) %>đ</div>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="border rounded p-2 text-center bg-light">
                                <div class="text-muted small">Số hóa đơn</div>
                                <div class="fw-bold text-success fs-6"><%= revSummary.getTotalInvoices() %></div>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="border rounded p-2 text-center bg-light">
                                <div class="text-muted small">Trung bình/đơn</div>
                                <div class="fw-bold text-info fs-6"><%= String.format("%,.0f", revSummary.getAverageOrderValue()) %>đ</div>
                            </div>
                        </div>
                    </div>
                    <% } %>
                </div>

                <script>
                function onFilterChange() {
                    var sel = document.getElementById('filterTypeSelect');
                    var customDiv = document.getElementById('customDateRange');
                    if (sel.value === 'custom') {
                        customDiv.style.display = 'flex';
                    } else {
                        customDiv.style.display = 'none';
                        document.getElementById('revenueFilterForm').submit();
                    }
                }
                </script>

                <div class="row g-4">

                    <div class="col-lg-5">
                        <div class="card p-4 shadow-sm">

                            <form action="invoices" method="get">
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <div class="input-group" style="max-width:360px;">
                                        <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
                                        <input name="value"  class="form-control" value="${value}" placeholder="Tìm theo SĐT hoặc biển số xe..." />
                                    </div>
                                    <button class="btn btn-outline-secondary"><i class="bi bi-funnel"></i> Lọc</button>
                                </div>
                            </form>

                            <h6 class="fw-bold mb-3"><i class="bi bi-list-ul me-2"></i>Hóa đơn đã thanh toán</h6>
                            <div class="table-responsive">
                                <table class="table align-middle table-hover">
                                    <thead>
                                        <tr>
                                            <th>Mã Đơn/Ngày</th>
                                            <th>Khách hàng</th>
                                            <th class="text-end">Tổng tiền</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                          List<Invoice> invoices = (List<Invoice>) request.getAttribute("invoices");
                                          SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                                          if (invoices != null && !invoices.isEmpty()) {
                                            for (Invoice inv : invoices) {
                                        %>
                                        <tr style="cursor: pointer" onclick="window.location.href = '${pageContext.request.contextPath}/admin/invoices?ticketId=<%= inv.getOrderID() %>'">                                            <td>
                                                <span class="fw-bold">#<%= inv.getInvoiceID() %></span>
                                                <div class="small text-muted"><%= inv.getCreatedDate() != null ? fmt.format(inv.getCreatedDate()) : "N/A" %></div>
                                            </td>
                                            <td>
                                                <div class="fw-semibold text-primary"><%= inv.getFullNameCustomer() %></div>
                                                <small class="badge bg-light text-dark border"><%= inv.getPlateNumber() %></small>
                                            </td>
                                            <td class="text-end fw-bold text-success">
                                                <%= String.format("%,.0f", inv.getFinalAmount()) %>đ
                                            </td>
                                        </tr>
                                        <%      }
                                          } else { %>
                                        <tr>
                                            <td colspan="3" class="text-center py-4 text-muted small">Chưa có hóa đơn nào được tìm thấy.</td>
                                        </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                            <%
                                Integer currentPage = (Integer) request.getAttribute("currentPage");
                                Integer totalPages = (Integer) request.getAttribute("totalPages");
                                String value = request.getParameter("value");
                                String ticketIdParam = request.getParameter("ticketId");
                                if (currentPage == null) currentPage = 1;
                                if (totalPages == null) totalPages = 1;

                                String encodedValue = "";
                                if (value != null && !value.isBlank()) {
                                    try {
                                        encodedValue = java.net.URLEncoder.encode(value, "UTF-8");
                                    } catch (java.io.UnsupportedEncodingException ignored) {
                                        encodedValue = value;
                                    }
                                }
                            %>
                            <% if (totalPages > 1) { %>
                            <nav aria-label="Page navigation" class="mt-3">
                                <ul class="pagination justify-content-center mb-0">
                                    <li class="page-item <%= currentPage <= 1 ? "disabled" : "" %>">
                                        <a class="page-link" href="<%= ((HttpServletRequest) request).getContextPath() %>/admin/invoices?page=<%= currentPage-1 %><%= value != null && !value.isBlank() ? "&value=" + encodedValue : "" %><%= ticketIdParam != null ? "&ticketId=" + ticketIdParam : "" %>">Trước</a>
                                    </li>
                                    <% int start = Math.max(1, currentPage - 2);
                                       int end = Math.min(totalPages, currentPage + 2);
                                       for (int p = start; p <= end; p++) { %>
                                    <li class="page-item <%= p == currentPage ? "active" : "" %>">
                                        <a class="page-link" href="<%= ((HttpServletRequest) request).getContextPath() %>/admin/invoices?page=<%= p %><%= value != null && !value.isBlank() ? "&value=" + encodedValue : "" %><%= ticketIdParam != null ? "&ticketId=" + ticketIdParam : "" %>"><%= p %></a>
                                    </li>
                                    <% } %>
                                    <li class="page-item <%= currentPage >= totalPages ? "disabled" : "" %>">
                                        <a class="page-link" href="<%= ((HttpServletRequest) request).getContextPath() %>/admin/invoices?page=<%= currentPage+1 %><%= value != null && !value.isBlank() ? "&value=" + encodedValue : "" %><%= ticketIdParam != null ? "&ticketId=" + ticketIdParam : "" %>">Sau</a>
                                    </li>
                                </ul>
                            </nav>
                            <% } %>

                        </div>
                    </div>

                    <div class="col-lg-7">
                        <div class="card p-4 shadow-sm border-start border-primary border-4">
                            <%
                              RepairTicket ticket = (RepairTicket) request.getAttribute("ticket");
                              List<TicketItem> items = (List<TicketItem>) request.getAttribute("items");
                            %>
                            <% if (ticket == null) { %>
                            <div class="text-center py-5">
                                <i class="bi bi-receipt text-muted" style="font-size: 3rem;"></i>
                                <div class="mt-2 text-muted">Chọn một hóa đơn từ danh sách để xem chi tiết hạng mục.</div>
                            </div>
                            <% } else { %>
                            <div class="d-flex justify-content-between align-items-start mb-3">
                                <div>
                                    <h5 class="fw-bold mb-1 text-uppercase">Chi tiết hóa đơn</h5>
                                    <span class="badge bg-primary">Phiếu sửa chữa:#<%= ticket.getTicketId() %></span>
                                </div>
                                <div class="text-end">
                                    <span class="badge bg-success">ĐÃ THANH TOÁN</span>
                                </div>
                            </div>

                            <div class="row mb-4">
                                <div class="col-6">
                                    <small class="text-muted d-block text-uppercase">Khách hàng</small>
                                    <div class="fw-bold"><%= ticket.getCustomerName() %></div>
                                    <div class="small"><%= ticket.getPhone() != null ? ticket.getPhone() : "" %></div>
                                </div>
                                <div class="col-6 text-end">
                                    <small class="text-muted d-block text-uppercase">Phương tiện</small>
                                    <div class="fw-bold"><%= ticket.getPlateNumber() %></div>
                                </div>
                            </div>

                            <div class="table-responsive">
                                <table class="table table-sm align-middle">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Hạng mục dịch vụ/phụ tùng</th>
                                            <th class="text-center">SL</th>
                                            <th class="text-end">Đơn giá</th>
                                            <th class="text-end">Thành tiền</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% if (items != null && !items.isEmpty()) {
                                            for (TicketItem it : items) { %>
                                        <tr>
                                            <td><%= it.getItemName() %></td>
                                            <td class="text-center"><%= it.getQuantity() %></td>
                                            <td class="text-end"><%= String.format("%,.0f", it.getUnitPrice()) %>đ</td>
                                            <td class="text-end fw-semibold"><%= String.format("%,.0f", it.getTotalPrice()) %>đ</td>
                                        </tr>
                                        <% } } else { %>
                                        <tr><td colspan="4" class="text-muted text-center py-3">Không có chi tiết hạng mục cho đơn này.</td></tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>

                            <div class="mt-4 border-top pt-3">
                                <div class="d-flex justify-content-between mb-2">
                                    <span class="text-muted">Tổng tiền dịch vụ & linh kiện:</span>
                                    <span class="fw-bold"><%= String.format("%,.0f", ticket.getTotalAmount()) %>đ</span>
                                </div>
                                <div class="d-flex justify-content-between mb-2 text-danger">
                                    <span class="text-muted">Chiết khấu/Giảm giá:</span>
                                    <span class="fw-bold">-<%= String.format("%,.0f", ticket.getDiscount()) %>đ</span>
                                </div>
                                <hr />
                                <div class="d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0 fw-bold">Tổng thanh toán:</h5>
                                    <h4 class="mb-0 fw-bold text-primary"><%= String.format("%,.0f", ticket.getFinalAmount()) %>đ</h4>
                                </div>
                            </div>

                            <div class="mt-4 d-grid gap-2 d-md-flex justify-content-md-end">
                                <button class="btn btn-outline-dark btn-sm" onclick="window.print()">
                                    <i class="bi bi-printer me-1"></i> In nhanh
                                </button>
                                <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/admin/invoice-print?ticketId=<%= ticket.getTicketId() %>">
                                    <i class="bi bi-file-earmark-pdf me-1"></i> Xuất PDF
                                </a>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>

                <%@ include file="_footer.jspf" %>
            </main>
        </div>
    </body>
</html>