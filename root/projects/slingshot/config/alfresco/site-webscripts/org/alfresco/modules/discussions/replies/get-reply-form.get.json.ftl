<#import "/org/alfresco/modules/discussions/replies.lib.ftl" as repliesLib/>
<#assign html><@repliesLib.replyFormHTML htmlid=htmlid post=item isEdit=isEdit /></#assign>
{
   "form" : "${html?j_string}"
}