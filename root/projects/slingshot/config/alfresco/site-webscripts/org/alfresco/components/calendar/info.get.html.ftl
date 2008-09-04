<!-- Event Info Panel -->
<div class="hd">${msg("label.eventinfo")}</div>
<div class="bd">
   <div class="yui-g">
      <h2>${msg("label.details")}</h2>
   </div>
   <div class="yui-gd">
      <div class="yui-u first">${msg("label.what")}: *</div>
      <div class="yui-u">${result.what!""}</div>
   </div>
   <div class="yui-gd">
      <div class="yui-u first">${msg("label.location")}:</div>
      <div class="yui-u">${result.location!""}</div>
   </div>
   <div class="yui-gd">
      <div class="yui-u first">${msg("label.description")}:</div>
      <div class="yui-u">${result.description!""}</div>
   </div>
   <div class="yui-gd">
      <div class="yui-u first">${msg("label.tags")}:</div>
      <div class="yui-u">
<#if result.tags??>
   <#list result.tags as tag>${tag}<#if tag_has_next>&nbsp;</#if></#list>
</#if>
      </div>
   </div>
   <div class="yui-g">
      <h2>${msg("label.time")}</h2>
   </div>
<#if result.allday?exists>
   <div class="yui-gd">
      <div class="yui-u first">&nbsp;</div>
      <div class="yui-u">${msg("label.allday")}</div>
   </div>
</#if>
   <div class="yui-gd">
      <div class="yui-u first">${msg("label.startdate")}:</div>
      <div class="yui-u" id="${args.htmlid}-startdate"><#if result.from?exists>${result.from?date("MM/dd/yyy")?string("EEEE, MMMM dd yyyy")}</#if><#if !result.allday?exists> at ${result.start!""}</#if></div>
   </div>
   <div class="yui-gd">
      <div class="yui-u first">${msg("label.enddate")}:</div>
      <div class="yui-u" id="${args.htmlid}-enddate"><#if result.to?exists>${result.to?date("MM/dd/yyy")?string("EEEE, MMMM dd yyyy")}</#if><#if !result.allday?exists> at ${result.end!""}</#if></div>
   </div>
   <br />
   <div class="bdft">
      <input type="submit" id="${args.htmlid}-edit-button" value="${msg("button.edit")}" />
      <input type="submit" id="${args.htmlid}-delete-button" value="${msg("button.delete")}" />
      <input type="submit" id="${args.htmlid}-cancel-button" value="${msg("button.close")}" />
   </div>
</div>