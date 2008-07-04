<#import "/org/alfresco/modules/blog/blogpostlist.lib.ftl" as blogpostlistLib/>
<#import "/org/alfresco/modules/paginator/paginator.lib.ftl" as paginatorLib/>
<#assign listHtml><@blogpostlistLib.postListHTML posts=items viewmode=viewmode /></#assign>
<#assign paginatorHtml><@paginatorLib.renderForumPaginator pdata=pdata /></#assign>
<#assign listTitle><@blogpostlistLib.postListTitle filter=filter fromDate=fromDate toDate=toDate tag=tag /></#assign>
{
   "listHtml" : "${listHtml?j_string}",
   "paginatorHtml" : "${paginatorHtml?j_string}",
   "listTitle" : "${listTitle?trim?j_string}",
   "startIndex" : ${pdata.startIndex}
}
