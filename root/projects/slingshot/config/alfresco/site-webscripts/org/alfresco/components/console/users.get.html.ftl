<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/yui/assets/blank.html"></iframe> 
<![endif]-->
<input id="yui-history-field" type="hidden" />

<script type="text/javascript">//<![CDATA[
   new Alfresco.ConsoleUsers("${args.htmlid}").setOptions(
   {
      minSearchTermLength: "${args.minSearchTermLength!'1'}",
      maxSearchResults: "${args.maxSearchResults!'100'}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="users">
   
   <!-- Search panel -->
   <div id="${el}-search" class="hidden">
      <div class="yui-g">
         <div class="yui-u first">
            <div class="title"><label for="${el}-search-text">${msg("label.title-search")}</label></div>
         </div>
         <div class="yui-u align-right">
            <!-- New User button -->
            <div class="newuser-button">
               <span class="yui-button yui-push-button" id="${el}-newuser-button">
                  <span class="first-child"><button>${msg("button.newuser")}</button></span>
               </span>
            </div>
         </div>
      </div>
      <div class="yui-g separator">
         <div class="yui-u first">
            <div class="search-text"><input type="text" id="${el}-search-text" name="-" value="" />
               <!-- Search button -->
               <div class="search-button">
                  <span class="yui-button yui-push-button" id="${el}-search-button">
                     <span class="first-child"><button>${msg("button.search")}</button></span>
                  </span>
               </div>
            </div>
         </div>
         <div class="yui-u align-right">
            <!-- TODO: enabled/disabled account list filter -->
         </div>
      </div>
      <div class="search-main">
         <div id="${el}-search-bar" class="search-bar theme-bg-color-3">${msg("message.noresults")}</div>
         <div class="results" id="${el}-datatable"></div>
      </div>
   </div>
   
   <!-- View User panel -->
   <div id="${el}-view" class="hidden">
      <div class="yui-g separator">
         <div class="yui-u first">
            <div class="title">${msg("label.title-view")}: <span id="${el}-view-title"></span></div>
         </div>
         <div class="yui-u">
            <!-- Delete User button -->
            <div class="deleteuser-button">
               <span class="yui-button yui-push-button" id="${el}-deleteuser-button">
                  <span class="first-child"><button>${msg("button.deleteuser")}</button></span>
               </span>
            </div>
            <!-- Edit User button -->
            <div class="edituser-button">
               <span class="yui-button yui-push-button" id="${el}-edituser-button">
                  <span class="first-child"><button>${msg("button.edituser")}</button></span>
               </span>
            </div>
         </div>
      </div>
      
      <div id="${el}-view-main" class="view-main">
         <!-- Each info section separated by a header-bar div -->
         <div class="header-bar">${msg("label.about")}</div>
         <div class="photo-row">
            <div class="photo">
               <img class="photoimg" src="${url.context}/components/images/no-user-photo-64.png" alt="" />
            </div>
            <div id="${el}-view-name" class="name-label"></div>
            <div id="${el}-view-jobtitle" class="field-label"></div>
            <div id="${el}-view-organization" class="field-label"></div>
            <div id="${el}-view-location" class="field-label"></div>
         </div>
         <div class="bio-row">
            <hr/>
            <div id="${el}-view-bio"></div>
         </div>
         
         <div class="header-bar">${msg("label.contactinfo")}</div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.email")}:</span>
            <span id="${el}-view-email" class="field-value"></span>
         </div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.telephone")}:</span>
            <span id="${el}-view-telephone" class="field-value"></span>
         </div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.mobile")}:</span>
            <span id="${el}-view-mobile" class="field-value"></span>
         </div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.skype")}:</span>
            <span id="${el}-view-skype" class="field-value"></span>
         </div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.im")}:</span>
            <span id="${el}-view-instantmsg" class="field-value"></span>
         </div>
         
         <div class="header-bar">${msg("label.companyinfo")}</div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.name")}:</span>
            <span id="${el}-view-companyname" class="field-value"></span>
         </div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.address")}:</span>
            <span id="${el}-view-companyaddress" class="field-value"></span>
         </div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.telephone")}:</span>
            <span id="${el}-view-companytelephone" class="field-value"></span>
         </div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.fax")}:</span>
            <span id="${el}-view-companyfax" class="field-value"></span>
         </div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.email")}:</span>
            <span id="${el}-view-companyemail" class="field-value"></span>
         </div>
         
         <div class="header-bar">${msg("label.moreuserinfo")}</div>
         <!-- TODO: Account Status -->
         <div class="field-row">
            <span class="field-label-right">${msg("label.username")}:</span>
            <span id="${el}-view-username" class="field-value"></span>
         </div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.groups")}:</span>
            <span id="${el}-view-groups" class="field-value"></span>
         </div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.accountstatus")}:</span>
            <span id="${el}-view-enabled" class="field-value"></span>
         </div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.quota")}:</span>
            <span id="${el}-view-quota" class="field-value"></span>
         </div>
         <div class="field-row">
            <span class="field-label-right">${msg("label.usage")}:</span>
            <span id="${el}-view-usage" class="field-value"></span>
         </div>
      </div>
   </div>
   
</div>