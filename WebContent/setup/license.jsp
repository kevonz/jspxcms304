<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="version" value="3.0.4"/>
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
</script>
</head>
<div class="container">
	<div class="header">
		<h1>安装向导</h1>
		<span>v${version}</span>
	</div>

<div class="main" style="margin-top:-123px;">
<div class="licenseblock">
<p style="color:red;padding-bottom:10px;text-align:center;">最低环境要求：JDK1.5、MySQL5.0、Tomcat5.5。</p>
<textarea style="width:100%;height:280px;">
  用户许可协议

  版权所有 (c)2010-2013，jspxcms.com

  保留所有权利。

  感谢您使用Jspxcms。

一、许可
  1.1 您可以在完全遵守本最终用户许可协议，特别是2.1条款，的基础上，将本软件应用于非商业和商业用途，而不必支付软件版权授权费用。
  1.2 您可以在协议规定的约束和限制范围内修改Jspxcms源代码(如果被提供的话)或界面风格以适应您的网站要求。
  1.3 您拥有使用本软件构建的网站中全部会员资料、文章及相关信息的所有权，并独立承担与文章内容的相关法律义务。
  1.4 在获得商业授权之后，您可以获得更多的软件功能，并且得到技术支持和服务。
  1.5 商业授权用户享有反映和提出意见的权力，并被优先考虑。

二、约束和限制
  2.1 无论如何，即无论用途如何、是否经过修改或美化、修改程度如何，只要使用Jspxcms的整体或任何部分，未经书面许可，网站所有页面标题（title）上的“Powered by Jspxcms”和页脚处的Jspxcms名称、链接（http://www.jspxcms.com/）都必须保留，而不能清除、修改或隐藏。
  2.2 不得对本软件或与之关联的商业授权进行出租、出售、抵押或发放子许可证。 
  2.3 禁止在Jspxcms的整体或任何部分基础上以发展任何派生版本、修改版本或第三方版本用于重新分发。
  2.4 如果您未能遵守本协议的条款，您的授权将被终止，所被许可的权利将被收回，并承担相应法律责任。

三、免责声明
  3.1 本软件及所附带的文件是作为不提供任何明确的或隐含的赔偿或担保的形式提供的。
  3.2 用户出于自愿而使用本软件，您必须了解使用本软件的风险，在尚未购买产品技术服务之前，我们不承诺提供任何形式的技术支持、使用担保，也不承担任何因使用本软件而产生问题的相关责任。
</textarea>
</div>
	<div class="btnbox marginbot">
		<form action="${ctx}/setup.servlet" method="post">
		<input type="hidden" name="step" value="0">
		<input type="hidden" name="uchidden" value="">
		<input type="submit" name="submit" value="我同意" style="padding: 2px">&nbsp;
		<input type="button" name="exit" value="我不同意" style="padding: 2px" onclick="javascript: window.close(); return false;">
		</form>
	</div>
	<div class="footer">&copy;2010 - 2013 <a href="http://www.jspxcms.com/">Jspxcms</a></div>
	</div>
</div>
</body>
</html>
