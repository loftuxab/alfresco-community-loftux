<#include "../component.head.inc">
<#-- Repository Document Library Actions: Supports concatenated JavaScript files via build scripts -->
<#if DEBUG>
   <script type="text/javascript" src="${page.url.context}/components/documentlibrary/repo-actions.js"></script>
<#else>
   <script type="text/javascript" src="${page.url.context}/js/repository-actions-min.js"></script>
</#if>
