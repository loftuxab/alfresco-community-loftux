<script type="text/javascript">//<![CDATA[
   new Alfresco.FormUI("${args.htmlid}");
//]]></script>

<div class="form">
   <i>The following markup has been written in order to test the behaviour of the forms code. It is
   not intended to be attractive or even usable.</i>
   <hr/>
   
  <b>Constraints</b>
  <table border="1">
    <tr>
      <th>Type</th>
      <th>Validation Handler</th>
      <th>Message</th>
      <th>MessageID</th>
    </tr>
     <#list constraints['types'] as constType>
       <tr>
         <td>${constType}</td>
         <td>${constraints['handlers']['${constType}']}</td>
         <td>${constraints['messages']['${constType}']}</td>
         <td>${constraints['messageIDs']['${constType}']}</td>
       </tr>
     </#list>
  </table>
  <br/>
  
  <b>Default Controls</b>
  <table border="1">
    <tr>
      <th>Name</th>
      <th>Template</th>
      <th>Params</th>
    </tr>
     <#list defaultcontrols['names'] as dcName>
       <tr>
         <td>${dcName}</td>
         <td>${defaultcontrols['templates']['${dcName}']}</td>
         <td>
           <#list defaultcontrols['control-params']['${dcName}']['names'] as paramName>
             ${paramName} = ${defaultcontrols['control-params']['${dcName}']['values'][paramName]}
           <#if paramName_has_next>, </#if>
           </#list>
         </td>
       </tr>
     </#list>
  </table>
  
  <hr/>
  <b>Forms</b>
  <br/>
  Fields visible in VIEW:
  <#list form.viewFieldNames as fieldName>
    <I>${fieldName}</I>:    
    <#list form.viewFields['${fieldName}'].attributeNames as attrName>
      ${attrName} = ${form.viewFields['${fieldName}'].attributes['${attrName}']}
      <#if attrName_has_next>, </#if>
    </#list>
    <#if fieldName_has_next>; </#if>
  </#list>
  <br/>
   
  submissionURL = ${form['submissionURL']}

  <br/>
  <b>TBC</b>
  
  <hr/>
   
   faked JSON: ${rubbish}
   
   <hr/>
   
   <form>
      <label for="${args.htmlid}-name">Name</label>
      <input type="text" name="name" id="${args.htmlid}-name" />

      <#list fields as f>
         <label for="${f}-id">${f} : </label>
         <input type="text" name="${f}" id="${f}-id" value="${f}"/>
      </#list>

      <input type="submit" value="${msg('submit.button.label')}" />

      <#-- args.htmlid = ${args.htmlid} -->
      currentUser = ${user.id}
   </form>
   
</div>
