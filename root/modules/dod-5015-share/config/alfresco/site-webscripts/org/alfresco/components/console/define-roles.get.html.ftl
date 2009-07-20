<#if (action=='new' || action='edit')>
   <#include "./define-roles-new-edit.ftl">
<#else>
   <script type="text/javascript" charset="utf-8">
    new Alfresco.Admin.RM.ViewRoles('manageRoles').setMessages(${messages});
  </script>
   <div id="manageRoles">
       <h2>${msg('label.roles')}</h2>
       <button id="newRole" value="newRole" class="action">New Role</button>   
       <div id="defaultRole" class="yui-gf">
       	<div id="roleSelection" class="yui-u first">
       		<h3 class="title">${msg('label.roles')}</h3>
       		<div id="roles">
       		  <ul>
       		      <li><a href="#User" class="role" >${msg('label.user')}</a></li>
       		      <li><a href="#PowerUser" class="role">${msg('label.power-user')}</a></li>
       		      <li><a href="#RecordsManager" class="role">${msg('label.records-manager')}</a></li>
       		      <li><a href="#Administrator" class="role">${msg('label.administrator')}</a></li>
       		      <li><a href="#SecurityOfficer" class="role">${msg('label.security-officer')}</a></li>
       		  </ul>
       		</div>
       	</div>
    	
       	<div id="roleContent" class="yui-u">
       		<h3 class="title">${msg('label.capabilities-for-user')}</h3>
   		    <button id="editRole" value="roleid" class="action">${msg('label.edit-role')}</button>   
   		    <button id="deleteRole" value="roleid" class="action">${msg('label.delete-role')}</button>
   		    <div class="roleCapabilities active">
		    
       		    <h4>${msg('label.basic')}</h4>
       		    <ul class="capabilities">
                       <li>Full list to be confirmed</li>
       		    </ul>
       	        <h4>Power</h4>
       		    <ul class="capabilities">
                       <li>Full list to be confirmed</li>
       		    </ul>
       	        <h4>Etc</h4>
       		    <ul class="capabilities">
                       <li>Full list to be confirmed</li>
       		    </ul>
       	        <h4>Etc</h4>
       		    <ul class="capabilities">
                       <li>Full list to be confirmed</li>
       		    </ul>
   		    </div>
       	</div>
       </div>
   </div>
</#if>
