<div id="${args.htmlid}-dialog" class="create-event">
<#if (!edit)>
   <div class="hd">${msg("title.addEvent")}</div>
<#else>
    <div class="hd">${msg("title.editEvent")}</div>
</#if>
   <div class="bd">

      <form id="${args.htmlid}-form" action="${url.context}/proxy/alfresco/calendar/create" method="POST">
         <input type="hidden" name="site" value="${args.site!""}" />
         <input type="hidden" name="page" value="calendar" />
         <input type="hidden" id="${args.htmlid}-from" name="from" value="${event.from!""}" />
         <input type="hidden" id="${args.htmlid}-to" name="to" value="${event.to!""}" />
         <div class="yui-g">
            <h2>${msg("section.details")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-title">${msg("label.what")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-title" type="text" name="what" value="${event.what?html}" tabindex="0" class="wide"/> * </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-location">${msg("label.where")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-location" type="text" name="where" value="${event.location?html}" tabindex="0" class="wide"/></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-description">${msg("label.description")}:</label></div>
            <div class="yui-u"><textarea id="${args.htmlid}-description" name="desc" rows="3" cols="20" class="wide" tabindex="0">${event.description?html}</textarea></div>
         </div>
         <div class="yui-g">
            <h2>${msg("section.time")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-allday">${msg("label.allday")}:</label></div>
            <#if (edit && event.allday=='true')>
            <div class="yui-u"><input id="${args.htmlid}-allday" type="checkbox" name="allday" tabindex="0" checked="checked"/></div>
            <#else>
            <div class="yui-u"><input id="${args.htmlid}-allday" type="checkbox" name="allday" tabindex="0"/></div>
            </#if>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="fd">${msg("label.startdate")}:</label></div>
            <div class="yui-u overflow"><span id="${args.htmlid}-startdate"><input id="fd" type="text" name="fromdate" readonly="readonly"  value="<#if event.from?exists>${event.from?date("MM/dd/yyy")?string("EEEE, MMMM dd yyyy")}</#if>" disabled /></span><span id="${args.htmlid}-starttime" class="eventTime">&nbsp;<label for="${args.htmlid}-start">${msg("label.at")}&nbsp;</label><input id="${args.htmlid}-start" name="start" value="${event.start!"12:00"}" type="text" size="10" tabindex="0" /></span></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="td">${msg("label.enddate")}:</label></div>
            <div class="yui-u overflow"><span id="${args.htmlid}-enddate"><input id="td" type="text" name="todate" readonly="readonly"  value="<#if event.to?exists>${event.to?date("MM/dd/yyy")?string("EEEE, MMMM dd yyyy")}</#if>" disabled /></span><span id="${args.htmlid}-endtime" class="eventTime"><label for="${args.htmlid}-end">${msg("label.at")}&nbsp;</label><input id="${args.htmlid}-end" name="end" value="${event.end!"13:00"}" type="text" size="10" tabindex="0" /></span></div>
         </div>
         <!-- tags -->
         <div class="yui-gd">
            <div class="yui-u first">${msg("label.tags")}:</div>
            <div class="yui-u overflow">
              <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
               <div class="taglibrary">
                  <div class="top_taglist tags_box">
                     <ul id="${args.htmlid}-current-tags">
                     </ul>
                  </div>
                  <#assign tags = ''>
                  <#if event.tags?? && event.tags?size &gt; 0>
                     <#list event.tags as tag>
                        <#assign tags = tags + tag>
                        <#if tag_has_next><#assign tags = tags + ' '></#if>
                     </#list>
                  </#if>
                  <input type="text" size="30" class="rel_left" id="${args.htmlid}-tag-input-field" value="${tags}"/>
                  <input type="button" id="${args.htmlid}-add-tag-button" value="${msg("button.add")}" />
                  <div class="bottom_taglist tags_box">
                     <a href="#" id="${args.htmlid}-load-popular-tags-link">${msg("taglibrary.populartagslink")}</a>
                     <ul id="${args.htmlid}-popular-tags">
                     </ul>
                  </div>
               </div>
               <!-- end tags -->                    
            </div>
         </div>
         <div class="yui-g">
            <h2>${msg("section.documents")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-docfolder">${msg("label.docfolder")}:</label></div>
            <div class="yui-u" >
               <input type="text" id="${args.htmlid}-docfolder" name="docfolder" value="${event.docfolder?html}" class="docfolder-input" readonly="true" />
               <input type="button" id="${args.htmlid}-browse-button" value="${msg("label.browse")}" />
            </div>
         </div>	 
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="submit" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
        <#if edit && event.isoutlook == 'false'>
        <div name="edit-available" id="${args.htmlid}-edit-available" />
        </#if>
      </form>

   </div>
</div>
