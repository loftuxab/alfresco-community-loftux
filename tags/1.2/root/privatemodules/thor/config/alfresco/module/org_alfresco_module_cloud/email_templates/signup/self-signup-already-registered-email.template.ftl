<html>
<@emailtemplate "" "">
   <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
         <td>
            <div style="${standard_text}">
               ${label_signup_already_reg}
            </div>
         </td>
      </tr>
      <@button "${label_log_in}" "${login_url}"/>
      <tr>
         <td>
            <div style="font-size: 14px; margin: 7px 0px; ">
               &nbsp;
            </div>
         </td>
      </tr>
      <@button "${label_forgot}" "${reset_password_url}"/>
      <tr>
         <td>
            <div style="${standard_text}">
               ${label_capabilities}
               <br />
               <br />
               ${label_signed}
            </div>
         </td>
      </tr>
      <@footerText ["${label_learn_more}",
                  "${label_legal}"]/>
   </table>
</@>
</html>