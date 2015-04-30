<#macro field label value labelAttr="">
<div class="yui-g form-field">
   <div class="yui-u first">
      <span ${labelAttr}>${label}:</span>
   </div>
   <div class="yui-u">
      <span class="viewmode-value">${value}</span>
   </div>
</div>
</#macro>
<#macro usageField label usage quota labelAttr="">
   <#assign totalUsage = usage?c/>
   <#if usage < 0>
      <#assign totalUsage = 0/>
   </#if>
   <#assign totalQuota = quota?c>
   <#if quota < 0>
      <#assign totalQuota = msg("label.unlimited")/>
   </#if>
   <#if quota == 0>
      <@field msg(label) msg("label.noneAllowed")/>
   <#else>
      <@field msg(label) totalUsage + " / " + totalQuota labelAttr/>
   </#if>
</#macro>

<div class="cloud-account-summary">
   <div class="share-form">

   <#if account??>
      <#-- Account -->
      <div class="form-manager">
         <h1>${msg("header.account")}</h1>
      </div>
      <div class="form-container">
         <div class="form-fields">
            <#-- Account name -->
            <@field msg("label.name") account.name/>
            <#-- Account type -->
            <@field msg("label.typeDisplayName") account.typeDisplayName/>
            <#-- Created Date -->
            <#assign tmp>
               <script type="text/javascript">document.write(Alfresco.util.formatDate("${account.creationDate}", Alfresco.util.message("date-format.default")));</script>
            </#assign>
            <@field msg("label.creationDate") tmp/>
            <#-- Total Sites -->
            <@usageField "label.siteCountUsage" account.usageQuota.siteCountUQ.u account.usageQuota.siteCountUQ.q/>
         </div>
      </div>

      <#-- Users -->
      <div class="form-manager">
         <h1>${msg("header.users")}</h1>
      </div>
      <div class="form-container">
         <div class="form-fields">
            <#assign totalUsers = account.usageQuota.personCountUQ.u/>
            <#assign networkUsers = account.usageQuota.personIntOnlyCountUQ.u/>
            <#assign externalUsers = totalUsers - networkUsers />
            <#assign networkAdministrators = account.usageQuota.personNetworkAdminCountUQ.u/>

            <#-- Network Users -->
            <@usageField "label.networkUsers" account.usageQuota.personIntOnlyCountUQ.u account.usageQuota.personIntOnlyCountUQ.q/>
            <#-- Network Administrators -->
            <@usageField "label.networkAdministrators" account.usageQuota.personNetworkAdminCountUQ.u account.usageQuota.personNetworkAdminCountUQ.q/>
            <#-- External Users -->
            <#if externalUsers < 0>
            <@field msg("label.externalUsers") msg("label.none")/>
            <#else>
            <#assign externalUsers = account.usageQuota.personCountUQ.u - account.usageQuota.personIntOnlyCountUQ.u/>
            <@field msg("label.externalUsers") externalUsers?c/>
            </#if>
            <#-- Total Users -->
            <@usageField "label.totalUsers" account.usageQuota.personCountUQ.u account.usageQuota.personCountUQ.q "class='label'"/>
         </div>
      </div>

      <#-- File Usage-->
      <div class="form-manager">
         <h1>${msg("header.fileUsage")}</h1>
      </div>
      <div class="form-container">
         <div class="form-fields">
            <#assign fileUploadQuota = account.usageQuota.fileUploadSizeUQ.q/>
            <#assign fileQuota = account.usageQuota.fileSizeUQ.q/>
            <#assign fileUsage = account.usageQuota.fileSizeUQ.u/>
            <#if fileUsage &gt; fileQuota>
               <#assign fileUsage = fileQuota/>
            </#if>
            <#-- File Upload Quota -->
            <#if fileUploadQuota < 0>
               <@field msg("label.fileUploadQuota") msg("label.none")/>
            <#else>
               <#assign tmp>
                  <script type="text/javascript">document.write(Alfresco.util.formatFileSize(${fileUploadQuota?c}));</script>
               </#assign>
               <@field msg("label.fileUploadQuota") tmp/>
            </#if>
            <#-- Account Quota -->
            <#if fileQuota < 0>
               <@field msg("label.fileQuota") msg("label.none")/>
            <#else>
               <#assign tmp>
                  <script type="text/javascript">document.write(Alfresco.util.formatFileSize(${fileQuota?c}));</script>
               </#assign>
               <@field msg("label.fileQuota") tmp/>
            </#if>
            <#if fileQuota &gt;= 0>
               <#if fileUsage < 0>
                  <#assign fileUsage = 0/>
               </#if>
               <div class="quota">
                  <div class="quota-bar theme-border-5 theme-bg-3">
                     <div class="quota-bar-used theme-border-5 theme-bg-4" style="left: ${(((fileUsage / fileQuota) * 100)?floor - 100)?string}%;"></div>
                     <div class="quota-bar-25 theme-border-5"></div>
                     <div class="quota-bar-50 theme-border-5"></div>
                     <div class="quota-bar-75 theme-border-5"></div>
                  </div>
                 <div class="quota-legend yui-g">
                     <div class="yui-u first">
                        <div class="quota-box theme-border-5 theme-bg-4">&nbsp;</div>
                        <span class="label">
                           ${msg("label.usedSpace")}
                           <script type="text/javascript">
                              document.write(Alfresco.util.formatFileSize(${fileUsage?c}));
                           </script>
                        </span>
                     </div>
                     <div class="yui-u">
                        <div class="quota-box theme-border-5 theme-bg-3">&nbsp;</div>
                        <span class="label">
                           ${msg("label.freeSpace")}
                           <script type="text/javascript">
                              document.write(Alfresco.util.formatFileSize(${(fileQuota - fileUsage)?c}));
                           </script>
                        </span>
                     </div>
                  </div>
               </div>
            <#else>
               <#if fileUsage &gt;= 0>
                  <#assign tmp>
                     <script type="text/javascript">document.write(Alfresco.util.formatFileSize(${fileUsage}));</script>
                  </#assign>
                  <@field msg("label.fileUsage") tmp/>
               <#else>
                  <@field msg("label.fileUsage") msg("label.none")/>
               </#if>
            </#if>
         </div>
      </div>
   <#elseif message??>
      <div class="form-manager">
         <h1>${msg("header." + message)}</h1>
      </div>
      <div class="form-container">
         <span>${msg("text." + message)}</span>
      </div>
   </#if>
   </div>
</div>
