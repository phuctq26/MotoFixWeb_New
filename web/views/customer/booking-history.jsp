<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,java.text.SimpleDateFormat,java.net.URLEncoder,java.nio.charset.StandardCharsets,com.motofix.model.Booking" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="vi">
<head>
    <title>Lịch sử đặt lịch | MotoFix</title>
    <%@ include file="_head.jspf" %>
</head>
<body>
    <%-- Đánh dấu menu active cho Header --%>
    <% request.setAttribute("activeMenu","bookings"); %>
    <%@ include file="_header.jspf" %>

    <section class="page-hero">
        <div class="container">
            <h2>Lịch sử đặt lịch</h2>
            <p class="text-light">Xem lại các yêu cầu đặt lịch sửa chữa đã được cửa hàng xử lý.</p>
        </div>
    </section>

    <section class="py-5">
        <div class="container">
            <div class="row g-4">
                <div class="col-lg-3">
                    <%-- Đánh dấu active cho Sidebar --%>
                    <% request.setAttribute("activeAccount","bookings"); %>
                    <%@ include file="_account_sidebar.jspf" %>
                </div>
                <div class="col-lg-9">
                    <div class="table-card p-4 bg-white rounded-3 shadow-sm">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h5 class="fw-bold mb-0">Danh sách lịch hẹn đã xử lý</h5>
                            <span class="text-muted small">Cập nhật bởi cửa hàng</span>
                        </div>
                        <%
                            String currentSearch = (String) request.getAttribute("currentSearch");
                            if (currentSearch == null) {
                                currentSearch = "";
                            }
                            String encodedSearch = URLEncoder.encode(currentSearch, StandardCharsets.UTF_8);
                        %>
                        <form action="${pageContext.request.contextPath}/booking-history" method="get" class="mb-3">
                            <div class="input-group">
                                <input
                                    type="text"
                                    class="form-control"
                                    name="search"
                                    placeholder="Tìm theo mã lịch, biển số, trạng thái, ghi chú..."
                                    value="<%= currentSearch %>">
                                <button class="btn btn-primary" type="submit">
                                    <i class="bi bi-search"></i> Tìm kiếm
                                </button>
                            </div>
                        </form>

                        <div class="table-responsive">
                            <table class="table align-middle">
                                <thead>
                                    <tr>
                                        <th>Ngày hẹn</th>
                                        <th>Mã lịch</th>
                                        <th>Biển số xe</th>
                                        <th>Trạng thái</th>
                                        <th class="text-end">Ghi chú</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        List<Booking> listB = (List<Booking>) request.getAttribute("listB");
                                        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                        
                                        if (listB != null && !listB.isEmpty()) {
                                            for (Booking b : listB) {
                                                String status = b.getStatus();
                                                String pillClass = "status-pill";
                                                String label = status;

                                                if ("CONFIRMED".equalsIgnoreCase(status)) {
                                                    label = "Đã tiếp nhận";
                                                    pillClass = "status-pill success"; // Màu xanh lá
                                                } else if ("CANCELLED".equalsIgnoreCase(status)) {
                                                    label = "Đã hủy";
                                                    pillClass = "status-pill danger"; // Màu đỏ
                                                }
                                    %>
                                    <tr>
                                        <td class="fw-semibold"><%= b.getBookingDate() != null ? fmt.format(b.getBookingDate()) : "" %></td>
                                        <td><span class="text-primary">#BK-<%= b.getBookingId() %></span></td>
                                        <td><%= (b.getPlateNumber() != null) ? b.getPlateNumber() : "---" %></td>
                                        <td><span class="<%= pillClass %>"><%= label %></span></td>
                                        <td class="text-end text-muted small"><%= (b.getNote() != null) ? b.getNote() : "" %></td>
                                    </tr>
                                    <% 
                                            } 
                                        } else { 
                                    %>
                                    <tr>
                                        <td colspan="5" class="text-center py-5 text-muted">
                                            <i class="bi bi-calendar-x d-block mb-2 fs-2"></i>
                                            Bạn chưa có lịch hẹn nào được xử lý.
                                        </td>
                                    </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                        <%
                            Integer currentPageObj = (Integer) request.getAttribute("currentPage");
                            Integer totalPagesObj = (Integer) request.getAttribute("totalPages");
                            int currentPage = currentPageObj != null ? currentPageObj : 1;
                            int totalPages = totalPagesObj != null ? totalPagesObj : 1;
                            if (totalPages > 1) {
                        %>
                        <nav aria-label="Booking pagination" class="mt-3">
                            <ul class="pagination justify-content-end mb-0">
                                <li class="page-item <%= (currentPage <= 1) ? "disabled" : "" %>">
                                    <a class="page-link" href="${pageContext.request.contextPath}/booking-history?page=<%= currentPage - 1 %>&search=<%= encodedSearch %>">Trước</a>
                                </li>
                                <%
                                    for (int i = 1; i <= totalPages; i++) {
                                %>
                                <li class="page-item <%= (i == currentPage) ? "active" : "" %>">
                                    <a class="page-link" href="${pageContext.request.contextPath}/booking-history?page=<%= i %>&search=<%= encodedSearch %>"><%= i %></a>
                                </li>
                                <%
                                    }
                                %>
                                <li class="page-item <%= (currentPage >= totalPages) ? "disabled" : "" %>">
                                    <a class="page-link" href="${pageContext.request.contextPath}/booking-history?page=<%= currentPage + 1 %>&search=<%= encodedSearch %>">Sau</a>
                                </li>
                            </ul>
                        </nav>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <%@ include file="_footer.jspf" %>
</body>
</html>