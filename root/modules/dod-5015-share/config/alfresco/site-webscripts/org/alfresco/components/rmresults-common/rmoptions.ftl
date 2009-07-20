<div class="rmoptions">
   <div id="${el}-options-toggle" class="separator">
      <div class="options">${msg("label.options")}</div>
   </div>
   <div id="${el}-options">
      <table cellspacing="0" cellpadding="0" border="0">
         <tr>
            <td>
               <span>${msg("label.metadata")}:</span>
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
                        <input type="checkbox" id="${el}-metadata-originator" checked="checked" />
                        <label for="${el}-metadata-originator">${msg("label.originator")}</label>
                     </li>
                     <li>
                        <input type="checkbox" id="${el}-metadata-dateFiled" checked="checked" />
                        <label for="${el}-metadata-dateFiled">${msg("label.dateFiled")}</label>
                     </li>
                     <li>
                        <input type="checkbox" id="${el}-metadata-publicationDate" checked="checked" />
                        <label for="${el}-metadata-publicationDate">${msg("label.publicationDate")}</label>
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
                     <#list meta as d>
                     <#assign prop=d.name?substring(4)>
                     <li>
                        <input type="checkbox" id="${el}-metadata-${prop}" />
                        <label for="${el}-metadata-${prop}">${d.title}</label>
                     </li>
                     </#list>
                  </ul>
               </div>
            </td>
            <td>
               <div class="sort">
                  <span>${msg("label.order")}:</span>
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
                           <option value="rma:supplementalMarkingList">${msg("label.supplementalMarkingList")}</option>
                           <#list meta as d>
                           <option value="${d.name}">${d.title}</option>
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
            </td>
         </tr>
      </table>
   </div>
</div>