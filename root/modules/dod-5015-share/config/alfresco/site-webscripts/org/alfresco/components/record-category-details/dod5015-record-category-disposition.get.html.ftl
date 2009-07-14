<script type="text/javascript">//<![CDATA[

   new Alfresco.RecordCategoryDisposition("${args.htmlid}").setOptions({      
   }).setMessages(
      ${messages}
   );

//]]></script>

<div class="dod5015-record-category-disposition">

   <div class="heading">${msg("disposition-schedule.heading")}</div>

   <div>
      <div id="${args.htmlid}-actions" class="actions">
         <#if (actions?size > 0)>
         <#list actions as action>
         <div class="action">
            <div class="no">${action.index}</div>
            <div class="more collapsed"><a href="#">${msg("link.description")}</a></div>
            <div class="name">${action.title}</div>
            <div class="description" style="display: none;">${action.description}</div>
         </div>
         </#list>
         <#else>
         ${msg("label.noactions")}
         </#if>
      </div>
   </div>

</div>