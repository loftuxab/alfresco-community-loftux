<#include "../admin-template.ftl" />

<@page title=msg("directorymanagement.title") controller="/enterprise/admin/admin-directorymanagement">
   
   <div class="column-full">
      <p class="intro">${msg("directorymanagement.help-text")}</p>
      <p>${msg("directorymanagement.help-link")}</p>
      
      <@section label=msg("directorymanagement.auth-chain") />
   </div>
   <div class="column-left">
      <@text name="" id="dm-name" label=msg("directorymanagement.name") placeholder=msg("directorymanagement.example-directory") />
   </div>
   <div class="column-right">
      <@options name="" id="dm-type" label=msg("directorymanagement.type") style="display:inline-block">
         <@option label=msg("directorymanagement.directory.ldap") value="ldap" />
         <@option label=msg("directorymanagement.directory.ldap-ad") value="ldap-ad" />
         <@option label=msg("directorymanagement.directory.passthru") value="passthru" />
         <@option label=msg("directorymanagement.directory.kerberos") value="kerberos" />
         <@option label=msg("directorymanagement.directory.external") value="external" />
      </@options>
      <@button label=msg("directorymanagement.add") onclick="AdminDM.addAuthentication()" style="margin-left:8px" />
   </div>
   <div class="column-full">
      <table id="dm-authtable" class="data">
         <tr>
            <th>${msg("directorymanagement.order")}</th>
            <th>${msg("directorymanagement.name")}</th>
            <th>${msg("directorymanagement.type")}</th>
            <th>${msg("directorymanagement.enabled")}</th>
            <th>${msg("directorymanagement.synchronized")}</th>
            <th>${msg("directorymanagement.actions")}</th>
         </tr>
         <#assign action_edit><a href="#" onclick="AdminDM.editAuthentication(this);return false;">${msg("directorymanagement.action.edit")}</a></#assign>
         <#assign action_test_authenticate><a href="#" onclick="AdminDM.testAuthentication(this);return false;">${msg("directorymanagement.action.test")}</a></#assign>
         <#assign action_reset><a href="#" onclick="AdminDM.resetAuthentication(this);return false;">${msg("directorymanagement.action.reset")}</a></#assign>
         <#assign action_remove><a href="#" onclick="AdminDM.removeAuthentication(this);return false;">${msg("directorymanagement.action.remove")}</a></#assign>
         <#assign action_test_sync><a href="#" onclick="AdminDM.testSyncAuthentication(this);return false;">${msg("directorymanagement.action.synchronize")}</a></#assign>
         <#list auths as a>
            <#if a.cifsSelected><#assign cifsSelected=a.id/></#if>
            <#if a.browserSelected><#assign browserSelected=a.id/></#if>
         <tr>
            <td></td>
            <td>${a.name?html}</td>
            <td>${msg("directorymanagement.directory." + a.type)}</td>
            <td>${msg("admin-console." + a.active?string)}</td>
            <td>${msg("admin-console." + a.synched?string)}</td>
            <td>
               ${action_edit}<#if a.testable> | ${action_test_authenticate}</#if> | ${action_reset} <#if a.removable> | ${action_remove}</#if><#if a.synchable> | ${action_test_sync} </#if>
               <#if a.syncStatus?size != 0>
                  | <a href="#" onclick='AdminDM.syncStatusAuthentication(this,"${a.id?html}");return false;'>${msg("directorymanagement.status")}&nbsp;[+]</a><br>
                  <div class="hidden">
                     <table id="dm-syncstatus-${a.id?html}" class="sync-status"></table>
                  </div>
               </#if>
            </td>
         </tr>
         </#list>
      </table>
      <#if syncStatus!="COMPLETE">
      <p style="padding-left:3em">${msg("directorymanagement.sync-status")}: <b>${syncStatus?html}</b></p>
      </#if>
      <p id="dm-modified" class="hidden">${msg("directorymanagement.modified-chain")}</p>
      <p class="info">${msg("directorymanagement.change-order")}</p>
      <p class="info">${msg("directorymanagement.external-text")}</p>
   </div>
   <div class="column-left">
      <p class="info"><@button label=msg("directorymanagement.sync-settings") onclick="AdminDM.syncSettings()" /></p>
      <p class="info">${msg("directorymanagement.sync-settings-text")}</p>
   </div>
   <div class="column-right">
      <p class="info"><@button label=msg("directorymanagement.run-sync") onclick="AdminDM.runSync()" /></p>
      <p class="info">${msg("directorymanagement.run-sync-text")}</p>
   </div>
   <div class="column-full">
      <@section label=msg("directorymanagement.cifs-auth") />
      <@options name="AdminDM-cifs" id="dm-cifs" value=cifsSelected!"">
         <@option label=msg("admin-console.disabled") value="" />
         <#list auths as a>
            <#if a.cifs><@option label=(msg("directorymanagement.directory." + a.type) + " (" + a.name?html + ")") value=a.id /></#if>
         </#list>
      </@options>
      <p>${msg("directorymanagement.cifs-auth-text")}</p>
      
      <@section label=msg("directorymanagement.browser-login") />
      <@options name="AdminDM-browser" id="dm-browser" value=browserSelected!"">
         <@option label=msg("admin-console.disabled") value="" />
         <#list auths as a>
            <#if a.browser><@option label=(msg("directorymanagement.directory." + a.type) + " (" + a.name?html + ")") value=a.id /></#if>
         </#list>
      </@options>
      <p>${msg("directorymanagement.browser-login-text")}</p>
      
      <@section label=msg("directorymanagement.ftp-auth") />
      <p>${msg("directorymanagement.ftp-auth-text")}</p>
      
      <@attrhidden id="dm-authchain" attribute=attributes["chain"] />
   </div>
   
   <script type="text/javascript">//<![CDATA[

