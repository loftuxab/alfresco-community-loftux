<#import "/org/alfresco/modules/blog/blogpost.lib.ftl" as blogpostLib/>

<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPost("${args.htmlid}").setOptions(
   {
      siteId: "${site}",
      containerId: "${container}",
      <#if item??>
      mode: "edit",
      postId: "${item.name?html?j_string}"
      <#else>
      mode: "create"
      </#if>
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-post">
   <div id="${args.htmlid}-formDiv">
     <#if item??>
        <@blogpostLib.blogpostFormHTML htmlId="${args.htmlid}" post=item/>
     <#else>
        <@blogpostLib.blogpostFormHTML htmlId="${args.htmlid}"/>
     </#if>
   </div>
</div>