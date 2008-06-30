<#import "/org/alfresco/modules/discussions/topic.lib.ftl" as topicLib/>
<#assign html><@topicLib.topicFormHTML htmlId=htmlId topic=item!""/></#assign>
{
   "form" : "${html?j_string}"
}
