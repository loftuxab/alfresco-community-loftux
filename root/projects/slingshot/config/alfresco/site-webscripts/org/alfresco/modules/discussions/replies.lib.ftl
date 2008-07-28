<#import "/org/alfresco/modules/discussions/user.lib.ftl" as userLib/>

<#--
   Macros to render replies and the reply form
-->


<#--
   Renders a list of replies
-->
<#macro repliesHTML htmlid parentPostRef replies="">
<div class="indented" id="replies-of-${parentPostRef}" >

   <#-- Add reply form will be added here -->
   <div id="reply-add-form-${parentPostRef}" class="hidden">
   </div>
   
   <#if replies?has_content>
      <#list replies as reply>
         <@replyHTML htmlid=htmlid reply=reply/> 
      </#list>
   </#if>
</div>
</#macro>


<#--
   Renders a reply, including its enclosing div, the form div and the child replies
-->
<#macro replyHTML htmlid reply>
<#assign replyRef=(reply.nodeRef?replace("://", "_"))?replace("/", "_") >
<div class="reply" id="reply-${replyRef}">
   <@replyHTMLContent htmlid=htmlid reply=reply /> 
</div>

<div id="reply-edit-form-${replyRef}" class="hidden">
</div>

<@repliesHTML htmlid=htmlid parentPostRef=replyRef replies=reply.children /> 
</#macro>


<#--
   Renders the html for a reply view element
-->
<#macro replyHTMLContent htmlid reply>
<#assign replyRef=(reply.nodeRef?replace("://", "_"))?replace("/", "_") >
<div class="nodeEdit">
   <#if (reply.permissions.reply)>
   <div class="onAddReply" id="${htmlid}-onAddReply-${replyRef}">
      <a href="#" class="reply-action-link">${msg("replies.action.reply")}</a>
   </div>
   </#if>

   <#if (reply.permissions.edit)>
   <div class="onEditReply" id="${htmlid}-onEditReply-${replyRef}">
      <a href="#" class="reply-action-link">${msg("replies.action.edit")}</a>
   </div>
   </#if>
   <#-- we won't allow deletion, as it is not clear what to do with child replies
   <#if (reply.permissions.delete)>
      <div class="onDeleteReply" id="${htmlid}-onDeleteReply-${replyRef}">
         <a href="#" class="reply-action-link">${msg("replies.action.delete")}</a>
      </div>
   </#if>
   -->
</div>
  
<div class="authorPicture">
   <@userLib.renderAvatarImage user=reply.author />
</div>

<div class="nodeContent">
   <div class="userLink">
      <@userLib.renderUserLink user=reply.author /> ${msg("replies.said")}:
      <#if reply.isUpdated><span class="nodeStatus">(${msg("replies.updated")})</span></#if>
   </div>

   <div class="content">${reply.content}</div>
</div>
<div class="topicFooter">
   <span class="nodeFooterBloc">
      <span class="nodeAttrLabel replyTo">${msg("replies.footer.replies")}:</span><span class="nodeAttrValue">
        (<#if reply.children??>${reply.children?size}<#else>0</#if>)
      </span>
       
      <#if (reply.replyCount > 0)>
      <span class="nodeAttrValue">
         <a href="#" id="showHideReply-${replyRef}" class="showHideReply">${msg("replies.footer.hide")}</a>
      </span>
      </#if>
   </span>
   <span class="spacer"> | </span>
   <span class="nodeFooterBloc">
      <span class="nodeAttrLabel">${msg("replies.footer.postedOn")}: ${reply.createdOn?datetime?string.medium_short}</span>
   </span>
</div>
</#macro>


<#--
   Renders a reply html form
-->
<#macro replyFormHTML htmlid post isEdit>
<#assign postRef=(post.nodeRef?replace("://", "_"))?replace("/", "_")>
<div class="editNodeForm">
   <#if ! isEdit>
   <div class="replyTo">
      ${msg("replies.form.replyTo")}: <em><@userLib.renderUserName user=post.author /></em>
   </div>
   </#if>
   
   <div class="editReply">
      <form id="${htmlid}-${postRef}-form" name="replyForm" method="POST"
         <#if isEdit>
            action="${url.serviceContext}/modules/discussions/replies/update-reply"
         <#else>
            action="${url.serviceContext}/modules/discussions/replies/create-reply"
         </#if>
      >
         <input type="hidden" name="site" value="${site}" />
         <input type="hidden" name="container" value="${container}" />
         <input type="hidden" name="browseTopicUrl" value="${url.context}/page/site/${site}/discussions-topicview?container=${container}&topicId={post.name}" />
         <input type="hidden" name="path" value="${path}" />
         <input type="hidden" name="postRef" value="${post.nodeRef}" />
         <input type="hidden" name="htmlid" value="${htmlid}" />
         <textarea id="${htmlid}-replyContent" rows="8" cols="80" name="content"><#if isEdit>${post.content}</#if></textarea>
         <div class="nodeFormAction">
            <input type="submit" id="${htmlid}-${postRef}-ok-button"  value='<#if isEdit>${msg("replies.form.updateReply")}<#else>${msg("replies.form.postReply")}</#if>' />
            <input type="reset"  id="${htmlid}-${postRef}-cancel-button"  value="${msg('replies.form.cancel')}" />
         </div>
      </form>
   </div>
</div>
</#macro>