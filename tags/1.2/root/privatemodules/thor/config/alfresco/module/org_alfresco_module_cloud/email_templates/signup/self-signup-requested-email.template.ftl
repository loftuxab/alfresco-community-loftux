<html>
<@emailtemplate "" "">
   <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
         <td>
            <div style="${standard_text}">
               <#if saml_enabled??>${label_saml_activate}<#else>${label_activate_reminder}</#if>
            </div>
         </td>
      </tr>
      <#if saml_enabled??>
         <@button "${label_log_in}" "${activate_account_url}"/>
      <#else>
         <@button "${label_activate_cta}" "${activate_account_url}"/>
      </#if>
      <tr>
         <td>
            <div style="${standard_text}">
               <#if !saml_enabled??>
                  ${label_capabilities}
                  <br />
                  <br />
               </#if>
               ${label_signed}
            </div>
         </td>
      </tr>
      <@footerText ["${label_learn_more}",
                  "${label_legal}"]/>
   </table>
</@>
</html>