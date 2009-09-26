<div class="rmoptions">
   <div id="${el}-options-toggle" class="separator">
      <div class="options">${msg("label.options")}</div>
   </div>
   <div class="bd options-hidden" id="${el}-options">
      <div class="yui-gb">
         <div class="yui-u first">
            <span class="header">${msg("label.metadata")}</span>
            <div id="${el}-metadata" class="metadata">
               <ul>
                  <li>
                     <input type="checkbox" id="${el}-metadata-identifier" checked="checked" />
                     <label for="${el}-metadata-identifier">${msg("label.identifier")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-name" checked="checked" />
                     <label for="${el}-metadata-name">${msg("label.name")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-title" checked="checked" />
                     <label for="${el}-metadata-title">${msg("label.title")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-description" />
                     <label for="${el}-metadata-description">${msg("label.description")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-parentFolder" checked="checked" />
                     <label for="${el}-metadata-parentFolder">${msg("label.parentFolder")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-creator" />
                     <label for="${el}-metadata-creator">${msg("label.creator")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-created" />
                     <label for="${el}-metadata-created">${msg("label.created")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-modifier" />
                     <label for="${el}-metadata-modifier">${msg("label.modifier")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-modified" checked="checked" />
                     <label for="${el}-metadata-modified">${msg("label.modified")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-author" />
                     <label for="${el}-metadata-author">${msg("label.author")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-originator" checked="checked" />
                     <label for="${el}-metadata-originator">${msg("label.originator")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-dateFiled" checked="checked" />
                     <label for="${el}-metadata-dateFiled">${msg("label.dateFiled")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-publicationDate" />
                     <label for="${el}-metadata-publicationDate">${msg("label.publicationDate")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-reviewDate" />
                     <label for="${el}-metadata-reviewDate">${msg("label.reviewDate")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-vitalRecord" checked="checked" />
                     <label for="${el}-metadata-vitalRecord">${msg("label.vitalRecord")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-originatingOrganization" />
                     <label for="${el}-metadata-originatingOrganization">${msg("label.originatingOrganization")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-mediaType" />
                     <label for="${el}-metadata-mediaType">${msg("label.mediaType")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-format" />
                     <label for="${el}-metadata-format">${msg("label.format")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-dateReceived" />
                     <label for="${el}-metadata-dateReceived">${msg("label.dateReceived")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-location" />
                     <label for="${el}-metadata-location">${msg("label.location")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-address" />
                     <label for="${el}-metadata-address">${msg("label.address")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-supplementalMarkingList" />
                     <label for="${el}-metadata-supplementalMarkingList">${msg("label.supplementalMarkingList")}</label>
                  </li>
                  <li>
                     <input type="checkbox" id="${el}-metadata-dispositionActionAsOf" />
                     <label for="${el}-metadata-dispositionActionAsOf">${msg("label.dispositionActionAsOf")}</label>
                  </li>
                  <#list meta as d>
                  <#assign prop=d.name?substring(4)>
                  <li>
                     <input type="checkbox" id="${el}-metadata-${prop}" />
                     <label for="${el}-metadata-${prop}">${d.title?html}</label>
                  </li>
                  </#list>
               </ul>
            </div>
         </div>
         <div class="yui-u">
            <div class="sort">
               <span class="header">${msg("label.order")}</span>
               <#list 1..3 as i>
               <div>
                  <span class="sortlabel"><#if i=1>${msg("label.sortFirst")}<#else>${msg("label.sortNext")}</#if></span>
                  <span>
                     <input id="${el}-sort${i}" type="button" name="sort${i}" value="<#if i=1>${msg("label.identifier")}<#else>${msg("label.sortNone")}</#if>" />
                     <select id="${el}-sort${i}-menu">
                        <#if i!=1><option value="">${msg("label.sortNone")}</option></#if>
                        <option value="rma:identifier">${msg("label.identifier")}</option>
                        <option value="cm:name">${msg("label.name")}</option>
                        <option value="cm:title">${msg("label.title")}</option>
                        <option value="cm:description">${msg("label.description")}</option>
                        <option value="cm:creator">${msg("label.creator")}</option>
                        <option value="cm:created">${msg("label.created")}</option>
                        <option value="cm:modifier">${msg("label.modifier")}</option>
                        <option value="cm:modified">${msg("label.modified")}</option>
                        <option value="cm:author">${msg("label.author")}</option>
                        <option value="rma:originator">${msg("label.originator")}</option>
                        <option value="rma:dateFiled">${msg("label.dateFiled")}</option>
                        <option value="rma:publicationDate">${msg("label.publicationDate")}</option>
                        <option value="rma:reviewAsOf">${msg("label.reviewDate")}</option>
                        <option value="rma:originatingOrganization">${msg("label.originatingOrganization")}</option>
                        <option value="rma:mediaType">${msg("label.mediaType")}</option>
                        <option value="rma:format">${msg("label.format")}</option>
                        <option value="rma:dateReceived">${msg("label.dateReceived")}</option>
                        <option value="rma:location">${msg("label.location")}</option>
                        <option value="rma:address">${msg("label.address")}</option>
                        <option value="rmc:supplementalMarkingList">${msg("label.supplementalMarkingList")}</option>
                        <!-- double ?html encoding required here due to YUI bug -->
                        <#list meta as d>
                        <option value="${d.name}">${d.title?html?html}</option>
                        </#list>
                     </select>
                  </span>
                  <span>
                     <input id="${el}-sort${i}-order" type="button" name="sort${i}-order" value="${msg("label.sortAscending")}" />
                     <select id="${el}-sort${i}-order-menu">
                        <option value="asc">${msg("label.sortAscending")}</option>
                        <option value="dsc">${msg("label.sortDescending")}</option>
                     </select>
                  </span>
               </div>
               </#list>
            </div>
         </div>
         <div class="yui-u">
            <div class="components">
               <span class="header">${msg("label.components")}</span>
               <div>
                  <input type="checkbox" id="${el}-records" checked="checked" />
                  <label for="${el}-records">${msg("label.records")}</label>
               </div>
               <div class="indented">
                  <input type="checkbox" id="${el}-undeclared" />
                  <label for="${el}-undeclared">${msg("label.undeclared")}</label>
               </div>
               <div class="indented">
                  <input type="checkbox" id="${el}-vital" />
                  <label for="${el}-vital">${msg("label.vital")}</label>
               </div>
               <div>
                  <input type="checkbox" id="${el}-folders" />
                  <label for="${el}-folders">${msg("label.recordFolders")}</label>
               </div>
               <div>
                  <input type="checkbox" id="${el}-categories" />
                  <label for="${el}-categories">${msg("label.recordCategories")}</label>
               </div>
               <div>
                  <input type="checkbox" id="${el}-series" />
                  <label for="${el}-series">${msg("label.recordSeries")}</label>
               </div>
               <div>
                  <input type="checkbox" id="${el}-frozen" />
                  <label for="${el}-frozen">${msg("label.frozen")}</label>
               </div>
               <div>
                  <input type="checkbox" id="${el}-cutoff" />
                  <label for="${el}-cutoff">${msg("label.cutoff")}</label>
               </div>
            </div>
         </div>
      </div>
   </div>
</div>