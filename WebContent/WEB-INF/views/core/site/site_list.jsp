<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="f" uri="http://www.jspxcms.com/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Jspxcms管理平台 - Powered by Jspxcms</title>
<jsp:include page="/WEB-INF/views/commons/head.jsp"></jsp:include>
<script type="text/javascript">
$(function() {
	$("#pagedTable").tableHighlight();
});
function confirmDelete() {
	return confirm("<s:message code='confirmDelete'/>");
}
function batchDelete(form) {
	if(Cms.checkeds("ids")==0) {
		alert("<s:message code='pleaseSelectRecord'/>");
		return false;
	}
	if(!confirmDelete()) {
		return false;
	}
	form.action='delete.do';
	form.submit();
	return true;
}
</script>
</head>
<body class="r_body">
<div class="r_bar">
  <div class="r_bar_left"><s:message code="site.management"/> - <s:message code="list"/></div>
  <div class="r_bar_right">
  <form method="get" style="display:inline;">
    <input type="submit" value="<s:message code="create"/>" onclick="this.form.action='create.do'"/>
  </form>
  </div>
  <div style="clear:both;"></div>
</div>
<form method="post">
<table id="pagedTable" border="0" cellpadding="0" cellspacing="0" class="ls-tb margin-top5">
  <thead>
  <tr class="r_table_th">
    <th width="25">#</th>
    <th width="30">ID</th>
    <th><s:message code="site.name"/></th>
    <th width="80"><s:message code="operate"/></th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="bean" items="${pagedList.items}">
  <tr>
    <td><input type="checkbox" name="ids" value="${bean.id}"/></td>
    <td>${bean.id}</td>
    <td><c:out value="${bean.name}"/></td>
    <td align="center">
      <a href="edit.do?id=${bean.id}&page=${pagedList.page}" class="r_opt"><s:message code="edit"/></a>
      <a href="delete.do?ids=${bean.id}&page=${pagedList.page}" onclick="return confirmDelete();" class="r_opt"><s:message code="delete"/></a>
     </td>
  </tr>
  </c:forEach>
  </tbody>
</table>
<c:choose>
<c:when test="${fn:length(pagedList.items) gt 0}">
<div class="r_bc_opt">
  <input type="checkbox" onclick="Cms.check('ids',this.checked);"/>
  <input type="button" value="<s:message code="delete"/>" onclick="return batchDelete(this.form);"/>
</div>
</c:when>
<c:otherwise>  
<div class="r_norecord"><s:message code="recordNotFound"/></div>
</c:otherwise>
</c:choose>
</form>
<form action="list.do" method="get" class="r_page">
  <tags:pagination pagedList="${pagedList}"/>
</form>
</body>
</html>