<#--Invite email template-->
<#assign label_invite_title="${inviter_first_name} ${inviter_last_name} vous a invité à rejoindre le site ${site_title} avec le rôle ${message('invitation.invitesender.email.role.' + invitee_role)}.">
<#assign label_invite_message_header="Message de ${inviter_first_name}:">
<#assign label_invite_accept="Accepter l'invitation">
<#assign label_invite_know_more="En savoir plus sur Alfresco Cloud">
<#assign label_invite_not_joining="Vous ne souhaitez pas rejoindre ce site ? Vous pouvez toujours <a href=\"${reject_invitation_url}\">rejeter l'invitation</a>">