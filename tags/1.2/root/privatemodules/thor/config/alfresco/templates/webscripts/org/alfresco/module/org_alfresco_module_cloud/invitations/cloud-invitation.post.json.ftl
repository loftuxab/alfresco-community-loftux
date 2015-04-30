<#import "./invitation.lib.ftl" as invitationLib/>
<#escape x as jsonUtils.encodeJSONString(x)>
{
   "invitations": [
      <#list invitations as item>
         <@invitationLib.cloudInvitationJSON item=item />
      <#if item_has_next>,</#if>
      </#list>
   ]
}
</#escape>
