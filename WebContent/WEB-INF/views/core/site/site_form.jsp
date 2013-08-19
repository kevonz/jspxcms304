<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="f" uri="http://www.jspxcms.com/tags/form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>Jspxcms管理平台 - Powered by Jspxcms</title>
<jsp:include page="/WEB-INF/views/commons/head.jsp"></jsp:include>
<script type="text/javascript">
$(function() {
	$("#validForm").validate();
});
</script>
</head>
<body class="r_body">
<div class="r_bar">
  <div class="r_bar_left"><s:message code="site.management"/> - <s:message code="${oprt=='edit' ? 'edit' : 'create'}"/></div>
  <div class="r_bar_right">
  <form method="get" style="display:inline;">
    <input type="submit" value="<s:message code="create"/>" onclick="this.form.action='create.do'"/>
  </form>
  </div>
  <div style="clear:both;"></div>
</div>
<form id="validForm" action="${oprt=='edit' ? 'update' : 'save'}.do" method="post">
<table border="0" cellpadding="0" cellspacing="0" class="in-tb margin-top5">
  <tr>
    <td class="in-lab" width="15%"><em class="required">*</em><s:message code="site.name"/>:</td>
    <td class="in-ctt" colspan="3"><f:text name="name" value="${bean.name}" class="required" style="width:180px;"/></td>
  </tr>
  <tr>
    <td colspan="4" align="center">
      <f:hidden name="oid" value="${bean.id}"/>
      <input type="submit" value="<s:message code="submit"/>"/> &nbsp;
      <input type="reset" value="<s:message code="reset"/>"/>
    </td>
  </tr>
</table>
</form>
</body>
</html>