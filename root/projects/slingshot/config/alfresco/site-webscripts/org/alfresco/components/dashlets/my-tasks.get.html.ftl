<#macro doclibUrl doc>
   <#if ((doc.location.site?exists) && (doc.location.site != ""))>
   <a href="${url.context}/page/site/${doc.location.site}/documentlibrary?file=${doc.fileName?url}#path=${doc.location.path?url}">${doc.displayName?html}</a>
   <#else>
   ${doc.displayName?html}
   </#if>
</#macro>
<script type="text/javascript">//<![CDATA[
   new Alfresco.MyTasks("${args.htmlid}").setMessages(
      ${messages}
   );
//]]></script>

<div class="dashlet my-tasks">
   <div class="title">${msg("header")}</div>
   <div class="toolbar">
      <span class="all"><a href="#" class="task-filter">${msg("filter.all")}</a></span>
      <span class="separator">|</span>
      <span class="dueOn">
         <a href="#" class="task-filter">
            <span id="${args.htmlid}-filter-dueOn">${msg("filter.due-on", msg("filter.due-on.today"))}</span>
            <img src="${url.context}/yui/assets/skins/${theme}/menu-button-arrow.png" alt="expand" />
         </a>
      </span>
      <span class="separator">|</span>
      <span class="overdue"><a href="#" class="task-filter">${msg("filter.overdue")}</a></span>
      <span class="separator">|</span>
      <span class="no-due-date"><a href="#" class="task-filter">${msg("filter.no-due-date")}</a></span>
      <div id="${args.htmlid}-dueDate-menu" class="yuimenu">
         <div class="bd">
            <ul class="first-of-type">
               <li class="yuimenuitem today">
                  <a class="yuimenuitemlabel" href="#">${msg("filter.due-on.today")}</a>
               </li>
               <li class="yuimenuitem tomorrow">
                  <a class="yuimenuitemlabel" href="#">${msg("filter.due-on.tomorrow")}</a>
               </li>
               <li class="yuimenuitem this-week">
                  <a class="yuimenuitemlabel" href="#">${msg("filter.due-on.this-week")}</a>
               </li>
               <li class="yuimenuitem next-week">
                  <a class="yuimenuitemlabel" href="#">${msg("filter.due-on.next-week")}</a>
               </li>
            </ul>
         </div>
      </div>
   </div>
   <div id="${args.htmlid}-taskList" class="body scrollableList">
   </div>
</div>