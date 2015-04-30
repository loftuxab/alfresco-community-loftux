<#--Invite email template-->
<#assign label_invite_title="${inviter_first_name} ${inviter_last_name} ti ha invitato a partecipare al sito ${site_title} con il ruolo ${message('invitation.invitesender.email.role.' + invitee_role)}.">
<#assign label_invite_message_header="Messaggio da ${inviter_first_name}:">
<#assign label_invite_accept="Accetta invito">
<#assign label_invite_know_more="Ulteriori informazioni su Alfresco Cloud">
<#assign label_invite_not_joining="Non di desidera partecipare al sito? È sempre possibile <a href=\"${reject_invitation_url}\">rifiutare l'invito</a>">