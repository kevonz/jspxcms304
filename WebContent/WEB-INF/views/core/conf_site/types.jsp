<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib prefix="s" uri="http://www.springframework.org/tags"%><%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<input type="radio" id="radioBase" onclick="location.href='base_edit.do';"<c:if test="${type eq 'base'}"> checked="checked"</c:if>/><label for="radioBase"><s:message code="site.base.setting"/></label>
<shiro:hasPermission name="core:conf_site:watermark_edit">
<input type="radio" id="radioWatermark" onclick="location.href='watermark_edit.do';"<c:if test="${type eq 'watermark'}"> checked="checked"</c:if>/><label for="radioWatermark"><s:message code="site.watermark.setting"/></label>
</shiro:hasPermission>
