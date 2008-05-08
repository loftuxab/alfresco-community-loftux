<script type="text/javascript">//<![CDATA[
new Alfresco.CreateSite("${args.htmlid}");
//]]></script>

<div id="${args.htmlid}-createSite-panel">
   <!-- var uriDoclist = Alfresco.constants.PROXY_URI + "http://localhost:8080/alfresco/service/slingshot/doclib/doclist&alf_ticket=" + Alfresco.constants.TICKET + "&";-->
   <form id="${args.htmlid}-createSite-form" action="/alfresco/service/api/sites" method="POST">
      <input type="hidden" name="shortName" value="spk8"/>
      <input type="hidden" name="isPublic" value="true"/>
      <div id="doc" class="yui-t7">
         <div id="hd"><h1>Create site</h1></div>
         <div id="bd">
            <div class="yui-g">
               <h2>Info</h2>
               <hr/>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">
                  Name:
               </div>
               <div class="yui-u">
                  <input type="text" name="title"/>
               </div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">
                  Description
               </div>
               <div class="yui-u">
                  <textarea name="description" rows="3" cols="20"></textarea>
               </div>
            </div>
            <div class="yui-g">
               <h2>Type</h2>
               <hr/>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">
                  Type:
               </div>
               <div class="yui-u">
                  <select name="sitePreset">
                     <option value="extranet">Extranet</option>
                  </select>

               </div>
            </div>
            <div class="yui-g">
               <h2>Logo:</h2>
               <hr/>
            </div>
            <div class="yui-g">
               ...
            </div>
            <div class="yui-g">
               <input type="submit" id="${args.htmlid}-ok-button" value="OK" />
            </div>
         </div>
      </div>
   </form>
</div>
