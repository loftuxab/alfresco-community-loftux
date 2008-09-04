<#import "/org/alfresco/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <@wsLib.head>Web Script Installer</@wsLib.head>
  <body>
    <div>
    <@wsLib.header>Web Scripts Installer</@wsLib.header>
    <br/>
    <@wsLib.home/>
    <br/>
    <br/>
    <table>
      <form action="${url.service}" method="post" enctype="multipart/form-data">
        <tr><td><td>Web Script:
        <tr><td><td><input type="file" name="webscript">
        <tr><td><td>&nbsp;
        <tr><td><td><input type="submit" name="submit" value="Install">
      </form>
      <tr><td><td>&nbsp;
      <tr><td><td>&nbsp;
      <tr><td><td>To create a Web Script file that can be installed:
      <tr><td><td>&nbsp;
      <tr><td><td>1) Display the full definition of the Web Script via the URL <a href="${url.serviceContext}/script/{scriptid}">${url.serviceContext}/script/{scriptid}</a>
      <tr><td><td>&nbsp;
      <tr><td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;e.g. to display the "AVM Browse Sample", issue <a href="${url.serviceContext}/script/org/alfresco/sample/avmbrowse.get">${url.serviceContext}/script/org/alfresco/sample/avmbrowse.get</a>
      <tr><td><td>&nbsp;
      <tr><td><td>2) Save the the HTML page displayed in step 1
    </table>
    </div>
  </body>
</html>
