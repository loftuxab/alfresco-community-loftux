<#--
  Renders a topic.
  
  @param topic The topic data to render.
  
  ${page.url.context}
  ${url.context}
-->

<#macro topicViewHTML htmlid topic>
<div id="${topic.name}" class="node topic topicview">

   <div class="nodeEdit">
      <#if (topic.permissions.reply)>
      <div class="onAddReply" id="${htmlid}-onAddReply-${topic.name}">
         <a href="#" class="topic-action-link">${msg("topic.action.addReply")}</a>
      </div>
      </#if>
      <#if (topic.permissions.edit)>
      <div class="onEditNode" id="${htmlid}-onEditNode-${topic.name}">
         <a href="#" class="topic-action-link">${msg("topic.action.edit")}</a>
      </div>
      </#if>
      <#if (topic.permissions.delete)>
      <div class="onDeleteNode" id="${htmlid}-onDeleteNode-${topic.name}">
         <a href="#" class="topic-action-link">${msg("topic.action.delete")}</a>
      </div>
      </#if>
   </div>
  
   <div class="authorPicture"><img src="${url.context}/components/images/no-photo.png" width="64" height="64" alt="photo" /></div>

   <div class="nodeContent">
      <div class="nodeTitle">
         <a href="discussions-topicview?topicId=${topic.name}">
            ${topic.title}
         </a>
         <#if topic.isUpdated><span class="nodeStatus">(${msg("topic.updated")})</span></#if>
      </div>
      <div class="published">
         <span class="nodeAttrLabel">${msg("topic.info.createdOn")}:</span> <span class="nodeAttrValue"> ${topic.createdOn?datetime?string.medium_short}</span>
         <span class="spacer"> | </span>
         <span class="nodeAttrLabel">${msg("topic.info.author")}:</span><span class="nodeAttrValue"><a href=""> ${topic.author}</a></span>      
         <br />
         <#if (topic.totalReplyCount > 0)>
            <span class="nodeAttrLabel">${msg("topic.info.lastReplyBy")}:</span> <span class="nodeAttrValue">${topic.lastReplyBy!""}</span>
            <span class="spacer"> | </span>
            <span class="nodeAttrLabel">${msg("topic.info.lastReplyOn")}:</span> <span class="nodeAttrValue"><#if topic.lastReplyOn??>${topic.lastReplyOn?datetime?string.medium_short}</#if></span>
         <#else>
            <span class="nodeAttrLabel">${msg("topic.footer.replies")}:</span> <span class="nodeAttrValue">${msg("topic.info.noReplies")}</span>
         </#if>
      </div>
      
      <div class="userLink"><a href="">${topic.author}</a> ${msg("topic.said")}:</div>
      <div class="content">${topic.content}</div>
   </div>    

   <br clear="all" />
   <div class="nodeFooter">
      <span class="nodeFooterBloc">
         <span class="nodeAttrLabel replyTo">${msg("topic.footer.replies")}:</span><span class="nodeAttrValue"> (${topic.replyCount})</span>
      </span> 
      
      <span class="spacer"> | </span>

      <#if (topic.tags?size > 0)>
      <span class="nodeFooterBloc">
         <span class="nodeAttrLabel tag">${msg("topic.tags")}:</span>
         <#list topic.tags as tag>
            <span class="nodeAttrValue" id="${htmlid}-onTagSelection-${tag}">
               <a href="" class="tag-link-span">${tag}</a>
            </span><#if tag_has_next> , </#if> 
         </#list>
      </span>
      </#if> 
   </div>
</div>
</#macro>


<#--
  Renders a form to edit a topic.
  
  @param form-id The form id to use
  @param topic The topic data to insert into the form.
               Can be empty in which case the form will contain no data.
-->
<#macro topicFormHTML htmlid topic="">
<div class="editNodeForm">
   <form id="${htmlid}-form" name="${htmlid}-form" method="POST"
      <#if topic?has_content>
         action="${url.serviceContext}/modules/discussions/topic/update-topic"
      <#else>
         action="${url.serviceContext}/modules/discussions/topic/create-topic"
      </#if>
   >
      <input type="hidden" name="site" value="${site}">
      <input type="hidden" name="htmlid" value="${htmlid}">
      <#if topic?has_content>
         <input type="hidden" name="topicId" value="${topic.name}" >
      </#if>
       
      <label>${msg("topic.form.topicTitle")}:</label>
      <input type="text" value="<#if topic?has_content && topic.title?has_content>${topic.title}</#if>"
             name="title" id="${htmlid}-title" size="80" />
      <label>${msg("topic.form.topicText")}:</label>
      <textarea rows="8" cols="80" name="content" id="${htmlid}-content"
         ><#if topic?has_content && topic.content?has_content>${topic.content}</#if></textarea> 
         
      <!-- tags -->
      <#if topic?has_content>
         <#assign tags=topic.tags />
      <#else>
         <#assign tags=[] />
      </#if>
      <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
      <!-- Render the tag inputs -->
      <@taglibraryLib.renderTagInputs htmlid=htmlid tags=tags tagInputName="tags" />
      <!-- Render the library component -->
      <@taglibraryLib.renderTagLibrary htmlid=htmlid site=site tags=tags />
      <!-- end tags -->

      <div class="nodeFormAction">
         <input type="submit" id="${htmlid}-ok-button" value="<#if topic?has_content>${msg('topic.form.save')}<#else>${msg('topic.form.create')}</#if>" />
         <input type="reset" id="${htmlid}-cancel-button" value="${msg('topic.form.cancel')}" />
      </div>
   </form>
</div>
</#macro>
