<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head> 
      <title>Alfresco Javascript Debugger</title> 
      <link rel="stylesheet" href="${url.context}/css/base.css" type="text/css" />
   </head>
   <body>
      <form action="${url.serviceContext}${url.match}" method="post">
         <div>
            <input type="hidden" name="visible" value="<#if visible>false<#else>true</#if>" />
            <table>
               <tr>
                  <td><img src="${url.context}/images/logo/AlfrescoLogo32.png" alt="Alfresco" /></td>
                  <td><span class="title">Alfresco Javascript Debugger</span></td>
               </tr>
               <tr><td colspan="2">Alfresco ${server.edition?html} v${server.version?html}</td></tr>
               <tr><td colspan="2">Currently <#if visible>enabled<#else>disabled</#if>.
                                   <input type="submit" name="submit" value="<#if visible>Disable<#else>Enable</#if>" /></td></tr>
            </table>
         </div>
      </form>
   </body>
</html>