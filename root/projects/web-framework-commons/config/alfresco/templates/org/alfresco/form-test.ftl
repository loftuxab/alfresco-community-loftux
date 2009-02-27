<#import "import/alfresco-template.ftl" as template />
<@template.header>
</@>

<@template.body>
   <div id="hd">
      <@region id="header" scope="global" protected=true/>
      <@region id="title" scope="template" protected=true />
   </div>
   <div id="bd">
      <div style="padding: 10px; margin-top: 10px; background-color: #eee; border: 1px dotted #c0c0c0;">
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
            <br/>
            <label for="submitMode-multi">Submit Type:</label>
            <input id="submitMode-multi" type="radio" name="submitMode" value="multipart"<#if submitMode == "multipart"> checked</#if>>&nbsp;Multipart&nbsp;
            <input id="submitMode-json" type="radio" name="submitMode" value="json"<#if submitMode == "json"> checked</#if>>&nbsp;JSON&nbsp;
            <input id="submitMode-url" type="radio" name="submitMode" value="urlencoded"<#if submitMode == "urlencoded"> checked</#if>>&nbsp;URL Encoded&nbsp;&nbsp;&nbsp;
            <input id="fr-toggle" name="fr" type="checkbox"<#if url.args.fr?exists> checked</#if>>&nbsp;Test Forms Runtime
            <br/>
            <input type="submit" value="Show Form" style="margin-top: 5px; margin-bottom: 5px;" />
            <input type="button" value="Clear" style="margin-top: 5px; margin-bottom: 5px;"
                   onclick="javascript:document.getElementById('nodeRef').value='';" />
         </form>
      </div>
      
      <div style="margin-left: 1em; margin-top: 0.8em;">
         <@region id="form-ui" scope="template" />
      </div>
      
      <#if url.args.fr?exists>
         <div style="padding: 10px; margin-bottom: 10px; margin-top: 10px; background-color: #eee; border: 1px dotted #c0c0c0;">
            <h2>Forms Runtime Test</h2>
            
            <div id="form-errors" style="margin-top: 5px; color: red;">
            </div>
            
            <div class="form-container">
               <form id="forms-runtime-test" method="get">
                  <input type="hidden" name="fr" value="true" />
                  <label for="username">Username</label>
                  <input id="username" name="username" type="text" size="40" />
                  <label for="pwd">Password</label>
                  <input id="pwd" name="pwd" type="password" size="40" />
                  <label for="email">Email Address</label>
                  <input id="email" name="email" type="text" size="40" />
                  <label for="name">Name</label>
                  <input id="name" name="name" type="text" size="40" />
                  <label for="age">Age</label>
                  <input id="age" type="text" name="age" size="5" />
                  <label for="gender">Gender</label>
                  <input id="gender" name="gender" type="radio" value="male" />&nbsp;Male
                  <input id="gender" name="gender" type="radio" value="female" />&nbsp;Female
                  <label for="country">Country</label>
                  <select id="country" name="country">
                     <option value="">Please select...</option>
                     <option value="france">France</option>
                     <option value="germany">Germany</option>
                     <option value="">Separator...</option>
                     <option value="spain">Spain</option>
                     <option value="uk">United Kingdom</option>
                     <option value="us">United States</option>
                  </select>
                  <label for="interests">Interests</label>
                  <input id="interests" name="interests" type="text" size="40" />
               
                  <hr/>
                  
                  <input id="regsubmit" type="submit" value="Submit" />
                  <input value="Clear" type="reset" />
               </form>
            </div>
         </div>
         
         <script type="text/javascript">
            // setup forms-runtime-test form   
            var frTest = new Alfresco.forms.Form("forms-runtime-test");
            frTest.addValidation("username", Alfresco.forms.validation.length, {min: 3, max: 10}, "blur");
            //frTest.addValidation("age", Alfresco.forms.validation.mandatory);
            //frTest.addValidation("age", Alfresco.forms.validation.numberRange, {min: 18}, "blur");
            //frTest.addValidation("country", Alfresco.forms.validation.mandatory);
            //frTest.addValidation("gender", Alfresco.forms.validation.mandatory);
            //frTest.addValidation("email", Alfresco.forms.validation.email, null, "blur");
            //frTest.setValidateOnSubmit(false);
            //frTest.setErrorContainer("form-errors");
            //frTest.setValidateAllOnSubmit(true);
            frTest.init();
         </script>
         
      </#if>
   </div>
</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>