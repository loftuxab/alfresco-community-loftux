<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
      <title>${msg("alfresco.index.title")}</title> 
      <link rel="stylesheet" href="${url.context}/css/base.css" type="text/css" />
   </head>
   <body>
      <form action="${url.serviceContext}${url.match}" method="post">
         <div>
            <input type="hidden" name="reset" value="on"/>
            <table>
               <tr>
                  <td style="width:32px"><img src="${url.context}/images/logo/AlfrescoLogo32.png" alt="Alfresco" /></td>
                  <td><span class="title">Web Scripts Home</span></td>
               </tr>
               <tr><td colspan="2">Alfresco ${server.edition?html} v${server.version?html}</td></tr>
               <tr><td colspan="2">${webscripts?size} (+${failures?size} failed) Web Scripts - <a href="http://wiki.alfresco.com/wiki/HTTP_API">Online documentation</a>.
                                   <input type="submit" name="submit" value="Refresh list of Web Scripts"/></td></tr>
            </table>
         </div>
      </form>
      <div>
         <br/>
         <br/>
         <span class="title">Index</span>
         <table>
            <#if failures?size &gt; 0>
            <tr><td><a href="${url.serviceContext}/index/failures">Browse failed Web Scripts</a></td></tr>
            </#if>
            <tr><td><a href="${url.serviceContext}/index/all">Browse all Web Scripts</a></td></tr>
            <tr><td><a href="${url.serviceContext}/index/uri/">Browse by Web Script URL</a></td></tr>
            <tr><td><a href="${url.serviceContext}/index/package/">Browse by Web Script Package</a></td></tr>
         </table>
         <br/>
         <br/>
         <span class="title">Maintenance</span>
         <table>
            <tr><td><a href="${url.serviceContext}/api/javascript/debugger">Alfresco Javascript Debugger</a></td></tr>
            <tr><td><a href="${url.serviceContext}/installer">Web Script Installer</a></td></tr>
         </table>
      </div>
   </body>
</html>