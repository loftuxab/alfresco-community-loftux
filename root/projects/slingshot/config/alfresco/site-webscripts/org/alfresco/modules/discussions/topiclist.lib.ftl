<#--
   Contains all html snippets for the topiclist component
-->

<#--
  Renders a list of topics
-->
<#macro topicListHTML topics viewmode="details">
    <#if (topics?size > 0)>
		<#list topics as topic>
			<#switch viewmode>
			<#case "simple">
				<@renderSimpleTopicEntry topic=topic/>
				<#break>
			<#case "details">
			<#default>
				<@renderDetailedTopicEntry topic=topic/>
			</#switch>
		</#list>
	<#else>
        <div class="noNode">${msg("topic.noTopic")}</div>
	</#if>
</div>
</#macro>

<#--
	Detailed topic entry
--> 
<#macro renderDetailedTopicEntry topic>
  <div id="${topic.name}" class="node topic">
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  <td width="80%">
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
			<span class="nodeAttrLabel">${msg("topic.info.author")}:</span><span class="nodeAttrValue"><a href=""> ${topic.author}</a></span>		
			<br />
			<#if (topic.totalReplyCount > 0)>
			<span class="nodeAttrLabel">${msg("topic.info.lastReplyBy")}:</span> <span class="nodeAttrValue"> ${topic.lastReplyBy!""}</span>
			<span class="spacer"> | </span>
			<span class="nodeAttrLabel">${msg("topic.info.lastReplyOn")}:</span> <span class="nodeAttrValue"><#if topic.lastReplyOn??>${topic.lastReplyOn?datetime?string.medium_short}</#if></span>		
			<#else>
			<span class="nodeAttrValue">${msg("topic.info.noReplies")}</span>
			</#if>
		</div>
		
		<div class="userLink"><a href="">${topic.author}</a> ${msg("topic.said")}:</div>
		<div class="content">${topic.content}</div>
	</div>
	</td>
	
	<td width="20%">
	<div class="nodeEdit">
        <div class="onViewNode" id="onViewNode-${topic.name}">
           <a href="#" class="action-link">${msg("topic.action.view")}</a>
        </div>
        
		<#if (topic.permissions.edit)>
	        <div class="onEditNode" id="onEditNode-${topic.name}">
	           <a href="#" class="action-link">${msg("topic.action.edit")}</a>
	        </div>
        </#if>
        
		<#if (topic.permissions.delete)>
	        <div class="onDeleteNode" id="onDeleteNode-${topic.name}">
	           <a href="#" class="action-link">${msg("topic.action.delete")}</a>
	        </div>
	    </#if>
	            
        <!--		<div><a id="reply-${topic.name}" class="addEventReply" href="">Reply</a></div>
		<div><a id="edit-${topic.name}" class="editEventTopic" href="site/${site}/discussions-topicview?topicId=topic-${topic.name}&edit=true">Edit</a></div>
		<div><a id="delete-${topic.name}" class="deleteEventTopic" href="">Delete</a></div>-->
	</div>
  </td>
  </tr>
  </table>
  </div>
  <div class="nodeFooter">
  	<span class="nodeFooterBloc">
		<span class="nodeAttrLabel replyTo">${msg("topic.footer.replies")}:</span><span class="nodeAttrValue"> (${topic.totalReplyCount})</span>
		<span class="nodeAttrValue"><a href="discussions-topicview?topicId=${topic.name}">${msg("topic.footer.read")}</a></span>
	</span> 
	<span class="spacer"> | </span>
<#--
	<span class="nodeFooterBloc">
		<span class="nodeAttrLabel">${msg("topic.footer.views")}:</span><span class="nodeAttrValue"> ${topic.nbViews}</span>
	</span>
	<span class="spacer"> | </span>
-->
	<#if (topic.tags?size > 0)>
		<span class="nodeFooterBloc">
			<span class="nodeAttrLabel tag">${msg("topic.tags")}:</span>
			<#list topic.tags as tag>
				<span class="nodeAttrValue"><a href="">${tag}</a></span><#if tag_has_next> , </#if> 
			</#list>
		</span>
	</#if> 
  </div>
</#macro>

<#--
	Simple topic entry
-->
<#macro renderSimpleTopicEntry topic>
  <div id="${topic.name}" class="node topic simple">
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  <td width="80%">
	<div class="nodeContent">
		<div class="nodeTitle">
			<a href="discussions-topicview?topicId=${topic.name}">
				${topic.title}
			</a>
		</div>
	</div>
  </td>

  <td width="20%">
	<div class="nodeEdit">
		<span class="onViewNode">
			<a id="reply-${topic.name}" class="viewEventTopic" href="">
				${msg("topic.action.view")}
			</a>
		</span>
		<span class="spacer"> | </span>
		<#if (topic.permissions.edit)>
			<span class="onEditNode">
				<a id="edit-${topic.name}" class="editEventTopic" href="site/${site}/discussions-topicview?topicId=topic-${topic.name}&edit=true">
					${msg("topic.action.edit")}
				</a>
			</span>
			<span class="spacer"> | </span>
		</#if>
		<#if (topic.permissions.delete)>
			<span class="onDeleteNode">
				<a id="delete-${topic.name}" class="deleteEventTopic" href="">
					${msg("topic.action.delete")}
				</a>
			</span>
		</#if>
	</div>
  </td>
  </tr>
  </table>
  </div>
</#macro>
