<html>
<@emailtemplate "" "">
   <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
         <td>
            <div style="${standard_text}">
               <#if saml_enabled??>${label_saml_invited}<#else>${label_signup_invited}</#if>
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
            </div>
         </td>
      </tr>
      <#if saml_enabled??>
         <@button "${label_log_in}" "${activate_account_url}"/>
      <#else>
         <@button "${label_accept}" "${activate_account_url}"/>
      </#if>
      <tr><td style="font-size:5px;padding-top:20px;">&nbsp;</td></tr>
      <@footerText ["${label_learn_more}",
                    "${label_ignore}"]/>
   </table>
</@>
</html>