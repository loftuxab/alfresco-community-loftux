<#import "import/alfresco-template.ftl" as template />
<@template.header>
</@>

<@template.body>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id="path" scope="template" protected=true />
      
      <div class="yui-g">
         <div class="yui-g first">
            <@region id="preview" scope="template" protected=true />
            <@region id="comments" scope="template" protected=true />
            <@region id="createcomment" scope="template" protected=true />
         </div>
         <div class="yui-g"> 
            <div class="yui-u first"> 
               <@region id="document-info" scope="template" protected=true />
            </div>
            <div class="yui-u"> 
               <@region id="document-links" scope="template" protected=true />
            </div>
         </div>
      </div>
   </div>
</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>