<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head>
      <title>Index of Web Scripts Package '${package.path}'</title> 
      <link rel="stylesheet" href="${url.context}/css/base.css" type="text/css" />
   </head>
   <body>
      <div>
         <table>
            <tr>
               <td><img src="${url.context}/images/logo/AlfrescoLogo32.png" alt="Alfresco" /></td>
               <td><span class="title">Index of Web Scripts Package '${package.path}'</span></td>
            </tr>
            <tr><td colspan="2">Alfresco ${server.edition?html} v${server.version?html}</td></tr>
            <tr><td colspan="2">${package.scripts?size} Web Scripts</td></tr>
         </table>
         <br/>
         <table>
            <tr><td><a href="${url.serviceContext}/index">Back to Web Scripts Home</a></td></tr>
            <#if package.parent?exists>
            <tr><td><a href="${url.serviceContext}/index/package${package.parent.path}">Up to ${package.parent.path}</a></td></tr>
            </#if>
         </table>
         <br/>
         <#if package.children?size &gt; 0>
         <table>
         <@recurseuri package=package/>
         </table>
         <br/>
         </#if>
         <#macro recurseuri package>
         <#list package.children as childpath>
         <#if childpath.scripts?size &gt; 0>
         <tr><td><a href="${url.serviceContext}/index/package${childpath.path}">${childpath.path}</a></td></tr>
         </#if>
         <@recurseuri package=childpath/>
         </#list>  
         </#macro>
         <#list package.scripts as webscript>
         <#assign desc = webscript.description>
         <span class="mainSubTitle">${desc.shortName}</span>
         <table>
            <#list desc.URIs as uri>
            <tr><td><a href="${url.serviceContext}${uri?html}">${desc.method?html} ${url.serviceContext}${uri?html}</a></td></tr>
            </#list>
            <tr><td></td></tr>
         </table>
         <table>
            <#if desc.description??><tr><td>Description:</td><td>${desc.description}</td></tr><#else></#if>
            <tr><td>Authentication:</td><td>${desc.requiredAuthentication}</td></tr>
            <tr><td>Transaction:</td><td>${desc.requiredTransaction}</td></tr>
            <tr><td>Format Style:</td><td>${desc.formatStyle}</td></tr>
            <tr><td>Default Format:</td><td>${desc.defaultFormat!"<i>Determined at run-time</i>"}</td></tr>
            <tr><td></td></tr>
            <tr><td>Id:</td><td><a href="${url.serviceContext}/script/${desc.id}">${desc.id}</a></td></tr>
            <tr><td>Description:</td><td><a href="${url.serviceContext}/description/${desc.id}">${desc.storePath}/${desc.descPath}</a></td></tr>
         </table>
         <br/>
         </#list>
      </div>
   </body>
</html>