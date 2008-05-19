<div class="hd">Create site</div>
<div class="bd">

   <form id="${args.htmlid}-createSite-form" action="${url.context}/proxy/alfresco/api/sites" method="POST">
      <input type="hidden" id="${args.htmlid}-isPublic" name="isPublic" value="true"/>

      <div class="yui-t1">

            <div class="yui-g">
               <h2>Info</h2>
               <hr/>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">Name:</div>
               <div class="yui-u"><input id="${args.htmlid}-title" type="text" name="title"/></div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first"><label for="${args.htmlid}-shortName">Short name:</label></div>
               <div class="yui-u"><input id="${args.htmlid}-shortName" type="text" name="shortName"/></div>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">Description:</div>
               <div class="yui-u"><textarea id="${args.htmlid}-description" name="description" rows="3" cols="20"></textarea></div>
            </div>
            <div class="yui-g">
               <h2>Type</h2>
               <hr/>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">Type:</div>
               <div class="yui-u">
                  <select id="${args.htmlid}-sitePreset" name="sitePreset"><option value="extranet">Extranet</option></select>
               </div>
            </div>
            <div class="yui-g">
               <h2>Logo:</h2>
               <hr/>
            </div>
            <div class="yui-g">
               <input type="submit" id="${args.htmlid}-ok-button" value="OK" />
            </div>
         
      </div>

   </form>


</div>
