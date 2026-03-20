<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<!doctype html>
<html lang="vi">
<head>
  <title>Khách hàng | MotoFix Admin</title>
  <%@ include file="_head.jspf" %>
</head>
<body>
  <div class="layout">
    <c:set scope="request" var="activeMenu" value="customers"/>
    <c:set scope="request" var="pageTitle"  value="Danh sách khách hàng"/>
    <%@ include file="_sidebar.jspf" %>
    <main class="content">
      <%@ include file="_topbar.jspf" %>

     
      <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
          <i class="bi bi-check-circle me-2"></i>${sessionScope.message}
          <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="message" scope="session"/>
      </c:if>

  
      <c:set var="formError"   value="${sessionScope.formError}"/>
      <c:set var="fUsername"   value="${sessionScope.formUsername}"/>
      <c:set var="fFirstName"  value="${sessionScope.formFirstName}"/>
      <c:set var="fLastName"   value="${sessionScope.formLastName}"/>
      <c:set var="fEmail"      value="${sessionScope.formEmail}"/>
      <c:set var="fAddress"    value="${sessionScope.formAddress}"/>
      <c:set var="openModal"   value="${not empty formError}"/>
      <c:remove var="formError"   scope="session"/>
      <c:remove var="formUsername"  scope="session"/>
      <c:remove var="formFirstName" scope="session"/>
      <c:remove var="formLastName"  scope="session"/>
      <c:remove var="formEmail"     scope="session"/>
      <c:remove var="formAddress"   scope="session"/>

      <div class="card p-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <form method="get" action="${pageContext.request.contextPath}/admin/customers" class="d-flex gap-2 align-items-center flex-wrap">
            <input type="hidden" name="filter" value="<%= request.getAttribute("currentFilter") != null ? request.getAttribute("currentFilter") : "all" %>" />
            <div class="input-group" style="max-width:320px;">
              <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
              <input class="form-control" name="search" placeholder="Tìm theo tên, SĐT..." value="<c:out value="${currentSearch}"/>" />
              <button type="submit" class="btn btn-outline-secondary">Lọc</button>
            </div>
            
            <% String curSearch = request.getAttribute("currentSearch") != null ? (String) request.getAttribute("currentSearch") : ""; 
               String searchParam = !curSearch.isEmpty() ? "&search=" + java.net.URLEncoder.encode(curSearch, "UTF-8") : ""; 
               String currentFilter = (String) request.getAttribute("currentFilter"); %>
               
            <div class="btn-group">
              <a href="?filter=all<%=searchParam%>" class="btn btn-outline-secondary btn-sm <%= "all".equals(currentFilter) ? "active" : "" %>">Tất cả</a>
              <a href="?filter=active<%=searchParam%>" class="btn btn-outline-success btn-sm <%= "active".equals(currentFilter) ? "active" : "" %>">Đang hoạt động</a>
              <a href="?filter=inactive<%=searchParam%>" class="btn btn-outline-secondary btn-sm <%= "inactive".equals(currentFilter) ? "active" : "" %>">Vô hiệu</a>
            </div>
          </form>
          <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addCustomerModal">
            <i class="bi bi-plus-lg"></i> Thêm khách hàng
          </button>
        </div>

      
        <div class="modal fade" id="addCustomerModal" tabindex="-1">
          <div class="modal-dialog">
            <div class="modal-content">
              <form method="post" action="${pageContext.request.contextPath}/admin/customers">
                <div class="modal-header">
                  <h5 class="modal-title"><i class="bi bi-person-plus me-2 text-primary"></i>Thêm khách hàng mới</h5>
                  <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                  <c:if test="${openModal and not empty formError}">
                    <div class="alert alert-danger py-2 d-flex align-items-center gap-2 mb-3" role="alert">
                      <i class="bi bi-exclamation-triangle-fill flex-shrink-0"></i>
                      <span>${formError}</span>
                    </div>
                  </c:if>
                  <div class="row g-3">
                    <div class="col-md-6">
                      <label class="form-label fw-semibold">Họ <span class="text-danger">*</span></label>
                      <input class="form-control" name="lastName" placeholder="Nguyễn"
                             value="${fn:escapeXml(fLastName)}" required>
                    </div>
                    <div class="col-md-6">
                      <label class="form-label fw-semibold">Tên <span class="text-danger">*</span></label>
                      <input class="form-control" name="firstName" placeholder="Văn A"
                             value="${fn:escapeXml(fFirstName)}" required>
                    </div>
                    <div class="col-12">
                      <label class="form-label fw-semibold">Tên đăng nhập (SĐT) <span class="text-danger">*</span></label>
                      <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-phone"></i></span>
                        <input class="form-control ${openModal and not empty fUsername ? 'is-invalid' : ''}"
                               name="username" placeholder="0901234567"
                               value="${fn:escapeXml(fUsername)}" required>
                        <c:if test="${openModal and not empty fUsername}">
                          <div class="invalid-feedback">Tên đăng nhập này đã tồn tại.</div>
                        </c:if>
                      </div>
                    </div>
                    <div class="col-12">
                      <label class="form-label fw-semibold">Email</label>
                      <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                        <input type="email" class="form-control" name="email"
                               placeholder="example@email.com" value="${fn:escapeXml(fEmail)}">
                      </div>
                    </div>
                    <div class="col-12">
                      <label class="form-label fw-semibold">Địa chỉ</label>
                      <input class="form-control" name="address"
                             placeholder="Số nhà, đường, quận..." value="${fn:escapeXml(fAddress)}">
                    </div>
                    <div class="col-12">
                      <label class="form-label fw-semibold">Mật khẩu</label>
                      <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-lock"></i></span>
                        <input type="password" class="form-control" name="password" placeholder="Mặc định: 123">
                      </div>
                      <div class="form-text">Để trống sẽ dùng mật khẩu mặc định: <code>123</code></div>
                    </div>
                  </div>
                </div>
                <div class="modal-footer">
                  <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                  <button type="submit" class="btn btn-primary"><i class="bi bi-check-lg me-1"></i>Thêm mới</button>
                </div>
              </form>
            </div>
          </div>
        </div>

        
        <div class="table-responsive">
          <table class="table align-middle" id="customersTable">
            <thead class="table-light">
              <tr>
                <th>#</th>
                <th>Khách hàng</th>
                <th>Tên đăng nhập</th>
                <th>Email</th>
                <th>Địa chỉ</th>
                <th class="text-center">Trạng thái</th>
                <th class="text-end">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${not empty customers}">
                  <c:forEach var="c" items="${customers}" varStatus="st">
                    <tr class="customer-row ${c.active ? 'row-active' : 'row-inactive'}"
                        data-status="${c.active ? 'active' : 'inactive'}">
                      <td class="text-muted small">${st.count}</td>
                      <td>
                        <div class="d-flex align-items-center gap-2">
                          <div class="avatar-circle ${c.active ? 'bg-primary' : 'bg-secondary'} text-white">
                            ${fn:substring(c.fullName, 0, 1)}
                          </div>
                          <div>
                            <div class="fw-semibold ${c.active ? '' : 'text-muted'}">${fn:escapeXml(c.fullName)}</div>
                            <small class="text-muted">ID: #${c.customerId}</small>
                          </div>
                        </div>
                      </td>
                      <td><i class="bi bi-phone text-muted me-1"></i>
                        <c:out value="${empty c.username ? '—' : c.username}"/>
                      </td>
                      <td>
                        <c:choose>
                          <c:when test="${not empty c.email}">
                            <a href="mailto:${fn:escapeXml(c.email)}" class="text-decoration-none">${fn:escapeXml(c.email)}</a>
                          </c:when>
                          <c:otherwise><span class="text-muted">—</span></c:otherwise>
                        </c:choose>
                      </td>
                      <td class="text-muted small"><c:out value="${empty c.address ? '—' : c.address}"/></td>
                      <td class="text-center">
                        <c:choose>
                          <c:when test="${c.active}">
                            <span class="badge bg-success-subtle text-success border border-success-subtle">
                              <i class="bi bi-circle-fill me-1" style="font-size:.4rem;vertical-align:middle;"></i>Hoạt động
                            </span>
                          </c:when>
                          <c:otherwise>
                            <span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle">
                              <i class="bi bi-slash-circle me-1"></i>Vô hiệu
                            </span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td class="text-end">
                        <button class="btn btn-sm btn-outline-primary" title="Sửa thông tin"
                                data-bs-toggle="modal" data-bs-target="#editModal${c.customerId}">
                          <i class="bi bi-pencil"></i>
                        </button>
                        <c:choose>
                          <c:when test="${c.active}">
                            <button class="btn btn-sm btn-outline-warning" title="Vô hiệu hoá tài khoản"
                                    data-bs-toggle="modal" data-bs-target="#deactivateModal${c.customerId}">
                              <i class="bi bi-slash-circle"></i>
                            </button>
                          </c:when>
                          <c:otherwise>
                            <button class="btn btn-sm btn-outline-success" title="Khôi phục tài khoản"
                                    data-bs-toggle="modal" data-bs-target="#restoreModal${c.customerId}">
                              <i class="bi bi-arrow-counterclockwise"></i>
                            </button>
                          </c:otherwise>
                        </c:choose>
                      </td>
                    </tr>

                  
                    <div class="modal fade" id="editModal${c.customerId}" tabindex="-1">
                      <div class="modal-dialog">
                        <div class="modal-content">
                          <form method="post" action="${pageContext.request.contextPath}/admin/customers">
                            <input type="hidden" name="action" value="edit">
                            <input type="hidden" name="id"     value="${c.customerId}">
                            <div class="modal-header">
                              <h5 class="modal-title"><i class="bi bi-pencil me-2 text-primary"></i>Sửa khách hàng</h5>
                              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                              <div class="row g-3">
                                <div class="col-md-6">
                                  <label class="form-label fw-semibold">Họ <span class="text-danger">*</span></label>
                                  <input class="form-control" name="lastName" placeholder="Nguyễn"
                                         value="${fn:escapeXml(c.lastName)}" required>
                                </div>
                                <div class="col-md-6">
                                  <label class="form-label fw-semibold">Tên <span class="text-danger">*</span></label>
                                  <input class="form-control" name="firstName" placeholder="Văn A"
                                         value="${fn:escapeXml(c.firstName)}" required>
                                </div>
                                <div class="col-12">
                                  <label class="form-label fw-semibold">Tên đăng nhập (SĐT)</label>
                                  <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-phone"></i></span>
                                    <input class="form-control bg-light" name="username"
                                           value="${fn:escapeXml(c.username)}" readonly>
                                  </div>
                                  <div class="form-text">Tên đăng nhập không thể thay đổi.</div>
                                </div>
                                <div class="col-12">
                                  <label class="form-label fw-semibold">Email</label>
                                  <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                                    <input type="email" class="form-control" name="email"
                                           value="${fn:escapeXml(c.email)}">
                                  </div>
                                </div>
                                <div class="col-12">
                                  <label class="form-label fw-semibold">Địa chỉ</label>
                                  <input class="form-control" name="address" value="${fn:escapeXml(c.address)}">
                                </div>
                              </div>
                            </div>
                            <div class="modal-footer">
                              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                              <button type="submit" class="btn btn-primary"><i class="bi bi-check-lg me-1"></i>Lưu thay đổi</button>
                            </div>
                          </form>
                        </div>
                      </div>
                    </div>

                    
                    <c:choose>
                      <c:when test="${c.active}">
                        <div class="modal fade" id="deactivateModal${c.customerId}" tabindex="-1">
                          <div class="modal-dialog modal-sm">
                            <div class="modal-content">
                              <form method="post" action="${pageContext.request.contextPath}/admin/customers">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id"     value="${c.customerId}">
                                <div class="modal-header border-0 pb-0">
                                  <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <div class="modal-body text-center pt-0">
                                  <div class="text-warning fs-1"><i class="bi bi-slash-circle-fill"></i></div>
                                  <h6 class="mb-1 mt-2">Vô hiệu hoá tài khoản?</h6>
                                  <p class="text-muted small mb-0">
                                    Tài khoản <strong>${fn:escapeXml(c.fullName)}</strong>
                                    (<code>${fn:escapeXml(c.username)}</code>) sẽ không thể đăng nhập.<br>
                                    Bạn có thể <strong>khôi phục</strong> lại bất cứ lúc nào.
                                  </p>
                                </div>
                                <div class="modal-footer border-0 justify-content-center gap-2">
                                  <button type="button" class="btn btn-secondary btn-sm px-4" data-bs-dismiss="modal">Hủy</button>
                                  <button type="submit" class="btn btn-warning btn-sm px-4 text-white">
                                    <i class="bi bi-slash-circle me-1"></i>Vô hiệu hoá
                                  </button>
                                </div>
                              </form>
                            </div>
                          </div>
                        </div>
                      </c:when>
                      <c:otherwise>
                        <div class="modal fade" id="restoreModal${c.customerId}" tabindex="-1">
                          <div class="modal-dialog modal-sm">
                            <div class="modal-content">
                              <form method="post" action="${pageContext.request.contextPath}/admin/customers">
                                <input type="hidden" name="action" value="restore">
                                <input type="hidden" name="id"     value="${c.customerId}">
                                <div class="modal-header border-0 pb-0">
                                  <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <div class="modal-body text-center pt-0">
                                  <div class="text-success fs-1"><i class="bi bi-arrow-counterclockwise"></i></div>
                                  <h6 class="mb-1 mt-2">Khôi phục tài khoản?</h6>
                                  <p class="text-muted small mb-0">
                                    Tài khoản <strong>${fn:escapeXml(c.fullName)}</strong>
                                    (<code>${fn:escapeXml(c.username)}</code>) sẽ có thể đăng nhập trở lại.
                                  </p>
                                </div>
                                <div class="modal-footer border-0 justify-content-center gap-2">
                                  <button type="button" class="btn btn-secondary btn-sm px-4" data-bs-dismiss="modal">Hủy</button>
                                  <button type="submit" class="btn btn-success btn-sm px-4">
                                    <i class="bi bi-arrow-counterclockwise me-1"></i>Khôi phục
                                  </button>
                                </div>
                              </form>
                            </div>
                          </div>
                        </div>
                      </c:otherwise>
                    </c:choose>
                  </c:forEach>
                </c:when>
                <c:otherwise>
                  <tr>
                    <td colspan="7" class="text-center text-muted py-5">
                      <i class="bi bi-person-x fs-1 d-block mb-2 opacity-25"></i>
                      Chưa có khách hàng nào trong hệ thống.
                    </td>
                  </tr>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        
        <div class="d-flex justify-content-between align-items-center mt-3">
          <div class="text-muted small">
            Hiển thị <strong>${fn:length(customers)}</strong> / tổng số khách hàng
          </div>
          <c:if test="${totalPages > 1}">
            <nav>
              <ul class="pagination pagination-sm mb-0">
                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                  <a class="page-link" href="?filter=<%= currentFilter %><%=searchParam%>&page=${currentPage - 1}"><i class="bi bi-chevron-left"></i></a>
                </li>
                <c:forEach begin="1" end="${totalPages}" var="i">
                  <li class="page-item ${currentPage == i ? 'active' : ''}">
                    <a class="page-link" href="?filter=<%= currentFilter %><%=searchParam%>&page=${i}">${i}</a>
                  </li>
                </c:forEach>
                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                  <a class="page-link" href="?filter=<%= currentFilter %><%=searchParam%>&page=${currentPage + 1}"><i class="bi bi-chevron-right"></i></a>
                </li>
              </ul>
            </nav>
          </c:if>
        </div>
      </div>

      <%@ include file="_footer.jspf" %>
    </main>
  </div>

  <style>
    .avatar-circle {
      width: 36px; height: 36px; border-radius: 50%;
      display: flex; align-items: center; justify-content: center;
      font-weight: 700; font-size: .9rem; flex-shrink: 0;
    }
    .row-inactive td { opacity: 0.55; }
    .row-inactive td:last-child { opacity: 1; }
  </style>
  <script>
    <c:if test="${openModal}">
    document.addEventListener('DOMContentLoaded', function () {
      new bootstrap.Modal(document.getElementById('addCustomerModal')).show();
    });
    </c:if>
  </script>
</body>
</html>
