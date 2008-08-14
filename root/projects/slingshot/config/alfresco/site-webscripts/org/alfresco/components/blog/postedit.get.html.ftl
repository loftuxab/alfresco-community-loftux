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
   <h1>${msg("postedit.editpost")}</h1>
<#else>
   <h1>${msg("postedit.createpost")}</h1>
</#if>

<div id="${args.htmlid}-container" class="editBlogPostForm hidden">
   <form id="${args.htmlid}-form" method="post" action="">
      <div>
         <input type="hidden" id="${args.htmlid}-site" name="site" value="" />
         <input type="hidden" id="${args.htmlid}-container" name="container" value="" />
         <input type="hidden" id="${args.htmlid}-browsePostUrl" name="browsePostUrl" value="" />
         <input type="hidden" id="${args.htmlid}-draft" name="draft" value=""/>
               
         <!-- title -->
         <label>${msg("post.form.postTitle")}:</label>
         <input type="text" id="${args.htmlid}-title" name="title" size="180" value="" />

         <!-- content -->
         <label>${msg("post.form.postText")}:</label>
         <textarea rows="8" id="${args.htmlid}-content" name="content" cols="180" class="yuieditor"></textarea> 
      
         <!-- tags -->
         <label>${msg("post.tags")}:</label>
         <#assign tags=[] />
         <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
         <!-- Render the tag inputs -->
         <@taglibraryLib.renderTagInputs htmlid=args.htmlid tags=tags tagInputName="tags" />
         <!-- Render the library component -->
         <@taglibraryLib.renderTagLibrary htmlid=args.htmlid site=page.url.templateArgs.site tags=tags />
         <!-- end tags -->

      </div>
      <div class="nodeFormAction">
         <input type="submit" id="${args.htmlid}-save-button" value="" />         
         <input type="button" id="${args.htmlid}-publish-button" value="${msg('post.form.publish')}" class="hidden" />
         <input type="button" id="${args.htmlid}-publishexternal-button" value="" />
         <input type="reset" id="${args.htmlid}-cancel-button" value="${msg('post.form.cancel')}" />
      </div>
   </form>
</div>
