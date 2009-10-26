<#--
   JavaScript minimisation via YUI Compressor
-->
<#assign minJS=(config.global.flags.childrenMap["client-debug"][0].value = "true")?string(".js", "-min.js")>
<#macro script type src>
   <script type="${type}" src="${src?replace(".js", minJS)}"></script>
</#macro>