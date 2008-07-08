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
        
	<div class="nodeEdit">
		<#if (post.permissions.edit)>
	        <div class="onEditNode" id="onEditNode-${post.name}">
	           <a href="#" class="action-link-${post.name}">${msg("post.action.edit")}</a>
	        </div>
        </#if>
        
		<#if (post.permissions.publishExt && ! post.isDraft)>
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
	           <a href="#" class="action-link">${msg("post.action.delete")}</a>
	        </div>
	    </#if>
	</div>
  
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
	<br clear="all" />
	
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
    <#assign editForm = post?has_content>
    <#assign isDraft = (! post?has_content) || (post.isDraft)>
    <#assign isPublished = post?has_content && post.isPublished>
	<form id="${htmlId}-form" name="${htmlId}-form" method="POST"
        <#if editForm>
            action="${page.url.context}/proxy/alfresco/blog/post/site/${site}/${container}/${post.name}" 
        <#else>
            action="${page.url.context}/proxy/alfresco/blog/site/${site}/${container}/posts" 
        </#if>
    >
    	<input type="hidden" name="site" id="${htmlId}-site" value="${site}" />
    	<input type="hidden" name="draft" id="${htmlId}-draft" value="<#if isDraft>true<#else>false</#if>">
	    <#if editForm>
	      <input type="hidden" name="postId" id="${htmlId}-postId" value="${post.name}" />
	    </#if>
        <span id="${htmlId}-tags-inputs">
          <#if editForm>
            <#list post.tags as tag>
              <input type="hidden" name="tags[]" value="${tag}" />
            </#list>
          </#if>
        </span>
        	    
   		<!-- title -->
		<label>${msg("post.form.postTitle")}:</label>
		<input type="text" value="<#if editForm>${post.title!''}</#if>"
		       name="title" id="${htmlId}-title" size="80" />

		<!-- content -->
		<label>${msg("post.form.postText")}:</label>
		<textarea rows="8" cols="80" name="content" id="${htmlId}-content"><#if editForm>${post.content!''}</#if></textarea> 
		
		<!-- tags -->
		<#if editForm>
			<#assign tags=post.tags />
		<#else>
			<#assign tags=[] />
		</#if>
		<#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
		<!-- Render the tag inputs -->
		<@taglibraryLib.renderTagInputs htmlid=htmlId tags=tags tagInputName="tags" />
		<!-- Render the library component -->
		<@taglibraryLib.renderTagLibrary htmlid=htmlId tags=tags />
		 
		<div class="nodeFormAction">
			<input type="submit" id="${htmlId}-save-button" value="<#if editForm>${msg('post.form.update')}<#else>${msg('post.form.saveAsDraft')}</#if>" />			
			<#if isDraft>
			<input type="button" id="${htmlId}-publish-button" value="${msg('post.form.publish')}" />
			</#if>
			<#assign updateExt><#if isDraft>${msg('post.form.publishIntAndExt')}<#else><#if isPublished>${msg('post.form.updateIntAndExt')}<#else>${msg('post.form.updateIntAndPublishExt')}</#if></#if></#assign>
			<input type="button" id="${htmlId}-publishexternal-button" value="${updateExt}" />
			<input type="reset" id="${htmlId}-cancel-button" value="${msg('post.form.cancel')}" />
		</div>
	</form>
</div>
</#macro>

<#--
<#macro blogpostFormHTML htmlId post="">
<div class="editNodeForm">
	<form id="${htmlId}-form" name="${htmlId}-form" method="POST"
        <#if post?has_content>
            action="${page.url.context}/proxy/alfresco/blog/post/site/${site}/${container}/${post.name}" 
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
		<br />
		<label>${msg("post.form.postText")}:</label>
		<textarea rows="8" cols="80" name="content" id="${htmlId}-content"
		   ><#if post?has_content && post.content?has_content>${post.content}</#if></textarea> 
		<br />
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
			<input type="submit" id="${htmlId}-save-button" value="<#if post?has_content>${msg('post.form.save')}<#else>${msg('post.form.create')}</#if>" />
			<input type="submit" id="${htmlId}-save-button" value="<#if post?has_content>${msg('post.form.save')}<#else>${msg('post.form.create')}</#if>" />
			<input type="reset" id="${htmlId}-cancel-button" value="${msg('post.form.cancel')}" />
		</div>
	</form>
</div>
</#macro>
-->