<#import "/org/alfresco/modules/blog/comments.lib.ftl" as commentsLib/>
<#assign html><@commentsLib.commentHTMLContent htmlid=htmlid comment=item /></#assign>
{
   "html" : "${html?j_string}",
   "nodeRef" : "${item.nodeRef}"
}