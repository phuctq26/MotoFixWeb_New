<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,java.text.SimpleDateFormat,java.net.URLEncoder,java.nio.charset.StandardCharsets,com.motofix.model.RepairTicket" %>
<!doctype html>
<html lang="vi">
<head>
  <title>Xe & tiến độ | MotoFix</title>
  <%@ include file="_head.jspf" %>
</head>
<body>
  <% request.setAttribute("activeMenu","vehicles"); %>
  <%@ include file="_header.jspf" %>

  <section class="page-hero">
    <div class="container">
      <h2>Xe &amp; tiến độ sửa chữa</h2>
      <p class="text-light">Theo dõi trực tiếp tình trạng sửa chữa của xe bạn tại cửa hàng.</p>
    </div>
  </section>

  <section class="py-5">
    <div class="container">
      <div class="row g-4">
        <div class="col-lg-3">
          <% request.setAttribute("activeAccount","vehicles"); %>
          <%@ include file="_account_sidebar.jspf" %>
        </div>
        <div class="col-lg-9">
          <div class="table-card">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h5 class="fw-bold mb-0">Tiến độ sửa chữa</h5>
              <span class="text-muted small">Cập nhật bởi cửa hàng</span>
            </div>
            <%
              String repairQ = request.getAttribute("repairSearchQ") != null
                      ? String.valueOf(request.getAttribute("repairSearchQ")) : "";
              String repairSt = request.getAttribute("repairSearchStatus") != null
                      ? String.valueOf(request.getAttribute("repairSearchStatus")) : "";
              String vehiclesBase = request.getContextPath() + "/vehicles";
              String encQ = URLEncoder.encode(repairQ, StandardCharsets.UTF_8);
              String encSt = URLEncoder.encode(repairSt, StandardCharsets.UTF_8);
              String repairQEsc = repairQ.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
            %>
            <form method="get" action="<%= vehiclesBase %>" class="row g-2 align-items-end mb-3">
              <div class="col-md-5 col-lg-5">
                <label class="form-label small text-muted mb-1">Tìm kiếm</label>
                <input type="text" name="q" class="form-control" placeholder="Tìm ngày sửa hoặc biển số..."
                       value="<%= repairQEsc %>">
              </div>
              <div class="col-md-4 col-lg-4">
                <label class="form-label small text-muted mb-1">Trạng thái</label>
                <select name="status" class="form-select">
                  <option value="" <%= repairSt.isEmpty() || "ALL".equalsIgnoreCase(repairSt) ? "selected" : "" %>>— Tất cả trạng thái —</option>
                  <option value="RECEIVED" <%= "RECEIVED".equalsIgnoreCase(repairSt) ? "selected" : "" %>>Đã tiếp nhận</option>
                  <option value="IN_PROGRESS" <%= "IN_PROGRESS".equalsIgnoreCase(repairSt) ? "selected" : "" %>>Đang sửa</option>
                  <option value="TESTING" <%= "TESTING".equalsIgnoreCase(repairSt) ? "selected" : "" %>>Kiểm tra</option>
                  <option value="COMPLETED" <%= "COMPLETED".equalsIgnoreCase(repairSt) ? "selected" : "" %>>Hoàn thành</option>
                  <option value="REJECTED" <%= "REJECTED".equalsIgnoreCase(repairSt) ? "selected" : "" %>>Đã từ chối</option>
                  <option value="CANCELLED" <%= "CANCELLED".equalsIgnoreCase(repairSt) ? "selected" : "" %>>Đã từ chối</option>
                </select>
              </div>
              <div class="col-md-3 col-lg-3">
                <button type="submit" class="btn btn-primary w-100">Tìm kiếm</button>
              </div>
            </form>
            <div class="table-responsive">
              <table class="table align-middle">
                <thead>
                  <tr>
                    <th>Ngày</th>
                    <th>Mã phiếu</th>
                    <th>Biển số</th>
                    <th>Trạng thái</th>
                    <th class="text-end">Ghi chú</th>
                  </tr>
                </thead>
                <tbody>
                  <%
                    List<RepairTicket> tickets = (List<RepairTicket>) request.getAttribute("tickets");
                    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                    if (tickets != null && !tickets.isEmpty()) {
                      for (RepairTicket t : tickets) {
                        String status = t.getStatus();
                        String pill = "status-pill info";
                        String label = status;
                        if ("OPEN".equalsIgnoreCase(status) || "RECEIVED".equalsIgnoreCase(status)) { label = "Đã tiếp nhận"; pill = "status-pill info"; }
                        if ("IN_PROGRESS".equalsIgnoreCase(status)) { label = "Đang sửa"; pill = "status-pill warning"; }
                        if ("TESTING".equalsIgnoreCase(status)) { label = "Kiểm tra"; pill = "status-pill info"; }
                        if ("COMPLETED".equalsIgnoreCase(status)) { label = "Hoàn thành"; pill = "status-pill success"; }
                        if ("REJECTED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) { label = "Đã từ chối"; pill = "status-pill danger"; }
                  %>
                  <tr>
                    <td><%= t.getCreatedAt() != null ? fmt.format(t.getCreatedAt()) : "" %></td>
                    <td><%= t.getTicketCode() %></td>
                    <td><%= t.getPlateNumber() %></td>
                    <td><span class="<%= pill %>"><%= label %></span></td>
                    <td class="text-end"><span class="text-muted small">Theo dõi tại cửa hàng</span></td>
                  </tr>
                  <% } } else { %>
                  <tr>
                    <td colspan="5" class="text-center text-muted">Chưa có phiếu sửa nào.</td>
                  </tr>
                  <% } %>
                </tbody>
              </table>
            </div>
            <%
              Integer rpPage = (Integer) request.getAttribute("repairPagerPage");
              Integer rpTotalPages = (Integer) request.getAttribute("repairPagerTotalPages");
              Integer rpTotalCount = (Integer) request.getAttribute("repairPagerTotalCount");
              if (rpPage != null && rpTotalPages != null && rpTotalCount != null
                      && rpTotalCount > 0 && rpTotalPages > 1) {
                int cur = rpPage;
                boolean hasPrev = cur > 1;
                boolean hasNext = cur < rpTotalPages;
                String prevHref = vehiclesBase + "?q=" + encQ + "&status=" + encSt + "&page=" + (cur - 1);
                String nextHref = vehiclesBase + "?q=" + encQ + "&status=" + encSt + "&page=" + (cur + 1);
            %>
            <nav class="d-flex justify-content-center align-items-center gap-2 mt-3" aria-label="Phân trang">
              <% if (hasPrev) { %>
              <a class="btn btn-outline-secondary btn-sm" href="<%= prevHref %>">&lt;</a>
              <% } else { %>
              <span class="btn btn-outline-secondary btn-sm disabled" aria-disabled="true">&lt;</span>
              <% } %>
              <span class="btn btn-primary btn-sm"><%= cur %></span>
              <% if (hasNext) { %>
              <a class="btn btn-outline-secondary btn-sm" href="<%= nextHref %>">&gt;</a>
              <% } else { %>
              <span class="btn btn-outline-secondary btn-sm disabled" aria-disabled="true">&gt;</span>
              <% } %>
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
