<#--Invite email template-->
<#assign label_invite_title="${inviter_first_name} ${inviter_last_name} le ha invitado a unirse al sitio ${site_title} con el rol ${message('invitation.invitesender.email.role.' + invitee_role)}.">
<#assign label_invite_message_header="Mensaje de ${inviter_first_name}:">
<#assign label_invite_accept="Aceptar invitación">
<#assign label_invite_know_more="¿Desea más información sobre Alfresco en la nube?">
<#assign label_invite_not_joining="¿No desea unirse a este sitio? Siempre podrá <a href=\"${reject_invitation_url}\">rechazar la invitación</a>">