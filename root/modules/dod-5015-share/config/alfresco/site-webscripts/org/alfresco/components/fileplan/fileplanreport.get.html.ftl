<#macro recordSeriesHTML recordSeries>
   <#list recordSeries as recordSerie>
   <div class="report-title recordseries">
      <div><img src="${url.context}/components/documentlibrary/images/record-series-48.png"/></div>
      <div>${recordSerie.name}</div>
   </div>
   <div class="report-section recordseries">
      <div class="report-property">
         <span class="report-label">${msg("label.parentPath")}:</span>
         <span class="report-value">${recordSerie.parentPath}</span>
      </div>
      <div class="report-property">
         <span class="report-label">${msg("label.recordSeriesIdentifier")}:</span>
         <span class="report-value">${recordSerie.identifier}</span>
      </div>
      <div class="report-property">
         <span class="report-label">${msg("label.description")}:</span>
         <span class="report-value">${recordSerie.description!""}</span>
      </div>
   </div>
   <@recordCategoriesHTML recordCategories=recordSerie.recordCategories/>
   </#list>
</#macro>

<#macro recordCategoriesHTML recordCategories>
   <#list recordCategories as recordCategory>
   <div class="report-title recordcategory">
      <div><img src="${url.context}/components/documentlibrary/images/record-category-48.png"/></div>
      <div>${recordCategory.name}</div>
   </div>
   <div class="report-section recordcategory">
      <div class="report-property">
         <span class="report-label">${msg("label.parentPath")}:</span>
         <span class="report-value">${recordCategory.parentPath}</span>
         </div>
      <div class="report-property">
         <span class="report-label">${msg("label.recordCategoryIdentifier")}:</span>
         <span class="report-value">${recordCategory.identifier}</span>
      </div>
      <div class="report-property">
         <span class="report-label">${msg("label.dispositionAuthority")}:</span>
         <#if (recordCategory.dispositionAuthority??)>
         <span class="report-value">${recordCategory.dispositionAuthority}</span>
         <#else>
         <span class="report-value">${msg("label.noDispositionAuthority")}</span>
         </#if>
      </div>
      <#if (recordCategory.vitalRecordIndicator??)>
      <div class="report-property">
         <span class="report-label">${msg("label.vitalRecordIndicator")}:</span>
         <span class="report-value">${recordCategory.vitalRecordIndicator}</span>
      </div>
      </#if>
      <br/>
      <div class="report-property">
         <span class="report-label">${msg("label.dispositionSchedule")}:</span>
      </div>
      <#if (recordCategory.dispositionActions?size == 0)>
      <div class="report-property">
         <span class="report-value">${msg("label.noActions")}</span>
      </div>
      <#else>
         <#list recordCategory.dispositionActions as dispositionAction>
         <div class="report-property">
            <span class="report-value">
               ${dispositionAction_index + 1}.
               <#if (dispositionAction.dispositionDescription != "")>
                  ${dispositionAction.dispositionDescription}
               <#else>
                  ${msg("label.noActionDescription")}
               </#if>
            </span>
         </div>
         </#list>
      </#if>
   </div>
   <@recordFoldersHTML recordFolders=recordCategory.recordFolders/>
   </#list>
</#macro>

<#macro recordFoldersHTML recordFolders>
   <#list recordFolders as recordFolder>
   <div class="report-title recordfolder">
      <div><img src="${url.context}/components/documentlibrary/images/record-folder-48.png"/></div>
      <div>${recordFolder.name}</div>
   </div>
   <div class="report-section recordfolder">
      <div class="report-property">
         <span class="report-label">${msg("label.parentPath")}:</span>
         <span class="report-value">${recordFolder.parentPath}</span>
      </div>
      <div class="report-property"><span class="report-label">${msg("label.recordFolderIdentifier")}:</span>
         <span class="report-value">${recordFolder.identifier}</span>
      </div>
      <#if (recordFolder.vitalRecordIndicator??)>
      <div class="report-property"><span class="report-label">${msg("label.vitalRecordIndicator")}:</span>
         <span class="report-value">${recordFolder.vitalRecordIndicator}</span>
      </div>
      </#if>
   </div>
   </#list>
</#macro>

<div class="fileplanreport">
   <div class="report-section">
      <div class="report-property">
         <span class="report-label">${msg("label.user")}:</span>
         <span class="report-value">${firstName} ${lastName}</span>
      </div>
      <div class="report-property">
         <span class="report-label">${msg("label.dateAndTime")}:</span>
         <span class="report-value">${printDate}</span>
      </div>
   </div>
   <#if (recordSeries??)>
   <@recordSeriesHTML recordSeries=recordSeries/>
   <#elseif (recordCategories??)>
   <@recordCategoriesHTML recordCategories=recordCategories/>
   <#elseif (recordFolders??)>
   <@recordFoldersHTML recordFolders=recordFolders/>
   </#if>
</div>