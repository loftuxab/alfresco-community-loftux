<%@ page buffer="0kb" autoFlush="true" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/adw.tld" prefix="adw" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title><adw:pageTitle/></title>
    <adw:imports/>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Expires" CONTENT="-1">    
</head>
<body>
<table border="0" align="left" cellpadding="0" cellspacing="0" width="100%">
  <tr>
    <td colspan="3" valign="top">
    	<adw:region name="header" scope="site"/>
    </td>
  </tr>
  <tr>
    <td colspan="3" valign="top">
    	<adw:region name="topNav" scope="template"/>
    </td>
  </tr>
  <tr>
    <td colspan="3" valign="top">
       <adw:region name="blurb" scope="page" />
    </td>
  </tr>
  <tr>
    <td colspan="1" valign="top">
       <adw:region name="leftContent1" scope="page" />
       <adw:region name="leftContent2" scope="page" />
    </td>
    <td colspan="1" valign="top">
       <adw:region name="content" scope="page" />
    </td>
    <td colspan="1" valign="top">
       <adw:region name="rightContent1" scope="page" />
       <adw:region name="rightContent2" scope="page" />
    </td>
  </tr>
  <tr>
    <td colspan="3" valign="top">
    	<adw:region name="footer" scope="site" />
    </td>
  </tr>
</table>

<adw:floatingmenu/>
</body>
</html>