/* Page load handler */
Admin.addEventListener(window, 'load', function() {
   // bind Enter key press to call the Add button event handler
   Admin.addEventListener(el("dm-name"), 'keypress', function(e) {
      if (e.keyCode === 13) AdminDM.addAuthentication();
      return true;
   });
   
   // capture the submit event for the form
   var form = el("${FORM_ID}");
   Admin.addEventListener(form, 'submit', function(e) {
      e.preventDefault ? e.preventDefault() : event.returnValue = false;
      
      // combine authentication chain values
      var chain = "";
      for (var i in AdminDM.authentications)
      {
         chain += (i !== 0 ? "," : "") + AdminDM.authentications[i].id + ":" + AdminDM.authentications[i].type;
      }
      el("dm-authchain").value = chain;
      
      form.submit();
      return false;
   });
   
   // populate the index and actions for the "Order" column
   var table = el("dm-authtable");
   AdminDM.buildOrderColumn(table);
});

/**
 * Admin Directory Management Component
 */
var AdminDM = AdminDM || {};

(function() {
   
   /* map of I18N message IDs to labels */
   AdminDM.msgs = {
      "ldap": "${msg("directorymanagement.directory.ldap")?j_string}",
      "ldap-ad": "${msg("directorymanagement.directory.ldap-ad")?j_string}",
      "passthru": "${msg("directorymanagement.directory.passthru")?j_string}",
      "kerberos": "${msg("directorymanagement.directory.kerberos")?j_string}",
      "external": "${msg("directorymanagement.directory.external")?j_string}",
      "true": "${msg("admin-console.true")?j_string}",
      "false": "${msg("admin-console.false")?j_string}",
      "move-up": "${msg("directorymanagement.move-up")?j_string}",
      "move-down": "${msg("directorymanagement.move-down")?j_string}",
      "starttime": "${msg("directorymanagement.status.starttime")?j_string}",
      "endtime": "${msg("directorymanagement.status.endtime")?j_string}",
      "successful": "${msg("directorymanagement.status.successful")?j_string}",
      "failed": "${msg("directorymanagement.status.failed")?j_string}",
      "percentcomplete": "${msg("directorymanagement.status.percentcomplete")?j_string}",
      "totalresults": "${msg("directorymanagement.status.totalresults")?j_string}"
   };
   
   /* map of action IDs to action link HTML fragments */
   AdminDM.actions = {
      "edit": " ${action_edit?j_string} ",
      "test": " ${action_test_authenticate?j_string} ",
      "reset": " ${action_reset?j_string} ",
      "remove": " ${action_remove?j_string} ",
      "sync": " ${action_test_sync?j_string} "
   };
   
   /* list of authentication object state in the authentication chain */
   AdminDM.authentications = [
      <#list auths as a>
      {
         "id": "${a.id?j_string}",
         "type": "${a.type?j_string}",
         "active": ${a.active?string},
         "synched": ${a.synched?string}
      }<#if a_has_next>,</#if>
      </#list>
   ];
   
   /* Inform the user that the authentication chain will need saving after modifications. */
   AdminDM.chainModified = function chainModified()
   {
      // display important information block
      el("dm-modified").className = "info-important";
   }
   
   /* Add Authentation to chain - button event handler */
   AdminDM.addAuthentication = function addAuthentication()
   {
      var authName = Admin.trim(el("dm-name").value),
          authType = el("dm-type").value;
      
      if (authName.length !== 0)
      {
         // use URL encoding pattern to ensure valid JMX bean names
         var encodedName = encodeURIComponent(authName).replace("*", "%2A");
         
         // validate auth chain
         for (var i in AdminDM.authentications)
         {
            // only allow unique IDs
            if (AdminDM.authentications[i].id === encodedName)
            {
               alert("${msg("directorymanagement.error.uniqueids")}");
               return;
            }
            
            // only allow 'external' auth to appear once
            if (authType === "external" && AdminDM.authentications[i].type === "external")
            {
               alert("${msg("directorymanagement.error.external")}");
               return;
            }
         }
         
         // if we get here, no validation errors occured
         var table = el("dm-authtable");
         
         // special case for "external" auth - always at the front of the auth chain
         var index = authType !== "external" ? -1 : 1;
         // actions available for a newly added item
         var actions = AdminDM.actions["remove"];
         Admin.addTableRow(table, [table.tBodies[0].rows.length, authName, AdminDM.msgs[authType], AdminDM.msgs["false"], AdminDM.msgs["false"], actions], index);
         var auth = {
            "id": encodedName,
            "type": authType,
            "active": false,
            "synched": false
         };
         if (index !== -1)
         {
            // insert at the front of the auth chain structure
            AdminDM.authentications.splice(0, 0, auth);
         }
         else
         {
            // add to the auth chain structure
            AdminDM.authentications.push(auth);
         }
         
         // regenerate "Order" column cell value for each table row
         AdminDM.buildOrderColumn(table);
         
         // reset form element
         el("dm-name").value = "";
         
         AdminDM.chainModified();
      }
   }
   
   /* Remove Authentication from the chain - action link event handler */
   AdminDM.removeAuthentication = function removeAuthentication(actionEl)
   {
      var table = el("dm-authtable"),
          rowIndex = actionEl.parentElement.parentElement.rowIndex;
      
      // remove the row that contains this action element
      table.deleteRow(rowIndex);
      
      // remove from authentication chain structure
      AdminDM.authentications.splice(rowIndex - 1, 1);
      
      // regenerate "Order" column cell value for each table row
      AdminDM.buildOrderColumn(table);
      
      AdminDM.chainModified();
   }
   
   /* Test authentication connection - action link event handler */
   AdminDM.testAuthentication = function testAuthentication(actionEl)
   {
      var table = el("dm-authtable"),
          rowIndex = actionEl.parentElement.parentElement.rowIndex,
          authId = AdminDM.authentications[rowIndex - 1].id;
      
      var url = "${url.serviceContext}/enterprise/admin/admin-authentication-test?authenticatorName=" + encodeURIComponent(authId);
      
      Admin.showDialog(url);
   }
   
   /* Run Sync - action button event handler */
   AdminDM.runSync = function runSync(actionEl)
   {
      var url = "${url.serviceContext}/enterprise/admin/admin-sync";
      
      Admin.showDialog(url);
   }
   
   /* Test sync connection - action link event handler */
   AdminDM.testSyncAuthentication = function testSyncAuthentication(actionEl)
   {
      var table = el("dm-authtable"),
          rowIndex = actionEl.parentElement.parentElement.rowIndex,
          authId = AdminDM.authentications[rowIndex - 1].id;
      
      var url = "${url.serviceContext}/enterprise/admin/admin-user-sync-test?authenticatorName=" + encodeURIComponent(authId);
      
      Admin.showDialog(url);
   }
   
   /* Sync status panel toggle - action link event handler */
   AdminDM.syncStatusAuthentication = function syncStatusAuthentication(actionEl, authId)
   {
      // TODO: need to retrieve this panel info as JSON and update via Ajax when open
      var div = actionEl.parentElement.getElementsByTagName("div")[0];
      if (Admin.toggleHiddenElement(div))
      {
         // opened panel - update immediately and set function to refresh on a timer - 5 seconds
         AdminDM.updateSyncStatus(authId);
         AdminDM.updateSyncStatusTimers[authId] = window.setInterval(
            function() {
               AdminDM.updateSyncStatus(authId);
            },
            5000
         );
      }
      else
      {
         // closed panel - stop any active timers
         if (AdminDM.updateSyncStatusTimers[authId])
         {
            window.clearInterval(AdminDM.updateSyncStatusTimers[authId]);
            delete AdminDM.updateSyncStatusTimers[authId];
         }
      }
   }
   
   /* Sync status update function - Ajax update of the sync status until sync completed */
   AdminDM.updateSyncStatusTimers = {};
   AdminDM.updateSyncStatus = function updateSyncStatus(authId)
   {
      Admin.request({
         url: "${url.service}",
         fnSuccess: function(res)
         {
            if (res.responseJSON && res.responseJSON.data)
            {
               // find the auth details we are interested in
               for (var i=0; i<res.responseJSON.data.length; i++)
               {
                  if (res.responseJSON.data[i].id === authId)
                  {
                     // found the auth item to display sync info for
                     var table = el("dm-syncstatus-" + authId),
                         syncStatus = res.responseJSON.data[i].syncStatus;
                     
                     // remove existing data - replace with rows from json response
                     for (var i=table.rows.length-1; i>=0; i--)
                     {
                        table.deleteRow(i);
                     }
                     for (var i=0; i<syncStatus.length; i++)
                     {
                        Admin.addTableRow(table, [
                           Admin.html(syncStatus[i].id),
                           AdminDM.msgs["starttime"] + ": " + syncStatus[i].startTime + "<br>" +
                           AdminDM.msgs["endtime"] + ": " + syncStatus[i].endTime,
                           AdminDM.msgs["successful"] + ":&nbsp;" + syncStatus[i].successfullyProcessedEntries + "<br>" +
                           AdminDM.msgs["failed"] + ":&nbsp;" + syncStatus[i].totalErrors,
                           AdminDM.msgs["percentcomplete"] + ":&nbsp;" + syncStatus[i].percentComplete + "<br>" +
                           AdminDM.msgs["totalresults"] + ":&nbsp;" + syncStatus[i].totalResults
                        ]);
                     }
                     break;
                  }
               }
               
               // if the sync is complete then disable any further panel update timers
               if (res.responseJSON.syncStatus === "COMPLETE")
               {
                  if (AdminDM.updateSyncStatusTimers[authId])
                  {
                     window.clearInterval(AdminDM.updateSyncStatusTimers[authId]);
                     delete AdminDM.updateSyncStatusTimers[authId];
                  }
               }
            }
         }
      });
   }
   
   /* Edit authentication connection - action link event handler */
   AdminDM.editRowIndex = null;
   AdminDM.editAuthentication = function editAuthentication(actionEl)
   {
      var table = el("dm-authtable"),
          rowIndex = actionEl.parentElement.parentElement.rowIndex,
          auth = AdminDM.authentications[rowIndex - 1];
      
      var url = null;
      switch (auth.type)
      {
         case "ldap":
            url = "${url.serviceContext}/enterprise/admin/admin-authentication-ldap?id=" + encodeURIComponent(auth.id);
            break;
         case "ldap-ad":
            url = "${url.serviceContext}/enterprise/admin/admin-authentication-ldapad?id=" + encodeURIComponent(auth.id);
            break;
         case "alfrescoNtlm":
            url = "${url.serviceContext}/enterprise/admin/admin-authentication-internal?id=" + encodeURIComponent(auth.id);
            break;
         case "external":
            url = "${url.serviceContext}/enterprise/admin/admin-authentication-external?id=" + encodeURIComponent(auth.id);
            break;
         case "kerberos":
            url = "${url.serviceContext}/enterprise/admin/admin-authentication-kerberos?id=" + encodeURIComponent(auth.id);
            break;
         case "passthru":
            url = "${url.serviceContext}/enterprise/admin/admin-authentication-passthru?id=" + encodeURIComponent(auth.id);
            break;
      }
      
      if (url)
      {
         AdminDM.editRowIndex = rowIndex;
         Admin.showDialog(url);
      }
   }
   
   /* Reset authentication connection - action link event handler */
   AdminDM.resetAuthentication = function resetAuthentication(actionEl)
   {
      var rowIndex = actionEl.parentElement.parentElement.rowIndex,
          auth = AdminDM.authentications[rowIndex - 1];
      
      AdminDM.editRowIndex = rowIndex;
      
      var url = "${url.serviceContext}/enterprise/admin/admin-authentication-reset?authenticatorName=" + encodeURIComponent(auth.id);
      Admin.showDialog(url, true);
   }
   
   /* Dialog Finish event handler */
   Admin.ondialogFinished = function ondialogFinished(args)
   {
      // make a JSON request to retrieve the updated data for the directories
      Admin.request({
         url: "${url.service}",
         fnSuccess: function(res)
         {
            if (res.responseJSON && res.responseJSON.data)
            {
               // update stale local auth data and update displayed table row
               if (AdminDM.editRowIndex !== null)
               {
                  var auth = AdminDM.authentications[AdminDM.editRowIndex - 1];
                  for (var i=0; i<res.responseJSON.data.length; i++)
                  {
                     if (res.responseJSON.data[i].id === auth.id)
                     {
                        // found the stale auth item to update
                        var table = el("dm-authtable"),
                            updatedAuth = res.responseJSON.data[i];
                        
                        // update with fresh data
                        // NOTE: Enabled and Synchronized are the only fields that can be modified in the Edit dialogs
                        auth.active = updatedAuth.active;
                        auth.synched = updatedAuth.synched;
                        
                        var cells = table.tBodies[0].rows[AdminDM.editRowIndex].cells;
                        cells[3].innerHTML = AdminDM.msgs[auth.active ? "true" : "false"];
                        cells[4].innerHTML = AdminDM.msgs[auth.synched ? "true" : "false"];
                        break;
                     }
                  }
               }
            }
         }
      });
   }
   
   /* Show the common Sync Settings dialog */
   AdminDM.syncSettings = function syncSettings()
   {
      var url = "${url.serviceContext}/enterprise/admin/admin-authentication-syncsettings";
      Admin.showDialog(url);
   }
   
   /* Reset the "Order" column with correct indexes */
   AdminDM.buildOrderColumn = function buildOrderColumn(table)
   {
      // reset "Order" column cell value for each table row
      var hasExternalAuth = (AdminDM.authentications[0].type === "external");
      var rows = table.tBodies[0].rows;
      for (var i=1; i<rows.length; i++)
      {
         // generate the move up/down action icons
         var html = "<span style='width:16px;display:inline-block'>";
         if (i !== 1 && !(i === 2 && hasExternalAuth))
         {
            html += "<a href='#' class='action' title='" + AdminDM.msgs["move-up"] + "' onclick='AdminDM.reorderAuthChain(" + i + ",0);return false;'>\u25B3</a>"; // unicode up arrow
         }
         html += "</span><span style='width:16px;display:inline-block'>";
         if (i < rows.length-1 && !(i === 1 && hasExternalAuth))
         {
            html += "<a href='#' class='action' title='" + AdminDM.msgs["move-down"] + "' onclick='AdminDM.reorderAuthChain(" + i + ",1);return false;'>\u25BD</a>"; // unicode down arrow
         }
         html += "</span>";
         html += i;           // row index counter
         
         // ensure controls don't wrap around
         rows[i].cells[0].style.whiteSpace = "nowrap";
         
         rows[i].cells[0].innerHTML = html;
      }
   }
   
   /**
    * Reorder the auth chain table
    * 
    * @param index   {Integer}   Index of the row to operate on
    * @param dir     {Integer}   Direction; 0=up, 1=down
    */
   AdminDM.reorderAuthChain = function reorderAuthChain(index, dir)
   {
      var table = el("dm-authtable");
      
      var i0, i1;
      if (dir) // down
      {
         i0 = index + 1;
         i1 = index;
      }
      else     // up
      {
         i0 = index;
         i1 = index - 1;
      }
      var row0 = table.rows[i0],
          row1 = table.rows[i1];
      row1.parentElement.insertBefore(row0, row1);
      
      // swap items in the authentications structure
      AdminDM.authentications[i0-1] = AdminDM.authentications.splice(i1-1, 1, AdminDM.authentications[i0-1])[0];
      
      // regenerate the "Order" column now rows are reordered
      AdminDM.buildOrderColumn(table);
   }
   
})();
//]]></script>
</@page>