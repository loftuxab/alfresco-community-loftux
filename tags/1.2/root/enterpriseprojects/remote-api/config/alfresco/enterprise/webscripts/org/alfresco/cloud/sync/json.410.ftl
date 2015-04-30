<#-- The SSD can't be found (usually means it was deleted) -->
{
  <#escape x as jsonUtils.encodeJSONString(x)>
    "message": "${message}",
    "ssdId": "${ssdId}"
  </#escape>
}
