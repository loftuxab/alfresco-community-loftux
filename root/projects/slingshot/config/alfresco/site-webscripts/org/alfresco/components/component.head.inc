<#--
   JavaScript and (future) CSS minimisation via YUI Compressor
-->
<#assign minJS=(config.global.flags.childrenMap["debug"][0].value = "true")?string(".js", "-min.js")>
<#macro script type src>
   <script type="${type}" src="${src?replace(".js", minJS)}"></script>
</#macro>
<#macro link rel type href><#-- Compressing CSS not currently supported -->
   <link rel="${rel}" type="${type}" href="${href}" />
</#macro>