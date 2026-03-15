<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,com.motofix.model.Booking,java.text.SimpleDateFormat" %>
<!doctype html>

<html lang="vi">
    <head>
        <title>Yêu cầu đặt lịch | MotoFix Admin</title>
        <%@ include file="_head.jspf" %>
    </head>
    <body>
        <div class="layout">
            <% request.setAttribute("activeMenu","bookings"); %>
            <% request.setAttribute("pageTitle","Danh sách yêu cầu đặt lịch"); %>
            <%@ include file="_sidebar.jspf" %>
            <main class="content">
                <%@ include file="_topbar.jspf" %>

                <div class="card p-4">
                    <form action="${pageContext.request.contextPath}/admin/bookings" method="get">
                        <input type="hidden" name="filter" value="<%= request.getAttribute("currentFilter") %>"/>
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <div class="input-group" style="max-width:360px;">
                                <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
                                <input name="value" class="form-control" placeholder="Tìm theo SĐT hoặc biển số xe..." value="<c:out value="${currentSearch}"/>" />
                            </div>
                            <button type="submit" class="btn btn-outline-secondary"><i class="bi bi-funnel"></i> Lọc</button>
                        </div>
                    </form>

                    <ul class="nav nav-tabs mb-4">
                        <% String curSearch = request.getAttribute("currentSearch") != null ? (String) request.getAttribute("currentSearch") : ""; 
                           String searchParam = !curSearch.isEmpty() ? "&value=" + java.net.URLEncoder.encode(curSearch, "UTF-8") : ""; %>
                        <li class="nav-item">
                            <a class="nav-link <%= "all".equals(request.getAttribute("currentFilter")) ? "active fw-bold" : "" %>" href="?filter=all<%=searchParam%>">Tất cả</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link <%= "pending".equals(request.getAttribute("currentFilter")) ? "active fw-bold" : "" %>" href="?filter=pending<%=searchParam%>">Chờ xử lý</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link <%= "processed".equals(request.getAttribute("currentFilter")) ? "active fw-bold" : "" %>" href="?filter=processed<%=searchParam%>">Đã xử lý</a>
                        </li>
                    </ul>

                    <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                    <% } %>

                    <div class="table-responsive">
                        <table class="table align-middle">
                            <thead>
                                <tr>
                                    <th>Khách hàng</th>
                                    <th>Xe</th>
                                    <th>Giờ hẹn</th>
                                    <th>Ghi chú</th>
                                    <th>Trạng thái</th>
                                    <th class="text-end">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                  List<Booking> bookings = (List<Booking>) request.getAttribute("bookings");
                                  SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                  if (bookings != null && !bookings.isEmpty()) {
                                    for (Booking b : bookings) {
                                      String status = b.getStatus();
                                      String badgeClass = "badge-soft info";
                                      String statusText = status;
                                      if ("PENDING".equalsIgnoreCase(status)) { badgeClass = "badge-soft warning"; statusText = "Chờ duyệt"; }
                                      else if ("CONFIRMED".equalsIgnoreCase(status)) { badgeClass = "badge-soft info"; statusText = "Đã duyệt"; }
                                      else if ("CANCELLED".equalsIgnoreCase(status)) { badgeClass = "badge-soft danger"; statusText = "Từ chối"; }
                                %>
                                <tr>
                                    <td>
                                        <div class="fw-semibold"><%= b.getCustomerName() %></div>
                                        <small class="text-muted"><%= b.getPhone() %></small>
                                    </td>
                                    <td><%= (b.getPlateNumber() == null ? "(Chưa có)" : b.getPlateNumber()) %></td>
                                    <td><%= b.getBookingDate() != null ? fmt.format(b.getBookingDate()) : "" %></td>
                                    <td><%= b.getNote() == null ? "" : b.getNote() %></td>
                                    <td><span class="<%= badgeClass %>"><%= statusText %></span></td>
                                    <td class="text-end">
                                        <% if ("PENDING".equalsIgnoreCase(status)) { %>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/bookings" class="d-inline">
                                            <input type="hidden" name="bookingId" value="<%= b.getBookingId() %>" />
                                            <input type="hidden" name="action" value="approve" />
                                            <input type="hidden" name="filter" value="<%= request.getAttribute("currentFilter") %>" />
                                            <input type="hidden" name="page" value="<%= request.getAttribute("currentPage") %>" />
                                            <button class="btn btn-sm btn-primary">Duyệt</button>
                                        </form>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/bookings" class="d-inline">
                                            <input type="hidden" name="bookingId" value="<%= b.getBookingId() %>" />
                                            <input type="hidden" name="action" value="reject" />
                                            <input type="hidden" name="filter" value="<%= request.getAttribute("currentFilter") %>" />
                                            <input type="hidden" name="page" value="<%= request.getAttribute("currentPage") %>" />
                                            <button class="btn btn-sm btn-outline-danger">Từ chối</button>
                                        </form>
                                        <% } else { %>
                                        <button class="btn btn-sm btn-outline-secondary" disabled>Đã xử lý</button>
                                        <% } %>
                                    </td>
                                </tr>
                                <%
                                    }
                                  } else {
                                %>
                                <tr>
                                    <td colspan="6" class="text-center text-muted">Chưa có yêu cầu đặt lịch.</td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    
                    <!-- ===== PAGINATION ===== -->
                    <%
                        Integer totalPagesObj = (Integer) request.getAttribute("totalPages");
                        Integer currentPageObj = (Integer) request.getAttribute("currentPage");
                        String currentFilter = (String) request.getAttribute("currentFilter");
                        if (totalPagesObj != null && totalPagesObj > 1) {
                            int totalPages = totalPagesObj;
                            int currentPage = currentPageObj;
                    %>
                    <div class="d-flex justify-content-end mt-3">
                        <nav>
                            <ul class="pagination pagination-sm mb-0">
                                <li class="page-item <%= (currentPage == 1) ? "disabled" : "" %>">
                                    <a class="page-link" href="?filter=<%= currentFilter %><%=searchParam%>&page=<%= currentPage - 1 %>"><i class="bi bi-chevron-left"></i></a>
                                </li>
                                <% for(int i = 1; i <= totalPages; i++) { %>
                                <li class="page-item <%= (currentPage == i) ? "active" : "" %>">
                                    <a class="page-link" href="?filter=<%= currentFilter %><%=searchParam%>&page=<%= i %>"><%= i %></a>
                                </li>
                                <% } %>
                                <li class="page-item <%= (currentPage == totalPages) ? "disabled" : "" %>">
                                    <a class="page-link" href="?filter=<%= currentFilter %><%=searchParam%>&page=<%= currentPage + 1 %>"><i class="bi bi-chevron-right"></i></a>
                                </li>
                            </ul>
                        </nav>
                    </div>
                    <% } %>
                </div>

                <%@ include file="_footer.jspf" %>
            </main>
        </div>
    </body>
</html>
