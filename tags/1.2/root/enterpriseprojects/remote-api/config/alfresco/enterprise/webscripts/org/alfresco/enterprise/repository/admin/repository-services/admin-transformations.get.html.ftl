<#include "../admin-template.ftl" />

<@page title=msg("transformations.title")>

   <div class="column-full">
      <p class="intro">${msg("transformations.intro-text")?html}</p>
      <@section label=msg("transformations.office-transform") />
      <p class="info">${msg("transformations.office-transform.description")?html}</p>
   </div>
   
   <div class="column-left">
      <@attrcheckbox attribute=jodConvAttributes["jodconverter.enabled"] label=msg("transformations.office-transform.jodconverter-enabled") description=msg("transformations.office-transform.jodconverter-enabled.description") />
      <@attrtext attribute=jodConvAttributes["jodconverter.maxTasksPerProcess"] label=msg("transformations.office-transform.max-tasks-per-process") description=msg("transformations.office-transform.max-tasks-per-process.description") />
      <@attrtext attribute=jodConvAttributes["jodconverter.taskExecutionTimeout"] label=msg("transformations.office-transform.task-execution-timeout") description=msg("transformations.office-transform.task-execution-timeout.description") />
      <@attrtext attribute=jodConvAttributes["jodconverter.officeHome"] label=msg("transformations.office-transform.openoffice-home") description=msg("transformations.office-transform.openoffice-home.description") />
   </div>
   <div class="column-right">
      <@attrtext attribute=jodConvAttributes["jodconverter.portNumbers"] label=msg("transformations.office-transform.port-numbers") description=msg("transformations.office-transform.port-numbers.description") />
      <@attrtext attribute=jodConvAttributes["jodconverter.taskQueueTimeout"] label=msg("transformations.office-transform.task-queue-timeout") description=msg("transformations.office-transform.task-queue-timeout.description") />
   </div>
   
   <div class="column-full">
      <@section label=msg("transformations.image-transform") />
      <p class="info">${msg("transformations.image-transform.description")?html}</p>
   </div>

   <div class="column-left">
      <@attrstatus attribute=imageMagicAttributes["Available"] label=msg("transformations.image-transform.imagemagick-available") description=msg("transformations.image-transform.imagemagick-available.description") />
      <@attrfield attribute=imageMagicAttributes["VersionString"] label=msg("transformations.image-transform.version-details") />
      <@attrtext attribute=thirdPartyAttributes["img.exe"] label=msg("transformations.image-transform.imagemagick-executable-path") description=msg("transformations.image-transform.imagemagick-executable-path.description") />
      <@attrtext attribute=thirdPartyAttributes["img.root"] label=msg("transformations.image-transform.imagemagick-root-directory-path") description=msg("transformations.image-transform.imagemagick-root-directory-path.description") />
      <@attrtext attribute=thirdPartyAttributes["img.dyn"] label=msg("transformations.image-transform.imagemagick-library-path") description=msg("transformations.image-transform.imagemagick-library-path.description") />
   </div>
   <div class="column-right">
      <@attrtext attribute=thirdPartyAttributes["img.coders"] label=msg("transformations.image-transform.imagemagick-coders-path") description=msg("transformations.image-transform.imagemagick-coders-path.description") />
      <@attrtext attribute=thirdPartyAttributes["img.gslib"] label=msg("transformations.image-transform.imagemagick-gslib-path") description=msg("transformations.image-transform.imagemagick-gslib-path.description") />
      <@attrtext attribute=thirdPartyAttributes["img.config"] label=msg("transformations.image-transform.imagemagick-config-path") description=msg("transformations.image-transform.imagemagick-config-path.description") />
   </div>

   <div class="column-full">
      <@section label=msg("transformations.transfrom-to-flash") />
      <p class="info">${msg("transformations.transfrom-to-flash.description")?html}</p>
   </div>

   <div class="column-left">
      <@attrstatus attribute=swfToolsAttributes["Available"] label=msg("transformations.transfrom-to-flash.pdf2swf-available") description=msg("transformations.transfrom-to-flash.pdf2swf-available.description") />
      <@attrfield attribute=swfToolsAttributes["VersionString"] label=msg("transformations.transfrom-to-flash.version-details") />
   </div>
   <div class="column-right">
      <@attrtext attribute=thirdPartyAttributes["swf.exe"] label=msg("transformations.transfrom-to-flash.pdf2swf-executable-path") description=msg("transformations.transfrom-to-flash.pdf2swf-executable-path.description") />
      <@attrtext attribute=thirdPartyAttributes["swf.encoder.params"] label=msg("transformations.transfrom-to-flash.pdf2swf-parameters") description=msg("transformations.transfrom-to-flash.pdf2swf-parameters.description") />
   </div>

</@page>