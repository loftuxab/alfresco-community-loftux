<#import "/org/alfresco/modules/discussions/user.lib.ftl" as userLib/>

<#--
   User related rendering macros.
   A user object is expected of following form:
   
   {
      "username" : "name of the user",
      "firstName" : "first name",
      "lastName" : "last name",
      "avatarRef" : node reference of the avatar image
   }
   
   firstName, lastName and avatarRef can be missing, in which case
   the macros will do a fallback to defaults
-->


<#--
  Renders the title of the list
-->
<#macro topicListTitle filter="" tag="">
   <#if (filter?length > 0 && filter != "all")>
      <#if filter == "hot">
         ${msg("topiclist.title.hottopics")}
      <#elseif (filter == "new")>
         ${msg("topiclist.title.newtopics")}
      <#elseif (filter == "mine")>
         ${msg("topiclist.title.mytopics")}
      <#else>
         Unknown filter!
      </#if>
   <#elseif (tag?length > 0)>
      ${msg("topiclist.title.bytag", tag)}
   <#else>
      ${msg("topiclist.title.alltopics")}
   </#if>
</#macro>


<#--
  Renders a list of topics
-->
<#macro topicListHTML htmlid topics viewmode="details">
   <#if (topics?size > 0)>
      <#list topics as topic>
         <#switch viewmode>
         <#case "simple">
            <@renderSimpleTopicEntry htmlid topic />
            <#break>
         <#case "details">
         <#default>
            <@renderDetailedTopicEntry htmlid topic />
         </#switch>
      </#list>
   <#else>
      <div class="noNode">${msg("topiclist.noTopics")}</div>
   </#if>
</div>
</#macro>

<#--
  Detailed topic entry
--> 
<#macro renderDetailedTopicEntry htmlid topic>
<div id="${topic.name}" class="node topic">
   <div class="nodeEdit">
      <div class="onViewNode" id="${htmlid}-onViewNode-${topic.name}">
         <a href="#" class="action-link-div">${msg("topic.action.view")}</a>
      </div>
        
      <#if (topic.permissions.edit)>
         <div class="onEditNode" id="${htmlid}-onEditNode-${topic.name}">
            <a href="#" class="action-link-div">${msg("topic.action.edit")}</a>
         </div>
      </#if>
        
      <#if (topic.permissions.delete)>
         <div class="onDeleteNode" id="${htmlid}-onDeleteNode-${topic.name}">
            <a href="#" class="action-link-div">${msg("topic.action.delete")}</a>
         </div>
      </#if>
   </div>
  
   <div class="nodeContent">
      <span class="nodeTitle">
         <a href="discussions-topicview?topicId=${topic.name}">
            ${topic.title}
         </a>
         <#if topic.isUpdated><span class="nodeStatus">(${msg("topic.updated")})</span></#if>
      </span>
      <div class="published">
         <span class="nodeAttrLabel">${msg("topic.info.createdOn")}:</span> <span class="nodeAttrValue"> ${topic.createdOn?datetime?string.medium_short}</span>
         <span class="spacer"> | </span>
         <span class="nodeAttrLabel">${msg("topic.info.author")}:</span><span class="nodeAttrValue"><@userLib.renderUserLink user=topic.author /></a></span>
         <br />
         <#if topic.lastReplyBy??>
            <span class="nodeAttrLabel">${msg("topic.info.lastReplyBy")}:</span> <span class="nodeAttrValue"><@userLib.renderUserLink user=topic.lastReplyBy /></span>
            <span class="spacer"> | </span>
            <span class="nodeAttrLabel">${msg("topic.info.lastReplyOn")}:</span> <span class="nodeAttrValue"><#if topic.lastReplyOn??>${topic.lastReplyOn?datetime?string.medium_short}</#if></span>    
         <#else>
            <span class="nodeAttrLabel">${msg("topic.footer.replies")}:</span> <span class="nodeAttrValue">${msg("topic.info.noReplies")}</span>
         </#if>
      </div>
      
      <div class="userLink"><@userLib.renderUserLink user=topic.author /></a> ${msg("topic.said")}:</div>
      <div class="content">${topic.content}</div>
   </div>
   <br clear="all" />
</div>
  
<div class="nodeFooter">
   <span class="nodeFooterBloc">
      <span class="nodeAttrLabel replyTo">${msg("topic.footer.replies")}:</span><span class="nodeAttrValue"> (${topic.totalReplyCount})</span>
      <span class="nodeAttrValue"><a href="discussions-topicview?topicId=${topic.name}">${msg("topic.footer.read")}</a></span>
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
</#macro>


<#--
  Simple topic entry
-->
<#macro renderSimpleTopicEntry htmlid topic>
<div id="${topic.name}" class="node topic simple">
   <div class="nodeEdit">
      <span class="onViewNode" id="${htmlid}-onViewNode-${topic.name}">
         <a class="action-link-span">${msg("topic.action.view")}</a>
      </span>
      <span class="spacer"> | </span>
      <#if (topic.permissions.edit)>
         <span class="onEditNode" id="${htmlid}-onEditNode-${topic.name}">
            <a class="action-link-span">${msg("topic.action.edit")}</a>
         </span>
         <span class="spacer"> | </span>
      </#if>
      <#if (topic.permissions.delete)>
         <span class="onDeleteNode" id="${htmlid}-onDeleteNode-${topic.name}">
            <a class="action-link-span">${msg("topic.action.delete")}</a>
         </span>
      </#if>
   </div>
   <div class="nodeContent">
      <div class="nodeTitle">
         <a href="discussions-topicview?topicId=${topic.name}">
            ${topic.title}
         </a>
      </div>
   </div>
   <br clear="all" />
</div>
</#macro>
