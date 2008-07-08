{
  <#-- Details of the response code -->
  "status" : 
  {
    "code" : ${status.code},
    "name" : "${status.codeName}",
    "description" : "${status.codeDescription}"
  },  
  
  <#-- Exception details -->
  "message" : "${status.message?j_string!""}",  
  "exception" : "<#if status.exception?exists>${status.exception.class.name?j_string}<#if status.exception.message?exists> - ${status.exception.message?j_string}</#if></#if>",
  
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
      ,"${exception?j_string}"
      <#list exception.stackTrace as element>
      ,"${element?j_string}"
      </#list>  
   <#else>
      ,"${exception?j_string}"
      ,"${exception.stackTrace[0]?j_string}"
   </#if>
</#macro>
