<#--Invite email template-->
<#assign label_invite_title="${inviter_first_name} ${inviter_last_name} has invited you to join the site ${site_title} with the role ${message('invitation.invitesender.email.role.' + invitee_role)}.">
<#assign label_invite_message_header="Message from ${inviter_first_name}:">
<#assign label_invite_accept="Accept Invitation">
<#assign label_invite_know_more="Want to know all about using Alfresco Cloud?">
<#assign label_invite_not_joining="Not going to join the site? You can always <a href=\"${reject_invitation_url}\">reject the invitation</a>.">