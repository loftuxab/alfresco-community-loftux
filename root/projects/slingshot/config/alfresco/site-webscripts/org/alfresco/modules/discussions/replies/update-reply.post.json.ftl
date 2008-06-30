<#import "/org/alfresco/modules/discussions/replies.lib.ftl" as repliesLib/>
<#assign html><@repliesLib.replyHTMLContent htmlid=htmlid reply=item /></#assign>
{
   "reply" : "${html?j_string}",
   "replyNodeRef" : "${item.nodeRef}"
}