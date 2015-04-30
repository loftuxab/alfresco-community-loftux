<#include "../admin-template.ftl" />

<@page title=msg("repositoryinfo.title") readonly=true>

   <div class="column-full">
      <p class="intro">${msg("repositoryinfo.intro-text")?html}</p>
      <@section label=msg("repositoryinfo.current-install") />
      <p class="info">${msg("repositoryinfo.current-install.description")?html}</p>
   </div>

   <div class="column-left">
      <@attrfield attribute=currentAttributes["Id"] label=msg("repositoryinfo.current-install.Id") description=msg("repositoryinfo.current-install.Id.description") />
      <@attrfield attribute=currentAttributes["VersionNumber"] label=msg("repositoryinfo.current-install.VersionNumber") description=msg("repositoryinfo.current-install.VersionNumber.description") />
      <@attrfield attribute=currentAttributes["VersionLabel"] label=msg("repositoryinfo.current-install.VersionLabel") description=msg("repositoryinfo.current-install.VersionLabel.description") />
   </div>
   <div class="column-right">
      <@attrfield attribute=currentAttributes["Schema"] label=msg("repositoryinfo.current-install.Schema") description=msg("repositoryinfo.current-install.Schema.description") />
      <@attrfield attribute=currentAttributes["VersionBuild"] label=msg("repositoryinfo.current-install.VersionBuild") description=msg("repositoryinfo.current-install.VersionBuild.description") />
   </div>
   
   <div class="column-full">
      <@section label=msg("repositoryinfo.original-install") />
      <p class="info">${msg("repositoryinfo.original-install.description")?html}</p>
   </div>
   <div class="column-left">
      <@attrfield attribute=initialAttributes["Id"] label=msg("repositoryinfo.original-install.Id") description=msg("repositoryinfo.original-install.Id.description") />
      <@attrfield attribute=initialAttributes["VersionNumber"] label=msg("repositoryinfo.original-install.VersionNumber") description=msg("repositoryinfo.original-install.VersionNumber.description") />
      <@attrfield attribute=initialAttributes["VersionLabel"] label=msg("repositoryinfo.original-install.VersionLabel") description=msg("repositoryinfo.original-install.VersionLabel.description") />
   </div>
   <div class="column-right">
      <@attrfield attribute=initialAttributes["Schema"] label=msg("repositoryinfo.original-install.Schema") description=msg("repositoryinfo.original-install.Schema.description") />
      <@attrfield attribute=initialAttributes["VersionBuild"] label=msg("repositoryinfo.original-install.VersionBuild") description=msg("repositoryinfo.original-install.VersionBuild.description") />
   </div>

</@page>