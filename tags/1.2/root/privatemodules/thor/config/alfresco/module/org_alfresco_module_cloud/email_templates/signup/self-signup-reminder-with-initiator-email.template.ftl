<html>
<@emailtemplate "" "">
   <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
         <td>
            <div style="${standard_text}">
               ${label_signup_invited}
            </div>
         </td>
      </tr>
      <tr>
         <td>
            <div style="${standard_text}">
               <#if userMessage??>
                  <div style="font-weight: bold;">${label_user_msg_msg}</div>
                  ${userMessage?html}<br/>
                  <br/>
               </#if>
               <br />
               <a href="${activate_account_url}">${label_accept}</a>
            </div>
         </td>
      </tr>
      <@button "${label_accept}" "${activate_account_url}"/>
      <tr><td style="font-size:5px;padding-top:20px;">&nbsp;</td></tr>
      <@footerText ["${label_learn_more}",
                    "${label_ignore}"]/>
   </table>
</@>
</html>