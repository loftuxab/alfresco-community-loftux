<#macro auditJSON log>
<#escape x as jsonUtils.encodeJSONString(x)>
{
    "data":
    {
        "enabled": ${log.enabled?string},
        "started": "${log.started}",
        "stopped": "${log.stopped}",
        "entries":
        [
            <#list log.entries as entry>
            {
                "timestamp": "${entry.timestampString}",
                "userName": "${entry.userName}",
                "userRole": "${entry.userRole}",
                "fullName": "${entry.fullName}",
                "nodeRef": "${entry.nodeRef}",
                "nodeName": "${entry.nodeName}",
                "event": "${entry.event}",
            }<#if entry_has_next>,</#if>
            </#list>
        ]
    }
}
</#escape>
</#macro>
