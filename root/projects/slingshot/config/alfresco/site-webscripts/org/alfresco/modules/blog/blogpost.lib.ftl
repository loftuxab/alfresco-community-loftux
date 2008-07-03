<#--
   Contains all html snippets for the post component
-->


<#--
  Renders a post.
  
  @param post The post data to render.
  
  ${page.url.context}
  ${url.context}
-->


<#macro blogpostViewHTML post>
  <div id="${post.name}" class="node post postview">
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  <td>
	<div class="nodeContent">
		<div class="nodeTitle">
			<a href="blog-postview?postId=${post.name}">
				${post.title}
			</a>
			<#import "/org/alfresco/modules/blog/blogpostlist.lib.ftl" as blogpostlistLib/>
			<@blogpostlistLib.renderPostStatus post=post/>
		</div>
		<div class="published">
			<span class="nodeAttrLabel">${msg("post.info.publishedOn")}:</span> <span class="nodeAttrValue">${post.modifiedOn?datetime?string.medium_short}</span>
			<span class="spacer"> | </span>
			<span class="nodeAttrLabel">${msg("post.info.author")}:</span><span class="nodeAttrValue"><a href=""> ${post.author}</a></span>
		</div>
		
		<div class="content">${post.content}</div>
	</div>
	</td>
	
	<td width="150">
	<div class="nodeEdit">
		<#if (post.permissions.edit)>
	        <div class="onEditNode" id="onEditNode-${post.name}">
	           <a href="#" class="action-link-${post.name}">${msg("post.action.edit")}</a>
	        </div>
        </#if>
        
		<#if (post.permissions.publishExt)>
			<#if post.isPublished>
				<#if post.outOfDate>
			    <div class="onUpdateExternal" id="onUpdateExternal-${post.name}">
	    	       <a href="#" class="action-link-${post.name}">${msg("post.action.updateexternal")}</a>
	            </div>
                </#if>
			    <div class="onUnpublishExternal" id="onUnpublishExternal-${post.name}">
	    	       <a href="#" class="action-link-${post.name}">${msg("post.action.unpublishexternal")}</a>
	            </div>
	        <#else>
		    <div class="onPublishExternal" id="onPublishExternal-${post.name}">
    	       <a href="#" class="action-link-${post.name}">${msg("post.action.publishexternal")}</a>
            </div>
            </#if>
        </#if>
        
		<#if (post.permissions.delete)>
	        <div class="onDeleteNode" id="onDeleteNode-${post.name}">
	           <a href="#" class="action-link-${post.name}">${msg("post.action.delete")}</a>
	        </div>
	    </#if>

	</div>
	</td>
	</tr>
	</table>
	
    <div class="nodeFooter">
	  	<span class="nodeFooterBloc">
			<span class="nodeAttrLabel replyTo">${msg("post.footer.comments")}:</span><span class="nodeAttrValue"> (${post.commentCount})</span>
		</span> 
		
		<#if (post.tags?size > 0)>
		<span class="spacer"> | </span>
		
			<span class="nodeFooterBloc">
				<span class="nodeAttrLabel tag">${msg("post.tags")}:</span>
				<#list post.tags as tag>
					<span class="nodeAttrValue"><a href="">${tag}</a></span><#if tag_has_next> , </#if> 
				</#list>
			</span>
		</#if> 
  	</div>
  </div>
</#macro>


<#--
  Returns a comma-separated string of the elements in the passed array-
  @param elems an array with the elements to concat
  @return a comma separated string of the elements in the array
-->
<#function concatArray elems>
  <#assign res><#list elems as x>${x}<#if x_has_next>, </#if></#list></#assign>
  <#return res>
</#function>


<#--
  Renders a form to edit a post.
  
  @param form-id The form id to use
  @param post The post data to insert into the form.
               Can be empty in which case the form will contain no data.
-->
<#macro blogpostFormHTML htmlId post="">
<div class="editNodeForm">
	<form id="${htmlId}-form" name="${htmlId}-form" method="POST"
        <#if post?has_content>
            action="${page.url.context}/proxy/alfresco/blog/post/site/${site}/${container}/${post.name}?alf_method=PUT" 
        <#else>
            action="${page.url.context}/proxy/alfresco/blog/site/${site}/${container}/posts" 
        </#if>
    >
    	<input type="hidden" name="site" value="${site}">
    	<input type="hidden" name="draft" value="false">
	    <#if post?has_content>
	      <input type="hidden" name="postId" value="${post.name}" >
	    </#if>
	    
		<label>${msg("post.form.postTitle")}:</label>
		<input type="text" value="<#if post?has_content && post.title?has_content>${post.title}</#if>"
		       name="title" id="${htmlId}-title" size="80" />
		<label>${msg("post.form.postText")}:</label>
		<textarea rows="8" cols="80" name="content" id="${htmlId}-content"
		   ><#if post?has_content && post.content?has_content>${post.content}</#if></textarea> 
		<label>${msg("post.tags")}:</label>
		<input type="text" name="tags" id="${htmlId}-tags" size="80" 
		    <#if post?has_content && post.tags?has_content>value="${concatArray(post.tags)}"</#if> />
		<label>${msg("post.form.suggested")}:</label>
		<div class="suggestedTags">
			<ul>
				<li><a href="">ECM</a></li>
				<li><a href="">Computer</a></li>
				<li><a href="">Brand</a></li>
				<li><a href="">Trend</a></li>
			</ul>
			<br />
			<a href="">${msg("post.form.viewTagLibrary")}</a>
		</div>
		<div class="nodeFormAction">
			<input type="submit" id="${htmlId}-ok-button" value="<#if post?has_content>${msg('post.form.save')}<#else>${msg('post.form.create')}</#if>" />
			<input type="reset" id="${htmlId}-cancel-button" value="${msg('post.form.cancel')}" />
		</div>
	</form>
</div>
</#macro>
