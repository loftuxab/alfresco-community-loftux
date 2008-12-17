<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPostEdit("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site}",
      containerId: "blog",
      <#if page.url.args.postId??>
         editMode: true,
         postId: "${page.url.args.postId?html}"
      <#else>
         editMode: false,
         postId: ""
      </#if>
   }).setMessages(
      ${messages}
   );
//]]></script>

<#if page.url.args.postId??>
   <h1>${msg("editPost")}</h1>
<#else>
   <h1>${msg("createPost")}</h1>
</#if>

<div id="${args.htmlid}-div" class="editBlogPostForm hidden">
   <form id="${args.htmlid}-form" method="post" action="">
      <div>
         <input type="hidden" id="${args.htmlid}-site" name="site" value="" />
         <input type="hidden" id="${args.htmlid}-container" name="container" value="" />
         <input type="hidden" id="${args.htmlid}-page" name="page" value="blog-postview" />
         <input type="hidden" id="${args.htmlid}-draft" name="draft" value=""/>
               
         <!-- title -->
         <label for="${args.htmlid}-title">${msg("title")}:</label>
         <input type="text" id="${args.htmlid}-title" name="title" value="" />

         <!-- content -->
         <label for="${args.htmlid}-content">${msg("text")}:</label>
         <textarea rows="8" id="${args.htmlid}-content" name="content" cols="180" class="yuieditor"></textarea> 
      
         <!-- tags -->
         <label for="${htmlid}-tag-input-field">${msg("tags")}:</label>
         <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
         <@taglibraryLib.renderTagLibraryHTML htmlid=args.htmlid />
         <!-- end tags -->

      </div>
      <div class="nodeFormAction">
         <input type="submit" id="${args.htmlid}-save-button" value="" />         
         <input type="button" id="${args.htmlid}-publish-button" value="${msg('action.publish')}" class="hidden" />
         <input type="button" id="${args.htmlid}-publishexternal-button" value="" />
         <input type="reset" id="${args.htmlid}-cancel-button" value="${msg('action.cancel')}" />
      </div>
   </form>
</div>
