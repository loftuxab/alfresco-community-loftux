<script type="text/javascript" charset="utf-8">
    new Alfresco.Admin.RM.Roles('manageRoles').setMessages( ${messages});
  </script>
<div id="manageRoles">
    <#if (action='new')>
    <h2>${msg('label.new-role')}</h2>
    <#else>
    <h2>${msg('label.edit-role')}</h2>    
    </#if>

    <form id="newRoleForm"  method="get">
        <label for="roleName">${msg('label.name')}:</label>
        <input type="text" name="roleName" value="" id="roleName" />
        <h3>${msg('label.capabilities')}</h3>

        <button id="basicSelectAll" value="basicSelectAll" class="selectAll action">${msg('label.select-all')}</button>    
        <fieldset>
        <legend>${msg('label.basic')}</legend>
        <ul id="basicCapabilities" class="capabilities">
            <li><input name="declareRecords" type="checkbox" id="declareRecords" /><label for="declareRecords">To be confirmed</label></li>
            <li><input name="viewRecords" type="checkbox" id="viewRecords" /><label for="viewRecords">To be confirmed</label></li>        
        </ul>
        </fieldset>
           
        <button id="powerSelectAll" value="powerSelectAll" class="selectAll action">${msg('label.select-all')}</button>    
        <fieldset> 
        <legend>${msg('label.power')}</legend>
        <ul id="powerCapabilities" class="capabilities">
            <li><input name="to-be-confirmed" type="checkbox" id="to-be-confirmed" /><label for="to-be-confirmed">To be confirmed</label></li>
        </ul>
        </fieldset>    
        
        <button id="managerSelectAll" value="managerSelectAll" class="selectAll action">${msg('label.select-all')}</button>    
        <fieldset>
        <legend>${msg('label.manager')}</legend>
        <ul id="managerCapabilities" class="capabilities">
            <li><input name="to-be-confirmed" type="checkbox" id="to-be-confirmed" /><label for="to-be-confirmed">To be confirmed</label></li>
        </ul>
        </fieldset>
        
        <button id="adminSelectAll" value="adminSelectAll" class="selectAll action">${msg('label.select-all')}</button>    
        <fieldset>
        <legend>${msg('label.admin')}</legend>
        <ul id="adminCapabilities" class="capabilities">
            <li><input name="to-be-confirmed" type="checkbox" id="to-be-confirmed" /><label for="to-be-confirmed">To be confirmed</label></li>
        </ul>
        </fieldset>   

        <button id="securitySelectAll" value="securitySelectAll" class="selectAll action">${msg('label.select-all')}</button>    
        <fieldset>
        <legend>${msg('label.security')}</legend>
        <ul id="securityCapabilities" class="capabilities">
            <li><input name="to-be-confirmed" type="checkbox" id="to-be-confirmed" /><label for="to-be-confirmed">To be confirmed</label></li>
        </ul>
        </fieldset>         

        <#if (action=='new')>
        <input name="submitType" value="create" id="submitType" type="hidden" />
        <input type="submit" name="submitCreate" value="${msg('label.create')}" id="submitCreate" />
        <input type="submit" name="submitCreateAnother" value="${msg('label.create-another')}" id="submitCreateAnother" /> 
        <#else>
        <input type="submit" name="submitSave" id="submitSave" value="${msg('label.save')}"/>
        </#if>
        
        <button name="submitCancel" value="Cancel" id="submitCancel" class="cancel">${msg('label.cancel')}</button>    
    </form>
        
  </div>