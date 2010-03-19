<#include "../component.head.inc">
<#-- Data List Actions: Supports concatenated JavaScript files via build scripts -->
<#if DEBUG>
   <script type="text/javascript" src="${page.url.context}/components/data-lists/actions.js"></script>
   <script type="text/javascript" src="${page.url.context}/modules/simple-dialog.js"></script>
<#else>
   <script type="text/javascript" src="${page.url.context}/js/datalists-actions-min.js"></script>
</#if>