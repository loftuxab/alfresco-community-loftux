<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head> 
    <title>Alfresco Web Scripts Maintenance</title> 
    <link rel="stylesheet" href="${url.context}/css/base.css" TYPE="text/css">
  </head>
  <body>
    <table>
     <tr>
        <td><img src="${url.context}/images/logo/AlfrescoLogo32.png" alt="Alfresco" /></td>
        <td><nobr><span class="title">Alfresco Web Scripts Maintenance</span></nobr></td>
     </tr>
    </table>
	<br>
    <table>
      <tr align="left"><td><b>Maintenance Completed</tr>
<#list tasks as task>
      <tr><td>${task}<td></tr>
</#list>
    </table>
    <br>
    <table><tr><td><a href="${url.serviceContext}${url.match}">List Web Scripts</a></td></tr></table>
</html>