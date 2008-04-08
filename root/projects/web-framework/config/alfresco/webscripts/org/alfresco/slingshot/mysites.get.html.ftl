<!-- Main gui -->

<div class="component">
  <div class="titlebar">My Sites</div>
  <div class="linkbar">
    <a href="javascript:showCreateSiteDialog();">Create site</a>
  </div>
  <div class="list">
    <table>
<#list sites as site>
      <tr>
        <td><img src="/SOME-IMAGE-PROXY-PATH/${site.icon}"></td>
        <td>${site.name}</td>
      </tr>
</#list>
    </table>
  </div>
</div>
<!-- Create new site form -->
<div id="createSiteDialog" style="visibility: hidden; position:absolute;">
  <div class="hd">Create</div>
  <div class="bd"></div>
  <div class="ft"></div>
</div>
