<#import "/org/alfresco/modules/discussions/topiclist.lib.ftl" as topiclistLib/>
<#import "/org/alfresco/modules/paginator/paginator.lib.ftl" as paginatorLib/>
<#assign listHtml><@topiclistLib.topicListHTML topics=items viewmode=viewmode /></#assign>
<#assign paginatorHtml><@paginatorLib.renderForumPaginator pdata=pdata /></#assign>
{
   "listHtml" : "${listHtml?j_string}",
   "paginatorHtml" : "${paginatorHtml?j_string}",
   "listTitle" : "${listTitle}",
   "startIndex" : ${pdata.startIndex}
}
