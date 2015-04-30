<html>
<@emailtemplate "" "">
   <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
         <td>
            <div style="${standard_text}">
               ${label_invite_title}<br/>
               <br>
               <div style="font-weight: bold;">${label_invite_message_header}</div>
               <br/>
               "${inviter_message}"<br/>
               <br/><a href="${accept_invitation_url}">${label_invite_accept}</a>
            </div>
         </td>
      </tr>
      <@button "${label_invite_accept}" "${accept_invitation_url}"/>
      <tr><td style="font-size:5px;padding-top:20px;">&nbsp;</td></tr>
      <@footerText ["<a href=\"http://cloud.alfresco.com\">${label_invite_know_more}</a>",
                    "${label_invite_not_joining}"]/>
   </table>
</@>
</html>