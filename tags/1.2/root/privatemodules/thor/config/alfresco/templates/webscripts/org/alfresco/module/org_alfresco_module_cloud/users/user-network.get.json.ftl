<#escape x as jsonUtils.encodeJSONString(x)>
{
    "url": "${serviceContext + "/api/person/" + userName}",
    "avatar": "${serviceContext + "/api/person/" + userName}",
    "userName": "${userName}",
    "isExternal": ${isExternal?string},
    "isNetworkAdmin": ${isNetworkAdmin?string}<#if accountType??>,
    "accountTypeId": ${accountType.id?c},
    "accountClassName": "${accountType.accountClass.name!""}",
    "accountClassDisplayName": "${accountType.accountClass.displayName!""}"</#if>
}
</#escape>