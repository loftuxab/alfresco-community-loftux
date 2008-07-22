<#import "/org/alfresco/modules/discussions/user.lib.ftl" as userLib/>

<#--
   Macros to render comments and the comment form
-->

<#--
   Renders a list of comments
-->
<#macro commentsHTML htmlid comments="">
   <#if comments?has_content>
      <div class="commentsListTitle">${msg("comments.commentsListTitle")}: (${comments?size})</div>
      <#assign item_index=1>
         <#list comments as comment>
            <@commentHTML htmlid=htmlid comment=comment item_index=item_index /> 
            <#assign item_index = item_index + 1>
         </#list>
   </#if>
</#macro>


<#--
   Renders a comment view element, including the encosing div and the div for the edit form
-->
<#macro commentHTML htmlid comment item_index>
<#assign commentRef=(comment.nodeRef?replace("://", "_"))?replace("/", "_") >
    
<div class="comment ${item_index}<#if ((item_index % 2) == 0)> odd <#else> even </#if>" id="comment-${commentRef}">
   <@commentHTMLContent htmlid=htmlid comment=comment /> 
</div>

<#-- Edit form will be added here -->
<div id="comment-edit-form-${commentRef}" class="hidden">
</div>
   
</#macro>


<#--
    Renders the html for a comment
-->
<#macro commentHTMLContent htmlid comment>
<#assign commentRef=(comment.nodeRef?replace("://", "_"))?replace("/", "_") >
  
<div class="nodeEdit">
   <#if (comment.permissions.edit)>
   <div class="onEditComment" id="${htmlid}-onEditComment-${commentRef}">
      <a href="#" class="blogcomment-action">${msg("comments.action.edit")}</a>
   </div>
   </#if>
   
   <#if (comment.permissions.delete)>
   <div class="onDeleteComment" id="${htmlid}-onDeleteComment-${commentRef}">
      <a href="#" class="blogcomment-action">${msg("comments.action.delete")}</a>
   </div>
   </#if>
</div>
  
<div class="authorPicture">
   <@userLib.renderAvatarImage user=comment.author />
</div>
  
<div class="nodeContent">
   <div class="userLink">
      <@userLib.renderUserLink user=comment.author /> ${msg("comments.said")}:
      <#if comment.isUpdated><span class="nodeStatus">(${msg("comments.updated")})</span></#if>
   </div>

   <div class="content">${comment.content}</div>
</div>

<div class="commentFooter">
   <span class="nodeFooterBloc">
      <span class="nodeAttrLabel">${msg("comments.footer.postedOn")}: ${comment.createdOn?datetime?string.medium_short}</span>
   </span>
</div>  
</#macro>


<#--
    EDIT COMMENT FORM
-->
<#macro editCommentFormHTML htmlid comment>
<#assign commentRef=(comment.nodeRef?replace("://", "_"))?replace("/", "_") >
<div class="submitCommentForm">
   <div class="commentFormTitle">
      ${msg("comments.editComment")}
   </div>
   <div class="editComment">
      <form id="${htmlid}-${commentRef}-editform" name="commentForm" method="POST"
            action="${url.serviceContext}/modules/blog/comments/update-comment"
      >
         <input type="hidden" name="nodeRef" value="${comment.nodeRef}" />
         <input type="hidden" name="htmlid" value="${htmlid}" />
         <textarea id="${htmlid}-${commentRef}-editform-content" rows="8" cols="80" name="content">${comment.content}</textarea>
         <div class="commentFormAction">
            <input type="submit" id="${htmlid}-${commentRef}-editform-ok-button"  value='${msg("editcomment.form.updateComment")}' />
            <input type="reset"  id="${htmlid}-${commentRef}-editform-cancel-button"  value="${msg('editcomment.form.cancel')}" />
         </div>
      </form>
   </div>
</div>
</#macro>