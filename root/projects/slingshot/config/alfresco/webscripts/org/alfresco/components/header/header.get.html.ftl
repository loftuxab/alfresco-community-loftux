<div class="logo">
   <img src="${url.context}${args.logo}">
</div>
<div class="personal-menu">
   <span class="menu-item"><a href="${url.context}/page/user-dashboard">My Dashboard</a></span>
   <span class="menu-item"><a href="${url.context}/page/user-profile">My Profile</a></span>
</div>
<div class="util-menu">
   <span class="menu-item"><a href="">Trash</a></span>
   <span class="menu-item"><a href="">Help</a></span>
   <span class="menu-item"><a href="">Search</a></span>
   <span class="menu-item"><input type="text" name="search" value="All Sites"></span>
   <span class="menu-item"><a href="">Logout</a></span>
</div>
<div class="collaboration-menu">
   <input type="submit" class="header-sitesSelect-button" value="Sites">
   <select class="header-sitesSelect-menu">
   <#list sites as site>
      <option value="${site.nodeRef}">${site.name}</option>
   </#list>
   </select>

   <input type="submit" class="header-colleaguesSelect-button" value="Colleagues">
   <select class="header-colleaguesSelect-menu">
   <#list persons as person>
      <option value="${person.nodeRef}">${person.name}</option>
   </#list>
   </select>

   <span class="menu-item"><a href=""></a></span>
</div>
<div class="clear"></div>
