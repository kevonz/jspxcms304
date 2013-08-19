<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fnx" uri="http://java.sun.com/jsp/jstl/functionsx"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
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
* html{overflow-y: scroll;}
.tabs{}
.tabs li{float:left;background-color:#F1F1F1;border-left:1px solid #e2e2e2;border-top:1px solid #e2e2e2;border-right:1px solid #e2e2e2;margin-right:5px;}
.tabs li a{color:#555555;float:left;text-decoration:none;padding:5px 12px;}
.tabs li a:link,.tabs li a:visited,.tabs li a:hover,.tabs li a:active{text-decoration:none;}
.tabs li.active{background-color:#FFFFFF;border-left:1px solid #C5C5C5;border-top:1px solid #C5C5C5;border-right:1px solid #C5C5C5;}
.tabs li.active a{color:#000;}
.tabs li.hover{background-color:#e5e5e5;border-left:1px solid #C5C5C5;border-top:1px solid #C5C5C5;border-right:1px solid #C5C5C5;}
.tabs li.hover a{color:#000;}
</style>
<script type="text/javascript">
$(function() {
	$("#radio").buttonset();
	$("#pagedTable").tableHighlight();
	$("#sortHead").headSort();

	$("#tabs li").each(function(){
		$(this).hover(function(){
			if(!$(this).hasClass("active")) {
				$(this).addClass("hover");
			}
		},function(){
			$(this).removeClass("hover");
		});
	});

});
function confirmDelete() {
	return confirm("<s:message code='confirmDelete'/>");
}
function optSingle(opt) {
	if(Cms.checkeds("ids")==0) {
		alert("<s:message code='pleaseSelectRecord'/>");
		return false;
	}
	if(Cms.checkeds("ids")>1) {
		alert("<s:message code='pleaseSelectOne'/>");
		return false;
	}
	var id = $("input[name='ids']:checkbox:checked").val();
	location.href=$(opt+id).attr("href");
}
function optMulti(form, action, confirmMsg) {
	if(Cms.checkeds("ids")==0) {
		alert("<s:message code='pleaseSelectRecord'/>");
		return false;
	}
	if(confirmMsg) {
		if($.isFunction(confirmMsg)) {
			if(!confirmMsg()) {
				return false;
			}
		} else {
			if(!confirm(confirmMsg)) {
				return false;
			}
		}
	}
	form.action=action;
	form.submit();
	return true;
}
function optDelete(form) {
	optMulti(form,"delete.do",confirmDelete);
}
</script>
</head>
<body class="c-body">
<jsp:include page="/WEB-INF/views/commons/show_message.jsp"/>
<div class="c-bar margin-top5">
  <span class="c-position"><s:message code="info.management"/> - <s:message code="list"/></span>
	<span class="c-total">(<s:message code="totalElements" arguments="${pagedList.totalElements}"/>)</span>
</div>
<form id="searchForm" action="list.do" method="get">
	<fieldset class="c-fieldset">
    <legend><s:message code="search"/></legend>
	  <label class="c-lab"><s:message code="info.title"/>: <input type="text" name="search_CONTAIN_detail.title" value="${requestScope['search_CONTAIN_detail.title'][0]}" style="width:150px;"/></label>
	  <label class="c-lab"><s:message code="info.tagKeywords"/>: <input type="text" name="search_CONTAIN_Jtags.name" value="${requestScope['search_CONTAIN_Jtags.name'][0]}" style="width:100px;"/></label>
	  <label class="c-lab"><s:message code="startTime"/>: <f:text name="search_GTE_publishDate_Date" value="${search_GTE_publishDate_Date[0]}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" style="width:80px;"/></label>
	  <label class="c-lab"><s:message code="endTime"/>: <f:text name="search_LTE_publishDate_Date" value="${search_LTE_publishDate_Date[0]}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" style="width:80px;"/></label>
	  <label class="c-lab">
	  	<select name="search_EQ_priority">
	  		<option value="">---<s:message code="info.priority"/>---</option>
	  		<c:forEach var="i" begin="0" end="9">
	  		<c:set var="istr">${i}</c:set>
	  		<option<c:if test="${istr eq search_EQ_priority[0]}"> selected="selected"</c:if>>${i}</option>
	  		</c:forEach>
  		</select>
	  </label>
	  <label class="c-lab">
	  	<select name="search_EQ_JinfoAttrs.Jattribute.id">
	  		<option value="">---<s:message code="info.attributes"/>---</option>
	  		<c:forEach var="attr" items="${attributeList}">
	  		<c:set var="idstr">${attr.id}</c:set>
	  		<option value="${attr.id}"<c:if test="${idstr eq requestScope['search_EQ_JinfoAttrs.Jattribute.id'][0]}"> selected="selected"</c:if>>${attr.name}</option>
	  		</c:forEach>
  		</select>
	  </label>
	  <label class="c-lab">
	  	<select name="queryInfoRightType">
	  		<option value="">---<s:message code="role.infoRightType"/>---</option>
	  		<%-- <option value="2"<c:if test="${queryInfoRightType eq '2'}"> selected="selected"</c:if>><s:message code="role.infoRightType.2"/></option> --%>
	  		<option value="3"<c:if test="${queryInfoRightType eq '3'}"> selected="selected"</c:if>><s:message code="role.infoRightType.3"/></option>
  		</select>
	  </label>
	  <label class="c-lab"><input type="submit" value="<s:message code="search"/>"/></label>
		<f:hidden name="queryNodeId" value="${queryNodeId}"/>
		<f:hidden name="queryNodeType" value="${queryNodeType}"/>
		<f:hidden id="queryStatus" name="queryStatus" value="${queryStatus}"/>
  </fieldset>
</form>
<form method="post">
<tags:search_params/>
<f:hidden name="queryNodeId" value="${queryNodeId}"/>
<f:hidden name="queryNodeType" value="${queryNodeType}"/>
<f:hidden name="queryInfoRightType" value="${queryInfoRightType}"/>
<f:hidden name="queryStatus" value="${queryStatus}"/>
<div class="ls-bc-opt">
	<shiro:hasPermission name="core:info:create">
	<div class="ls-btn"><input type="button" value="<s:message code="create"/>" onclick="location.href='create.do?queryNodeId=${queryNodeId}&queryNodeType=${queryNodeType}&queryInfoRightType=${queryInfoRightType}&queryStatus=${queryStatus}&${searchstring}';"/></div>
	<div class="ls-btn"></div>
	</shiro:hasPermission>
	<shiro:hasPermission name="core:info:copy">
	<div class="ls-btn"><input type="button" value="<s:message code="copy"/>" onclick="return optSingle('#copy_opt_');"/></div>
	</shiro:hasPermission>
	<shiro:hasPermission name="core:info:edit">
	<div class="ls-btn"><input type="button" value="<s:message code="edit"/>" onclick="return optSingle('#edit_opt_');"/></div>
	</shiro:hasPermission>
	<shiro:hasPermission name="core:info:delete">
	<div class="ls-btn"><input type="button" value="<s:message code="delete"/>" onclick="return optDelete(this.form);"/></div>
	</shiro:hasPermission>
	<div class="ls-btn"></div>
	<shiro:hasPermission name="core:info:audit_pass">
	<div class="ls-btn"><input type="button" value="<s:message code="info.auditPass"/>" onclick="return optMulti(this.form,'audit_pass.do');"/></div>
	</shiro:hasPermission>
	<shiro:hasPermission name="core:info:audit_reject">
	<div class="ls-btn"><input type="button" value="<s:message code="info.auditReject"/>" onclick="return optMulti(this.form,'audit_reject.do');"/></div>
	</shiro:hasPermission>
	<shiro:hasPermission name="core:info:submit">
	<div class="ls-btn"><input type="button" value="<s:message code="submit"/>" onclick="return optMulti(this.form,'submit.do');"/></div>
	</shiro:hasPermission>
	<shiro:hasPermission name="core:info:anti_submit">
	<div class="ls-btn"><input type="button" value="<s:message code="antiSubmit"/>" onclick="return optMulti(this.form,'anti_submit.do');"/></div>
	</shiro:hasPermission>
	<div style="clear:both"></div>
</div>
<ul id="tabs" class="tabs margin-top5">
<shiro:hasPermission name="core:info:status">
	<li<c:if test="${empty queryStatus}"> class="active"</c:if>><a href="javascript:void(0);" onclick="$('#queryStatus').val('');$('#searchForm').submit();"><s:message code="info.status.all"/></a></li>
	<li<c:if test="${queryStatus eq 'pending'}"> class="active"</c:if>><a href="javascript:void(0);" onclick="$('#queryStatus').val('pending');$('#searchForm').submit();"><s:message code="info.status.pending"/></a></li>
	<li<c:if test="${queryStatus eq 'notpassed'}"> class="active"</c:if>><a href="javascript:void(0);" onclick="$('#queryStatus').val('notpassed');$('#searchForm').submit();"><s:message code="info.status.notpassed"/></a></li>
	<li<c:if test="${queryStatus eq 'auditing'}"> class="active"</c:if>><a href="javascript:void(0);" onclick="$('#queryStatus').val('auditing');$('#searchForm').submit();"><s:message code="info.status.auditing"/></a></li>
	<li<c:if test="${queryStatus eq 'A'}"> class="active"</c:if>><a href="javascript:void(0);" onclick="$('#queryStatus').val('A');$('#searchForm').submit();"><s:message code="info.status.A"/></a></li>
	<li<c:if test="${queryStatus eq 'B'}"> class="active"</c:if>><a href="javascript:void(0);" onclick="$('#queryStatus').val('B');$('#searchForm').submit();"><s:message code="info.status.B"/></a></li>
	<li<c:if test="${queryStatus eq 'C'}"> class="active"</c:if>><a href="javascript:void(0);" onclick="$('#queryStatus').val('C');$('#searchForm').submit();"><s:message code="info.status.C"/></a></li>
	<li<c:if test="${queryStatus eq 'D'}"> class="active"</c:if>><a href="javascript:void(0);" onclick="$('#queryStatus').val('D');$('#searchForm').submit();"><s:message code="info.status.D"/></a></li>
	<li<c:if test="${queryStatus eq 'E'}"> class="active"</c:if>><a href="javascript:void(0);" onclick="$('#queryStatus').val('E');$('#searchForm').submit();"><s:message code="info.status.E"/></a></li>
	<%-- 
	<li<c:if test="${queryStatus eq 'Z'}"> class="active"</c:if>><a href="javascript:void(0);" onclick="$('#queryStatus').val('Z');$('#searchForm').submit();"><s:message code="info.status.Z"/></a></li>
	<li<c:if test="${queryStatus eq 'X'}"> class="active"</c:if>><a href="javascript:void(0);" onclick="$('#queryStatus').val('X');$('#searchForm').submit();"><s:message code="info.status.X"/></a></li>
	 --%>
</shiro:hasPermission>
</ul>
<table id="pagedTable" border="0" cellpadding="0" cellspacing="0" class="ls-tb">
  <thead id="sortHead" pagesort="<c:out value='${page_sort[0]}' />" pagedir="${page_sort_dir[0]}" pageurl="list.do?page_sort={0}&page_sort_dir={1}&queryNodeId=${queryNodeId}&queryNodeType=${queryNodeType}&queryInfoRightType=${queryInfoRightType}&queryStatus=${queryStatus}&${searchstringnosort}">
  <tr class="ls_table_th">
    <th width="25"><input type="checkbox" onclick="Cms.check('ids',this.checked);"/></th>
    <th width="110"><s:message code="operate"/></th>
    <th width="30" class="ls-th-sort"><span class="ls-sort" pagesort="id">ID</span></th>
    <th class="ls-th-sort"><span class="ls-sort" pagesort="detail.title"><s:message code="info.title"/></span></th>
    <th class="ls-th-sort"><span class="ls-sort" pagesort="publishDate"><s:message code="info.publishDate"/></span></th>
    <th class="ls-th-sort"><span class="ls-sort" pagesort="priority"><s:message code="info.priority"/></span></th>
    <th class="ls-th-sort"><span class="ls-sort" pagesort="views"><s:message code="info.views"/></span></th>
    <th class="ls-th-sort"><span class="ls-sort" pagesort="status"><s:message code="info.status"/></span></th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="bean" varStatus="status" items="${pagedList.content}">
  <tr<shiro:hasPermission name="core:info:edit"> ondblclick="location.href=$('#edit_opt_${bean.id}').attr('href');"</shiro:hasPermission>>
    <td><input type="checkbox" name="ids" value="${bean.id}"/></td>
    <td align="center">
    	<shiro:hasPermission name="core:info:copy">
      <a id="copy_opt_${bean.id}" href="create.do?id=${bean.id}&queryNodeId=${queryNodeId}&queryNodeType=${queryNodeType}&queryInfoRightType=${queryInfoRightType}&queryStatus=${queryStatus}&${searchstring}" class="ls-opt"><s:message code="copy"/></a>
      </shiro:hasPermission>
    	<shiro:hasPermission name="core:info:edit">
      <a id="edit_opt_${bean.id}" href="edit.do?id=${bean.id}&queryNodeId=${queryNodeId}&queryNodeType=${queryNodeType}&queryInfoRightType=${queryInfoRightType}&queryStatus=${queryStatus}&position=${pagedList.number*pagedList.size+status.index}&${searchstring}" class="ls-opt"><s:message code="edit"/></a>
      </shiro:hasPermission>
    	<shiro:hasPermission name="core:info:delete">
      <a href="delete.do?ids=${bean.id}&queryNodeId=${queryNodeId}&queryNodeType=${queryNodeType}&queryInfoRightType=${queryInfoRightType}&queryStatus=${queryStatus}&${searchstring}" onclick="return confirmDelete();" class="ls-opt"><s:message code="delete"/></a>
      </shiro:hasPermission>
     </td>
    <td><c:out value="${bean.id}"/></td>
    <td>
    	<div><a href="${bean.url}" target="_blank" title="<c:out value='${bean.title}'/>"><c:out value="${fnx:substringx_sis(bean.title,30,'...')}"/></a></div>
    	<div>[<span style="color:blue;"><c:out value="${bean.node.displayName}"/></span>]
    		<c:if test="${fn:length(bean.infoAttrs) gt 0}">&nbsp;[<span style="color:red;"><c:forEach var="ia" items="${bean.infoAttrs}" varStatus="status">${ia.attribute.name}<c:if test="${!status.last}"> </c:if></c:forEach></span>]</c:if>
    	</div>
    </td>
    <td>
    	<div><fmt:formatDate value="${bean.publishDate}" pattern="yyyy-MM-dd HH:mm:ss"/></div>
    	<div><span style="color:blue;"><c:out value="${bean.creator.username}"/></span></div>
    </td>
    <td align="right"><c:out value="${bean.priority}"/></td>
    <td align="right"><c:out value="${bean.views}"/></td>
    <td align="center">    	
    	<c:choose>
    	<c:when test="${bean.statusChar ge 49 and bean.statusChar le 57}">
    		<s:message code="info.status.waitfor" arguments="${bean.status}"/>
    	</c:when>
    	<c:otherwise>
    		<s:message code="info.status.${bean.status}"/>
    	</c:otherwise>
    	</c:choose>	
    </td>
  </tr>
  </c:forEach>
  </tbody>
</table>
<c:if test="${fn:length(pagedList.content) le 0}"> 
<div class="ls-norecord margin-top5"><s:message code="recordNotFound"/></div>
</c:if>
</form>
<form action="list.do" method="get" class="ls-page">
	<tags:search_params excludePage="true"/>
	<f:hidden name="queryNodeId" value="${queryNodeId}"/>
	<f:hidden name="queryNodeType" value="${queryNodeType}"/>
	<f:hidden name="queryInfoRightType" value="${queryInfoRightType}"/>
	<f:hidden id="queryStatus" name="queryStatus" value="${queryStatus}"/>
  <tags:pagination pagedList="${pagedList}"/>
</form>
</body>
</html>