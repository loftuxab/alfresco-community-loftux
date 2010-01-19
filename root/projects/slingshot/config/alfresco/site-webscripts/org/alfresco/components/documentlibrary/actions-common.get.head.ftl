<#include "../component.head.inc">
<#-- Document Library Actions: Supports concatenated JavaScript files via build scripts -->
<#if DEBUG>
   <script type="text/javascript" src="${page.url.context}/components/documentlibrary/actions.js"></script>
   <script type="text/javascript" src="${page.url.context}/modules/simple-dialog.js"></script>
   <script type="text/javascript" src="${page.url.context}/modules/documentlibrary/global-folder.js"></script>
   <script type="text/javascript" src="${page.url.context}/modules/documentlibrary/copy-move-to.js"></script>
   <script type="text/javascript" src="${page.url.context}/modules/documentlibrary/details.js"></script>
   <script type="text/javascript" src="${page.url.context}/modules/taglibrary/taglibrary.js"></script>
   <script type="text/javascript" src="${page.url.context}/modules/documentlibrary/workflow.js"></script>
   <script type="text/javascript" src="${page.url.context}/components/people-finder/people-finder.js"></script>
   <script type="text/javascript" src="${page.url.context}/modules/documentlibrary/permissions.js"></script>
   <script type="text/javascript" src="${page.url.context}/modules/documentlibrary/aspects.js"></script>
<#else>
   <script type="text/javascript" src="${page.url.context}/js/documentlibrary-actions-min.js"></script>
</#if>
<#-- Global Folder Picker (req'd by Copy/Move To) -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/modules/documentlibrary/global-folder.css" />
<#-- Details -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/modules/documentlibrary/details.css" />
<#-- Tag Library -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/modules/taglibrary/taglibrary.css" />
<#-- Assign Workflow -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/modules/documentlibrary/workflow.css" />
<#-- People Finder Assets (req'd by Assign Workflow)  -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/components/people-finder/people-finder.css" />
<#-- Manage Permissions -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/modules/documentlibrary/permissions.css" />
<#-- Manage Aspects -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/modules/documentlibrary/aspects.css" />