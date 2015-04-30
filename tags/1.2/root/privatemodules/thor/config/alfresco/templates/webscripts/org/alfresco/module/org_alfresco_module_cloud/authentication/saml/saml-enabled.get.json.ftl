<#escape x as jsonUtils.encodeJSONString(x)>
   {
      "isSamlEnabled": ${isSamlEnabled?string},
      <#if isNetAdmin??>
      "isNetAdmin": ${isNetAdmin?string},
      </#if>
      "tenantDomain": "${tenantDomain}"
      
   }
</#escape>
