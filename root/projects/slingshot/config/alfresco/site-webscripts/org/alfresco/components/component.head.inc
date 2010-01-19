<#function globalConfig key default>
   <#assign values = config.global.flags.childrenMap[key]>
   <#if values?? && values?is_sequence>
      <#return values[0].value>
   </#if>
   <#return default>
</#function>

<#-- Global flags retrieved from web-framework-config-application -->
<#assign DEBUG=(globalConfig("client-debug", "false") = "true")>

<#--
   JavaScript minimisation via YUI Compressor
-->
<#assign minJS=DEBUG?string(".js", "-min.js")>
<#macro script type src>
   <script type="${type}" src="${src?replace(".js", minJS)}"></script>
</#macro>