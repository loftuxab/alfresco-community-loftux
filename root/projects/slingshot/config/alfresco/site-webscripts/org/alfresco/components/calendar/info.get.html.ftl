<!-- Event Info Panel -->
<script type="text/javascript">//<![CDATA[
   Alfresco.util.addMessages(${messages}, "Alfresco.EventInfo");
//]]></script>
<div class="hd">${msg("label.eventinfo")}</div>
<div class="bd">
   <div class="yui-g">
      <h2>${msg("label.details")}</h2>
   </div>
   <div class="yui-gd">
      <div class="yui-u first">${msg("label.what")}: *</div>
      <div class="yui-u">${result.what?html!""}</div>
   </div>
   <div class="yui-gd">
      <div class="yui-u first">${msg("label.location")}:</div>
      <div class="yui-u">${result.location?html!""}</div>
   </div>
   <div class="yui-gd">
      <div class="yui-u first">${msg("label.description")}:</div>
      <div class="yui-u">${result.description?html!""}</div>
   </div>
   <div class="yui-gd">
      <div class="yui-u first">${msg("label.tags")}:</div>
      <div class="yui-u">
<#if result.tags?? && result.tags?size &gt; 0>
   <#list result.tags as tag>${tag}<#if tag_has_next>&nbsp;</#if></#list>
<#else>
    ${msg("label.none")}
</#if>
      </div>
   </div>
   <div class="yui-g">
      <h2>${msg("label.time")}</h2>
   </div>
<#if result.allday=='true'>
   <div class="yui-gd">
      <div class="yui-u first">&nbsp;</div>
      <div class="yui-u">${msg("label.allday")}</div>
   </div>
</#if>
   <div class="yui-gd">
      <div class="yui-u first">${msg("label.startdate")}:</div>
      <div class="yui-u" id="${args.htmlid}-startdate"><#if result.from?exists>${result.from?date("MM/dd/yyy")?string("yyyy-MM-dd")}</#if><#if result.allday!='true'> ${msg("label.at")} ${result.start!""} </#if></div>
   </div>
   <div class="yui-gd">
      <div class="yui-u first">${msg("label.enddate")}:</div>
      <div class="yui-u" id="${args.htmlid}-enddate"><#if result.to?exists>${result.to?date("MM/dd/yyy")?string("yyyy-MM-dd")}</#if><#if result.allday!='true'> ${msg("label.at")} ${result.end!""} </#if></div>
   </div>
<#if result.recurrence!=''>
   <div class="yui-gd">
      <div class="yui-u first">${msg("label.recurrence")}:</div>
      <div class="yui-u">${result.recurrence}</div>
   </div>
</#if>
   <div class="yui-g">
      <h2>${msg("label.documents")}</h2>
   </div>
   <div class="yui-gd">
      <div class="yui-u first">${msg("label.docfolder")}:</div>
      <div class="yui-u docfolder-label" id="${args.htmlid}-docfolder">${result.docfolder}</div>
   </div>
   <br />
   <div class="bdft">
      <input type="submit" id="${args.htmlid}-edit-button" value="${msg("button.edit")}" />
      <input type="submit" id="${args.htmlid}-delete-button" value="${msg("button.delete")}" />
      <input type="submit" id="${args.htmlid}-cancel-button" value="${msg("button.cancel")}" />
   </div>
   <#if result.isoutlook == 'false'>
   <div id="${args.htmlid}-edit-available" />
   </#if>
</div>