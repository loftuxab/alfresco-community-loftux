<#--Invite email template-->
<#assign label_invite_title="${inviter_first_name} ${inviter_last_name} hat Sie eingeladen, der Site  ${site_title} beizutreten, mit der Rolle ${message('invitation.invitesender.email.role.' + invitee_role)}.">
<#assign label_invite_message_header="Nachricht von ${inviter_first_name}:">
<#assign label_invite_accept="Einladung annehmen">
<#assign label_invite_know_more="Weitere Informationen zu Alfresco Cloud">
<#assign label_invite_not_joining="Möchten Sie der Site nicht beitreten? Sie können <a href=\"${reject_invitation_url}\">die Einladung jederzeit ablehnen</a>">