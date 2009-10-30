<#import "/org/alfresco/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <@wsLib.head>${msg("alfresco.index.title")}</@wsLib.head>
   <body>
      <div>
      <@wsLib.indexheader>Web Scripts Home</@wsLib.indexheader>
      <#if failures?size &gt; 0>
      <br/>
      <table>
         <tr><td><a href="${url.serviceContext}/index/failures">(+${failures?size} failed)</td></tr>
      </table>
      </#if>      
      <br>
      <@wsLib.onlinedoc/>
      <br/>
      <span class="mainSubTitle">Index</span>
      <#if rootfamily.children?size &gt; 0>
      <table>
         <#list rootfamily.children as childpath>
         <tr><td><a href="${url.serviceContext}/index/family${childpath.path}">Browse '${childpath.name}' Web Scripts</a></td></tr>
         </#list>  
      </table>
      <br/>
      </#if> 
      <table>
         <tr><td><a href="${url.serviceContext}/index/all">Browse all Web Scripts</a></td></tr>
         <tr><td><a href="${url.serviceContext}/index/uri/">Browse by Web Script URI</a></td></tr>
         <tr><td><a href="${url.serviceContext}/index/package/">Browse by Web Script Package</a></td></tr>
         <tr><td><a href="${url.serviceContext}/index/lifecycle/">Browse by Web Script Lifecycle</a></td></tr>
      </table>
      <br/>
      <br/>
      <span class="mainSubTitle">Maintenance</span>
      <form action="${url.serviceContext}${url.match}" method="post">
      <input type="hidden" name="reset" value="on"/>
      <table>
         <#if failures?size &gt; 0>
         <tr><td><a href="${url.serviceContext}/index/failures">Browse failed Web Scripts</a></td></tr>
         </#if>
         <tr><td><a href="${url.serviceContext}/api/javascript/debugger">Alfresco Javascript Debugger</a></td></tr>
         <tr><td><a href="${url.serviceContext}/installer">Web Script Installer</a></td></tr>
      </table>
      <br/>
      <table>
         <tr><td><input type="submit" name="submit" value="Refresh Web Scripts"/></td></tr>
      </table>
      </div>
   </body>
</html>