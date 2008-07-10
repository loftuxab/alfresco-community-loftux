<#import "/org/alfresco/modules/discussions/topic.lib.ftl" as topicLib/>
<#assign html><@topicLib.topicViewHTML htmlid=htmlid topic=item/></#assign>
{
   "topic" : "${html?j_string}",
   "topicId" : "${item.name}"
}
