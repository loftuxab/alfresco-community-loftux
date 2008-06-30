<#import "/org/alfresco/modules/blog/blogpostlist.lib.ftl" as blogpostlistLib/>
<#import "/org/alfresco/modules/paginator/paginator.lib.ftl" as paginatorLib/>
<#assign listHtml><@blogpostlistLib.postListHTML posts=items viewmode=viewmode /></#assign>
<#assign paginatorHtml><@paginatorLib.renderForumPaginator pdata=pdata /></#assign>
{
   "listHtml" : "${listHtml?j_string}",
   "paginatorHtml" : "${paginatorHtml?j_string}",
   "listTitle" : "${listTitle}",
   "startIndex" : ${pdata.startIndex}
}
