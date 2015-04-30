<html>
<@emailtemplate "" "">
   <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
         <td>
            <div style="${standard_text}">
               ${label_activate}
            </div>
         </td>
      </tr>
      <@button "${label_activate_cta}" "${activate_account_url}"/>
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