<#import "/org/alfresco/modules/blog/blogpostlist.lib.ftl" as blogpostlistLib/>
<#import "/org/alfresco/modules/paginator/paginator.lib.ftl" as paginatorLib/>
<#assign listHtml><@blogpostlistLib.postListHTML htmlid=htmlid posts=items viewmode=viewmode /></#assign>
<#assign listTitle><@blogpostlistLib.postListTitle filter=filter fromDate=fromDate toDate=toDate tag=tag /></#assign>
{
   "listHtml" : "${listHtml?j_string}",
   "listTitle" : "${listTitle?trim?j_string}",
   "paginatorData" : <@paginatorLib.getPaginatorUpdateJSONData htmlid=htmlid pdata=pdata />
}
