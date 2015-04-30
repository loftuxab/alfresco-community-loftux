<#--Invite email template-->
<#assign label_invite_title="${inviter_first_name} ${inviter_last_name} から、サイト ${site_title} に次の役割で参加するよう招待されています: ${message('invitation.invitesender.email.role.' + invitee_role)}.">
<#assign label_invite_message_header="メッセージ送信者 ${inviter_first_name}:">
<#assign label_invite_accept="招待の承諾">
<#assign label_invite_know_more="Alfresco Cloudについて">
<#assign label_invite_not_joining="サイトに参加しない場合は、 いつでも <a href=\"${reject_invitation_url}\">招待を拒否できます</a>">