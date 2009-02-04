<#import "import/alfresco-template.ftl" as template />
<@template.header>
</@>

<@template.body>
   <div id="hd">
      <@region id="header" scope="global" protected=true/>
      <@region id="title" scope="template" protected=true />
   </div>
   <div id="bd">
      <div style="padding: 10px; margin-bottom: 10px; margin-top: 10px; background-color: #eee; border: 1px dotted #c0c0c0;">
         <h2>Form Test Page</h2>
         <#if url.args.mode?exists>
            <#assign mode="${url.args.mode}">
         <#else>
            <#assign mode="edit">
         </#if>
         <#if url.args.submitMode?exists>
            <#assign submitMode="${url.args.submitMode}">
         <#else>
            <#assign submitMode="multipart">
         </#if>
         <form method="get">
            <label for="nodeRef">NodeRef:</label>
            <input id="nodeRef" type="text" name="nodeRef" style="width: 400px; margin-top: 5px; margin-bottom: 5px;" 
                   value="<#if url.args.nodeRef?exists>${url.args.nodeRef}</#if>"/>
            &nbsp;&nbsp;
            <input id="mode-view" type="radio" name="mode" value="view"<#if mode == "view"> checked</#if>>&nbsp;View&nbsp;
            <input id="mode-edit" type="radio" name="mode" value="edit"<#if mode == "edit"> checked</#if>>&nbsp;Edit&nbsp;
            <input id="mode-create" type="radio" name="mode" value="create"<#if mode == "create"> checked</#if>>&nbsp;Create
            <br/>
            <label for="submitMode-multi">Submit Type:</label>
            <input id="submitMode-multi" type="radio" name="submitMode" value="multipart"<#if submitMode == "multipart"> checked</#if>>&nbsp;Multipart&nbsp;
            <input id="submitMode-json" type="radio" name="submitMode" value="json"<#if submitMode == "json"> checked</#if>>&nbsp;JSON&nbsp;
            <input id="submitMode-url" type="radio" name="submitMode" value="urlencoded"<#if submitMode == "urlencoded"> checked</#if>>&nbsp;URL Encoded
            <br/>
            <input type="submit" value="Show Form" style="margin-top: 5px; margin-bottom: 5px;" />
         </form>
      </div>
      
      <@region id="form-ui" scope="template" />
   </div>
</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>