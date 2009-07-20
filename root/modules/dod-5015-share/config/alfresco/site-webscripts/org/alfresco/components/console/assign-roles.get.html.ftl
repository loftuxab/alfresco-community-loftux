<script type="text/javascript" charset="utf-8">
 new Alfresco.Admin.RM.AssignRoles('assignRoles').setMessages(${messages});
  </script>
  
  <div id="assignRoles">
    <h2>${msg('label.assign-roles-title')}</h2>
    <form id="searchRolesFrm"><input type="text" name="q" value="q" id="searchQuery" /><input type="submit" name="search" value="Search" id="search" /></form>
    <button id="assignRoles-showType" name="assignRoles-showType">${msg('label.show')}</button>
    <div id="assignRoles-showTypeMenu" class="yuimenu">
       <div class="bd">
          <ul>
             <li><a rel="" href="#"><span class="onShowUsers">${msg('label.users')}</span></a></li>
             <li><a rel="delete" href="#"><span class="onShowGroups">${msg('label.groups')}</span></a></li>
             <li><a rel="delete" href="#"><span class="onShowUsersGroups">${msg('label.users-groups')}</span></a></li>
          </ul>
       </div>
    </div>    
    
    <div id="assignRoleDT">
    </div>
        
  </div>