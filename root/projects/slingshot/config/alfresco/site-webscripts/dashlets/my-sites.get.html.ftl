<script type="text/javascript">//<![CDATA[
   new Alfresco.MySites("${args.htmlid}");
//]]></script>

<div id="mysites_1">

<div class="component">
  <div class="component-title">My Sites</div>
  <div class="component-links">
    <span class="mysites-createSite-button"><a href="#">Create site &gt;</a></span>
  </div>
  <div class="component-list">
<#list sites as site>
      <div>
        <!--<img src="/SOME-IMAGE-PROXY-PATH/${site.icon}">-->
        <a href="${url.context}/page/collaboration/dashboard?doc=${site.name?url}">${site.name}</a>
      </div>
</#list>
  </div>
</div>

<!-- Create new site form -->
<div class="hiddenComponents">
   <div class="mysites-createSite-panel">
     <div class="bd">
        Name: <input type="text" name="name" /><br />
        Type: <select name="type">
                       <option value="1">Collaboration</option>
                   </select>
     </div>
   </div>
</div>

</div>

<div id="${args.htmlid}-createSite"></div>
