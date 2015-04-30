<#escape x as jsonUtils.encodeJSONString(x)>
{
    "data":
    {
        "userId": "${userId}",
        <#if ticket??>
        "ticket": "${ticket}",
        <#else>	
        "ticket": null,
        </#if>
        <#if registration??>
        "registration":
        {
            "id": "${registration.id}",
            "key": "${registration.key}",
            "type": "${registrationType}"
        },
        </#if>
        "idpSessionIndex": "${idpSessionIndex}"
    }
}
</#escape>
