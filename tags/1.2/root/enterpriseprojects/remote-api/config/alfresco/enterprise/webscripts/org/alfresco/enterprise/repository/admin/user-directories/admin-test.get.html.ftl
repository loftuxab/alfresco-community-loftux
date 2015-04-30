<#include "../admin-template.ftl" />

<@page title="Control and Layout TEST Page" params="debug=true">

   <div class="column-full">
      <@tsection label="Toggle section header - closed">
         <@field label="This is a readonly field - inside Closed section" value="The field value" />
      </@tsection>
      <@tsection label="Toggle section header - open" closed=false>
         <@field label="This is a readonly field - inside Open section" value="The field value" />
      </@tsection>
   </div>

   <div class="column-full">
      <@section label="Control test list - this is a section header" />
      
      <@hidden name="hidden01" value="hidden01_value" id="hidden01_id" />
      <@attrhidden attribute=attributes["Subject"] />
      
      <@field label="This is a readonly field 01" value="The field value 01" />
      <@field label="This is a readonly field 02" value="The field value 02" description="Non default description." />
      <@field label="This is a readonly field 03" value="The field value 03" description="Non default description. Inline styling." style="color:blue" />
      <@attrfield attribute=attributes["Subject"] />
      <@attrfield attribute=attributes["Subject"] description="Non default description." />
      <@attrfield attribute=attributes["Subject"] description="Non default description. Inline styling." style="color:blue" />
      
      <@text name="text01" label="Text Label01" />
      <@text name="text02" label="Text Label02" description="A much longer description here, could be quite long indeed. From the visuals there could be a lot of description text that would need to wrap etc. to inform the Administrator of important configuration details." value="Default Value - max 32 chars" maxlength=32 />
      <@text name="text03" label="Text Label03" description="Non default description. Inline styling." style="color:blue" />
      <@attrtext attribute=attributes["Subject"] />
      <@attrtext attribute=attributes["Subject"] description="A much longer description here, could be quite long indeed. Max value 32 chars." maxlength=32 />
      <@attrtext attribute=attributes["Subject"] description="Non default description. Inline styling." style="color:blue" />
      
      <@textarea name="textarea01" label="Text Area Label01" />
      <@textarea name="textarea02" label="Text Area Label02" description="A much longer description here, could be quite long indeed." value="Default Value - max 32 chars" maxlength=32 />
      <@textarea name="textarea03" label="Text Area Label03" description="Non default description. Inline styling." style="color:blue" />
      <@attrtextarea attribute=attributes["Subject"] />
      <@attrtextarea attribute=attributes["Subject"] description="Non default description. Max value 32 chars." maxlength=32 />
      <@attrtextarea attribute=attributes["Subject"] description="Non default description. Inline styling." style="color:blue" />
      
      <@checkbox name="checkbox01" label="Boolean value 01 (false)" />
      <@checkbox name="checkbox02" label="Boolean value 02 (true)" description="A much longer description here, could be quite long indeed. From the visuals there could be a lot of description text that would need to wrap etc. to inform the Administrator of important configuration details." value="true" />
      <@checkbox name="checkbox03" label="Boolean value 03 (false)" description="Non default description. Inline styling." style="color:blue" />
      <@attrcheckbox attribute=attributes["HeartBeatDisabled"] />
      <@attrcheckbox attribute=attributes["HeartBeatDisabled"] description="Non default description." />
      <@attrcheckbox attribute=attributes["HeartBeatDisabled"] description="Non default description. Inline styling." style="color:blue" />
      
      <@options name="options01" label="Options 01">
         <@option label="Simple" value="simple" />
         <@option label="Advanced" value="advanced" />
         <@option label="Extreme" value="extreme" />
      </@options>
      
      <@options name="options02" label="Options 02" description="Non default description.">
         <@option label="Simple" value="simple" />
         <@option label="Advanced" value="advanced" />
         <@option label="Extreme" value="extreme" />
      </@options>
      
      <@options name="options03" label="Options 03 (selected 'extreme' option)" value="extreme">
         <@option label="Simple" value="simple" />
         <@option label="Advanced" value="advanced" />
         <@option label="Extreme" value="extreme" />
      </@options>
      
      <@radios name="radios01" label="Radios 01">
         <@radio label="Choice 1" value="choice1" />
         <@radio label="Choice 2" value="choice2" />
         <@radio label="Choice 3" value="choice3" />
      </@radios>
      
      <@radios name="radios02" label="Radios 02" description="Non default description.">
         <@radio label="Choice 1" value="choice1" />
         <@radio label="Choice 2" value="choice2" />
         <@radio label="Choice 3" value="choice3" />
      </@radios>
      
      <@radios name="radios03" label="Radios 03 (selected 'choice3' option)" value="choice3">
         <@radio label="Choice 1" value="choice1" />
         <@radio label="Choice 2" value="choice2" />
         <@radio label="Choice 3" value="choice3" />
      </@radios>
      
      <@status label="Service Status 01 (true)" description="" value="true" />
      <@status label="Service Status 02 (false)" description="Non default description." value="false" />
      <@attrstatus attribute=attributes["HeartBeatDisabled"] />
      <@attrstatus attribute=attributes["HeartBeatDisabled"] description="Non default description." />
   </div>
   
   <div class="column-left">
      <@ulist label="Unordered List 01" value="Thing1,Thing2" />
      <@ulist label="Unordered List 02" value="Thing1,Thing2" description="Non default description." />
      <@olist label="Ordered List 01" value="Thing1,Thing2" />
      <@olist label="Ordered List 02" value="Thing1,Thing2" description="Non default description." />
   </div>
   <div class="column-right">
      <@attrulist attribute=attributes["Issuer"] />
      <@attrulist attribute=attributes["Issuer"] description="Non default description." />
      <@attrolist attribute=attributes["Issuer"] />
      <@attrolist attribute=attributes["Issuer"] description="Non default description." />
   </div>
   
   <div class="column-left">
      <@section label="Left Column Section" />
      <#list attributes?values as a>
         <@attrfield attribute=a />
      </#list>
   </div>
   <div class="column-right">
      <@section label="Right Column Section" />
      <#list attributes?values as a>
         <@attrfield attribute=a />
      </#list>
   </div>
   
   <div class="column-full">
      <@section label="Full Column Section" />
      <#list attributes?values as a>
         <@attrtext attribute=a />
      </#list>
   </div>
   
   <div class="column-left">
      <@section label="Left Column Section 2" />
      <#list attributes?values as a>
         <@attrtextarea attribute=a />
      </#list>
   </div>
   <div class="column-right">
      <@section label="Right Column Section 2" />
      <#list attributes?values as a>
         <@control attribute=a />
      </#list>
      <@button label="Test" onclick="alert('test');" />
   </div>

</@page>