<%@ page buffer="0kb" autoFlush="true" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/adw.tld" prefix="adw" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title><adw:pageTitle/></title>
    <adw:imports/>
</head>
<body>
<table border="0" align="left" cellpadding="0" cellspacing="0" width="100%">
  <tr>
    <td colspan="2" valign="top">
    	<adw:region name="header" scope="site"/>
    </td>
  </tr>
  <tr>
    <td colspan="2" valign="top">
    	<adw:region name="topNav" scope="template"/>
    </td>
  </tr>
  <tr>
    <td colspan="1" valign="top" width="25%">
       <adw:region name="leftNav" scope="template"/>
    </td>
    <td colspan="1" valign="top" width="75%">
       <adw:region name="contentitem" scope="template" />
    </td>
  </tr>
  <tr>
    <td colspan="2" valign="top">
    	<adw:region name="footer" scope="site" />
    </td>
  </tr>
</table>

<adw:floatingmenu/>
</body>
</html>
