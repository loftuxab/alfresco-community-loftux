<#--
   Renders the status of a post
-->
<#macro renderPostStatus post>
    <#if post.isDraft>
		<span class="nodeStatus">
			(${msg("post.draft")})
		</span>       
	<#elseif post.isUpdated || post.isPublished>
		<span class="nodeStatus">
			(${msg("post.updated")})
			<#if post.isPublished>
				<#if post.outOfDate>
					(${msg("post.published.outofsync")})
				<#else>
					(${msg("post.published")})
				</#if>
			</#if>
		</span>
	</#if>
</#macro>


<#--
  Renders a list of posts
-->
<#macro postListHTML posts viewmode="details">
    <#if (posts?size > 0)>
		<#list posts as post>
			<#switch viewmode>
			<#case "simple">
				<@renderSimplePostEntry post=post/>
				<#break>
			<#case "details">
			<#default>
				<@renderDetailedPostEntry post=post/>
			</#switch>
		</#list>
	<#else>
        <div class="noNode">${msg("post.noPost")}</div>
	</#if>
</div>
</#macro>


<#--
	Detailed post entry
--> 
<#macro renderDetailedPostEntry post>
  <div id="${post.name}" class="node post">
  
	<div class="nodeEdit">
		<#if (post.permissions.edit)>
	        <div class="onEditNode" id="onEditNode-${post.name}">
	           <a href="#" class="action-link">${msg("post.action.edit")}</a>
	        </div>
        </#if>
        
		<#if (post.permissions.publishExt && ! post.isDraft)>
			<#if post.isPublished>
				<#if post.outOfDate>
			    <div class="onUpdateExternal" id="onUpdateExternal-${post.name}">
	    	       <a href="#" class="action-link">${msg("post.action.updateexternal")}</a>
	            </div>
                </#if>
			    <div class="onUnpublishExternal" id="onUnpublishExternal-${post.name}">
	    	       <a href="#" class="action-link">${msg("post.action.unpublishexternal")}</a>
	            </div>
	        <#else>
		    <div class="onPublishExternal" id="onPublishExternal-${post.name}">
    	       <a href="#" class="action-link">${msg("post.action.publishexternal")}</a>
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
		<span class="nodeTitle">
			<a href="blog-postview?postId=${post.name}">
				${post.title}
			</a>
			<@renderPostStatus post=post/>
		</span>
		<div class="published">
			<span class="nodeAttrLabel">${msg("post.info.createdOn")}:</span> <span class="nodeAttrValue"> ${post.createdOn?datetime?string.medium_short}</span>
			<span class="spacer"> | </span>
			<span class="nodeAttrLabel">${msg("post.info.author")}:</span><span class="nodeAttrValue"><a href=""> ${post.author}</a></span>
		</div>
		
		<div class="content">${post.content}</div>
	</div>
	
	
  
  </div>
  <div class="nodeFooter">
  	<span class="nodeFooterBloc">
		<span class="nodeAttrLabel replyTo">${msg("post.footer.replies")}:</span><span class="nodeAttrValue"> (${post.commentCount})</span>
		<span class="nodeAttrValue"><a href="blog-postview?postId=${post.name}">${msg("post.footer.read")}</a></span>
	</span> 

	<#if (post.tags?size > 0)>
		<span class="nodeFooterBloc">
			<span class="nodeAttrLabel tag">${msg("post.tags")}:</span>
			<#list post.tags as tag>
				<span class="nodeAttrValue"><a href="">${tag}</a></span><#if tag_has_next> , </#if> 
			</#list>
		</span>
	</#if> 
  </div>
</#macro>


<#--
	Simple post entry
-->
<#macro renderSimplePostEntry post>
  <div id="${post.name}" class="node post simple">
  
  
  <div class="nodeEdit">
    <span class="onViewNode">
      <a id="comment-${post.name}" class="addEventComment" href="">
        ${msg("post.action.addComment")}
      </a>
    </span>
    <span class="spacer"> | </span>
    
    <#if (post.permissions.edit)>
      <span class="onEditNode">
        <a id="edit-${post.name}" class="editEventPost" href="blog-postview?postId=${post.name}&edit=true">
          ${msg("post.action.edit")}
        </a>
      </span>
      <span class="spacer"> | </span>
    </#if>
    <#if (post.permissions.delete)>
      <span class="onDeleteNode">
        <a id="delete-${post.name}" class="deleteEventPost" href="">
          ${msg("post.action.delete")}
        </a>
      </span>
    </#if>
  </div>
  
	<div class="nodeContent">
		<div class="nodeTitle">
			<a href="blog-postview?postId=${post.name}">
				${post.title}
			</a>
		</div>
	</div>
  
  </div>
</#macro>
