<#--
   Contains all html snippets for the topic component
-->


<#--
  Renders a topic.
  
  @param topic The topic data to render.
  
  ${page.url.context}
  ${url.context}
-->


<#macro topicViewHTML topic>
  <div id="${topic.name}" class="node topic topicview">
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  <td width="100">
	<div class="authorPicture"><img src="${url.context}/components/images/no-photo.png" width="64" height="64" alt="photo" /></div>
  </td>
  <td>
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
			<span class="nodeAttrLabel">${msg("topic.info.lastReplyBy")}:</span> <span class="nodeAttrValue">${topic.lastReplyBy!""}</span>
			<span class="spacer"> | </span>
			<span class="nodeAttrLabel">${msg("topic.info.lastReplyOn")}:</span> <span class="nodeAttrValue"><#if topic.lastReplyOn??>${topic.lastReplyOn?datetime?string.medium_short}</#if></span>
		</div>
		
		<div class="userLink"><a href="">${topic.author}</a> ${msg("topic.said")}:</div>
		<div class="content">${topic.content}</div>
	</div>
	</td>
	
	<td width="150">
	<div class="nodeEdit">
		<#if (topic.permissions.reply)>
	        <div class="onAddReply" id="onAddReply-${topic.name}">
	           <a href="#" class="action-link-${topic.name}">${msg("topic.action.addReply")}</a>
	        </div>
	    </#if>
		<#if (topic.permissions.edit)>
	        <div class="onEditNode" id="onEditNode-${topic.name}">
	           <a href="#" class="action-link-${topic.name}">${msg("topic.action.edit")}</a>
	        </div>
	    </#if>
		<#if (topic.permissions.delete)>
	        <div class="onDeleteNode" id="onDeleteNode-${topic.name}">
	           <a href="#" class="action-link-${topic.name}">${msg("topic.action.delete")}</a>
	        </div>
	    </#if>
	</div>
	</td>
	</tr>
	</table>
	
    <div class="nodeFooter">
	  	<span class="nodeFooterBloc">
			<span class="nodeAttrLabel replyTo">${msg("topic.footer.replies")}:</span><span class="nodeAttrValue"> (${topic.replyCount})</span>
		</span> 
		
		<span class="spacer"> | </span>
		
		<#--
		<span class="nodeFooterBloc">
			<span class="nodeAttrLabel">${msg("topic.footer.views")}:</span><span class="nodeAttrValue"> ${topic.nbViews}</span>
		</span>
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
  </div>
</#macro>


<#--
  Renders a form to edit a topic.
  
  @param form-id The form id to use
  @param topic The topic data to insert into the form.
               Can be empty in which case the form will contain no data.
-->

<#--
  Returns a comma-separated string of the elements in the passed array-
  @param elems an array with the elements to concat
  @return a comma separated string of the elements in the array
-->
<#function concatArray elems>
  <#assign res><#list elems as x>${x}<#if x_has_next>, </#if></#list></#assign>
  <#return res>
</#function>

<#macro topicFormHTML htmlId topic="">
<div class="editNodeForm">
	<form id="${htmlId}-form" name="${htmlId}-form" method="POST"
        <#if topic?has_content>
            action="${url.serviceContext}/modules/discussions/topic/update-topic"
        <#else>
            action="${url.serviceContext}/modules/discussions/topic/create-topic"
        </#if>
    >
    	<input type="hidden" name="site" value="${site}">
	    <#if topic?has_content>
	      <input type="hidden" name="topicId" value="${topic.name}" >
	    </#if>
	    
		<label>${msg("topic.form.topicTitle")}:</label>
		<input type="text" value="<#if topic?has_content && topic.title?has_content>${topic.title}</#if>"
		       name="title" id="${htmlId}-title" size="80" />
		<label>${msg("topic.form.topicText")}:</label>
		<textarea rows="8" cols="80" name="content" id="${htmlId}-content"
		   ><#if topic?has_content && topic.content?has_content>${topic.content}</#if></textarea> 
		<label>${msg("topic.tags")}:</label>
		<input type="text" name="tags" id="${htmlId}-tags" size="80" 
		    <#if topic?has_content && topic.tags?has_content>value="${concatArray(topic.tags)}"</#if> />
		<label>${msg("topic.form.suggested")}:</label>
		<div class="suggestedTags">
			<ul>
				<li><a href="">ECM</a></li>
				<li><a href="">Computer</a></li>
				<li><a href="">Brand</a></li>
				<li><a href="">Trend</a></li>
			</ul>
			<br />
			<a href="">${msg("topic.form.viewTagLibrary")}</a>
		</div>
		<div class="nodeFormAction">
			<input type="submit" id="${htmlId}-ok-button" value="<#if topic?has_content>${msg('topic.form.save')}<#else>${msg('topic.form.create')}</#if>" />
			<input type="reset" id="${htmlId}-cancel-button" value="${msg('topic.form.cancel')}" />
		</div>
	</form>
</div>
</#macro>
