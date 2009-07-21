<script type="text/javascript">//<![CDATA[

   new Alfresco.Events("${args.htmlid}").setOptions({
      nodeRef: "${page.url.args.nodeRef}"
   }).setMessages(
      ${messages}
   );

//]]></script>
<#assign el=args.htmlid>

<div class="events">
   <div class="heading">${msg("events.heading")}</div>

   <div class="header">${msg("title.completedEvents")}</div>
   <ul id="${el}-completed-events" class="completed-events">
      <li id="${el}-completedEventTemplate" class="event completed">
         <div class="icons"></div>
         <div class="info">
            <div class="field name">
               <span class="value"></span>
            </div>
            <div class="field automatic">
               <span class="value"></span>
            </div>
            <div class="field completed-at">
               <span class="label">${msg("label.completedAt")}:</span>
               <span class="value"></span>
            </div>
            <div class="field completed-by">
               <span class="label">${msg("label.completedBy")}:</span>
               <span class="value"></span>
            </div>
         </div>
         <div class="buttons">
            <span class="yui-button undo-button">
               <span class="first-child">
                  <button type="button">${msg("button.undo")}</button>
               </span>
            </span>            
         </div>
      </li>
   </ul>

   <div class="header">${msg("title.incompleteEvents")}</div>
   <ul id="${el}-incomplete-events" class="incomplete-events">
      <li id="${el}-incompleteEventTemplate" class="event incomplete">
         <div class="icons"></div>
         <div class="info">
            <div class="field name">
               <span class="value"></span>
            </div>
            <div class="field automatic">
               <span class="value"></span>
            </div>
            <div class="field asof">
               <span class="label">${msg("label.asOf")}:</span>
               <span class="value"></span>
            </div>
         </div>
         <div class="buttons">
            <span class="yui-button complete-button">
               <span class="first-child">
                  <button type="button">${msg("button.completeEvent")}</button>
               </span>
            </span>
         </div>
      </li>
   </ul>
</div>