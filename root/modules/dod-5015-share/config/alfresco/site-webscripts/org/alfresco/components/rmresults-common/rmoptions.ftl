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
                        <input type="checkbox" id="${el}-metadata-supplementalMarkingList" />
                        <label for="${el}-metadata-supplementalMarkingList">${msg("label.supplementalMarkingList")}</label>
                     </li>
                  </ul>
               </div>
            </td>
            <td>
               <div class="sort">
                  <span>${msg("label.order")}:</span>
                  <div>
                     <span class="sortlabel">${msg("label.sortFirst")}</span>
                     <span>
                        <input id="${el}-sort1" type="button" name="sort1" value="${msg("label.identifier")}" />
                        <select id="${el}-sort1-menu">
                           <option value="identifier">${msg("label.identifier")}</option>
                           <option value="name">${msg("label.name")}</option>
                           <option value="title">${msg("label.title")}</option>
                           <option value="originator">${msg("label.originator")}</option>
                           <option value="dateFiled">${msg("label.dateFiled")}</option>
                           <option value="publicationDate">${msg("label.publicationDate")}</option>
                           <option value="vitalRecord">${msg("label.vitalRecord")}</option>
                           <option value="originatingOrganization">${msg("label.originatingOrganization")}</option>
                           <option value="mediaType">${msg("label.mediaType")}</option>
                           <option value="format">${msg("label.format")}</option>
                           <option value="dateReceived">${msg("label.dateReceived")}</option>
                           <option value="location">${msg("label.location")}</option>
                           <option value="supplementalMarkingList">${msg("label.supplementalMarkingList")}</option>
                        </select>
                     </span>
                  </div>
                  <div>
                     <span class="sortlabel">${msg("label.sortNext")}</span>
                     <span>
                        <input id="${el}-sort2" type="button" name="sort2" value="${msg("label.sortNone")}" />
                        <select id="${el}-sort2-menu">
                           <option value="">${msg("label.sortNone")}</option>
                           <option value="identifier">${msg("label.identifier")}</option>
                           <option value="name">${msg("label.name")}</option>
                           <option value="title">${msg("label.title")}</option>
                           <option value="originator">${msg("label.originator")}</option>
                           <option value="dateFiled">${msg("label.dateFiled")}</option>
                           <option value="publicationDate">${msg("label.publicationDate")}</option>
                           <option value="vitalRecord">${msg("label.vitalRecord")}</option>
                           <option value="originatingOrganization">${msg("label.originatingOrganization")}</option>
                           <option value="mediaType">${msg("label.mediaType")}</option>
                           <option value="format">${msg("label.format")}</option>
                           <option value="dateReceived">${msg("label.dateReceived")}</option>
                           <option value="location">${msg("label.location")}</option>
                           <option value="supplementalMarkingList">${msg("label.supplementalMarkingList")}</option>
                        </select>
                     </span>
                  </div>
                  <div>
                     <span class="sortlabel">${msg("label.sortNext")}</span>
                     <span>
                        <input id="${el}-sort3" type="button" name="sort3" value="${msg("label.sortNone")}" />
                        <select id="${el}-sort3-menu">
                           <option value="">${msg("label.sortNone")}</option>
                           <option value="identifier">${msg("label.identifier")}</option>
                           <option value="name">${msg("label.name")}</option>
                           <option value="title">${msg("label.title")}</option>
                           <option value="originator">${msg("label.originator")}</option>
                           <option value="dateFiled">${msg("label.dateFiled")}</option>
                           <option value="publicationDate">${msg("label.publicationDate")}</option>
                           <option value="vitalRecord">${msg("label.vitalRecord")}</option>
                           <option value="originatingOrganization">${msg("label.originatingOrganization")}</option>
                           <option value="mediaType">${msg("label.mediaType")}</option>
                           <option value="format">${msg("label.format")}</option>
                           <option value="dateReceived">${msg("label.dateReceived")}</option>
                           <option value="location">${msg("label.location")}</option>
                           <option value="supplementalMarkingList">${msg("label.supplementalMarkingList")}</option>
                        </select>
                     </span>
                  </div>
               </div>
            </td>
         </tr>
      </table>
   </div>
</div>