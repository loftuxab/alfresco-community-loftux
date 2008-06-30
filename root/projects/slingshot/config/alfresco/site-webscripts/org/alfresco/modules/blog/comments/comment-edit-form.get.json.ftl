   <#import "/org/alfresco/modules/blog/comments.lib.ftl" as commentsLib/>
   <#assign html><@commentsLib.editCommentFormHTML htmlid=htmlid comment=item /></#assign>
{
   "form" : "${html?j_string}"
}