<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="version" value="3.0.4"/>
<c:set var="databaseName" value="jspxcms304"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Jspxcms 安装向导</title>
<link rel="stylesheet" href="${ctx}/setup/style.css" type="text/css"/>
<script type="text/javascript">
	function $(id) {
		return document.getElementById(id);
	}

	function showmessage(message) {
		document.getElementById('notice').innerHTML += message + '<br />';
	}
	
	function submitForm() {
		$("validForm").submit();
		$("previousButton").disabled=true;
		$("nextButton").disabled=true;
		$("nextButton").value="正在安装，请稍候...";
	}
</script>
</head>
<div class="container">
	<div class="header">
		<h1>安装向导</h1>
		<span>v${version}</span>
	</div>
<div class="main" style="margin-top:-123px;"><form id="validForm" action="${ctx}/setup.servlet" method="post">
<input type="hidden" name="step" value="1">
<div class="desc"><b>填写数据库信息</b></div><table class="tb2">
<tr><th class="tbopt">&nbsp;数据库服务器:</th>
<td><input type="text" name="host" value="localhost" size="35" class="txt"></td>
<td>&nbsp;一般为 localhost</td>
</tr>

<tr><th class="tbopt">&nbsp;数据库端口:</th>
<td><input type="text" name="port" value="" size="35" class="txt"></td>
<td>&nbsp;默认端口请留空</td>
</tr>

<tr><th class="tbopt">&nbsp;数据库名:</th>
<td><input type="text" name="name" value="${databaseName}" size="35" class="txt"></td>
<td>&nbsp;</td>
</tr>

<tr><th class="tbopt">&nbsp;数据库用户名:</th>
<td><input type="text" name="user" value="root" size="35" class="txt"></td>
<td>&nbsp;</td>
</tr>

<tr><th class="tbopt">&nbsp;数据库密码:</th>
<td><input type="text" name="password" value="" size="35" class="txt"></td>
<td>&nbsp;</td>
</tr>

<tr><th class="tbopt">&nbsp;是否创建数据库:</th>
<td>
	<label><input type="radio" name="isCreateDatabase" value="true" checked="checked"/>是</label> &nbsp;
	<label><input type="radio" name="isCreateDatabase" value="false"/>否</label>
</td>
<td>&nbsp;如数据库不存在，请选"是"</td>
</tr>

</table><div class="desc"><b>填写管理员信息</b></div><table class="tb2">
<tr><th class="tbopt">&nbsp;管理员账号:</th>
<td><input type="text" name="adminUsername" value="admin" size="35" class="txt"></td>
<td>&nbsp;</td>
</tr>

<tr><th class="tbopt">&nbsp;管理员密码:</th>
<td><input type="text" id="adminPassword" name="adminPassword" value="" size="35" class="txt"></td>
<td>&nbsp;</td>
</tr>

<tr><th class="tbopt">&nbsp;</th>
<td>
	<input type="hidden" name="version" value="${version}"/>
	<input type="button" id="previousButton" onclick="$('previousForm').submit();" value="上一步"/> &nbsp;
	<input type="button" id="nextButton" onclick="submitForm();" name="submitname" value="下一步" class="btn">
</td>
<td>&nbsp;</td>
</tr>

</table>
</form>
		<div class="footer">&copy;2010 - 2013 <a href="http://www.jspxcms.com/">Jspxcms</a></div>
	</div>
</div>
<form id="previousForm" action="${ctx}/setup.servlet" method="post">
<input type="hidden" name="previous" value="true"/>
</form>
</body>
</html>