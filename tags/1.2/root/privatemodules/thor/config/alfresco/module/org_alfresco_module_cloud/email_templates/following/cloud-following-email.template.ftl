<#include "../email-macros.ftl">
<html>
<@emailtemplate "" "">
   <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
         <td>
            <div style="${standard_text}">
               <#if tenantDomain?? && tenantDomain != "">
                  ${label_following_message_tenantDomain}
               <#else>
                  ${label_following_message}
               </#if>
                <p/>
                <table cellpadding="0" callspacing="0" border="0" bgcolor="#eeeeee" style="font-size: 14px; padding: 10px">
                    <tr>
                       <td style="padding: 0 15px; text-align: left">
                          <span style="font-weight:bold">${followerLink}</span>
                       </td>
                       <td style="${follow_count_cell}">
                          <span style="${follow_label}">${label_following}</span><span>${followingCount}</span>
                       </td>
                       <td style="${follow_count_cell}">
                          <span style="${follow_label}">${label_followers}</span><span>${followerCount}</span>
                       </td>
                    </tr>
               </table>
            </div>
         </td>
      </tr>
      <@footerText ["${label_info_requested}"]/>
   </table>
</@>
</html>