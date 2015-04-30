<#include "../admin-template.ftl" />

<@page title=msg("license.title") readonly=true>

   <div class="column-full">
      <p class="intro-tall">${msg("license.intro-text")?html}</p>
   </div>

   <div class="column-left">
      <@attrfield attribute=licenseAttributes["Subject"] label=msg("license.license-subject") description=msg("license.license-subject.description") />
      <@attrfield attribute=licenseAttributes["LicenseMode"] label=msg("license.license-mode") description=msg("license.license-mode.description") />
      <@attrfield attribute=licenseAttributes["Issued"] label=msg("license.issued") description=msg("license.issued.description") />
   </div>
   <div class="column-right">
      <@attrfield attribute=licenseAttributes["Holder"] label=msg("license.license-holder") description=msg("license.license-holder.description") />
      <#if licenseAttributes["Days"].value &gt; -1>
      <@attrfield attribute=licenseAttributes["Days"] label=msg("license.days") description=msg("license.days.description") />
      <#else>
      <@field value=msg("license.license-restrictions.unlimited") label=msg("license.days") description=msg("license.days.description") />
      </#if>
      <@attrfield attribute=licenseAttributes["Issuer"] label=msg("license.issuer") description=msg("license.issuer.description") />
   </div>
   
   <div class="column-full">
      <@section label=msg("license.license-restrictions") />
   </div>
   <div class="column-left">
      <#if licenseAttributes["ValidUntil"].value?long &gt; 0>
      <@attrfield attribute=licenseAttributes["ValidUntil"] label=msg("license.license-restrictions.valid-until") description=msg("license.license-restrictions.valid-until.description") />
      <#else>
      <@field value=msg("license.license-restrictions.unlimited") label=msg("license.license-restrictions.valid-until") description=msg("license.license-restrictions.valid-until.description") />
      </#if>
      <#if licenseAttributes["MaxUsers"].value?has_content>
      <@attrfield attribute=licenseAttributes["MaxUsers"] label=msg("license.license-restrictions.max-users") description=msg("license.license-restrictions.max-users.description") />
      <#else>
      <@field value=msg("license.license-restrictions.unlimited") label=msg("license.license-restrictions.max-users") description=msg("license.license-restrictions.max-users.description") />
      </#if>
   </div>
   <div class="column-right">
      <#if licenseAttributes["RemainingDays"].value &gt; -1>
      <@attrfield attribute=licenseAttributes["RemainingDays"] label=msg("license.license-restrictions.remaining-days") description=msg("license.license-restrictions.remaining-days.description") />
      <#else>
      <@field value=msg("license.license-restrictions.unlimited") label=msg("license.license-restrictions.remaining-days") description=msg("license.license-restrictions.remaining-days.description") />
      </#if>
      <#if licenseAttributes["MaxDocs"].value?has_content>
      <@attrfield attribute=licenseAttributes["MaxDocs"] label=msg("license.license-restrictions.max-content-objects") description=msg("license.license-restrictions.max-content-objects.description") />
      <#else>
      <@field value=msg("license.license-restrictions.unlimited") label=msg("license.license-restrictions.max-content-objects") description=msg("license.license-restrictions.max-content-objects.description") />
      </#if>
   </div>

   <div class="column-full">
      <@section label=msg("license.license-usage-information") />
   </div>
   <div class="column-left">
      <#if !licenseAttributes["MaxUsers"].value?has_content>
      <@field value=msg("license.license-restrictions.unlimited") label=msg("license.license-usage-information.users") description=msg("license.license-usage-information.users.description") />
      <#elseif licenseAttributes["CurrentUsers"].value?has_content>
      <@attrfield attribute=licenseAttributes["CurrentUsers"] label=msg("license.license-usage-information.users") description=msg("license.license-usage-information.users.description") />
      <#else>
      <@field value=msg("license.license-usage-information.pending") label=msg("license.license-usage-information.users") description=msg("license.license-usage-information.users.description") />
      </#if>
   </div>
   <div class="column-right">
      <#if !licenseAttributes["MaxDocs"].value?has_content>
      <@field value=msg("license.license-restrictions.unlimited") label=msg("license.license-usage-information.content-items") description=msg("license.license-usage-information.content-items.description") />
      <#elseif licenseAttributes["CurrentDocs"].value?has_content>
      <@attrfield attribute=licenseAttributes["CurrentDocs"] label=msg("license.license-usage-information.content-items") description=msg("license.license-usage-information.content-items.description") />
      <#else>
      <@field value=msg("license.license-usage-information.pending") label=msg("license.license-usage-information.content-items") description=msg("license.license-usage-information.content-items.description") />
      </#if>
   </div>
   
   <#if licenseAttributes["MaxUsers"].value?has_content && licenseAttributes["CurrentUsers"].value?has_content>
      <#if licenseAttributes["CurrentUsers"].value gt licenseAttributes["MaxUsers"].value >
           <div class="column-full">
           <@section label=msg("license.license-error") />
           <p class="info">${msg("license.license-error.description", licenseAttributes["MaxUsers"].value, licenseAttributes["CurrentUsers"].value )?html}</p>
           </div>
      <#elseif licenseAttributes["CurrentUsers"].value == licenseAttributes["MaxUsers"].value>
           <div class="column-full">
           <@section label=msg("license.license-warning") />
           <p class="info">${msg("license.license-warning.description", licenseAttributes["MaxUsers"].value, licenseAttributes["CurrentUsers"].value )?html}</p>
           </div>
      <#elseif licenseAttributes["CurrentUsers"].value gt licenseAttributes["MaxUsers"].value - (licenseAttributes["MaxUsers"].value * 0.1 + 1) >
           <div class="column-full">
           <@section label=msg("license.license-info") />
           <p class="info">${msg("license.license-info.description", licenseAttributes["MaxUsers"].value, licenseAttributes["CurrentUsers"].value )?html}</p>
           </div> 
      </#if>       
   </#if> 

   <div class="column-full">
      <@section label=msg("license.system-heartbeat") />
      <@attrstatus attribute=heartBeatAttribute label=msg("license.system-heartbeat.heart-beat") description=msg("license.system-heartbeat.heart-beat-disabled.description") />
   </div>

   <div class="column-full">
      <@section label=msg("license.cloud-sync") />
      <@attrstatus attribute=licenseAttributes["CloudSyncKeyAvailable"] label=msg("license.cloud-sync.cloud-sync-enabled") description=msg("license.cloud-sync.cloud-sync-enabled.description") />
   </div>

   <div class="column-full">
      <@section label=msg("license.cluster") />
      <@attrstatus attribute=licenseAttributes["ClusterEnabled"] label=msg("license.cluster.cluster-enabled") description=msg("license.cluster.cluster-enabled.description") />
   </div>
   
   <div class="column-full">
      <@section label=msg("license.cryptodoc") />
      <@attrstatus attribute=licenseAttributes["CryptodocEnabled"] label=msg("license.cryptodoc.cryptodoc-enabled") description=msg("license.cryptodoc.cryptodoc-enabled.description") />
   </div>

   <div class="column-full">
      <@section label=msg("license.license-management") />
      <p class="info">${msg("license.license-management.description")?html}</p>
      <p class="info">${msg("license.license-management.help-link")}</p>
   </div>

   <div class="column-left">
      <@button id="upload-license" label=msg("license.license-management.upload-license") description=msg("license.license-management.upload-license.description") onclick="Admin.showDialog('${url.serviceContext}/enterprise/admin/admin-license-upload');" />
   </div>
   <div class="column-right">
      <@button id="apply-new-license" label=msg("license.license-management.apply-new-license") description=msg("license.license-management.apply-new-license.description") onclick="Admin.showDialog('${url.serviceContext}/enterprise/admin/admin-license-apply');" />
   </div>

</@page>