<div class="disposition-edit">

   <script type="text/javascript">//<![CDATA[
   new Alfresco.DispositionEdit("${args.htmlid}").setMessages(
      ${messages}
   ).setOptions({
      nodeRef: "${page.url.args.nodeRef}",
      siteId: "${page.url.templateArgs.site!""}"
   });
   //]]></script>

   <#assign el=args.htmlid>
   <div class="disposition-form">
      <div>
         <div class="header">${msg("header.action")}</div>
         <hr/>
         <ol id="${el}-actionList" class="action-list">
            <li id="${el}-action-template" class="action collapsed">
               <div class="header">
                  <div class="no"></div>
                  <div class="buttons">
                     <span class="edit" title="${msg("icon.editaction")}">&nbsp;</span>
                     <span class="delete" title="${msg("icon.deleteaction")}">&nbsp;</span>
                  </div>
                  <div class="title"></div>
               </div>
               <div class="details" style="display:none;">
                  <form class="action-form" method="" action="">
                     <input type="hidden" name="period" value="" class="period">
                     <input type="hidden" name="id" value="" class="id">
                     <div class="section">
                        ${msg("label.action")}
                        <select name="name" class="action-type">
                           <#list dispositionActions as action>
                           <option value="${action.value}">${action.label}</option>
                           </#list>
                        </select>
                     </div>
                     <div class="section">
                        <input type="checkbox" class="period-enabled" checked="true"/>
                        ${msg("label.after")}
                        <input type="text" class="period-amount"/>
                        <select class="period-unit">
                           <#list periodTypes as periodType>
                           <option value="${periodType.value}">${periodType.label}</option>
                           </#list>
                        </select>
                        ${msg("label.from")}
                        <select name="periodProperty" class="period-action">
                           <#list periodProperties as periodProperty>
                           <option value="${periodProperty.value}">${periodProperty.label}</option>
                           </#list>
                        </select>
                        <span class="or-relation">${msg("label.or")}</span>
                        <span class="and-relation">${msg("label.and")}</span>
                     </div>
                     <div class="section">
                        <input type="checkbox" class="events-enabled" checked="true"/>
                        ${msg("label.when")}
                     </div>
                     <div class="section events">
                        <div class="events-header">
                           <div class="event-name-header">${msg("header.event")}</div>
                           <div class="event-type-header">${msg("header.type")}</div>
                           <hr/>
                        </div>
                        <ul class="events-list">
                           <li id="${el}-event-template" class="event">
                              <div class="action-event-relation">
                                 <span class="or">${msg("label.or")}</span>
                                 <span class="and">${msg("label.and")}</span>
                              </div>
                              <div class="action-event-name">
                                 <select name="events[]" class="action-event-name-value">
                                    <#list events as event>
                                    <option value="${event.value}">${event.label}</option>
                                    </#list>
                                 </select>
                              </div>
                              <div class="action-event-type"></div>
                              <div class="action-event-buttons">
                                 <span class="delete" title="${msg("icon.deleteevent")}">&nbsp;</span>
                              </div>
                           </li>
                        </ul>
                        <div class="events-header">
                           <span class="yui-button addevent">
                              <span class="first-child">
                                 <button type="button">${msg("button.addevent")}</button>
                              </span>
                           </span>
                           <hr/>
                        </div>
                        <select name="eligibleOnFirstCompleteEvent" class="relation">
                           <option value="false">${msg("relation.option.and")}</option>
                           <option value="true">${msg("relation.option.or")}</option>
                        </select>
                     </div>
                     <div class="section">
                        <hr/>
                        ${msg("label.description")}:<br>
                        <textarea name="description" class="description"></textarea>
                     </div>
                     <div class="buttons">
                        <span class="yui-button saveaction">
                           <span class="first-child">
                              <button type="button">${msg("button.save")}</button>
                           </span>
                        </span>
                        <span class="yui-button cancel">
                           <span class="first-child">
                              <button type="button">${msg("button.cancel")}</button>
                           </span>
                        </span>
                     </div>
                  </form>
               </div>
            </li>
         </ol>
      </div>

   <div id="${el}-flowButtons">
      <hr />
         <span id="${el}-createaction-button" class="yui-button createaction">
             <span class="first-child">
                 <button type="button">${msg("button.createaction")}</button>
             </span>
         </span>
      </div>
   </div>

   <div class="main-buttons">
      <hr />
      <span id="${el}-done-button" class="yui-button done">
          <span class="first-child">
              <button type="button">${msg("button.done")}</button>
          </span>
      </span>
   </div>


</div>
