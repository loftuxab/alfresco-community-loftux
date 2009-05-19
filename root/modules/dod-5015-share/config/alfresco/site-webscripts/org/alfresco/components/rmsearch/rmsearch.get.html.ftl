<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsSearch("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="search">
   <div class="title">${msg("label.searchtitle")}</div>
   
   <div class="builder">
      <div class="yui-g">
         <div class="yui-u first">
            <span>${msg("label.metadata")}:</span>
            <div id="${el}-metadata" class="metadata">
               <ul>
                  <li>
                     <input type="checkbox" id="${el}-metadata-identifier" checked="checked" />
                     <label for="${el}-metadata-identifier">${msg("label.identifier")}</label>
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
               </ul>
            </div>
         </div>
         <div class="yui-u">
            <span>${msg("label.order")}:</span>
            <div>
               <span>${msg("label.sortFirst")}</span>
               <span>
                  <select id="${el}-sort1">
                     <option value="identifier">${msg("label.identifier")}</option>
                     <option value="title">${msg("label.title")}</option>
                     <option value="originator">${msg("label.originator")}</option>
                     <option value="dateFiled">${msg("label.dateFiled")}</option>
                     <option value="publicationDate">${msg("label.publicationDate")}</option>
                     <option value="originatingOrganization">${msg("label.originatingOrganization")}</option>
                     <option value="mediaType">${msg("label.mediaType")}</option>
                     <option value="format">${msg("label.format")}</option>
                     <option value="dateReceived">${msg("label.dateReceived")}</option>
                     <option value="location">${msg("label.location")}</option>
                  </select>
               </span>
            </div>
            <div>
               <span>${msg("label.sortNext")}</span>
               <span>
                  <select id="${el}-sort2">
                     <option value="identifier">${msg("label.identifier")}</option>
                     <option value="title">${msg("label.title")}</option>
                     <option value="originator">${msg("label.originator")}</option>
                     <option value="dateFiled">${msg("label.dateFiled")}</option>
                     <option value="publicationDate">${msg("label.publicationDate")}</option>
                     <option value="originatingOrganization">${msg("label.originatingOrganization")}</option>
                     <option value="mediaType">${msg("label.mediaType")}</option>
                     <option value="format">${msg("label.format")}</option>
                     <option value="dateReceived">${msg("label.dateReceived")}</option>
                     <option value="location">${msg("label.location")}</option>
                  </select>
               </span>
            </div>
            <div>
               <span>${msg("label.sortNext")}</span>
               <span>
                  <select id="${el}-sort3">
                     <option value="identifier">${msg("label.identifier")}</option>
                     <option value="title">${msg("label.title")}</option>
                     <option value="originator">${msg("label.originator")}</option>
                     <option value="dateFiled">${msg("label.dateFiled")}</option>
                     <option value="publicationDate">${msg("label.publicationDate")}</option>
                     <option value="originatingOrganization">${msg("label.originatingOrganization")}</option>
                     <option value="mediaType">${msg("label.mediaType")}</option>
                     <option value="format">${msg("label.format")}</option>
                     <option value="dateReceived">${msg("label.dateReceived")}</option>
                     <option value="location">${msg("label.location")}</option>
                  </select>
               </span>
            </div>
         </div>
      </div>
      <div>${msg("label.searchterm")}:</div>
      <div>
         <!-- Query text input -->
         <textarea id="${el}-query" rows="2" cols="120"></textarea>
         <!-- Search button -->
         <div class="search-button">
            <span class="yui-button yui-push-button" id="${el}-search-button">
               <span class="first-child"><button>${msg("button.search")}</button></span>
            </span>
         </div>
      </div>
   </div>
   
   <div id="${el}-results" class="results"></div>
</div>