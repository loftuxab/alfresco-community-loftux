{
  <#-- Details of the response code -->
  "status" : 
  {
    "code" : ${status.code},
    "name" : "${status.codeName}",
    "description" : "${status.codeDescription}"
  },  
  
  <#-- Exception details -->
  "message" : "${jsonUtils.encodeJSONString(status.message!'')}",  
  "exception" : "<#if status.exception?exists>${jsonUtils.encodeJSONString(status.exception.class.name)}<#if status.exception.message?exists> - ${status.exception.message?j_string}</#if></#if>",
  
  <#-- Exception call stack --> 
  "callstack" : 
  [ 
  	  <#if status.exception?exists><@recursestack status.exception/></#if> 
  ],
  
  <#-- Server details and time stamp -->
  "server" : "Alfresco ${server.edition?xml} v${server.version?xml} schema ${server.schema?xml}",
  "time" : "${date?datetime}"
}

<#-- TODO ... need to remove the extra comma from the list below -->

<#macro recursestack exception>
   <#if exception.cause?exists>
      <@recursestack exception=exception.cause/>
   </#if>
   <#if exception.cause?exists == false>
      ,"${jsonUtils.encodeJSONString(exception)}"
      <#list exception.stackTrace as element>
      ,"${jsonUtils.encodeJSONString(element)}"
      </#list>  
   <#else>
      ,"${jsonUtils.encodeJSONString(exception)}"
      ,"${jsonUtils.encodeJSONString(exception.stackTrace[0])}"
   </#if>
</#macro>
