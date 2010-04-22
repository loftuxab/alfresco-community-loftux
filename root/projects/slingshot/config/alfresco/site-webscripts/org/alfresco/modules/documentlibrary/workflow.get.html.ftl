<script type="text/javascript">//<![CDATA[
   Alfresco.util.ComponentManager.get("${args.htmlid}").setMessages(${messages});
//]]></script>
<div id="${args.htmlid}-dialog" class="workflow">
   <div id="${args.htmlid}-title" class="hd"></div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="post">
         <input type="hidden" name="date" id="${args.htmlid}-date" value="" />
         <div class="yui-g">
            <h2>${msg("header.type")}</h2>
         </div>
         <div class="field">
            <select id="${args.htmlid}-type" name="type" tabindex="0">
            <#list workflows as w>
               <option value="${w}"<#if w_index == 0> selected="selected"</#if>>${msg("workflow." + w?replace(":", "_"))}</option>
            </#list>
            </select>
         </div>
         <div class="yui-g">
            <h2>${msg("header.people")}</h2>
         </div>
         <div class="yui-ge field">
            <div class="yui-u first">
               <div id="${args.htmlid}-peoplefinder"></div>
            </div>
            <div class="yui-u">
               <div id="${args.htmlid}-peopleselected" class="people-selected"></div>
            </div>
         </div>
         <div class="yui-g">
            <h2>${msg("header.date")}</h2>
         </div>
         <div class="field">    
            <input id="${args.htmlid}-dueDate-checkbox" name="-" type="checkbox" value="${msg("label.due-date.none")}" tabindex="0"/>&nbsp;
            <span id="${args.htmlid}-dueDate"><label for="${args.htmlid}-dueDate-checkbox">${msg("label.due-date.none")}</label></span>
         </div>
         <div id="${args.htmlid}-calendarOverlay" class="calendar-overlay">
            <div class="bd">
               <div id="${args.htmlid}-calendar" class="calendar"></div>
            </div>
         </div>
         <div class="yui-g">
            <h2>${msg("header.comment")}</h2>
         </div>
         <div class="field">
            <textarea id="${args.htmlid}-comment" name="description" rows="3" tabindex="0"></textarea>
            <span>${msg("label.comment.max-length")}</span>
            </div>
         <div class="bdft">
            <input type="button" id="${args.htmlid}-ok" value="${msg("button.assign")}" tabindex="0" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>