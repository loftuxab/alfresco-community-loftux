<#macro emailtemplate bannerText="" bannerSubText="">
      <head>
         <meta http-equiv="Content-Type" content="text/html charset=UTF-8" />
         <#include "email-css.ftl"/>
      </head>
      <body style="${body}">
         <table style="${outer_table!""}"><tr><td align="center">
            <table style="${inner_table!""}" cellpadding="0" cellspacing="0">
                <tr style="${banner_colour_row!""}">
                    <td style="${banner_color_cell!""}${banner_colour_1!""}">&nbsp;</td>
                    <td style="${banner_color_cell!""}${banner_colour_2!""}">&nbsp;</td>
                    <td style="${banner_color_cell!""}${banner_colour_3!""}">&nbsp;</td>
                    <td style="${banner_color_cell!""}${banner_colour_4!""}">&nbsp;</td>
                    <td style="${banner_color_cell!""}${banner_colour_5!""}">&nbsp;</td>
                    <td style="${banner_color_cell!""}${banner_colour_6!""}">&nbsp;</td>
                </tr>
                <tr style="${banner_body_row!""}">
                    <td style="${banner_body_cell!""}" colspan="6">
                        <table>
                           <tr>
                               <td style="${banner_logo!""}" rowspan="2"><img src="${shareUrl}/res/cloud/components/images/Email-Template_logo_187x53.png" alt="Alfresco" style="${banner_logo_img}"/></td>
                               <td style="${banner_text!""}">${bannerText!""}</td>
                           </tr>
                           <tr>
                               <td style="${banner_subtext!""}">${bannerSubText!""}</td>
                           </tr> 
                        </table>
                    </td>
                </tr>
                <tr style="${banner_rounding_row!""}">
                    <td style="${banner_bottom_left_corner!""}"></td>
                    <td style="${banner_bottom_corner_fill!""}" colspan="4">&nbsp;</td>
                    <td style="${banner_bottom_right_corner!""}"></td>
                </tr>
                <tr style="${spacing_row!""}"><td style="${spacing_cell}" colspan="6">&nbsp;</td>
                </tr>
                <tr style="${body_top_corners_row!""}">
                    <td style="${body_top_left_corner!""}"></td>
                    <td style="${body_top_corner_fill!""}" colspan="4">&nbsp;</td>
                    <td style="${body_top_right_corner!""}"></td>
                </tr>
                <tr><td style="${body_nesting_cell}" colspan="6"><#nested></td></tr>
                <tr style="${body_bottom_corners_row!""}">
                    <td style="${body_bottom_left_corner!""}"></td>
                    <td style="${body_bottom_corner_fill!""}" colspan="4">&nbsp;</td>
                    <td style="${body_bottom_right_corner!""}"></td>
                </tr>
            </table>
         </td></tr></table>
      </body>
</#macro>
<#macro button label URL>
   <tr style="${body_footer_row_nodash}">
      <td>
         <a href="${URL}" style="text-decoration: none; ${button_center}">
            ${label}
         </a>
      </td>
   </tr>
</#macro>
<#macro footerText text=[]>
   <tr style="${body_footer_row}"><td><div style="${body_footer_row}"<div></td></tr>
   <#list text as item>
      <tr><td style="${body_footer_item}">${item}</td></tr>
   </#list>
   <tr><td><div style="${body_footer_row_bottom}"><div></td></tr>
</#macro>
<#if tenantDomain?? && tenantDomain != "">
   <#assign shareUrlCtx="${shareUrl}/${tenantDomain}">
<#else>
   <#assign shareUrlCtx="${shareUrl}/-default-">
</#if>
<#if followerFirstName?? && followerLastName?? && followerUserName??>
   <#assign followerFullName>${followerFirstName} ${followerLastName}</#assign>
   <#assign followerLink><a style="text-decoration:none;" href="${shareUrlCtx}/page/user/${followerUserName?url('ISO-8859-1')}/profile">${(followerFullName?trim)?html}</a></#assign>
</#if>