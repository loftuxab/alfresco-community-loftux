<div id="mysites_1">

<div class="component">
  <div class="component-title">My Sites</div>
  <div class="component-links">
    <a href="javascript:Alfresco.MySites.showCreateDialog('createSiteDialog');">Create site</a>
  </div>
  <div class="component-list">
<#list sites as site>
      <div>
        <!--<img src="/SOME-IMAGE-PROXY-PATH/${site.icon}">-->
        <a href="${url.context}/page/collaboration/dashboard?doc=${site.name}">${site.name}</a>
      </div>
</#list>
  </div>
</div>

<!-- Create new site form -->
<div class="mysites-createdialog-panel">
  <div class="bd">
      <form>
          Name: <input type="text" name="name"><br>
          Type: <select name="type">
                    <option value="1">Collaboration
                </select>
      </form>
  </div>
</div>

</div>