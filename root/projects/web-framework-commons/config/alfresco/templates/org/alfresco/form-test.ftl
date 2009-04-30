<#include "include/alfresco-template.ftl" />
<@templateHeader>
</@>

<@templateBody>
   <div id="alf-hd">
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
         <#if url.args.submitType?exists>
            <#assign submitType="${url.args.submitType}">
         <#else>
            <#assign submitType="multipart">
         </#if>
         <form method="get">
            <fieldset style="border: 1px solid #aaa; margin-top: 10px; padding: 8px;">
               <legend style="color: #515d6b;">Item Details</legend>
               <label for="itemKind">Kind:</label>
               <input id="itemKind" type="text" name="itemKind" style="width: 50px; margin: 5px 5px 5px 0px;" 
                      value="<#if url.args.itemKind?exists>${url.args.itemKind}<#else>node</#if>" />
               <label for="itemId">Id:</label>
               <input id="itemId" type="text" name="itemId" style="width: 450px; margin: 5px 5px 5px 0px;" 
                      value="<#if url.args.itemId?exists>${url.args.itemId}</#if>" />
            </fieldset>
            <fieldset style="border: 1px solid #aaa; margin: 10px 0px; padding: 8px;">
               <legend style="color: #515d6b;">Form Details</legend>
               <label for="formId">Id:</label>
               <input id="formId" name="formId" style="width: 200px; margin: 5px 10px 5px 0px;" 
                      value="<#if url.args.formId?exists>${url.args.formId}</#if>" />
               <label style="margin-right: 5px;">Mode:</label>
               <input id="mode-view" type="radio" name="mode" value="view"<#if mode == "view"> checked</#if>>&nbsp;View&nbsp;
               <input id="mode-edit" type="radio" name="mode" value="edit"<#if mode == "edit"> checked</#if>>&nbsp;Edit&nbsp;
               <br/>
               <label style="margin-right: 5px;">Submit Type:</label>
               <input id="submitType-multi" type="radio" name="submitType" value="multipart"<#if submitType == "multipart"> checked</#if>>&nbsp;Multipart&nbsp;
               <input id="submitType-json" type="radio" name="submitType" value="json"<#if submitType == "json"> checked</#if>>&nbsp;JSON&nbsp;
               <input id="submitType-url" type="radio" name="submitType" value="urlencoded"<#if submitType == "urlencoded"> checked</#if>>&nbsp;URL Encoded&nbsp;&nbsp;&nbsp;
               <input id="fr-toggle" name="fr" type="checkbox"<#if url.args.fr?exists> checked</#if>>&nbsp;Test Forms Runtime
            </fieldset>
            <input type="submit" value="Show Form" />
            <input type="button" value="Clear"
                   onclick="javascript:document.getElementById('itemKind').value='';document.getElementById('itemId').value='';document.getElementById('formId').value='';" />
         </form>
      </div>
      
      <div style="margin-left: 1.4em; margin-top: 1.4em;">
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

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>