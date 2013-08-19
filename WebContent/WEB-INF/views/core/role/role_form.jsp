<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fnx" uri="http://java.sun.com/jsp/jstl/functionsx"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="f" uri="http://www.jspxcms.com/tags/form"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>Jspxcms管理平台 - Powered by Jspxcms</title>
<jsp:include page="/WEB-INF/views/commons/head.jsp"></jsp:include>
<style type="text/css">
html{height:100%;}
.ztree li span.button.switch.level0 {visibility:hidden; width:1px;}
.ztree li ul.level0 {padding:0; background:none;}
</style>
<script type="text/javascript">
$(function() {
	$("#validForm").validate();
	$("input[name='name']").focus();
});
function confirmDelete() {
	return confirm("<s:message code='confirmDelete'/>");
}
</script>
</head>
<body class="c-body">
<jsp:include page="/WEB-INF/views/commons/show_message.jsp"/>
<div class="c-bar margin-top5">
  <span class="c-position"><s:message code="role.management"/> - <s:message code="${oprt=='edit'?'edit':'create'}"/></span>
</div>
<form id="validForm" action="${oprt=='edit'?'update':'save'}.do" method="post">
<tags:search_params/>
<f:hidden name="oid" value="${bean.id}"/>
<f:hidden name="position" value="${position}"/>
<input type="hidden" id="redirect" name="redirect" value="edit"/>
<table border="0" cellpadding="0" cellspacing="0" class="in-tb margin-top5">
  <tr>
    <td colspan="4" class="in-opt">
			<shiro:hasPermission name="core:role:create">
			<div class="in-btn"><input type="button" value="<s:message code="create"/>" onclick="location.href='create.do?${searchstring}';"<c:if test="${oprt=='create'}"> disabled="disabled"</c:if>/></div>
			<div class="in-btn"></div>
			</shiro:hasPermission>
			<shiro:hasPermission name="core:role:copy">
			<div class="in-btn"><input type="button" value="<s:message code="copy"/>" onclick="location.href='create.do?id=${bean.id}&${searchstring}';"<c:if test="${oprt=='create'}"> disabled="disabled"</c:if>/></div>
			</shiro:hasPermission>
			<shiro:hasPermission name="core:role:delete">
			<div class="in-btn"><input type="button" value="<s:message code="delete"/>" onclick="if(confirmDelete()){location.href='delete.do?ids=${bean.id}&${searchstring}';}"<c:if test="${oprt=='create'}"> disabled="disabled"</c:if>/></div>
			</shiro:hasPermission>
			<div class="in-btn"></div>
			<div class="in-btn"><input type="button" value="<s:message code="prev"/>" onclick="location.href='edit.do?id=${side.prev.id}&position=${position-1}&${searchstring}';"<c:if test="${empty side.prev}"> disabled="disabled"</c:if>/></div>
			<div class="in-btn"><input type="button" value="<s:message code="next"/>" onclick="location.href='edit.do?id=${side.next.id}&position=${position+1}&${searchstring}';"<c:if test="${empty side.next}"> disabled="disabled"</c:if>/></div>
			<div class="in-btn"></div>
			<div class="in-btn"><input type="button" value="<s:message code="return"/>" onclick="location.href='list.do?${searchstring}';"/></div>
      <div style="clear:both;"></div>
    </td>
  </tr>
  <tr>
    <td class="in-lab" width="15%"><em class="required">*</em><s:message code="role.name"/>:</td>
    <td class="in-ctt" width="35%"><f:text name="name" value="${oprt=='edit'?bean.name:''}" class="required" maxlength="255" style="width:180px;"/></td>
    <td class="in-lab" width="15%"><s:message code="role.description"/>:</td>
    <td class="in-ctt" width="35%"><f:text name="description" value="${bean.description}" maxlength="255" style="width:180px;"/></td>
  </tr>
  <tr>
    <td class="in-lab" width="15%"><s:message code="role.perms"/>:</td>
    <td class="in-ctt" width="85%" colspan="3">
    	<span id="permsContainer">
		  	<f:text id="permsNumber" name="perms" value="${perms}" style="width:300px;"/><input id="permsButton" type="button" value="F7"/>
			</span>
			&nbsp;
			<label for="allPerm"><f:checkbox id="allPerm" name="allPerm" value="${allPerm}" default="true" onclick="$('#permsContainer input').prop('disabled',this.checked);"/><s:message code="role.allRights"/></label>
			<script type="text/javascript">
			$(function(){
	    	Cms.f7.perm("${ctx}","perms",{
	    		settings: {"title": "<s:message code='role.perms.select'/>"}
	    	});
	    	<c:if test="${empty allPerm || allPerm}">$('#permsContainer input').prop('disabled',true);</c:if>
	    });
			</script>
    </td>
  </tr>
  <tr>
    <td class="in-lab" width="15%"><s:message code="role.nodeRights"/>:</td>
    <td class="in-ctt" width="85%" colspan="3">
    	<label for="allNode"><f:checkbox id="allNode" name="allNode" value="${allNode}" default="true"/><s:message code="role.allRights"/></label>
    </td>
  </tr>
  <tr>
    <td class="in-lab" width="15%"><s:message code="role.infoRights"/>:</td>
    <td class="in-ctt" width="85%" colspan="3">
    	<label for="allInfo"><f:checkbox id="allInfo" name="allInfo" value="${allInfo}" default="true"/><s:message code="role.allRights"/></label>
			&nbsp;
			<select name="infoRightType">
				<f:option value="1" selected="${infoRightType}" default="1"><s:message code="role.infoRightType.1"/></f:option>
			</select>
    </td>
  </tr>
  <tr>
    <td colspan="4" class="in-opt">
      <div class="in-btn"><input type="submit" value="<s:message code="save"/>"/></div>
      <div class="in-btn"><input type="submit" value="<s:message code="saveAndReturn"/>" onclick="$('#redirect').val('list');"/></div>
      <c:if test="${oprt=='create'}">
      <div class="in-btn"><input type="submit" value="<s:message code="saveAndCreate"/>" onclick="$('#redirect').val('create');"/></div>
      </c:if>
      <div style="clear:both;"></div>
    </td>
  </tr>
</table>
</form>
</body>
</html>