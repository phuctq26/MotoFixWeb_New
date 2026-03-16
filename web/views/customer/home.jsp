<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html lang="vi">
<head>
  <title>Trang chủ | MotoFix</title>
  <%@ include file="_head.jspf" %>
  <style>
    .hero { background:url("https://images.unsplash.com/photo-1489515217757-5fd1be406fef?q=80&w=1400&auto=format&fit=crop") center/cover; }
    .stats-badge { position:absolute; left:20px; bottom:-20px; background:var(--brand); color:#fff; border-radius:14px; padding:10px 16px; box-shadow:0 12px 20px rgba(30,120,255,.25); font-weight:700; }
  </style>
</head>
<body>
  <% request.setAttribute("activeMenu","home"); %>
  <%@ include file="_header.jspf" %>

  <section class="hero">
    <div class="container hero-content">
      <div class="col-lg-7">
        <h1>Chăm sóc xe yêu của bạn <span class="text-primary">Chuyên nghiệp &amp; tận tâm</span></h1>
        <p class="text-light mt-3">Hệ thống sửa chữa xe máy hiện đại với đội ngũ kỹ thuật viên tay nghề cao. Đặt lịch ngay hôm nay để trải nghiệm dịch vụ tốt nhất.</p>
        <div class="d-flex gap-3 mt-4">
          <a class="btn btn-primary" href="${pageContext.request.contextPath}/booking">Đặt lịch ngay</a>
          <a class="btn btn-outline-light" href="#services">Xem dịch vụ</a>
        </div>
      </div>
    </div>
  </section>

  <section id="services" class="py-5">
    <div class="container">
      <div class="text-center mb-4">
        <h3 class="section-title">Dịch vụ phổ biến</h3>
        <p class="text-muted">Các gói dịch vụ được khách hàng tin dùng nhất tại MotoFix</p>
      </div>
      <div class="row g-4">
        <c:forEach var="s" items="${topServices}" varStatus="loop">
          <div class="col-md-3">
            <div class="service-card">
              <div class="icon">
                <c:choose>
                  <c:when test="${loop.index == 0}"><i class="bi bi-droplet"></i></c:when>
                  <c:when test="${loop.index == 1}"><i class="bi bi-circle"></i></c:when>
                  <c:when test="${loop.index == 2}"><i class="bi bi-gear"></i></c:when>
                  <c:otherwise><i class="bi bi-battery-charging"></i></c:otherwise>
                </c:choose>
              </div>
              <h6 class="fw-bold"><c:out value="${s.serviceName}"/></h6>
              <p class="text-muted small"><c:out value="${s.description}"/></p>
              <div class="badge-price">
                <fmt:formatNumber value="${s.price}" type="number"/>đ
              </div>
            </div>
          </div>
        </c:forEach>
      </div>
    </div>
  </section>

  <section class="py-5">
    <div class="container">
      <div class="row g-4 align-items-center">
        <div class="col-lg-6">
          <h3 class="section-title">Tại sao chọn MotoFix?</h3>
          <ul class="list-unstyled mt-3">
            <li class="mb-2"><i class="bi bi-check-circle text-success"></i> Phụ tùng chính hãng 100%</li>
            <li class="mb-2"><i class="bi bi-check-circle text-success"></i> Minh bạch về giá</li>
            <li class="mb-2"><i class="bi bi-check-circle text-success"></i> Đặt lịch dễ dàng</li>
          </ul>
        </div>
        <div class="col-lg-6">
          <div class="position-relative">
            <img class="img-fluid rounded-4" src="https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?q=80&w=1200&auto=format&fit=crop" alt="MotoFix" />
            <div class="stats-badge">10+ năm kinh nghiệm</div>
          </div>
        </div>
      </div>
    </div>
  </section>

  <%@ include file="_footer.jspf" %>
</body>
</html>
