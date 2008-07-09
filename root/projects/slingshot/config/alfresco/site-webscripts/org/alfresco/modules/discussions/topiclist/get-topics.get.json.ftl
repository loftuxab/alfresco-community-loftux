<#import "/org/alfresco/modules/discussions/topiclist.lib.ftl" as topiclistLib/>
<#import "/org/alfresco/modules/paginator/paginator.lib.ftl" as paginatorLib/>
<#assign listHtml><@topiclistLib.topicListHTML htmlid=htmlid topics=items viewmode=viewmode /></#assign>
<#assign listTitle><@topiclistLib.topicListTitle filter=filter tag=tag /></#assign>
{
   "listHtml" : "${listHtml?j_string}",
   "listTitle" : "${listTitle?j_string}",
   "paginatorData" : <@paginatorLib.getPaginatorUpdateJSONData htmlid=htmlid pdata=pdata />
}
