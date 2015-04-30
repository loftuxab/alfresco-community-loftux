<#include "../email-macros.ftl">
<html>
<@emailtemplate "" "">
   <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
         <td>
            <div style="${standard_text}">
               ${label_reset_unactivated}
            </div>
         </td>
      </tr>
      <@button "${label_activate}" "${activate_account_url}"/>
      <tr><td style="font-size:5px;padding-top:20px;">&nbsp;</td></tr>
      <@footerText ["${label_info_requested}"]/>
   </table>
</@>
</html>