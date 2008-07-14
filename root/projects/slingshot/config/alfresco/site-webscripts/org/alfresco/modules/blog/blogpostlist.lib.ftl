
<#--
  Renders the title of the list
-->
<#macro postListTitle filter="" fromDate="" toDate="" tag="">
   <#if (filter?length > 0 && filter != "all")>
      <#if filter == "new">
         ${msg("postlist.title.newposts")}
      <#elseif (filter == "mydrafts")>
         ${msg("postlist.title.mydrafts")}
      <#elseif (filter == "mypublished")>
         ${msg("postlist.title.mypublished")}
      <#elseif (filter == "publishedext")>
         ${msg("postlist.title.publishedext")}
      <#else>
         Unknown filter!
      </#if>
   <#elseif (tag?length > 0)>
      ${msg("postlist.title.bytag", tag)}
   <#elseif (fromDate?is_date)>
      ${msg("postlist.title.bymonth", fromDate?datetime?string("MMMM yyyy"))}
   <#else>
      ${msg("postlist.title.allposts")}
   </#if>
</#macro>


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
<#macro postListHTML htmlid posts viewmode="details">
<#if (posts?size > 0)>
   <#list posts as post>
      <#switch viewmode>
      <#case "simple">
         <@renderSimplePostEntry htmlid post />
         <#break>
      <#case "details">
      <#default>
         <@renderDetailedPostEntry htmlid post />
      </#switch>
   </#list>
<#else>
   <div class="noNode">${msg("postlist.noPosts")}</div>
</#if>

<#-- PENDING: find why we have to add this! -->
</div>
</#macro>


<#--
   Detailed post entry
--> 
<#macro renderDetailedPostEntry htmlid post>
<div id="${post.name}" class="node post">
  
   <div class="nodeEdit">
      <#if (post.permissions.edit)>
      <div class="onEditNode" id="${htmlid}-onEditNode-${post.name}">
         <a href="#" class="action-link-div">${msg("post.action.edit")}</a>
      </div>
      </#if>
        
      <#if (post.permissions.publishExt && ! post.isDraft)>
         <#if post.isPublished>
            <#if post.outOfDate>
            <div class="onUpdateExternal" id="${htmlid}-onUpdateExternal-${post.name}">
               <a href="#" class="action-link-div">${msg("post.action.updateexternal")}</a>
            </div>
            </#if>
            
            <div class="onUnpublishExternal" id="${htmlid}-onUnpublishExternal-${post.name}">
               <a href="#" class="action-link-div">${msg("post.action.unpublishexternal")}</a>
            </div>
         <#else>
         <div class="onPublishExternal" id="${htmlid}-onPublishExternal-${post.name}">
            <a href="#" class="action-link-div">${msg("post.action.publishexternal")}</a>
         </div>
         </#if>
      </#if>
        
      <#if (post.permissions.delete)>
      <div class="onDeleteNode" id="${htmlid}-onDeleteNode-${post.name}">
         <a href="#" class="action-link-div">${msg("post.action.delete")}</a>
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
<#macro renderSimplePostEntry htmlid post>
<div id="${post.name}" class="node post simple">
  
   <div class="nodeEdit">
   
      <#if (post.permissions.edit)>
      <span class="onEditNode" id="${htmlid}-onEditNode-${post.name}">
         <a href="#" class="action-link-span">${msg("post.action.edit")}</a>
      </span>
      </#if>
       
      <#if (post.permissions.publishExt && ! post.isDraft)>
         <#if post.isPublished>
            <#if post.outOfDate>
            <span class="onUpdateExternal" id="${htmlid}-onUpdateExternal-${post.name}">
               <a href="#" class="action-link-span">${msg("post.action.updateexternal")}</a>
            </span>
            </#if>
            
            <span class="onUnpublishExternal" id="${htmlid}-onUnpublishExternal-${post.name}">
               <a href="#" class="action-link-span">${msg("post.action.unpublishexternal")}</a>
            </span>
         <#else>
         <span class="onPublishExternal" id="${htmlid}-onPublishExternal-${post.name}">
            <a href="#" class="action-link-span">${msg("post.action.publishexternal")}</a>
         </span>
         </#if>
      </#if>
       
      <#if (post.permissions.delete)>
         <span class="onDeleteNode" id="${htmlid}-onDeleteNode-${post.name}">
            <a href="#" class="action-link-span">${msg("post.action.delete")}</a>
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
