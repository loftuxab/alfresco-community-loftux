<#escape x as jsonUtils.encodeJSONString(x)>
<#if valid>
  {
     "username": "${person.properties['userName']}",
     "email": "${person.properties['email']}"
  }
</#if>
</#escape>
