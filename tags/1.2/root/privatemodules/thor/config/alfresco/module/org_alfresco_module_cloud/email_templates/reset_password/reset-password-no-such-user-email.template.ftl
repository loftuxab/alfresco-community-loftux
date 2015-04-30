<#include "../email-macros.ftl">
<html>
<@emailtemplate "" "">
   <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
         <td>
            <div style="${standard_text}">
               ${label_reset_unknown}
               <br><br>
               <a href="${initiate_signup_url}?utm_medium=cloudapp&utm_source=ResetPassword">${label_sign_up}</a>
               <br><br>
               ${label_different_email}
               <br><br>
               ${label_signed}
            </div>
         </td>
      </tr>
      <@footerText ["${label_info_requested}"]/>
   </table>
</@>
</html>