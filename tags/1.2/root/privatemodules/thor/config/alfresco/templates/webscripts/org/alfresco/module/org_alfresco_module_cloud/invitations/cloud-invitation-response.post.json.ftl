<#import "./invitation.lib.ftl" as invitationLib/>
<#escape x as jsonUtils.encodeJSONString(x)>
   <@invitationLib.cloudInvitationJSON item=invitation />
</#escape>
