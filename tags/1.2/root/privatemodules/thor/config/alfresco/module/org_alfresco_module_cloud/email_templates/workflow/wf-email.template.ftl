<html>
<@emailtemplate "" "">
   <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
         <td>
            <div style="${standard_text}">
               <p style="${dashed_bottom_border}">
                  <#if args.workflowPooled == true>
                     ${label_wf_pool_task}
                  <#else>
                     <#if (args.workflowAssigner)?? && (args.workflowAssignerLink)??>
                        <a href="${args.workflowAssignerLink}">${args.workflowAssignee}</a> ${label_wf_assigned_by}
                     <#else>
                        ${label_wf_assigned}
                     </#if>
                  </#if>
               </p>
               <table width="66%" cellpadding="0" border="0">
                  <tr>
                     <th colspan="3" style="${th}">${label_wf_task}</th>
                  </tr>
                  <tr>
                     <td colspan="3">
                        <#if (args.workflowDescription)??>
                           <p style="padding-bottom:5px;">${args.workflowDescription}</p>
                        </#if>
                     </td>
                  </tr>
                  <tr>
                     <th style="${th}">${label_wf_due}</th>
                     <th style="${th}">${label_wf_priority}</th>
                     <th style="${th}">${label_wf_type}</th>
                  </tr>
                  <tr>
                     <td><#if (args.workflowDueDate)??>${args.workflowDueDate?date?string.full}<#else>${label_no_due_date}</#if></td>
                     <td>
                        <#if (args.workflowPriority)??>
                              <#if args.workflowPriority == 3>
                                 ${label_wf_priority_low}
                              <#elseif args.workflowPriority == 2>
                                 ${label_wf_priority_medium}
                              <#else>
                                 ${label_wf_priority_high}
                              </#if>
                        </#if>
                     </td>
                     <td>${message(args.workflowTitle)!args.workflowTitle}</td>
                  </tr>
               </table>
               <#if (args.workflowDocuments)??>
                  <table cellpadding="0" callspacing="0" border="0" bgcolor="#eeeeee" style="padding:10px; border: 1px solid #aaaaaa;">
                     <#list args.workflowDocuments as doc>
                        <tr>
                           <td>
                              <table cellpadding="0" cellspacing="0" border="0">
                                 <tr>
                                    <td valign="top">
                                       <img src="${shareUrl}/res/components/images/generic-file.png" alt="" width="64" height="64" border="0" style="padding-right: 10px;" />
                                    </td>
                                    <td>
                                       <table cellpadding="2" cellspacing="0" border="0">
                                          <tr>
                                             <td>
                                                <a href="${shareUrl}/${args.workflowTenant}/proxy/alfresco/api/node/content/workspace/SpacesStore/${doc.id}/${doc.name}?a=true">
                                                ${label_wf_download} ${doc.name}</a>
                                             </td>
                                          </tr>
                                       </table>
                                    </td>
                                 </tr>
                              </table>
                           </td>
                        </tr>
                        <#if doc_has_next>
                           <tr><td><div style="border-top: 1px solid #aaaaaa; margin-top:10px;"></div></td></tr>
                        </#if>
                     </#list>
                  </table>
               </#if>
            </div>
         </td>
      </tr>
      <@button "${label_wf_view_task}" "${shareUrl}/${args.workflowTenant}/page/task-details?taskId=${args.workflowId}"/>
      <tr>
         <td><div style="margin-top:10px; margin-bottom:10px;">&nbsp;</div></td>
      </tr>
      <@footerText ["${label_wf_legal}"]/>
   </table>
</@>
</html>